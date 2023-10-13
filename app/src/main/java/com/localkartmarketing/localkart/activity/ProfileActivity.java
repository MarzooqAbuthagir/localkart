package com.localkartmarketing.localkart.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.DistrictData;
import com.localkartmarketing.localkart.model.StateData;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.LoginSharedPreference;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private String Tag = "ProfileActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    EditText etName, etMobile, etEmail, etCountry, etLanguage, etUserType, etUserId, etState, etDistrict;
    private SearchableSpinner spinState;
    private final ArrayList<StateData> stateListValue = new ArrayList<StateData>();
    private final List<String> stateSpinnerValue = new ArrayList<>();

    private SearchableSpinner spinDistrict;
    private final ArrayList<DistrictData> districtListValue = new ArrayList<DistrictData>();
    private final List<String> districtSpinnerValue = new ArrayList<>();

    private String strName = "", strMobile = "", strEmail = "", strCountry = "", strLanguage = "", strUserType = "", strUserId = "", strStateId = "", strDistrictId = "";
    String str_result = "", str_message = "";

    UserDetail obj;
    static SharedPreferences mPrefs;

    boolean isAllFieldsChecked = false;

    ImageView imgDp;
    String base64img = "";

    File photoFile = null;
    String mCurrentPhotoPath;
    Uri photoURI;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    int CAPTURE_IMAGE_REQUEST = 1;
    int SELECT_IMAGE_REQUEST = 2;

    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        utils = new Utils(ProfileActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Profile");

        imgDp = findViewById(R.id.img_dp);
        etName = findViewById(R.id.et_name);
        etMobile = findViewById(R.id.et_mobile);
        etEmail = findViewById(R.id.et_email);
        spinState = findViewById(R.id.spin_state);
        spinDistrict = findViewById(R.id.spin_district);
        etCountry = findViewById(R.id.et_country);
        etLanguage = findViewById(R.id.et_lang);
        etUserType = findViewById(R.id.et_user_type);
        etUserId = findViewById(R.id.et_user_id);

        etState = findViewById(R.id.et_state);
        etDistrict = findViewById(R.id.et_district);

        spinState.setTitle("Select State");
        spinDistrict.setTitle("Select District / Zone");

        imgDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions(ProfileActivity.this)) {
                    chooseImage(ProfileActivity.this);
                }
            }
        });

        getProfileDetails();

        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    spinDistrict.setEnabled(true);
                    strStateId = stateListValue.get(i).getStateId();
                    getDistrictList();
                } else {
                    districtListValue.clear();
                    spinDistrict.setEnabled(false);
                    strStateId = "";
                    DistrictData initDistrictData = new DistrictData();
                    initDistrictData.setDistrictId("-1");
                    initDistrictData.setDistrictName("District / Zone");
                    districtListValue.add(0, initDistrictData);

                    districtSpinnerValue.add(districtListValue.get(0).getDistrictName());

                    ArrayAdapter arrayAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.spinner_item, districtSpinnerValue);
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
                } else {
                    strDistrictId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strName = etName.getText().toString().trim();
                strMobile = etMobile.getText().toString().trim();
                strEmail = etEmail.getText().toString().trim();

                isAllFieldsChecked = validateString();

                if (isAllFieldsChecked) {
                    updateProfile();
                }
            }
        });
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Utils.getAndroidOs());
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Utils.getAndroidOs());
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void updateProfile() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(ProfileActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.updateprofile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " updateProfile response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " updateProfile result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");

                            UserDetail userDetail = new UserDetail();
                            userDetail.setId(json.getString("Id"));
                            userDetail.setMobile(json.getString("Phone"));
                            userDetail.setName(json.getString("Name"));
                            userDetail.setStateId(json.getString("stateId"));
                            userDetail.setDistrictId(json.getString("districtId"));
                            if (json.getString("Email").equalsIgnoreCase("null"))
                                userDetail.setEmail("");
                            else
                                userDetail.setEmail(json.getString("Email"));
                            userDetail.setImage(json.getString("profileImage"));

                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            String gsonJson = gson.toJson(userDetail);
                            prefsEditor.putString("MyObject", gsonJson);
                            prefsEditor.apply();

                            back();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", obj.getId());
                    params.put("name", strName);
                    params.put("stateId", strStateId);
                    params.put("districtId", strDistrictId);
                    params.put("emailAddress", strEmail);
                    params.put("profileImage", base64img);
                    System.out.println(Tag + " updateProfile inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateString() {
        if (strName.isEmpty()) {
            etName.requestFocus();
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strEmail.length() > 0 && Utils.eMailValidation(strEmail)) {
            etEmail.requestFocus();
            Toast.makeText(ProfileActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strStateId.isEmpty()) {
            Toast.makeText(this, "Select state", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strDistrictId.isEmpty()) {
            Toast.makeText(this, "Select district / zone", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getProfileDetails() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(ProfileActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.getprofiledetails, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getProfileDetails response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getProfileDetails result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");

                            etName.setText(json.getString("customerName"));
                            etMobile.setText(json.getString("mobileNumber"));
                            if (json.getString("emailAddress").equalsIgnoreCase("null"))
                                etEmail.setText("");
                            else
                                etEmail.setText(json.getString("emailAddress"));
                            etCountry.setText(json.getString("country"));
                            etLanguage.setText(json.getString("Language"));
                            etUserType.setText(json.getString("userType"));
                            etUserId.setText(json.getString("userId"));

                            strStateId = json.getString("state");
                            strDistrictId = json.getString("district");

                            Glide.with(ProfileActivity.this).load(json.getString("profileImage"))
                                    .placeholder(R.drawable.placeholder_profile)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgDp);

                            getStateList();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", obj.getId());
                    System.out.println(Tag + " getProfileDetails inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDistrictList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(ProfileActivity.this);

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

                            int prefPos = 0;
                            for (int i = 0; i < districtListValue.size(); i++) {
                                if (districtListValue.get(i).getDistrictId().equalsIgnoreCase(strDistrictId)) {
                                    prefPos = i;
                                    etDistrict.setText(districtListValue.get(i).getDistrictName());
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.spinner_item, districtSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinDistrict.setAdapter(arrayAdapter);
                            spinDistrict.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getStateList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(ProfileActivity.this);

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

                            int prefPos = 0;
                            for (int i = 0; i < stateListValue.size(); i++) {
                                if (stateListValue.get(i).getStateId().equalsIgnoreCase(strStateId)) {
                                    prefPos = i;
                                    getDistrictList();
                                    etState.setText(stateListValue.get(i).getStateName());
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.spinner_item, stateSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinState.setAdapter(arrayAdapter);
                            spinState.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                                "Permission Requires to Access Camera.", Toast.LENGTH_SHORT)
                        .show();
            } else if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                    Utils.getAndroidOs()) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Your Storage.",
                        Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(ProfileActivity.this);
            }
        }
    }

    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Cancel"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Take Photo")) {

//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                        try {
//                            photoFile = createImageFile();
//
//                            photoURI = FileProvider.getUriForFile(
//                                    ProfileActivity.this,
//                                    "com.localkartmarketing.localkart.fileprovider",
//                                    photoFile
//                            );
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
//
//                        } catch (IOException ex) {
//                            // Error occurred while creating the File
//                        }
//                    }

                    fileName = System.currentTimeMillis() + ".jpg";
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }

                } else if (optionsMenu[i].equals("Choose from Gallery")) {
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, SELECT_IMAGE_REQUEST);
                } else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(ProfileActivity.this, getPackageName() + ".provider", image);
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(1, 1).setRequestedSize(300,300).start(ProfileActivity.this);
            cropImage(getCacheImagePath(fileName));
//        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            Uri resultUri = result.getUri();
            Uri resultUri = UCrop.getOutput(data);
            onSelectFromGalleryResult(resultUri);
        } else if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(1, 1).setRequestedSize(300,300).start(ProfileActivity.this);
            cropImage(photoURI);
        }
    }

    private void cropImage(Uri sourceUri) {
        int IMAGE_COMPRESSION = 100;
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        options.setHideBottomControls(true);
        options.withAspectRatio(1, 1);

        options.setToolbarTitle("Crop Photo");
        options.setToolbarColor(ContextCompat.getColor(this, R.color.adv_bus_color));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.adv_bus_color));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.adv_bus_color));

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void onSelectFromGalleryResult(Uri data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        imgDp.setImageBitmap(bm);
        Glide.with(ProfileActivity.this).asBitmap().load(bm)
                .placeholder(R.drawable.placeholder_profile)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgDp);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("Gallery image " + base64img);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ic_delete) {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfileActivity.this);

            builder
                    .setMessage("Are you sure you want to delete ?")
                    .setPositiveButton(ProfileActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            dialog.dismiss();

                            deleteUserAccount();
                        }
                    })
                    .setNegativeButton(ProfileActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

            android.app.AlertDialog alert = builder.create();
            alert.show();

            Button btn_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            Button btn_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);

            btn_no.setTextColor(Color.parseColor("#000000"));
            btn_yes.setTextColor(Color.parseColor("#000000"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteUserAccount() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(ProfileActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.userdelete, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " deleteUserAccount response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " deleteUserAccount result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            stateListValue.clear();
                            str_message = obj.getString("message");

                            toLogoutCustomer();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", obj.getId());
                    System.out.println(Tag + " deleteUserAccount inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(ProfileActivity.this, ProfileActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void toLogoutCustomer() {
        LoginSharedPreference.setLoggedIn(ProfileActivity.this, false);

        SharedPreferences preferences = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }
}