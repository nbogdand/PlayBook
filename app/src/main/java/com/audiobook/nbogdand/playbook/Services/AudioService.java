package com.audiobook.nbogdand.playbook.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.Constants;
import com.audiobook.nbogdand.playbook.NotificationGenerator;
import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.data.Songs;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener {

    private static MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;

    public static final String TAG = "FOREGROUND SERVICE:";
    private static Song playingSong;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction() != null) {
            if (intent.getAction().equals(Constants.START_FOREGROUND_SERVICE)) {
                Log.i(Constants.LOGGER_TAG, "foreground service started");

                intent.setExtrasClassLoader(Song.class.getClassLoader());
                playingSong = intent.getParcelableExtra(Constants.PLAYING_SONG);

                //Song is already playing, so next action would be pause --> PAUSE_STATE
                Notification notif = null;
                if (playingSong != null) {
                    notif = NotificationGenerator.createNotification(getApplicationContext(), playingSong, Constants.PAUSE_STATE);
                }

                playSong(getApplicationContext(), playingSong.getSongPath());

                // Id is an identifier for notification
                if (notif != null)
                    startForeground(Constants.NOTIFICATION_ID, notif);
                Log.i("bogdanzzz",playingSong.getTitle());

            } else
                // Current state is paused
                if (intent.getAction().equals(Constants.PAUSE_FOREGROUND_SERVICE)) {
                    pauseSong();

                } else if (intent.getAction().equals(Constants.REPLAY_FOREGROUND_SERVICE)) {
                    pauseSong();
                } else if (intent.getAction().equals(Constants.STOP_FOREGROUND_SERVICE)) {
                    Log.i(TAG, "Stop Fservice: ");
                    stopSong();
                    stopForeground(false);
                    // stopSelf();
                }
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        notificationManager = getApplicationContext().getSystemService(NotificationManager.class);

        Notification notification = NotificationGenerator.createNotification(getApplicationContext(),
                                                                    playingSong,
                                                                    Constants.PAUSE_STATE);
        notificationManager.notify(Constants.NOTIFICATION_ID,notification);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Songs songs = Songs.getInstance();
                List<Song> songsList = songs.getSongsList();

                // Update the position of the current playing song to the next one
                songs.setSelectedPosition(songs.getSelectedPosition()+1);

                // See if there is a next song
                if(songsList.get(songs.getSelectedPosition()) != null) {

                    Intent playNextSong = new Intent(getApplicationContext(),AudioService.class);
                    playNextSong.setAction(Constants.START_FOREGROUND_SERVICE);
                    playNextSong.putExtra(Constants.PLAYING_SONG,songsList.get(songs.getSelectedPosition()));
                    getApplicationContext().startService(playNextSong);


                }else {
                    // If there is no next song, set the current playing position to the next song
                    songs.setSelectedPosition(0);
                }
            }
        });

    }

    public void playSong(Context applicationContext, String songPath){

        Uri songUri = null;
        if(songPath != null) {
            songUri = Uri.fromFile(new File(songPath));
        }
        if (mediaPlayer == null && songUri != null) {
            mediaPlayer = new MediaPlayer();

            try {

                //preparing mediaPlayer
                mediaPlayer.setDataSource(applicationContext, songUri);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Ups! Something went wrong :(",Toast.LENGTH_LONG).show();
            }
        } else {
            // stop the current instance of mediaplayer
            // then recall the playSong method to start/play the song from the beginning
            if(mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = null;
            if(songPath!=null)
                playSong(applicationContext,songPath);
        }
    }

    public void pauseSong(){

        notificationManager = getApplicationContext().getSystemService(NotificationManager.class);

        // If mediaPlayer is stopped and then pressed pause
        // the app will crash
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Notification notification = NotificationGenerator.createNotification(getApplicationContext(),
                                                                                playingSong,
                                                                                Constants.PLAY_STATE);
                notificationManager.notify(Constants.NOTIFICATION_ID,notification);
            } else {
                mediaPlayer.start();
                Notification notification = NotificationGenerator.createNotification(getApplicationContext(),
                                                                                playingSong,
                                                                                Constants.PAUSE_STATE);
                notificationManager.notify(Constants.NOTIFICATION_ID,notification);
            }
        }
    }

    public void stopSong(){
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static MediaPlayer getMediaPlayer(){return mediaPlayer;}


}
