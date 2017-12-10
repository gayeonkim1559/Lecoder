package com.lecoder.team9.lecoder;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
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
    TextView gridTitle;
    ListView gridListView;
    ImageView dialogImage;
    RelativeLayout relativeLayout;
    MediaPlayer mediaPlayer;
    RelativeLayout dialougLayout;
    ProgressMovement progressMovement;
    String pathToLecoder;
    GridListAdapter gridListAdapter;
    SimpleDateFormat formatter;
    String playTimeText;
    float progressTime;
    View v;
    Dialog dialog;
    Dialog gridDialog;
    Intent intent;
    int indexPhoto,indexText;
    ArrayList<GridListItem> gridListItems=new ArrayList<>();
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

        //확대 다이얼로그
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.play_dialog);
        dialogTitle=dialog.findViewById(R.id.gridTitle);
        dialogText=dialog.findViewById(R.id.textDialog);
        dialougLayout=dialog.findViewById(R.id.gridDialogLayout);
        dialogText.setMovementMethod(new ScrollingMovementMethod());
        dialogImage=dialog.findViewById(R.id.imageDialog);

        //그리드 다이얼로그
        gridDialog=new Dialog(this);
        gridDialog.setContentView(R.layout.play_grid_dialog);
        gridDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000055));
        gridListView=gridDialog.findViewById(R.id.gridDialogListView);
        gridTitle=gridDialog.findViewById(R.id.gridTitle);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager.LayoutParams gridParams = gridDialog.getWindow().getAttributes();
        final int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, getResources().getDisplayMetrics());
        final int gridHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());

        gridParams.width=WindowManager.LayoutParams.MATCH_PARENT;
        gridParams.height=gridHeight;
        gridDialog.getWindow().setAttributes(gridParams);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = height;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));

        gridListAdapter=new GridListAdapter();
        gridListView.setAdapter(gridListAdapter);
        gridListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String time=gridListItems.get(i).tagTime;
                int min=Integer.valueOf(time.substring(0,2));
                int sec=Integer.valueOf(time.substring(3,5));
                int setTime=(min*60+sec)*1000+50;
                mediaPlayer.seekTo(setTime);
                trackingIndex(mediaPlayer.getCurrentPosition());
                Toast.makeText(getApplicationContext(),time+" 으로 이동합니다.",Toast.LENGTH_SHORT).show();
                gridDialog.dismiss();
            }
        });

        intent=getIntent();
        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridListAdapter.notifyDataSetChanged();
                gridDialog.show();
            }
        });
        playBtn = (ImageButton) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        pauseBtn = (ImageButton) findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(this);


        formatter = new SimpleDateFormat("mm:ss");

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
            String time=fileName.substring(3,8).replace("m",":");
            if (fileName.substring(0,3).equals("img")){
                pictureNameList.add(fileName);
                int min=Integer.valueOf(fileName.substring(3,5));
                int sec=Integer.valueOf(fileName.substring(6,8));
                int setTime=(min*60+sec)*1000;
                pictureTimeList.add(setTime);
                Log.e("[TEST-LIST] ",fileName+":"+setTime);
                Bitmap bitmap = BitmapFactory.decodeFile(pathToLecoder + "/Day1/" + fileName);
                gridListItems.add(new GridListItem(time,bitmap,"참고자료"+i));
            }
            if (fileName.substring(0,3).equals("txt")){
                textNameList.add(fileName);
                int min=Integer.valueOf(fileName.substring(3,5));
                int sec=Integer.valueOf(fileName.substring(6,8));
                int setTime=(min*60+sec)*1000;
                textTimeList.add(setTime);
                Log.e("[TEST-LIST] ",fileName+":"+setTime);
                String[] readText=readTextFile(fileName);
                String title="Page"+readText[0];
                String text=readText[1].trim();
                gridListItems.add(new GridListItem(time,title,text));
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
                trackingIndex(mediaPlayer.getCurrentPosition());
                if (circleSeekbar.getProgress()>0&&playBtn.getVisibility()==View.INVISIBLE){
                    mediaPlayer.start();
                }
            }
        });
        playLecture("/sample123.mp3","/Day1");// Day1 -> 과목이름
        progressMovement=new ProgressMovement();
        progressMovement.start();


        Collections.sort(gridListItems, new Comparator<GridListItem>() {
            @Override
            public int compare(GridListItem gridListItem, GridListItem t1) {
                return gridListItem.getTagTime().compareTo(t1.getTagTime());
            }
        });



    }
    private void trackingIndex(int time) {
        indexPhoto =0;
        indexText=0;
        int picSize=pictureTimeList.size();
        int textSize=textTimeList.size();
        int i=0;
        int j=0;
        while (pictureTimeList.get(i)<time){
            indexPhoto++;
            i++;
            if (i==picSize){
                break;
            }
        }
        while (textTimeList.get(j)<time){
            indexText++;
            j++;
            if (j==textSize){
                break;
            }
        }
        if (indexPhoto==0&&indexText==0){
            circleImage.setImageResource(R.drawable.text_backg);
            showText.setText("");
            showTextPage.setText("");
            dialogImage.setVisibility(View.INVISIBLE);
            dialogText.setVisibility(View.INVISIBLE);
        }else{
            int prevPhotoIndex=indexPhoto-1;
            int prevTextIndex=indexText-1;
            int timePicture;
            int timeText;
            if (prevPhotoIndex<0){
                timePicture=0;
                timeText=textTimeList.get(prevTextIndex);
            }else if(prevTextIndex<0){
                timePicture=pictureTimeList.get(prevPhotoIndex);
                timeText=0;
            }else {
                timePicture=pictureTimeList.get(prevPhotoIndex);
                timeText=textTimeList.get(prevTextIndex);
            }
            if (timePicture>timeText){
                Bitmap bitmap = BitmapFactory.decodeFile(pathToLecoder + "/Day1/" + pictureNameList.get(prevPhotoIndex));
                circleImage.setImageBitmap(bitmap);
                dialogTitle.setText("참고자료 "+ prevPhotoIndex);
                showText.setText("");
                showTextPage.setText("");
                dialogImage.setVisibility(View.VISIBLE);
                dialogText.setVisibility(View.INVISIBLE);
                dialogImage.setImageBitmap(bitmap);
            }else {
                circleImage.setImageResource(R.drawable.text_backg);
                String[] readText=readTextFile(textNameList.get(prevTextIndex));
                String title="Page"+readText[0];
                String text=readText[1];
                if (readText!=null){
                    showTextPage.setText(title);
                    dialogTitle.setText(title);
                    dialogImage.setVisibility(View.INVISIBLE);
                    dialogText.setVisibility(View.VISIBLE);
                    if (text.equals("")){//페이지정보만 있는 경우
                        showText.setText("");
                        dialogText.setVisibility(View.INVISIBLE);
                    }else
                        showText.setText(text);
                        dialogText.setText(text);
                }
            }
        }


        Log.e("TIME_IndexPhoto", String.valueOf(indexPhoto));
        Log.e("TIME_IndexText", String.valueOf(indexText));
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
            Bitmap bitmap;
            int pictureListSize=pictureTimeList.size();
            int textListSize=textTimeList.size();
            indexPhoto =0;
            indexText=0;
            totalTime.setText(formatter.format(new Date((long) mediaPlayer.getDuration())));
            while (isPlaying){
                try {
                    Thread.sleep(100);
                    progressTime=mediaPlayer.getCurrentPosition();
                    if (mediaPlayer!=null){
                        playTimeText = formatter.format(new Date((long) progressTime));
                        playingTime.setText(playTimeText);
                        circleProgressbar.setProgress(progressTime);
                        if (indexPhoto <pictureListSize){
                            if (pictureTimeList.get(indexPhoto)<progressTime){
                                bitmap = BitmapFactory.decodeFile(pathToLecoder +"/Day1/"+pictureNameList.get(indexPhoto));
                                circleImage.setImageBitmap(bitmap);
                                Log.e("[TEST-LIST] ","플레이시간 : "+progressTime+", 사진시간 : "+pictureTimeList.get(indexPhoto));
                                Log.e("[TEST-LIST] ", indexPhoto +"번째 사진파일"+pictureNameList.get(indexPhoto));
                                dialogTitle.setText("참고자료 "+ indexPhoto);
                                showText.setText("");
                                showTextPage.setText("");
                                dialogImage.setVisibility(View.VISIBLE);
                                dialogText.setVisibility(View.INVISIBLE);
                                dialogImage.setImageBitmap(bitmap);
                                indexPhoto++;
                            }
                        }
                        if (indexText<textListSize){
                            Log.e("[TEST-LIST] 텍스트=",textTimeList.get(indexText)+"::"+progressTime);
                            if (textTimeList.get(indexText)<progressTime){
                                Log.e("[TEST-LIST] ",indexText+"번째 텍스트파일"+textTimeList.get(indexText));
                                circleImage.setImageResource(R.drawable.text_backg);
                                String[] readText=readTextFile(textNameList.get(indexText));
                                String title="Page"+readText[0];
                                String text=readText[1];
                                Log.e("[TEST-LIST] ",title+":"+title.length());
                                Log.e("[TEST-LIST] ",text);
                                if (readText!=null){
                                    showTextPage.setText(title);
                                    dialogTitle.setText(title);
                                    dialogImage.setVisibility(View.INVISIBLE);
                                    dialogText.setVisibility(View.VISIBLE);
                                    Log.e("[TEST-LIST] ",indexText+"번째 텍스트파일수정");
                                    if (text.equals("")){//페이지정보만 있는 경우
                                        showText.setText("");
                                        dialogText.setVisibility(View.INVISIBLE);
                                    }else
                                        showText.setText(text);
                                        dialogText.setText(text);
                                }
                                indexText++;
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
                text+="\n"+line;
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

    private class GridListAdapter extends BaseAdapter{

        public void addItem(GridListItem item) {
            gridListItems.add(item);
        }
        @Override
        public int getCount() {
            return gridListItems.size();
        }

        @Override
        public GridListItem getItem(int i) {
            return gridListItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            GridListView gridListView=new GridListView(getApplicationContext());
            GridListItem listItem=gridListItems.get(i);
            gridListView.setTagTime(listItem.getTagTime());
            if (listItem.getBitmapImg()==null){
                gridListView.setTitleIfText(listItem.getTitleIfText());
            }else {
                gridListView.setBitmapImg(listItem.getBitmapImg());
            }
            gridListView.setDescription(listItem.getDescription());
            return gridListView;
        }
    }
}
