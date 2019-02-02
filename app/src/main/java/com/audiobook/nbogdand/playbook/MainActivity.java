package com.audiobook.nbogdand.playbook;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.BroadcastReciever.NotificationBroadcast;
import com.audiobook.nbogdand.playbook.Services.AudioService;
import com.audiobook.nbogdand.playbook.adapter.SongsAdapter;
import com.audiobook.nbogdand.playbook.adapter.SongsAdapterViewHolder;
import com.audiobook.nbogdand.playbook.data.Song;

import java.util.List;

import static com.audiobook.nbogdand.playbook.PlaySongActivity.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    private SongsViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private View albumItemView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(SongsViewModel.class);
        viewModel.init();

        NotificationBroadcast mNotificationBroadcast = new NotificationBroadcast();
        getApplicationContext().registerReceiver(mNotificationBroadcast,
                    new IntentFilter(AudioService.NOTIFY_MINUS));

        getApplicationContext().registerReceiver(mNotificationBroadcast,
                new IntentFilter(AudioService.NOTIFY_PAUSE));

        getApplicationContext().registerReceiver(mNotificationBroadcast,
                new IntentFilter(AudioService.NOTIFY_PLUS));

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){

            Log.d("permission","denied 1");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Log.d("permission","denied 2");
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                                        MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);

            }else{

                Log.d("permission","denied 3");
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                                        MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            }

        }else {
            Log.d("permission","granted fetch");
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

                        // Map transitionName : song's title
                        // to be available in the second activity
                        play.putExtra("transitionName",viewModel.getSongAt(viewModel.getSelectedPosition()).getTitle());
                        play.putExtra("artist",viewModel.getSongAt(viewModel.getSelectedPosition()).getAuthor());
                        play.putExtra("duration",viewModel.getSongAt(viewModel.getSelectedPosition()).getLength());


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


    /*
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)context))

            }


        }

    }
*/
}
