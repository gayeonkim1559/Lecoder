package com.lecoder.team9.lecoder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton plusBtn, fastRecordBtn, lectureRecordBtn;
    boolean isClickedPlusBtn=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

}
