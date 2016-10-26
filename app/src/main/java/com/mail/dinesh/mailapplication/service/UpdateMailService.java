package com.mail.dinesh.mailapplication.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.badoo.mobile.util.WeakHandler;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.mail.dinesh.mailapplication.conf.Configuration;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.utils.SharedPrefs;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dinesh on 26.10.16.
 */

public class UpdateMailService extends Service {

    private final static String TAG = UpdateMailService.class.getSimpleName();
    private WeakHandler mHandler;
    GoogleAccountCredential mCredential;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "OnCreate");
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());

        mHandler = new WeakHandler();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartService = new Intent(getApplicationContext(),this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(),
                1, restartService, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);
        //super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPrefs.storePref(Configuration.SERVICE_ON,true,getApplicationContext());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateDirtyMail.gMailToDirtyMail(mCredential,getApplicationContext());
            }
        },0,Configuration.FIFTEEN_MIN);
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                broadcastIntent.putExtra(Configuration.BROADCAST_SERVICE_ON,Configuration.BROADCAST_SERVICE_ON);
                getApplicationContext().sendBroadcast(broadcastIntent);
            }
        },5000);*/


        return START_STICKY;
    }
}
