package com.localkartmarketing.localkart.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.localkartmarketing.localkart.adapter.OfferAdapter;
import com.localkartmarketing.localkart.adapter.ViewPagerShopBannerAdapter;
import com.localkartmarketing.localkart.model.AccessOptions;
import com.localkartmarketing.localkart.model.AddOfferData;
import com.localkartmarketing.localkart.model.ShopBanner;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.model.ViewOfferData;
import com.localkartmarketing.localkart.support.App;
import com.localkartmarketing.localkart.support.RegBusinessTypeSharedPreference;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MegaSalesViewPostActivity extends AppCompatActivity {
    private String Tag = "MegaSalesViewPostActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strFromDate = "", strToDate = "", strAccessOption = "", strLati = "", strLongi = "", strOfferId = "", strOfferTitle = "", strDealCount = "", strFDate="", strTDate="";

    RecyclerView recyclerView;
    List<AddOfferData> offerDataList;
    OfferAdapter offerAdapter;

    TextView tvDate, tvShopName, tvDirection, tvAccessKey, tvAccessValue;
    private static ViewPager mPager;
    LinearLayout sliderDotspanel;
    private static int currentPage = 0;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    UserDetail obj;
    static SharedPreferences mPrefs;

    String str_result = "", str_message = "";
    List<ShopBanner> shopBannerList = new ArrayList<>();
    ViewPagerShopBannerAdapter viewPagerAdapter;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mega_sales_view_post);


        utils = new Utils(MegaSalesViewPostActivity.this);

        app = (App) getApplication();

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strFromDate = intent.getStringExtra("fromDate");
        strToDate = intent.getStringExtra("toDate");
        strFDate = intent.getStringExtra("fDate");
        strTDate = intent.getStringExtra("tDate");
        strAccessOption = intent.getStringExtra("accessOption");
        strLati = intent.getStringExtra("latitude");
        strLongi = intent.getStringExtra("longitude");
        strOfferId = intent.getStringExtra("megaSalesIndexId");
        strOfferTitle = intent.getStringExtra("megaSalesTitle");
        offerDataList = Utils.getOfferList("offerList");
        strDealCount = intent.getStringExtra("dealCount");


        if (offerDataList != null) {
            System.out.println("array list contains images");
        } else {
            offerDataList = new ArrayList<>();
        }

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MegaSalesViewPostActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Post Details");

        tvDate = findViewById(R.id.tv_date);
        tvDate.setText(strFDate + " To " + strTDate);

        tvShopName = findViewById(R.id.tv_shop_name);
        tvDirection = findViewById(R.id.tv_direction);
        tvAccessKey = findViewById(R.id.tv_access_key);
        tvAccessValue = findViewById(R.id.tv_acccess_value);

        mPager = findViewById(R.id.view_pager);

        sliderDotspanel = findViewById(R.id.slider_dots);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MegaSalesViewPostActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        offerAdapter = new OfferAdapter(this, offerDataList, 1, false, keyIntent, "", "", "", "");
        recyclerView.setAdapter(offerAdapter);

        TextView tvOfferTitle = findViewById(R.id.tv_offer_title);
        if (offerDataList.size() > 1) {
            tvOfferTitle.setText("Deals");
        } else {
            tvOfferTitle.setText("Deal");
        }

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnPost = findViewById(R.id.btn_post);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPost();
            }
        });

        getPostDetails();
    }

    private void sendPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MegaSalesViewPostActivity.this);

        builder.setMessage("Post details cannot be changed once saved. Are you sure you want to save and show this post to customer?")
                .setPositiveButton(MegaSalesViewPostActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        if (Utils.isInternetOn()) {
                            Utils.showProgress(MegaSalesViewPostActivity.this);

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.createmegasalespost, new Response.Listener<String>() {
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

                                            str_message = obj.getString("message");
                                            String str_post_index_id = obj.getString("postIndexId");
                                            sendOffer(str_post_index_id);

                                        } else if (Integer.parseInt(str_result) == 2) {
                                            str_message = obj.getString("message");
                                            Toast.makeText(MegaSalesViewPostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Utils.dismissProgress();
                                    Toast.makeText(MegaSalesViewPostActivity.this, MegaSalesViewPostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                                    params.put("megasalesIndexId", strOfferId);
                                    params.put("fromDate", strFromDate);
                                    params.put("toDate", strToDate);
                                    params.put("accessOptions", strAccessOption);
                                    params.put("megasalesTitle", strOfferTitle);
                                    System.out.println(Tag + " sendPost inputs " + params);
                                    return params;
                                }
                            };

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            VolleySingleton.getInstance(MegaSalesViewPostActivity.this).addToRequestQueue(stringRequest);
                        } else {
                            Toast.makeText(MegaSalesViewPostActivity.this, MegaSalesViewPostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(MegaSalesViewPostActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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

    private void sendOffer(final String str_post_index_id) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(MegaSalesViewPostActivity.this);

            for (int i = 0; i < offerDataList.size(); i++) {

                final int currentPos = i;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.createmegasalesoffers, new Response.Listener<String>() {
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
                                if (currentPos + 1 == offerDataList.size()) {

                                    Utils.dismissProgress();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MegaSalesViewPostActivity.this);
                                    builder.setMessage("Your post has been successfully saved.")
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(MegaSalesViewPostActivity.this, ManageBusinessActivity.class);
                                                    intent.putExtra("key", keyIntent);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
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
                                Toast.makeText(MegaSalesViewPostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utils.dismissProgress();
                        Toast.makeText(MegaSalesViewPostActivity.this, MegaSalesViewPostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                        params.put("heading", offerDataList.get(currentPos).getHeading());
                        params.put("description", offerDataList.get(currentPos).getDesc());
                        params.put("offerImage", offerDataList.get(currentPos).getImage());
                        params.put("postIndexId", str_post_index_id);
                        System.out.println(Tag + " sendOffer inputs " + params);
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }

        } else {
            Toast.makeText(this, MegaSalesViewPostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getPostDetails() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(MegaSalesViewPostActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.viewoffer, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getPostDetails response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getPostDetails result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");

                            shopBannerList.clear();

                            JSONObject json = obj.getJSONObject("result");
                            ViewOfferData viewOfferData = new ViewOfferData();
                            viewOfferData.setName(json.getString("name"));
                            viewOfferData.setLogo(json.getString("logo"));
                            viewOfferData.setDistance(json.getString("distance"));

                            JSONObject jsonObject = json.getJSONObject("accessOptions");
                            AccessOptions accessOptions = new AccessOptions(
                                    jsonObject.getString("key"),
                                    jsonObject.getString("value")
                            );
                            viewOfferData.setAccessOptions(accessOptions);

                            JSONArray jsonArray = json.getJSONArray("multipleImages");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                ShopBanner shopBanner = new ShopBanner(
                                        object.getString("imageUrl")
                                );
                                shopBannerList.add(shopBanner);
                            }
                            viewOfferData.setMultiImages(shopBannerList);

                            setViews(viewOfferData);

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(MegaSalesViewPostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MegaSalesViewPostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(MegaSalesViewPostActivity.this, MegaSalesViewPostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("type", RegBusinessTypeSharedPreference.getBusinessType(MegaSalesViewPostActivity.this));
                    params.put("latitude", strLati);
                    params.put("longitude", strLongi);
                    params.put("accessOptions", strAccessOption);
                    System.out.println(Tag + " getPostDetails inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, MegaSalesViewPostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViews(ViewOfferData viewOfferData) {
        tvShopName.setText(viewOfferData.getName());
        tvDirection.setText(viewOfferData.getDistance() + " • Map");
        tvAccessKey.setText(viewOfferData.getAccessOptions().getKey());
        tvAccessValue.setText(viewOfferData.getAccessOptions().getValue());

        setShopBanners(shopBannerList);
    }

    private void setShopBanners(final List<ShopBanner> shopBannerList) {
        viewPagerAdapter = new ViewPagerShopBannerAdapter(shopBannerList, MegaSalesViewPostActivity.this);
        mPager.setAdapter(viewPagerAdapter);

//        // Auto start of viewpager
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//
//                if (currentPage == shopBannerList.size()) {
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
                if (currentPage == shopBannerList.size()) {
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

            dots[j] = new ImageView(MegaSalesViewPostActivity.this);
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

    private void back() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}