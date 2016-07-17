package com.xbenjii.pokemock;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class HUD extends Service implements FloatingViewListener {

    private static final String TAG = "HUD";

    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_SAVE_DATA = 2;
    static final int MSG_LOAD_DATA = 3;

    private String mockLocationProvider = LocationManager.GPS_PROVIDER;
    private LocationManager mLocationManager;
    private Location currentLocation = new Location(mockLocationProvider);

    private FloatingViewManager mFloatingViewManager;

    private SharedPreferencesStorage storage;

    private LatLng lastCoordinates;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "Location updated: " + location.toString());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    };

    @Override
    public void onCreate() {
        super.onCreate();

        this.storage = new SharedPreferencesStorage(this);
        lastCoordinates = storage.getLastCoordinates();

        int density = getResources().getDisplayMetrics().densityDpi;

        int defaultSize = (int) (150 * (density / DisplayMetrics.DENSITY_DEFAULT));
        RelativeLayout.LayoutParams lpPin = new RelativeLayout.LayoutParams(defaultSize, defaultSize);

        lpPin.addRule(RelativeLayout.ALIGN_PARENT_END);
        lpPin.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        JoystickView joystickView = new JoystickView(this);
        joystickView.setLayoutParams(lpPin);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationManager.addTestProvider(mockLocationProvider, true, true, true, false, true,
                true, true, 0, 5);
        mLocationManager.setTestProviderEnabled(mockLocationProvider, true);
        mLocationManager.setTestProviderStatus(mockLocationProvider, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(mockLocationProvider, 5, 15, locationListener);
        }

        currentLocation.setTime(System.currentTimeMillis());
        currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        currentLocation.setAccuracy(50);
        currentLocation.setAltitude(5);
        currentLocation.setLatitude(lastCoordinates.latitude);
        currentLocation.setLongitude(lastCoordinates.longitude);

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setTrashViewEnabled(false);
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.shape = FloatingViewManager.SHAPE_CIRCLE;
        options.overMargin = (int) (16 * metrics.density);

        mFloatingViewManager.addViewToWindow(joystickView, options);

        joystickView.setOnJostickMovedListener(new JoystickMovedListener() {
            @Override
            public void OnMoved(double diffLongitude, double diffLatitude) {
                currentLocation.setLongitude(currentLocation.getLongitude() + diffLongitude);
                currentLocation.setLatitude(currentLocation.getLatitude() + diffLatitude);
            }

            @Override
            public void OnReleased() {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    currentLocation.setTime(System.currentTimeMillis());
                    currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                    mLocationManager.setTestProviderLocation(mockLocationProvider, currentLocation);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onFinishFloatingView() {

    }

    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    //currentLocation
                    break;
                case MSG_SAVE_DATA:
                    storage.saveCoordinates(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                    break;
                case MSG_LOAD_DATA:
                    lastCoordinates = storage.getLastCoordinates();
                    currentLocation.setLongitude(lastCoordinates.longitude);
                    currentLocation.setLatitude(lastCoordinates.latitude);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}