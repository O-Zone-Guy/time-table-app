package com.example.user.timetable_test.alarms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;
import com.example.user.timetable_test.timeTable;

import java.util.Calendar;

import static com.example.user.timetable_test.testFiles.SharedPreference.FIRST_DAY;
import static com.example.user.timetable_test.testFiles.SharedPreference.PREFERENCE;


public class EarlyAlarm extends BroadcastReceiver{


    @Override
    public void onReceive (Context context, Intent intent){

        TableDBHandler           dbHandler = new TableDBHandler(context, null);
        SharedPreferences        pref      =
                context.getSharedPreferences(PREFERENCE.get(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor    = pref.edit();

        AlarmManager         alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Notification.Builder builder      = new Notification.Builder(context);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //setting up calendar object
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);

        //variables
        int day = c.get(Calendar.DAY_OF_WEEK);
        day = (day - pref.getInt(FIRST_DAY.get(), 1)) % 7;
        int    curSession      = pref.getInt("curSession", 0);
        String nextSessionName = "";

        //setting up intent
        Intent onTimeAlarm = new Intent(context, OnTImeAlarm.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 1, onTimeAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        //set up alarm time
        c.setTime(dbHandler.getSessionEnd(day, curSession));

        Intent timeTableIntent = new Intent(context, timeTable.class);
        PendingIntent timeTablePIntent =
                PendingIntent.getActivity(context, 5, timeTableIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(curSession + 1 < dbHandler.getSessionNumOnDay(day)){

            nextSessionName = dbHandler.getSessionName(day, curSession + 1);

            //building notification
            builder.setContentTitle(nextSessionName);
            builder.setContentText("You have " + nextSessionName + " in 5 min.");
            builder.setSmallIcon(R.mipmap.icon_launcher);
            builder.setContentIntent(timeTablePIntent);
            notificationManager.notify(0, builder.build());
            //set up alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        } else {

            //say that the day is over
            builder.setContentTitle("Have a nice day!");
            builder.setContentText("The day is over.");
            builder.setSmallIcon(R.mipmap.icon_launcher);
            builder.setAutoCancel(true);
            notificationManager.notify(0, builder.build());

        }

        editor.putInt("curSession", curSession);
        editor.putString("sessionName", nextSessionName);
        editor.apply();

    }
}
