package com.xbenjii.pokemock;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class HUD extends Service implements FloatingViewListener {

    private static final String TAG = "HUD";

    private String mockLocationProvider = LocationManager.GPS_PROVIDER;
    private LocationManager mLocationManager;
    private Location currentLocation = new Location(mockLocationProvider);

    private FloatingViewManager mFloatingViewManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

        int density = getResources().getDisplayMetrics().densityDpi;

        int defaultSize = (int) (150 * (density / DisplayMetrics.DENSITY_DEFAULT));
        RelativeLayout.LayoutParams lpPin = new RelativeLayout.LayoutParams(defaultSize, defaultSize);

        lpPin.addRule(RelativeLayout.ALIGN_PARENT_END);
        lpPin.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        JoystickView joystickView = new JoystickView(this);
        joystickView.setLayoutParams(lpPin);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            mLocationManager.addTestProvider(mockLocationProvider, true, true, true, false, true,
                    true, true, 0, 5);
            mLocationManager.setTestProviderEnabled(mockLocationProvider, true);
            mLocationManager.setTestProviderStatus(mockLocationProvider, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        } catch (Exception e) {
            Toast.makeText(HUD.this, "You haven't set PokeMock as the active mock location app.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(mockLocationProvider, 5, 15, locationListener);
        }

        currentLocation.setTime(System.currentTimeMillis());
        currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        currentLocation.setAccuracy(50);
        currentLocation.setAltitude(5);
        currentLocation.setLatitude(53.7282337);
        currentLocation.setLongitude(-1.8642777);

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
                currentLocation.setTime(System.currentTimeMillis());
                currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                mLocationManager.setTestProviderLocation(mockLocationProvider, currentLocation);
            }

            @Override
            public void OnReleased() {

            }
        });
    }

    @Override
    public void onFinishFloatingView() {

    }
}