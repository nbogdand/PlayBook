package com.audiobook.nbogdand.playbook.data;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Songs extends BaseObservable {

    private static Songs mInstance = null;

    private int selectedPosition;
    private List<Song> songsList = new ArrayList<>();
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>();

    private Songs(){}

    public static Songs getInstance(){
        if(mInstance == null){
            mInstance = new Songs();
        }
        return mInstance;
    }

    public MutableLiveData<List<Song>> getSongs(){
        return songs;
    }

    // providing songsList for the recycleView's adapter
    public List<Song> getSongsList(){
        return songsList;
    }

    public void fetchList(Context context){

        ArrayList<Song> songsArray = findAllSongs(context);

        songsList.addAll(songsArray);

        songs.setValue(songsList);

    }

    private ArrayList<Song> findAllSongs(Context context){
        ArrayList<Song> songsList = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songsCursor = contentResolver.query(uri,null,null,null, MediaStore.Audio.Media.TITLE);

        if (songsCursor != null && songsCursor.moveToFirst()){

            int songTitle = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songAuthor = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int duration = songsCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int column_index = songsCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                Song song = new Song(songsCursor.getString(songTitle),
                                    songsCursor.getString(songAuthor),
                                    songsCursor.getLong(duration),
                                    songsCursor.getString(column_index));
                songsList.add(song);

            } while ( songsCursor.moveToNext());

        }

        if(songsCursor != null) {
            songsCursor.close();
        }
        return songsList;

    }

    public void setSelectedPosition(int position){
        selectedPosition = position;
    }

    public int getSelectedPosition(){
        return  selectedPosition;
    }

}
