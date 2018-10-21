package com.example.user.timetable_test.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;
import com.example.user.timetable_test.testFiles.SharedPreference;

import static com.example.user.timetable_test.testFiles.SharedPreference.DAYS_NUMBER;
import static com.example.user.timetable_test.testFiles.SharedPreference.FIRST_DAY;
import static com.example.user.timetable_test.testFiles.SharedPreference.SET_UP_PROG;


public class Set_Up extends FragmentActivity implements frag_welcome.welcomeListener, fragNumSubj.numSubj,
        subjNames.names, fDayWeek.days, fLeesonsPerDay.lessons, fBreakWeek.breaks, fMiscWeek.miscWeek{

    MiscData data = MiscData.getInstance();

    FragmentManager fm = getSupportFragmentManager();

    //Intent SetTable = new Intent(this, SetTable.class);

    SharedPreferences pref;

    @Override
    protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set__up);
        FragmentTransaction ftToWelcome = fm.beginTransaction();
        pref = getSharedPreferences(SharedPreference.PREFERENCE.get(), MODE_PRIVATE);
        int prog = pref.getInt(SharedPreference.SET_UP_PROG.get(), 0);

        switch (prog) {
            case 0:
                Fragment wel = new frag_welcome();

                ftToWelcome.add(R.id.fragment_container, wel);
                ftToWelcome.commit();
                break;
            case 1:
                Fragment numSubj = new fragNumSubj();

                ftToWelcome.add(R.id.fragment_container, numSubj);
                ftToWelcome.commit();
                break;
            case 2:
                data.setNoSubjects(pref.getInt("subjNum", 0));

                Fragment subjNames = new subjNames();

                ftToWelcome.add(R.id.fragment_container, subjNames);
                ftToWelcome.commit();
                break;
            case 3:
                data.setNoSubjects(pref.getInt("subjNum", 0));
                data.setSubjectNames(pref.getString("subjects", "noValues").split(","));

                Fragment days = new fDayWeek();

                ftToWelcome.add(R.id.fragment_container, days);
                ftToWelcome.commit();
                break;
            case 4:
                data.setFirstDay(pref.getInt(FIRST_DAY.get(), 0));
                data.setNoSubjects(pref.getInt("subjNum", 0));
                data.setSubjectNames(pref.getString("subjects", "noValues").split(","));

                Fragment lessons = new fLeesonsPerDay();

                ftToWelcome.add(R.id.fragment_container, lessons);
                ftToWelcome.commit();
                break;
            case 5:
                data.setFirstDay(pref.getInt(FIRST_DAY.get(), 0));
                data.setNoSubjects(pref.getInt("subjNum", 0));
                data.setSubjectNames(pref.getString("subjects", "noValues").split(","));
                data.setLessons(pref.getString("lessonsNumArray", "0"));

                Fragment breaks = new fBreakWeek();

                ftToWelcome.add(R.id.fragment_container, breaks);
                ftToWelcome.commit();
                break;
            case 6:
                data.setFirstDay(pref.getInt(FIRST_DAY.get(), 0));
                data.setNoSubjects(pref.getInt("subjNum", 0));
                data.setSubjectNames(pref.getString("subjects", "noValues").split(","));
                data.setLessons(pref.getString("lessonsNumArray", "0"));
                data.setBreaks(pref.getString("breaksNUmArray", "0"));

                Fragment misc = new fMiscWeek();

                ftToWelcome.add(R.id.fragment_container, misc);
                ftToWelcome.commit();
                break;
            case 7:
                data.setFirstDay(pref.getInt(FIRST_DAY.get(), 0));
                data.setNoSubjects(pref.getInt("subjNum", 0));
                data.setSubjectNames(pref.getString("subjects", "noValues").split(","));
                data.setLessons(pref.getString("lessonsNumArray", "0"));
                data.setBreaks(pref.getString("breaksNUmArray", "0"));
                data.setMisc(pref.getString("miscNumArray", "0"));

                startActivity(new Intent(this, SetTable.class));

                break;

        }

    }


    @Override
    public void firstSetUpFrag (){
        SharedPreferences.Editor editor = pref.edit();

        Fragment            fragment = new fragNumSubj();
        FragmentTransaction ft       = fm.beginTransaction();

        editor.putInt(SET_UP_PROG.get(), 1);
        editor.apply();

        ft.setCustomAnimations(R.anim.frag_in, R.anim.frag_out);
        ft.replace(R.id.fragment_container, fragment);
        ft.disallowAddToBackStack();
        //add anim
        ft.commit();
    }


    @Override
    public void setSubjNum (int subjNum){
        SharedPreferences.Editor editor = pref.edit();

        Fragment            fragment = new subjNames();
        FragmentTransaction ft       = fm.beginTransaction();

        data.setNoSubjects(subjNum);
        editor.putInt(SET_UP_PROG.get(), 2);
        editor.putInt("subjNum", subjNum);
        editor.apply();

        ft.setCustomAnimations(R.anim.frag_in, R.anim.frag_out);
        ft.replace(R.id.fragment_container, fragment);
        ft.disallowAddToBackStack();
        //add anim
        ft.commit();

    }


    @Override
    public void subjNamesNext (){
        SharedPreferences.Editor editor = pref.edit();

        Fragment            fragment = new fDayWeek();
        FragmentTransaction ft       = fm.beginTransaction();

        editor.putInt(SET_UP_PROG.get(), 3);
        editor.apply();

        ft.setCustomAnimations(R.anim.frag_in, R.anim.frag_out);
        ft.replace(R.id.fragment_container, fragment);
        ft.disallowAddToBackStack();
        ft.commit();
    }


    @Override
    public void daysNext (){
        SharedPreferences.Editor editor = pref.edit();

        Fragment            fragment = new fLeesonsPerDay();
        FragmentTransaction ft       = fm.beginTransaction();

        editor.putInt(FIRST_DAY.get(), data.getFirstDay());
        editor.putInt(DAYS_NUMBER.get(), data.getDays());
        editor.putInt(SET_UP_PROG.get(), 4);
        editor.apply();

        ft.setCustomAnimations(R.anim.frag_in, R.anim.frag_out);
        ft.replace(R.id.fragment_container, fragment);
        ft.disallowAddToBackStack();
        ft.commit();

    }


    @Override
    public void lessons (){
        SharedPreferences.Editor editor = pref.edit();

        Fragment            fragment = new fBreakWeek();
        FragmentTransaction ft       = fm.beginTransaction();

        editor.putString("lessonsNumArray", data.convertArrayToString(data.getLessons()));
        editor.putInt(SET_UP_PROG.get(), 5);
        editor.apply();

        ft.setCustomAnimations(R.anim.frag_in, R.anim.frag_out);
        ft.replace(R.id.fragment_container, fragment);
        ft.disallowAddToBackStack();
        ft.commit();
    }


    @Override
    public void breaksNext (){
        SharedPreferences.Editor editor = pref.edit();

        Fragment            fragment = new fMiscWeek();
        FragmentTransaction ft       = fm.beginTransaction();

        editor.putString("breaksArray", data.convertArrayToString(data.getBreaks()));
        editor.putInt(SET_UP_PROG.get(), 6);
        editor.apply();

        ft.setCustomAnimations(R.anim.frag_in, R.anim.frag_out);
        ft.replace(R.id.fragment_container, fragment);
        ft.disallowAddToBackStack();
        ft.commit();
    }


    @Override
    public void miscNext (){
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("miscNumArray", data.convertArrayToString(data.getMisc()));
        editor.putInt(SET_UP_PROG.get(), 7);
        editor.apply();

        startActivity(new Intent(this, SetTable.class));

    }
}

