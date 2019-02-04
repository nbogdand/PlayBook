package com.audiobook.nbogdand.playbook;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import static com.audiobook.nbogdand.playbook.PlaySongActivity.CHANNEL_ID;
import static com.audiobook.nbogdand.playbook.Services.AudioService.NOTIFY_MINUS;
import static com.audiobook.nbogdand.playbook.Services.AudioService.NOTIFY_PAUSE;
import static com.audiobook.nbogdand.playbook.Services.AudioService.NOTIFY_PLUS;

public class NotificationGenerator {

    private static Notification mNotification;

    public static Notification createNotification(Context context,String songName,String nextState){

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION_MAIN_ACTIVITY);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, notificationIntent,0);

        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.big_notification);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("PlayBook")
                .setTicker("PlayBook player")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setSound(null,0);

        notification.getBigContentView().setTextViewText(R.id.song_title_notif,songName);

        if(nextState.equals(Constants.PLAY_STATE)){
            notification.getBigContentView().setImageViewResource(R.id.notif_pause,R.drawable.play);
            notification.setOngoing(false);
        }

        mNotification = notification.build();

        setListeners(notificationLayout,context,songName);

        return mNotification;
    }


    private static void setListeners(RemoteViews view, Context context,String songName){

        Intent pause = new Intent();
        Intent minus = new Intent();
        Intent plus = new Intent();

        pause.putExtra("songName",songName);

        pause.setAction(NOTIFY_PAUSE);
        minus.setAction(NOTIFY_MINUS);
        plus.setAction(NOTIFY_PLUS);

        // Setting up the pendingIntent for buttons in notification
        // layout to do the appropriate action (rewind 30s,pause,fastforward 30s)
        PendingIntent pausePending = PendingIntent.getBroadcast(context,0,pause,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_pause,pausePending);

        PendingIntent minusPending = PendingIntent.getBroadcast(context,0,minus,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_minus30,minusPending);

        PendingIntent plusPending = PendingIntent.getBroadcast(context,0,plus,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_plus30,plusPending);

    }


}
