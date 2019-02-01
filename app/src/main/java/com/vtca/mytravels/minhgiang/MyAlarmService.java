package com.vtca.mytravels.minhgiang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vtca.mytravels.base.MyConst;

import androidx.annotation.Nullable;

public class MyAlarmService extends Service {
    SharedPreferences preferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyTestService", "Service running");
        scheduleAlarm();
        return START_STICKY;
    }

    private void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("fisrt_time", true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long time = preferences.getLong(MyConst.FREQUENCY_NOTI, 86400000);
        intent.setAction("alarm");
        final PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                time, pIntent);
    }
}
