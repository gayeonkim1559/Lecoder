package com.lecoder.team9.lecoder;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CreateTimeTableActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    EditText editText;
    RadioButton mon, tue, wed, thu, fri, sat;
    Button startTimeBtn, endTimeBtn, saveTimeBtn;
    static int START_TIME = 1, END_TIME = 2;
    int isModify = -1,nextHour,nextMin;
    Intent intent;
    ListView listView;
    String day = "";
    TimeTableAdapter adapter;
    ArrayList<TimeTableItem> items = new ArrayList<>();
    private class TimeTableAdapter extends BaseAdapter {

        public void addItem(TimeTableItem item) {
            items.add(item);
        }
        public boolean isValid(TimeTableItem newItem) throws ParseException {
            SimpleDateFormat transFormat=new SimpleDateFormat("HH:mm");
            Date startTime,endTime;
            Date newStartTime=transFormat.parse(newItem.classStartTime);
            Date newEndTime=transFormat.parse(newItem.classEndTime);
            for (TimeTableItem i: items) {
                if (i.classDay.equals(newItem.classDay)){
                    startTime= transFormat.parse(i.classStartTime);
                    endTime= transFormat.parse(i.classEndTime);
                    if (startTime.compareTo(newStartTime)>0){
                        if (startTime.compareTo(newEndTime)<0){
                            return false;
                        }
                    }else if (startTime.compareTo(newStartTime)<=0){
                        if (endTime.compareTo(newStartTime)>0){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        public void setItem(int i, TimeTableItem item) {
            items.set(i, item);
        }

        @Override
        public TimeTableItem getItem(int i) {
            return items.get(i);
        }

        public void removeItem(int pos) {
            items.remove(pos);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TimeTableListView listView = new TimeTableListView(getApplicationContext());
            TimeTableItem item = items.get(i);
            listView.setClassDay(item.classDay);
            listView.setClassName(item.className);
            listView.setClassStartTime(item.classStartTime);
            listView.setClassEndTime(item.classEndTime);
            return listView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_time_table);
        radioGroup = (RadioGroup) findViewById(R.id.radioWeek);
        mon = (RadioButton) findViewById(R.id.radio_monday);
        tue = (RadioButton) findViewById(R.id.radio_tuesday);
        wed = (RadioButton) findViewById(R.id.radio_wednesday);
        thu = (RadioButton) findViewById(R.id.radio_thursday);
        fri = (RadioButton) findViewById(R.id.radio_friday);
        sat = (RadioButton) findViewById(R.id.radio_saturday);
        editText = (EditText) findViewById(R.id.editText_TimeTable);
        startTimeBtn = (Button) findViewById(R.id.startTimeBtn);
        endTimeBtn = (Button) findViewById(R.id.endTimeBtn);
        saveTimeBtn = (Button) findViewById(R.id.saveTimeBtn);
        listView = (ListView) findViewById(R.id.timetableListView);

        intent=getIntent();
        items= (ArrayList<TimeTableItem>) intent.getSerializableExtra("items");
        if (items==null){
            items = new ArrayList<>();
        }
        adapter = new TimeTableAdapter();
        listView.setAdapter(adapter);
        initRegister();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isModify = i;
                dateReader(i);
                editText.setText(adapter.getItem(i).className);
                startTimeBtn.setText(adapter.getItem(i).classStartTime);
                endTimeBtn.setText(adapter.getItem(i).classEndTime);
                Toast.makeText(getApplicationContext(), "기존의 항목이 선택되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                requestRemoveItem(i);
                isModify = -1;
                initRegister();
                return false;
            }
        });
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(START_TIME);
            }
        });
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(END_TIME);
            }
        });
        saveTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString();
                String startTime = startTimeBtn.getText().toString();
                String endTime = endTimeBtn.getText().toString();
                TimeTableItem newItem = new TimeTableItem(day, name, startTime, endTime);
                boolean validate=false;
                try {
                    validate=adapter.isValid(newItem);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (day.equals("") || editText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
                }else if(!validate){
                    Toast.makeText(getApplicationContext(), "이미 등록된 시간이 있습니다..", Toast.LENGTH_SHORT).show();
                }else {
                    if (isModify != -1) {
                        adapter.setItem(isModify, newItem);
                        Toast.makeText(getApplicationContext(), "기존 일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        isModify = -1;
                    } else {
                        adapter.addItem(newItem);
                        Toast.makeText(getApplicationContext(), "새로운 일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    listView.setAdapter(adapter);
                    initRegister();
                }

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.radio_monday: {
                        mon.setTextColor(Color.BLACK);
                        tue.setTextColor(Color.parseColor("#cdcdcd"));
                        wed.setTextColor(Color.parseColor("#cdcdcd"));
                        thu.setTextColor(Color.parseColor("#cdcdcd"));
                        fri.setTextColor(Color.parseColor("#cdcdcd"));
                        sat.setTextColor(Color.parseColor("#cdcdcd"));
                        day = "월";
                        break;
                    }
                    case R.id.radio_tuesday: {
                        mon.setTextColor(Color.parseColor("#cdcdcd"));
                        tue.setTextColor(Color.BLACK);
                        wed.setTextColor(Color.parseColor("#cdcdcd"));
                        thu.setTextColor(Color.parseColor("#cdcdcd"));
                        fri.setTextColor(Color.parseColor("#cdcdcd"));
                        sat.setTextColor(Color.parseColor("#cdcdcd"));
                        day = "화";
                        break;
                    }
                    case R.id.radio_wednesday: {
                        mon.setTextColor(Color.parseColor("#cdcdcd"));
                        tue.setTextColor(Color.parseColor("#cdcdcd"));
                        wed.setTextColor(Color.BLACK);
                        thu.setTextColor(Color.parseColor("#cdcdcd"));
                        fri.setTextColor(Color.parseColor("#cdcdcd"));
                        sat.setTextColor(Color.parseColor("#cdcdcd"));
                        day = "수";
                        break;
                    }
                    case R.id.radio_thursday: {
                        mon.setTextColor(Color.parseColor("#cdcdcd"));
                        tue.setTextColor(Color.parseColor("#cdcdcd"));
                        wed.setTextColor(Color.parseColor("#cdcdcd"));
                        thu.setTextColor(Color.BLACK);
                        fri.setTextColor(Color.parseColor("#cdcdcd"));
                        sat.setTextColor(Color.parseColor("#cdcdcd"));
                        day = "목";
                        break;
                    }
                    case R.id.radio_friday: {
                        mon.setTextColor(Color.parseColor("#cdcdcd"));
                        tue.setTextColor(Color.parseColor("#cdcdcd"));
                        wed.setTextColor(Color.parseColor("#cdcdcd"));
                        thu.setTextColor(Color.parseColor("#cdcdcd"));
                        fri.setTextColor(Color.BLACK);
                        sat.setTextColor(Color.parseColor("#cdcdcd"));
                        day = "금";
                        break;
                    }

                    case R.id.radio_saturday: {
                        mon.setTextColor(Color.parseColor("#cdcdcd"));
                        tue.setTextColor(Color.parseColor("#cdcdcd"));
                        wed.setTextColor(Color.parseColor("#cdcdcd"));
                        thu.setTextColor(Color.parseColor("#cdcdcd"));
                        fri.setTextColor(Color.parseColor("#cdcdcd"));
                        sat.setTextColor(Color.BLUE);
                        day = "토";
                        break;
                    }
                }
            }
        });
    }
    private void initRegister() {
        editText.setText("");
        startTimeBtn.setText("08:00");
        endTimeBtn.setText("09:00");
        mon.setChecked(false);
        tue.setChecked(false);
        wed.setChecked(false);
        thu.setChecked(false);
        fri.setChecked(false);
        sat.setChecked(false);
    }

    private void dateReader(int i) {
        switch (adapter.getItem(i).classDay) {
            case "월":
                mon.setChecked(true);
                tue.setChecked(false);
                wed.setChecked(false);
                thu.setChecked(false);
                fri.setChecked(false);
                sat.setChecked(false);
                break;
            case "화":
                mon.setChecked(false);
                tue.setChecked(true);
                wed.setChecked(false);
                thu.setChecked(false);
                fri.setChecked(false);
                sat.setChecked(false);
                break;
            case "수":
                mon.setChecked(false);
                tue.setChecked(false);
                wed.setChecked(true);
                thu.setChecked(false);
                fri.setChecked(false);
                sat.setChecked(false);
                break;
            case "목":
                mon.setChecked(false);
                tue.setChecked(false);
                wed.setChecked(false);
                thu.setChecked(true);
                fri.setChecked(false);
                sat.setChecked(false);
                break;
            case "금":
                mon.setChecked(false);
                tue.setChecked(false);
                wed.setChecked(false);
                thu.setChecked(false);
                fri.setChecked(true);
                sat.setChecked(false);
                break;
            case "토":
                mon.setChecked(false);
                tue.setChecked(false);
                wed.setChecked(false);
                thu.setChecked(false);
                fri.setChecked(false);
                sat.setChecked(true);
                break;
        }
    }

    private void showTimePicker(final int toggle) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String hour=(i<10?"0"+String.valueOf(i):String.valueOf(i));
                String minute=(i1<10?"0"+String.valueOf(i1):String.valueOf(i1));
                String value = hour + ":" + minute;
                if (toggle==START_TIME){
                    startTimeBtn.setText(value);
                    nextHour=i;
                    nextMin=i1;
                    String newHour=(i+1<10?"0"+String.valueOf(i+1):String.valueOf(i+1));
                    if (nextHour==Integer.valueOf(endTimeBtn.getText().toString().substring(0,2))&&nextMin>=Integer.valueOf(endTimeBtn.getText().toString().substring(3,5))){
                        endTimeBtn.setText(newHour+":00");
                    }else if (nextHour>Integer.valueOf(endTimeBtn.getText().toString().substring(0,2))){
                        endTimeBtn.setText(newHour+":00");
                    }
                }else{
                    endTimeBtn.setText(value);
                }
            }
        };
        BoundTimePickerDialog dialog;

        if (toggle == START_TIME) {
            dialog = new BoundTimePickerDialog(this, listener, 8, 0, true);
            dialog.setMin(8,0);
            dialog.setMax(18,0);
        } else {
            dialog = new BoundTimePickerDialog(this, listener, nextHour+1, 0, true);
            dialog.setMin(nextHour,nextMin+1);
            dialog.setMax(19,0);
        }
        dialog.show();

    }

    private void requestRemoveItem(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_MyDialog));
        builder.setTitle("안내");
        builder.setMessage("삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.removeItem(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "기존 일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                isModify = -1;
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public class BoundTimePickerDialog extends TimePickerDialog {

        private int minHour = -1, minMinute = -1, maxHour = 100, maxMinute = 100;

        private int currentHour, currentMinute;

        public BoundTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
        }

        public void setMin(int hour, int minute) {
            minHour = hour;
            minMinute = minute;
            currentHour=minHour;
            currentMinute=minMinute;
        }

        public void setMax(int hour, int minute) {
            maxHour = hour;
            maxMinute = minute;
        }

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            super.onTimeChanged(view, hourOfDay, minute);

            boolean validTime;
            if (hourOfDay < minHour) {
                validTime = false;
            } else if (hourOfDay == minHour) {
                validTime = minute >= minMinute;
            } else if (hourOfDay == maxHour) {
                validTime = minute <= maxMinute;
            } else {
                validTime = true;
            }
            if (validTime) {
                currentHour = hourOfDay;
                currentMinute = minute;
            } else {
                updateTime(currentHour, currentMinute);
            }
        }
    }

    @Override
    public void finish() {
        intent.putExtra("items",items);
        setResult(RESULT_OK,intent);
        super.finish();
    }

}
