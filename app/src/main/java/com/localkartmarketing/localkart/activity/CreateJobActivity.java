package com.localkartmarketing.localkart.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.AddOfferAdapter;
import com.localkartmarketing.localkart.model.AddOfferData;
import com.localkartmarketing.localkart.model.PostInitialData;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.LocationTrack;
import com.localkartmarketing.localkart.support.RegBusinessIdSharedPreference;
import com.localkartmarketing.localkart.support.RegBusinessTypeSharedPreference;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateJobActivity extends AppCompatActivity {
    private String Tag = "CreateJobActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    EditText etFromDate, etToDate;
    LinearLayout fromDateLayout, toDateLayout;

    AddOfferAdapter offerAdapter;
    RecyclerView recyclerView;
    List<AddOfferData> listOfOffer = new ArrayList<>();

    private SearchableSpinner spinAccessOption;
    String str_result = "", str_message = "", strOfferCount = "", strJobImage = "";
    private ArrayList<PostInitialData> accessOptionListValue = new ArrayList<PostInitialData>();
    private List<String> accessOptionSpinnerValue = new ArrayList<>();
    private String strAccessOption = "", strFromDate = "", strToDate = "";

    UserDetail obj;
    static SharedPreferences mPrefs;

    LocationTrack currentLocation;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        utils = new Utils(CreateJobActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(CreateJobActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Job Post");

        spinAccessOption = findViewById(R.id.spin_access_option);
        spinAccessOption.setTitle("Access Option");

        getApiCall();

        Button btnJob = findViewById(R.id.btn_add_job);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreateJobActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        btnJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listOfOffer.size() < Integer.parseInt(strOfferCount)) {
                    addJobDialog(view);
                } else {
                    Toast.makeText(CreateJobActivity.this, strOfferCount + " posts are allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        offerAdapter = new AddOfferAdapter(this, listOfOffer, 2, strJobImage);
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
                editJobDialog(view, offerData, position);
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

                if (strFromDate.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Select From Date", Toast.LENGTH_SHORT).show();
                } else if (strToDate.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Select To Date", Toast.LENGTH_SHORT).show();
                } else if (!checkDates()) {
                    Toast.makeText(CreateJobActivity.this, "Check From and To Date", Toast.LENGTH_SHORT).show();
                } else if (strAccessOption.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                } else if (listOfOffer.size() == 0) {
                    Toast.makeText(CreateJobActivity.this, "Add Job Opening", Toast.LENGTH_SHORT).show();
                } else {
                    fetchLastLocation();
                }
            }
        });

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strFromDate.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Select From Date", Toast.LENGTH_SHORT).show();
                } else if (strToDate.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Select To Date", Toast.LENGTH_SHORT).show();
                } else if (!checkDates()) {
                    Toast.makeText(CreateJobActivity.this, "Check From and To Date", Toast.LENGTH_SHORT).show();
                } else if (strAccessOption.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                } else if (listOfOffer.size() == 0) {
                    Toast.makeText(CreateJobActivity.this, "Add Job Opening", Toast.LENGTH_SHORT).show();
                } else {
                    postValidation();
                }
            }
        });
    }

    private void editJobDialog(View view, AddOfferData offerData, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateJobActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_job, viewGroup, false);
        final EditText etHeading = dialogView.findViewById(R.id.et_heading);
        final EditText etDesc = dialogView.findViewById(R.id.et_desc);
        final TextView tvTitle = dialogView.findViewById(R.id.tv_offer);
        final ImageView imageView = dialogView.findViewById(R.id.iv_img);

//        final int size = offerData.getOffCount();
        final int size = position + 1;
        tvTitle.setText("Job Opening " + size);
        etHeading.setText(offerData.getHeading());
        etDesc.setText(offerData.getDesc());
        Glide.with(CreateJobActivity.this).load(strJobImage)
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

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
                    Toast.makeText(CreateJobActivity.this, "Enter job title / designation", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Enter job description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 50) {
                    Toast.makeText(CreateJobActivity.this, "Job description not exceeding 50 words", Toast.LENGTH_SHORT).show();
                } else {
                    listOfOffer.remove(position);
                    AddOfferData offerData = new AddOfferData(size, strHeading, strDesc, "", null);
                    listOfOffer.add(position, offerData);
                    offerAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void addJobDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateJobActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_job, viewGroup, false);
        final EditText etHeading = dialogView.findViewById(R.id.et_heading);
        final EditText etDesc = dialogView.findViewById(R.id.et_desc);
        final TextView tvTitle = dialogView.findViewById(R.id.tv_offer);
        final ImageView imageView = dialogView.findViewById(R.id.iv_img);

        Glide.with(CreateJobActivity.this).load(strJobImage)
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

        if (listOfOffer.isEmpty()) {
            tvTitle.setText("Job Opening 1");
        } else {
            int size = listOfOffer.size() + 1;
            tvTitle.setText("Job Opening " + size);
        }

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
                    Toast.makeText(CreateJobActivity.this, "Enter job title / designation", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(CreateJobActivity.this, "Enter job description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 50) {
                    Toast.makeText(CreateJobActivity.this, "Job description not exceeding 50 words", Toast.LENGTH_SHORT).show();
                } else {
                    AddOfferData offerData = new AddOfferData(listOfOffer.size() + 1, strHeading, strDesc, strJobImage, null);
                    listOfOffer.add(offerData);
                    offerAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
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

    private void getApiCall() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(CreateJobActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.listarray, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("Message");

                            accessOptionListValue.clear();

                            strOfferCount = obj.getString("jobCount");
                            strJobImage = obj.getString("jobImage");

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

                            PostInitialData initPostData1 = new PostInitialData();
                            initPostData1.setKey("-1");
                            initPostData1.setValue("Access Option");
                            accessOptionListValue.add(0, initPostData1);

                            accessOptionSpinnerValue.clear();
                            for (int i = 0; i < accessOptionListValue.size(); i++) {
                                accessOptionSpinnerValue.add(accessOptionListValue.get(i).getValue());
                            }


                            ArrayAdapter arrayAdapter = new ArrayAdapter(CreateJobActivity.this, R.layout.spinner_item, accessOptionSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinAccessOption.setAdapter(arrayAdapter);
                            spinAccessOption.setSelection(0);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(CreateJobActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(CreateJobActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(CreateJobActivity.this, CreateJobActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("type", RegBusinessTypeSharedPreference.getBusinessType(CreateJobActivity.this));
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, CreateJobActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFromDate() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(CreateJobActivity.this, new DatePickerDialog.OnDateSetListener() {

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

        DatePickerDialog dpd = new DatePickerDialog(CreateJobActivity.this, new DatePickerDialog.OnDateSetListener() {

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(CreateJobActivity.this, ManageBusinessActivity.class);
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

        if (Utils.isGpsOn()) {
            currentLocation = new LocationTrack(CreateJobActivity.this);

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

    private void previewPost() {
        Utils.clearOfferList(CreateJobActivity.this);
        Utils.setOfferList("offerList", listOfOffer, CreateJobActivity.this);
        Intent intent = new Intent(CreateJobActivity.this, ViewJobActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("fromDate", etFromDate.getText().toString().trim());
        intent.putExtra("toDate", etToDate.getText().toString().trim());
        intent.putExtra("fDate", strFromDate);
        intent.putExtra("tDate", strToDate);
        intent.putExtra("accessOption", strAccessOption);
        intent.putExtra("latitude", String.valueOf(latitude));
        intent.putExtra("longitude", String.valueOf(longitude));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void postValidation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateJobActivity.this);

        builder.setMessage("Job post details cannot be changed once saved. Are you sure you want to save and show this job opening to customer?")
                .setPositiveButton(CreateJobActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        if (Utils.isInternetOn()) {
                            Utils.showProgress(CreateJobActivity.this);

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.jobpostvalidation, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        //converting response to json object
                                        JSONObject obj = new JSONObject(response);

                                        System.out.println(Tag + " postValidation response - " + response);

                                        Utils.dismissProgress();

                                        str_result = obj.getString("errorCode");
                                        System.out.print(Tag + " postValidation result " + str_result);

                                        if (Integer.parseInt(str_result) == 0) {

                                            str_message = obj.getString("Message");

                                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateJobActivity.this);
                                            builder.setMessage(str_message)
                                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                            Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                                            btnOk.setTextColor(Color.parseColor("#000000"));
                                        } else if (Integer.parseInt(str_result) == 1) {
                                            str_message = obj.getString("Message");
                                            sendPost();
                                        } else if (Integer.parseInt(str_result) == 2) {
                                            str_message = obj.getString("Message");
                                            Toast.makeText(CreateJobActivity.this, str_message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Utils.dismissProgress();
                                    Toast.makeText(CreateJobActivity.this, CreateJobActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                                    params.put("fromDate", strFromDate);
                                    params.put("toDate", strToDate);

                                    System.out.println(Tag + " postValidation inputs " + params);
                                    return params;
                                }
                            };

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            VolleySingleton.getInstance(CreateJobActivity.this).addToRequestQueue(stringRequest);
                        } else {
                            Toast.makeText(CreateJobActivity.this, CreateJobActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(CreateJobActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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
        if (Utils.isInternetOn()) {
            Utils.showProgress(CreateJobActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.createjobpost, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " sendPost response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " sendPost result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("Message");
                            String str_post_index_id = obj.getString("postIndexId");
                            sendOffer(str_post_index_id);


                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("Message");
                            Toast.makeText(CreateJobActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(CreateJobActivity.this, CreateJobActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("fromDate", strFromDate);
                    params.put("toDate", strToDate);
                    params.put("accessOptions", strAccessOption);
                    System.out.println(Tag + " sendPost inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, CreateJobActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendOffer(final String str_post_index_id) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(CreateJobActivity.this);

            for (int i = 0; i < listOfOffer.size(); i++) {

                final int currentPos = i;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.createjoboffers, new Response.Listener<String>() {
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

                                    Utils.dismissProgress();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateJobActivity.this);
                                    builder.setMessage("Your job post has been successfully saved.")
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    back();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                                    btnOk.setTextColor(Color.parseColor("#000000"));
                                }

                            } else if (Integer.parseInt(str_result) == 2) {
                                Utils.dismissProgress();
                                str_message = obj.getString("message");
                                Toast.makeText(CreateJobActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utils.dismissProgress();
                        Toast.makeText(CreateJobActivity.this, CreateJobActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                        params.put("heading", listOfOffer.get(currentPos).getHeading());
                        params.put("description", listOfOffer.get(currentPos).getDesc());
                        params.put("postIndexId", str_post_index_id);
                        System.out.println(Tag + " sendOffer inputs " + params);
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }

        } else {
            Toast.makeText(this, CreateJobActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission Granted");
                if (Utils.isGpsOn()) {
                    fetchLastLocation();
                }
            }
        }
    }
}