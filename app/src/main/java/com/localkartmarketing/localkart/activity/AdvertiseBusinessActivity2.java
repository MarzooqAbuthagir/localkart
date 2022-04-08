package com.localkartmarketing.localkart.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.AddressDetailsData;
import com.localkartmarketing.localkart.model.DistrictData;
import com.localkartmarketing.localkart.model.StateData;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertiseBusinessActivity2 extends AppCompatActivity {
    private String Tag = "AdvertiseBusinessActivity2";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strBusinessId = "";

    private SearchableSpinner spinState;
    private ArrayList<StateData> stateListValue = new ArrayList<StateData>();
    private List<String> stateSpinnerValue = new ArrayList<>();
    private SearchableSpinner spinDistrict;
    private ArrayList<DistrictData> districtListValue = new ArrayList<DistrictData>();
    private List<String> districtSpinnerValue = new ArrayList<>();
    private String str_result = "";
    private String str_message = "";
    private String strDoorNo = "", strLocality = "", strArea = "", strPost = "", strLandmark = "", strPinCode = "";
    private String strStateId = "", strState = "";
    private String strDistrictId = "", strDistrict = "";

    EditText etDoorNo;
    EditText etLocality;
    EditText etArea;
    EditText etPost;
    EditText etLandmark;
    EditText etPinCode;
    AddressDetailsData ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_business2);

        utils = new Utils(AdvertiseBusinessActivity2.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strBusinessId = intent.getStringExtra("businessType");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AdvertiseBusinessActivity2.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Register Your Business");

        View progressView = findViewById(R.id.progress_view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(350, 10);
        progressView.setLayoutParams(lp);

        etDoorNo = findViewById(R.id.et_door_no);
        etLocality = findViewById(R.id.et_locality);
        etArea = findViewById(R.id.et_area);
        etPost = findViewById(R.id.et_post);
        etLandmark = findViewById(R.id.et_landmark);
        etPinCode = findViewById(R.id.et_pin_code);

        spinState = findViewById(R.id.spin_state);
        spinState.setTitle("Select State");
        spinDistrict = findViewById(R.id.spin_district);
        spinDistrict.setTitle("Select District / Zone");

        getStateList();

        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    spinDistrict.setEnabled(true);
                    strStateId = stateListValue.get(i).getStateId();
                    strState = stateListValue.get(i).getStateName();
                    getDistrictList();
                } else {
                    districtListValue.clear();
                    spinDistrict.setEnabled(false);
                    strStateId = "";
                    strState = "";
                    DistrictData initDistrictData = new DistrictData();
                    initDistrictData.setDistrictId("-1");
                    initDistrictData.setDistrictName("District / Zone");
                    districtListValue.add(0, initDistrictData);

                    districtSpinnerValue.add(districtListValue.get(0).getDistrictName());

                    ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity2.this, R.layout.spinner_item, districtSpinnerValue);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    spinDistrict.setAdapter(arrayAdapter);
                    spinDistrict.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strDistrictId = districtListValue.get(i).getDistrictId();
                    strDistrict = districtListValue.get(i).getDistrictName();
                } else {
                    strDistrictId = "";
                    strDistrict = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

//        etDoorNo.setText("39-c");
//        etLocality.setText("Thennur");
//        etArea.setText("Thennur");
//        etPost.setText("Thillai Nagar");
//        etLandmark.setText("Ram Nivas Hotel");
//        etPinCode.setText("620008");

        ad = Utils.getAddressDetails(AdvertiseBusinessActivity2.this);
        if (ad != null) {
            etDoorNo.setText(ad.getDoorNo());
            etLocality.setText(ad.getLocality());
            etArea.setText(ad.getArea());
            etPost.setText(ad.getPost());
            etLandmark.setText(ad.getLandmark());
            etPinCode.setText(ad.getPinCode());
        }

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
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Enter door no / flat no / building no", Toast.LENGTH_SHORT).show();
                } else if (strLocality.isEmpty()) {
                    etLocality.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Enter locality", Toast.LENGTH_SHORT).show();
                } else if (strArea.isEmpty()) {
                    etArea.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Enter area", Toast.LENGTH_SHORT).show();
                } else if (strPost.isEmpty()) {
                    etPost.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Enter block / taluk / post", Toast.LENGTH_SHORT).show();
                } /*else if (strLandmark.isEmpty()) {
                    etLandmark.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Enter landmark", Toast.LENGTH_SHORT).show();
                }*/ else if (strStateId.equalsIgnoreCase("")) {
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Select state", Toast.LENGTH_SHORT).show();
                } else if (strDistrictId.equalsIgnoreCase("")) {
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Select district / zone", Toast.LENGTH_SHORT).show();
                } else if (strPinCode.isEmpty()) {
                    etPinCode.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Enter PIN code", Toast.LENGTH_SHORT).show();
                } else if (strPinCode.length() < 6) {
                    etPinCode.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity2.this, "Enter Valid PIN code", Toast.LENGTH_SHORT).show();
                } else {
                    AddressDetailsData addressDetailsData = new AddressDetailsData(
                            strDoorNo,
                            strLocality,
                            strArea,
                            strPost,
                            strLandmark,
                            strPinCode,
                            strStateId,
                            strDistrictId,
                            strState,
                            strDistrict
                    );
                    Utils.saveAddressDetails(addressDetailsData);
                    Intent intent = new Intent(AdvertiseBusinessActivity2.this, AdvertiseBusinessActivity3.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private void getDistrictList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(AdvertiseBusinessActivity2.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.districtList, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getDistrictList response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getDistrictList result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            districtListValue.clear();
                            str_message = obj.getString("message");

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                DistrictData districtData = new DistrictData(
                                        jsonObject.getString("districtId"),
                                        jsonObject.getString("districtName"));

                                districtListValue.add(districtData);
                            }

                            DistrictData initDistrictData = new DistrictData();
                            initDistrictData.setDistrictId("-1");
                            initDistrictData.setDistrictName("District / Zone");
                            districtListValue.add(0, initDistrictData);

                            districtSpinnerValue.clear();
                            for (int i = 0; i < districtListValue.size(); i++) {
                                districtSpinnerValue.add(districtListValue.get(i).getDistrictName());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity2.this, R.layout.spinner_item, districtSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinDistrict.setAdapter(arrayAdapter);
                            spinDistrict.setSelection(0);

                            if (ad != null) {
                                for (int i = 0; i < districtListValue.size(); i++) {
                                    if (districtListValue.get(i).getDistrictId().equalsIgnoreCase(ad.getDistrictId())) {
                                        spinDistrict.setSelection(i);
                                    }
                                }
                            }

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity2.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity2.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(AdvertiseBusinessActivity2.this, AdvertiseBusinessActivity2.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                    if (error instanceof NoConnectionError) {
                        System.out.println("NoConnectionError");
                    } else if (error instanceof TimeoutError) {
                        System.out.println("TimeoutError");

                    } else if (error instanceof ServerError) {
                        System.out.println("ServerError");

                    } else if (error instanceof AuthFailureError) {
                        System.out.println("AuthFailureError");

                    } else if (error instanceof NetworkError) {
                        System.out.println("NetworkError");
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("stateId", strStateId);
                    System.out.println(Tag + " getDistrictList inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, AdvertiseBusinessActivity2.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getStateList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(AdvertiseBusinessActivity2.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.stateList, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getStateList response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getStateList result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            stateListValue.clear();
                            str_message = obj.getString("message");

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                StateData stateData = new StateData(
                                        jsonObject.getString("stateId"),
                                        jsonObject.getString("stateName"));

                                stateListValue.add(stateData);
                            }

                            StateData initStateData = new StateData();
                            initStateData.setStateId("-1");
                            initStateData.setStateName("State");
                            stateListValue.add(0, initStateData);

                            stateSpinnerValue.clear();
                            for (int i = 0; i < stateListValue.size(); i++) {
                                stateSpinnerValue.add(stateListValue.get(i).getStateName());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity2.this, R.layout.spinner_item, stateSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinState.setAdapter(arrayAdapter);
                            spinState.setSelection(0);

                            if (ad != null) {
                                for (int i = 0; i < stateListValue.size(); i++) {
                                    if (stateListValue.get(i).getStateId().equalsIgnoreCase(ad.getStateId())) {
                                        spinState.setSelection(i);
                                    }
                                }
                            }

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity2.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity2.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(AdvertiseBusinessActivity2.this, AdvertiseBusinessActivity2.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                    if (error instanceof NoConnectionError) {
                        System.out.println("NoConnectionError");
                    } else if (error instanceof TimeoutError) {
                        System.out.println("TimeoutError");

                    } else if (error instanceof ServerError) {
                        System.out.println("ServerError");

                    } else if (error instanceof AuthFailureError) {
                        System.out.println("AuthFailureError");

                    } else if (error instanceof NetworkError) {
                        System.out.println("NetworkError");
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return new HashMap<>();
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, AdvertiseBusinessActivity2.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
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
        AddressDetailsData addressDetailsData = new AddressDetailsData(
                strDoorNo,
                strLocality,
                strArea,
                strPost,
                strLandmark,
                strPinCode,
                strStateId,
                strDistrictId,
                strState,
                strDistrict
        );
        Utils.saveAddressDetails(addressDetailsData);
        finish();
    }
}