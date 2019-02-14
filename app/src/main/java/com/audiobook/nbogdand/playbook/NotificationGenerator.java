package com.audiobook.nbogdand.playbook;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.data.Songs;

import static com.audiobook.nbogdand.playbook.PlaySongActivity.CHANNEL_ID;

public class NotificationGenerator {

    private static Notification mNotification;

    public static Notification createNotification(Context context, Song playingSong, String nextState){

        Intent notificationIntent = new Intent(context, PlaySongActivity.class);
        notificationIntent.setAction(Constants.OPEN_NOTIFICATION);
        notificationIntent.putExtra(Constants.PLAYING_SONG,playingSong);
        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                                                .addNextIntentWithParentStack(notificationIntent)
                                                .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.big_notification);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("PlayBook")
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setSound(null,0);

        notification.getBigContentView().setTextViewText(R.id.song_title_notif,playingSong.getTitle());
        if(nextState.equals(Constants.PLAY_STATE)){
            notification.getBigContentView().setImageViewResource(R.id.notif_pause,R.drawable.play);
            notification.setOngoing(false);
        }

        if(nextState.equals(Constants.PAUSE_STATE)){
            notification.getBigContentView().setImageViewResource(R.id.notif_pause,R.drawable.pause);
            notification.setOngoing(false);
        }

        mNotification = notification.build();

        setListeners(notificationLayout,context,playingSong);

        return mNotification;
    }


    private static void setListeners(RemoteViews view, Context context,Song playingSong){

        Intent pause = new Intent();
        Intent minus = new Intent();
        Intent plus = new Intent();
        Intent stop = new Intent();

        pause.putExtra(Constants.PLAYING_SONG,playingSong);

        pause.setAction(Constants.NOTIFY_PAUSE);
        minus.setAction(Constants.NOTIFY_MINUS);
        plus.setAction(Constants.NOTIFY_PLUS);
        stop.setAction(Constants.STOP_FOREGROUND_SERVICE);

        // Setting up the pendingIntent for buttons in notification
        // layout to do the appropriate action (rewind 30s,pause,fastforward 30s)
        PendingIntent pausePending = PendingIntent.getBroadcast(context,0,pause,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_pause,pausePending);

        PendingIntent minusPending = PendingIntent.getBroadcast(context,0,minus,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_minus30,minusPending);

        PendingIntent plusPending = PendingIntent.getBroadcast(context,0,plus,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notif_plus30,plusPending);

        PendingIntent stopPending = PendingIntent.getBroadcast(context,0,stop,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.stopService,stopPending);

    }


}
