package com.kart.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import com.kart.R;
import com.kart.adapter.OfferAdapter;
import com.kart.adapter.ViewPagerShopBannerAdapter;
import com.kart.model.AccessOptions;
import com.kart.model.AddOfferData;
import com.kart.model.ShopBanner;
import com.kart.model.UserDetail;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealsMoreDetailsActivity extends AppCompatActivity {
    private String Tag = "DealsMoreDetailsActivity";

    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strPostIndexId = "", strShopIndexId = "", strType = "", strLatitude = "", strLongitude = "", strIsSubscribed = "", strConstPostType = "";
    String str_result = "", str_message = "";
    String strShopLati = "", strShopLongi = "";

    LinearLayout layMain;
    TextView tvShopName, tvDate, tvDirection, tvAccessKey, tvAccessValue, tvReportPost;

    private static ViewPager mPager;
    LinearLayout sliderDotspanel;
    private static int currentPage = 0;
    //    Timer swipeTimer;
//    final long DELAY_MS = 1000; //delay in milliseconds before task is to be executed
//    final long PERIOD_MS = 4500; // time in milliseconds between successive task executions.
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    List<ShopBanner> shopBannerList = new ArrayList<>();
    ViewPagerShopBannerAdapter viewPagerAdapter;

    RecyclerView recyclerView;
    List<AddOfferData> offerDataList = new ArrayList<>();
    OfferAdapter offerAdapter;

    LinearLayout layNotify, layShare;
    ImageView imgNotify;
    Button btnSubscribe;

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    String strShopName = "", strFromDate ="", strToDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_more_details);

        utilis = new Utilis(DealsMoreDetailsActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strPostIndexId = intent.getStringExtra("postIndexId");
        strShopIndexId = intent.getStringExtra("shopIndexId");
        strType = intent.getStringExtra("type");
        strLatitude = intent.getStringExtra("latitude");
        strLongitude = intent.getStringExtra("longitude");
        strShopLati = intent.getStringExtra("shopLatitude");
        strShopLongi = intent.getStringExtra("shopLongitude");
        strIsSubscribed = intent.getStringExtra("isSubscribed");
        strConstPostType = intent.getStringExtra("constPostType");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(DealsMoreDetailsActivity.this, R.color.colorPrimaryDark));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        layMain = findViewById(R.id.lay_main);
        tvShopName = findViewById(R.id.tv_shop_name);
        tvDate = findViewById(R.id.tv_date);
        tvDirection = findViewById(R.id.tv_direction);
        tvAccessKey = findViewById(R.id.tv_access_key);
        tvAccessValue = findViewById(R.id.tv_acccess_value);
        tvReportPost = findViewById(R.id.tv_report_post);

        mPager = findViewById(R.id.view_pager);
        sliderDotspanel = findViewById(R.id.slider_dots);

        getApiCall();

        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("More Details");

        layNotify = findViewById(R.id.lay_subscribe);
        btnSubscribe = findViewById(R.id.btn_subscribe);
        imgNotify = findViewById(R.id.img_subscribe);

        if (Integer.parseInt(strIsSubscribed) == 0) {
            btnSubscribe.setText("Subscribe");
            imgNotify.setBackgroundResource(R.drawable.ic_outline_notifications_active_24);
        } else {
            btnSubscribe.setText("UnSubscribe");
            imgNotify.setBackgroundResource(R.drawable.ic_bell_subscribe);
        }

        layNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilis.isInternetOn()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DealsMoreDetailsActivity.this);
                    String state = Integer.parseInt(strIsSubscribed) == 0 ? "Subscribe" : "UnSubscribe";
                    builder.setTitle(state)
                            .setMessage(Html.fromHtml("You'll receive notifications when <b>" + strShopName + "</b> posts new Deals and Offers. Are you sure want to " + state + "?"))
                            .setPositiveButton(DealsMoreDetailsActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing but close the dialog
                                    dialog.dismiss();
                                    if (Integer.parseInt(strIsSubscribed) == 0) {
                                        subscribeShop();
                                    } else {
                                        unsubscribeShop();
                                    }
                                }
                            })
                            .setNegativeButton(DealsMoreDetailsActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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

                } else {
                    Toast.makeText(DealsMoreDetailsActivity.this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        layShare = findViewById(R.id.lay_share);
        layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String shareMessage = "";
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    if (offerDataList.size() == 1)
                        shareMessage = "Valid From " + strFromDate + " To " + strToDate + "\n\n" + strShopName + " \n\n" + offerDataList.get(0).getHeading() + " \n\n" + offerDataList.get(0).getDesc() + " \n\nDownload Local Kart App Now ";
                    else {
                        int count = offerDataList.size() - 1;
                        shareMessage = "Valid From " + strFromDate + " To " + strToDate + "\n\n" + strShopName + " \n\n" + offerDataList.get(0).getHeading() + " \n\n" + offerDataList.get(0).getDesc() + "\n\nand " + count + " more deals" + " \n\nDownload Local Kart App Now ";
                    }
                    shareMessage = shareMessage + Utilis.shareUrl;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvReportPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DealsMoreDetailsActivity.this, ReportActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("title", tvReportPost.getText().toString().trim());
                intent.putExtra("shopIndexId", strShopIndexId);
                intent.putExtra("postIndexId", strPostIndexId);
                intent.putExtra("type", strType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void unsubscribeShop() {
        Utilis.showProgress(DealsMoreDetailsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.unsubscribe, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " unsubscribeShop response - " + response);

                    Utilis.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " unsubscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");

                        strIsSubscribed = "0";
                        btnSubscribe.setText("Subscribe");
                        imgNotify.setBackgroundResource(R.drawable.ic_outline_notifications_active_24);

                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DealsMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DealsMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utilis.dismissProgress();
                Toast.makeText(DealsMoreDetailsActivity.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", strType);
                params.put("shopId", strShopIndexId);
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " unsubscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(DealsMoreDetailsActivity.this).addToRequestQueue(stringRequest);
    }

    private void subscribeShop() {
        Utilis.showProgress(DealsMoreDetailsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.savesubscribers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " subscribeShop response - " + response);

                    Utilis.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " subscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");

                        strIsSubscribed = "1";
                        btnSubscribe.setText("UnSubscribe");
                        imgNotify.setBackgroundResource(R.drawable.ic_bell_subscribe);
                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DealsMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DealsMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilis.dismissProgress();
                Toast.makeText(DealsMoreDetailsActivity.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", strType);
                params.put("shopId", strShopIndexId);
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " subscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(DealsMoreDetailsActivity.this).addToRequestQueue(stringRequest);
    }

    private void getApiCall() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(DealsMoreDetailsActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.viewpostdetails, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utilis.dismissProgress();

                        layMain.setVisibility(View.VISIBLE);

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");
                            strShopName = json.getString("shopName");
                            tvShopName.setText(json.getString("shopName"));
                            tvDate.setText(json.getString("fromDate") + " To " + json.getString("toDate"));
                            tvDirection.setText(json.getString("distance") + " • Map");
                            strFromDate = json.getString("fromDate");
                            strToDate = json.getString("toDate");

                            JSONObject js = json.getJSONObject("accessOptions");
                            AccessOptions accessOptions = new AccessOptions(
                                    js.getString("key"),
                                    js.getString("value")
                            );

                            tvAccessKey.setText(accessOptions.getKey());
                            tvAccessValue.setText(accessOptions.getValue());

                            shopBannerList.clear();

                            JSONArray jsonArray = json.getJSONArray("shopImageList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                ShopBanner shopBanner = new ShopBanner(
                                        object.getString("imageUrl")
                                );
                                shopBannerList.add(shopBanner);
                            }

                            offerDataList.clear();
                            JSONArray jsonArray1 = json.getJSONArray("shopOfferList");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                AddOfferData offerData = new AddOfferData(
                                        object.getString("heading"),
                                        object.getString("description"),
                                        object.getString("offerImage")
                                );
                                offerDataList.add(offerData);
                            }
                            setViews();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(DealsMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(DealsMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(DealsMoreDetailsActivity.this, DealsMoreDetailsActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("postIndexId", strPostIndexId);
                    params.put("shopIndexId", strShopIndexId);
                    params.put("shopType", strType);
                    params.put("latitude", strLatitude);
                    params.put("longitude", strLongitude);
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, DealsMoreDetailsActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViews() {

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DealsMoreDetailsActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        offerAdapter = new OfferAdapter(this, offerDataList, 0, true, keyIntent, strPostIndexId, strShopIndexId, strType);
        recyclerView.setAdapter(offerAdapter);

        TextView tvOfferTitle = findViewById(R.id.tv_offer_title);
        if (offerDataList.size() > 1) {
            tvOfferTitle.setText("Deals");
        } else {
            tvOfferTitle.setText("Deal");
        }

        setShopBanners(shopBannerList);

        RelativeLayout layDirection = findViewById(R.id.lay_direction);
        layDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + strShopLati + "," + strShopLongi;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });

        RelativeLayout layAccessOption = findViewById(R.id.lay_access_option);
        layAccessOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accessKey = tvAccessKey.getText().toString().trim();
                String accessValue = tvAccessValue.getText().toString().trim();

                switch (accessKey) {
                    case "Website":
                    case "Facebook":
                    case "Digital VCard":
                        Uri uri = Uri.parse("googlechrome://navigate?url=" + accessValue);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            startActivity(intent);
                        }
                        break;

                    case "WhatsApp":
                        String url = "https://api.whatsapp.com/send?phone=+91" + accessValue;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;

                    case "Phone":
                    case "Mobile":
                    case "Alternate Number":
                    case "COD":
                        String telPhone = "tel:" + accessValue;
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(telPhone));
                        startActivity(callIntent);
                        break;

                    case "Email":
                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                                    Uri.fromParts("mailto", accessValue, null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                            startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
                        } catch (android.content.ActivityNotFoundException e) {
                            System.out.println("There is no email client installed.");
                        }
                        break;
                }

            }
        });
    }

    private void setShopBanners(final List<ShopBanner> shopBannerList) {
        viewPagerAdapter = new ViewPagerShopBannerAdapter(shopBannerList, DealsMoreDetailsActivity.this);
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

            dots[j] = new ImageView(DealsMoreDetailsActivity.this);
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
        Utilis.callResume = 1;
        Utilis.constPostType = strConstPostType;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}