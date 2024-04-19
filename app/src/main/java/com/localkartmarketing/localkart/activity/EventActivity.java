package com.localkartmarketing.localkart.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.localkartmarketing.localkart.adapter.BookingSliderAdapter;
import com.localkartmarketing.localkart.adapter.EventAdapter;
import com.localkartmarketing.localkart.adapter.ViewPagerAdapter;
import com.localkartmarketing.localkart.model.DistrictData;
import com.localkartmarketing.localkart.model.EventListData;
import com.localkartmarketing.localkart.model.MyBooking;
import com.localkartmarketing.localkart.model.SilderData;
import com.localkartmarketing.localkart.model.StateData;
import com.localkartmarketing.localkart.model.UserDetail;
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

public class EventActivity extends AppCompatActivity {
    private String Tag = "EventActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    LinearLayout layEvents, layShopping, layServices, layRecharge;
    ImageView imgEvents, imgShopping, imgServices, imgRecharge;
    TextView tvEvents, tvShopping, tvServices, tvRecharge;

    LinearLayout layLocation, layMyBooking;
    ImageView ivLocation, imgMyBooking;
    Button btnLocation, btnMyBooking;

    private SearchableSpinner spinState;
    private ArrayList<StateData> stateListValue = new ArrayList<StateData>();
    private List<String> stateSpinnerValue = new ArrayList<>();

    private SearchableSpinner spinDistrict;
    private ArrayList<DistrictData> districtListValue = new ArrayList<DistrictData>();
    private List<String> districtSpinnerValue = new ArrayList<>();

    private String strStateId = "";
    private String strDistrictId = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    String str_result = "", str_message = "";
    String stateId = "", districtId = "";

    RecyclerView recyclerView;
    private ArrayList<EventListData> eventListData = new ArrayList<EventListData>();

    private ViewPager viewPager;
    private ViewGroup dotsLayout;
    BookingSliderAdapter adapter;

    private ArrayList<MyBooking> bookingData = new ArrayList<MyBooking>();

    private static ViewPager mPager;
    LinearLayout sliderDotspanel;
    private ArrayList<SilderData> silders;
    private static int currentPage = 0;
    //    Timer swipeTimer;
//    final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
//    final long PERIOD_MS = 4500; // time in milliseconds between successive task executions.
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    ViewPagerAdapter viewPagerAdapter;
    RelativeLayout layBooking;
    NestedScrollView nestedScrollView;
    TextView tvNoRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        utils = new Utils(EventActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(EventActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Events");

        layEvents = findViewById(R.id.lay_events);
        layShopping = findViewById(R.id.lay_shopping);
        layServices = findViewById(R.id.lay_services);
        layRecharge = findViewById(R.id.lay_recharge);
        tvEvents = findViewById(R.id.text_view_event);
        tvShopping = findViewById(R.id.text_view_shopping);
        tvServices = findViewById(R.id.text_view_services);
        tvRecharge = findViewById(R.id.text_view_recharge);
        imgEvents = findViewById(R.id.img_view_event);
        imgShopping = findViewById(R.id.img_view_shopping);
        imgServices = findViewById(R.id.img_view_services);
        imgRecharge = findViewById(R.id.img_view_recharge);
        layBooking = findViewById(R.id.lay_booking);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        tvNoRecords = findViewById(R.id.tv_no_records);

        layEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyIntent = "Events";
                eventsClicked();
            }
        });
        layShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyIntent = "Shopping";
                Intent intent = new Intent(EventActivity.this, MainActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        layServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyIntent = "Services";
                Intent intent = new Intent(EventActivity.this, MainActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        layRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyIntent = "Recharge";
                Intent intent = new Intent(EventActivity.this, MainActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        if (keyIntent.equalsIgnoreCase("Events")) {
            eventsClicked();
        }

        layLocation = findViewById(R.id.lay_location);
        btnLocation = findViewById(R.id.btn_location);
        ivLocation = findViewById(R.id.img_location);

        layLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDialog(view);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDialog(view);
            }
        });

        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDialog(view);
            }
        });

        layMyBooking = findViewById(R.id.lay_my_booking);
        imgMyBooking = findViewById(R.id.img_my_booking);
        btnMyBooking = findViewById(R.id.btn_my_booking);
        layMyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directToMyBooking();
            }
        });
        imgMyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directToMyBooking();
            }
        });
        btnMyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directToMyBooking();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        mPager = findViewById(R.id.view_pager);

        sliderDotspanel = findViewById(R.id.slider_dots);

        silders = new ArrayList<>();

        getBannerImages();
        getBookingList();

    }


    private void locationDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this, R.style.CustomAlertDialog);
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

                    ArrayAdapter arrayAdapter = new ArrayAdapter(EventActivity.this, R.layout.spinner_item, districtSpinnerValue);
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
                    Toast.makeText(EventActivity.this, "Select state", Toast.LENGTH_SHORT).show();
                } else if (strDistrictId.isEmpty()) {
                    Toast.makeText(EventActivity.this, "Select district / zone", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isInternetOn()) {
                        Utils.saveStateFilter(strStateId);
                        Utils.saveDistrictFilter(strDistrictId);
                        alertDialog.dismiss();
                        getEventList();
                    } else {
                        Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        alertDialog.show();
    }

    private void getDistrictList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(EventActivity.this);

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

                            String prefDistrictId = Utils.getDistrictFilter(EventActivity.this).isEmpty() ? userDetail.getDistrictId() : Utils.getDistrictFilter(EventActivity.this);
                            int prefPos = 0;
                            for (int i = 0; i < districtListValue.size(); i++) {
                                if (districtListValue.get(i).getDistrictId().equalsIgnoreCase(prefDistrictId)) {
                                    prefPos = i;
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(EventActivity.this, R.layout.spinner_item, districtSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinDistrict.setAdapter(arrayAdapter);
                            spinDistrict.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(EventActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getStateList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(EventActivity.this);

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

                            String prefStateId = Utils.getStateFilter(EventActivity.this).isEmpty() ? userDetail.getStateId() : Utils.getStateFilter(EventActivity.this);
                            int prefPos = 0;
                            for (int i = 0; i < stateListValue.size(); i++) {
                                if (stateListValue.get(i).getStateId().equalsIgnoreCase(prefStateId)) {
                                    prefPos = i;
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(EventActivity.this, R.layout.spinner_item, stateSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinState.setAdapter(arrayAdapter);
                            spinState.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(EventActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getBannerImages() {
        if (Utils.isInternetOn()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.homeslider, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getBannerImages response - " + response);

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getBannerImages result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                SilderData silderData = new SilderData(
                                        jsonObject.getString("Image"),
                                        jsonObject.getString("actionType"),
                                        jsonObject.getString("dataLink"));

                                silders.add(silderData);
                            }
                            if (json.length() > 0) {
                                setViewPager(json.length());
                            }

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
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
                    params.put("stateId", userDetail.getStateId());
                    params.put("districtId", userDetail.getDistrictId());
                    System.out.println(Tag + " getBannerImages inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, EventActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }


    private void setViewPager(final int length) {
        viewPagerAdapter = new ViewPagerAdapter(silders, EventActivity.this, "Main");
        mPager.setAdapter(viewPagerAdapter);

//        // Auto start of viewpager
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//
//                if (currentPage == length) {
//                    currentPage = 0;
//                }
//                mPager.setCurrentItem(currentPage++, true);
//            }
//        };
//        handler.postDelayed(Update, 1000);
//        swipeTimer = new Timer();
//        swipeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, DELAY_MS, PERIOD_MS);

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                if (currentPage == length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        }, delay);

        addDot(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addDot(int dotPosition) {
        int dotscount = viewPagerAdapter.getCount();
        ImageView[] dots = new ImageView[dotscount];

        sliderDotspanel.removeAllViews();

        for (int j = 0; j < dotscount; j++) {

            dots[j] = new ImageView(EventActivity.this);
            dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[j], params);

        }
        dots[dotPosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }


    private void getBookingList() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(EventActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.mybookings + "?userid=" + userDetail.getId() + "&filter=future", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getBookingList response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getBookingList result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            bookingData.clear();

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);

                                MyBooking myBooking = new MyBooking(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("eventname"),
                                        jsonObject.getString("date"),
                                        jsonObject.getString("time"),
                                        jsonObject.getString("event_id"),
                                        jsonObject.getString("district"),
                                        jsonObject.getString("tickets"),
                                        jsonObject.getString("pri_image"));

                                bookingData.add(myBooking);
                            }

                            layBooking.setVisibility(View.VISIBLE);

                            adapter = new BookingSliderAdapter(EventActivity.this, bookingData);
                            viewPager.setAdapter(adapter);

                            addDotsIndicator(0);

//                            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//                                @Override
//                                public void onPageSelected(int position) {
//                                    super.onPageSelected(position);
//                                    updateDots(position);
//                                }
//                            });

                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {
                                    addDotsIndicator(position);
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
//                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            layBooking.setVisibility(View.GONE);
                        }

                        getEventList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, EventActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void addDotsIndicator(int dotPosition) {
        int dotscount = adapter.getCount();
        ImageView[] dots = new ImageView[dotscount];

        dotsLayout.removeAllViews();

        for (int j = 0; j < dotscount; j++) {

            dots[j] = new ImageView(EventActivity.this);
            dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            dotsLayout.addView(dots[j], params);

        }
        dots[dotPosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }

    private void updateDots(int position) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            int drawableId = (i == position) ? R.drawable.indicator_active : R.drawable.indicator_inactive;
            dotsLayout.getChildAt(i).setBackground(ContextCompat.getDrawable(getApplicationContext(), drawableId));
        }
    }

    private void getEventList() {
        stateId = Utils.getStateFilter(EventActivity.this).isEmpty() ? userDetail.getStateId() : Utils.getStateFilter(EventActivity.this);
        districtId = Utils.getDistrictFilter(EventActivity.this).isEmpty() ? userDetail.getDistrictId() : Utils.getDistrictFilter(EventActivity.this);
        if (Utils.isInternetOn()) {
            Utils.showProgress(EventActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.customereventlist + "?state=" + stateId + "&city=" + districtId + "&filter=future", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getEventList response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getCategoryData result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            eventListData.clear();

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                System.out.println(Tag + jsonObject.getString("id"));
                                System.out.println(Tag + jsonObject.getString("eventname"));

                                EventListData eventData = new EventListData(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("eventname"),
                                        jsonObject.getString("date"),
                                        jsonObject.getString("time1"),
                                        jsonObject.getString("time2"),
                                        jsonObject.getString("address"),
                                        jsonObject.getString("district"),
                                        jsonObject.getString("image"));

                                eventListData.add(eventData);
                            }

                            nestedScrollView.setVisibility(View.VISIBLE);
                            tvNoRecords.setVisibility(View.GONE);
                            System.out.println("Event List size " + eventListData.size());
                            EventAdapter adapter = new EventAdapter(EventActivity.this, eventListData);
                            recyclerView.setAdapter(adapter);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
//                            Toast.makeText(EventActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            nestedScrollView.setVisibility(View.GONE);
                            tvNoRecords.setVisibility(View.VISIBLE);
                            tvNoRecords.setText(str_message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(EventActivity.this, EventActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, EventActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void directToMyBooking() {
        Intent intent = new Intent(EventActivity.this, MyBookingActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    void eventsClicked() {
        layServices.setBackgroundResource(R.drawable.white_box);
        layShopping.setBackgroundResource(R.drawable.white_box);
        layRecharge.setBackgroundResource(R.drawable.white_box);

        layEvents.setBackgroundResource(R.drawable.pink_box);

        tvServices.setTextColor(Color.parseColor("#E4287C"));
        tvShopping.setTextColor(Color.parseColor("#E4287C"));
        tvRecharge.setTextColor(Color.parseColor("#E4287C"));

        tvEvents.setTextColor(Color.parseColor("#ffffff"));

        imgServices.setBackgroundResource(R.drawable.ic_services);
        imgShopping.setBackgroundResource(R.drawable.ic_shopping);
        imgRecharge.setBackgroundResource(R.drawable.ic_recharge);

        imgEvents.setBackgroundResource(R.drawable.ic_events_white);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(EventActivity.this, MainActivity.class);
        intent.putExtra("key", "Shopping");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}