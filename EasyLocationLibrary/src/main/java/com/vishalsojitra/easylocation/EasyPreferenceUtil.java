package com.vishalsojitra.easylocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;

class EasyPreferenceUtil {
    private static final String LAST_KNOWN_LOCATION = "last_known_location";
    private static final String GPS = "GPS";
    private static final String PREF_NAME = "easylocation";
    private static EasyPreferenceUtil instance;
    private final SharedPreferences mPreferences;

    private EasyPreferenceUtil(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static EasyPreferenceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new EasyPreferenceUtil(context.getApplicationContext());
        }
        return instance;
    }

    public Location getLastKnownLocation() {
        String locationString = mPreferences.getString(LAST_KNOWN_LOCATION, null);
        if(TextUtils.isEmpty(locationString))
            return null;
        else {
            String[] latLong = locationString.split(",");
            Location location = new Location(GPS);
            location.setLatitude(Double.parseDouble(latLong[0]));
            location.setLongitude(Double.parseDouble(latLong[1]));
            return location;
        }
    }
    public void saveLastKnownLocation(Location location) {
        EasyLocation easyLocation= new EasyLocation(location);
        mPreferences.edit().putString(LAST_KNOWN_LOCATION,easyLocation.toString()).apply();
    }
}