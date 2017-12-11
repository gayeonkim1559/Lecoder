package com.lecoder.team9.lecoder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton plusBtn, fastRecordBtn, lectureRecordBtn;
    boolean isClickedPlusBtn=false;
    Context mContext;
    RecyclerView recyclerViewFast,recyclerViewLecture;
    MyAdapter adapter,adapterLecture;
    RecyclerView.LayoutManager layoutManager,layoutManager2;
    Toolbar toolbar;
    String key = "Key";
    SharedPreferences shref,fastShref,lecShref;
    SharedPreferences.Editor editor;
    CharSequence itemList[];
    ArrayList<TimeTableItem> itemArrayList;
    ArrayList<RecordListItem> fastItem;
    ArrayList<RecordListItem> lectureItem;

    private static String[] permissions = {Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();
        recyclerViewFast = (RecyclerView) findViewById(R.id.recyclerView_fast);
        recyclerViewFast.setHasFixedSize(true);

        recyclerViewLecture= (RecyclerView) findViewById(R.id.recyclerView_lecture);
        recyclerViewLecture.setHasFixedSize(true);

        //---툴바 설정
        CollapsingToolbarLayout toolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout01);
        toolbarLayout.setTitleEnabled(true);
        toolbar= (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("나만의 강의록, LECORDER");
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        //---툴바 설정

        //----팝업 버튼 애니메이션 동작
        plusBtn = (FloatingActionButton) findViewById(R.id.mainPlusBtn);
        fastRecordBtn = (FloatingActionButton) findViewById(R.id.mainFastRecordBtn);
        lectureRecordBtn = (FloatingActionButton) findViewById(R.id.mainLectureRecordBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation plusBtnAnim,fastRecordAnim,lectureRecordAnim;
                plusBtnAnim=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_plusbtn);
                if (isClickedPlusBtn){
                    fastRecordBtn.setVisibility(View.INVISIBLE);
                    lectureRecordBtn.setVisibility(View.INVISIBLE);
                    fastRecordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down_fastbtn);
                    lectureRecordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down_lecbtn);
                    isClickedPlusBtn=false;
                }else {
                    fastRecordBtn.setVisibility(View.VISIBLE);
                    lectureRecordBtn.setVisibility(View.VISIBLE);
                    fastRecordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up_fastbtn);
                    lectureRecordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up_lecbtn);
                    isClickedPlusBtn=true;
                }
                plusBtn.startAnimation(plusBtnAnim);
                fastRecordBtn.startAnimation(fastRecordAnim);
                lectureRecordBtn.startAnimation(lectureRecordAnim);
            }
        });
        fastRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"빠른녹음 시작",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),RecordActivity.class);
                intent.putExtra("recordType","Fast");
                startActivity(intent);
            }
        });
        lectureRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"강의녹음 시작",Toast.LENGTH_SHORT).show();
                showLectureList();
            }
        });
        //----팝업 버튼 애니메이션 동작

        //----빠른녹음 샘플 리스트
        shref = getApplicationContext().getSharedPreferences("table", Context.MODE_PRIVATE);
        fastShref=getApplicationContext().getSharedPreferences("fastList", Context.MODE_PRIVATE);;
        lecShref=getApplicationContext().getSharedPreferences("lectureList", Context.MODE_PRIVATE);
        fastItem=getRecordArrayPref(fastShref,"fastList");
        lectureItem=getRecordArrayPref(lecShref,"lectureList");
//        fastItem.add(new RecordListItem("느린녹음001","2017/03/14","24:11"));
//        fastItem.add(new RecordListItem("빠른녹음001","2017/08/10","09:31"));
//        fastItem.add(new RecordListItem("빠른녹음002","2017/06/07","00:36"));

        layoutManager=new LinearLayoutManager(this);
        recyclerViewFast.setLayoutManager(layoutManager);
        if (fastItem!=null){
            adapter=new MyAdapter(fastItem,mContext,false);
            recyclerViewFast.setAdapter(adapter);
        }
        //----강의녹음 샘플 리스트
//        lectureItem.add(new RecordListItem("002","2017/11/21","33:12","모바일소프트웨어공학"));
//        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
//        lectureItem.add(new RecordListItem("001","2017/10/01","53:04","기초글쓰기"));
//        lectureItem.add(new RecordListItem("002","2017/11/21","33:12","모바일소프트웨어공학"));
//        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
//        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
//        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
//        lectureItem.add(new RecordListItem("001","2017/10/01","53:04","끝"));

        layoutManager2=new LinearLayoutManager(this);
        recyclerViewLecture.setLayoutManager(layoutManager2);
        if (lectureItem!=null){
            adapterLecture=new MyAdapter(lectureItem,mContext,true);
            recyclerViewLecture.setAdapter(adapterLecture);
        }

        //강의버튼 - 목록
        setItemArrayPref(fastItem,"fastList");
        setItemArrayPref(lectureItem,"lectureList");

        requestPermissions(); //권한 얻기
    }

    private void showLectureList() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("강의를 선택하세요.");
        builder.setItems(itemList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),RecordActivity.class);
                intent.putExtra("recordType","Lecture");
                intent.putExtra("recordClass",itemArrayList.get(i).className);
                Toast.makeText(MainActivity.this, itemList[i].toString(), Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                startActivity(intent);
            }


        });
        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //강의목록 읽어오기
        fastItem=getRecordArrayPref(fastShref,"fastList");
        lectureItem=getRecordArrayPref(lecShref,"lectureList");
        if (fastItem!=null){
            adapter=new MyAdapter(fastItem,mContext,false);
            recyclerViewFast.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        if (lectureItem!=null){
            adapterLecture=new MyAdapter(lectureItem,mContext,true);
            recyclerViewLecture.setAdapter(adapterLecture);
            adapterLecture.notifyDataSetChanged();
        }

        itemArrayList=getItemArrayPref();
        if (itemArrayList!=null){
            itemList=new CharSequence[itemArrayList.size()];
            int i=0;
            for (TimeTableItem item:itemArrayList){
                CharSequence cs=new StringBuffer("["+item.classDay+"] ["+item.classStartTime+"~"+item.classEndTime+"] "+item.className);
                itemList[i++]=cs;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public ArrayList<TimeTableItem> getItemArrayPref() {
        Gson gson = new Gson();
        String response=shref.getString(key , "");
        return gson.fromJson(response,new TypeToken<List<TimeTableItem>>(){}.getType());
    }
    public ArrayList<RecordListItem> getRecordArrayPref(SharedPreferences shref,String key) {
        Gson gson = new Gson();
        String response=shref.getString(key , "");
        return gson.fromJson(response,new TypeToken<List<RecordListItem>>(){}.getType());
    }

    public void setItemArrayPref(ArrayList<RecordListItem> values,String key) {
        Gson gson = new Gson();
        String json = gson.toJson(values);

        editor = shref.edit();
        editor.remove(key).commit();
        editor.putString(key, json);
        editor.commit();
    }
    class MyAdapter extends RecyclerView.Adapter{
        private Context context;
        private ArrayList<RecordListItem> mItems;
        private boolean isLectureList;
        private View v;
        public MyAdapter(ArrayList mItems,Context context,boolean isLectureList) {
            this.mItems = mItems;
            this.context = context;
            this.isLectureList =isLectureList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list,parent,false);
            MyViewHolder holder=new MyViewHolder(v);
            return holder;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MyViewHolder aHolder= (MyViewHolder) holder;
            aHolder.recordName.setText(mItems.get(position).recordName);
            aHolder.recordDuration.setText(mItems.get(position).recordDuration);
            aHolder.recordDate.setText(mItems.get(position).recordDate);
            if (isLectureList){
                aHolder.recordName.setText(mItems.get(position).tag);
                aHolder.recordClass.setText(mItems.get(position).recordClass);
                aHolder.recordClass.setVisibility(View.VISIBLE);
                TextView recordClassText= v.findViewById(R.id.recordName);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recordClassText.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, R.id.recordGoBtn);
            }
            aHolder.itemView.setOnClickListener(new View.OnClickListener() {
                //터치 이벤트 임시 출력
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"리스트 구분 : "+(isLectureList?"강의녹음":"빠른녹음")+"\n클릭한 아이템 : "+mItems.get(position).recordName.toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),PlayActivity.class);
                    if (isLectureList){
                        intent.putExtra("playType","Lecture");
                    }else {
                        intent.putExtra("playType","빠른녹음");
                    }
                    intent.putExtra("playDate",mItems.get(position).recordDate);
                    intent.putExtra("playName",mItems.get(position).recordName);
                    intent.putExtra("playClass",mItems.get(position).recordClass);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView recordDate,recordName,recordDuration,recordClass;
            public MyViewHolder(View itemView) {
                super(itemView);
                recordDate =itemView.findViewById(R.id.play_subjectDate);
                recordDuration=itemView.findViewById(R.id.recordDuration);
                recordName=itemView.findViewById(R.id.recordName);
                if (isLectureList){
                    recordClass=itemView.findViewById(R.id.recordClass);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.calendarBtn){
            Intent intent=new Intent(this,TimeTableActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        }else if(item.getItemId()==R.id.settingsBtn){
            Intent intent=new Intent(this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, 1);
    }
    
}
