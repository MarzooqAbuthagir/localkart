package com.localkartmarketing.localkart.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.AddServiceAdapter;
import com.localkartmarketing.localkart.adapter.ImageAdapter;
import com.localkartmarketing.localkart.model.AddressDetailsData;
import com.localkartmarketing.localkart.model.BasicDetailsData;
import com.localkartmarketing.localkart.model.ContactDetailsData;
import com.localkartmarketing.localkart.model.LocationData;
import com.localkartmarketing.localkart.model.UploadImages;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.MyGridView;
import com.localkartmarketing.localkart.support.RegBusinessIdSharedPreference;
import com.localkartmarketing.localkart.support.RegBusinessSharedPrefrence;
import com.localkartmarketing.localkart.support.RegBusinessTypeSharedPreference;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.yalantis.ucrop.UCrop;

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

public class MyBusinessActivity5 extends AppCompatActivity {
    private String Tag = "MyBusinessActivity5";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strBusinessId = "";

    private EditText etKeyword;
    private ChipGroup chipGroup;

    private MyGridView gridView;

    private List<UploadImages> arrayList;
    //    NestedScrollView nestedScrollView;
    LinearLayout layGrid;
    String strChip;

    UserDetail obj;
    static SharedPreferences mPrefs;

    File photoFile = null;
    String mCurrentPhotoPath;
    Uri photoURI;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    int CAPTURE_IMAGE_REQUEST = 1;
    int SELECT_IMAGE_REQUEST = 2;

    ImageAdapter adapter;

    BasicDetailsData basicDetailsData;
    AddressDetailsData addressDetailsData;
    ContactDetailsData contactDetailsData;
    LocationData locationData;

    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business5);

        utils = new Utils(MyBusinessActivity5.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strBusinessId = intent.getStringExtra("businessType");
        basicDetailsData = Utils.getBasicDetails(MyBusinessActivity5.this);
        addressDetailsData = Utils.getAddressDetails(MyBusinessActivity5.this);
        contactDetailsData = Utils.getContactDetails(MyBusinessActivity5.this);
        locationData = Utils.getLocDetails(MyBusinessActivity5.this);

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
        final BasicDetailsData basicDetailsData = Utils.getBasicDetails(MyBusinessActivity5.this);
        if (strBusinessId.equalsIgnoreCase("1")) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 10);
            progressView.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(900, 10);
            progressView.setLayoutParams(lp);
        }

        etKeyword = findViewById(R.id.et_keyword);
        Button btnAddTag = findViewById(R.id.btn_add_tags);
        chipGroup = findViewById(R.id.chipGroup);

        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewChip();
            }
        });

//        nestedScrollView = findViewById(R.id.nested_scroll_view);
        layGrid = findViewById(R.id.lay_grid);
        arrayList = new ArrayList<>();
        arrayList = Utils.getImageList("imageList");
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
        gridView = findViewById(R.id.grid_image);
        adapter = new ImageAdapter(MyBusinessActivity5.this, arrayList, 1);
        gridView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AddServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onCustomItemClick(position);
            }
        });

        Button btnUploadPhoto = findViewById(R.id.btn_upload_photo);
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.size() < 2) {
                    if (checkAndRequestPermissions(MyBusinessActivity5.this)) {
                        chooseImage(MyBusinessActivity5.this);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyBusinessActivity5.this);
                    builder.setMessage("Maximum Photo Limit (2) Reached. You can upload more photos in your profile section after successful registration.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                    btnOk.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        LinearLayout layTC = findViewById(R.id.lay_tc);
        if (strBusinessId.equalsIgnoreCase("1")) {
            layTC.setVisibility(View.VISIBLE);
        } else {
            layTC.setVisibility(View.GONE);
        }
        final CheckBox checkBox = findViewById(R.id.checkbox);
        TextView tvBusTC = findViewById(R.id.tv_bus_tc);
        tvBusTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyBusinessActivity5.this, BusinessTCActivity.class));
            }
        });

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnNext = findViewById(R.id.btn_next);
        if (strBusinessId.equalsIgnoreCase("1")) {
            btnNext.setText("Update");
        } else {
            btnNext.setText("Next");
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = chipGroup.getChildCount();

                strChip = "";
                for (int i = 0; i < count; i++) {
                    Chip child = (Chip) chipGroup.getChildAt(i);

                    if (!child.isChecked()) {
                        continue;
                    }

                    if (strChip.isEmpty()) {
                        strChip = child.getText().toString();
                    } else {
                        strChip += ", " + child.getText().toString();
                    }
                }

                if (strChip.isEmpty()) {
                    etKeyword.requestFocus();
                    Toast.makeText(MyBusinessActivity5.this, "Enter keyword", Toast.LENGTH_SHORT).show();
                } else {
                    if (strBusinessId.equalsIgnoreCase("1")) {
                        if (!checkBox.isChecked()) {
                            Toast.makeText(MyBusinessActivity5.this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show();
                        } else {
                            updateBusiness(view);
                        }
                    } else {
                        Utils.saveChip(strChip);
                        Utils.clearImageList(MyBusinessActivity5.this);
                        Utils.setImageList("imageList", arrayList, MyBusinessActivity5.this);
                        Intent intent = new Intent(MyBusinessActivity5.this, MyBusinessActivity6.class);
                        intent.putExtra("key", keyIntent);
                        intent.putExtra("businessType", strBusinessId);
                        intent.putExtra("chips", strChip);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }
        });

        setChip();
    }

    private void updateBusiness(final View view) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(MyBusinessActivity5.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.businessupdate, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " updateBusiness response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " updateBusiness result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");
                            String str_index_id = obj.getString("indexId");
                            uploadImages(str_index_id, view);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MyBusinessActivity5.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(MyBusinessActivity5.this, MyBusinessActivity5.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("type", basicDetailsData.getBusinessType());
                    params.put("indexId", basicDetailsData.getIndexId());
                    params.put("name", basicDetailsData.getBusinessName());
                    params.put("catId", basicDetailsData.getCategoryId());
                    params.put("subCatId", basicDetailsData.getSubCategoryId());
                    params.put("Image", basicDetailsData.getLogo());
                    params.put("description", basicDetailsData.getDesc());
                    params.put("doorNo", addressDetailsData.getDoorNo());
                    params.put("locality", addressDetailsData.getLocality());
                    params.put("area", addressDetailsData.getArea());
                    params.put("taulk", addressDetailsData.getPost());
                    params.put("landMark", addressDetailsData.getLandmark());
                    params.put("pincode", addressDetailsData.getPinCode());
                    params.put("state", addressDetailsData.getStateId());
                    params.put("district", addressDetailsData.getDistrictId());
                    params.put("phoneNo", contactDetailsData.getPhoneNo());
                    params.put("mobileNo", contactDetailsData.getMobileNo());
                    params.put("alternateNo", contactDetailsData.getAltNo());
                    params.put("watsappNo", contactDetailsData.getWhatsappNo());
                    params.put("emailAddress", contactDetailsData.getEmailId());
                    params.put("website", contactDetailsData.getWebsite());
                    params.put("facebook", contactDetailsData.getFacebook());
                    params.put("digitalVcard", contactDetailsData.getVcard());
                    params.put("cod", contactDetailsData.getCod());
                    params.put("latitude", locationData.getLatitude());
                    params.put("longitude", locationData.getLongitude());
                    params.put("address", locationData.getAddress());
                    params.put("tags", strChip);
                    System.out.println(Tag + " updateBusiness inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, MyBusinessActivity5.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    ProgressDialog progressDialog;

    private void uploadImages(final String str_index_id, final View view) {
        final List<UploadImages> list = new ArrayList<>();
        for (int i=0; i<arrayList.size(); i++) {
            if (arrayList.get(i).getImageIndexId().isEmpty()) {
                list.add(arrayList.get(i));
            }
        }

        if (list.size() > 0) {
            progressDialog = ProgressDialog.show(MyBusinessActivity5.this, "Uploading Images",
                    "Please wait...", true);

            for (int i = 0; i < list.size(); i++) {

                final int currentPos = i;
                StringRequest request = new StringRequest(Request.Method.POST, Utils.Api + Utils.uploadimage, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        if (currentPos + 1 == list.size()) {
                            System.out.println("on upload " + currentPos + " array size " + list.size());
                            progressDialog.dismiss();

                            successfulUpdate(view, str_index_id);

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(MyBusinessActivity5.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("Image", list.get(currentPos).getImage());
                        parameters.put("type", basicDetailsData.getBusinessType());
                        parameters.put("indexId", str_index_id);
                        System.out.println(Tag + " ImageUploadTask inputs " + parameters);
                        return parameters;
                    }
                };

                RequestQueue rQueue = Volley.newRequestQueue(MyBusinessActivity5.this);
                rQueue.add(request);

            }

        } else {
            successfulUpdate(view, str_index_id);
        }
    }

    private void successfulUpdate(View view, final String str_index_id) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyBusinessActivity5.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_business_update, viewGroup, false);
        Button btnManageMyBusiness = dialogView.findViewById(R.id.btn_manage_my_business);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        btnManageMyBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                RegBusinessSharedPrefrence.setMenuFlag(MyBusinessActivity5.this, "1");

                RegBusinessTypeSharedPreference.setBusinessType(MyBusinessActivity5.this, "Shopping");
                RegBusinessIdSharedPreference.setBusinessId(MyBusinessActivity5.this, str_index_id);

                Utils.clearRegPref(MyBusinessActivity5.this);

                Intent intent = new Intent(MyBusinessActivity5.this, ManageBusinessActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        alertDialog.show();
    }

    String str_result = "", str_message = "";

    private void onCustomItemClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyBusinessActivity5.this);
        builder.setTitle("Confirmation")
                .setMessage("Are you sure want to delete?")
                .setPositiveButton(MyBusinessActivity5.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();

                        if (arrayList.get(position).getImageIndexId().isEmpty()) {
                            arrayList.remove(position);
                            adapter.notifyDataSetChanged();
                            if (arrayList.size() > 0) {
//                                nestedScrollView.setVisibility(View.VISIBLE);
                                layGrid.setVisibility(View.VISIBLE);
                            } else {
//                                nestedScrollView.setVisibility(View.GONE);
                                layGrid.setVisibility(View.GONE);
                            }
                        } else {
                            if (Utils.isInternetOn()) {
                                Utils.showProgress(MyBusinessActivity5.this);

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.deletebusinessbanner, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            //converting response to json object
                                            JSONObject obj = new JSONObject(response);

                                            System.out.println(Tag + " deletebusinessbanner response - " + response);

                                            Utils.dismissProgress();

                                            str_result = obj.getString("errorCode");
                                            System.out.print(Tag + " deletebusinessbanner result " + str_result);

                                            if (Integer.parseInt(str_result) == 0) {

                                                str_message = obj.getString("message");

                                                arrayList.remove(position);
                                                adapter.notifyDataSetChanged();
                                                if (arrayList.size() > 0) {
//                                nestedScrollView.setVisibility(View.VISIBLE);
                                                    layGrid.setVisibility(View.VISIBLE);
                                                } else {
//                                nestedScrollView.setVisibility(View.GONE);
                                                    layGrid.setVisibility(View.GONE);
                                                }

                                            } else if (Integer.parseInt(str_result) == 2) {
                                                str_message = obj.getString("message");
                                                Toast.makeText(MyBusinessActivity5.this, str_message, Toast.LENGTH_SHORT).show();
                                            } else if (Integer.parseInt(str_result) == 1) {
                                                str_message = obj.getString("message");
                                                Toast.makeText(MyBusinessActivity5.this, str_message, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Utils.dismissProgress();
                                        Toast.makeText(MyBusinessActivity5.this, MyBusinessActivity5.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                                        params.put("imageIndexId", arrayList.get(position).getImageIndexId());
                                        System.out.println(Tag + " deletebusinessbanner inputs " + params);
                                        return params;
                                    }
                                };

                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                VolleySingleton.getInstance(MyBusinessActivity5.this).addToRequestQueue(stringRequest);
                            } else {
                                Toast.makeText(MyBusinessActivity5.this, MyBusinessActivity5.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton(MyBusinessActivity5.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

        Button btn_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        Button btn_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);

        btn_no.setTextColor(Color.parseColor("#000000"));
        btn_yes.setTextColor(Color.parseColor("#000000"));
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

    private void addNewChip() {
        String keyword = etKeyword.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(MyBusinessActivity5.this, "Enter keyword", Toast.LENGTH_LONG).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(MyBusinessActivity5.this);

        // Create a Chip from Layout.
        Chip newChip = (Chip) inflater.inflate(R.layout.chip_entry, chipGroup, false);
        newChip.setText(keyword);


        chipGroup.addView(newChip);
        chipGroup.setVisibility(View.VISIBLE);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipCloseIconClicked((Chip) v);
            }
        });

        etKeyword.setText("");
    }

    private void handleChipCloseIconClicked(Chip chip) {
        ChipGroup parent = (ChipGroup) chip.getParent();
        parent.removeView(chip);

        int count = chipGroup.getChildCount();
        if (count > 0) {
            chipGroup.setVisibility(View.VISIBLE);
        } else {
            chipGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(MyBusinessActivity5.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Camera.", Toast.LENGTH_SHORT)
                        .show();
            } else if (ContextCompat.checkSelfPermission(MyBusinessActivity5.this,
                    Utils.getAndroidOs()) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Your Storage.",
                        Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(MyBusinessActivity5.this);
            }
        }
    }

    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Cancel"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
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
//                                    MyBusinessActivity5.this,
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
        return getUriForFile(MyBusinessActivity5.this, getPackageName() + ".provider", image);
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
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(MyBusinessActivity5.this);
            cropImage(getCacheImagePath(fileName));
//        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            Uri resultUri = result.getUri();
            Uri resultUri = UCrop.getOutput(data);
            onSelectFromGalleryResult(resultUri);
        } else if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(MyBusinessActivity5.this);
            cropImage(photoURI);
        }
    }


    private void cropImage(Uri sourceUri) {
        int IMAGE_COMPRESSION = 100;
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        options.setHideBottomControls(true);
        options.withAspectRatio(16, 9);

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

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("Gallery image " + base64img);

        UploadImages uploadImages = new UploadImages(
                base64img,
                bm,
                ""
        );
        arrayList.add(0, uploadImages);

        adapter = new ImageAdapter(MyBusinessActivity5.this, arrayList, 1);
        gridView.setAdapter(adapter);

        if (arrayList.size() > 0) {
//                        nestedScrollView.setVisibility(View.VISIBLE);
            layGrid.setVisibility(View.VISIBLE);
        } else {
//                        nestedScrollView.setVisibility(View.GONE);
            layGrid.setVisibility(View.GONE);
        }

        adapter.setOnItemClickListener(new AddServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onCustomItemClick(position);
            }
        });
    }

    private void setChip() {
        String prefChips = Utils.getChips(MyBusinessActivity5.this);

        if (prefChips.isEmpty()) {
            System.out.println("chips are empty");
        } else {
            String[] arrPrefChips = prefChips.split(",");

            for (String arrPrefChip : arrPrefChips) {

                LayoutInflater inflater = LayoutInflater.from(MyBusinessActivity5.this);

                // Create a Chip from Layout.
                Chip newChip = (Chip) inflater.inflate(R.layout.chip_entry, chipGroup, false);
                newChip.setText(arrPrefChip);

                chipGroup.addView(newChip);
                chipGroup.setVisibility(View.VISIBLE);

                newChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleChipCloseIconClicked((Chip) v);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        int count = chipGroup.getChildCount();

        strChip = "";
        for (int i = 0; i < count; i++) {
            Chip child = (Chip) chipGroup.getChildAt(i);

            if (!child.isChecked()) {
                continue;
            }

            if (strChip.isEmpty()) {
                strChip = child.getText().toString();
            } else {
                strChip += ", " + child.getText().toString();
            }
        }

        Utils.saveChip(strChip);
        Utils.clearImageList(MyBusinessActivity5.this);
        Utils.setImageList("imageList", arrayList, MyBusinessActivity5.this);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        MenuItem menuItem = menu.getItem(0);
        if (strBusinessId.equalsIgnoreCase("1")) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ic_delete) {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyBusinessActivity5.this);

            builder
                    .setMessage("Are you sure you want to delete ?")
                    .setPositiveButton(MyBusinessActivity5.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            dialog.dismiss();
                            deleteBusinessAccount();

                        }
                    })
                    .setNegativeButton(MyBusinessActivity5.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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

    private void deleteBusinessAccount() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(MyBusinessActivity5.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.businessdelete, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " businessdelete response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " businessdelete result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            Utils.clearRegPref(MyBusinessActivity5.this);
                            RegBusinessSharedPrefrence.setMenuFlag(MyBusinessActivity5.this, "0");

                            RegBusinessTypeSharedPreference.setBusinessType(MyBusinessActivity5.this, "");
                            RegBusinessIdSharedPreference.setBusinessId(MyBusinessActivity5.this, "");

                            Intent intent = new Intent(MyBusinessActivity5.this, MainActivity.class);
                            intent.putExtra("key", "Shopping");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(MyBusinessActivity5.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MyBusinessActivity5.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(MyBusinessActivity5.this, MyBusinessActivity5.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("businessIndexId", basicDetailsData.getIndexId());
                    params.put("businessType", basicDetailsData.getBusinessType());
                    System.out.println(Tag + " businessdelete inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, MyBusinessActivity5.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }
}