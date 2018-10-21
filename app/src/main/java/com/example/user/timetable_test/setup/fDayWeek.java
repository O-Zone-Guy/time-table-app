package com.example.user.timetable_test.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;

import static com.example.user.timetable_test.testFiles.SharedPreference.PREFERENCE;


public class fDayWeek extends Fragment{

    MiscData data = MiscData.getInstance();

    days activityCommander;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState){

        View           v         = inflater.inflate(R.layout.fragment_day_week, container, false);
        Button         next      = (Button) v.findViewById(R.id.day_week_next);
        final EditText numDays   = (EditText) v.findViewById(R.id.dayNum);
        final Spinner  startDay  = (Spinner) v.findViewById(R.id.startDay);
        final EditText startTime = (EditText) v.findViewById(R.id.start_time);

        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter <String> arrayAdapter =
                new ArrayAdapter <>(getContext(), android.R.layout.simple_spinner_item, days);
        startDay.setAdapter(arrayAdapter);

        SharedPreferences preferences =
                getContext().getSharedPreferences(PREFERENCE.get(), Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = preferences.edit();


        next.setOnClickListener(new View.OnClickListener(){

            public void onClick (View v){

                data.setDays(Integer.parseInt(numDays.getText().toString()));
                data.setFirstDay(getDay(startDay.getSelectedItem().toString()));
                data.setStartTime(startTime.getText().toString());


                editor.putString("startTime", startTime.getText().toString());
                editor.apply();

                activityCommander.daysNext();

            }


        });


        return v;
    }

    @Override
    public void onAttach (Context context){

        super.onAttach(context);

        try{
            activityCommander = (days) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }
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
                dayInt = 7;
                break;
            default:
                dayInt = 1;
                break;

        }

        return dayInt;
    }


    public interface days{

        void daysNext ();

    }

}
