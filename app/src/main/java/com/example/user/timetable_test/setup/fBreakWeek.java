package com.example.user.timetable_test.setup;

import android.content.Context;
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


public class fBreakWeek extends Fragment{

    MiscData data = MiscData.getInstance();

    breaks activityCommander;

    private boolean done = false;


    public interface breaks{

        void breaksNext();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_break_days, container, false);

        LinearLayout baseLayout = (LinearLayout) v.findViewById(R.id.baseLayoutBreak);

        Button next = (Button) v.findViewById(R.id.setBreakNum);

        final RelativeLayout subGroup[] = new RelativeLayout[data.getDays() + 1];
        final TextView       day[]      = new TextView[data.getDays()];
        final EditText       num[]      = new EditText[data.getDays()];

        final TextView averageLengthtext = new TextView(getContext());
        final EditText averageLength     = new EditText(getContext());

        Resources r = getResources();

        int dayWidth = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
        int textWidth = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

        LinearLayout.LayoutParams group = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams text = new RelativeLayout.LayoutParams(dayWidth,
                                                                           RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams input = new RelativeLayout.LayoutParams(textWidth,
                                                                            RelativeLayout.LayoutParams.MATCH_PARENT);

        text.addRule(RelativeLayout.ALIGN_PARENT_START);
        text.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        input.addRule(RelativeLayout.ALIGN_PARENT_END);

        for(int i = 0; i < data.getDays(); i++){
            subGroup[i] = new RelativeLayout(getContext());
            day[i] = new TextView(getContext());
            num[i] = new EditText(getContext());

            num[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            day[i].setTextSize(22);

            baseLayout.addView(subGroup[i], group);
            subGroup[i].addView(day[i], text);
            subGroup[i].addView(num[i], input);

            day[i].setText(data.getDay(i));

        }

        subGroup[data.getDays()] = new RelativeLayout(getContext());
        baseLayout.addView(subGroup[data.getDays()]);
        subGroup[data.getDays()].addView(averageLengthtext, text);
        subGroup[data.getDays()].addView(averageLength, input);

        averageLengthtext.setText("On average, how long is the break?");
        averageLengthtext.setTextSize(22);
        averageLength.setInputType(InputType.TYPE_CLASS_NUMBER);

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                for(int i = 0; i < data.getDays(); i++){
                    String tempChecker = num[i].getText().toString();

                    if(!tempChecker.isEmpty()){

                        data.setBreaks(i, Integer.parseInt(num[i].getText().toString()));

                    } else{

                        Toast.makeText(getContext(), "You forgot something!", Toast.LENGTH_SHORT)
                             .show();
                        break;
                    }

                    if(i == (data.getDays() - 1)){
                        done = true;

                    }

                }

                if(!averageLength.getText().toString().isEmpty()){

                    data.setBreakLength(Integer.parseInt(averageLength.getText().toString()));

                } else{
                    Toast.makeText(getContext(), "You forgot something!", Toast.LENGTH_SHORT)
                         .show();
                    done = false;
                }

                if(done){

                    activityCommander.breaksNext();
                }

            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context){

        super.onAttach(context);

        try {
            activityCommander = (breaks) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString());

        }
    }


    public String day(int dayInt){

        String day;

        switch(dayInt){
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
            case 7:
                day = "Saturday";
                break;
            default:
                return null;

        }

        return day;
    }

}
