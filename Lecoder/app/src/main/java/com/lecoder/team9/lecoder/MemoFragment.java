package com.lecoder.team9.lecoder;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by GAYEON on 2017-11-23.
 */

public class MemoFragment extends Fragment implements View.OnClickListener{
    EditText pageNum;
    EditText memo;
    ImageButton storeBtn;
    TextView currentTime;
    final private  static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    String dirPath = RECORDED_FILE.getAbsolutePath() + "/Lecoder";
    BufferedWriter out;
    String savePath,fileName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_memo, container, false);

        Log.d("메모프레그먼트", "프래그먼트 실행");

        pageNum = view.findViewById(R.id.pageInput);
        memo = view.findViewById(R.id.memoInput);
        storeBtn = view.findViewById(R.id.saveBtn);
        currentTime=view.findViewById(R.id.currentTime);
        storeBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveBtn:
                if (((RecordActivity)getActivity()).isRecording()){
                    storeMemo();
                }else {
                    Toast.makeText(getContext(),"녹음 버튼을 눌러주세요.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void storeMemo() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dirPath+"/"+savePath+"/"+fileName);
            OutputStreamWriter OutputStreamWriter = new OutputStreamWriter(fileOutputStream, "MS949");
            out = new BufferedWriter(OutputStreamWriter);
//            out = new BufferedWriter(new FileWriter(dirPath+"/"+savePath+"/"+fileName));

            out.write(pageNum.getText().toString()+"\n"+memo.getText().toString());
            currentTime.setText(((RecordActivity)getActivity()).getSaveTime());
            out.flush();
            Log.d("메모프레그먼트", "텍스트 저장완료");
            memo.setText("");
            Toast.makeText(getContext(), "텍스트가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("MemoStoring Erro", "메모저장오류");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        savePath=((RecordActivity)getActivity()).pathToSave();
        String name=((RecordActivity)getActivity()).getSaveTime().replace(":","m");
        fileName="txt"+name+".txt";
    }
}
