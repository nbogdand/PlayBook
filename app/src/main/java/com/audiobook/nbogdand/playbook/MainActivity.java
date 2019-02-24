package com.audiobook.nbogdand.playbook;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.audiobook.nbogdand.playbook.TabFragments.CategoriesFragment;
import com.audiobook.nbogdand.playbook.TabFragments.ListAllFragment;

public class MainActivity extends AppCompatActivity {

    static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

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
