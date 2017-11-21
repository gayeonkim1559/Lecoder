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
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton plusBtn, fastRecordBtn, lectureRecordBtn;
    boolean isClickedPlusBtn=false;
    Context mContext;
    RecyclerView recyclerView;
    MyAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
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

        ArrayList item=new ArrayList<>();
        item.add(new RecordListItem("느린녹음001","2017/03/14","24:11"));
        item.add(new RecordListItem("빠른녹음001","2017/08/10","09:31"));
        item.add(new RecordListItem("빠른녹음002","2017/06/07","00:36"));

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new MyAdapter(item,mContext);
        recyclerView.setAdapter(adapter);
    }
    class MyAdapter extends RecyclerView.Adapter{
        private Context context;
        private ArrayList<RecordListItem> mItems;

        public MyAdapter(ArrayList mItems,Context context) {
            this.mItems = mItems;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.fastrecord_list,parent,false);
            MyViewHolder holder=new MyViewHolder(v);
            return holder;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder aHolder= (MyViewHolder) holder;
            aHolder.recordName.setText(mItems.get(position).recordName);
            aHolder.recordDuration.setText(mItems.get(position).recordDuration);
            aHolder.recordDate.setText(mItems.get(position).recordDate);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView recordDate,recordName,recordDuration,recordClass;
            public MyViewHolder(View itemView) {
                super(itemView);
                recordDate =itemView.findViewById(R.id.fastRecordDate);
                recordDuration=itemView.findViewById(R.id.fastRecordDuration);
                recordName=itemView.findViewById(R.id.fastRecordName);
            }
        }
    }
}
