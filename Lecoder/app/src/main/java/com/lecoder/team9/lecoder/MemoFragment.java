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
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by GAYEON on 2017-11-23.
 */

public class MemoFragment extends Fragment implements View.OnClickListener{
    EditText pageNum;
    EditText memo;
    Button storeBtn;

    final private  static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    String dirPath = RECORDED_FILE.getAbsolutePath() + "/Lecoder";

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
            Log.d("메모 프래그먼트", "파일에 쓰기 전");
            PrintWriter out = new PrintWriter(dirPath + "/text.txt");
            out.print(memo.getText().toString());
        }catch (Exception e) {
            Log.e("MemoStoring Erro", "메모저장오류");
        }
    }
}
