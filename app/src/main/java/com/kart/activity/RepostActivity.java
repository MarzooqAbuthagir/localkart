package com.kart.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.kart.R;
import com.kart.adapter.OfferData;
import com.kart.adapter.RepostOfferAdapter;
import com.kart.model.PostInitialData;
import com.kart.model.UserDetail;
import com.kart.support.App;
import com.kart.support.LocationTrack;
import com.kart.support.RegBusinessTypeSharedPreference;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RepostActivity extends AppCompatActivity {
    private String Tag = "RepostActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strPostIndexId = "", strShopIndexId = "", strShopType = "";
    EditText etFromDate, etToDate, etFestivalName;
    LinearLayout fromDateLayout, toDateLayout;
    TextInputLayout layFestival;

    RepostOfferAdapter repostOfferAdapter;
    RecyclerView recyclerView;
    List<OfferData> listOfOffer = new ArrayList<>();

    String base64img = "";
    Bitmap bitmap;
    ImageView imageView;

    private SearchableSpinner spinPostType;
    private SearchableSpinner spinAccessOption;
    String str_result = "", str_message = "", strOfferCount = "";
    private ArrayList<PostInitialData> selectTypeListValue = new ArrayList<PostInitialData>();
    private ArrayList<PostInitialData> accessOptionListValue = new ArrayList<PostInitialData>();
    private List<String> postTypeSpinnerValue = new ArrayList<>();
    private List<String> accessOptionSpinnerValue = new ArrayList<>();
    private String strPostType = "", strAccessOption = "", strFromDate = "", strToDate = "";

    UserDetail obj;
    static SharedPreferences mPrefs;

    LocationTrack currentLocation;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CODE = 102;

    private String oldPostType = "", oldFromDate = "", oldToDate = "", oldAccessOption = "", oldFestivalName = "";

    File photoFile = null;
    String mCurrentPhotoPath;
    Uri photoURI;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    int CAPTURE_IMAGE_REQUEST = 1;
    int SELECT_IMAGE_REQUEST = 2;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repost);

        utilis = new Utilis(RepostActivity.this);

        app = (App) getApplication();

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strPostIndexId = intent.getStringExtra("postIndexId");
        strShopIndexId = intent.getStringExtra("shopIndexId");
        strShopType = intent.getStringExtra("type");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(RepostActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Repost");

        layFestival = findViewById(R.id.lay_festival);
        etFestivalName = findViewById(R.id.et_festival_name);
        spinPostType = findViewById(R.id.spin_post_type);
        spinPostType.setTitle("Select Post Type");
        spinAccessOption = findViewById(R.id.spin_access_option);
        spinAccessOption.setTitle("Access Option");

        Button btnAddOffer = findViewById(R.id.btn_add_offer);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RepostActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                base64img = "";
                addOfferDialog(view);
            }
        });

        fromDateLayout = findViewById(R.id.from_date_layout);
        etFromDate = findViewById(R.id.from_date_txt);

        toDateLayout = findViewById(R.id.to_date_layout);
        etToDate = findViewById(R.id.to_date_txt);

        etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFromDate();
            }
        });

        fromDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFromDate();
            }
        });

        toDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectToDate();
            }
        });

        etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectToDate();
            }
        });

        getApiCall();

        spinPostType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strPostType = selectTypeListValue.get(i).getKey();
                } else {
                    strPostType = "";
                }

                if (strPostType.equalsIgnoreCase("3")) {
                    layFestival.setVisibility(View.VISIBLE);
                } else {
                    layFestival.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinAccessOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strAccessOption = accessOptionListValue.get(i).getKey();
                } else {
                    strAccessOption = "";
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

                if (strPostType.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select post type", Toast.LENGTH_SHORT).show();
                } else if (strFromDate.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select from date", Toast.LENGTH_SHORT).show();
                } else if (strToDate.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select to date", Toast.LENGTH_SHORT).show();
                } else if (!checkDates()) {
                    Toast.makeText(RepostActivity.this, "Check from and to date", Toast.LENGTH_SHORT).show();
                } else {
                    if (strPostType.equalsIgnoreCase("2")) {
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            Date date1 = myFormat.parse(etFromDate.getText().toString());
                            Date date2 = myFormat.parse(etToDate.getText().toString());
                            long diff = date2.getTime() - date1.getTime();
                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            int noOfDays = (int) days;
                            int diffDays = noOfDays + 1;
                            if (diffDays > 7) {
                                Toast.makeText(RepostActivity.this, "Only 7 days allowed for weekly post", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(RepostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(RepostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                fetchLastLocation();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (strPostType.equalsIgnoreCase("3")) {
                            if (etFestivalName.getText().toString().trim().isEmpty()) {
                                Toast.makeText(RepostActivity.this, "Enter festival name", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(RepostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(RepostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                fetchLastLocation();
                            }
                        } else if (strAccessOption.isEmpty()) {
                            Toast.makeText(RepostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                        } else if (listOfOffer.size() == 0) {
                            Toast.makeText(RepostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                        } else {
                            fetchLastLocation();
                        }
                    }
                }
            }
        });

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strPostType.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select post type", Toast.LENGTH_SHORT).show();
                } else if (strFromDate.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select from date", Toast.LENGTH_SHORT).show();
                } else if (strToDate.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select to date", Toast.LENGTH_SHORT).show();
                } else if (!checkDates()) {
                    Toast.makeText(RepostActivity.this, "Check from and to date", Toast.LENGTH_SHORT).show();
                } else {
                    if (strPostType.equalsIgnoreCase("2")) {
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            Date date1 = myFormat.parse(etFromDate.getText().toString());
                            Date date2 = myFormat.parse(etToDate.getText().toString());
                            long diff = date2.getTime() - date1.getTime();
                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            int noOfDays = (int) days;
                            int diffDays = noOfDays + 1;
                            if (diffDays > 7) {
                                Toast.makeText(RepostActivity.this, "Only 7 days allowed for weekly post", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(RepostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(RepostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                postValidation();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (strPostType.equalsIgnoreCase("3")) {
                            if (etFestivalName.getText().toString().trim().isEmpty()) {
                                Toast.makeText(RepostActivity.this, "Enter festival name", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(RepostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(RepostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                postValidation();
                            }
                        } else if (strAccessOption.isEmpty()) {
                            Toast.makeText(RepostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                        } else if (listOfOffer.size() == 0) {
                            Toast.makeText(RepostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                        } else {
                            postValidation();
                        }
                    }
                }
            }
        });
    }

    private void addOfferDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RepostActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_offer, viewGroup, false);
        final EditText etHeading = dialogView.findViewById(R.id.et_heading);
        final EditText etDesc = dialogView.findViewById(R.id.et_desc);
        final TextView tvTitle = dialogView.findViewById(R.id.tv_offer);
        imageView = dialogView.findViewById(R.id.iv_img);

        if (listOfOffer.isEmpty()) {
            tvTitle.setText("Deal 1");
        } else {
            int size = listOfOffer.size() + 1;
            tvTitle.setText("Deal " + size);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions(RepostActivity.this)) {
                    chooseImage(RepostActivity.this);
                }
            }
        });

        final Button btnAdd = dialogView.findViewById(R.id.btn_add);
        Button btnClose = dialogView.findViewById(R.id.btn_close);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strHeading = etHeading.getText().toString().trim();
                String strDesc = etDesc.getText().toString().trim();
                int headingCount = strHeading.isEmpty() ? 0 : strHeading.split("\\s+").length;
                int descCount = strDesc.isEmpty() ? 0 : strDesc.split("\\s+").length;

                if (strHeading.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Enter heading", Toast.LENGTH_SHORT).show();
                } else if (headingCount > 8) {
                    Toast.makeText(RepostActivity.this, "Heading not exceeding 8 words", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 20) {
                    Toast.makeText(RepostActivity.this, "Description not exceeding 20 words", Toast.LENGTH_SHORT).show();
                } else if (base64img.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select image", Toast.LENGTH_SHORT).show();
                } else {
                    OfferData offerData = new OfferData("", strHeading, strDesc, "", bitmap, base64img);
                    listOfOffer.add(offerData);
                    repostOfferAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void getApiCall() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RepostActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.listarray, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("Message");

                            selectTypeListValue.clear();
                            accessOptionListValue.clear();

                            JSONObject json = obj.getJSONObject("selectType");
                            Iterator<String> iterator = json.keys();
                            List<String> keyArray = new ArrayList<>();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                keyArray.add(key);
                            }

                            for (int k = 0; k < keyArray.size(); k++) {
                                PostInitialData postInitialData = new PostInitialData(
                                        keyArray.get(k),
                                        json.optString(keyArray.get(k))
                                );

                                selectTypeListValue.add(postInitialData);
                            }

                            strOfferCount = obj.getString("count");

                            JSONArray jsonArray2 = obj.getJSONArray("accessOption");
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                                PostInitialData accessOptions = new PostInitialData(
                                        jsonObject.getString("keyName"),
                                        jsonObject.getString("value")
                                );
                                accessOptionListValue.add(accessOptions);
                            }

                            PostInitialData initPostData = new PostInitialData();
                            initPostData.setKey("-1");
                            initPostData.setValue("Select Post Type");
                            selectTypeListValue.add(0, initPostData);

                            postTypeSpinnerValue.clear();
                            for (int i = 0; i < selectTypeListValue.size(); i++) {
                                postTypeSpinnerValue.add(selectTypeListValue.get(i).getValue());
                            }

                            PostInitialData initPostData1 = new PostInitialData();
                            initPostData1.setKey("-1");
                            initPostData1.setValue("Access Option");
                            accessOptionListValue.add(0, initPostData1);

                            accessOptionSpinnerValue.clear();
                            for (int i = 0; i < accessOptionListValue.size(); i++) {
                                accessOptionSpinnerValue.add(accessOptionListValue.get(i).getValue());
                            }

                            getPostDetails();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(RepostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(RepostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(RepostActivity.this, RepostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("userIndexId", obj.getId());
                    params.put("type", RegBusinessTypeSharedPreference.getBusinessType(RepostActivity.this));
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, RepostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getPostDetails() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RepostActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.getrepost, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getPostDetails response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getPostDetails result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");

                            oldPostType = json.getString("postType");
                            oldFromDate = json.getString("fromDate");
                            oldToDate = json.getString("toDate");
                            oldAccessOption = json.getString("accessOptions");
                            oldFestivalName = json.getString("festivalName");

                            listOfOffer.clear();
                            JSONArray jsonArray1 = json.getJSONArray("offerImageList");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                OfferData offerData = new OfferData(
                                        object.getString("offerIndexId"),
                                        object.getString("heading"),
                                        object.getString("description"),
                                        object.getString("offerImage"),
                                        null,
                                        ""
                                );
                                listOfOffer.add(offerData);
                            }

                            repostOfferAdapter = new RepostOfferAdapter(RepostActivity.this, listOfOffer);
                            recyclerView.setAdapter(repostOfferAdapter);

                            repostOfferAdapter.setOnItemDeleteClickListener(new RepostOfferAdapter.OnItemDeleteClickListener() {
                                @Override
                                public void onItemDeleteClick(View view, int position) {
                                    listOfOffer.remove(position);
                                    repostOfferAdapter.notifyDataSetChanged();
                                }
                            });

                            repostOfferAdapter.setOnItemEditClickListener(new RepostOfferAdapter.OnItemEditClickListener() {
                                @Override
                                public void onItemEditClick(View view, int position) {
                                    OfferData offerData = listOfOffer.get(position);
                                    editOfferDialog(view, offerData, position);
                                }
                            });

                            ArrayAdapter arrayAdapter = new ArrayAdapter(RepostActivity.this, R.layout.spinner_item, postTypeSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinPostType.setAdapter(arrayAdapter);
                            spinPostType.setSelection(0);

                            for (int i = 0; i < selectTypeListValue.size(); i++) {
                                if (selectTypeListValue.get(i).getKey().equalsIgnoreCase(oldPostType)) {
                                    spinPostType.setSelection(i);
                                    break;
                                }
                            }


                            ArrayAdapter arrayAdapter1 = new ArrayAdapter(RepostActivity.this, R.layout.spinner_item, accessOptionSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinAccessOption.setAdapter(arrayAdapter1);
                            spinAccessOption.setSelection(0);

                            for (int i = 0; i < accessOptionListValue.size(); i++) {
                                if (accessOptionListValue.get(i).getKey().equalsIgnoreCase(oldAccessOption)) {
                                    spinAccessOption.setSelection(i);
                                    break;
                                }
                            }

                            if (oldFestivalName != null && !oldFestivalName.equalsIgnoreCase("null")) {
                                etFestivalName.setText(oldFestivalName.trim());
                            } else {
                                etFestivalName.setText("");
                            }

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(RepostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(RepostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(RepostActivity.this, RepostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("oldPostIndexId", strPostIndexId);
                    System.out.println(Tag + " getPostDetails inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, RepostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void editOfferDialog(View view, final OfferData offerData, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RepostActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_offer, viewGroup, false);
        final EditText etHeading = dialogView.findViewById(R.id.et_heading);
        final EditText etDesc = dialogView.findViewById(R.id.et_desc);
        final TextView tvTitle = dialogView.findViewById(R.id.tv_offer);
        imageView = dialogView.findViewById(R.id.iv_img);

        int size = position + 1;
        tvTitle.setText("Deal " + size);
        etHeading.setText(offerData.getHeading());
        etDesc.setText(offerData.getDesc());
        imageView.getLayoutParams().height = 500;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (offerData.getBitmap() != null) {
            imageView.setImageBitmap(offerData.getBitmap());
            base64img = offerData.getBase64Str();
            bitmap = offerData.getBitmap();
        } else {
            base64img = "";
            bitmap = null;
            Glide.with(RepostActivity.this).load(offerData.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions(RepostActivity.this)) {
                    chooseImage(RepostActivity.this);
                }
            }
        });

        final Button btnAdd = dialogView.findViewById(R.id.btn_add);
        Button btnClose = dialogView.findViewById(R.id.btn_close);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strHeading = etHeading.getText().toString().trim();
                String strDesc = etDesc.getText().toString().trim();
                int headingCount = strHeading.isEmpty() ? 0 : strHeading.split("\\s+").length;
                int descCount = strDesc.isEmpty() ? 0 : strDesc.split("\\s+").length;
                if (strHeading.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Enter heading", Toast.LENGTH_SHORT).show();
                } else if (headingCount > 8) {
                    Toast.makeText(RepostActivity.this, "Heading not exceeding 8 words", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 20) {
                    Toast.makeText(RepostActivity.this, "Description not exceeding 20 words", Toast.LENGTH_SHORT).show();
                } else if (base64img.isEmpty() && offerData.getImage().isEmpty()) {
                    Toast.makeText(RepostActivity.this, "Select image", Toast.LENGTH_SHORT).show();
                } else {
                    listOfOffer.remove(position);
                    OfferData data = new OfferData(offerData.getOfferIndexId(), strHeading, strDesc, offerData.getImage(), bitmap, base64img);
                    listOfOffer.add(position, data);
                    repostOfferAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void selectFromDate() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(RepostActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Display Selected date in textbox

                String strMonth = "";
                String strDay = "";
                String monthofYear = String.valueOf(monthOfYear + 1);

                try {
                    if (String.valueOf(monthOfYear + 1).length() == 1) {
                        strMonth = String.valueOf("0" + monthofYear);
                    } else {
                        strMonth = String.valueOf(monthofYear);
                    }

                    if (String.valueOf(dayOfMonth).length() == 1) {
                        strDay = String.valueOf("0" + dayOfMonth);
                    } else {
                        strDay = String.valueOf(dayOfMonth);
                    }
                } catch (Exception e) {
                    Log.e(Tag, "onDateSet: " + e.getMessage(), e);
                }

                String date = strDay + "-" + strMonth + "-" + year;

                etFromDate.setText(date);
                strFromDate = year + "-" + strMonth + "-" + strDay;

            }
        }, mYear, mMonth, mDay);
        dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //ignored
            }
        });
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
    }

    private void selectToDate() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(RepostActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Display Selected date in textbox

                String strMonth = "";
                String strDay = "";
                String monthofYear = String.valueOf(monthOfYear + 1);

                try {
                    if (String.valueOf(monthOfYear + 1).length() == 1) {
                        strMonth = String.valueOf("0" + monthofYear);
                    } else {
                        strMonth = String.valueOf(monthofYear);
                    }

                    if (String.valueOf(dayOfMonth).length() == 1) {
                        strDay = String.valueOf("0" + dayOfMonth);
                    } else {
                        strDay = String.valueOf(dayOfMonth);
                    }
                } catch (Exception e) {
                    Log.e(Tag, "onDateSet: " + e.getMessage(), e);
                }

                String date = strDay + "-" + strMonth + "-" + year;

                etToDate.setText(date);
                strToDate = year + "-" + strMonth + "-" + strDay;
            }
        }, mYear, mMonth, mDay);
        dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //ignored
            }
        });
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
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

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        try {
                            photoFile = createImageFile();

                            photoURI = FileProvider.getUriForFile(
                                    RepostActivity.this,
                                    "com.kart.fileprovider",
                                    photoFile
                            );
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);

                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
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

    private void onSelectFromGalleryResult(Uri data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmap = bm;

        imageView.getLayoutParams().height = 500;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("Gallery image " + base64img);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(RepostActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Camera.", Toast.LENGTH_SHORT)
                        .show();
            } else if (ContextCompat.checkSelfPermission(RepostActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Your Storage.",
                        Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(RepostActivity.this);
            }
        } else if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission Granted");
                if (Utilis.isGpsOn()) {
                    fetchLastLocation();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(RepostActivity.this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            onSelectFromGalleryResult(resultUri);
        } else if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(RepostActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(RepostActivity.this, HistoryActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        if (Utilis.isGpsOn()) {
            currentLocation = new LocationTrack(RepostActivity.this);

            if (currentLocation.canGetLocation()) {
                longitude = currentLocation.getLongitude();
                latitude = currentLocation.getLatitude();
                System.out.println("latitude " + latitude + " and longitude " + longitude);

                previewPost();
            }

        } else {
            Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkDates() {
        SimpleDateFormat dfDate = new SimpleDateFormat("dd-MM-yyyy");
        boolean dateChecker = false;
        try {
            //If start date is after the end date
            if (dfDate.parse(etFromDate.getText().toString()).before(dfDate.parse(etToDate.getText().toString()))) {
                dateChecker = true;//If start date is before end date
            } else
                dateChecker = dfDate.parse(etFromDate.getText().toString()).equals(dfDate.parse(etToDate.getText().toString()));//If two dates are equal
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("parse exception " + e.getMessage());
        }
        return dateChecker;
    }

    private void previewPost() {
        Utilis.clearReOfferList(RepostActivity.this);
        Utilis.setReOfferList("offerList", listOfOffer, RepostActivity.this);
        Intent intent = new Intent(RepostActivity.this, ViewRepostActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("fromDate", etFromDate.getText().toString().trim());
        intent.putExtra("toDate", etToDate.getText().toString().trim());
        intent.putExtra("fDate", strFromDate);
        intent.putExtra("tDate", strToDate);
        intent.putExtra("accessOption", strAccessOption);
        intent.putExtra("latitude", String.valueOf(latitude));
        intent.putExtra("longitude", String.valueOf(longitude));
        intent.putExtra("postType", strPostType);
        intent.putExtra("festivalName", etFestivalName.getText().toString());
        intent.putExtra("shopIndexId",strShopIndexId);
        intent.putExtra("shopType", strShopType);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void postValidation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RepostActivity.this);

        builder.setMessage("Post details cannot be changed once saved. Are you sure you want to save and show this post to customer?")
                .setPositiveButton(RepostActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        if (Utilis.isInternetOn()) {
                            Utilis.showProgress(RepostActivity.this);

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.postvalidation, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        //converting response to json object
                                        JSONObject obj = new JSONObject(response);

                                        System.out.println(Tag + " postValidation response - " + response);

                                        Utilis.dismissProgress();

                                        str_result = obj.getString("errorCode");
                                        System.out.print(Tag + " postValidation result " + str_result);

                                        if (Integer.parseInt(str_result) == 0) {

                                            str_message = obj.getString("Message");

                                            AlertDialog.Builder builder = new AlertDialog.Builder(RepostActivity.this);
                                            builder.setMessage(str_message)
                                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        } else if (Integer.parseInt(str_result) == 1) {
                                            str_message = obj.getString("Message");
                                            sendPost();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Utilis.dismissProgress();
                                    Toast.makeText(RepostActivity.this, RepostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("userIndexId", obj.getId());
                                    params.put("typeId", strPostType);
                                    params.put("fromDate", strFromDate);
                                    params.put("toDate", strToDate);
                                    params.put("count", String.valueOf(listOfOffer.size()));
                                    System.out.println(Tag + " postValidation inputs " + params);
                                    return params;
                                }
                            };

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            VolleySingleton.getInstance(RepostActivity.this).addToRequestQueue(stringRequest);
                        } else {
                            Toast.makeText(RepostActivity.this, RepostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(RepostActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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

    private void sendPost() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RepostActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.createpost, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " sendPost response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " sendPost result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");
                            String str_post_index_id = obj.getString("postIndexId");
                            String isBoost = obj.getString("isBoost");
                            sendOffer(str_post_index_id, isBoost);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(RepostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(RepostActivity.this, RepostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("userIndexId", obj.getId());
                    params.put("typeId", strPostType);
                    params.put("fromDate", strFromDate);
                    params.put("toDate", strToDate);
                    params.put("accessOptions", strAccessOption);
                    params.put("festivalName", etFestivalName.getText().toString());
                    System.out.println(Tag + " sendPost inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, RepostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendOffer(final String str_post_index_id, final String isBoost) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RepostActivity.this);

            for (int i = 0; i < listOfOffer.size(); i++) {

                final int currentPos = i;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.createrepostoffers, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            System.out.println(Tag + " sendOffer response - " + response);

                            str_result = obj.getString("errorCode");
                            System.out.print(Tag + " sendOffer result " + str_result);

                            if (Integer.parseInt(str_result) == 0) {
                                str_message = obj.getString("message");
                                if (currentPos + 1 == listOfOffer.size()) {
                                    if (isBoost.equalsIgnoreCase("Yes")) {
                                        app.notifyToUsers(str_post_index_id, strShopIndexId, strShopType);
                                    }
                                    Utilis.dismissProgress();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RepostActivity.this);
                                    builder.setMessage("Your post has been successfully saved.")
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    back();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }

                            } else if (Integer.parseInt(str_result) == 2) {
                                Utilis.dismissProgress();
                                str_message = obj.getString("message");
                                Toast.makeText(RepostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utilis.dismissProgress();
                        Toast.makeText(RepostActivity.this, RepostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("userIndexId", obj.getId());
                        params.put("heading", listOfOffer.get(currentPos).getHeading());
                        params.put("description", listOfOffer.get(currentPos).getDesc());
                        if (listOfOffer.get(currentPos).getBase64Str().isEmpty()) {
                            params.put("offerImage", "");
                        } else {
                            params.put("offerImage", listOfOffer.get(currentPos).getBase64Str());
                        }
                        params.put("offerIndexId", listOfOffer.get(currentPos).getOfferIndexId());
                        params.put("postIndexId", str_post_index_id);
                        System.out.println(Tag + " sendOffer inputs " + params);
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }

        } else {
            Toast.makeText(this, RepostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }
}