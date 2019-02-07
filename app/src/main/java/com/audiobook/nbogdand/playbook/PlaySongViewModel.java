package com.audiobook.nbogdand.playbook;

import android.arch.lifecycle.ViewModel;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.audiobook.nbogdand.playbook.Services.AudioService;
import com.audiobook.nbogdand.playbook.data.Song;

import java.util.ArrayList;

public class PlaySongViewModel extends ViewModel implements MediaPlayer.OnPreparedListener {

    private static MediaPlayer mediaPlayer;
/*
    public void playSong(Context applicationContext, String path){

        Uri songUri = Uri.fromFile(new File(path));

         if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();

                try {
                    //preparing mediaPlayer
                    //Log.d("playSong zxc", "am ajuns pana aici");
                    mediaPlayer.setDataSource(applicationContext, songUri);
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // stop the current instance of mediaplayer
                // then recall the playSong method to start/play the song from the beginning
                mediaPlayer.release();
                mediaPlayer = null;
                playSong(applicationContext,path);
         }
    }


    public void pauseSong(){

        // If mediaPlayer is stopped and then pressed pause
        // the app will crash
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    public void stopSong(){
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    */

    public void playSong(Context context,Song playingSong){
        Intent statPlaying = new Intent(context, AudioService.class);
        statPlaying.setAction(Constants.START_FOREGROUND_SERVICE);
        statPlaying.putExtra(Constants.PLAYING_SONG,playingSong);
        statPlaying.putExtra("nextState",Constants.PAUSE_STATE);
        context.startService(statPlaying);
    }

    public void pauseSong(Context context){
        Intent pauseSong = new Intent(context,AudioService.class);
        pauseSong.setAction(Constants.PAUSE_FOREGROUND_SERVICE);
        context.startService(pauseSong);
    }

    public void stopSong(Context context){
        Intent stopSong = new Intent(context,AudioService.class);
        stopSong.setAction(Constants.STOP_FOREGROUND_SERVICE);
        context.startService(stopSong);

    }

    public static MediaPlayer getMediaPlayer(){return mediaPlayer;}

    public String getSongPath(Context context,String title,String artist){

        ArrayList<Song> songsList = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songsCursor = contentResolver.query(uri,null,null,null,null);

        if (songsCursor != null && songsCursor.moveToFirst()){

            int songTitle = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songAuthor = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int duration = songsCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int column_index = songsCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                Song song = new Song(songsCursor.getString(songTitle),
                        songsCursor.getString(songAuthor),
                        21,songsCursor.getString(column_index));

                if(songsCursor.getString(songTitle).equals(title) && songsCursor.getString(songAuthor).equals(artist)) {
                    Log.d("uri raw",uri.toString());
                    Log.d("uri songs:::",songsCursor.getString(songTitle));
                    Log.d("uri songs", songsCursor.getString(songAuthor));
                    Log.d("uri after", ContentUris.withAppendedId(uri, songsCursor.getInt(songTitle)).toString());
                    Log.d("uri url??",songsCursor.getString(column_index));
                    return songsCursor.getString(column_index);
                }

            } while ( songsCursor.moveToNext());

        }

        return null;

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
