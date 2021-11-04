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
import android.widget.Toast;

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

    String keyIntent = "", strBusinessId = "";
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

    boolean isAllFieldsChecked = false;
    String strPhoneNo = "", strMobileNo = "", strAltNo = "", strWhatsAppNo = "", strEmailId = "", strWebsite = "", strFacebook = "", strVcard = "", strCod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business3);

        utilis = new Utilis(MyBusinessActivity3.this);
        contactDetailsData = Utilis.getContactDetails(MyBusinessActivity3.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strBusinessId = intent.getStringExtra("businessType");

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
                strPhoneNo = etPhoneNo.getText().toString().trim();
                strMobileNo = etMobileNo.getText().toString().trim();
                strAltNo = etAltNo.getText().toString().trim();
                strWhatsAppNo = etWhatsAppNo.getText().toString().trim();
                strEmailId = etEmail.getText().toString().trim();
                strWebsite = etWebsite.getText().toString().trim();
                strFacebook = etFacebook.getText().toString().trim();
                strVcard = etVcard.getText().toString().trim();
                strCod = etCod.getText().toString().trim();

                isAllFieldsChecked = validateString();

                if (isAllFieldsChecked) {
                    ContactDetailsData contactDetailsData = new ContactDetailsData(
                            strPhoneNo,
                            strMobileNo,
                            strAltNo,
                            strWhatsAppNo,
                            strEmailId,
                            strWebsite,
                            strFacebook,
                            strVcard,
                            strCod
                    );
                    Utilis.saveContactDetails(contactDetailsData);
                    Intent intent = new Intent(MyBusinessActivity3.this, MyBusinessActivity4.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }


    private boolean validateString() {
        if (strMobileNo.isEmpty()) {
            etMobileNo.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strMobileNo.length() < 10) {
            etMobileNo.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strAltNo.length() > 0 && strAltNo.length() < 10) {
            etAltNo.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter 10 digit alternate / appointment number", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (strWhatsAppNo.isEmpty()) {
//            etWhatsAppNo.requestFocus();
//            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter whatsApp number", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (strCod.length() > 0 && strCod.length() < 10) {
            etCod.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter 10 digit cod number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strWhatsAppNo.length() > 0 && strWhatsAppNo.length() < 10) {
            etWhatsAppNo.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter 10 digit WhatsApp number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strEmailId.length() > 0 && !Utilis.eMailValidation(strEmailId)) {
            etEmail.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strWebsite.length() > 0 && !Utilis.webURLValidation(strWebsite)) {
            etWebsite.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter valid website address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strFacebook.length() > 0 && !Utilis.webURLValidation(strFacebook)) {
            etFacebook.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter valid facebook URL", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strVcard.length() > 0 && !Utilis.webURLValidation(strVcard)) {
            etVcard.requestFocus();
            Toast.makeText(MyBusinessActivity3.this, "Enter valid digital vcard", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        strPhoneNo = etPhoneNo.getText().toString().trim();
        strMobileNo = etMobileNo.getText().toString().trim();
        strAltNo = etAltNo.getText().toString().trim();
        strWhatsAppNo = etWhatsAppNo.getText().toString().trim();
        strEmailId = etEmail.getText().toString().trim();
        strWebsite = etWebsite.getText().toString().trim();
        strFacebook = etFacebook.getText().toString().trim();
        strVcard = etVcard.getText().toString().trim();
        strCod = etCod.getText().toString().trim();
        ContactDetailsData contactDetailsData = new ContactDetailsData(
                strPhoneNo,
                strMobileNo,
                strAltNo,
                strWhatsAppNo,
                strEmailId,
                strWebsite,
                strFacebook,
                strVcard,
                strCod
        );
        Utilis.saveContactDetails(contactDetailsData);
        finish();
    }
}