package com.example.user.timetable_test.alarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;
import com.example.user.timetable_test.ViewTaskDetails;


public class ReminderAlarms extends BroadcastReceiver{

    @Override
    public void onReceive (Context context, Intent intent){

        TableDBHandler dbHandler = new TableDBHandler(context, null);

        int reminderID = intent.getIntExtra("id", 0);

        boolean valid = dbHandler.getSubjectNameReminder(reminderID) != null;

        if(valid){
            String subject = dbHandler.getSubjectNameReminder(reminderID);
            String title   = dbHandler.getTitle(subject, dbHandler.getTaskIDReminder(reminderID));

            Intent viewTask = new Intent(context, ViewTaskDetails.class);
            viewTask.putExtra("id", dbHandler.getTaskIDReminder(reminderID));
            viewTask.putExtra("subject", subject);
            viewTask.putExtra("noti", "noti");
            viewTask.putExtra("remID", reminderID);

            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, reminderID, viewTask, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle(subject);
            builder.setContentText(title);
            builder.setSmallIcon(R.mipmap.icon_launcher);
            builder.setContentIntent(pendingIntent);

            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(reminderID, builder.build());
            dbHandler.remDone(reminderID);
        } else {
            Log.i("alarm", "reminder deleted or doesn't exist");
        }
    }
}
