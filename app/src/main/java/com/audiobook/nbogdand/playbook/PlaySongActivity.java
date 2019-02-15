package com.audiobook.nbogdand.playbook;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Connection;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.Services.AudioService;
import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.databinding.PlaySongActivityBinding;

import java.net.Inet4Address;

import static com.audiobook.nbogdand.playbook.MainActivity.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

public class PlaySongActivity extends AppCompatActivity {

    private PlaySongViewModel playSongViewModel;
    private PlaySongActivityBinding binding;
    private Song playingSong;

    private SeekBar songSeekBar,volumeSeekbar;

    private AudioService audioService;
    private AudioManager audioManager;

    public static boolean SERVICE_READY_BOOL = false;

    static final int MY_PERMISSION_REQUEST = 1;

    public static final String CHANNEL_ID = "musicServiceChannel";


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            // read external storage, MY_PERMISSION_REQUEST defined in MainActivity
            case MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    playSongViewModel.playSong(getApplicationContext(),playingSong);

                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindService(playSongViewModel.getServiceConnection());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(playSongViewModel.getBinder() != null){
              //unbindService(playSongViewModel.getServiceConnection());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If activity is open from notification
        // It also means it was open at least 1 before and all things are initialized
            if (getIntent().getAction().equals(Constants.OPEN_NOTIFICATION)) {

                Song currentSong = getIntent().getParcelableExtra(Constants.PLAYING_SONG);
                Log.i("OPENNOTIFICATION ",currentSong.getTitle() + " ");
                playSongViewModel = ViewModelProviders.of(this).get(PlaySongViewModel.class);

                binding = DataBindingUtil.setContentView(this,R.layout.play_song_activity);
                binding.setApplicationContext(getApplicationContext());
                binding.setPlayViewModel(playSongViewModel);
                binding.setPlayingSong(currentSong);

                long mm = currentSong.getLength() / 60 / 1000;
                long ss = currentSong.getLength() / 1000 % 60;
                String duration = String.format("%02d:%02d",mm,ss);
                binding.setDuration(duration);

                songSeekBar = findViewById(R.id.song_seekBar);

                bindService();
                setObservers();
                int delay = 5 * 100;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toggleUpdates();
                    }
                }, delay);

            } else {

                //If activity is open from main activity
                if (getIntent().getAction().equals(Constants.OPEN_FROM_MAIN_ACTIVITY)) {

                    // Retrieving transition name from intent's extras
                    String imageTransitionName = null;

                    playingSong = (Song) getIntent().getParcelableExtra(Constants.PLAYING_SONG);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && playingSong != null) {
                        imageTransitionName = playingSong.getTitle();
                    }

                    // Initialize viewModel and databinding
                    init();

                    // Setting the transition name to ImageView (shared
                    // element transition)
                    ImageView albumArtImageView = findViewById(R.id.album_art);
                    if (imageTransitionName != null) {
                        albumArtImageView.setTransitionName(imageTransitionName);
                    }


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSION_REQUEST);

                        } else {

                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSION_REQUEST);
                        }

                    } else {
                        // Log.i(Constants.LOGGER_TAG,"Inainte de startMyService()");
                        startMyService();
                        setObservers();


                        // Need to start delayed toggle updates because
                        // service needs longer time to initialize
                        // and otherwise it will result in null AudioService object
                        int delay = 1 * 1000;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toggleUpdates();
                            }
                        }, delay);
                    }

                }
            }

    }

    private void init() {
        playSongViewModel = ViewModelProviders.of(this).get(PlaySongViewModel.class);

        binding = DataBindingUtil.setContentView(this,R.layout.play_song_activity);
        binding.setApplicationContext(getApplicationContext());
        binding.setPlayingSong(playingSong);
        binding.setPlayViewModel(playSongViewModel);

        songSeekBar = findViewById(R.id.song_seekBar);
        initVolumeSeekbar();

        long mm = playingSong.getLength() / 60 / 1000;
        long ss = playingSong.getLength() / 1000 % 60;
        String duration = String.format("%02d:%02d",mm,ss);
        binding.setDuration(duration);

    }

    private void initVolumeSeekbar(){
        try {

            volumeSeekbar = findViewById(R.id.change_volume_seekbar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                volumeSeekbar.setMin(audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC));
            }
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"There has been a problem with volume controls :(",Toast.LENGTH_LONG).show();
        }
    }

    public void startMyService(){


        Log.i(Constants.LOGGER_TAG,"In startMyService()");

        Intent serviceIntent = new Intent(getApplicationContext(), AudioService.class);
        serviceIntent.setAction(Constants.START_FOREGROUND_SERVICE);
        serviceIntent.putExtra(Constants.PLAYING_SONG,playingSong);
        startForegroundService(serviceIntent);

        bindService();
    }

    public void stopMyService(){

        Intent serviceIntent = new Intent(this,AudioService.class);
        serviceIntent.setAction(Constants.STOP_FOREGROUND_SERVICE);

        stopService(serviceIntent);
    }

    private void bindService(){
        Intent serviceIntent = new Intent(this,AudioService.class);
        bindService(serviceIntent,playSongViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    private void toggleUpdates(){
        if(audioService != null){

            // start changing progress bar
            audioService.changeProgressForSeekbar();
            playSongViewModel.setIsProgressUpdating(true);

            songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
                    if(fromUser){
                        audioService.setProgress(progress);
                        AudioService.getMediaPlayer().seekTo(progress * AudioService.getMediaPlayer().getDuration() /1000);

                        seekBar.setProgress(progress);

                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }else{
            Log.i("bogdanzzz", "toggleUpdates: audio service is NULL");
        }
    }

    private void setObservers(){

        playSongViewModel.getBinder().observe(this, new Observer<AudioService.AudioServiceBinder>() {
            @Override
            public void onChanged(@Nullable AudioService.AudioServiceBinder audioServiceBinder) {
                if(audioServiceBinder != null){

                    Log.i("bogdanzzz", "onChanged: bound to service");
                    audioService = audioServiceBinder.getAudioService();

                    if(audioService == null){
                        Log.i("bogdanzz", "onChanged: audioService is still null");
                    }else{
                        Log.i("bogdanzz", "onChanged: audioService is OK");
                    }

                }else{
                    Log.i("bogdanzz", "onChanged: unbound from service");
                    audioService = null;
                }
            }
        });

        playSongViewModel.getIsProgressUpdating().observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable final Boolean aBoolean) {

                Log.i("bogdanzzz", "onChanged: isProgressUpdating");

                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        if(playSongViewModel.getIsProgressUpdating().getValue() != null && AudioService.getMediaPlayer()!= null){

                            // Meaning the service is bound
                            if(playSongViewModel.getBinder().getValue() != null) {

                                if(audioService.getSongHasBeenChanged()) {
                                    binding.setPlayingSong(AudioService.getPlayingSong());
                                }

                                songSeekBar.setProgress(audioService.getProgress());
                                songSeekBar.setMax(audioService.getMaxValue());

                                long currentPositionInMs = audioService.getProgress() *
                                        AudioService.getMediaPlayer().getDuration() /1000;

                                long mm = currentPositionInMs / 60 / 1000;
                                long ss = currentPositionInMs /1000 % 60;
                                String duration = String.format("%02d:%02d",mm,ss);
                                binding.setCurrentPosition(duration);
                            }

                            handler.postDelayed(this,100);

                        }else{
                            handler.postDelayed(this,100);
                        }
                    }
                };

                if(aBoolean){
                    handler.postDelayed(runnable,100);
                }

            }
        });



    }

}
