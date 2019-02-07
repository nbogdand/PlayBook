package com.audiobook.nbogdand.playbook;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.audiobook.nbogdand.playbook.Services.AudioService;
import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.databinding.PlaySongActivityBinding;

import static com.audiobook.nbogdand.playbook.MainActivity.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

public class PlaySongActivity extends AppCompatActivity {

    private PlaySongViewModel playSongViewModel;
    private PlaySongActivityBinding binding;
    private Song playingSong;


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

            } else
                //If activity is open from main activity
                if(getIntent().getAction().equals(Constants.OPEN_FROM_MAIN_ACTIVITY)){

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

                    Log.d("permission", "denied 1");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        Log.d("permission", "denied 2");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSION_REQUEST);

                    } else {

                        Log.d("permission", "denied 3");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSION_REQUEST);
                    }

                } else {
                    Log.i(Constants.LOGGER_TAG,"Inainte de startMyService()");
                    startMyService();
                }

            }


    }

    private void init() {
        playSongViewModel = ViewModelProviders.of(this).get(PlaySongViewModel.class);

        binding = DataBindingUtil.setContentView(this,R.layout.play_song_activity);
        binding.setApplicationContext(getApplicationContext());
        binding.setPlayingSong(playingSong);
        binding.setPlayViewModel(playSongViewModel);

    }

    public void startMyService(){


        Log.i(Constants.LOGGER_TAG,"In startMyService()");

        Intent serviceIntent = new Intent(getApplicationContext(), AudioService.class);
        serviceIntent.setAction(Constants.START_FOREGROUND_SERVICE);
        serviceIntent.putExtra(Constants.PLAYING_SONG,playingSong);
        startService(serviceIntent);

    }

    public void stopMyService(){

        Intent serviceIntent = new Intent(this,AudioService.class);
        serviceIntent.setAction(Constants.STOP_FOREGROUND_SERVICE);

        stopService(serviceIntent);
    }


}
