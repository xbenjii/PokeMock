package com.xbenjii.pokemock;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Ben on 13/07/2016.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    private String mockLocationProvider;
    private LocationManager mLocationManager;
    private OnMessageReceivedListener listener = null;

    private static final String TAG = "MyBroadcastReceiver";

    public void onReceive(final Context context, final Intent intent) {

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        mockLocationProvider = LocationManager.GPS_PROVIDER;

        mLocationManager.addTestProvider(mockLocationProvider, true, true, true, false, true,
                true, true, 0, 5);
        mLocationManager.setTestProviderEnabled(mockLocationProvider, true);
        mLocationManager.setTestProviderStatus(mockLocationProvider, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        String action = intent.getAction();
        if(action.equals("address")) {
            final String address = intent.getExtras().getString("address");
            try {
                new WebSocketFactory()
                    .setConnectionTimeout(5000)
                    .createSocket(address)
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String message) {
                            Log.v(TAG, "Message received:" + message);
                            onMessageReceived("Message received: " + message);
                            String[] parts = message.split(":");
                            final String latitude = parts[0];
                            final String longitude = parts[1];
                            final String altitude = parts[2];
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Location mockLocation = new Location(mockLocationProvider);
                                    mockLocation.setLatitude(Double.parseDouble(latitude));
                                    mockLocation.setLongitude(Double.parseDouble(longitude));
                                    mockLocation.setAltitude(Double.parseDouble(altitude));
                                    mockLocation.setTime(System.currentTimeMillis());
                                    mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                                    mockLocation.setAccuracy(50);
                                    mLocationManager.setTestProviderLocation(mockLocationProvider, mockLocation);
                                }
                            });
                            t.start();
                        }
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                            Log.v(TAG, "Connected to server: " + address);
                            onMessageReceived("Connected to server: " + address);
                        }
                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connect();
            } catch(IOException ex) {
                Log.e(TAG, "IO Exception " + ex.getMessage());
                onMessageReceived(ex.getMessage());
            } catch(WebSocketException ex) {
                Log.e(TAG, "WS Exception " + ex.getMessage());
                onMessageReceived(ex.getMessage());
            }
        }
    }

    private void onMessageReceived(String message) {
        if(listener != null) {
            listener.onMessageReceived(message);
        }
    }

    public void setOnMessageReceivedListener(Context context) {
        this.listener = (OnMessageReceivedListener) context;
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }

}
