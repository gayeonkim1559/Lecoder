package com.lecoder.team9.lecoder;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.jackandphantom.circularprogressbar.CircleProgressbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    CircleImage circleImage;
    CircleProgressbar circleProgressbar;
    ImageButton playBtn, pauseBtn, gridBtn, addBtn;
    TextView subjectDate,subjectClass,playingTime,totalTime,subjectName;
    TextView dialogText,dialogTitle;
    TextView showText, showTextPage;
    ImageView dialogImage;
    RelativeLayout relativeLayout;
    MediaPlayer mediaPlayer;
    ProgressMovement progressMovement;
    String pathToLecoder;
    float progressTime;
    Dialog dialog;
    Intent intent;
    List<String> pictureNameList=new ArrayList<>();
    List<Integer> pictureTimeList=new ArrayList<>();
    List<String> textNameList=new ArrayList<>();
    List<Integer> textTimeList=new ArrayList<>();
    boolean isPlaying=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mediaPlayer = new MediaPlayer();
        pathToLecoder=Environment.getExternalStorageDirectory().getPath()+"/LecordingTest";//LecordingTest -> Lecorder

        relativeLayout= (RelativeLayout) findViewById(R.id.playActivityContainer);
        gridBtn = (ImageButton) findViewById(R.id.gridBtn);
        addBtn = (ImageButton) findViewById(R.id.addBtn);
        playingTime= (TextView) findViewById(R.id.time_playing);
        totalTime= (TextView) findViewById(R.id.time_total);
        subjectDate= (TextView) findViewById(R.id.play_subjectDate);
        subjectClass = (TextView) findViewById(R.id.play_subjectClass);
        subjectName= (TextView)findViewById(R.id.play_subjectName);
        showText = (TextView) findViewById(R.id.textShow);
        showTextPage = (TextView) findViewById(R.id.textShowPage);
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.play_dialog);
        dialogTitle=dialog.findViewById(R.id.playTitle);
        dialogText=dialog.findViewById(R.id.textDialog);
        dialogImage=dialog.findViewById(R.id.imageDialog);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, getResources().getDisplayMetrics());


        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = height;
        dialog.getWindow().setAttributes(params);

        intent=getIntent();

        playBtn = (ImageButton) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        pauseBtn = (ImageButton) findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(this);

        File directory=new File(pathToLecoder+"/Day1");
        File[] files=directory.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return file.getName().compareToIgnoreCase(t1.getName());
            }
        });
        for (int i=0;i<files.length;i++){
            String fileName=files[i].getName();
            Log.e("[TEST-LIST] ",fileName);
            if (fileName.substring(0,3).equals("img")){
                pictureNameList.add(fileName);
                int min=Integer.valueOf(fileName.substring(3,5));
                int sec=Integer.valueOf(fileName.substring(6,8));
                int setTime=(min*60+sec)*1000;
                pictureTimeList.add(setTime);
                Log.e("[TEST-LIST] ",fileName+":"+setTime);
            }
            if (fileName.substring(0,3).equals("txt")){
                textNameList.add(fileName);
                int min=Integer.valueOf(fileName.substring(3,5));
                int sec=Integer.valueOf(fileName.substring(6,8));
                int setTime=(min*60+sec)*1000;
                textTimeList.add(setTime);
                Log.e("[TEST-LIST] ",fileName+":"+setTime);
            }
        }
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        circleImage = (CircleImage) findViewById(R.id.circleImage);
        circleProgressbar = (CircleProgressbar) findViewById(R.id.circleProgressbar);
        circleProgressbar.setOnProgressbarChangeListener(new CircleProgressbar.OnProgressbarChangeListener() {
            @Override
            public void onProgressChanged(CircleProgressbar circleSeekbar, float progress, boolean fromUser) {

            }

            @Override
            public void onStartTracking(CircleProgressbar circleSeekbar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTracking(CircleProgressbar circleSeekbar) {
                mediaPlayer.seekTo((int) circleSeekbar.getProgress());
                if (circleSeekbar.getProgress()>0&&playBtn.getVisibility()==View.INVISIBLE){
                    mediaPlayer.start();
                }
            }
        });
        playLecture("/sample123.mp3","/Day1");// Day1 -> 과목이름
        progressMovement=new ProgressMovement();
        progressMovement.start();


    }



    public void playLecture(String fileName,String filePath) {
        try {
            String playName=intent.getStringExtra("playName");
            String playDate=intent.getStringExtra("playDate");
            String playClass=intent.getStringExtra("playClass");
            if (playClass.equals("")){
                playClass="빠른녹음";
            }
            circleProgressbar.setProgress(0);
            String uri = pathToLecoder+filePath;
            subjectClass.setText(playClass);
            subjectName.setText(playName);
            subjectDate.setText(playDate);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(uri+fileName);
            mediaPlayer.prepare();
            circleProgressbar.setMaxProgress(mediaPlayer.getDuration());
            mediaPlayer.start();
            if (mediaPlayer.isPlaying()) {
                playBtn.setVisibility(View.INVISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
            } else {
                pauseBtn.setVisibility(View.INVISIBLE);
                playBtn.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.playBtn:
                pauseBtn.setVisibility(View.VISIBLE);
                playBtn.setVisibility(View.INVISIBLE);
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();

                break;
            case R.id.pauseBtn:
                pauseBtn.setVisibility(View.INVISIBLE);
                playBtn.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;
        }

    }

    private class ProgressMovement extends Thread{
        @Override
        public void run() {
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
            String dateString;
            Bitmap bitmap;
            int pictureListSize=pictureTimeList.size();
            int textListSize=textTimeList.size();
            int i=0,j=0;
            totalTime.setText(formatter.format(new Date((long) mediaPlayer.getDuration())));
            while (isPlaying){
                try {
                    Thread.sleep(100);
                    progressTime=mediaPlayer.getCurrentPosition();
                    if (mediaPlayer!=null){
                        dateString = formatter.format(new Date((long) progressTime));
                        playingTime.setText(dateString);
                        circleProgressbar.setProgress(progressTime);
                        if (i<pictureListSize){
                            if (pictureTimeList.get(i)<progressTime){
                                bitmap = BitmapFactory.decodeFile(pathToLecoder +"/Day1/"+pictureNameList.get(i));
                                circleImage.setImageBitmap(bitmap);
                                Log.e("[TEST-LIST] ","플레이시간 : "+progressTime+", 사진시간 : "+pictureTimeList.get(i));
                                Log.e("[TEST-LIST] ",i+"번째 사진파일"+pictureNameList.get(i));
                                dialogTitle.setText("참고자료 "+i);
                                showText.setText("");
                                showTextPage.setText("");
                                dialogImage.setVisibility(View.VISIBLE);
                                dialogText.setVisibility(View.INVISIBLE);
                                dialogImage.setImageBitmap(bitmap);
                                i++;
                            }
                        }
                        if (j<textListSize){
                            Log.e("[TEST-LIST] 텍스트=",textTimeList.get(j)+"::"+progressTime);
                            if (textTimeList.get(j)<progressTime){
                                Log.e("[TEST-LIST] ",j+"번째 텍스트파일"+textTimeList.get(j));
                                circleImage.setImageResource(R.drawable.text_backg);
                                String[] readText=readTextFile(textNameList.get(j));
                                String title="Page"+readText[0];
                                String text=readText[1];
                                Log.e("[TEST-LIST] ",title+":"+title.length());
                                Log.e("[TEST-LIST] ",text);
//                                showText.setEllipsize(null);
//                                showText.setMaxLines(Integer.MAX_VALUE);
                                if (readText!=null){
                                    showTextPage.setText(title);
                                    dialogTitle.setText(title);
                                    dialogImage.setVisibility(View.INVISIBLE);
                                    dialogText.setVisibility(View.VISIBLE);
                                    Log.e("[TEST-LIST] ",j+"번째 텍스트파일수정");
                                    if (text.equals("")){//페이지정보만 있는 경우
                                        showText.setText("");
                                        dialogText.setVisibility(View.INVISIBLE);
                                    }else
                                        showText.setText(text);
//                                        showText.setEllipsize(TextUtils.TruncateAt.END);
//                                        showText.setMaxLines(3);
                                        dialogText.setText(text);
                                }
                                j++;
                            }
                        }
                        relativeLayout.postInvalidate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String[] readTextFile(String fileName) {
        String[] result= new String[2];
        try {
            FileInputStream stream=new FileInputStream(pathToLecoder+"/Day1/"+fileName);
            BufferedReader reader=new BufferedReader(new InputStreamReader(stream,"euc-kr"));

            String line, page,text="";
            page=reader.readLine();//페이지정보
            result[0]=page;
            while ((line=reader.readLine())!=null)
            {
                text+=line;
            }
            result[1]=text;

        } catch (Exception e) {
            e.printStackTrace();
            result=null;
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying=false;
        if (mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
