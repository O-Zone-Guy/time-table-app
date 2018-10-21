package com.example.user.timetable_test.CustomAdaptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.user.timetable_test.MiscData;
import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;
import com.example.user.timetable_test.testFiles.SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.user.timetable_test.MiscData.getInstance;


public class TimetableAdaptor extends RecyclerView.Adapter<TimetableAdaptor.ViewHolder>{

    public    List<Spinner>  nameSpinner  = new ArrayList<>();
    public    List<EditText> locationText = new ArrayList<>();
    public    List<EditText> lengthText   = new ArrayList<>();
    protected List<String>   names        = new ArrayList<>();
    protected List<Integer>  length       = new ArrayList<>();
    List<Date>    endTimes  = new ArrayList<>();
    List<String>  locations = new ArrayList<>();
    List<Integer> taskNum   = new ArrayList<>();
    Context  context;
    MiscData data;
    private SimpleDateFormat sdf = new SimpleDateFormat("kk:mm", Locale.US);
    private SharedPreferences pref;
    private int mExpandPosition = -1;
    private int day;
    private boolean isTimeOn = false;
    private TableDBHandler dbHandler;


    public TimetableAdaptor(Context context, int day){

        this.context = context;
        this.day = day;
        pref = context
                .getSharedPreferences(SharedPreference.PREFERENCE.get(), Context.MODE_PRIVATE);
        data = getInstance();
        dbHandler = new TableDBHandler(context, null);
    }


    static void setBottomMargins(View v, int b, Context context){

        Resources resources = context.getResources();

        b = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, b, resources.getDisplayMetrics());

        if(v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, b);
            v.requestLayout();
        }
    }


    public void setLists(List<String> names, List<Date> endTimes, List<Integer> length,
                         List<String> locationDesc, List<Integer> taskNum){

        this.names = names;
        this.endTimes = endTimes;
        this.locations = locationDesc;
        this.taskNum = taskNum;
        this.length = length;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.timetable_adabter, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){

        String time = position == 0 ? pref.getString("startTime", "7:00") :
                      sdf.format(endTimes.get(position - 1));


        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                                                  data.getNamesArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final boolean isExpanded = position == mExpandPosition;
        boolean isLesson =
                !names.get(position).equals("Break") && !names.get(position).equals("Misc.");


        holder.name.setText(names.get(position));
        holder.sessionEnd.setText(sdf.format(endTimes.get(position)));
        holder.location.setText(locations.get(position));
        holder.taskNums.setText("You have " + taskNum.get(position) + " tasks.");

        holder.nameEdit.setAdapter(adapter);
        holder.nameEdit.setSelection(getInstance().getNamesArray().indexOf(names.get(position)));
        holder.lengthEdit.setText(String.valueOf(length.get(position)));
        holder.locEdit.setText(locations.get(position));

        holder.taskNums.setVisibility(isExpanded && isLesson ? View.VISIBLE : View.GONE);
        holder.locSwitcher.setVisibility(isExpanded && isLesson ? View.VISIBLE : View.GONE);
        holder.endSwitcher.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.edit.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.edit.setVisibility(View.GONE);


        if(isBefore(time)){
            int positionTime = position + 1;
            if(positionTime == position){
                holder.sessionStart.setVisibility(isTimeOn ? View.GONE : View.VISIBLE);
                holder.sessionStart.setText(time);
                isTimeOn = true;
            }
        } else{
            holder.sessionStart.setVisibility(View.GONE);

        }

        setBottomMargins(holder.namesSwitcher, isExpanded || !isLesson ? 0 : 20, context);
        setBottomMargins(holder.sessionCard, position + 1 == getItemCount() ? 10 : 0, context);

        holder.sessionCard.setBackgroundColor(
                isLesson ? context.getColor(R.color.cardview_light_background) :
                names.get(position).equals("Break") ? Color.rgb(67, 160, 71) : Color.GRAY);


        holder.parentLayout.setActivated(isExpanded);
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mExpandPosition = isExpanded ? -1 : holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(holder.sessionCard);
                isTimeOn = false;
                notifyDataSetChanged();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });


    }


    private boolean isBefore(String timeString){

        Calendar c  = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance();

        //noinspection EmptyCatchBlock
        try {
            c1.setTime(sdf.parse(timeString));
        } catch(ParseException e) {

        }

        return c.getTime().before(c1.getTime());

    }


    @Override
    public int getItemCount(){

        return names.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, location, sessionEnd, taskNums, sessionStart;
        Button       edit;
        ViewSwitcher namesSwitcher, locSwitcher, endSwitcher;
        EditText locEdit, lengthEdit;
        Spinner        nameEdit;
        RelativeLayout parentLayout;
        CardView       sessionCard;


        ViewHolder(View v){

            super(v);

            sessionStart = (TextView) v.findViewById(R.id.session_start);
            name = (TextView) v.findViewById(R.id.subject_name_text);
            location = (TextView) v.findViewById(R.id.location_text);
            sessionEnd = (TextView) v.findViewById(R.id.session_end_text);
            taskNums = (TextView) v.findViewById(R.id.task_count_text);

            nameEdit = (Spinner) v.findViewById(R.id.session_name_edit);
            locEdit = (EditText) v.findViewById(R.id.session_loc_edit);
            lengthEdit = (EditText) v.findViewById(R.id.session_length_edit);

            namesSwitcher = (ViewSwitcher) v.findViewById(R.id.subject_text);
            locSwitcher = (ViewSwitcher) v.findViewById(R.id.location_desc);
            endSwitcher = (ViewSwitcher) v.findViewById(R.id.session_end);

            parentLayout = (RelativeLayout) v.findViewById(R.id.table_adaptor_layout);
            sessionCard = (CardView) v.findViewById(R.id.session_card);

            edit = (Button) v.findViewById(R.id.edit_save_session);

            nameSpinner.add(nameEdit);
            locationText.add(locEdit);
            lengthText.add(lengthEdit);

        }


    }


}
