package com.lecoder.team9.lecoder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by GAYEON on 2017-11-23.
 */

public class RecordActivity extends FragmentActivity implements View.OnClickListener {
    private final int MEMO = 1;
    private final int DRAWING = 2;

    private Button memoBtn, drawingBtn, cameraBtn;
    private ImageButton recordBtn, stopBtn;

    private TextView timer, subjectName;

    Fragment memoFragment = new MemoFragment();
    Fragment drawingFragment = new DrawingFragment();

    private final int OPEN = 1;
    private final int CLOSED = 2;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private int memoFragment_Flag = CLOSED;
    private int drawingFragment_Flag = CLOSED;
    String currentTime,pictureTakenTime;
    String path;
    int tempFileNum = 1;
    public static int order = 0;

    private boolean isFirst = true;
    private boolean isRecording = false;

    UpdateTimer updateTimer;
    ImageView cameraPicture;
    private String recordType, recordClass;
    SharedPreferences fastShref, lecShref;
    SharedPreferences.Editor editor;
    ArrayList<RecordListItem> fastArrayList;
    ArrayList<RecordListItem> lectureArrayList;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        memoBtn = (Button) findViewById(R.id.memoBtn);
        drawingBtn = (Button) findViewById(R.id.drawingBtn);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);

        recordBtn = (ImageButton) findViewById(R.id.recordBtn);
        stopBtn = (ImageButton) findViewById(R.id.stopBtn);
        cameraPicture = findViewById(R.id.cameraPicture);
        timer = findViewById(R.id.timer);
        subjectName = findViewById(R.id.subject_name);
        frameLayout=findViewById(R.id.fragment_container);
        memoBtn.setOnClickListener(this);
        drawingBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        updateTimer = new UpdateTimer();
        updateTimer.start();

        fastShref = getApplicationContext().getSharedPreferences("fastList", Context.MODE_PRIVATE);
        lecShref = getApplicationContext().getSharedPreferences("lectureList", Context.MODE_PRIVATE);
        Intent getIntent = getIntent();
        recordType = getIntent.getStringExtra("recordType");
        recordClass = getIntent.getStringExtra("recordClass");
        subjectName.setText(recordClass);

        fastArrayList = getItemArrayPref(fastShref, "fastList");
        lectureArrayList = getItemArrayPref(lecShref, "lectureList");
        if (fastArrayList == null) {
            fastArrayList = new ArrayList<>();
        }
        if (lectureArrayList == null) {
            lectureArrayList = new ArrayList<>();
        }

        //TODO fastArrayList,lectureArrayList에 갱신해야함... 갱신한걸 메인에서 또 갱신해야함...
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.memoBtn:
                drawingFragment_Flag = CLOSED;
                if (memoFragment_Flag == CLOSED) {
                    Log.d("레코드 액티비티", "프래그먼트 열기 전");
                    openFragment(MEMO);
                    memoFragment_Flag = OPEN;
                } else {
                    closeFragment(MEMO);
                    memoFragment_Flag = CLOSED;
                }
                break;

            case R.id.drawingBtn:
                memoFragment_Flag = CLOSED;
                if (drawingFragment_Flag == CLOSED) {
                    openFragment(DRAWING);
                    drawingFragment_Flag = OPEN;
                } else {
                    closeFragment(DRAWING);
                    drawingFragment_Flag = CLOSED;
                }


            case R.id.cameraBtn:

                if (isFirst) {
                    Toast.makeText(getApplicationContext(), "녹음을 먼저 시작해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    pictureTakenTime="img" + getSaveTime().replace(":", "m") + ".jpg";
                    startActivityForResult(intentCamera, PICK_FROM_CAMERA);
                }
                break;

            case R.id.recordBtn:
                //처음에 녹음 할 때
                if (isFirst) {
                    isFirst = false;
                    recordBtn.setBackgroundResource(R.drawable.pause_btn_black);

                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
                    currentTime = String.valueOf(formatter1.format(new Date(System.currentTimeMillis())));
                    path = pathToSave();

                    Toast.makeText(getApplicationContext(), "녹음을 시작합니다.", Toast.LENGTH_SHORT).show();
                }

                //녹음 -> 일시중지
                if (isRecording) {
                    isRecording = false;
                    recordBtn.setBackgroundResource(R.drawable.record_btn);
                    //order++;

                    Intent pause = new Intent(this, RecordService.class);
                    pause.putExtra("isRecording", true);
                    //mService.stopService(pause);
                    stopService(pause);
                }

                //일시중지 -> 녹음
                else {
                    isRecording = true;
                    recordBtn.setBackgroundResource(R.drawable.pause_btn_black);

                    Intent intent = new Intent(this, RecordService.class);
                    intent.putExtra("savePath", path);
                    intent.putExtra("fileName", tempFileNum);
                    intent.putExtra("isRecording", true);
                    tempFileNum++;
                    //mService.startService(intent);
                    startService(intent);
                }
                break;

            case R.id.stopBtn:
                if (!isFirst) {
                    Intent intent2 = new Intent(this, RecordService.class);
                    stopService(intent2);
                    isRecording = false;
                    Toast.makeText(getApplicationContext(), "녹음 저장을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                    updateRecentList();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "먼저 녹음을 진행하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        String mImageCaptureUri = data.getStringExtra(android.provider.MediaStore.EXTRA_OUTPUT);
        path = pathToSave();
        String name = pictureTakenTime;

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_CAMERA: {

                    final Bundle extras = data.getExtras();

                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
                        cameraPicture.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌

                        try {
                            String dirPath =  Environment.getExternalStorageDirectory() + "/Lecoder";
                            File file = new File(dirPath+"/"+path+"/"+name);
                            FileOutputStream fos = new FileOutputStream(file);
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                            Toast.makeText(getApplicationContext(), "사진 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("카메라", "저장오류");
                        }
                        break;

                    }
                }
            }
        }
    }

    private void updateRecentList() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String date = formatter.format(new Date(System.currentTimeMillis()));
        if (recordType.equals("Fast")) {
            if (fastArrayList.size() > 3) {
                fastArrayList.remove(0);
            }
            fastArrayList.add(new RecordListItem(currentTime, date, timer.getText().toString()));
            setItemArrayPref(fastArrayList, "fastList", fastShref);
        } else {
            if (lectureArrayList.size() > 5) {
                lectureArrayList.remove(0);
            }
            int count = 0;
            for (RecordListItem item : lectureArrayList) {
                if (item.recordClass.equals(recordClass)) {
                    count++;
                }
            }
            lectureArrayList.add(new RecordListItem(String.valueOf(count), currentTime, date, timer.getText().toString(), recordClass));
            setItemArrayPref(lectureArrayList, "lectureList", lecShref);
        }
    }

    private void openFragment(int fragmentNum) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        frameLayout.setVisibility(View.VISIBLE);
        switch (fragmentNum) {
            case MEMO:
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                transaction.replace(R.id.fragment_container, memoFragment);
                transaction.commit();
                break;

            case DRAWING:
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                transaction.replace(R.id.fragment_container, drawingFragment);
                transaction.commit();
                break;
        }
    }

    private void closeFragment(int fragmentNum) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        frameLayout.setVisibility(View.INVISIBLE);
        switch (fragmentNum) {
            case MEMO:
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                transaction.replace(R.id.fragment_container, memoFragment);
                transaction.commit();
                break;

            case DRAWING:
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                transaction.replace(R.id.fragment_container, drawingFragment);
                transaction.commit();
                break;
        }
    }

    private class UpdateTimer extends Thread {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        long time = 0;

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (isRecording) {
                        time += 1000;
                        Log.d("TESTTIME", String.valueOf(time));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer.setText(formatter.format(new Date(time)));
                            }
                        });
                    } else {
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String pathToSave() {
        String path;
        if (recordType.equals("Fast")) {
            path = "빠른녹음/" + currentTime;
        } else {
            path = recordType + "/" + recordClass + "/" + currentTime;
        }
        return path;
    }

    public String getSaveTime() {
        return timer.getText().toString();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setItemArrayPref(ArrayList<RecordListItem> values, String key, SharedPreferences shref) {
        Gson gson = new Gson();
        String json = gson.toJson(values);

        editor = shref.edit();
        editor.remove(key).commit();
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<RecordListItem> getItemArrayPref(SharedPreferences shref, String key) {
        Gson gson = new Gson();
        String response = shref.getString(key, "");
        return gson.fromJson(response, new TypeToken<List<RecordListItem>>() {
        }.getType());
    }


}



