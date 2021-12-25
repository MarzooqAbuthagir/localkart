package com.localkartmarketing.localkart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.support.LocationTrack;
import com.localkartmarketing.localkart.support.Utilis;

public class ManageBusinessActivity extends AppCompatActivity {
    private String Tag = "ManageBusinessActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    LocationTrack currentLocation;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_business);

        utilis = new Utilis(ManageBusinessActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ManageBusinessActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Manage Business");

        CardView cardMyBusiness = findViewById(R.id.card_business_layout);
        CardView cardSubscription = findViewById(R.id.card_subscription_layout);
        CardView cardPost = findViewById(R.id.card_post_layout);
        CardView cardHistory = findViewById(R.id.card_history_layout);
        CardView cardHelp = findViewById(R.id.card_help_layout);
        CardView cardDigitalVcard = findViewById(R.id.card_digital_vcard);

        cardMyBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilis.clearRegPref(ManageBusinessActivity.this);
                Intent intent = new Intent(ManageBusinessActivity.this, MyBusinessActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, SubscriptionActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, CreatePostActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, HistoryActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, FAQActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardDigitalVcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchLastLocation();
            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(ManageBusinessActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ManageBusinessActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ManageBusinessActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        if (Utilis.isGpsOn()) {
            currentLocation = new LocationTrack(ManageBusinessActivity.this);

            if (currentLocation.canGetLocation()) {
                longitude = currentLocation.getLongitude();
                latitude = currentLocation.getLatitude();
                System.out.println("latitude " + latitude + " and longitude " + longitude);

                Intent intent = new Intent(ManageBusinessActivity.this, DigitalVCardActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("latitude", String.valueOf(latitude));
                intent.putExtra("longitude", String.valueOf(longitude));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        } else {
            Toast.makeText(ManageBusinessActivity.this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission Granted");
                if (Utilis.isGpsOn()) {
                    fetchLastLocation();
                }
            } else {
                Toast.makeText(ManageBusinessActivity.this, "Enable GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Utilis.isGpsOn()) {
                fetchLastLocation();
            } else {
                Toast.makeText(ManageBusinessActivity.this, "Enable GPS", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(ManageBusinessActivity.this, MainActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}