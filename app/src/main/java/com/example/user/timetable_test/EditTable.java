package com.example.user.timetable_test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.user.timetable_test.CustomAdaptors.EditTimeTableAdaptor;
import com.example.user.timetable_test.CustomAdaptors.TimeTableItemTouchHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditTable extends AppCompatActivity{

    Spinner              spinner;
    RecyclerView         recyclerView;
    EditTimeTableAdaptor adaptor;
    MiscData data = MiscData.getInstance();

    List <String>  names     = new ArrayList <>();
    List <String>  locations = new ArrayList <>();
    List <Integer> taskNum   = new ArrayList <>();
    List <Integer> length    = new ArrayList <>();
    List <Date>    endTimes  = new ArrayList <>();

    TableDBHandler dbHandler;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_table);

        dbHandler = new TableDBHandler(this, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit_table);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                recyclerView.smoothScrollToPosition(adaptor.getItemCount());
                adaptor.addItem();
            }
        });

        spinner = (Spinner) findViewById(R.id.edit_table_spinner);
        recyclerView = (RecyclerView) findViewById(R.id.edit_table_rec);

        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter <String> arrayAdapter =
                new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, days);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        names = dbHandler.getSessionNames(data.getDay(spinner.getSelectedItem().toString()));
        locations = dbHandler.getLocations(data.getDay(spinner.getSelectedItem().toString()));
        length = dbHandler.getSessionLength(data.getDay(spinner.getSelectedItem().toString()));
        for(String name : names){
            taskNum.add(name.equals("Break") || name.equals("Misc.") || name.equals("Free") ? 0 :
                        dbHandler.getTaskTitles(name).size());
        }
        endTimes = dbHandler.getSessionsEnd(data.getDay(spinner.getSelectedItem().toString()));

        adaptor = new EditTimeTableAdaptor(this, data.getDay(spinner.getSelectedItem().toString()));
        adaptor.setLists(names, endTimes, length, locations, taskNum);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new TimeTableItemTouchHelper(adaptor);
        ItemTouchHelper          helper   = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {
            case R.id.saveTaskMenu:
                saveDay();
                finish();
                return true;
            case R.id.cancel:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void saveDay (){
        List <String>  names     = new ArrayList <>();
        List <String>  locations = new ArrayList <>();
        List <Integer> length    = new ArrayList <>();

        for(int i = 0; i < adaptor.nameSpinner.size(); i++){
            Log.i("debug", "saving session " + i);


            Spinner spinner = adaptor.nameSpinner.get(i);
            names.add(spinner.getSelectedItem().toString());

            EditText location = adaptor.locationText.get(i);
            locations.add(location.getText().toString());

            EditText lengthText = adaptor.lengthText.get(i);
            length.add(Integer.parseInt(lengthText.getText().toString()));
        }

        dbHandler.editDay((spinner.getSelectedItemPosition()) % 7, names, length, locations);
    }
}
