package com.localkartmarketing.localkart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReferralActivity extends AppCompatActivity {
    private String Tag = "ReferralActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    RelativeLayout relLayNotBusinessUser;
    RelativeLayout relLayNotSubscriptionUser;
    RelativeLayout relLayBusinessUser;

    Button btnRegisterNow;
    Button btnBuyPlan;

    TextView tvReferralCode;
    TextView tvDiscountAmount;
    TextView tvDailyPost;
    TextView tvWeeklyPost;
    TextView tvFestivalPost;
    TextView tvDeals;
    TextView tvValidity;

    Button btnShare;

    UserDetail obj;
    static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        utils = new Utils(ReferralActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(ReferralActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Refer");

        relLayNotBusinessUser = findViewById(R.id.lay_not_business_user);
        relLayNotSubscriptionUser = findViewById(R.id.lay_not_subscription_user);
        relLayBusinessUser = findViewById(R.id.lay_business_user);

        btnRegisterNow = findViewById(R.id.btn_register_now);
        btnBuyPlan = findViewById(R.id.btn_buy_plan);

        tvReferralCode = findViewById(R.id.tv_referral_code);
        tvDiscountAmount = findViewById(R.id.tv_discount_amount);
        tvDailyPost = findViewById(R.id.tv_daily_post);
        tvWeeklyPost = findViewById(R.id.tv_weekly_post);
        tvFestivalPost = findViewById(R.id.tv_festival_post);
        tvDeals = findViewById(R.id.tv_deals);
        tvValidity = findViewById(R.id.tv_validity);

        btnShare = findViewById(R.id.btn_share);

        btnRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.clearRegPref(ReferralActivity.this);
                Intent intent = new Intent(ReferralActivity.this, AdvertiseBusinessActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnBuyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReferralActivity.this, PlanActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "Take Your Business Promotion to Next Level. \n\nShare Digital vCard, Exciting Deals and Offers through Digital Platform and Reach More Customers. \n\nUse Code " + strReferralCode + " and Get Rs." + strAmount + " Discount.\n\nDownload LocalKart App Now ";
                    shareMessage = shareMessage + Utils.shareUrl;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getApiCall();
    }

    String str_result = "", str_message = "";
    String strReferralCode = "", strAmount = "", strDailyPost = "", strWeeklyPost = "", strFestivalPost = "", strDealsPost = "", strValidity = "";

    private void getApiCall() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(ReferralActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.viewreferral, new Response.Listener<String>() {
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

                            relLayBusinessUser.setVisibility(View.VISIBLE);
                            relLayNotBusinessUser.setVisibility(View.GONE);
                            relLayNotSubscriptionUser.setVisibility(View.GONE);

                            JSONObject json = obj.getJSONObject("result");
                            strReferralCode = json.getString("referralCode");
                            strAmount = json.getString("amount");
                            strDailyPost = json.getString("dailyPost");
                            strWeeklyPost = json.getString("weeklyPost");
                            strFestivalPost = json.getString("festivalPost");
                            strDealsPost = json.getString("dealsPost");
                            strValidity = json.getString("validity");

                            tvReferralCode.setText(strReferralCode);
                            String userBenefit = "\n<b>User Benefits</b>\n" + getResources().getString(R.string.user_benefit_1) + " Rs." + strAmount + " " + getResources().getString(R.string.user_benefit_2);
                            userBenefit = userBenefit.replace("\n", "<br>");
                            tvDiscountAmount.setText(Html.fromHtml(userBenefit));
                            tvDailyPost.setText(strDailyPost);
                            tvWeeklyPost.setText(strWeeklyPost);
                            tvFestivalPost.setText(strFestivalPost);
                            tvDeals.setText(strDealsPost);
                            tvValidity.setText(strValidity);

                        } else if (Integer.parseInt(str_result) == 2) {

                            relLayBusinessUser.setVisibility(View.GONE);
                            relLayNotBusinessUser.setVisibility(View.VISIBLE);
                            relLayNotSubscriptionUser.setVisibility(View.GONE);

                        } else if (Integer.parseInt(str_result) == 3 || Integer.parseInt(str_result) == 4) {

                            relLayBusinessUser.setVisibility(View.GONE);
                            relLayNotBusinessUser.setVisibility(View.GONE);
                            relLayNotSubscriptionUser.setVisibility(View.VISIBLE);

                        } else if (Integer.parseInt(str_result) == 1) {

                            str_message = obj.getString("message");
                            Toast.makeText(ReferralActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ReferralActivity.this, ReferralActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, ReferralActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(ReferralActivity.this, MainActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}