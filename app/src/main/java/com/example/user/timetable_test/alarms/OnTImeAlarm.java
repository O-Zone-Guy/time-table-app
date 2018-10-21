package com.example.user.timetable_test.alarms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;
import com.example.user.timetable_test.timeTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.user.timetable_test.testFiles.SharedPreference.FIRST_DAY;
import static com.example.user.timetable_test.testFiles.SharedPreference.PREFERENCE;


public class OnTImeAlarm extends BroadcastReceiver{

    @Override
    public void onReceive (Context context, Intent intent){


        TableDBHandler dbHandler = new TableDBHandler(context, null);
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE.get(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor     = pref.edit();
        Intent                   earlyAlarm = new Intent(context, EarlyAlarm.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 1, earlyAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager         alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Notification.Builder builder      = new Notification.Builder(context);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //set up calendar object
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);

        //setting up variables
        int    curSession  = pref.getInt("curSession", 0) + 1;
        String sessionName = pref.getString("sessionName", null);

        int day = c.get(Calendar.DAY_OF_WEEK);
        day = (day - pref.getInt(FIRST_DAY.get(), 1)) % 7;

        //set up early alarm time
        c.setTime(dbHandler.getSessionEnd(day, curSession ));
        c.add(Calendar.MINUTE, -5);

        //set pending intent
        Intent timeTableIntent = new Intent(context, timeTable.class);
        PendingIntent pendingIntent1 =
                PendingIntent.getActivity(context, 5, timeTableIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //make notification
        builder.setContentTitle(sessionName);
        builder.setContentText("You have " + sessionName + ".");
        builder.setSmallIcon(R.mipmap.icon_launcher);
        builder.setContentIntent(pendingIntent1);
        notificationManager.notify(0, builder.build());

        //setting alarm for
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        editor.putInt("curSession", curSession);
        editor.apply();
    }
}
