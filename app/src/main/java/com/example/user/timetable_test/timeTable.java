package com.example.user.timetable_test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.timetable_test.CustomAdaptors.TimetableAdaptor;
import com.example.user.timetable_test.alarms.FirstAlarm;
import com.example.user.timetable_test.alarms.RemindersClass;
import com.example.user.timetable_test.testFiles.SharedPreference;
import com.example.user.timetable_test.testFiles.testClass;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class timeTable extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */

    MiscData data = MiscData.getInstance();
    SectionsPagerAdapter mSectionsPagerAdapter;
    PlaceholderFragment  fragment;
    Toolbar              toolbar;
    boolean              edit;


    ViewPager mViewPager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onBackPressed (){

        finish();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState){

        data.initNamesArray();
        final Intent[] dailyAlarmIntent = new Intent[1];
        dailyAlarmIntent[0] = new Intent(getApplicationContext(), FirstAlarm.class);

        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        final SharedPreferences pref =
                getSharedPreferences(SharedPreference.PREFERENCE.get(), Context.MODE_PRIVATE);
        final Calendar c = Calendar.getInstance();
        boolean isAlarmSet =
                (PendingIntent.getBroadcast(this, 0, dailyAlarmIntent[0], PendingIntent.FLAG_NO_CREATE) ==
                         null);

        //runnable
        Runnable setMiscData = new Runnable(){
            @Override
            public void run (){

                data.setFirstDay(pref.getInt(SharedPreference.FIRST_DAY.get(), 0));
                data.setDays(pref.getInt(SharedPreference.DAYS_NUMBER.get(), 0));
                data.setSubjectNames(pref.getString("subjects", " ").split(data.strSeparator));
                data.initNamesArray();

                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(SharedPreference.CHECK_SET_UP.get(), true);
                editor.apply();

            }
        };

        Runnable setAlarms = new Runnable(){
            @Override
            public void run (){
                try{
                    c.setTime(sdf.parse(pref.getString("startTime", "7:00")));
                } catch (ParseException e) {
                    Log.e("alarm", "caught parse exception setting alarm start time");
                }
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

                PendingIntent pendingIntent = PendingIntent
                        .getBroadcast(getApplicationContext(), 0, dailyAlarmIntent[0],
                                      PendingIntent.FLAG_UPDATE_CURRENT);

                manager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                                     android.app.AlarmManager.INTERVAL_DAY, pendingIntent);

                Intent reminderChecker = new Intent(getApplicationContext(), RemindersClass.class);
                PendingIntent reminders = PendingIntent
                        .getBroadcast(getApplicationContext(), 15, reminderChecker,
                                      PendingIntent.FLAG_UPDATE_CURRENT);
                manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, c.getTimeInMillis(),
                                            AlarmManager.INTERVAL_HALF_DAY, reminders);
            }
        };

        //threads
        Thread threadSetData = new Thread(setMiscData);
        threadSetData.start();
        Thread alarms = new Thread(setAlarms);
        if(isAlarmSet){
            alarms.start();
        }


        if(getIntent().getBooleanExtra("EXIT", false)){
            finish();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar_View_task_det);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        final Intent addTask = new Intent(this, AddTask.class);
        addTask.putExtra("lc", "timetable");
        addTask.putExtra("edit", false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){

                startActivity(addTask);
                finish();

            }
        });


        Calendar c1 = Calendar.getInstance();
        c1.setFirstDayOfWeek(Calendar.SUNDAY);

        int day;
        if(getIntent().getIntExtra("day", 15) == 15){
            day = (c1.get(Calendar.DAY_OF_WEEK) - data.getFirstDay()) % 7;
        } else {
            day = getIntent().getIntExtra("day", 0) + 1;
        }

        if(day < data.getDays()){
            mViewPager.setCurrentItem(day, true);
        } else {
            mViewPager.setCurrentItem(1);
        }


        //nave drawer stuff
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                                          R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        fragment = (PlaceholderFragment) mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
        edit = false;
    }


    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item){

        Intent tasks = new Intent(timeTable.this, ViewTasks.class);
        switch (item.getItemId()) {
            case R.id.tasks:
                startActivity(tasks);
                finish();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, testClass.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {
            case R.id.edit_table:
                Intent edit = new Intent(getApplicationContext(), EditTable.class);
                //edit.putExtra("day", data.getDay(mViewPager.getCurrentItem()));
                //Toast.makeText(getApplicationContext(), "Should edit", Toast.LENGTH_SHORT).show();
                startActivity(edit);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction (){

        Thing object = new Thing.Builder().setName("timeTable Page")
                                          .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]")).build();
        return new Action.Builder(Action.TYPE_VIEW).setObject(object)
                                                   .setActionStatus(Action.STATUS_TYPE_COMPLETED).build();
    }

    @Override
    public void onStart (){

        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop (){

        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        List <String>  names     = new ArrayList <>();
        List <Date>    endTimes  = new ArrayList <>();
        List <String>  locations = new ArrayList <>();
        List <Integer> length    = new ArrayList <>();
        List <Integer> tankNum   = new ArrayList <>();
        MiscData       data      = MiscData.getInstance();
        TableDBHandler dbHandler;
        RecyclerView   recyclerView;

        public PlaceholderFragment (){

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance (int sectionNumber){

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle              args     = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

            int day = getArguments().getInt(ARG_SECTION_NUMBER) - 1;

            View v = inflater.inflate(R.layout.fragment_time_table, container, false);

            recyclerView = (RecyclerView) v.findViewById(R.id.timetable_rec_view);
            TextView pageLabel = (TextView) v.findViewById(R.id.pageLabel);

            pageLabel.setText(data.getDay(day));

            dbHandler = new TableDBHandler(getContext(), null);

            names = dbHandler.getSessionNames(day);
            locations = dbHandler.getLocations(day);
            endTimes = dbHandler.getSessionsEnd(day);
            length = dbHandler.getSessionLength(day);

            for(String name : names){
                tankNum.add(name.equals("Break") || name.equals("Misc.") || name.equals("Free") ? 0 :
                            dbHandler.getTaskTitles(name).size());
            }

            TimetableAdaptor adaptor = new TimetableAdaptor(getContext(), day);
            adaptor.setLists(names, endTimes, length, locations, tankNum);
            recyclerView.setAdapter(adaptor);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            return v;
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter{

        SectionsPagerAdapter (FragmentManager fm){

            super(fm);
        }


        @Override
        public Fragment getItem (int position){

            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount (){

            return data.getDays();
        }

    }
}
