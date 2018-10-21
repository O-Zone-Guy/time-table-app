package com.example.user.timetable_test.CustomAdaptors;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


public class TimeTableItemTouchHelper extends ItemTouchHelper.SimpleCallback{

    private EditTimeTableAdaptor timetableAdaptor;

    public TimeTableItemTouchHelper (EditTimeTableAdaptor timetableAdaptor){

        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.timetableAdaptor = timetableAdaptor;

    }

    @Override
    public boolean onMove (RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView
            .ViewHolder target){
        timetableAdaptor.moveView(viewHolder.getAdapterPosition(), target.getAdapterPosition());

        return true;
    }

    @Override
    public void onSwiped (RecyclerView.ViewHolder viewHolder, int direction){

        timetableAdaptor.remove(viewHolder.getAdapterPosition());
    }

}
