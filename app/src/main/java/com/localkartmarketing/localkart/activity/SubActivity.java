package com.localkartmarketing.localkart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.localkartmarketing.localkart.adapter.SubCategoryAdapter;
import com.localkartmarketing.localkart.adapter.ViewPagerAdapter;
import com.localkartmarketing.localkart.model.SilderData;
import com.localkartmarketing.localkart.model.SubCategoryData;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubActivity extends AppCompatActivity {
    private String Tag = "SubActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

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
    String str_result = "", str_message = "";

    private ArrayList<SubCategoryData> subCategoryListValue = new ArrayList<SubCategoryData>();
    private GridView gridView;

    String keyIntent = "";
    String categoryId = "";
    String categoryName = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        utils = new Utils(SubActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        categoryId = intent.getStringExtra("categoryId");
        categoryName = intent.getStringExtra("categoryName");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(SubActivity.this, R.color.my_statusbar_color));

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
        toolBarTitle.setText(keyIntent);

        mPager = findViewById(R.id.view_pager);

        sliderDotspanel = findViewById(R.id.slider_dots);

        silders = new ArrayList<>();

        getBannerImages();

        Button btnSubCategory = findViewById(R.id.btn_sub_category);
        btnSubCategory.setText(categoryName);
        gridView = findViewById(R.id.grid_category);

        String apiName = "";
        if (keyIntent.equalsIgnoreCase("Shopping")) {
            apiName = Utils.shopsubcat;
        } else {
            apiName = Utils.servicesubcat;
        }
        getSubCategoryData(apiName);
    }

    private void back() {
        Intent intent = new Intent(SubActivity.this, MainActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getSubCategoryData(String apiName) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(SubActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + apiName, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getSubCategoryData response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getSubCategoryData result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            subCategoryListValue.clear();

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                SubCategoryData subCategoryData = new SubCategoryData(
                                        jsonObject.getString("subCategoryName"),
                                        jsonObject.getString("Image"),
                                        jsonObject.getString("Id"));

                                subCategoryListValue.add(subCategoryData);
                            }

                            SubCategoryAdapter adapter = new SubCategoryAdapter(SubActivity.this, subCategoryListValue, keyIntent, categoryId, categoryName);
                            gridView.setAdapter(adapter);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(SubActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(SubActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(SubActivity.this, SubActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("Id", categoryId);
                    System.out.println(Tag + " getSubCategoryData inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, SubActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getBannerImages() {
        if (Utils.isInternetOn()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.categoryslider, new Response.Listener<String>() {
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
                            setViewPager(json.length());

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(SubActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(SubActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    Toast.makeText(SubActivity.this, SubActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("categoryId", categoryId);
                    params.put("type", keyIntent);
                    System.out.println(Tag + " getBannerImages inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, SubActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViewPager(final int length) {
        viewPagerAdapter = new ViewPagerAdapter(silders, SubActivity.this);
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

            dots[j] = new ImageView(SubActivity.this);
            dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[j], params);

        }
        dots[dotPosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}