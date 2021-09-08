package com.kart.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.kart.R;
import com.kart.adapter.OfferAdapter;
import com.kart.adapter.ViewPagerShopBannerAdapter;
import com.kart.model.AccessOptions;
import com.kart.model.AddOfferData;
import com.kart.model.ShopBanner;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationPostActivity extends AppCompatActivity {
    private String Tag = "NotificationPostActivity";

    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strPostIndexId = "", strShopIndexId = "", strType = "";
    String str_result = "", str_message = "";

    LinearLayout layMain;
    TextView tvShopName, tvDate, tvAccessKey, tvAccessValue;

    private static ViewPager mPager;
    LinearLayout sliderDotspanel;
    private static int currentPage = 0;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    List<ShopBanner> shopBannerList = new ArrayList<>();
    ViewPagerShopBannerAdapter viewPagerAdapter;

    RecyclerView recyclerView;
    List<AddOfferData> offerDataList = new ArrayList<>();
    OfferAdapter offerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_post);

        utilis = new Utilis(NotificationPostActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strPostIndexId = intent.getStringExtra("postIndexId");
        strShopIndexId = intent.getStringExtra("shopIndexId");
        strType = intent.getStringExtra("shopType");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(NotificationPostActivity.this, R.color.colorPrimaryDark));

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
        tvAccessKey = findViewById(R.id.tv_access_key);
        tvAccessValue = findViewById(R.id.tv_acccess_value);

        mPager = findViewById(R.id.view_pager);
        sliderDotspanel = findViewById(R.id.slider_dots);

        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Notification Details");

        getApiCall();
    }


    private void getApiCall() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(NotificationPostActivity.this);

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

                            tvShopName.setText(json.getString("shopName"));
                            tvDate.setText(json.getString("fromDate") + " To " + json.getString("toDate"));

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
                            Toast.makeText(NotificationPostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(NotificationPostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(NotificationPostActivity.this, NotificationPostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, NotificationPostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViews() {

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NotificationPostActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        offerAdapter = new OfferAdapter(this, offerDataList, 0, false, keyIntent, strPostIndexId, strShopIndexId, strType);
        recyclerView.setAdapter(offerAdapter);

        TextView tvOfferTitle = findViewById(R.id.tv_offer_title);
        if (offerDataList.size() > 1) {
            tvOfferTitle.setText("Deals");
        } else {
            tvOfferTitle.setText("Deal");
        }

        setShopBanners(shopBannerList);

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
        viewPagerAdapter = new ViewPagerShopBannerAdapter(shopBannerList, NotificationPostActivity.this);
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

            dots[j] = new ImageView(NotificationPostActivity.this);
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
        Intent intent = new Intent(NotificationPostActivity.this, NotificationActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}