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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.support.Utilis;

public class ShopLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String Tag = "ShopLocationActivity";

    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strLatitude = "", strLongitude = "";
    View mapView;
    public static GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_location);

        utilis = new Utilis(ShopLocationActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strLatitude = intent.getStringExtra("latitude");
        strLongitude = intent.getStringExtra("longitude");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ShopLocationActivity.this, R.color.colorPrimaryDark));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        try {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapView = supportMapFragment.getView();
            supportMapFragment.getMapAsync(ShopLocationActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Shop Location");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(Double.parseDouble(strLatitude), Double.parseDouble(strLongitude));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Shop Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }
}