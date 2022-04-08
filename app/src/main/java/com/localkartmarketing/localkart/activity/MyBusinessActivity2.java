package com.localkartmarketing.localkart.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.AddressDetailsData;
import com.localkartmarketing.localkart.support.Utils;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class MyBusinessActivity2 extends AppCompatActivity {
    private String Tag = "MyBusinessActivity2";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strBusinessId = "";
    AddressDetailsData addressDetailsData;

    EditText etDoorNo;
    EditText etLocality;
    EditText etArea;
    EditText etPost;
    EditText etLandmark;
    EditText etPinCode;

    private List<String> stateSpinnerValue = new ArrayList<>();
    private List<String> districtSpinnerValue = new ArrayList<>();

    private String strDoorNo = "", strLocality = "", strArea = "", strPost = "", strLandmark = "", strPinCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business2);

        utils = new Utils(MyBusinessActivity2.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strBusinessId = intent.getStringExtra("businessType");
        addressDetailsData = Utils.getAddressDetails(MyBusinessActivity2.this);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MyBusinessActivity2.this, R.color.colorPrimaryDark));

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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(350, 10);
        progressView.setLayoutParams(lp);


        etDoorNo = findViewById(R.id.et_door_no);
        etLocality = findViewById(R.id.et_locality);
        etArea = findViewById(R.id.et_area);
        etPost = findViewById(R.id.et_post);
        etLandmark = findViewById(R.id.et_landmark);
        etPinCode = findViewById(R.id.et_pin_code);

        SearchableSpinner spinState = findViewById(R.id.spin_state);
        spinState.setEnabled(false);
        spinState.setClickable(false);

        SearchableSpinner spinDistrict = findViewById(R.id.spin_district);
        spinDistrict.setEnabled(false);
        spinDistrict.setClickable(false);

        etDoorNo.setText(addressDetailsData.getDoorNo());
        etLocality.setText(addressDetailsData.getLocality());
        etArea.setText(addressDetailsData.getArea());
        etPost.setText(addressDetailsData.getPost());
        etLandmark.setText(addressDetailsData.getLandmark());
        etPinCode.setText(addressDetailsData.getPinCode());


        stateSpinnerValue.add(addressDetailsData.getState());
        ArrayAdapter arrayAdapter1 = new ArrayAdapter(MyBusinessActivity2.this, R.layout.spinner_item, stateSpinnerValue);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinState.setAdapter(arrayAdapter1);
        spinState.setSelection(0);

        districtSpinnerValue.add(addressDetailsData.getDistrict());
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(MyBusinessActivity2.this, R.layout.spinner_item, districtSpinnerValue);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinDistrict.setAdapter(arrayAdapter2);
        spinDistrict.setSelection(0);

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
                strDoorNo = etDoorNo.getText().toString().trim();
                strLocality = etLocality.getText().toString().trim();
                strArea = etArea.getText().toString().trim();
                strPost = etPost.getText().toString().trim();
                strLandmark = etLandmark.getText().toString().trim();
                strPinCode = etPinCode.getText().toString().trim();
                if (strDoorNo.equalsIgnoreCase("")) {
                    etDoorNo.requestFocus();
                    Toast.makeText(MyBusinessActivity2.this, "Enter door no / flat no / building no", Toast.LENGTH_SHORT).show();
                } else if (strLocality.isEmpty()) {
                    etLocality.requestFocus();
                    Toast.makeText(MyBusinessActivity2.this, "Enter locality", Toast.LENGTH_SHORT).show();
                } else if (strArea.isEmpty()) {
                    etArea.requestFocus();
                    Toast.makeText(MyBusinessActivity2.this, "Enter area", Toast.LENGTH_SHORT).show();
                } else if (strPost.isEmpty()) {
                    etPost.requestFocus();
                    Toast.makeText(MyBusinessActivity2.this, "Enter block / taluk / post", Toast.LENGTH_SHORT).show();
                } else if (strPinCode.isEmpty()) {
                    etPinCode.requestFocus();
                    Toast.makeText(MyBusinessActivity2.this, "Enter PIN code", Toast.LENGTH_SHORT).show();
                } else if (strPinCode.length() < 6) {
                    etPinCode.requestFocus();
                    Toast.makeText(MyBusinessActivity2.this, "Enter Valid PIN code", Toast.LENGTH_SHORT).show();
                } else {
                    AddressDetailsData ad = Utils.getAddressDetails(MyBusinessActivity2.this);
                    AddressDetailsData addressDetailsData = new AddressDetailsData(
                            strDoorNo,
                            strLocality,
                            strArea,
                            strPost,
                            strLandmark,
                            strPinCode,
                            ad.getStateId(),
                            ad.getDistrictId(),
                            ad.getState(),
                            ad.getDistrict()
                    );
                    Utils.saveAddressDetails(addressDetailsData);
                    Intent intent = new Intent(MyBusinessActivity2.this, MyBusinessActivity3.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        strDoorNo = etDoorNo.getText().toString().trim();
        strLocality = etLocality.getText().toString().trim();
        strArea = etArea.getText().toString().trim();
        strPost = etPost.getText().toString().trim();
        strLandmark = etLandmark.getText().toString().trim();
        strPinCode = etPinCode.getText().toString().trim();
        AddressDetailsData ad = Utils.getAddressDetails(MyBusinessActivity2.this);
        AddressDetailsData addressDetailsData = new AddressDetailsData(
                strDoorNo,
                strLocality,
                strArea,
                strPost,
                strLandmark,
                strPinCode,
                ad.getStateId(),
                ad.getDistrictId(),
                ad.getState(),
                ad.getDistrict()
        );
        Utils.saveAddressDetails(addressDetailsData);
        finish();
    }
}