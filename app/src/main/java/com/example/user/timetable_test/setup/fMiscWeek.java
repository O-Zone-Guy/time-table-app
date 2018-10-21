package com.example.user.timetable_test.setup;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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


public class fMiscWeek extends Fragment{

    private boolean done = false;

    MiscData data = MiscData.getInstance();

    miscWeek activityCommander;


    public interface miscWeek{

        void miscNext();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_misc_week, container, false);

        LinearLayout view = (LinearLayout) v.findViewById(R.id.miscLinearView);
        Button       next = (Button) v.findViewById(R.id.miscWeekButt);

        final RelativeLayout subGroup[] = new RelativeLayout[data.getDays() + 1];
        final TextView       days[]     = new TextView[data.getDays()];
        final EditText       miscNum[]  = new EditText[data.getDays()];

        final TextView averageLengthText = new TextView(getContext());
        final EditText averagelength     = new EditText(getContext());

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

        LinearLayout.LayoutParams group2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        group2.setMargins((int) getResources().getDimension(R.dimen.activity_vertical_margin), 20,
                          (int) getResources().getDimension(R.dimen.activity_vertical_margin), 0);

        group.setMargins((int) getResources().getDimension(R.dimen.activity_vertical_margin), 0,
                         (int) getResources().getDimension(R.dimen.activity_vertical_margin), 0);

        text.addRule(RelativeLayout.ALIGN_PARENT_START);
        text.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        input.addRule(RelativeLayout.ALIGN_PARENT_END);


        for(int i = 0; i < data.getDays(); i++){

            subGroup[i] = new RelativeLayout(getContext());
            days[i] = new TextView(getContext());
            miscNum[i] = new EditText(getContext());

            miscNum[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            days[i].setTextSize(22);

            view.addView(subGroup[i], group);
            subGroup[i].addView(days[i], text);
            subGroup[i].addView(miscNum[i], input);

            days[i].setText(data.getDay(i));

        }

        subGroup[data.getDays()] = new RelativeLayout(getContext());
        view.addView(subGroup[data.getDays()], group);
        subGroup[data.getDays()].addView(averageLengthText, text);
        subGroup[data.getDays()].addView(averagelength, input);

        averageLengthText.setText("On average, how long are misc sessions?");
        averageLengthText.setTextSize(22);

        averagelength.setInputType(InputType.TYPE_CLASS_NUMBER);

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                for(int i = 0; i < data.getDays(); i++){
                    String tempChecker = miscNum[i].getText().toString();

                    if(!tempChecker.isEmpty()){

                        data.setMisc(i, Integer.parseInt(miscNum[i].getText().toString()));

                    } else{

                        Toast.makeText(getContext(), "You forgot something!", Toast.LENGTH_SHORT)
                             .show();
                        break;
                    }

                    if(i == (data.getDays() - 1)){
                        done = true;

                    }

                }

                if(!averagelength.getText().toString().isEmpty()){

                    data.setMiscLength(Integer.parseInt(averagelength.getText().toString()));

                } else{
                    Toast.makeText(getContext(), "You forgot something!", Toast.LENGTH_SHORT)
                         .show();
                    done = false;
                }

                if(done){

                    data.initNamesArray();
                    activityCommander.miscNext();
                }
            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context){

        super.onAttach(context);

        try {
            activityCommander = (miscWeek) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }
    }


    public String day(int dayInt){

        String day;

        dayInt = dayInt % 7;

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
            case 0:
                day = "Saturday";
                break;
            default:
                return null;

        }

        return day;
    }
}
