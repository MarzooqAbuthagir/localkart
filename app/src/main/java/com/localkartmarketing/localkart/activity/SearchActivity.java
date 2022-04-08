package com.localkartmarketing.localkart.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.DirectoryAdapter;
import com.localkartmarketing.localkart.model.AccessOptions;
import com.localkartmarketing.localkart.model.DirectoryData;
import com.localkartmarketing.localkart.model.DistrictData;
import com.localkartmarketing.localkart.model.StateData;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.LocationTrack;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private final String Tag = "SearchActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    EditText etSearch;
    ImageView ivSearch, ivClose;
    String strSearchText = "";

    LocationTrack currentLocation;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CODE = 101;

    RecyclerView recyclerView;
    TextView tvNoRecords;

    String str_result = "", str_message = "";

    List<DirectoryData> directoryDataList = new ArrayList<>();

    LinearLayout layNearMe, layLocation;
    Button btnNearMe, btnLocation;
    ImageView ivNearMe, ivLocation;

    private SearchableSpinner spinState;
    private final ArrayList<StateData> stateListValue = new ArrayList<StateData>();
    private final List<String> stateSpinnerValue = new ArrayList<>();

    private SearchableSpinner spinDistrict;
    private final ArrayList<DistrictData> districtListValue = new ArrayList<DistrictData>();
    private final List<String> districtSpinnerValue = new ArrayList<>();

    private String strStateId = "";
    private String strDistrictId = "";
    private int strProgress = 0;

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        utils = new Utils(SearchActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(SearchActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Search");

        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.iv_search);
        ivClose = findViewById(R.id.iv_close);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        tvNoRecords = findViewById(R.id.tv_no_records);

        layLocation = findViewById(R.id.lay_location);
        btnLocation = findViewById(R.id.btn_location);
        ivLocation = findViewById(R.id.img_location);
        layNearMe = findViewById(R.id.lay_near_me);
        btnNearMe = findViewById(R.id.btn_near_me);
        ivNearMe = findViewById(R.id.img_near_me);

        layNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSearchText = etSearch.getText().toString().trim();
                if (strSearchText.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    nearMeDialog(view);
                }
            }
        });

        btnNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSearchText = etSearch.getText().toString().trim();
                if (strSearchText.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    nearMeDialog(view);
                }
            }
        });

        ivNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSearchText = etSearch.getText().toString().trim();
                if (strSearchText.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    nearMeDialog(view);
                }
            }
        });

        layLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSearchText = etSearch.getText().toString().trim();
                if (strSearchText.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    locationDialog(view);
                }
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSearchText = etSearch.getText().toString().trim();
                if (strSearchText.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    locationDialog(view);
                }
            }
        });

        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSearchText = etSearch.getText().toString().trim();
                if (strSearchText.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    locationDialog(view);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = editable.toString();
                if (code.length() > 0) {
                    ivClose.setVisibility(View.VISIBLE);
                } else {
                    ivClose.setVisibility(View.GONE);
                }
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSearchText = etSearch.getText().toString().trim();
                if (strSearchText.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    fetchLastLocation();
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
                ivClose.setVisibility(View.GONE);
                directoryDataList.clear();
                recyclerView.setVisibility(View.GONE);
                tvNoRecords.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        if (Utils.isGpsOn()) {
            currentLocation = new LocationTrack(SearchActivity.this);

            if (currentLocation.canGetLocation()) {
                longitude = currentLocation.getLongitude();
                latitude = currentLocation.getLatitude();
                System.out.println("latitude " + latitude + " and longitude " + longitude);

                if (Utils.isInternetOn()) {
                    getListData();
                } else {
                    Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(SearchActivity.this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    private void nearMeDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_near_me, viewGroup, false);

        TextView tvHeading = dialogView.findViewById(R.id.tv_heading);
        tvHeading.setText("Show " + strSearchText + " within radius of Kilometer");

        BubbleSeekBar bubbleSeekBar = dialogView.findViewById(R.id.seek_bar);
        strProgress = Utils.getNearMeFilter(SearchActivity.this);
        bubbleSeekBar.setProgress(strProgress);
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                strProgress = progress;
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
            }
        });

        final Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetOn()) {
                    Utils.saveNearMeFilter(strProgress);
                    alertDialog.dismiss();
                    getNearMeData();
                } else {
                    Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    private void getNearMeData() {
        Utils.showProgress(SearchActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.searchnearme, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " getNearMeData response - " + response);

                    Utils.dismissProgress();

                    str_result = obj.getString("errorCode");
                    System.out.print(Tag + " getNearMeData result " + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        directoryDataList.clear();

                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoRecords.setVisibility(View.GONE);

                        JSONArray json = obj.getJSONArray("resut_array");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonObject = json.getJSONObject(i);
                            DirectoryData directoryData = new DirectoryData();

                            directoryData.setShopId(jsonObject.getString("shopIndexId"));
                            directoryData.setShopType(jsonObject.getString("type"));
                            directoryData.setName(jsonObject.getString("name"));
                            directoryData.setLogo(jsonObject.getString("logo"));
                            directoryData.setDescription(jsonObject.getString("description"));
                            directoryData.setDistance(jsonObject.getString("distance"));
                            directoryData.setLatitude(jsonObject.getString("latitude"));
                            directoryData.setLongitude(jsonObject.getString("longitude"));
                            directoryData.setIsSubscribed(jsonObject.getString("isSubscribed"));
                            directoryData.setShareUrl(jsonObject.getString("shareUrl"));

                            JSONObject js = jsonObject.getJSONObject("accessOptions");
                            AccessOptions accessOptions = new AccessOptions(
                                    js.getString("key"),
                                    js.getString("value")
                            );
                            directoryData.setAccessOptions(accessOptions);
                            directoryDataList.add(directoryData);
                        }

                        DirectoryAdapter adapter = new DirectoryAdapter(SearchActivity.this, directoryDataList, keyIntent, latitude, longitude);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new DirectoryAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {

                                if (Utils.isInternetOn()) {
                                    String state = Integer.parseInt(directoryDataList.get(position).getIsSubscribed()) == 0 ? "Subscribe" : "UnSubscribe";
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SearchActivity.this);
                                    builder.setTitle(state)
                                            .setMessage(Html.fromHtml("You'll receive notifications when <b>" + directoryDataList.get(position).getName() + "</b> posts new Deals and Offers. Are you sure want to " + state + "?"))
                                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing but close the dialog
                                                    dialog.dismiss();

                                                    if (Integer.parseInt(directoryDataList.get(position).getIsSubscribed()) == 0) {
                                                        subscribeShop(position);
                                                    } else {
                                                        unsubscribeShop(position);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing
                                                    dialog.dismiss();
                                                }
                                            });

                                    androidx.appcompat.app.AlertDialog alert = builder.create();
                                    alert.show();

                                    Button btn_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button btn_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);

                                    btn_no.setTextColor(Color.parseColor("#000000"));
                                    btn_yes.setTextColor(Color.parseColor("#000000"));

                                } else {
                                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("message");
                        recyclerView.setVisibility(View.GONE);
                        tvNoRecords.setVisibility(View.VISIBLE);
//                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("message");
//                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        recyclerView.setVisibility(View.GONE);
                        tvNoRecords.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.dismissProgress();
                Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("searchText", strSearchText);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("radius", String.valueOf(strProgress));
                System.out.println(Tag + " getNearMeData inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(stringRequest);
    }

    private void locationDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_location, viewGroup, false);

        spinState = dialogView.findViewById(R.id.spin_state);
        spinDistrict = dialogView.findViewById(R.id.spin_district);

        spinState.setTitle("Select State");
        spinDistrict.setTitle("Select District / Zone");

        getStateList();

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

                    ArrayAdapter arrayAdapter = new ArrayAdapter(SearchActivity.this, R.layout.spinner_item, districtSpinnerValue);
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

        final Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strStateId.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Select state", Toast.LENGTH_SHORT).show();
                } else if (strDistrictId.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Select district / zone", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isInternetOn()) {
                        Utils.saveStateFilter(strStateId);
                        Utils.saveDistrictFilter(strDistrictId);
                        alertDialog.dismiss();
                        getListData();
                    } else {
                        Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        alertDialog.show();
    }

    private void getDistrictList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(SearchActivity.this);

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

                            String prefDistrictId = Utils.getDistrictFilter(SearchActivity.this).isEmpty() ? userDetail.getDistrictId() : Utils.getDistrictFilter(SearchActivity.this);
                            int prefPos = 0;
                            for (int i = 0; i < districtListValue.size(); i++) {
                                if (districtListValue.get(i).getDistrictId().equalsIgnoreCase(prefDistrictId)) {
                                    prefPos = i;
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(SearchActivity.this, R.layout.spinner_item, districtSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinDistrict.setAdapter(arrayAdapter);
                            spinDistrict.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getStateList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(SearchActivity.this);

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

                            String prefStateId = Utils.getStateFilter(SearchActivity.this).isEmpty() ? userDetail.getStateId() : Utils.getStateFilter(SearchActivity.this);
                            int prefPos = 0;
                            for (int i = 0; i < stateListValue.size(); i++) {
                                if (stateListValue.get(i).getStateId().equalsIgnoreCase(prefStateId)) {
                                    prefPos = i;
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(SearchActivity.this, R.layout.spinner_item, stateSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinState.setAdapter(arrayAdapter);
                            spinState.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getListData() {
        Utils.showProgress(SearchActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.search, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " getListData response - " + response);

                    Utils.dismissProgress();

                    str_result = obj.getString("errorCode");
                    System.out.print(Tag + " getListData result " + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        directoryDataList.clear();

                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoRecords.setVisibility(View.GONE);

                        JSONArray json = obj.getJSONArray("resut_array");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonObject = json.getJSONObject(i);
                            DirectoryData directoryData = new DirectoryData();

                            directoryData.setShopId(jsonObject.getString("shopIndexId"));
                            directoryData.setShopType(jsonObject.getString("type"));
                            directoryData.setName(jsonObject.getString("name"));
                            directoryData.setLogo(jsonObject.getString("logo"));
                            directoryData.setDescription(jsonObject.getString("description"));
                            directoryData.setDistance(jsonObject.getString("distance"));
                            directoryData.setLatitude(jsonObject.getString("latitude"));
                            directoryData.setLongitude(jsonObject.getString("longitude"));
                            directoryData.setIsSubscribed(jsonObject.getString("isSubscribed"));
                            directoryData.setShareUrl(jsonObject.getString("shareUrl"));

                            JSONObject js = jsonObject.getJSONObject("accessOptions");
                            AccessOptions accessOptions = new AccessOptions(
                                    js.getString("key"),
                                    js.getString("value")
                            );
                            directoryData.setAccessOptions(accessOptions);
                            directoryDataList.add(directoryData);
                        }

                        DirectoryAdapter adapter = new DirectoryAdapter(SearchActivity.this, directoryDataList, keyIntent, latitude, longitude);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new DirectoryAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {

                                if (Utils.isInternetOn()) {
                                    String state = Integer.parseInt(directoryDataList.get(position).getIsSubscribed()) == 0 ? "Subscribe" : "UnSubscribe";
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SearchActivity.this);
                                    builder.setTitle(state)
                                            .setMessage(Html.fromHtml("You'll receive notifications when <b>" + directoryDataList.get(position).getName() + "</b> posts new Deals and Offers. Are you sure want to " + state + "?"))
                                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing but close the dialog
                                                    dialog.dismiss();

                                                    if (Integer.parseInt(directoryDataList.get(position).getIsSubscribed()) == 0) {
                                                        subscribeShop(position);
                                                    } else {
                                                        unsubscribeShop(position);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing
                                                    dialog.dismiss();
                                                }
                                            });

                                    androidx.appcompat.app.AlertDialog alert = builder.create();
                                    alert.show();

                                    Button btn_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button btn_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);

                                    btn_no.setTextColor(Color.parseColor("#000000"));
                                    btn_yes.setTextColor(Color.parseColor("#000000"));

                                } else {
                                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("message");
                        directoryDataList.clear();
                        recyclerView.setVisibility(View.GONE);
                        tvNoRecords.setVisibility(View.VISIBLE);
//                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("message");
//                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        directoryDataList.clear();
                        recyclerView.setVisibility(View.GONE);
                        tvNoRecords.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.dismissProgress();
                Toast.makeText(SearchActivity.this, SearchActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("searchText", strSearchText);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("radius", "0");
                params.put("stateId", Utils.getStateFilter(SearchActivity.this).isEmpty() ? userDetail.getStateId() : Utils.getStateFilter(SearchActivity.this));
                params.put("districtId", Utils.getDistrictFilter(SearchActivity.this).isEmpty() ? userDetail.getDistrictId() : Utils.getDistrictFilter(SearchActivity.this));
                System.out.println(Tag + " getListData inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(stringRequest);
    }

    private void unsubscribeShop(final int position) {
        Utils.showProgress(SearchActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.unsubscribe, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " unsubscribeShop response - " + response);

                    Utils.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " unsubscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");
                        getListData();

                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.dismissProgress();
                Toast.makeText(SearchActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", directoryDataList.get(position).getShopType());
                params.put("shopId", directoryDataList.get(position).getShopId());
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " unsubscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(stringRequest);
    }

    private void subscribeShop(final int position) {
        Utils.showProgress(SearchActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.savesubscribers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " subscribeShop response - " + response);

                    Utils.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " subscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");

                        getListData();
                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(SearchActivity.this, str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismissProgress();
                Toast.makeText(SearchActivity.this, getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", directoryDataList.get(position).getShopType());
                params.put("shopId", directoryDataList.get(position).getShopId());
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " subscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(stringRequest);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Utils.isGpsOn()) {
                fetchLastLocation();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}