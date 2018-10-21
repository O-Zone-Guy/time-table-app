package com.example.user.timetable_test.setup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.timetable_test.R;


public class fragNumSubj extends Fragment{

    numSubj activityCommander;


    public interface numSubj{

        void setSubjNum(int subjNum);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_num_subj, container, false);

        Button         setSubjNum = (Button) v.findViewById(R.id.setSubjNum);
        final EditText subjNum    = (EditText) v.findViewById(R.id.numSubj);


        setSubjNum.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                activityCommander.setSubjNum(Integer.parseInt(subjNum.getText().toString()));

            }

        });

        return v;
    }


    @Override
    public void onAttach(Context context){

        super.onAttach(context);

        try {
            activityCommander = (numSubj) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }
    }
}
