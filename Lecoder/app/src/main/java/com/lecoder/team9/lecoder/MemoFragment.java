package com.lecoder.team9.lecoder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by GAYEON on 2017-11-23.
 */

public class MemoFragment extends Fragment{
    EditText pageNum;
    EditText memo;
    Button storeBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_memo, container, false);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//    }
}
