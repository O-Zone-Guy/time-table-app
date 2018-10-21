package com.example.user.timetable_test.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;


public class fLeesonsPerDay extends Fragment{

    MiscData                 data   = MiscData.getInstance();

    lessons activityCommander;

    private boolean done = false;


    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_sessions_day, container, false);

        LinearLayout baseLayout = (LinearLayout) v.findViewById(R.id.baseLayout);

        final Button next = (Button) v.findViewById(R.id.setBreakNum);

        final TextView       dayName[]  = new TextView[data.getDays()];
        final RelativeLayout subGroup[] = new RelativeLayout[data.getDays() + 1];
        final EditText       num[]      = new EditText[data.getDays()];

        final TextView averageLengthText = new TextView(getContext());
        final EditText averageLength     = new EditText(getContext());

        Resources r = getResources();

        int dayWidth =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
        int textWidth =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

        LinearLayout.LayoutParams group =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams text =
                new RelativeLayout.LayoutParams(dayWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams input =
                new RelativeLayout.LayoutParams(textWidth, RelativeLayout.LayoutParams.MATCH_PARENT);

        text.addRule(RelativeLayout.ALIGN_PARENT_START);
        text.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        input.addRule(RelativeLayout.ALIGN_PARENT_END);

        for(int i = 0; i < data.getDays(); i++){

            subGroup[i] = new RelativeLayout(getContext());
            dayName[i] = new TextView(getContext());
            num[i] = new EditText(getContext());

            num[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            dayName[i].setTextSize(22);

            baseLayout.addView(subGroup[i], group);
            subGroup[i].addView(dayName[i], text);
            subGroup[i].addView(num[i], input);

            dayName[i].setText(data.getDay(i));

        }
        subGroup[data.getDays()] = new RelativeLayout(getContext());
        baseLayout.addView(subGroup[data.getDays()]);
        subGroup[data.getDays()].addView(averageLengthText, text);
        averageLengthText.setTextSize(22);
        averageLengthText.setText("On average, how long is each lesson?");

        subGroup[data.getDays()].addView(averageLength, input);
        averageLength.setInputType(InputType.TYPE_CLASS_NUMBER);

        next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View view){

                for(int i = 0; i < data.getDays(); i++){

                    if(!num[i].getText().toString().isEmpty()){

                        data.setLessons(i, Integer.parseInt(num[i].getText().toString()));

                    } else {

                        Toast.makeText(getContext(), "You missed something!", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    if(i == (data.getDays() - 1)){
                        done = true;

                    }

                }

                if(!averageLength.getText().toString().isEmpty()){

                    data.setLessonLength(Integer.parseInt(averageLength.getText().toString()));

                } else {

                    Toast.makeText(getContext(), "You missed something!", Toast.LENGTH_SHORT).show();
                    done = false;
                }

                if(done){
                    activityCommander.lessons();
                }

            }
        });


        return v;
    }


    @Override
    public void onAttach (Context context){

        super.onAttach(context);

        try{
            activityCommander = (lessons) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }

    }


    public String day (int dayInt){

        String day;

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
                return null;

        }

        return day;
    }


    public interface lessons{

        void lessons ();

    }


}
