package com.lecoder.team9.lecoder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.lecoder.team9.lecoder.RecordActivity.order;

public class RecordService extends Service {
    final private  static File RECORDED_FILE = Environment.getExternalStorageDirectory();

    String dirPath;
    String filePath;

    static String savePath;
    String fileName;
    MediaRecorder recorder;

    boolean isRecording = false;
    //int order;

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
//        isRecording = intent.getExtras().getBoolean("isRecording");

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
        unifyFiles();

        super.onDestroy();
    }

    public void startRecording() {
        Log.d("레코드 서비스", "startRecording() 메소드");

        dirPath = RECORDED_FILE.getAbsolutePath() + "/Lecoder";
        filePath= dirPath +"/"+savePath+"/"+fileName;
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

    public void unifyFiles() {
        Log.d("레코드 서비스", "unifyFiles() 메소드");

        //getOrder();
        File path = new File(dirPath +"/"+savePath);

        if (!path.exists()) {
            Log.e("Record Service", "경로 오류");
        }

        //해당 경로에 있는 mp4 파일들의 이름
        String[] files = path.list();

        for (String file : files) {
            Log.d("파일 목록", file);
        }

        Log.d("레코드 서비스", "녹음 파일 개수 : " + files.length);

        //여러 녹음본을 하나로 합치기
        if (files.length > 1) {
            Log.d("레코드 서비스", "녹음본 합치기!");
            Log.d("레코드 서비스", "order : " + order);

            String outputFile = dirPath + "/" + savePath + "/Output" + order + ".mp4";

            Movie[] inMovies = new Movie[2];
            try {
                if (order >= 1)
                    inMovies = new Movie[]{MovieCreator.build(dirPath + "/" + savePath + "/" + files[1]), MovieCreator.build(dirPath + "/" + savePath + "/" + files[0])};
                else
                    inMovies = new Movie[]{MovieCreator.build(dirPath + "/" + savePath + "/" + files[0]), MovieCreator.build(dirPath + "/" + savePath + "/" + files[1])};
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Track> audioTracks = new LinkedList<Track>();
            for (Movie m : inMovies) {
                for (Track track : m.getTracks()) {
                    if (track.getHandler().equals("soun")) {
                        audioTracks.add(track);
                    }
                }
            }

            Movie output = new Movie();
            if (audioTracks.size() > 0) {
                try {
                    output.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Container out = new DefaultMp4Builder().build(output);
            FileChannel fc = null;
            try {
                fc = new FileOutputStream(new File(outputFile)).getChannel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                out.writeContainer(fc);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                fc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("레코드 서비스", "합치고 난 후의 목록");
            String[] temp = path.list();

            for (String file : temp) {
                Log.d("파일 목록", file);
            }

            //합치기 전의 파일들 삭제하기
            for (String file : files) {
                try {
                    File f = new File(dirPath + "/" + savePath + "/" + file);
                    if (!file.equals("Output" + order + ".mp4")) {
                        if (f.delete())
                            Log.d("레코드 서비스", "파일삭제 성공");
                        else
                            Log.d("레코드 서비스", "파일삭제 실패");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            order++;
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