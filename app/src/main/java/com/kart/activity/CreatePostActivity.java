package com.kart.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.kart.R;
import com.kart.adapter.AddOfferAdapter;
import com.kart.model.AddOfferData;
import com.kart.model.PostInitialData;
import com.kart.model.UserDetail;
import com.kart.support.LocationTrack;
import com.kart.support.RegBusinessTypeSharedPreference;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CreatePostActivity extends AppCompatActivity {
    private String Tag = "CreatePostActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    EditText etFromDate, etToDate, etFestivalName;
    LinearLayout fromDateLayout, toDateLayout;
    TextInputLayout layFestival;

    AddOfferAdapter offerAdapter;
    RecyclerView recyclerView;
    List<AddOfferData> listOfOffer = new ArrayList<>();

    int SELECT_FILE = 102;
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
    private static final int REQUEST_CODE = 101;

    private static final int CROP_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        utilis = new Utilis(CreatePostActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(CreatePostActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Create Post");

        layFestival = findViewById(R.id.lay_festival);
        etFestivalName = findViewById(R.id.et_festival_name);
        spinPostType = findViewById(R.id.spin_post_type);
        spinPostType.setTitle("Select Post Type");
        spinAccessOption = findViewById(R.id.spin_access_option);
        spinAccessOption.setTitle("Access Option");

        Button btnAddOffer = findViewById(R.id.btn_add_offer);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreatePostActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                base64img = "";
                addOfferDialog(view);
            }
        });

        offerAdapter = new AddOfferAdapter(this, listOfOffer);
        recyclerView.setAdapter(offerAdapter);

        offerAdapter.setOnItemDeleteClickListener(new AddOfferAdapter.OnItemDeleteClickListener() {
            @Override
            public void onItemDeleteClick(View view, int position) {
                listOfOffer.remove(position);
                offerAdapter.notifyDataSetChanged();
            }
        });

        offerAdapter.setOnItemEditClickListener(new AddOfferAdapter.OnItemEditClickListener() {
            @Override
            public void onItemEditClick(View view, int position) {
                AddOfferData offerData = listOfOffer.get(position);
                editOfferDialog(view, offerData, position);
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
                    Toast.makeText(CreatePostActivity.this, "Select post type", Toast.LENGTH_SHORT).show();
                } else if (strFromDate.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Select from date", Toast.LENGTH_SHORT).show();
                } else if (strToDate.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Select to date", Toast.LENGTH_SHORT).show();
                } else if (!checkDates()) {
                    Toast.makeText(CreatePostActivity.this, "Check from and to date", Toast.LENGTH_SHORT).show();
                } else {
                    if (strPostType.equalsIgnoreCase("2")) {
                        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = myFormat.parse(etFromDate.getText().toString());
                            Date date2 = myFormat.parse(etToDate.getText().toString());
                            long diff = date2.getTime() - date1.getTime();
                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            int noOfDays = (int) days;
                            int diffDays = noOfDays + 1;
                            if (diffDays > 7) {
                                Toast.makeText(CreatePostActivity.this, "Only 7 days allowed for weekly post", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(CreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(CreatePostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                fetchLastLocation();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (strPostType.equalsIgnoreCase("3")) {
                            if (etFestivalName.getText().toString().trim().isEmpty()) {
                                Toast.makeText(CreatePostActivity.this, "Enter festival name", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(CreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(CreatePostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                fetchLastLocation();
                            }
                        } else if (strAccessOption.isEmpty()) {
                            Toast.makeText(CreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                        } else if (listOfOffer.size() == 0) {
                            Toast.makeText(CreatePostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CreatePostActivity.this, "Select post type", Toast.LENGTH_SHORT).show();
                } else if (strFromDate.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Select from date", Toast.LENGTH_SHORT).show();
                } else if (strToDate.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Select to date", Toast.LENGTH_SHORT).show();
                } else if (!checkDates()) {
                    Toast.makeText(CreatePostActivity.this, "Check from and to date", Toast.LENGTH_SHORT).show();
                } else {
                    if (strPostType.equalsIgnoreCase("2")) {
                        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = myFormat.parse(etFromDate.getText().toString());
                            Date date2 = myFormat.parse(etToDate.getText().toString());
                            long diff = date2.getTime() - date1.getTime();
                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            int noOfDays = (int) days;
                            int diffDays = noOfDays + 1;
                            if (diffDays > 7) {
                                Toast.makeText(CreatePostActivity.this, "Only 7 days allowed for weekly post", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(CreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(CreatePostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                postValidation();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (strPostType.equalsIgnoreCase("3")) {
                            if (etFestivalName.getText().toString().trim().isEmpty()) {
                                Toast.makeText(CreatePostActivity.this, "Enter festival name", Toast.LENGTH_SHORT).show();
                            } else if (strAccessOption.isEmpty()) {
                                Toast.makeText(CreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                            } else if (listOfOffer.size() == 0) {
                                Toast.makeText(CreatePostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                            } else {
                                postValidation();
                            }
                        } else if (strAccessOption.isEmpty()) {
                            Toast.makeText(CreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                        } else if (listOfOffer.size() == 0) {
                            Toast.makeText(CreatePostActivity.this, "Add offer", Toast.LENGTH_SHORT).show();
                        } else {
                            postValidation();
                        }
                    }
                }
            }
        });
    }

    private boolean checkDates() {
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
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
        Utilis.clearOfferList(CreatePostActivity.this);
        Utilis.setOfferList("offerList", listOfOffer, CreatePostActivity.this);
        Intent intent = new Intent(CreatePostActivity.this, ViewPostActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("fromDate", strFromDate);
        intent.putExtra("toDate", strToDate);
        intent.putExtra("accessOption", strAccessOption);
        intent.putExtra("latitude", String.valueOf(latitude));
        intent.putExtra("longitude", String.valueOf(longitude));
        intent.putExtra("postType", strPostType);
        intent.putExtra("festivalName", etFestivalName.getText().toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void postValidation() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(CreatePostActivity.this);

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

                            AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
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
                    Toast.makeText(CreatePostActivity.this, CreatePostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, CreatePostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPost() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(CreatePostActivity.this);

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
                            sendOffer(str_post_index_id);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(CreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(CreatePostActivity.this, CreatePostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, CreatePostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendOffer(final String str_post_index_id) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(CreatePostActivity.this);

            for (int i = 0; i < listOfOffer.size(); i++) {

                final int currentPos = i;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.createoffers, new Response.Listener<String>() {
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
                                    Utilis.dismissProgress();
                                    back();
                                }

                            } else if (Integer.parseInt(str_result) == 2) {
                                Utilis.dismissProgress();
                                str_message = obj.getString("message");
                                Toast.makeText(CreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utilis.dismissProgress();
                        Toast.makeText(CreatePostActivity.this, CreatePostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                        params.put("offerImage", listOfOffer.get(currentPos).getImage());
                        params.put("postIndexId", str_post_index_id);
                        System.out.println(Tag + " sendOffer inputs " + params);
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }

        } else {
            Toast.makeText(this, CreatePostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getApiCall() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(CreatePostActivity.this);

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

//                            JSONObject jsonObject = obj.getJSONObject("accessOption");
//                            Iterator<String> iterator1 = jsonObject.keys();
//                            List<String> keyArray1 = new ArrayList<>();
//                            while (iterator1.hasNext()) {
//                                String key = iterator1.next();
//                                keyArray1.add(key);
//                            }

//                            for (int k = 0; k < keyArray1.size(); k++) {
//                                PostInitialData postInitialData = new PostInitialData(
//                                        keyArray1.get(k),
//                                        jsonObject.optString(keyArray1.get(k))
//                                );
//
//                                accessOptionListValue.add(postInitialData);
//                            }

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

                            ArrayAdapter arrayAdapter = new ArrayAdapter(CreatePostActivity.this, R.layout.spinner_item, postTypeSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinPostType.setAdapter(arrayAdapter);
                            spinPostType.setSelection(0);

                            ArrayAdapter arrayAdapter1 = new ArrayAdapter(CreatePostActivity.this, R.layout.spinner_item, accessOptionSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinAccessOption.setAdapter(arrayAdapter1);
                            spinAccessOption.setSelection(0);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(CreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(CreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(CreatePostActivity.this, CreatePostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("type", RegBusinessTypeSharedPreference.getBusinessType(CreatePostActivity.this));
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, CreatePostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void editOfferDialog(View view, AddOfferData offerData, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_offer, viewGroup, false);
        final EditText etHeading = dialogView.findViewById(R.id.et_heading);
        final EditText etDesc = dialogView.findViewById(R.id.et_desc);
        final TextView tvTitle = dialogView.findViewById(R.id.tv_offer);
        imageView = dialogView.findViewById(R.id.iv_img);

//        final int size = offerData.getOffCount();
        final int size = position + 1;
        tvTitle.setText("Deal " + size);
        etHeading.setText(offerData.getHeading());
        etDesc.setText(offerData.getDesc());
        imageView.getLayoutParams().height = 500;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setImageBitmap(offerData.getBitmap());
        base64img = offerData.getImage();
        bitmap = offerData.getBitmap();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utilis.checkPermission(CreatePostActivity.this);
                if (result)
                    galleryIntent();
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
                    Toast.makeText(CreatePostActivity.this, "Enter heading", Toast.LENGTH_SHORT).show();
                } else if (headingCount > 8) {
                    Toast.makeText(CreatePostActivity.this, "Heading not exceeding 8 words", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 20) {
                    Toast.makeText(CreatePostActivity.this, "Description not exceeding 20 words", Toast.LENGTH_SHORT).show();
                } else if (base64img.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Select image", Toast.LENGTH_SHORT).show();
                } else {
                    listOfOffer.remove(position);
                    AddOfferData offerData = new AddOfferData(size, strHeading, strDesc, base64img, bitmap);
                    listOfOffer.add(position, offerData);
                    offerAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void addOfferDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this, R.style.CustomAlertDialog);
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
                boolean result = Utilis.checkPermission(CreatePostActivity.this);
                if (result)
                    galleryIntent();
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
                    Toast.makeText(CreatePostActivity.this, "Enter heading", Toast.LENGTH_SHORT).show();
                } else if (headingCount > 8) {
                    Toast.makeText(CreatePostActivity.this, "Heading not exceeding 8 words", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 20) {
                    Toast.makeText(CreatePostActivity.this, "Description not exceeding 20 words", Toast.LENGTH_SHORT).show();
                } else if (base64img.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Select image", Toast.LENGTH_SHORT).show();
                } else {
                    AddOfferData offerData = new AddOfferData(listOfOffer.size() + 1, strHeading, strDesc, base64img, bitmap);
                    listOfOffer.add(offerData);
                    offerAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void galleryIntent() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

        Intent GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), SELECT_FILE);

    }

    private void selectFromDate() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(CreatePostActivity.this, new DatePickerDialog.OnDateSetListener() {

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

                String date = year + "-" + strMonth + "-" + strDay;

                etFromDate.setText(date);
                strFromDate = date;

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

        DatePickerDialog dpd = new DatePickerDialog(CreatePostActivity.this, new DatePickerDialog.OnDateSetListener() {

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

                String date = year + "-" + strMonth + "-" + strDay;

                etToDate.setText(date);
                strToDate = date;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Utilis.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(CreatePostActivity.this, "Grant Permission to update profile image", Toast.LENGTH_SHORT).show();
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
//                onSelectFromGalleryResult(data);
                Uri uri = data.getData();
                ImageCropFunction(uri);
            } else if (requestCode == REQUEST_CODE) {
                if (Utilis.isGpsOn()) {
                    fetchLastLocation();
                }
            } else if (requestCode == CROP_IMG) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bm = null;
                    bm = bundle.getParcelable("data");
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
            }
        }
    }

    private void ImageCropFunction(Uri uri) {
        try {
            Intent CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 400);
            CropIntent.putExtra("outputY", 600); //180
            CropIntent.putExtra("aspectX", 16); //3
            CropIntent.putExtra("aspectY", 9); //4
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, CROP_IMG);
        } catch (ActivityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmap = bm;
        imageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
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
        Intent intent = new Intent(CreatePostActivity.this, ManageBusinessActivity.class);
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
            currentLocation = new LocationTrack(CreatePostActivity.this);

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
}