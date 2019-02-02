package com.audiobook.nbogdand.playbook.BroadcastReciever;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.PlaySongViewModel;
import com.audiobook.nbogdand.playbook.Services.AudioService;

public class NotificationBroadcast extends BroadcastReceiver {


    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(AudioService.NOTIFY_PAUSE)){
            Log.i("Broadcastxx:: ","pause");

            mediaPlayer = PlaySongViewModel.getMediaPlayer();

            // It will get an error if call
            // any method on null reference
            if(mediaPlayer != null) {
                // If music is playing, stop it
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }else if (!mediaPlayer.isPlaying()){
                // If is not playing, start playing :))
                mediaPlayer.start();
            }
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
