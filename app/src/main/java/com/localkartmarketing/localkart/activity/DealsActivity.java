package com.localkartmarketing.localkart.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.ViewPagerTabAdapter;
import com.localkartmarketing.localkart.support.Utils;

public class DealsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private String Tag = "DealsActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerTabAdapter viewPagerTabAdapter;

    String keyIntent = "", categoryId = "", categoryName = "", subcategoryName = "", subcategoryId = "", megaSalesIndexId = "", offerTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        utils = new Utils(DealsActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        categoryId = intent.getStringExtra("categoryId");
        categoryName = intent.getStringExtra("categoryName");
        subcategoryName = intent.getStringExtra("subcategoryName");
        subcategoryId = intent.getStringExtra("subcategoryId");
        megaSalesIndexId = intent.getStringExtra("megasalesIndexId");
        offerTitle = intent.getStringExtra("offerTitle");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(DealsActivity.this, R.color.my_statusbar_color));


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText(subcategoryName);

        //Initializing the tablayout
        tabLayout = findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        if (!offerTitle.isEmpty()) {
            tabLayout.addTab(tabLayout.newTab().setText(offerTitle));
        }
        tabLayout.addTab(tabLayout.newTab().setText("DIRECTORY"));
        tabLayout.addTab(tabLayout.newTab().setText("TODAY"));
        tabLayout.addTab(tabLayout.newTab().setText("WEEKLY"));
        tabLayout.addTab(tabLayout.newTab().setText("FESTIVAL"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if (!offerTitle.isEmpty()) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

        //Initializing viewPager
        viewPager = findViewById(R.id.pager);

        //Creating our pager adapter
        viewPagerTabAdapter = new ViewPagerTabAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), keyIntent, categoryId, subcategoryId, subcategoryName, megaSalesIndexId, offerTitle);

        //Adding adapter to pager
        viewPager.setAdapter(viewPagerTabAdapter);
        viewPager.setOffscreenPageLimit(viewPagerTabAdapter.getCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
    }

    private void back() {
        Intent intent = new Intent(DealsActivity.this, SubActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("categoryName", categoryName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}