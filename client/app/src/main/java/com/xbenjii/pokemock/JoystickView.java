package com.xbenjii.pokemock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    // =========================================
    // Private Members
    // =========================================

    private final String TAG = "JoystickView";
    private Paint circlePaint;
    private Paint handlePaint;
    private double touchX, touchY;
    private int innerPadding;
    private int handleRadius;
    private int joystickRadius;
    private int handleInnerBoundaries;
    private JoystickMovedListener listener;
    private float sensitivity;
    private double centerX;
    private double centerY;

    // =========================================
    // Constructors
    // =========================================

    public JoystickView(Context context) {
        super(context);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
        initJoystickView();
    }

    // =========================================
    // Initialization
    // =========================================

    private void initJoystickView() {
        setFocusable(true);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(0x55101010);
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.DKGRAY);
        handlePaint.setStrokeWidth(1);
        handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerPadding = 10;
        sensitivity = 0.00000250f;
    }

    // =========================================
    // Public Methods
    // =========================================

    public void setOnJostickMovedListener(JoystickMovedListener listener) {
        this.listener = listener;
    }

    // =========================================
    // Drawing Functionality
    // =========================================


    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        touchX = (int) getWidth() / 2;
        touchY = (int) getWidth() / 2;
        int d = Math.min(xNew, yNew);
        handleRadius = (int) (d / 2 * 0.25);
        joystickRadius = (int) (d / 2 * 0.75);
        handleInnerBoundaries = handleRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Here we make sure that we have a perfect circle
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;
        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int px = getWidth() / 2;
        int py = getHeight() / 2;

        centerX = px;
        centerY = py;

        // Draw the background
        canvas.drawCircle(px, py, joystickRadius, circlePaint);

        // Draw the handle
        canvas.drawCircle((float) touchX, (float) touchY,
                handleRadius, handlePaint);

        canvas.save();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionType = event.getAction();
        touchX = event.getX();
        touchY = event.getY();

        double abs = Math.sqrt((touchX - centerX) * (touchX - centerX)
                + (touchY - centerY) * (touchY - centerY));
        if (abs > joystickRadius) {
            touchX = ((touchX - centerX) * joystickRadius / abs + centerX);
            touchY = ((touchY - centerY) * joystickRadius / abs + centerY);
        }

        if (actionType == MotionEvent.ACTION_MOVE) {

            // Coordinates
            Log.d(TAG, "X:" + touchX + "|Y:" + touchY);

            float diffX = (float) (touchX - centerX);
            diffX = (float) (diffX / centerX * sensitivity);

            float diffY = (float) (touchY - centerY);
            diffY = (float) (diffY / centerY * sensitivity);

            // Pressure
            if (listener != null) {
                listener.OnMoved(diffX,
                        -diffY);
            }

            invalidate();
        } else if (actionType == MotionEvent.ACTION_UP) {
            returnHandleToCenter();
            Log.d(TAG, "X:" + touchX + "|Y:" + touchY);
        }
        return true;
    }

    private void returnHandleToCenter() {

        Handler handler = new Handler();
        int numberOfFrames = 5;
        final double intervalsX = (centerX - touchX) / numberOfFrames;
        final double intervalsY = (centerY - touchY) / numberOfFrames;

        for (int i = 0; i < numberOfFrames; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    touchX += intervalsX;
                    touchY += intervalsY;
                    invalidate();
                }
            }, i * 40);
        }

        if (listener != null) {
            listener.OnReleased();
        }
    }
}