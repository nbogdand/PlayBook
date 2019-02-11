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


    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Constants.NOTIFY_PAUSE)){

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
            mediaPlayer = AudioService.getMediaPlayer();
            int skipTime = 30 * 1000; // skip 30s

            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - skipTime);
            }

        }

        if(intent.getAction().equals(Constants.NOTIFY_PLUS)){
            mediaPlayer = AudioService.getMediaPlayer();
            int skipTime = 30 * 1000; // skip 30s

            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + skipTime);
            }
        }

        if(intent.getAction().equals(Constants.STOP_FOREGROUND_SERVICE)){
            Log.i("bogdanzzz", "onReceive: ");
            Intent stop = new Intent(context,AudioService.class);
            stop.setAction(Constants.STOP_FOREGROUND_SERVICE);
            context.startService(stop);

        }

    }
}
