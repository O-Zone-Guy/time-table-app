package com.example.user.timetable_test.CustomAdaptors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.timetable_test.R;
import com.example.user.timetable_test.ViewTaskDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ViewTasksAdaptor extends RecyclerView.Adapter <ViewTasksAdaptor.ViewHolder>{

    private List <String> titles      = new ArrayList <>();
    private List <String> desc        = new ArrayList <>();
    private List <Date>   dueDateList = new ArrayList <>();
    private List          id          = new ArrayList();
    private String subject;

    private int lastPosition = -1;

    private Context  context;
    private Activity activity;

    private SimpleDateFormat sdfDueDate = new SimpleDateFormat("EEE,\ndd/MM/yy", Locale.US);

    public ViewTasksAdaptor (Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void setViewLists (List <String> titles, List <String> desc, List <Date> dueDate, List <Integer>
            id, String subject){

        this.titles = titles;
        this.desc = desc;
        this.dueDateList = dueDate;
        this.id = id;
        this.subject = subject;

    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_task_adaptor, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position){

        if(id.size() != 0){
            holder.titleText.setText(titles.get(position));
            holder.desc.setText(desc.get(position));
            holder.id.setText(String.valueOf(id.get(position)));
            holder.dueDate.setText(dateToString(dueDateList.get(position)));
        }
        setAnimation(holder.layout, position);

        holder.layout.setVisibility(id.size() == 0 ? View.GONE : View.VISIBLE);
        holder.noTasks.setVisibility(id.size() == 0 ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount (){

        int count;
        count = id.size() == 0 ? 1 : id.size();

        return count;
    }

    private void setAnimation (View viewToAnimate, int position){

        if(position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_items);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }

    private String dateToString (Date date){

        String stringDate;

        stringDate = sdfDueDate.format(date);

        return stringDate;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleText, desc, dueDate, id, noTasks;
        LinearLayout layout;

        ViewHolder (View v){

            super(v);

            noTasks = (TextView) v.findViewById(R.id.no_tasks_text);
            titleText = (TextView) v.findViewById(R.id.task_title);
            desc = (TextView) v.findViewById(R.id.taskDesc);
            dueDate = (TextView) v.findViewById(R.id.dueDatetext);
            id = (TextView) v.findViewById(R.id.task_id);
            layout = (LinearLayout) v.findViewById(R.id.customViewLayout);

            RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.clicky);

            relativeLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View view){

                    Intent intent = new Intent(context, ViewTaskDetails.class);
                    intent.putExtra("desc", desc.getText().toString());
                    intent.putExtra("title", titleText.getText().toString());
                    intent.putExtra("subject", subject);
                    intent.putExtra("id", Integer.parseInt(id.getText().toString()));

                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(activity, Pair.create((View) titleText, "title"),
                                                          Pair.create((View) desc, "desc"),
                                                          Pair.create((View) dueDate, "dueDate"));

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent, options.toBundle());
                }
            });
        }
    }


}
