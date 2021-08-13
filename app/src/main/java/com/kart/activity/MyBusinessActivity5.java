package com.kart.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kart.R;
import com.kart.adapter.ImageAdapter;
import com.kart.model.BasicDetailsData;
import com.kart.model.UploadImages;
import com.kart.support.MyGridView;
import com.kart.support.Utilis;

import java.util.ArrayList;
import java.util.List;

public class MyBusinessActivity5 extends AppCompatActivity {
    private String Tag = "MyBusinessActivity5";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

//    NestedScrollView nestedScrollView;
    LinearLayout layGrid;
    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business5);

        utilis = new Utilis(MyBusinessActivity5.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MyBusinessActivity5.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("My Business");

        View progressView = findViewById(R.id.progress_view);
        final BasicDetailsData basicDetailsData = Utilis.getBasicDetails(MyBusinessActivity5.this);
        if (basicDetailsData.getBusinessType().equalsIgnoreCase("Shopping")) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 10);
            progressView.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(900, 10);
            progressView.setLayoutParams(lp);
        }

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnNext = findViewById(R.id.btn_next);
        if (basicDetailsData.getBusinessType().equalsIgnoreCase("Shopping")) {
            btnNext.setText("Ok");
        } else {
            btnNext.setText("Next");
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (basicDetailsData.getBusinessType().equalsIgnoreCase("Shopping")) {
                    Intent intent = new Intent(MyBusinessActivity5.this, ManageBusinessActivity.class);
                    intent.putExtra("key", keyIntent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MyBusinessActivity5.this, MyBusinessActivity6.class);
                    intent.putExtra("key", keyIntent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }


            }
        });


//        nestedScrollView = findViewById(R.id.nested_scroll_view);
        layGrid = findViewById(R.id.lay_grid);
        List<UploadImages> arrayList = new ArrayList<>();
        arrayList = Utilis.getImageList("imageList");
        if (arrayList != null) {
            System.out.println("array list contains images");
        } else {
            arrayList = new ArrayList<>();
        }
        if (arrayList.size() > 0) {
//            nestedScrollView.setVisibility(View.VISIBLE);
            layGrid.setVisibility(View.VISIBLE);
        } else {
            layGrid.setVisibility(View.GONE);
//            nestedScrollView.setVisibility(View.GONE);
        }
        MyGridView gridView = findViewById(R.id.grid_image);
        final ImageAdapter adapter = new ImageAdapter(MyBusinessActivity5.this, arrayList, 1);
        gridView.setAdapter(adapter);

        chipGroup = findViewById(R.id.chipGroup);
        setChip();
    }

    private void setChip() {
        List<String> tagList = Utilis.getTagList(MyBusinessActivity5.this);
        for (int i = 0; i < tagList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(MyBusinessActivity5.this);

            // Create a Chip from Layout.
            Chip newChip = (Chip) inflater.inflate(R.layout.chip_entry, chipGroup, false);
            newChip.setText(tagList.get(i));

            chipGroup.addView(newChip);
            chipGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        finish();
    }
}