package com.audiobook.nbogdand.playbook.TabFragments;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.audiobook.nbogdand.playbook.BroadcastReciever.NotificationBroadcast;
import com.audiobook.nbogdand.playbook.Constants;
import com.audiobook.nbogdand.playbook.MainActivity;
import com.audiobook.nbogdand.playbook.PlaySongActivity;
import com.audiobook.nbogdand.playbook.R;
import com.audiobook.nbogdand.playbook.SongsViewModel;
import com.audiobook.nbogdand.playbook.data.Song;
import com.audiobook.nbogdand.playbook.data.Songs;

import java.util.List;

import static com.audiobook.nbogdand.playbook.PlaySongActivity.CHANNEL_ID;

public class ListAllFragment extends Fragment {

    private SongsViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private Context activityContext, applicationContext;

    static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private View albumItemView;

    public void setContextsListAllFragment(Context activityContext, Context applicationContext) {
        this.activityContext = activityContext;
        this.applicationContext = applicationContext;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_all_fragment, container, false);

        //reduceSharedElementBlinking();

        //Opening app for the first time

        viewModel = ViewModelProviders.of(this).get(SongsViewModel.class);
        viewModel.init();

        NotificationBroadcast mNotificationBroadcast = NotificationBroadcast.getInstance();
        applicationContext.registerReceiver(mNotificationBroadcast,
                new IntentFilter(Constants.NOTIFY_MINUS));

        applicationContext.registerReceiver(mNotificationBroadcast,
                new IntentFilter(Constants.NOTIFY_PAUSE));

        applicationContext.registerReceiver(mNotificationBroadcast,
                new IntentFilter(Constants.NOTIFY_PLUS));
        applicationContext.registerReceiver(mNotificationBroadcast,
                new IntentFilter(Constants.STOP_FOREGROUND_SERVICE));

        if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            Log.d("permission", "denied 1");
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) activityContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Log.d("permission", "denied 2");
                ActivityCompat.requestPermissions((MainActivity) activityContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);

            } else {

                Log.d("permission", "denied 3");
                ActivityCompat.requestPermissions((MainActivity) activityContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            }

        } else {
            Log.d("permission", "granted fetch");
            viewModel.fetchSongs(applicationContext);
        }

        viewModel.getSongs().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable List<Song> songs) {
                viewModel.setSongsInAdapter(songs);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        recyclerView.setAdapter(viewModel.getSongsAdapter());
        linearLayoutManager = new LinearLayoutManager(activityContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        createNotificationChannel();

        setUpClick();

        return view;

    }

    public void setUpClick() {

        //Preparing the intent to start the activity
        // which is responsable of playing the selected song
        final Intent play = new Intent(activityContext, PlaySongActivity.class);

        viewModel.getSelected().observe(this, new Observer<Song>() {
            @Override
            public void onChanged(@Nullable Song song) {
                if (song != null) {

                    // Getting a reference of the view on which user clicked
                    albumItemView = linearLayoutManager.findViewByPosition(viewModel.getSelectedPosition());
                    View imageView = albumItemView.findViewById(R.id.album_item);
                    View titleTextView = albumItemView.findViewById(R.id.song_title_textView);
                    View authorTextView = albumItemView.findViewById(R.id.song_author_textView);

                    // Start new activity to play the selected song
                    // with the album icon as a shared element
                    if (albumItemView != null) {

                        /*
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                                new Pair<View, String>(imageView, ViewCompat.getTransitionName(albumItemView)));

                        */

                        Pair<View, String> albumView = Pair.create(imageView, viewModel.getSongAt(viewModel.getSelectedPosition()).getTitle());
                        Pair<View, String> titleView = Pair.create(titleTextView, viewModel.getSongAt(viewModel.getSelectedPosition()).getTitle()
                                + viewModel.getSongAt(viewModel.getSelectedPosition()).getAuthor());
                        Pair<View, String> authorView = Pair.create(authorTextView, viewModel.getSongAt(viewModel.getSelectedPosition()).getAuthor());
                        /*
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                                albumView,titleView,authorView);
                        */

                        Song playingSong = new Song(viewModel.getSongAt(viewModel.getSelectedPosition()).getTitle(),
                                viewModel.getSongAt(viewModel.getSelectedPosition()).getAuthor(),
                                viewModel.getSongAt(viewModel.getSelectedPosition()).getLength(),
                                viewModel.getSongAt(viewModel.getSelectedPosition()).getSongPath());

                        Songs songs = Songs.getInstance();
                        songs.setSelectedPosition(viewModel.getSelectedPosition());

                        play.setAction(Constants.OPEN_FROM_MAIN_ACTIVITY);
                        play.putExtra(Constants.PLAYING_SONG, playingSong);

                        //display the second activity
                        // startActivity(play, options.toBundle());
                        startActivity(play);
                    }
                }
            }
        });
    }


    private void reduceSharedElementBlinking() {

        ChangeBounds transition = new ChangeBounds();
        transition.setInterpolator(new AccelerateDecelerateInterpolator());
        transition.excludeTarget(android.R.id.statusBarBackground, true);
        transition.excludeTarget(android.R.id.navigationBarBackground, true);
        /// transition.excludeTarget(android.R.id.background,true);

        //  getWindow().setEnterTransition(transition);
        //  getWindow().setExitTransition(transition);


    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = applicationContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);


        }
    }
}