package com.example.user.timetable_test;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ViewTaskDetails extends AppCompatActivity{

    public String subject;
    public int    id;
    TableDBHandler dbHandler;


    @Override
    public void onBackPressed (){

        startActivity(new Intent(this, ViewTasks.class));
        finish();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState){

        dbHandler = new TableDBHandler(this, null);

        getWindow().setAllowEnterTransitionOverlap(true);

        Intent intent = getIntent();

        if(intent.getStringExtra("noti") != null){
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(intent.getIntExtra("remID", 0));
        }

        subject = intent.getStringExtra("subject");
        id = intent.getIntExtra("ids", 1);
        Log.i("SQL", subject + " " + id);


        SimpleDateFormat sdf          = new SimpleDateFormat("EEE,\nMM/yy", Locale.US);
        SimpleDateFormat sdfReminders = new SimpleDateFormat("hh:mm\ta    dd/MM/yy", Locale.US);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_View_task_det);
        setSupportActionBar(toolbar);


        if(getSupportActionBar() != null){

            getSupportActionBar().setTitle(subject);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){

                    startActivity(new Intent(getApplicationContext(), ViewTasks.class));
                }
            });

        }


        LinearLayout remindersLayout = (LinearLayout) findViewById(R.id.reminders_view);

        TextView title   = (TextView) findViewById(R.id.title);
        TextView desc    = (TextView) findViewById(R.id.task_desc);
        TextView setDate = (TextView) findViewById(R.id.setDate);
        TextView dueDate = (TextView) findViewById(R.id.dueDate);


        title.setText(dbHandler.getTitle(subject, id));
        desc.setText(dbHandler.getDesc(subject, id));
        setDate.setText(sdf.format(dbHandler.getSetDate(subject, id)));
        dueDate.setText(sdf.format(dbHandler.getDueDate(subject, id)));

        if(dbHandler.getReminders(subject, id) != null){

            final TextView reminder[] = new TextView[dbHandler.getReminders(subject, id).length];


            for(int i = 0; i < reminder.length; i++){

                reminder[i] = new TextView(this);
                remindersLayout.addView(reminder[i]);
                reminder[i].setText(sdfReminders.format(dbHandler.getReminders(subject, id)[i]));
                reminder[i].setTextAppearance(
                        android.R.style.TextAppearance_DeviceDefault_SearchResult_Subtitle);

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){

        getMenuInflater().inflate(R.menu.menu_view_task_details, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {
            case R.id.delTask:

                startActivity(new Intent(getApplicationContext(), ViewTasks.class));
                dbHandler.delTask(subject, id);
                finish();
                Toast.makeText(getApplicationContext(), "deleted task", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.editTask:
                Intent editTask = new Intent(getApplicationContext(), AddTask.class);
                editTask.putExtra("edit", true);
                editTask.putExtra("subject", subject);
                editTask.putExtra("ids", id);
                editTask.putExtra("lc", "viewTask");
                startActivity(editTask);
                finish();
                return true;

            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), ViewTasks.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
