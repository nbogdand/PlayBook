package com.audiobook.nbogdand.playbook;

import android.Manifest;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.audiobook.nbogdand.playbook.databinding.PlaySongActivityBinding;

import static com.audiobook.nbogdand.playbook.MainActivity.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

public class PlaySongActivity extends AppCompatActivity {

    private PlaySongViewModel playSongViewModel;
    private PlaySongActivityBinding binding;
    private String title,artist,duration;


    static final int MY_PERMISSION_REQUEST = 1;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            // read external storage, MY_PERMISSION_REQUEST defined in MainActivity
            case MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    playSongViewModel.playSong(getApplicationContext(), playSongViewModel.getSongPath(this, title,artist));
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

        // Retrieving transition name from intent's extras
        Bundle extras = getIntent().getExtras();
        String imageTransitionName = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageTransitionName = extras.getString("transitionName");
            title = imageTransitionName;
            artist = extras.getString("artist");
        }


        // Initialize viewModel and databinding
        init();


        // Setting the transition name to ImageView (shared
        // element transition)
        ImageView albumArtImageView = findViewById(R.id.album_art);
        if(imageTransitionName != null){
            albumArtImageView.setTransitionName(imageTransitionName);
        }

        //imageTransitionName is the same as title
        binding.setSongTitle(imageTransitionName);
        binding.setArtist(extras.getString("artist"));
        binding.setDuration(extras.getInt("duration"));
        binding.setSongPath(playSongViewModel.getSongPath(this,imageTransitionName,extras.getString("artist")));

        String TAG = "playsong";

        Log.d(TAG, "onCreate: " + imageTransitionName);
        Log.d(TAG, "onCreate: " + extras.getString("artist"));


        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){

            Log.d("permission","denied 1");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Log.d("permission","denied 2");
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST);

            }else{

                Log.d("permission","denied 3");
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST);
            }

        }else {
            Log.d("permission","granted play song");
            playSongViewModel.playSong(getApplicationContext(), playSongViewModel.getSongPath(this, imageTransitionName, extras.getString("artist")));
        }

    }

    private void init() {
        playSongViewModel = ViewModelProviders.of(this).get(PlaySongViewModel.class);

        binding = DataBindingUtil.setContentView(this,R.layout.play_song_activity);
        binding.setApplicationContext(getApplicationContext());
        binding.setPlayViewModel(playSongViewModel);


    }

}
