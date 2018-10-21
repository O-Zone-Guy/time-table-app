package com.example.user.timetable_test.alarms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;
import com.example.user.timetable_test.timeTable;

import java.util.Calendar;

import static com.example.user.timetable_test.testFiles.SharedPreference.DAYS_NUMBER;
import static com.example.user.timetable_test.testFiles.SharedPreference.FIRST_DAY;
import static com.example.user.timetable_test.testFiles.SharedPreference.PREFERENCE;


public class FirstAlarm extends BroadcastReceiver{


    @Override
    public void onReceive (Context context, Intent intent){

        Log.i("alarm", "inside first alarm");

        //calling all objects
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

        //setting up calendar object
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);

        //dealing with shared preferences
        editor.putInt("curSession", 0);
        editor.apply();


        //variables
        int curSession = pref.getInt("curSession", 0);

        int day = c.get(Calendar.DAY_OF_WEEK);
        day = (day - pref.getInt(FIRST_DAY.get(), 1)) % 7;
        String sessionName = dbHandler.getSessionName(day, curSession);


        if(day < pref.getInt(DAYS_NUMBER.get(), 5)){

            //setting intent extras
            earlyAlarm.putExtra("curSession", curSession);

            Intent timetable = new Intent(context, timeTable.class);
            PendingIntent pendingIntent1 =
                    PendingIntent.getActivity(context, 5, timetable, PendingIntent.FLAG_UPDATE_CURRENT);

            //building notification
            builder.setContentTitle(sessionName);
            builder.setContentText("You have: " + sessionName);
            builder.setSmallIcon(R.mipmap.icon_launcher);
            builder.setContentIntent(pendingIntent1);
            builder.setAutoCancel(true);
            notificationManager.notify(0, builder.build());


            //setting alarm for early alarm
            c.setTime(dbHandler.getSessionEnd(day, curSession));
            c.add(Calendar.MINUTE, -5);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        } else {

            builder.setContentTitle("Weekend!");
            builder.setContentText("Enjoy!");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.icon_launcher);
            notificationManager.notify(0, builder.build());
        }
    }
}
