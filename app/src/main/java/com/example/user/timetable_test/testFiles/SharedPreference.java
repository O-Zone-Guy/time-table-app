package com.example.user.timetable_test.testFiles;

/**
 * Created by omar on 1/16/17.
 */

public enum SharedPreference{
    DAYS_NUMBER("NumDays"),
    CHECK_SET_UP("SetUpCheck"),
    FIRST_DAY("FirstDay"),
    PREFERENCE("pref"),
    SET_UP_PROG("setUpProg");

    private final String string;

    SharedPreference (String string){
        this.string = string;
    }

    public String get(){
     
	return string;

    }
}
