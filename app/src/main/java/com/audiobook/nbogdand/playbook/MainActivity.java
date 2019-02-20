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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

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

    private ViewPager mViewPager;
    private SectionsPageAdapter mSectionsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setUpViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }

    private void setUpViewPager(ViewPager viewPager){

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        ListAllFragment listAllFragment = new ListAllFragment();
        listAllFragment.setContextsListAllFragment(MainActivity.this,getApplicationContext());

        adapter.addFragment(listAllFragment,"All Tracks");
        adapter.addFragment(new CategoriesFragment(),"Categories");

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }




}
