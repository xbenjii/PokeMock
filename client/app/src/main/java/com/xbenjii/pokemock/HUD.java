package com.xbenjii.pokemock;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class HUD extends Service {

    private static final String TAG = "HUD";

    private String mockLocationProvider = LocationManager.GPS_PROVIDER;
    private LocationManager mLocationManager;
    private Location currentLocation = new Location(mockLocationProvider);

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
        currentLocation.setLatitude(53.7282337);
        currentLocation.setLongitude(-1.8642777);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View myView = inflater.inflate(R.layout.hud, null);

        Button up = (Button) myView.findViewById(R.id.up);
        Button down = (Button) myView.findViewById(R.id.down);
        Button left = (Button) myView.findViewById(R.id.left);
        Button right = (Button) myView.findViewById(R.id.right);
        Button exit = (Button) myView.findViewById(R.id.exit);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Up clicked");
                currentLocation.setLatitude(currentLocation.getLatitude() + 0.0000100);
                currentLocation.setTime(System.currentTimeMillis());
                currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                mLocationManager.setTestProviderLocation(mockLocationProvider, currentLocation);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Down clicked");
                currentLocation.setLatitude(currentLocation.getLatitude() - 0.0000100);
                currentLocation.setTime(System.currentTimeMillis());
                currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                mLocationManager.setTestProviderLocation(mockLocationProvider, currentLocation);
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Left clicked");
                currentLocation.setLongitude(currentLocation.getLongitude() - 0.0000100);
                currentLocation.setTime(System.currentTimeMillis());
                currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                mLocationManager.setTestProviderLocation(mockLocationProvider, currentLocation);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Right clicked");
                currentLocation.setLongitude(currentLocation.getLongitude() + 0.0000100);
                currentLocation.setTime(System.currentTimeMillis());
                currentLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                mLocationManager.setTestProviderLocation(mockLocationProvider, currentLocation);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
            }
        });

        wm.addView(myView, params);
    }
}