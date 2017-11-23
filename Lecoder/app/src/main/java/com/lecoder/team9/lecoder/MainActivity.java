package com.lecoder.team9.lecoder;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton plusBtn, fastRecordBtn, lectureRecordBtn;
    boolean isClickedPlusBtn=false;
    Context mContext;
    RecyclerView recyclerViewFast,recyclerViewLecture;
    MyAdapter adapter;
    RecyclerView.LayoutManager layoutManager,layoutManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();
        recyclerViewFast = (RecyclerView) findViewById(R.id.recyclerView_fast);
        recyclerViewFast.setHasFixedSize(true);

        recyclerViewLecture= (RecyclerView) findViewById(R.id.recyclerView_lecture);
        recyclerViewLecture.setHasFixedSize(true);
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
        //----팝업 버튼 애니메이션 동작

        //----빠른녹음 리스트
        ArrayList fastItem=new ArrayList<>();
        fastItem.add(new RecordListItem("느린녹음001","2017/03/14","24:11"));
        fastItem.add(new RecordListItem("빠른녹음001","2017/08/10","09:31"));
        fastItem.add(new RecordListItem("빠른녹음002","2017/06/07","00:36"));

        layoutManager=new LinearLayoutManager(this);
        recyclerViewFast.setLayoutManager(layoutManager);
        adapter=new MyAdapter(fastItem,mContext,false);
        recyclerViewFast.setAdapter(adapter);
        //----빠른녹음 리스트
        ArrayList lectureItem=new ArrayList();
        lectureItem.add(new RecordListItem("002","2017/11/21","33:12","모바일소프트웨어공학"));
        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
        lectureItem.add(new RecordListItem("001","2017/10/01","53:04","기초글쓰기"));
        lectureItem.add(new RecordListItem("002","2017/11/21","33:12","모바일소프트웨어공학"));
        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
        lectureItem.add(new RecordListItem("012","2017/06/26","12:55","소프트웨어공학"));
        lectureItem.add(new RecordListItem("001","2017/10/01","53:04","끝"));

        layoutManager2=new LinearLayoutManager(this);
        recyclerViewLecture.setLayoutManager(layoutManager2);
        adapter=new MyAdapter(lectureItem,mContext,true);
        recyclerViewLecture.setAdapter(adapter);

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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder aHolder= (MyViewHolder) holder;
            aHolder.recordName.setText(mItems.get(position).recordName);
            aHolder.recordDuration.setText(mItems.get(position).recordDuration);
            aHolder.recordDate.setText(mItems.get(position).recordDate);
            if (isLectureList){
                aHolder.recordClass.setText(mItems.get(position).recordClass);
                aHolder.recordClass.setVisibility(View.VISIBLE);
                TextView recordClassText= v.findViewById(R.id.recordName);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recordClassText.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, R.id.recordGoBtn);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView recordDate,recordName,recordDuration,recordClass;
            public MyViewHolder(View itemView) {
                super(itemView);
                recordDate =itemView.findViewById(R.id.recordDate);
                recordDuration=itemView.findViewById(R.id.recordDuration);
                recordName=itemView.findViewById(R.id.recordName);
                if (isLectureList){
                    recordClass=itemView.findViewById(R.id.recordClass);
                }
            }
        }
    }
}
