package com.vtca.mytravels.minhgiang;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vtca.mytravels.MainActivity;
import com.vtca.mytravels.R;
import com.vtca.mytravels.base.MyApplication;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.entity.Travel;
import com.vtca.mytravels.quanghung.UpComingTravelsActivity;
import com.vtca.mytravels.repository.TravelRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import static android.content.Context.NOTIFICATION_SERVICE;


public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 0;
    public static boolean firstTime = true;
    Calendar calendar;
    NotificationManager mManager;
    String CHANNEL_ID = "my_channel_01";
    private Context mContext;
    private List<Travel> travelList = new ArrayList<>();
    SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (firstTime) {
            firstTime = false;
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                    preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    calendar = Calendar.getInstance();
                    TravelRepository mTravelRepository = TravelRepository.getInstance(((MyApplication) mContext.getApplicationContext()));
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    long countUpComingDays = preferences.getLong(MyConst.UP_COMING_DAY, 1);
                    long range = countUpComingDays * 24 * 60 * 60 * 1000;
                    travelList = mTravelRepository.getAllTravelsUpComingWithoutLiveData(currentTime, range);
                    Log.d("giangtm1", "onReceive: " + travelList.size() + "variable = "
                            + preferences.getBoolean(MyConst.NOTIFICATION_ON_OFF, false));
                    if (preferences.getBoolean(MyConst.NOTIFICATION_ON_OFF, false) && travelList.size() > 0) {
                        showNotifi(mContext);
                    }

                }
            });
            thread.start();
        }



    }

    public void showNotifi(Context context) {
        Intent intent1 = new Intent(context, UpComingTravelsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent1);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //   Uri sound = RingtoneManager.getDefaultType(RingtoneManager.getValidRingtoneUri())

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Travel is coming !!")
                .setContentText("You have " + travelList.size() + " travels is comming")
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(alarmSound)
                .setOnlyAlertOnce(true)
                .build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mManager.notify(0, notification);
    }

}

