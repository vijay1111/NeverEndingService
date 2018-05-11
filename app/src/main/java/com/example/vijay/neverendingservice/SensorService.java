package com.example.vijay.neverendingservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaCas;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.security.spec.ECField;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SensorService extends Service
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public int counter = 0;
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    static LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    static Location mCurrentLocation, lStart, lEnd;
    static double distance = 0, speed;
    private final IBinder mBinder = new LocalBinder();
    static double longitude, latitude, gpstime;
    SensorService sensorService;
    private Timer timer;
    private TimerTask timerTask;


    public class LocalBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
        Log.e("stopLocationUpdates", "stopLocationUpdates");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }


// --------------------------------------------------------------------------


    public SensorService(Context applicationContext) {
        super();
        Log.e("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(".RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.e("in timer", "in timer ++++  " + (counter++));

                createLocationRequest();
                try {
                    Log.e("initializeTimerTask latitude", String.valueOf(mCurrentLocation.getLatitude()));
                    Log.e("initializeTimerTask longitude", String.valueOf(mCurrentLocation.getLongitude()));



                } catch (Exception e) {

                }


            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
//        SensorService.longitude = mCurrentLocation.getLongitude();
//        SensorService.latitude = mCurrentLocation.getLatitude();
//        SensorService.gpstime = mCurrentLocation.getTime();

        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
//            Log.e("lStart", String.valueOf(lStart));
//            Log.e("lEnd", String.valueOf(lEnd));

        } else
            lEnd = mCurrentLocation;
        speed = location.getSpeed() * 18 / 5;
        Log.e("speed ", String.valueOf(speed));
    }
}