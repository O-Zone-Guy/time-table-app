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

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;

import static com.example.user.timetable_test.testFiles.SharedPreference.PREFERENCE;


public class subjNames extends Fragment{

    MiscData data = MiscData.getInstance();

    names activityCommander;


    private boolean done = false;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState){
        SharedPreferences pref =
                getContext().getSharedPreferences(PREFERENCE.get(), Context.MODE_PRIVATE);

        View v = inflater.inflate(R.layout.frag_subj_names, container, false);

        LinearLayout   layout    = (LinearLayout) v.findViewById(R.id.subjNamesView);
        final EditText subject[] = new EditText[data.getNoSubjects()];
        Button         setNames  = (Button) v.findViewById(R.id.setNames);

        final SharedPreferences.Editor editor = pref.edit();


/////////////////////set text box size///////////////////////
        Resources r      = getResources();
        int       width  =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
        int       margin =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());

        LinearLayout.LayoutParams text =
                new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        text.setMargins(margin / 2, margin, 0, 0);

///////////////////set subject names text input//////////////////////
        for(int i = 0; i < data.getNoSubjects(); i++){
            //layout.addView(spinner[i]);
            subject[i] = new EditText(getContext());
            subject[i].setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

            layout.addView(subject[i], text);

        }
//////////////////////click listener/////////////////////
        setNames.setOnClickListener(new View.OnClickListener(){

            public void onClick (View v){

                for(int i = 0; i < data.getNoSubjects(); i++){

                    if(!subject[i].getText().toString().isEmpty()){

                        if(!data.getSubjects().contains(subject[i].getText().toString())){
                            data.addSubject(subject[i].getText().toString());
                            String array[] = new String[data.getSubjects().size()];
                            data.getSubjects().toArray(array);
                            editor.putString("subjects", data.convertArrayToString(array));
                            editor.apply();

                        }

                    } else {

                        break;
                    }

                    if(i == (data.getNoSubjects() - 1)){
                        done = true;

                    }

                }

                if(done){

                    activityCommander.subjNamesNext();
                }


            }

        });

        return v;
    }

    @Override
    public void onAttach (Context context){

        super.onAttach(context);

        try{
            activityCommander = (names) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }
    }


    public interface names{

        void subjNamesNext ();

    }
}
