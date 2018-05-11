package com.example.vijay.neverendingservice;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;
    static boolean status;

    public Context getCtx() {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new SensorService(getCtx());
        bindService();
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }

        }
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);

        super.onDestroy();

    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                SensorService.LocalBinder binder = (SensorService.LocalBinder) service;
                mSensorService = binder.getService();
                status = true;

            } catch (Exception e) {
                Log.e("exception ", e.getMessage());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;

        }
    };

    void bindService() {
        if (status == true)
            return;
        try {
            Intent i = new Intent(getApplicationContext(), SensorService.class);
            bindService(i, sc, BIND_AUTO_CREATE);
            Intent broadcastIntent = new Intent(".RestartSensor");
            sendBroadcast(broadcastIntent);
            status = true;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage());
        }

    }
}


