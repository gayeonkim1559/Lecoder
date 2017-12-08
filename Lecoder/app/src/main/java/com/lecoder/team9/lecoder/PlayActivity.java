package com.lecoder.team9.lecoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.jackandphantom.circularprogressbar.CircleProgressbar;

import java.io.File;
import java.io.IOException;
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
    TextView subjectInfo,playingTime,totalTime;
    RelativeLayout relativeLayout;
    MediaPlayer mediaPlayer;
    ProgressMovement progressMovement;
    String pathToLecoder;
    float progressTime;
    List<String> pictureNameList=new ArrayList<>();
    List<Integer> pictureTimeList=new ArrayList<>();
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
        subjectInfo = (TextView) findViewById(R.id.play_subjectInfo);

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
        }
        //TODO 사진이름은 저장했고 이걸 시간에 맞춰서 쓰레드에 넣어 보여줘야함.

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
                if (circleSeekbar.getProgress()>0&&playBtn.getVisibility()==View.GONE){
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

            circleProgressbar.setProgress(0);
            String uri = pathToLecoder+filePath;
            subjectInfo.setText(fileName);
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
            int size=pictureTimeList.size();
            int i=0;
            totalTime.setText(formatter.format(new Date((long) mediaPlayer.getDuration())));
            while (isPlaying){
                try {
                    Thread.sleep(100);
                    progressTime=mediaPlayer.getCurrentPosition();
                    if (mediaPlayer!=null){
                        dateString = formatter.format(new Date((long) progressTime));
                        playingTime.setText(dateString);
                        circleProgressbar.setProgress(progressTime);
                        if (i<size){
                            if (pictureTimeList.get(i)<progressTime){
                                bitmap = BitmapFactory.decodeFile(pathToLecoder +"/Day1/"+pictureNameList.get(i));
                                circleImage.setImageBitmap(bitmap);
                                Log.e("[TEST-LIST] ","플레이시간 : "+progressTime+", 사진시간 : "+pictureTimeList.get(i));
                                Log.e("[TEST-LIST] ",i+"번째 사진파일"+pictureNameList.get(i));
                                i++;
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
