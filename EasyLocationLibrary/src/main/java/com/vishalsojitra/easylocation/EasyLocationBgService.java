package com.vishalsojitra.easylocation;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class EasyLocationBgService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final long NO_FALLBACK = 0;
    private final String TAG = EasyLocationBgService.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private int mLocationMode;
    private LocationRequest mLocationRequest;
    private Handler handler;
    private long fallBackToLastLocationTime;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d(TAG, "onCreate: googleApiClient created");
        googleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand: googleApiClient start command " + intent.getAction());
        if (intent.getAction().equals(EasyLocationConstants.ACTION_LOCATION_FETCH_START)) {
            mLocationMode = intent.getIntExtra(EasyLocationIntentKey.LOCATION_FETCH_MODE, EasyLocationConstants.SINGLE_FIX);
            mLocationRequest = intent.getParcelableExtra(EasyLocationIntentKey.LOCATION_REQUEST);
            fallBackToLastLocationTime = intent.getLongExtra(EasyLocationIntentKey.FALLBACK_TO_LAST_LOCATION_TIME, NO_FALLBACK);
            if (mLocationRequest == null)
                throw new IllegalStateException("Location request can't be null");
            if (googleApiClient.isConnected())
                requestLocationUpdates();
        } else if (intent.getAction().equals(EasyLocationConstants.ACTION_LOCATION_FETCH_STOP)) {
            stopLocationService();
        }
        return START_NOT_STICKY;
    }

    private void requestLocationUpdates() {
        if (mLocationRequest != null) {
            startFallbackToLastLocationTimer();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }
    }

    private void startFallbackToLastLocationTimer() {
        if (fallBackToLastLocationTime != NO_FALLBACK) {
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(EasyLocationBgService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EasyLocationBgService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                }
            }, fallBackToLastLocationTime);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: googleApiClient connected");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: googleApiClient connection suspended");
        stopLocationService();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: googleApiClient connection failed");
        stopLocationService();
    }

    private void stopLocationService() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "stopLocationService: googleApiClient removing location updates");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            Log.d(TAG, "stopLocationService: googleApiClient disconnect");
            googleApiClient.disconnect();
        }
        Log.d(TAG, "stopLocationService: googleApiClient stop service");
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: googleApiClient location received");
        if (location != null) {
            EasyPreferenceUtil.getInstance(this).saveLastKnownLocation(location);
            Intent intent = new Intent();
            intent.setAction(EasyLocationConstants.INTENT_LOCATION_RECEIVED);
            intent.putExtra(EasyLocationIntentKey.LOCATION, location);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        if (mLocationMode == EasyLocationConstants.SINGLE_FIX)
            stopLocationService();
    }
}