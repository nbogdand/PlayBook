package com.audiobook.nbogdand.playbook.BroadcastReciever;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.Constants;
import com.audiobook.nbogdand.playbook.NotificationGenerator;
import com.audiobook.nbogdand.playbook.PlaySongActivity;
import com.audiobook.nbogdand.playbook.PlaySongViewModel;
import com.audiobook.nbogdand.playbook.R;
import com.audiobook.nbogdand.playbook.Services.AudioService;

import static com.audiobook.nbogdand.playbook.PlaySongActivity.CHANNEL_ID;

public class NotificationBroadcast extends BroadcastReceiver {


    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(AudioService.NOTIFY_PAUSE)){
            Log.i("Broadcastxx:: ","pause");

            mediaPlayer = PlaySongViewModel.getMediaPlayer();
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            String songName = intent.getStringExtra("songName");

 //           RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.big_notification);
//            notificationLayout.setImageViewResource(R.id.notif_pause,R.drawable.play);

            // It will get an error if call
            // any method on null reference
            if(mediaPlayer != null) {

                // If music is playing, stop it
                if(mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();
                    //Change to play button in notification
                    Notification notification = NotificationGenerator.createNotification(context,songName,Constants.PLAY_STATE);
                    notificationManager.notify(Constants.NOTIFICATION_ID,notification);

                    // If is not playing, start playing :))
                }else if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();

                    //Change to pause button in notification
                    Notification notification = NotificationGenerator.createNotification(context,songName,Constants.PAUSE_STATE);
                    notificationManager.notify(Constants.NOTIFICATION_ID,notification);
                }
            }
/*
            Intent notificationIntent = new Intent(context, PlaySongActivity.class);
            notificationIntent.setAction(Constants.ACTION_MAIN_ACTIVITY);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0, notificationIntent,0);


            NotificationCompat.Builder mNewNotification = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("PlayBook")
                    .setTicker("PlayBook player")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayout)
                    // It means it can not be dismissed unless i do so
                    .setOngoing(true)
                    .setSound(null,0);

            mNewNotification.getBigContentView().setTextViewText(R.id.song_title_notif,songName);

            AudioService.setListeners(notificationLayout,context,songName);

            Notification notification = mNewNotification.build();

*/
        }

        if(intent.getAction().equals(AudioService.NOTIFY_MINUS)){
            Log.i("Broadcastxx:: ","minus");
            mediaPlayer = PlaySongViewModel.getMediaPlayer();
            int skipTime = 30 * 1000; // skip 30s

            if(mediaPlayer != null){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - skipTime);
            }

        }

        if(intent.getAction().equals(AudioService.NOTIFY_PLUS)){
            Log.i("Broadcastxx:: ","plus");
            mediaPlayer = PlaySongViewModel.getMediaPlayer();
            int skipTime = 30 * 1000; // skip 30s

            if(mediaPlayer != null){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + skipTime);
            }
        }


    }
}
