package com.xbenjii.pokemock;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by erdal.gunes on 7/17/16.
 */
public class SharedPreferencesStorage {

    private SharedPreferences sharedPreferences;

    private Context context;

    public SharedPreferencesStorage(Context context) {
        this.context = context;
    }

    public void saveCoordinates(LatLng latLng) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("coordinates", Context.MODE_PRIVATE);
        }

        sharedPreferences.edit().putLong("Latitude", Double.doubleToLongBits(latLng.latitude)).apply();
        sharedPreferences.edit().putLong("Longitude", Double.doubleToLongBits(latLng.longitude)).apply();
    }

    public LatLng getLastCoordinates() {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("coordinates", Context.MODE_PRIVATE);
        }

        return new LatLng(Double.longBitsToDouble(sharedPreferences.getLong("Latitude", Double.doubleToLongBits(53.7282337))),
                Double.longBitsToDouble(sharedPreferences.getLong("Longitude", Double.doubleToLongBits(-1.8642777))));
    }
}
