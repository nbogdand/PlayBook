package com.audiobook.nbogdand.playbook;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.audiobook.nbogdand.playbook.BroadcastReciever.NotificationBroadcast;
import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.data.Songs;

import java.util.List;

import static com.audiobook.nbogdand.playbook.PlaySongActivity.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    private SongsViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private View albumItemView ;
    public static Integer playingPosition = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


            //Opening app for the first time

            viewModel = ViewModelProviders.of(this).get(SongsViewModel.class);
            viewModel.init();

            NotificationBroadcast mNotificationBroadcast = new NotificationBroadcast();
            getApplicationContext().registerReceiver(mNotificationBroadcast,
                    new IntentFilter(Constants.NOTIFY_MINUS));

            getApplicationContext().registerReceiver(mNotificationBroadcast,
                    new IntentFilter(Constants.NOTIFY_PAUSE));

            getApplicationContext().registerReceiver(mNotificationBroadcast,
                    new IntentFilter(Constants.NOTIFY_PLUS));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                Log.d("permission", "denied 1");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Log.d("permission", "denied 2");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);

                } else {

                    Log.d("permission", "denied 3");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }

            } else {
                Log.d("permission", "granted fetch");
                viewModel.fetchSongs(this);
            }

            viewModel.getSongs().observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable List<Song> songs) {
                    viewModel.setSongsInAdapter(songs);
                }
            });

            recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
            recyclerView.setAdapter(viewModel.getSongsAdapter());
            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            createNotificationChannel();

            setUpClick();
        }

    public void setUpClick(){

        //Preparing the intent to start the activity
        // which is responsable of playing the selected song
        final Intent play = new Intent(this,PlaySongActivity.class);

        viewModel.getSelected().observe(this, new Observer<Song>() {
            @Override
            public void onChanged(@Nullable Song song) {
                if(song != null){

                    // Getting a reference of the view on which user clicked
                    albumItemView = linearLayoutManager.findViewByPosition(viewModel.getSelectedPosition());
                    View imageView = albumItemView.findViewById(R.id.album_item);

                    // Start new activity to play the selected song
                    // with the album icon as a shared element
                    if(albumItemView != null) {
                        ActivityOptions options = ActivityOptions.
                                makeSceneTransitionAnimation(MainActivity.this, albumItemView,
                                        viewModel.getSongAt(viewModel.getSelectedPosition()).getTitle());


                        Song playingSong = new Song(viewModel.getSongAt(viewModel.getSelectedPosition()).getTitle(),
                                                    viewModel.getSongAt(viewModel.getSelectedPosition()).getAuthor(),
                                                    viewModel.getSongAt(viewModel.getSelectedPosition()).getLength(),
                                                    viewModel.getSongAt(viewModel.getSelectedPosition()).getSongPath());

                        Songs songs = Songs.getInstance();
                        songs.setSelectedPosition(viewModel.getSelectedPosition());

                        play.setAction(Constants.OPEN_FROM_MAIN_ACTIVITY);
                        play.putExtra(Constants.PLAYING_SONG,playingSong);

                        //display the second activity
                        startActivity(play, options.toBundle());
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void createNotificationChannel(){

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setSound(null,null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);


        }

    }

}
