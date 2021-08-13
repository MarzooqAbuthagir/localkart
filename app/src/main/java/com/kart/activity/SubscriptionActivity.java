package com.kart.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kart.adapter.CategoryAdapter;
import com.kart.adapter.SubcriptionAdapter;
import com.kart.model.AccessOptions;
import com.kart.model.PostInitialData;
import com.kart.model.UserDetail;
import com.kart.support.MyGridView;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionActivity extends AppCompatActivity {
    private String Tag = "SubscriptionActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    String keyIntent = "";
    LinearLayout layNoPackage;
    ScrollView layPackage;

    TextView tvPlanName, tvDL, tvDLTotDays, tvDLAvaDays, tvDLUseDays, tvDLExpDays;
    TextView tvWP, tvWPTotPost, tvWPAvaPost, tvWPUsePost, tvWPExpPost;
    TextView tvDP, tvDPTotPost, tvDPAvaPost, tvDPUsePost, tvDPExpPost;
    TextView tvFP, tvFPTotPost, tvFPAvaPost, tvFPUsePost, tvFPExpPost;

    private MyGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        utilis = new Utilis(SubscriptionActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(SubscriptionActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Subscription");

        Button btnPlans = findViewById(R.id.btn_plans);
        btnPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubscriptionActivity.this, PlanActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        layPackage = findViewById(R.id.lay_package);
        layNoPackage = findViewById(R.id.lay_no_package);

        tvPlanName = findViewById(R.id.tv_plan_name);

        tvDL = findViewById(R.id.tv_dl);
        tvDLTotDays = findViewById(R.id.tv_dl_tot_days);
        tvDLAvaDays = findViewById(R.id.tv_dl_ava_days);
        tvDLUseDays = findViewById(R.id.tv_dl_use_days);
        tvDLExpDays = findViewById(R.id.tv_dl_exp_days);

        tvWP = findViewById(R.id.tv_wl);
        tvWPTotPost = findViewById(R.id.tv_wp_tot_post);
        tvWPAvaPost = findViewById(R.id.tv_wp_ava_post);
        tvWPUsePost = findViewById(R.id.tv_wp_use_post);
        tvWPExpPost = findViewById(R.id.tv_wp_exp_post);

        tvDP = findViewById(R.id.tv_dp);
        tvDPTotPost = findViewById(R.id.tv_dp_tot_post);
        tvDPAvaPost = findViewById(R.id.tv_dp_ava_post);
        tvDPUsePost = findViewById(R.id.tv_dp_use_post);
        tvDPExpPost = findViewById(R.id.tv_dp_exp_post);

        tvFP = findViewById(R.id.tv_fp);
        tvFPTotPost = findViewById(R.id.tv_fp_tot_post);
        tvFPAvaPost = findViewById(R.id.tv_fp_ava_post);
        tvFPUsePost = findViewById(R.id.tv_fp_use_post);
        tvFPExpPost = findViewById(R.id.tv_fp_exp_post);

        gridView = findViewById(R.id.grid_view);

        getApiCall();
    }

    String str_result = "", str_message = "";
    private ArrayList<AccessOptions> accessOptionListValue = new ArrayList<AccessOptions>();
    private void getApiCall() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(SubscriptionActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.viewplandetails, new Response.Listener<String>() {
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
                            str_message = obj.getString("message");

                            layPackage.setVisibility(View.VISIBLE);
                            layNoPackage.setVisibility(View.GONE);

                            accessOptionListValue.clear();

                            JSONObject json = obj.getJSONObject("result");

                            tvPlanName.setText(json.getString("planName"));

                            JSONObject js = json.getJSONObject("directoryListing");
                            tvDL.setText(js.getString("benifits"));
                            tvDLTotDays.setText(js.getString("Days Total"));
                            tvDLAvaDays.setText(js.getString("Days Available"));
                            tvDLUseDays.setText(js.getString("Days Used"));
//                            tvDLExpDays.setText(js.getString("Days Expired"));

                            JSONObject js1 = json.getJSONObject("weeklyPost");
                            tvWP.setText(js1.getString("benifits"));
                            tvWPTotPost.setText(js1.getString("Posts Total"));
                            tvWPAvaPost.setText(js1.getString("Posts Available"));
                            tvWPUsePost.setText(js1.getString("Posts Used"));
                            tvWPExpPost.setText(js1.getString("Posts Expired"));

                            JSONObject js2 = json.getJSONObject("festivalPost");
                            tvFP.setText(js2.getString("benifits"));
                            tvFPTotPost.setText(js2.getString("Posts Total"));
                            tvFPAvaPost.setText(js2.getString("Posts Available"));
                            tvFPUsePost.setText(js2.getString("Posts Used"));
                            tvFPExpPost.setText(js2.getString("Posts Expired"));

                            JSONObject js3 = json.getJSONObject("dailyPost");
                            tvDP.setText(js3.getString("benifits"));
                            tvDPTotPost.setText(js3.getString("Posts Total"));
                            tvDPAvaPost.setText(js3.getString("Posts Available"));
                            tvDPUsePost.setText(js3.getString("Posts Used"));
                            tvDPExpPost.setText(js3.getString("Posts Expired"));

                            JSONArray jsonArray = json.getJSONArray("others");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                AccessOptions accessOptions = new AccessOptions(
                                        jsonObject.getString("keyName"),
                                        jsonObject.getString("value")
                                );
                                accessOptionListValue.add(accessOptions);
                            }

                            SubcriptionAdapter adapter = new SubcriptionAdapter(SubscriptionActivity.this, accessOptionListValue);
                            gridView.setAdapter(adapter);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(SubscriptionActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            layPackage.setVisibility(View.GONE);
                            layNoPackage.setVisibility(View.VISIBLE);
                            str_message = obj.getString("message");
                            Toast.makeText(SubscriptionActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(SubscriptionActivity.this, SubscriptionActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, SubscriptionActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(SubscriptionActivity.this, ManageBusinessActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}