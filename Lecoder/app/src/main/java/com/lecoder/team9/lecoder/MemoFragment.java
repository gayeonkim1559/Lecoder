package com.lecoder.team9.lecoder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by GAYEON on 2017-11-23.
 */

public class MemoFragment extends Fragment implements View.OnClickListener{
    EditText pageNum;
    EditText memo;
    ImageButton storeBtn;

    final private  static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    String dirPath = RECORDED_FILE.getAbsolutePath() + "/Lecoder";

    BufferedWriter out;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_memo, container, false);

        Log.d("메모프레그먼트", "프래그먼트 실행");

        pageNum = view.findViewById(R.id.pageInput);
        memo = view.findViewById(R.id.memoInput);
        storeBtn = view.findViewById(R.id.saveBtn);

        storeBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveBtn:
                storeMemo();
                break;
        }
    }

    public void storeMemo() {
        try {
            out = new BufferedWriter(new FileWriter(dirPath + "/text.txt"));
            out.write(memo.getText().toString());
            out.flush();
            Log.d("메모프레그먼트", "텍스트 저장완료");
            memo.setText("");
            Toast.makeText(getContext(), "텍스트가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("MemoStoring Erro", "메모저장오류");
        }
    }
}
