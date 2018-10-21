package com.example.user.timetable_test.setup;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;
import com.example.user.timetable_test.timeTable;


public class SetTable extends AppCompatActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter mSectionsPagerAdapter;
    MiscData data = MiscData.getInstance();


    @Override
    protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_table);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        data.initDayDone();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        MiscData data = MiscData.getInstance();
        TableDBHandler tableDBHandler;


        public PlaceholderFragment (){

        }


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance (int sectionNumber){

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle              args     = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

            //misc
            data.initSessions();
            final int page = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
            tableDBHandler = new TableDBHandler(getContext(), null);


            //referencing other views/layouts
            View rootView = inflater.inflate(R.layout.fragment_set_table, container, false);
            final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            final LinearLayout baseView = (LinearLayout) rootView.findViewById(R.id.setTableBaseView);


            //showing day in Q.
            textView.setText(data.getDay(page));


            //declaring new Views
            final RelativeLayout lengthView[]      = new RelativeLayout[data.getSessions(page)];
            final Spinner        sessionSpinners[] = new Spinner[data.getSessions(page)];
            final TextView       text[]            = new TextView[data.getSessions(page)];
            final EditText       lengthText[]      = new EditText[data.getSessions(page)];
            final TextView       textLength[]      = new TextView[data.getSessions(page)];
            final Button         next              = new Button(getContext());
            final TextView       sectionDone       = new TextView(getContext());

            sectionDone.setText(R.string.section_done);
            sectionDone.setTextSize(22);
            sectionDone.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            //dip to px
            Resources r = getResources();

            int botMargin =
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());

            int lengthLength =
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());

            //setting adaptor for spinner
            final ArrayAdapter <String> sessionSpinnerArray =
                    new ArrayAdapter <>(getContext(), android.R.layout.simple_spinner_item,
                                        data.getNamesArray());
            sessionSpinnerArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            LinearLayout.LayoutParams relativeRules =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                  ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeRules.setMargins(0, 0, 0, botMargin);

            LinearLayout.LayoutParams sessionSpinnerRules =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT);

            RelativeLayout.LayoutParams spinner =
                    new RelativeLayout.LayoutParams(lengthLength, ViewGroup.LayoutParams.MATCH_PARENT);
            spinner.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            RelativeLayout.LayoutParams textRules =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT);
            textRules.addRule(RelativeLayout.ALIGN_PARENT_START);

            final RelativeLayout.LayoutParams doneRules =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if(!data.isDayDone(page)){
                //drawing views on screen
                for(int j = 0; j < data.getSessions(page); j++){

                    //session
                    sessionSpinners[j] = new Spinner(getContext());
                    text[j] = new TextView(getContext());
                    sessionSpinners[j].setGravity(Gravity.END);

                    //length
                    lengthView[j] = new RelativeLayout(getContext());
                    lengthText[j] = new EditText(getContext());
                    textLength[j] = new TextView(getContext());
                    lengthText[j].setInputType(InputType.TYPE_CLASS_NUMBER);

                    //set array adaptor
                    sessionSpinners[j].setAdapter(sessionSpinnerArray);

                    //set Text
                    text[j].setText(String.valueOf(j + 1));
                    textLength[j].setText(R.string.Length_min);

                    //adding views
                    baseView.addView(text[j]);
                    baseView.addView(sessionSpinners[j], sessionSpinnerRules);
                    baseView.addView(lengthView[j], relativeRules);
                    lengthView[j].addView(lengthText[j], spinner);
                    lengthView[j].addView(textLength[j], textRules);

                    final int temp = j;

                    sessionSpinners[j].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


                        @Override
                        public void onItemSelected (AdapterView <?> adapterView, View view, int i, long l){


                            if(sessionSpinners[temp].getSelectedItem().toString().equals("Break")){

                                lengthText[temp].setText(String.valueOf(data.getBreakLength()));

                            } else if(sessionSpinners[temp].getSelectedItem().toString().equals("Misc.")){
                                lengthText[temp].setText(String.valueOf(data.getMiscLength()));

                            } else {
                                lengthText[temp].setText(String.valueOf(data.getLessonLength()));
                            }


                        }


                        @Override
                        public void onNothingSelected (AdapterView <?> adapterView){

                        }
                    });

                }

                baseView.addView(next);
                next.setText(R.string.finish);

            } else {
                baseView.removeAllViews();
                baseView.addView(textView);
                baseView.addView(sectionDone, doneRules);

            }

            next.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View view){

                    //clearing tables
                    tableDBHandler.clearSessionsTables(page);

                    Log.i("SQL", "Clicked Button");
                    for(int i = 0; i < data.getSessions(page); i++){

                        //are text boxes full?
                        String tempChecker = lengthText[i].getText().toString();

                        if(!tempChecker.isEmpty()){

                            //adding entries to table
                            tableDBHandler.addSession(page, sessionSpinners[i].getSelectedItem().toString(),
                                                      Integer.parseInt(lengthText[i].getText().toString()));

                        } else {

                            //not all entries are filled
                            Toast.makeText(getContext(), "You Forgot Something", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        //finishing in each page
                        if(i == data.getSessions(page) - 1){

                            //adding days done
                            data.setDaysDone(data.getDaysDone() + 1);

                            //remove Views
                            baseView.removeAllViews();
                            baseView.addView(textView);
                            baseView.addView(sectionDone, doneRules);

                            data.setDayDone(page, true);

                            //finishing set  Up
                            if(data.getDaysDone() == data.getDays()){

                                Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), timeTable.class));

                            }

                        }
                    }
                }
            });

            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter{

        SectionsPagerAdapter (FragmentManager fm){

            super(fm);
        }


        @Override
        public Fragment getItem (int position){
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount (){
            // Show 3 total pages.
            return data.getDays();
        }


        @Override
        public CharSequence getPageTitle (int position){

            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
