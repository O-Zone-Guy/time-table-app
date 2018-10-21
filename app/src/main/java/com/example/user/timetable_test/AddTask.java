package com.example.user.timetable_test;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.user.timetable_test.alarms.RemindersClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddTask extends AppCompatActivity{


    int Year, Month, Day, Hour, Min;
    TextView setDate, dueDate;
    Button setDateBut, dueDateBut, addReminder, saveTask;
    Date setDatedate, dueDatedate;
    int remCount = 0;
    int year, month, day, hour, min, taskID;
    String  intentString;
    boolean edit;
    String  subject;
    List <Button>         remRem    = new ArrayList <>();
    List <TextView>       remDate   = new ArrayList <>();
    List <Button>         setRem    = new ArrayList <>();
    List <RelativeLayout> remLayout = new ArrayList <>();
    TableDBHandler dbHandler;
    SimpleDateFormat sdf         = new SimpleDateFormat("EEEE\ndd/MM/yy", Locale.US);
    SimpleDateFormat sdfReminder = new SimpleDateFormat("hh:mm a  dd/MM/yy", Locale.US);
    MiscData         data        = MiscData.getInstance();
    private LinearLayout baseView;
    private EditText     titleText, desc;
    private Spinner spinner;


    @Override
    public void onBackPressed (){

        switch (intentString) {
            case "timetable":
                startActivity(new Intent(this, timeTable.class));
                break;
            case "tasks":
                startActivity(new Intent(this, ViewTasks.class));
                break;
            case "viewTask":
                Intent taskDetail = new Intent(getApplicationContext(), ViewTaskDetails.class);
                taskDetail.putExtra("ids", taskID);
                taskDetail.putExtra("subject", subject);
                startActivity(taskDetail);
                finish();
                break;
        }
    }


    @Override
    protected void onCreate (Bundle savedInstanceState){
        dbHandler = new TableDBHandler(this, null);
        Intent intent = getIntent();

        intentString = intent.getStringExtra("lc");

        edit = intent.getBooleanExtra("edit", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        spinner = (Spinner) findViewById(R.id.subjectTaskSpinner);
        titleText = (EditText) findViewById(R.id.taskTitle);
        desc = (EditText) findViewById(R.id.taskDesc);
        setDate = (TextView) findViewById(R.id.setDate);
        dueDate = (TextView) findViewById(R.id.dueDate);
        setDateBut = (Button) findViewById(R.id.setDateBut);
        dueDateBut = (Button) findViewById(R.id.dueDateBut);
        addReminder = (Button) findViewById(R.id.addReminders);
        saveTask = (Button) findViewById(R.id.saveTask);
        baseView = (LinearLayout) findViewById(R.id.addTaskView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        final String subjects[] = new String[data.getSubjects().size()];
        data.getSubjects().toArray(subjects);

        final Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        ArrayAdapter spinnerAdaptor =
                new ArrayAdapter <>(this, android.R.layout.simple_spinner_item, subjects);
        spinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdaptor);

        Resources r = getResources();
        int dip1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());

        final RelativeLayout.LayoutParams text =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
        text.addRule(RelativeLayout.ALIGN_PARENT_START);
        text.addRule(RelativeLayout.CENTER_VERTICAL);
        text.setMargins(dip1 * 20, 0, 0, 0);

        final RelativeLayout.LayoutParams setBut =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT);

        final RelativeLayout.LayoutParams delBut =
                new RelativeLayout.LayoutParams(dip1 * 30, ViewGroup.LayoutParams.WRAP_CONTENT);
        delBut.setMarginEnd(dip1 * 20);
        delBut.addRule(RelativeLayout.ALIGN_PARENT_END);
        delBut.addRule(RelativeLayout.CENTER_VERTICAL);


        if(edit){

            taskID = intent.getIntExtra("ids", 0);
            subject = intent.getStringExtra("subject");

            titleText.setText(dbHandler.getTitle(subject, taskID));
            desc.setText(dbHandler.getDesc(subject, taskID));

            setDate.setText(sdf.format(dbHandler.getSetDate(subject, taskID)));
            dueDate.setText(sdf.format(dbHandler.getDueDate(subject, taskID)));

            if(dbHandler.getReminders(subject, taskID) != null){

                remCount = dbHandler.getReminders(subject, taskID).length;
            } else {
                remCount = 0;
            }

            spinner.setSelection(data.getSubjects().indexOf(subject));
            spinner.setEnabled(false);

            for(int id = 0; id < dbHandler.getAllReminderIDs().length; id++){

                remLayout.add(new RelativeLayout(getApplicationContext()));
                remDate.add(new TextView(getApplicationContext()));
                remRem.add(new Button(getApplicationContext()));
                setRem.add(new Button(getApplicationContext()));

                baseView.addView(remLayout.get(id));
                remLayout.get(id).addView(setRem.get(id), setBut);
                remLayout.get(id).addView(remDate.get(id), text);
                remLayout.get(id).addView(remRem.get(id), delBut);

                remDate.get(id)
                       .setText(sdfReminder.format(dbHandler.getReminder(dbHandler.getAllReminderIDs()[id])));
                remDate.get(id).setTextColor(Color.BLACK);
                remDate.get(id).setTextSize(20);

                setRem.get(id).setAlpha(0);

                remRem.get(id).setBackground(getDrawable(R.drawable.ripple_remove));
                remRem.get(id).setBackgroundTintList(
                        getApplicationContext().getColorStateList(android.R.color.holo_red_light));
                remRem.get(id).setForeground(getDrawable(R.drawable.ripple_add));

                final int Int = id;

                remRem.get(id).setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick (View v){

                        remLayout.get(Int).removeAllViews();
                        baseView.removeView(remLayout.get(Int));
                        remDate.get(Int).setText(null);

                    }
                });

                setRem.get(id).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick (View v){

                        setReminderTime(remDate.get(Int));

                    }
                });

                saveTask.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick (View v){
                        editTask();
                    }
                });

            }


        } else {
            //do everything normally
            setDate.setText(sdf.format(c.getTime()));
            dueDate.setTextKeepState(sdf.format(c.getTime()));

            saveTask.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){

                    savetask();
                    if(intentString.equals("timetable")){
                        startActivity(new Intent(getApplicationContext(), timeTable.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), ViewTasks.class));
                    }
                    finish();
                }
            });
        }

        setDateBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                DatePickerDialog.OnDateSetListener onDateSetListener =
                        new DatePickerDialog.OnDateSetListener(){
                            @Override
                            public void onDateSet (DatePicker datePicker, int i, int i1, int i2){
                                Calendar c = Calendar.getInstance();

                                c.set(Calendar.YEAR, i);
                                c.set(Calendar.MONTH, i1);
                                c.set(Calendar.DAY_OF_MONTH, i2);

                                setDate.setText(sdf.format(c.getTime()));
                            }
                        };

                new DatePickerDialog(AddTask.this, onDateSetListener, year, month, day).show();
            }
        });

        dueDateBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                DatePickerDialog.OnDateSetListener onDateSetListener =
                        new DatePickerDialog.OnDateSetListener(){
                            @Override
                            public void onDateSet (DatePicker datePicker, int i, int i1, int i2){
                                Calendar c = Calendar.getInstance();

                                c.set(Calendar.YEAR, i);
                                c.set(Calendar.MONTH, i1);
                                c.set(Calendar.DAY_OF_MONTH, i2);

                                dueDate.setText(sdf.format(c.getTime()));
                            }
                        };

                new DatePickerDialog(AddTask.this, onDateSetListener, year, month, day).show();
            }
        });

        addReminder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                remCount++;
                final int id = remCount - 1;

                remLayout.add(new RelativeLayout(getApplicationContext()));
                remDate.add(new TextView(getApplicationContext()));
                remRem.add(new Button(getApplicationContext()));
                setRem.add(new Button(getApplicationContext()));

                baseView.addView(remLayout.get(id));
                remLayout.get(id).addView(setRem.get(id), setBut);
                remLayout.get(id).addView(remDate.get(id), text);
                remLayout.get(id).addView(remRem.get(id), delBut);

                remDate.get(id).setText(sdfReminder.format(c.getTime()));
                remDate.get(id).setTextColor(Color.BLACK);
                remDate.get(id).setTextSize(20);

                setRem.get(id).setAlpha(0);

                remRem.get(id).setBackground(getDrawable(R.drawable.ic_remove_black_24dp));
                remRem.get(id).setBackgroundTintList(
                        getApplicationContext().getColorStateList(android.R.color.holo_red_light));

                remRem.get(id).setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick (View v){

                        remLayout.get(id).removeAllViews();
                        baseView.removeView(remLayout.get(id));
                        Log.i("SQL", "" + remCount);

                    }
                });

                setRem.get(id).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick (View v){

                        setReminderTime(remDate.get(id));

                    }
                });

            }
        });
    }

    void savetask (){

        try{
            setDatedate = sdf.parse(setDate.getText().toString());
            dueDatedate = sdf.parse(dueDate.getText().toString());
        } catch (ParseException e) {
            Log.e("SQL", "caught parse exception setting set and due dates");
        }

        taskID = dbHandler.addTask(spinner.getSelectedItem().toString(), titleText.getText().toString(),
                                   desc.getText().toString(), setDatedate, dueDatedate);

        for(int i = 0; i < remCount; i++){
            Date date = Calendar.getInstance().getTime();
            if(remDate.get(i) != null || remDate.get(i).getParent() != null){
                try{

                    date = sdfReminder.parse(remDate.get(i).getText().toString());
                } catch (ParseException e) {
                    Log.e("SQL", "caught parse exception parsing reminder " + i);
                }

                dbHandler.addReminder(spinner.getSelectedItem().toString(), taskID, date);
            }
        }

        sendBroadcast(new Intent(getApplicationContext(), RemindersClass.class));

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {

            case R.id.saveTaskMenu:

                if(edit){
                    editTask();
                } else {

                    savetask();
                    if(intentString.equals("timetable")){
                        startActivity(new Intent(getApplicationContext(), timeTable.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), ViewTasks.class));
                    }
                }
                finish();
                return true;

            case R.id.cancel:


                if(intentString.equals("timetable")){
                    startActivity(new Intent(this, timeTable.class));
                } else {
                    startActivity(new Intent(this, ViewTasks.class));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }


    public void setReminderTime (final TextView view){

        final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet (TimePicker timePicker, int i, int i1){

                Hour = i;
                Min = i1;

                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, Year);
                c.set(Calendar.MONTH, Month);
                c.set(Calendar.DAY_OF_MONTH, Day);
                c.set(Calendar.HOUR_OF_DAY, Hour);
                c.set(Calendar.MINUTE, Min);

                view.setText(sdfReminder.format(c.getTime()));

            }
        };


        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet (DatePicker datePicker, int i, int i1, int i2){

                Year = i;
                Month = i1;
                Day = i2;


                new TimePickerDialog(AddTask.this, onTimeSetListener, hour, min, false).show();
            }
        };

        new DatePickerDialog(AddTask.this, onDateSetListener, year, month, day).show();


    }


    void editTask (){

        try{
            setDatedate = sdf.parse(setDate.getText().toString());
            dueDatedate = sdf.parse(dueDate.getText().toString());
        } catch (ParseException e) {
            Log.e("SQL", "caught parse exception setting set and due dates");
        }

        dbHandler.editTask(taskID, spinner.getSelectedItem().toString(), titleText.getText().toString(),
                           desc.getText().toString(), setDatedate, dueDatedate);

        for(int i = 0; i < remCount; i++){
            Date date = Calendar.getInstance().getTime();
            if(!remDate.get(i).getText().toString().isEmpty()){
                try{

                    date = sdfReminder.parse(remDate.get(i).getText().toString());
                } catch (ParseException e) {
                    Log.e("SQL", "caught parse exception parsing reminder " + i);
                }

                dbHandler.addReminder(spinner.getSelectedItem().toString(), taskID, date);
            }
        }

        Intent viewtask = new Intent(getApplicationContext(), ViewTaskDetails.class);
        viewtask.putExtra("subject", spinner.getSelectedItem().toString());
        viewtask.putExtra("ids", taskID);

        sendBroadcast(new Intent(getApplicationContext(), RemindersClass.class));

        startActivity(viewtask);
        finish();
    }


}
