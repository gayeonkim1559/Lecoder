package com.lecoder.team9.lecoder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by GAYEON on 2017-11-23.
 */

public class RecordActivity extends FragmentActivity implements View.OnClickListener {
    private final int MEMO = 1;
    private final int DRAWING = 2;

    private Button memoBtn, drawingBtn, cameraBtn;
    private ImageButton recordBtn, stopBtn;

    Fragment memoFragment = new MemoFragment();
    Fragment drawingFragment = new MemoFragment();

    private final int OPEN = 1;
    private final int CLOSED = 2;

    private int memoFragment_Flag = CLOSED;
    private int drawingFragment_Flag = CLOSED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        memoBtn = (Button) findViewById(R.id.memoBtn);
        drawingBtn = (Button) findViewById(R.id.drawingBtn);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);

        recordBtn = (ImageButton) findViewById(R.id.recordBtn);
        stopBtn = (ImageButton) findViewById(R.id.stopBtn);

        memoBtn.setOnClickListener(this);
        drawingBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
                case R.id.memoBtn:
                    if (memoFragment_Flag == CLOSED) {
                        openFragment(MEMO);
                        memoFragment_Flag = OPEN;
                    }
                    else {
                        closeFragment(MEMO);
                        memoFragment_Flag = CLOSED;
                    }
                    break;
                case R.id.drawingBtn:
                    if (drawingFragment_Flag == CLOSED) {
                        openFragment(DRAWING);
                        drawingFragment_Flag = OPEN;
                    }
                    else {
                        closeFragment(DRAWING);
                        drawingFragment_Flag = CLOSED;
                    }
                    break;
                case R.id.cameraBtn:
        }
    }

    private void openFragment(int fragmentNum) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragmentNum) {
            case MEMO:
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                transaction.replace(R.id.fragment_container, memoFragment);
                transaction.commit();
                break;

            case DRAWING:
                transaction.replace(R.id.fragment_container, drawingFragment);
                //transaction.setCustomAnimations(R.anim.fragment_slide_up, R.anim.fragment_slide_down);
                transaction.commit();
                break;
        }
    }

    private void closeFragment(int fragmentNum) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragmentNum) {
            case MEMO:
                //transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
                //transaction.setCustomAnimations()
                transaction.replace(R.id.fragment_container, memoFragment);
                transaction.commit();
                break;

            case DRAWING:
                transaction.replace(R.id.fragment_container, drawingFragment);
                //transaction.setCustomAnimations(R.anim.fragment_slide_up, R.anim.fragment_slide_down);
                transaction.commit();
                break;
        }
    }
}



