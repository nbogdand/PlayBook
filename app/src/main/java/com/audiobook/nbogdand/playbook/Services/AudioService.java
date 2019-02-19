package com.audiobook.nbogdand.playbook.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.CommonBindingsUtils;
import com.audiobook.nbogdand.playbook.Commons;
import com.audiobook.nbogdand.playbook.Constants;
import com.audiobook.nbogdand.playbook.MainActivity;
import com.audiobook.nbogdand.playbook.NotificationGenerator;
import com.audiobook.nbogdand.playbook.PlaySongActivity;
import com.audiobook.nbogdand.playbook.R;
import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.data.Songs;


import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener{

    private IBinder iBinder = new AudioServiceBinder();
    private Handler handler;
    private int progress, maxValue;
    private boolean songHasBeenChanged, isPaused;

    private static MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;

    public static final String TAG = "FOREGROUND SERVICE:";
    private static Song playingSong;


    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        progress = 0;
        maxValue = 1000;
        isPaused = false;
        songHasBeenChanged = false;
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
                PlaySongActivity.SERVICE_READY_BOOL = true;

                // Id is an identifier for notification
                if (notif != null)
                    startForeground(Constants.NOTIFICATION_ID, notif);


            } else if (intent.getAction().equals(Constants.PAUSE_FOREGROUND_SERVICE)) {
                pauseSong();
            } else if (intent.getAction().equals(Constants.REPLAY_FOREGROUND_SERVICE)) {
                pauseSong();
            } else if(intent.getAction().equals(Constants.NOTIFY_MINUS)){
                minus30sec();
            }else if(intent.getAction().equals(Constants.NOTIFY_PLUS)) {
                plus30sec();
            }else if (intent.getAction().equals(Constants.STOP_FOREGROUND_SERVICE)) {

                Log.i("bogdanzzz", "onStopCommand: STOP service" );
                stopForeground(true);
                stopSong();
                stopSelf();
                progress = 0;

                NotificationManager nm = getApplicationContext().getSystemService(NotificationManager.class);
                nm.cancel(Constants.NOTIFICATION_ID);
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
        return iBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        notificationManager = getApplicationContext().getSystemService(NotificationManager.class);

        Notification notification = NotificationGenerator.createNotification(getApplicationContext(),
                                                                    playingSong,
                                                                    Constants.PAUSE_STATE);
        notificationManager.notify(Constants.NOTIFICATION_ID,notification);

        Commons.setIsPlaying(true);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Songs songs = Songs.getInstance();
                List<Song> songsList = songs.getSongsList();

                // Update the position of the current playing song to the next one
                songs.setSelectedPosition(songs.getSelectedPosition()+1);

                // See if there is a next song
                try {

                    if(songsList.get(songs.getSelectedPosition()) != null) {
                        songHasBeenChanged = true;
                        Intent playNextSong = new Intent(getApplicationContext(), AudioService.class);
                        playNextSong.setAction(Constants.START_FOREGROUND_SERVICE);
                        playNextSong.putExtra(Constants.PLAYING_SONG, songsList.get(songs.getSelectedPosition()));
                        getApplicationContext().startService(playNextSong);
                    }

                }catch (Exception exc){  // If there is no next song, set the current playing position to the next song
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

    private void pauseSong(){

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

                Commons.setIsPlaying(false);

            } else {
                mediaPlayer.start();
               Notification notification = NotificationGenerator.createNotification(getApplicationContext(),
                                                                                playingSong,
                                                                                Constants.PAUSE_STATE);
                notificationManager.notify(Constants.NOTIFICATION_ID,notification);

                Commons.setIsPlaying(true);
            }
        }else{
            // We need to start the service again, to play the song
            // Proper notification is created in playSong(Context,String) method
            playSong(getApplicationContext(),playingSong.getSongPath());
        }
    }

    private void stopSong(){
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            Commons.setIsPlaying(false);
            Commons.setServiceWasStopped(true);
        }
    }

    private void minus30sec(){
        int skipTime = 30 * 1000; // skip 30s

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - skipTime);
        }
    }

    private void plus30sec(){
        int skipTime = 30 * 1000; // skip 30s

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + skipTime);
        }
    }

    public static MediaPlayer getMediaPlayer(){return mediaPlayer;}

    public class AudioServiceBinder extends Binder {
        public AudioService getAudioService(){
            return AudioService.this;
        }

    }

    public void changeProgressForSeekbar(){

        Log.i("bogdanzzz", "changeProgressForSeekbar: ");

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if(mediaPlayer != null) {
                    float floatProgress = ((float) mediaPlayer.getCurrentPosition()) / mediaPlayer.getDuration();
                    progress = (int) (floatProgress * 1000);
                   // Log.i("bogdanzzz", "run: " + progress);
                    handler.postDelayed(this, 100);
                }else{
                    Log.i("bogdanzzz", "run: mediaplayer is NULL");
                }

            }
        };
        handler.postDelayed(runnable,100);
    }

    //
    // Methods for clients
    //

    public boolean getSongHasBeenChanged(){
        return songHasBeenChanged;
    }

    public int getProgress(){return progress;}

    public void setProgress(int progress){ this.progress = progress;}

    public int getMaxValue(){return maxValue;}

    public static Song getPlayingSong(){ return playingSong; }


    public void resetTask(){
        progress = 0;
    }

    // When the app is removed from recently used apps
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSong();
        stopSelf();
    }
}
