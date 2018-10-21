package com.example.user.timetable_test.CustomAdaptors;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.timetable_test.R;
import com.example.user.timetable_test.TableDBHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ViewAllTasksAdaptor extends RecyclerView.Adapter <ViewAllTasksAdaptor.ViewHolder>{

    private List <Boolean> isExpanded   = new ArrayList <>();
    private List <String>  subjectNames = new ArrayList <>();
    private Context        context;
    private TableDBHandler dbHandler;
    private List <ViewTasksAdaptor> tasksAdaptor = new ArrayList <>();

    private Activity activity;

    public ViewAllTasksAdaptor (Context context, Activity activity){

        this.context = context;
        this.activity = activity;

        dbHandler = new TableDBHandler(context, null);

    }

    public void setLists (List <String> subjectNames){

        this.subjectNames = subjectNames;

    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_tasks_adaptor, parent, false);

        isExpanded.add(false);
        tasksAdaptor.add(new ViewTasksAdaptor(context, activity));

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position){

        String name = subjectNames.get(position);

        List <String>  titles  = new ArrayList <>();
        List <String>  desc    = new ArrayList <>();
        List <Date>    dueDate = new ArrayList <>();
        List <Integer> ids     = new ArrayList <>();

        if(!dbHandler.getTaskDescs(name).isEmpty()){
            titles = dbHandler.getTaskTitles(name);
            desc = dbHandler.getTaskDescs(name);
            dueDate = dbHandler.getDueDate(name);
            ids = dbHandler.getIDs(name);
        }
        tasksAdaptor.get(position).setViewLists(titles, desc, dueDate, ids, name);

        holder.tasksNum.setText(ids.size() + " task(s).");
        holder.tasksNum.setVisibility(ids.size() == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.subjectText.setText(name);
        holder.tasksView.setAdapter(tasksAdaptor.get(position));
        holder.tasksView.setLayoutManager(new LinearLayoutManager(context));

        holder.tasksView.setVisibility(isExpanded.get(position) ? View.VISIBLE : View.GONE);

        holder.nameClick.setOnClickListener(new View.OnClickListener(){
            private boolean tempBoolean;

            @Override
            public void onClick (View v){

                Log.i("debug", "Clicked");
                tempBoolean = isExpanded.get(holder.getAdapterPosition());
                isExpanded.remove(holder.getAdapterPosition());
                isExpanded.add(holder.getAdapterPosition(), !tempBoolean);
                TransitionManager.beginDelayedTransition(holder.mainView);
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount (){
        return subjectNames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView subjectText, tasksNum;
        RelativeLayout nameClick;
        LinearLayout   mainView;
        RecyclerView   tasksView;

        ViewHolder (View v){
            super(v);
            mainView = (LinearLayout) v.findViewById(R.id.all_tasks_main_view);
            subjectText = (TextView) v.findViewById(R.id.subject_all_tasks);
            tasksNum = (TextView) v.findViewById(R.id.tasks_num);
            nameClick = (RelativeLayout) v.findViewById(R.id.task_name_clicker);
            tasksView = (RecyclerView) v.findViewById(R.id.all_tasks_rec_view);


        }
    }
}
