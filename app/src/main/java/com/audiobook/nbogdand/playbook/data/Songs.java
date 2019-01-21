package com.audiobook.nbogdand.playbook.data;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.BaseObservable;
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

    private List<Song> songsList = new ArrayList<>();
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>();

    public MutableLiveData<List<Song>> getSongs(){
        return songs;
    }

    // providing songsList for the recycleView's adapter
    public List<Song> getSongsList(){
        return songsList;
    }

    public void fetchList(Context context){

        ArrayList<Song> songsArray = findAllSongs(context);

        for(int i = 0; i < songsArray.size(); i++) {

            songsList.add(songsArray.get(i));
            Log.d("Env ::","" + songsArray.get(i).getTitle());
        }

        songs.setValue(songsList);

    }

    private ArrayList<Song> findAllSongs(Context context){
        ArrayList<Song> songsList = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songsCursor = contentResolver.query(uri,null,null,null,null);

        if (songsCursor != null && songsCursor.moveToFirst()){

            int songTitle = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songAuthor = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int duration = songsCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do{
                Song song = new Song(songsCursor.getString(songTitle),
                                    songsCursor.getString(songAuthor),
                                    21);
                songsList.add(song);

            } while ( songsCursor.moveToNext());

        }

        return songsList;

    }

}
