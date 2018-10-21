package com.example.user.timetable_test.testFiles;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;
import com.example.user.timetable_test.alarms.FirstAlarm;
import com.example.user.timetable_test.alarms.RemindersClass;

import java.util.Calendar;
import java.util.List;

import static com.example.user.timetable_test.testFiles.SharedPreference.PREFERENCE;


public class testClass extends AppCompatActivity{


    String string = "";


    @Override

    protected void onCreate (@Nullable Bundle savedInstanceState){


        final TableDBHandler dbHandler = new TableDBHandler(this, null);

        final MiscData data = MiscData.getInstance();


        final SharedPreferences pref = getSharedPreferences(PREFERENCE.toString(), MODE_PRIVATE);

        setContentView(R.layout.testing);


        Button immNoti = (Button) findViewById(R.id.setNoti);

        Button reset = (Button) findViewById(R.id.reset);

        Button reminder = (Button) findViewById(R.id.reminders);

        Button tempButton = (Button) findViewById(R.id.button2);


        final TextView times = (TextView) findViewById(R.id.endtimes);


        Calendar.getInstance().setFirstDayOfWeek(Calendar.SUNDAY);


        immNoti.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick (View view){


                string = "";

                Calendar c = Calendar.getInstance();
                c.setFirstDayOfWeek(Calendar.SUNDAY);
                List <Integer> integers = dbHandler.getSessionLength(2);
                for(int i = 0; i < integers.size(); i++){


                    string += String.valueOf(integers.get(i));

                    string += "\n";

                }


                times.setText(string);

            }

        });


        reset.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick (View view){


                sendBroadcast(new Intent(getApplicationContext(), FirstAlarm.class));

                Log.i("alarm", "sending broadcast to first alarm");

            }

        });


        reminder.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick (View v){


                sendBroadcast(new Intent(getApplicationContext(), RemindersClass.class));

                Log.i("alarm", "sending broadcast to reminders");


            }

        });


        tempButton.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick (View v){

                Toast.makeText(getApplicationContext(), "I dp nothing now", Toast.LENGTH_SHORT).show();

            }

        });


        super.onCreate(savedInstanceState);

    }


}
