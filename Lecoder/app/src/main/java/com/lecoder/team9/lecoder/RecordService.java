package com.lecoder.team9.lecoder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class RecordService extends Service {
    final private  static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    private boolean isFirst = true;

    String savePath,fileName;
    MediaRecorder recorder;

    public RecordService() {
    }

    @Override
    public void onCreate() {
        Log.d("레코드 서비스", "onCreat() 메소드");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("레코드 서비스", "onBind() 메소드");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("레코드 서비스", "onStartCommand() 메소드");

            if (intent == null) {
                return Service.START_STICKY;
            }
            else {
                savePath=intent.getStringExtra("savePath");
                fileName = intent.getExtras().getInt("fileName") + ".mp4";
                startRecording();
            }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("레코드 서비스", "onDestroy() 메소드");

        stopRecording();
        super.onDestroy();
    }

    public void startRecording() {
        Log.d("레코드 서비스", "startRecording() 메소드");

        String dirPath = RECORDED_FILE.getAbsolutePath() + "/Lecoder";

        String filePath= dirPath +"/"+savePath+"/"+fileName;
        File file = new File(dirPath +"/"+savePath);

        if (!file.exists())
            file.mkdirs();

        if (recorder == null)
            recorder = new MediaRecorder();
        else
            recorder.reset();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
            recorder.start();

        }catch (Exception e) {
            Log.e("Recording Error", "녹음오류");
        }
    }

    public void stopRecording() {
        if (recorder == null)
            return;

        //stop recording
        recorder.stop();

        //release memories used to record
        recorder.release();
        recorder = null;
    }

}