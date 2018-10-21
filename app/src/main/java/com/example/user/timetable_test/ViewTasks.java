package com.example.user.timetable_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.user.timetable_test.CustomAdaptors.ViewAllTasksAdaptor;
import com.example.user.timetable_test.CustomAdaptors.ViewTasksAdaptor;
import com.example.user.timetable_test.testFiles.testClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ViewTasks extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TableDBHandler dbHandler;
    MiscData data = MiscData.getInstance();
    //SimpleDateFormat sdf  = new SimpleDateFormat("E, dd/MM/yy", Locale.US);

    //for all tasks
    List <String> aNames = new ArrayList <>();

    //for single subjects
    List <String>  titles  = new ArrayList <>();
    List <String>  desc    = new ArrayList <>();
    List <Date>    dueDate = new ArrayList <>();
    List <Integer> ids     = new ArrayList <>();

    @Override
    public boolean onCreateOptionsMenu (Menu menu){

        getMenuInflater().inflate(R.menu.menu_view_tasks, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {

            case R.id.add_task:

                Intent addTask = new Intent(this, AddTask.class);
                addTask.putExtra("lc", "tasks");
                addTask.putExtra("edit", false);
                startActivity(addTask);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item){

        Intent timetable = new Intent(this, timeTable.class);
        Intent settings  = new Intent(this, testClass.class);

        switch (item.getItemId()) {

            case R.id.table:

                startActivity(timetable);
                return true;
            case R.id.settings:
                startActivity(settings);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed (){

        Intent timeTable = new Intent(this, com.example.user.timetable_test.timeTable.class);
        timeTable.addCategory(Intent.CATEGORY_HOME);
        startActivity(timeTable);

    }


    @Override
    protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_view_tasks);

        dbHandler = new TableDBHandler(this, null);

        final RecyclerView tasks          = (RecyclerView) findViewById(R.id.tasks_list);
        Spinner            subjectSpinner = (Spinner) findViewById(R.id.tasks_list_spinner);
        Toolbar            toolbar        = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        //nav view stuff
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_tasks);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                                          R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_task);
        navigationView.setNavigationItemSelectedListener(this);

        final ViewTasksAdaptor    viewTasksAdaptor    = new ViewTasksAdaptor(this, this);
        final ViewAllTasksAdaptor viewAllTasksAdaptor = new ViewAllTasksAdaptor(this, this);

        aNames.addAll(data.getSubjects());
        viewAllTasksAdaptor.setLists(data.getSubjects());
        aNames.add("All");

        ArrayAdapter <String> spinnerAdaptor =
                new ArrayAdapter <>(this, android.R.layout.simple_spinner_item, aNames);
        spinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(spinnerAdaptor);


        final String name = subjectSpinner.getSelectedItem().toString();

        switch (name) {
            case "All":
                tasks.setAdapter(viewAllTasksAdaptor);
                tasks.setLayoutManager(new LinearLayoutManager(this));
                break;
            default:
                setData(name);
                viewTasksAdaptor.setViewLists(titles, desc, dueDate, ids, name);
                tasks.setAdapter(viewTasksAdaptor);
                tasks.setLayoutManager(new LinearLayoutManager(this));
                break;
        }

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected (AdapterView <?> parent, View view, int position, long id){

                String name = aNames.get(position);

                switch (name) {
                    case "All":
                        tasks.setAdapter(viewAllTasksAdaptor);
                        break;
                    default:
                        setData(name);
                        viewTasksAdaptor.setViewLists(titles, desc, dueDate, ids, name);
                        tasks.setAdapter(viewTasksAdaptor);
                        break;
                }

            }

            @Override
            public void onNothingSelected (AdapterView <?> parent){

            }
        });

    }

    public void setData (String subjectName){
        ids = dbHandler.getIDs(subjectName);
        titles = dbHandler.getTaskTitles(subjectName);
        desc = dbHandler.getTaskDescs(subjectName);
        dueDate = dbHandler.getDueDate(subjectName);
    }

}
