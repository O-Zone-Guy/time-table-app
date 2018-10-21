package com.example.user.timetable_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.user.timetable_test.setup.Set_Up;

import static com.example.user.timetable_test.testFiles.SharedPreference.CHECK_SET_UP;
import static com.example.user.timetable_test.testFiles.SharedPreference.DAYS_NUMBER;
import static com.example.user.timetable_test.testFiles.SharedPreference.FIRST_DAY;


public class Router extends AppCompatActivity{

    private static final String TAG = "onlyThis";

    Intent setUp;
    Intent timeTable;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            String message = (String) msg.obj;
            switch(message){
                case "timeTable":
                    startActivity(timeTable);
                    finish();
                    break;
                case "setUp":
                    startActivity(setUp);
                    finish();
                    break;
                default:
                    Log.i("debug", "handler, something happened");
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rerouter);

        final MiscData data = MiscData.getInstance();

        final SharedPreferences        pref   = getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        editor.putInt(FIRST_DAY.toString(), 0);
        editor.putInt(DAYS_NUMBER.toString(), 0);


        setUp = new Intent(this, Set_Up.class);
        timeTable = new Intent(this, timeTable.class);

        final Message message = handler.obtainMessage();

        Runnable startActivity = new Runnable(){
            @Override
            public void run(){


                if(pref.getBoolean(CHECK_SET_UP.get(), false)){
                    message.obj = "timeTable";
                    handler.sendMessage(message);
                    Log.i(TAG, "Went to Timetable");
                } else{
                    editor.putBoolean(CHECK_SET_UP.get(), false);
                    editor.apply();
                    message.obj = "setUp";
                    handler.sendMessage(message);
                    Log.i(TAG, "Went to Set Up");
                }

            }
        };

        Thread thread = new Thread(startActivity);
        thread.start();
    }
}