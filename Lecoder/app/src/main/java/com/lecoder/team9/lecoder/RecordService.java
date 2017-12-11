package com.lecoder.team9.lecoder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class RecordService extends Service {
    private static final String TAG = "RecordService";
    final private  static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    String savePath,fileName;

    MediaRecorder recorder;

    public RecordService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "서비스 호출되었습니다!!!!!!!");
        super.onCreate();
        //filename = RECORDED_FILE.getAbsolutePath()+"/Lecoder/test.mp4";
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "서비스 호출되었습니다222222!!!!!!!");
        savePath=intent.getStringExtra("savePath");
        fileName="recorded.mp4";
        if (intent == null) {
            return Service.START_STICKY;
        }
        else {
            startRecording();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "서비스 종료!!!!!!!!!!!!");

        stopRecording();
        super.onDestroy();
    }

    public void startRecording() {
        Log.d("RecordService","StartRecording!!!!!!!!!!!!!!!!!");
        String dirPath = RECORDED_FILE.getAbsolutePath() + "/Lecoder";

        String filePath= dirPath +"/"+savePath+"/"+fileName;
        File file = new File(dirPath +"/"+savePath);
        if (!file.exists())
            file.mkdirs();
//        String filePath = dirPath +"/sibal.mp4";
//        Log.d("RecordService",aa);
        Log.d("RecordServicaaaaafasde",filePath);

        //String filePath = RECORDED_FILE.getAbsolutePath() +  File.separator  + Environment.DIRECTORY_DCIM + File.separator + "test.mp4";

        Log.d(TAG, filePath);
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