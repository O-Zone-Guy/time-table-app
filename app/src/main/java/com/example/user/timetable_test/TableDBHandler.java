package com.example.user.timetable_test;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.user.timetable_test.testFiles.SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TableDBHandler extends SQLiteOpenHelper{

    //database info
    private static int    DATABASE_VERSION = 6;
    private static String DATABASE_NAME    = "Timetable.db";

    //sessions table
    private static String[] TABLE_DAY          = new String[7];
    private static String   COLUMN_SESSION_NUM = "_id";
    private static String   COLUMN_NAME        = "name";
    private static String   COLUMN_LENGTH      = "length";
    private static String   COLUMN_END_TIME    = "end_time";
    private static MiscData data               = MiscData.getInstance();

    //tasks table
    private static String[] TABLE_TASKS     = new String[data.getNoSubjects()];
    private static String   COLUMN_ID       = "_id";
    private static String   COLUMN_TITLE    = "title";
    private static String   COLUMN_DESC     = "desc";
    private static String   COLUMN_SET_DATE = "setData";
    private static String   COLUMN_DUE_DATE = "dueDate";
    private static String   COLUMN_LOC_DESC = "location";

    //reminders table
    private static String TABLE_REMINDERS      = "reminders";
    private static String COLUMN_REMINDER_ID   = "_rID";
    private static String COLUMN_SUBJECT       = "subject";
    private static String COLUMN_TASK_ID       = "task_id";
    private static String COLUMN_REMINDER_TIME = "time";
    private static String COLUMN_REM_CHECKER   = "isDone";

    private static String           dateFormat  = "dd-MM-yyyy";
    private static SimpleDateFormat df          = new SimpleDateFormat(dateFormat, Locale.US);
    private static SimpleDateFormat sdfReminder = new SimpleDateFormat("d-M-yyyy h:mm a", Locale.US);
    private static SimpleDateFormat sdfEndTime  = new SimpleDateFormat("kk mm", Locale.US);
    private static SimpleDateFormat sdfHour     = new SimpleDateFormat("kk", Locale.US);
    private static SimpleDateFormat sdfMinute   = new SimpleDateFormat("mm", Locale.US);

    private Context context;

    private SharedPreferences pref;


    public TableDBHandler (Context context, SQLiteDatabase.CursorFactory factory){

        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
        pref = this.context
                .getSharedPreferences(SharedPreference.PREFERENCE.toString(), Context.MODE_PRIVATE);

        for(int i = 0; i < 7; i++){

            TABLE_DAY[i] = data.getDay(i);

        }

        for(int i = 0; i < data.getNoSubjects(); i++){

            TABLE_TASKS[i] = data.getSubject(i);

        }

    }


    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase){


        for(int i = 0; i < 7; i++){
            String query = "CREATE TABLE IF NOT EXISTS " + TABLE_DAY[i] + "(" + COLUMN_SESSION_NUM +
                    " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," +
                    COLUMN_LENGTH + " INTEGER, " + COLUMN_END_TIME + " TEXT, " + COLUMN_LOC_DESC + " TEXT " +
                    ");";

            Log.i("SQL", "Day Table created, no: " + i);

            sqLiteDatabase.execSQL(query);
        }

        for(int i = 0; i < data.getNoSubjects(); i++){

            String query = "CREATE TABLE IF NOT EXISTS " + TABLE_TASKS[i] + "(" + COLUMN_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT,  " + COLUMN_DESC + " TEXT, " + COLUMN_SET_DATE +
                    " TEXT, " + COLUMN_DUE_DATE + " TEXT);";

            Log.i("SQL", "Tasks Table created , no: " + i);

            sqLiteDatabase.execSQL(query);

        }

        String query =
                "CREATE TABLE IF NOT EXISTS " + TABLE_REMINDERS + "(" + COLUMN_REMINDER_ID + " INTEGER " +
                        "PRIMARY KEY " +
                        "AUTOINCREMENT, " + COLUMN_SUBJECT + " TEXT, " + COLUMN_TASK_ID + " INTEGER, " +
                        COLUMN_REMINDER_TIME + " TEXT, " + COLUMN_REM_CHECKER + " INTEGER);";

        sqLiteDatabase.execSQL(query);
    }


    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int i, int i1){

        Log.i("SQL", "DB updated");


        for(int j = 0; j < 7; j++){

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY[j]);

        }

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        onCreate(sqLiteDatabase);


    }


    //Session table methods
    public void addSession (int day, String name, int length){

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LENGTH, length);


        String         query = "SELECT " + COLUMN_END_TIME + " FROM " + TABLE_DAY[day] + " WHERE 1";
        SQLiteDatabase db    = getWritableDatabase();
        Cursor         c     = db.rawQuery(query, null);

        Date     endTime;
        Calendar calendarEndTime = Calendar.getInstance();

        if(c != null && c.getCount() != 0){
            c.moveToLast();

            try{

                endTime = sdfEndTime.parse(c.getString(c.getColumnIndex(COLUMN_END_TIME)));
                calendarEndTime.setTime(endTime);

            } catch (ParseException e) {

                Log.e("SQL", "Caught parse exception setting end times");
            }

            calendarEndTime.add(Calendar.MINUTE, length);
            endTime = calendarEndTime.getTime();

            values.put(COLUMN_END_TIME, sdfEndTime.format(endTime));

            c.close();
        } else {

            endTime = data.getStartTime();
            calendarEndTime.setTime(endTime);
            calendarEndTime.add(Calendar.MINUTE, length);
            Log.i("SQl", calendarEndTime.getTime().toString());
            endTime = calendarEndTime.getTime();

            values.put(COLUMN_END_TIME, sdfEndTime.format(endTime));
        }


        Log.i("SQL", "Add entry to sessions table");
        db.insert(TABLE_DAY[day], null, values);
        db.close();

    }

    void editDay (int day, List <String> names, List <Integer> length, List <String> locations){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DAY[day], null, null);

        ContentValues values = new ContentValues();

        String query = "SELECT * FROM " + TABLE_DAY[day] + " WHERE 1";
        for(int i = 0; i < names.size(); i++){
            values.clear();
            values.put(COLUMN_NAME, names.get(i));
            values.put(COLUMN_LENGTH, length.get(i));
            values.put(COLUMN_LOC_DESC,
                       locations.get(i).equals("No location set!") ? null : locations.get(i));

            Calendar calendarEndTime = Calendar.getInstance();
            Cursor   c               = db.rawQuery(query, null);

            if(c != null && c.getCount() != 0){
                c.moveToLast();

                try{
                    calendarEndTime.setTime(sdfEndTime.parse(c.getString(c.getColumnIndex(COLUMN_END_TIME))));
                } catch (ParseException e) {
                    Log.e("SQL", "Caught parse exception setting end times");
                }

                calendarEndTime.add(Calendar.MINUTE, length.get(i));
                values.put(COLUMN_END_TIME, sdfEndTime.format(calendarEndTime.getTime()));
                c.close();
            } else {
                calendarEndTime.setTime(data.getStartTime());
                calendarEndTime.add(Calendar.MINUTE, length.get(i));
                Log.i("SQl", calendarEndTime.getTime().toString());
                values.put(COLUMN_END_TIME, sdfEndTime.format(calendarEndTime.getTime()));
            }

            db.insert(TABLE_DAY[day], null, values);

        }

        db.close();

    }

    /*
        public void editSession (int day, int number, String name, int length, String location){
            String         query  = "SELECT " + COLUMN_SESSION_NUM + " FROM " + TABLE_DAY[day] + " WHERE 1";
            SQLiteDatabase db     = getWritableDatabase();
            Cursor         c      = db.rawQuery(query, null);
            ContentValues  values = new ContentValues();

            values.put(COLUMN_NAME, name);
            values.put(COLUMN_LENGTH, length);
            values.put(COLUMN_LOC_DESC, location);

            c.move(number + 1);
            number = c.getInt(c.getColumnIndex(COLUMN_SESSION_NUM));

            c.close();
            db.update(TABLE_DAY[day], values, COLUMN_SESSION_NUM + " = " + number, null);

            db.close();

        }*/
    //get Session name
    private List <Integer> getSessionIDs (int day){
        List <Integer> ids   = new ArrayList <>();
        String         query = "SELECT " + COLUMN_SESSION_NUM + " FROM " + TABLE_DAY[day] + " WHERE 1";
        SQLiteDatabase db    = getReadableDatabase();
        Cursor         c     = db.rawQuery(query, null);

        c.moveToFirst();
        while(!c.isAfterLast()){
            ids.add(c.getInt(c.getColumnIndex(COLUMN_SESSION_NUM)));
            c.moveToNext();
        }
        c.close();
        db.close();

        return ids;
    }


    public String getSessionName (int day, int session){

        String         name;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_DAY[day] +
                " WHERE 1";


        Cursor c = db.rawQuery(query, null);

        if(c != null && c.getCount() != 0){
            c.moveToPosition(session);
            name = c.getString(c.getColumnIndex(COLUMN_NAME));
            c.close();

        } else {
            name = "";
            Log.i("SQL", "Cursor is Null");
        }

        db.close();

        return name;
    }


    public List <String> getSessionNames (int day){

        List <String> names = new ArrayList <>();

        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_DAY[day] + " WHERE 1";


        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null){
            if(c.getCount() != 0){
                c.moveToFirst();

                while(!c.isAfterLast()){

                    names.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
                    c.moveToNext();
                }

                c.close();
            }
        }

        db.close();

        return names;
    }


    public List <Integer> getSessionLength (int day){

        List <Integer> names = new ArrayList <>();

        String query = "SELECT " + COLUMN_LENGTH + " FROM " + TABLE_DAY[day] + " WHERE 1";


        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        c.moveToFirst();

        while(!c.isAfterLast()){

            names.add(c.getInt(c.getColumnIndex(COLUMN_LENGTH)));
            c.moveToNext();
        }

        c.close();

        db.close();

        return names;
    }


    public int getSessionNumOnDay (int day){

        int sessions;

        SQLiteDatabase db    = getReadableDatabase();
        String         query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_DAY[day] + " WHERE 1";

        Cursor cursor = db.rawQuery(query, null);

        sessions = cursor.getCount();

        cursor.close();
        db.close();

        return sessions;
    }


    public void clearSessionsTables (int day){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_DAY[day], "1", null);

        db.close();

    }


    public Date getSessionEnd (int day, int sessionID){

        Date           endTime  = new Date();
        Calendar       calendar = Calendar.getInstance();
        String         query    = "SELECT " + COLUMN_END_TIME + " FROM " + TABLE_DAY[day] + " WHERE 1;";
        SQLiteDatabase db       = getReadableDatabase();
        Cursor         c        = db.rawQuery(query, null);


        if(c != null && c.getCount() != 0){
            c.moveToPosition(sessionID);
            try{

                endTime = sdfEndTime.parse(c.getString(c.getColumnIndex(COLUMN_END_TIME)));
            } catch (ParseException ignored) {

            }

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sdfHour.format(endTime)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(sdfMinute.format(endTime)));
            calendar.set(Calendar.SECOND, 0);
            endTime = calendar.getTime();
            c.close();
        } else {

            Log.i("SQL", "Cursor is empty from getSessionEnd");
        }


        return endTime;
    }

    public List <Date> getSessionsEnd (int day){
        List <Date> ends = new ArrayList <>();

        Calendar       c      = Calendar.getInstance();
        String         query  = "SELECT " + COLUMN_END_TIME + " FROM " + TABLE_DAY[day] + " WHERE 1";
        SQLiteDatabase db     = getReadableDatabase();
        Cursor         cursor = db.rawQuery(query, null);

        if(cursor != null){

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                if(cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)) != null){
                    Log.i("SQL",
                          cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)) + " " + ends.size());

                    try{
                        ends.add(sdfEndTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME))));
                    } catch (ParseException ignored) {

                    }
                    cursor.moveToNext();
                } else {
                    ends.add(c.getTime());
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        db.close();
        return ends;
    }

    List <String> getLocations (int day){

        List <String> locations = new ArrayList <>();
        String        query     = "SELECT " + COLUMN_LOC_DESC + " FROM " + TABLE_DAY[day] + " WHERE 1";

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null){
            c.moveToFirst();
            while(!c.isAfterLast()){
                if(c.getString(c.getColumnIndex(COLUMN_LOC_DESC)) != null){

                    locations.add(c.getString(c.getColumnIndex(COLUMN_LOC_DESC)));
                    c.moveToNext();
                } else {
                    locations.add("No location set!");
                    c.moveToNext();
                }
            }

            c.close();
        }

        db.close();
        return locations;
    }

    public void updateEndTimes (int day){

        List <Integer> ids    = getSessionIDs(day);
        List <Integer> length = getSessionLength(day);

        ContentValues    values = new ContentValues();
        Calendar         c      = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("kk:mm", Locale.US);

        try{
            c.setTime(sdf.parse(pref.getString("starTime", "7:00")));
        } catch (ParseException ignored) {

        }

        SQLiteDatabase db = getWritableDatabase();
        String         endTime;
        for(int i = 0; i < ids.size(); i++){

            c.add(Calendar.MINUTE, length.get(i));
            endTime = sdfEndTime.format(c.getTime());

            values.put(COLUMN_END_TIME, endTime);
            db.update(TABLE_DAY[day], values, COLUMN_SESSION_NUM + "=" + ids.get(i), null);
        }
        db.close();
    }

//tasks table methods


    int addTask (String subject, String title, String desc, Date setDate, Date dueDate){

        String setString = df.format(setDate);
        String dueString = df.format(dueDate);


        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESC, desc);
        values.put(COLUMN_SET_DATE, setString);
        values.put(COLUMN_DUE_DATE, dueString);


        SQLiteDatabase db = getWritableDatabase();
        db.insert(subject, null, values);

        db.close();
        Log.i("SQL", "Added Entry to tasks table");

        int id;

        SQLiteDatabase db2   = getReadableDatabase();
        String         query = "SELECT " + COLUMN_ID + " FROM " + subject + " WHERE 1";

        Cursor c = db2.rawQuery(query, null);

        if(c != null && c.getCount() != 0){

            c.moveToLast();
            id = c.getInt(c.getColumnIndex(COLUMN_ID));
            c.close();

        } else {

            id = 0;

        }

        db2.close();

        return id;
    }


    void delTask (String subject, int id){

        SQLiteDatabase db    = getWritableDatabase();
        String         query = COLUMN_ID + "=" + id;
        db.delete(subject, query, null);
        db.delete(TABLE_REMINDERS, COLUMN_TASK_ID + "=" + id, null);
    }


    public List <Integer> getIDs (String subject){

        List <Integer> IDs = new ArrayList <>();

        String query = "SELECT " + COLUMN_ID + " FROM " + subject + " WHERE 1";

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null){

            c.moveToFirst();

            for(int i = 0; i < c.getCount(); i++){

                IDs.add(c.getInt(c.getColumnIndex(COLUMN_ID)));
                c.moveToNext();

            }
            c.close();

        } else {

            IDs.add(0);

            Log.i("SQL", "The cursor is empty");

        }

        db.close();
        return IDs;
    }


    public List <String> getTaskTitles (String subject){

        List <String> titles = new ArrayList <>();

        String         query = "SELECT " + COLUMN_TITLE + " FROM " + subject + " WHERE 1";
        SQLiteDatabase db    = getReadableDatabase();

        Cursor c = db.rawQuery(query, null);

        if(c != null){
            c.moveToFirst();

            for(int i = 0; !c.isAfterLast() && i < c.getCount(); i++){

                titles.add(c.getString(c.getColumnIndex(COLUMN_TITLE)));
                c.moveToNext();

            }
            c.close();
            return titles;
        } else {
            Log.i("SQL", "Tasks selection is empty");
            db.close();
            return null;
        }


    }


    public List <String> getTaskDescs (String subject){

        List <String> desc = new ArrayList <>();

        String query = "SELECT " + COLUMN_DESC + " FROM " + subject + " WHERE 1;";

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(query, null);

        if(c != null){

            c.moveToFirst();
            for(int i = 0; i < c.getCount(); i++){
                desc.add(c.getString(c.getColumnIndex(COLUMN_DESC)));
                c.moveToNext();
            }
            c.close();
        } else {

            desc.add("empty1");
            Log.i("SQL", "Cursor at desc empty.");
        }

        db.close();
        return desc;
    }


    public List <Date> getDueDate (String subject){

        List <Date> dueDate = new ArrayList <>();

        String         query = "SELECT " + COLUMN_DUE_DATE + " FROM " + subject + " WHERE 1;";
        SQLiteDatabase db    = getReadableDatabase();
        Cursor         c     = db.rawQuery(query, null);

        if(c != null){

            c.moveToFirst();
            for(int i = 0; i < c.getCount(); i++){
                try{

                    dueDate.add(df.parse(c.getString(c.getColumnIndex(COLUMN_DUE_DATE))));

                } catch (ParseException e) {

                    Log.e("SQL", "Caught parse exception while getting deuDate.");

                }
            }
            c.close();

        } else {

            try{
                dueDate.add(df.parse("01-01-2000"));
            } catch (ParseException e) {
                Log.e("SQL", "Caught parse exception while getting deuDate.");
            }
            Log.i("SQL", "Cursor is null at dueDate");
        }

        return dueDate;
    }


    public String getTitle (String subject, int id){

        String title;

        String query = "SELECT " + COLUMN_TITLE + " FROM " + subject + " WHERE " + COLUMN_ID + "=" +
                id + "";

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null && c.getCount() != 0){

            c.moveToFirst();
            title = c.getString(c.getColumnIndex(COLUMN_TITLE));
            c.close();

        } else {
            Log.i("SQL", "ids cursor empty");
            title = "empty";
        }

        db.close();

        return title;
    }


    String getDesc (String subject, int id){

        String title = " ";

        String query = "SELECT " + COLUMN_DESC + " FROM " + subject + " WHERE " + COLUMN_ID + "=" + id;

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null){

            c.moveToFirst();
            title = c.getString(c.getColumnIndex(COLUMN_DESC));
            c.close();

        }

        db.close();

        return title;
    }


    Date getSetDate (String subject, int id){

        Date title = new Date();

        String query = "SELECT " + COLUMN_SET_DATE + " FROM " + subject + " WHERE " + COLUMN_ID + "=\"" +
                id + "\"";

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null){

            c.moveToFirst();

            try{

                title = df.parse(c.getString(c.getColumnIndex(COLUMN_SET_DATE)));

            } catch (ParseException e) {

                Log.e("SQL", "Caught a parse exception getting setDate");

            }
            c.close();
        }

        db.close();

        return title;
    }


    Date getDueDate (String subject, int id){

        Date title = new Date();

        String query = "SELECT " + COLUMN_DUE_DATE + " FROM " + subject + " WHERE " + COLUMN_ID + "=\"" +
                id + "\"";

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null){

            c.moveToFirst();

            try{

                title = df.parse(c.getString(c.getColumnIndex(COLUMN_DUE_DATE)));

            } catch (ParseException e) {

                Log.e("SQL", "Caught a parse exception getting setDate");

            }
            c.close();
        }

        db.close();

        return title;
    }


    //reminders
    void addReminder (String subject, int taskID, Date time){

        ContentValues  values = new ContentValues();
        SQLiteDatabase db     = getWritableDatabase();

        String rTime = sdfReminder.format(time);

        values.put(COLUMN_SUBJECT, subject);
        values.put(COLUMN_TASK_ID, taskID);
        values.put(COLUMN_REMINDER_TIME, rTime);

        db.insert(TABLE_REMINDERS, null, values);
        db.close();

    }

    public int[] getAllReminderIDs (){

        int[] ids;

        String query = "SELECT " + COLUMN_REMINDER_ID + " FROM " + TABLE_REMINDERS + " WHERE 1";

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        ids = new int[c.getCount()];
        c.moveToFirst();

        Log.i("alarms", "" + c.getCount());

        for(int i = 0; i < c.getCount(); i++){

            int id = c.getInt(c.getColumnIndex(COLUMN_REMINDER_ID));
            ids[i] = id;
        }

        db.close();
        c.close();

        return ids;
    }

    public int getTaskIDReminder (int reminderID){

        int id;
        String query = "SELECT " + COLUMN_TASK_ID + " FROM " + TABLE_REMINDERS + " WHERE " +
                COLUMN_REMINDER_ID + " = " + reminderID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);
        if(c != null && c.getCount() != 0){

            c.moveToFirst();
            id = c.getInt(c.getColumnIndex(COLUMN_TASK_ID));
            c.close();

        } else {
            id = 0;
        }

        db.close();

        return id;
    }

    public String getSubjectNameReminder (int reminderID){

        String subject;
        String query = "SELECT " + COLUMN_SUBJECT + " FROM " + TABLE_REMINDERS + " WHERE " +
                COLUMN_REMINDER_ID + " = " + reminderID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c.getCount() != 0){
            c.moveToFirst();
            subject = c.getString(c.getColumnIndex(COLUMN_SUBJECT));

            c.close();
            db.close();
            return subject;
        } else {
            return null;
        }
    }

    Date[] getReminders (String subject, int taskID){

        Date[] reminders;

        String query = "SELECT " + COLUMN_REMINDER_TIME + " FROM " + TABLE_REMINDERS + " WHERE " +
                COLUMN_TASK_ID + " = " + taskID + " AND " + COLUMN_SUBJECT + " = \"" + subject + "\"";
        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        if(c != null && c.getCount() != 0){

            reminders = new Date[c.getCount()];

            c.moveToFirst();

            for(int i = 0; i < c.getCount(); i++){

                try{
                    reminders[i] = sdfReminder.parse(c.getString(c.getColumnIndex(COLUMN_REMINDER_TIME)));
                } catch (ParseException e) {

                    Log.e("SQL", "caught parse exception at get reminder " + i);

                }

                c.moveToNext();

            }

            c.close();
            db.close();

            return reminders;

        } else {
            db.close();
            return null;
        }
    }

    public Date getReminder (int reminderID){

        Date date;
        date = Calendar.getInstance().getTime();

        String query = "SELECT " + COLUMN_REMINDER_TIME + " FROM " + TABLE_REMINDERS + " WHERE " +
                COLUMN_REMINDER_ID + " = " + reminderID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        c.moveToFirst();
        try{
            date = sdfReminder.parse(c.getString(c.getColumnIndex(COLUMN_REMINDER_TIME)));
        } catch (ParseException e) {

        }

        c.close();
        return date;
    }

    void editTask (int taskID, String subject, String title, String desc, Date setDate, Date dueDate){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, taskID);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESC, desc);
        values.put(COLUMN_SET_DATE, df.format(setDate));
        values.put(COLUMN_DUE_DATE, df.format(dueDate));

        db.replace(subject, null, values);

        int i = db.delete(TABLE_REMINDERS, COLUMN_TASK_ID + " = " + taskID, null);
        db.close();

        Log.i("SQL", "" + i);
    }

    public boolean isRemDone (int remID){

        boolean checker;

        String query = "SELECT " + COLUMN_REM_CHECKER + " FROM " + TABLE_REMINDERS + " WHERE " +
                COLUMN_REMINDER_ID + "=" + remID;
        SQLiteDatabase db = getReadableDatabase();
        Cursor         c  = db.rawQuery(query, null);

        c.moveToFirst();
        if(c.isNull(c.getColumnIndex(COLUMN_REM_CHECKER))){

            int check = c.getInt(c.getColumnIndex(COLUMN_REM_CHECKER));
            checker = check != 0;
            c.close();

        } else {
            checker = true;
        }
        return checker;
    }

    public void remDone (int remID){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REM_CHECKER, 1);
        db.update(TABLE_REMINDERS, values, COLUMN_REMINDER_ID + " = " + remID, null);
    }
}

