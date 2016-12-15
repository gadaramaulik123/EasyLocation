package com.vishalsojitra.easylocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

class EasyLocationBroadcastReceiver extends BroadcastReceiver {
    private final EasyLocationListener easyLocationListener;

    public EasyLocationBroadcastReceiver(EasyLocationListener easyLocationListener) {
        this.easyLocationListener = easyLocationListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(EasyLocationConstants.INTENT_LOCATION_RECEIVED)) {
            Location location = intent.getParcelableExtra(EasyLocationIntentKey.LOCATION);
            easyLocationListener.onLocationReceived(location);
        }
    }
}