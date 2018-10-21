package com.example.user.timetable_test.testFiles;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.user.timetable_test.CustomAdaptors.TimetableAdaptor;
import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class testTimeTable extends AppCompatActivity{


    List <String>  names    = new ArrayList <>();
    List <Date>    endTimes = new ArrayList <>();
    List <String>  location = new ArrayList <>();
    List <Integer> tasks    = new ArrayList <>();
    List <Integer> length   = new ArrayList <>();
    TimetableAdaptor adaptor;
    TableDBHandler   dbHandler;


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_time_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MiscData.getInstance().initNamesArray();

        RecyclerView view = (RecyclerView) findViewById(R.id.table_rec_view);

        dbHandler = new TableDBHandler(this, null);
        adaptor = new TimetableAdaptor(this, 1);

        names = dbHandler.getSessionNames(1);
        endTimes = dbHandler.getSessionsEnd(1);
        location = names;
        for(String name : names){
            if(name.equals("Misc.") || name.equals("Break")){
                tasks.add(0);
            } else {
                tasks.add(dbHandler.getTaskTitles(name).size());
            }
        }
        length = dbHandler.getSessionLength(1);

        adaptor.setLists(names, endTimes, length, location, tasks);
        view.setAdapter(adaptor);
        view.setLayoutManager(new LinearLayoutManager(this));
        Log.i("debug", "fine here line 52");

    }

}
