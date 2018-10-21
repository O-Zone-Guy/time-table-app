package com.example.user.timetable_test.CustomAdaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.user.timetable_test.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class EditTimeTableAdaptor extends TimetableAdaptor{

    private int itemCount;

    public EditTimeTableAdaptor (Context context, int day){
        super(context, day);


    }

    @Override
    public void setLists (List <String> names, List <Date> endTimes, List <Integer> length, List <String>
            locationDesc, List <Integer> taskNum){
        super.setLists(names, endTimes, length, locationDesc, taskNum);
        itemCount = names.size();
    }

    void moveView (int first, int second){
        Collections.swap(super.names, first, second);
        Collections.swap(super.endTimes, first, second);
        Collections.swap(super.locations, first, second);
        Collections.swap(super.taskNum, first, second);
        Collections.swap(super.length, first, second);
        notifyItemMoved(first, second);

    }


    void remove (int position){
        super.names.remove(position);
        super.locations.remove(position);
        super.length.remove(position);
        super.nameSpinner.remove(position);
        super.locationText.remove(position);
        super.lengthText.remove(position);
        itemCount--;

        if(position > length.size()){

            super.length.remove(position);
            super.endTimes.remove(position);
            if(position > taskNum.size()) super.taskNum.remove(position);

        }

        notifyItemRemoved(position);

    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        return super.onCreateViewHolder(parent, viewType);


    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position){

        ArrayAdapter <String> adapter =
                new ArrayAdapter <>(super.context, android.R.layout.simple_spinner_item,
                                    data.getNamesArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.nameEdit.setAdapter(adapter);

        holder.endSwitcher.removeView(holder.sessionEnd);
        holder.locSwitcher.removeView(holder.location);
        holder.namesSwitcher.removeView(holder.name);

        holder.sessionCard.setForeground(null);
        holder.sessionStart.setVisibility(View.GONE);

        holder.edit.setText(R.string.save);

        setBottomMargins(holder.sessionCard, position + 1 == getItemCount() ? 10 : 0, context);


        holder.taskNums.setVisibility(View.GONE);

        final TextView.OnEditorActionListener editLength = new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction (TextView v, int actionId, KeyEvent event){

                length.remove(holder.getAdapterPosition());
                length.add(holder.getAdapterPosition(),
                           Integer.parseInt(holder.lengthEdit.getText().toString()));

                return true;
            }
        };


        if(!(position < names.size())){
            names.add(holder.nameEdit.getSelectedItem().toString());
            locations.add("No location set!");
            holder.lengthEdit.setFocusable(true);
            holder.lengthEdit.requestFocus();
            holder.lengthEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
                @Override
                public boolean onEditorAction (TextView v, int actionId, KeyEvent event){

                    if(actionId == EditorInfo.IME_ACTION_DONE){
                        length.add(Integer.parseInt(holder.lengthEdit.getText().toString()));
                        holder.lengthEdit.setOnEditorActionListener(editLength);
                    }

                    return true;
                }
            });

        } else {
            holder.lengthEdit.setText(String.valueOf(length.get(position)));
            holder.lengthEdit.setOnEditorActionListener(editLength);
        }

        holder.nameEdit.setSelection(data.getNamesArray().indexOf(names.get(position)));
        boolean isLesson = !names.get(position).equals("Break") && !names.get(position).equals("Misc.");
        holder.sessionCard.setBackgroundColor(isLesson ? context.getColor(R.color.cardview_light_background) :
                                              names.get(position).equals("Break") ? Color.rgb(67, 160, 71) :
                                              Color.GRAY);
        holder.locEdit.setText(locations.get(position));

        holder.nameEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected (AdapterView <?> parent, View view, int position, long id){

                names.remove(holder.getLayoutPosition());
                names.add(holder.getLayoutPosition(), holder.nameEdit.getSelectedItem().toString());
                
            }

            @Override
            public void onNothingSelected (AdapterView <?> parent){

            }
        });

        holder.locEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction (TextView v, int actionId, KeyEvent event){

                if(actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN){
                    locations.remove(holder.getAdapterPosition());
                    locations.add(holder.getAdapterPosition(), holder.locEdit.getText().toString());
                }

                return true;
            }
        });


    }

    public void addItem (){
        itemCount++;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount (){
        return itemCount;
    }


}
