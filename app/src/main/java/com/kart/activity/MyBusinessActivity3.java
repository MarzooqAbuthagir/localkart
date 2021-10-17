package com.kart.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.kart.R;
import com.kart.model.ContactDetailsData;
import com.kart.support.Utilis;

public class MyBusinessActivity3 extends AppCompatActivity {
    private String Tag = "MyBusinessActivity3";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";
    ContactDetailsData contactDetailsData;

    EditText etPhoneNo;
    EditText etMobileNo;
    EditText etAltNo;
    EditText etWhatsAppNo;
    EditText etEmail;
    EditText etWebsite;
    EditText etFacebook;
    EditText etVcard;
    EditText etCod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business3);

        utilis = new Utilis(MyBusinessActivity3.this);
        contactDetailsData = Utilis.getContactDetails(MyBusinessActivity3.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MyBusinessActivity3.this, R.color.colorPrimaryDark));

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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(500, 10);
        progressView.setLayoutParams(lp);

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBusinessActivity3.this, MyBusinessActivity4.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        etPhoneNo = findViewById(R.id.et_phone_no);
        etMobileNo = findViewById(R.id.et_mobile_no);
        etAltNo = findViewById(R.id.et_alt_num);
        etWhatsAppNo = findViewById(R.id.et_whatsapp_no);
        etEmail = findViewById(R.id.et_email);
        etWebsite = findViewById(R.id.et_website);
        etFacebook = findViewById(R.id.et_facebook);
        etVcard = findViewById(R.id.et_vcard);
        etCod = findViewById(R.id.et_cod);

        etPhoneNo.setText(contactDetailsData.getPhoneNo());
        etMobileNo.setText(contactDetailsData.getMobileNo());
        etAltNo.setText(contactDetailsData.getAltNo());
        etWhatsAppNo.setText(contactDetailsData.getWhatsappNo());
        etEmail.setText(contactDetailsData.getEmailId());
        etWebsite.setText(contactDetailsData.getWebsite());
        etFacebook.setText(contactDetailsData.getFacebook());
        etVcard.setText(contactDetailsData.getVcard());
        etCod.setText(contactDetailsData.getCod());

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