package com.lecoder.team9.lecoder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class PushAlarmBroadcast extends BroadcastReceiver {
    String INTENT_ACTION=Intent.ACTION_BOOT_COMPLETED;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        String className=intent.getStringExtra("className");
        long diffCurrentAndAlarm=intent.getLongExtra("diff",0);
        Log.e("[test] diff:", String.valueOf(diffCurrentAndAlarm));
        if (diffCurrentAndAlarm<0){
            NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,new Intent(context,PlayActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder=new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.record2);
            builder.setTicker("강의시간 알림");
            builder.setContentTitle("LECORDER");
            builder.setContentText(className+" 수업시간입니다.");
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            Log.e("[test] Called Time : ", "["+className+"]"+String.valueOf(System.currentTimeMillis()));

            builder.addAction(R.drawable.record1, "녹음시작", pendingIntent);
            builder.setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(0,builder.build());
        }else {
            Log.e("[test] 지난 알림 : ",className);
        }

    }
}
