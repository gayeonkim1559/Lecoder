package com.lecoder.team9.lecoder;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Schwa on 2017-12-04.
 */

public class TimeTableListView extends LinearLayout {
    TextView classDay,className,classStartTime,classEndTime;
    public TimeTableListView(Context context) {
        super(context);
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timetable_list,this,true);
        classDay=findViewById(R.id.timeTableDay);
        className=findViewById(R.id.timeTableName);
        classStartTime=findViewById(R.id.timeTable_startTime);
        classEndTime=findViewById(R.id.timeTable_endTime);
    }

    public void setClassDay(String day) {
        classDay.setText(day);
    }

    public void setClassName(String name) {
        className.setText(name);
    }

    public void setClassStartTime(String time) {
        classStartTime.setText(time);
    }

    public void setClassEndTime(String time) {
        classEndTime.setText(time);
    }
}
