package com.example.user.timetable_test;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MiscData{

    private static MiscData instance = new MiscData();
    String strSeparator = ",";

    private List <String> subjects   = new ArrayList <>();
    private List <String> namesArray = new ArrayList <>();

    //days
    private int firstDay, noSubjects, days, daysDone, lessonLength, breakLength, miscLength;
    //per day
    private int[] lessons, misc, breaks, sessions;

    private Date startTime = new Date();

    //constructor
    private boolean dayDone[];


    private MiscData (){
    }

    public static MiscData getInstance (){

        return instance;
    }


    //getter methods
    Date getStartTime (){

        return startTime;
    }

    //setter methods
    public void setStartTime (String time){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        try{

            startTime = sdf.parse(time);
        } catch (ParseException e) {

            Log.e("SQL", "Caught parse exception while setting start time");
        }
    }

    public int getLessonLength (){

        return lessonLength;
    }

    public void setLessonLength (int length){

        lessonLength = length;
    }

    public int getBreakLength (){

        return breakLength;
    }

    public void setBreakLength (int length){

        breakLength = length;
    }

    public int getMiscLength (){

        return miscLength;
    }

    public void setMiscLength (int length){

        miscLength = length;
    }

    public int[] getMisc (){

        return misc;
    }

    public void setMisc (String string){

        String stringArray[] = string.split(",");

        misc = null;
        misc = new int[stringArray.length];

        for(int i = 0; i < stringArray.length; i++){

            misc[i] = Integer.parseInt(stringArray[i].trim());

        }


    }

    public int getNoSubjects (){

        return noSubjects;
    }

    public void setNoSubjects (int noSubjects){

        this.noSubjects = noSubjects;

    }

    public int getDays (){

        return days;
    }

    public void setDays (int days){

        this.days = days;

        lessons = new int[days];
        misc = new int[days];
        breaks = new int[days];
        sessions = new int[days];

    }

    public int[] getLessons (){

        return lessons;
    }

    public void setLessons (String string){

        String stringArray[] = string.split(",");

        lessons = null;
        lessons = new int[stringArray.length];

        for(int i = 0; i < stringArray.length; i++){

            lessons[i] = Integer.parseInt(stringArray[i].trim());

        }


    }

    public int[] getBreaks (){

        return breaks;
    }

    public void setBreaks (String string){

        String stringArray[] = string.split(",");

        breaks = null;
        breaks = new int[stringArray.length];

        for(int i = 0; i < stringArray.length; i++){

            breaks[i] = Integer.parseInt(stringArray[i].trim());

        }


    }

    public int getSessions (int day){

        return sessions[day];
    }

    public List <String> getSubjects (){

        return subjects;
    }

    String getSubject (int i){

        //String[] subjects = this.subjects.toArray(new String[noSubjects]);

        return subjects.get(i);
    }

    public int getFirstDay (){

        return firstDay;
    }

    public void setFirstDay (int firstDay){

        this.firstDay = firstDay;
    }

    public List <String> getNamesArray (){

        return namesArray;
    }

    public int getDaysDone (){

        return daysDone;
    }

    public void setDaysDone (int daysDone){

        this.daysDone = daysDone;

    }

    public boolean isDayDone (int day){

        return dayDone[day];
    }

    public void setLessons (int day, int lessonsNo){

        lessons[day] = lessonsNo;
    }

    public void setMisc (int day, int miscNo){

        misc[day] = miscNo;
    }

    public void setBreaks (int day, int breaksNo){

        breaks[day] = breaksNo;
    }

    public void addSubject (String subjName){

        subjects.add(subjName);


    }

    public void setSubjectNames (String[] subjectNames){


        subjects = Arrays.asList(subjectNames);
    }

    public void setDayDone (int day, boolean Boolean){

        dayDone[day] = Boolean;

    }

    //initialise arrays
    public void initSessions (){


        for(int i = 0; i < days; i++){

            sessions[i] = breaks[i] + lessons[i] + misc[i];

        }

    }

    public void initNamesArray (){

        namesArray.add("Break");
        namesArray.add("Misc.");
        namesArray.add("Free");
        namesArray.addAll(subjects);
    }

    public void initDayDone (){

        dayDone = new boolean[days];

        for(int i = 0; i < days; i++){

            dayDone[i] = false;
        }

    }

    //methods
    public String getDay (int dayInt){

        String day;

        dayInt = dayInt + getFirstDay();

        dayInt = dayInt % 7;

        switch (dayInt) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 0:
                day = "Saturday";
                break;
            default:
                day = "Sunday";

        }

        return day;
    }

    public int getDay (String day){
        int dayInt;

        switch (day) {
            case "Sunday":
                dayInt = 1;
                break;
            case "Monday":
                dayInt = 2;
                break;
            case "Tuesday":
                dayInt = 3;
                break;
            case "Wednesday":
                dayInt = 4;
                break;
            case "Thursday":
                dayInt = 5;
                break;
            case "Friday":
                dayInt = 6;
                break;
            case "Saturday":
                dayInt = 0;
                break;
            default:
                dayInt = 1;
                break;
        }

        dayInt--;

        return dayInt;
    }

    public String convertArrayToString (String[] array){

        String str = "";
        for(int i = 0; i < array.length; i++){
            str = str + array[i];
            // Do not append comma at the end of last element
            if(i < array.length - 1){
                str = str + strSeparator;
            }
        }
        return str;
    }

    public String convertArrayToString (int[] array){

        String str = "";
        for(int i = 0; i < array.length; i++){

            str = str + String.valueOf(array[i]);

            if(i < array.length - 1){
                str = str + strSeparator;
            }
        }
        return str;
    }

}

