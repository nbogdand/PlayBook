package com.audiobook.nbogdand.playbook.BroadcastReciever;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.audiobook.nbogdand.playbook.Constants;
import com.audiobook.nbogdand.playbook.NotificationGenerator;
import com.audiobook.nbogdand.playbook.Services.AudioService;
import com.audiobook.nbogdand.playbook.data.Song;

public class NotificationBroadcast extends BroadcastReceiver {

    public static NotificationBroadcast mNotificationBroadcast = null;

    private static MediaPlayer mediaPlayer;

    public static NotificationBroadcast getInstance(){
        if(mNotificationBroadcast == null){
            mNotificationBroadcast = new NotificationBroadcast();
        }
        return mNotificationBroadcast;
    }

    private NotificationBroadcast(){}

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Constants.NOTIFY_PAUSE)){

            Log.i("bogdanzzz", "onReceive: notif pause");

            mediaPlayer = AudioService.getMediaPlayer();
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            Song playingSong = (Song) intent.getParcelableExtra(Constants.PLAYING_SONG);

            // It will get an error if call
            // any method on null reference
            if(mediaPlayer != null) {

                // If music is playing, stop it
                if(mediaPlayer.isPlaying()) {

                    //mediaPlayer.pause();
                    //Change to play button in notification

                    Intent pauseService = new Intent(context,AudioService.class);
                    pauseService.setAction(Constants.PAUSE_FOREGROUND_SERVICE);
                    context.startService(pauseService);

                    Notification notification = NotificationGenerator.createNotification(context,playingSong,Constants.PLAY_STATE);
                    notificationManager.notify(Constants.NOTIFICATION_ID,notification);

                    // If is not playing, start playing :))
                }else if (!mediaPlayer.isPlaying()){

                  //  mediaPlayer.start();
                    //Change to pause button in notification
                    Intent serviceIntent = new Intent(context,AudioService.class);
                    serviceIntent.setAction(Constants.PAUSE_FOREGROUND_SERVICE);
                    serviceIntent.putExtra(Constants.PLAYING_SONG,playingSong);
                    context.startService(serviceIntent);

                    Notification notification = NotificationGenerator.createNotification(context,playingSong,Constants.PAUSE_STATE);
                    notificationManager.notify(Constants.NOTIFICATION_ID,notification);


                }
            }

        }

        if(intent.getAction().equals(Constants.NOTIFY_MINUS)){

            Song playingSong = (Song) intent.getParcelableExtra(Constants.PLAYING_SONG);

            Intent minus30sec = new Intent(context,AudioService.class);
            minus30sec.setAction(Constants.NOTIFY_MINUS);
            minus30sec.putExtra(Constants.PLAYING_SONG,playingSong);
            context.startService(minus30sec);

            /*
            mediaPlayer = AudioService.getMediaPlayer();
            int skipTime = 30 * 1000; // skip 30s

            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - skipTime);
            }
            */

        }

        if(intent.getAction().equals(Constants.NOTIFY_PLUS)){

            Song playingSong = (Song) intent.getParcelableExtra(Constants.PLAYING_SONG);

            Intent plus30sec = new Intent(context,AudioService.class);
            plus30sec.setAction(Constants.NOTIFY_PLUS);
            plus30sec.putExtra(Constants.PLAYING_SONG,playingSong);
            context.startService(plus30sec);

            /*
            mediaPlayer = AudioService.getMediaPlayer();
            int skipTime = 30 * 1000; // skip 30s

            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + skipTime);
            }
            */
        }

        if(intent.getAction().equals(Constants.STOP_FOREGROUND_SERVICE)){
            Log.i("bogdanzzz", "onReceive: stop service");
            Intent stop = new Intent(context,AudioService.class);
            stop.setAction(Constants.STOP_FOREGROUND_SERVICE);
            context.startService(stop);

        }

    }
}
