package com.lecoder.team9.lecoder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TimeTableActivity extends AppCompatActivity {

    Toolbar toolbar;
    GridView gridView;
    TimeTableAdapter tableAdapter;
    RelativeLayout drawLayout;
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    String key = "Key";
    int rangeBetweenStartFromCell = 0;
    ArrayList<TimeTableItem> getItemList = new ArrayList<>();
    String[] daySet = {"", "월", "화", "수", "목", "금", "토"};
    AlarmTimeSet alarmTimeSet;
    int index=0;

    public class AlarmTimeSet{
        private Context context;

        public AlarmTimeSet(Context context) {
            this.context = context;
        }
        public void alarmSet(TimeTableItem item,int dayPos,int index){
            AlarmManager alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent=new Intent(TimeTableActivity.this,PushAlarmBroadcast.class);

            Calendar calendar=Calendar.getInstance();

            int hour=Integer.valueOf(item.classStartTime.substring(0,2));//시작시각
            int minute=Integer.valueOf(item.classStartTime.substring(3,5));//시작분

            calendar.set(Calendar.DAY_OF_WEEK,dayPos+1);
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,minute);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            Log.e("[test] PUSH MSG : ",item.className);

            long diff = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();
            intent.putExtra("className",item.className);
            intent.putExtra("diff",diff);
            PendingIntent sender=PendingIntent.getBroadcast(TimeTableActivity.this,index,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),24*7*60*60*1000,sender);
        }
    }


    public void setItemArrayPref(ArrayList<TimeTableItem> values) {
        Gson gson = new Gson();
        String json = gson.toJson(values);

        editor = shref.edit();
        editor.remove(key).commit();
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<TimeTableItem> getItemArrayPref() {
        Gson gson = new Gson();
        String response=shref.getString(key , "");
        return gson.fromJson(response,new TypeToken<List<TimeTableItem>>(){}.getType());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        alarmTimeSet=new AlarmTimeSet(getApplicationContext());

        gridView = (GridView) findViewById(R.id.table_gridView);
        drawLayout = (RelativeLayout) findViewById(R.id.drawLayout);
        tableAdapter = new TimeTableAdapter();
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setAdapter(tableAdapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar_timetable);
        toolbar.setTitle("TimeTable");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        shref = getApplicationContext().getSharedPreferences("table", Context.MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int count=getItemList.size();
        if (count!=0){
            for (int i=0;i<count;i++){
                unregisterAlarm(getApplicationContext(),i);
            }
        }
        drawLayout.removeAllViews();
        getItemList=getItemArrayPref();
        index=0;
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getItemList!=null&&getItemList.size()>0){
                    for (int i = 0; i < 7; i++) {
                        renderTableCell(i);
                    }
                }
                ViewTreeObserver obs = gridView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        drawLayout.removeAllViews();
    }

    private void drawTimeTable(int i, String s, int duration) {
        View cell = gridView.getChildAt(i);
        int heightNorm = cell.getHeight();
        int width = cell.getWidth();
        int height_6 = cell.getHeight() / 6 * duration + (duration / 6 * 4);
        int top = heightNorm * rangeBetweenStartFromCell / 6;
        int left = cell.getLeft();
        Log.d("[TEST] DURATION", String.valueOf(duration));
        TextView text = new TextView(TimeTableActivity.this);

        text.setBackgroundResource(R.drawable.border_table);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height_6);
        params.setMargins(left, top, 0, 0);
        text.setLayoutParams(params);
        text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        text.setTextColor(Color.parseColor("#FFEEFF"));
        text.setTextSize(13);
        text.setText(s);
        drawLayout.addView(text);
    }

    class TimeTableAdapter extends BaseAdapter {
        Context mContext = getApplicationContext();
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        int count = 77;
        int[] timeSet = {8, 9, 10, 11, 12, 1, 2, 3, 4, 5, 6};

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View oldView, ViewGroup viewGroup) {
            View v;
            if (position < count) {
                v = new TextView(mContext);
                v.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                if (position % 7 == 0) {
                    ((TextView) v).setGravity(Gravity.END);
                    ((TextView) v).setText(timeSet[position / 7] + "시");
                    v.setBackgroundResource(R.drawable.timetable_cell);
                } else {
                    v.setBackgroundResource(R.drawable.border2);
//                    renderTableCell(position);

                }
            } else {
                v = oldView;
            }
            return v;

        }
    }

    private void renderTableCell(int position) {
        String day;
        int duration = 0;
        int dayPos=position%7;
        day = daySet[dayPos];//1월,2화,3수,4목,5금,6토
        if (getItemList.size() > 0) {
            for (TimeTableItem item : getItemList) {
                if (item.classDay.equals(day)) {

                    try {
                        duration = durationStartToEnd(item);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    drawTimeTable(position, item.className, duration);
                    alarmTimeSet.alarmSet(item,dayPos,index);
                    index++;
                }
            }
        }
    }

    private int durationStartToEnd(TimeTableItem item) throws ParseException {
        SimpleDateFormat transFormat = new SimpleDateFormat("HH:mm");
        Date firstTime = transFormat.parse("08:00");
        Date startTime = transFormat.parse(item.classStartTime);
        Date endTime = transFormat.parse(item.classEndTime);
        rangeBetweenStartFromCell = (int) (startTime.getTime() - firstTime.getTime()) / (60 * 10000);
        long diff = endTime.getTime() - startTime.getTime();
        return (int) (diff / (60 * 10000));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                getItemList = (ArrayList<TimeTableItem>) data.getSerializableExtra("items");
                setItemArrayPref(getItemList);
                if (getItemList!=null&&getItemList.size()>0){
                    for (int i = 0; i < 7; i++) {
                        renderTableCell(i);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.plusTableBtn) {
            Intent intent = new Intent(this, CreateTimeTableActivity.class);
            intent.putExtra("items",getItemList);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }

    public static void unregisterAlarm(Context context,int index)
    {
        Intent intent = new Intent();
        PendingIntent sender= PendingIntent.getBroadcast(context, index, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(sender);
    }

}
