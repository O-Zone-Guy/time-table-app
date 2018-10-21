package com.example.user.timetable_test.setup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.timetable_test.R;


public class frag_welcome extends Fragment{

    welcomeListener activityCommander;


    public interface welcomeListener{

        void firstSetUpFrag();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.frag_set_up_welcome, container, false);


        Button next = (Button) view.findViewById(R.id.startSetUp);

        next.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                activityCommander.firstSetUpFrag();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context){

        super.onAttach(context);

        try {
            activityCommander = (welcomeListener) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }
    }

}
