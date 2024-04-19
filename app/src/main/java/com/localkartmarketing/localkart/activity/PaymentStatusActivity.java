package com.localkartmarketing.localkart.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.support.Utils;

public class PaymentStatusActivity extends AppCompatActivity {
    private String Tag = "PaymentStatusActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", payStatus = "", eventId = "";

    ImageView imageView;
    TextView tvPayStatus;
    Button btnViewBooking, btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);

        utils = new Utils(PaymentStatusActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        payStatus = intent.getStringExtra("payStatus");
        eventId = intent.getStringExtra("eventId");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(PaymentStatusActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Event Booking");

        imageView = findViewById(R.id.imageView);
        tvPayStatus = findViewById(R.id.tv_pay_status);
        btnViewBooking = findViewById(R.id.btn_view_booking);
        btnTryAgain = findViewById(R.id.btn_try_again);

        if (payStatus.equalsIgnoreCase("success")) {
            imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pay_success));
            tvPayStatus.setText("Booking Successful !");
            btnViewBooking.setVisibility(View.VISIBLE);
        }
        if (payStatus.equalsIgnoreCase("fail")) {
            imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pay_fail));
            tvPayStatus.setText("Booking Failed !");
            btnTryAgain.setVisibility(View.VISIBLE);
        }

        btnViewBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentStatusActivity.this, BookingDetailsActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("index", "1");
                intent.putExtra("eventId", eventId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(PaymentStatusActivity.this, EventActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}