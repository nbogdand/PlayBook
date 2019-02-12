package com.audiobook.nbogdand.playbook;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.audiobook.nbogdand.playbook.Services.AudioService;
import com.audiobook.nbogdand.playbook.data.Song;

import java.util.ArrayList;

public class PlaySongViewModel extends ViewModel{

    private MutableLiveData<AudioService.AudioServiceBinder> mBinder = new MutableLiveData<>();
    private MutableLiveData<Boolean> isProgressUpdating = new MutableLiveData<>();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("bogdanzzz", "onServiceConnected: connected");
            AudioService.AudioServiceBinder binder = (AudioService.AudioServiceBinder)service;
            mBinder.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("bogdanzzz", "onServiceDisconnected: disconnected");
            mBinder.postValue(null);
        }
    };

    public LiveData<Boolean> getIsProgressUpdating(){
        return isProgressUpdating;
    }

    public void setIsProgressUpdating(Boolean bool){
        isProgressUpdating.postValue(bool);
    }

    public LiveData<AudioService.AudioServiceBinder> getBinder(){
        return mBinder;
    }

    public ServiceConnection getServiceConnection() {return serviceConnection;}

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

}
