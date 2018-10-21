package com.example.user.timetable_test.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.user.timetable_test.TableDBHandler;

import java.util.Date;


public class RemindersClass extends BroadcastReceiver{

    @Override
    public void onReceive (Context context, Intent intent){

        Log.i("alarm", "inside reminder class");

        TableDBHandler dbHandler = new TableDBHandler(context, null);
        AlarmManager   manager   = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int ids[];
        ids = dbHandler.getAllReminderIDs();

        for(int i : ids){

            boolean checker = dbHandler.isRemDone(i);
            if(!checker){
                Date date = dbHandler.getReminder(i);

                Intent reminder = new Intent(context, ReminderAlarms.class);
                reminder.putExtra("id", i);
                PendingIntent pendingIntent =
                        PendingIntent.getBroadcast(context, i, reminder, PendingIntent.FLAG_UPDATE_CURRENT);

                Log.i("alarm", "setting reminder alarm " + i + " " + ids.length);
                manager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
            }
        }

    }
}
