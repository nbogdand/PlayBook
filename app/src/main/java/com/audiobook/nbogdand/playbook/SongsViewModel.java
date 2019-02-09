package com.audiobook.nbogdand.playbook;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.audiobook.nbogdand.playbook.adapter.SongsAdapter;
import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.data.Songs;

import java.util.List;

public class SongsViewModel extends ViewModel {

    // data needed
    private Songs songs = null ;

    //adapter for RecycleView
    private SongsAdapter adapter ;

    // clicked song ("selected")
    private MutableLiveData<Song> selected;
    private int selectedPosition;



    public void init(){
        songs = Songs.getInstance();
        adapter = new SongsAdapter(R.layout.item_recycleview,this);
        selected = new MutableLiveData<>();
    }

    // providing item selected by click
    public MutableLiveData<Song> getSelected(){
        return selected;
    }

    // providing selected item position
    public int getSelectedPosition(){
        return selectedPosition;
    }

    // providing SongsAdapter (RecycleView's adapter)
    public SongsAdapter getSongsAdapter(){
        return adapter;
    }

    // Fetch all songs from Songs class, passing context and
    // give the adapter the songs list
    public void fetchSongs(Context context){
        songs.fetchList(context);
    }

    //returning the list of songs fetched previously
    public MutableLiveData<List<Song>> getSongs(){
        return songs.getSongs();
    }

    // give the adapter a list of songs to be displayed
    public void setSongsInAdapter(List<Song> songs){
        this.adapter.setSongsList(songs);
        this.adapter.notifyDataSetChanged();
    }

    public Song getSongAt(Integer position){
        if (songs.getSongs().getValue() != null && position != null &&
                    songs.getSongs().getValue().size() > position){

            //selectedPosition = position;
            return songs.getSongs().getValue().get(position);
        }

        return null;
    }

    public void onItemClick(Integer position){
        Song song = getSongAt(position);
        selectedPosition = position;
        selected.setValue(song);

    }

}




