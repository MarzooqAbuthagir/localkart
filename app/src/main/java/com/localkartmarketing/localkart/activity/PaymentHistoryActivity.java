package com.localkartmarketing.localkart.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.localkartmarketing.localkart.adapter.PaymentHistoryAdapter;
import com.localkartmarketing.localkart.adapter.SubcriptionAdapter;
import com.localkartmarketing.localkart.model.AccessOptions;
import com.localkartmarketing.localkart.model.PaymentHistoryData;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentHistoryActivity extends AppCompatActivity {
    private String Tag = "PaymentHistoryActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    String str_result = "", str_message = "";

    private ArrayList<PaymentHistoryData> historyData = new ArrayList<PaymentHistoryData>();

    RecyclerView recyclerView;
    LinearLayout layHeader;
    TextView tvNoRecords;
    HorizontalScrollView horizontalScrollView;

    String dailyCount = "";
    String weeklyCount = "";
    String festivalCount = "";
    String dealsCount = "", packageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        utils = new Utils(PaymentHistoryActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(PaymentHistoryActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Subscription History");

        horizontalScrollView = findViewById(R.id.hscrll1);
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PaymentHistoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        tvNoRecords = findViewById(R.id.tv_no_records);
        layHeader = findViewById(R.id.lay_header);
        getHistory();
    }

    private void getHistory() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(PaymentHistoryActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.paymenthistory, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getHistory response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getHistory result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            historyData.clear();

                            horizontalScrollView.setVisibility(View.VISIBLE);
                            layHeader.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            tvNoRecords.setVisibility(View.GONE);

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                PaymentHistoryData history = new PaymentHistoryData(
                                        jsonObject.getString("package"),
                                        jsonObject.getString("paymentDate"),
                                        jsonObject.getString("validity"),
                                        jsonObject.getString("indexId"),
                                        jsonObject.getString("planType"),
                                        jsonObject.getString("amount"),
                                        jsonObject.getString("Status"));

                                historyData.add(history);
                            }

                            PaymentHistoryAdapter adapter = new PaymentHistoryAdapter(PaymentHistoryActivity.this, historyData);
                            recyclerView.setAdapter(adapter);

                            Drawable mDivider = ContextCompat.getDrawable(PaymentHistoryActivity.this, R.drawable.divider_line);
                            DividerItemDecoration itemDecoration = new DividerItemDecoration(PaymentHistoryActivity.this, DividerItemDecoration.VERTICAL);
                            itemDecoration.setDrawable(mDivider);
                            recyclerView.addItemDecoration(itemDecoration);

                            adapter.setOnItemClickListener(new PaymentHistoryAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    getPayDetails(view, historyData.get(position).getIndexId(), historyData.get(position).getPlanType());
                                }
                            });

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(PaymentHistoryActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            layHeader.setVisibility(View.GONE);
                            horizontalScrollView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            tvNoRecords.setVisibility(View.VISIBLE);
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PaymentHistoryActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(PaymentHistoryActivity.this, PaymentHistoryActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    System.out.println(Tag + " getHistory inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PaymentHistoryActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }


    private ArrayList<AccessOptions> accessOptionListValue = new ArrayList<AccessOptions>();

    private void getPayDetails(final View view, final String indexId, final String planType) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(PaymentHistoryActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.paymenthistorydetails, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getPayDetails response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getPayDetails result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            accessOptionListValue.clear();

                            JSONObject json = obj.getJSONObject("result");

                            dailyCount = json.getString("dailyTotalCount");
                            weeklyCount = json.getString("weeklyTotalCount");
                            festivalCount = json.getString("festivalTotalCount");
                            dealsCount = json.getString("dealsTotalCount");
                            packageName = json.getString("packageName");

                            JSONArray jsonArray = json.getJSONArray("othersList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                AccessOptions accessOptions = new AccessOptions(
                                        jsonObject.getString("keyName"),
                                        jsonObject.getString("value")
                                );
                                accessOptionListValue.add(accessOptions);
                            }

                            showAlertDialog(view);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(PaymentHistoryActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PaymentHistoryActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(PaymentHistoryActivity.this, PaymentHistoryActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    params.put("planType", planType);
                    params.put("indexId", indexId);
                    System.out.println(Tag + " getPayDetails inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PaymentHistoryActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PaymentHistoryActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_payment_history_details, viewGroup, false);

        final TextView tvPackageName = dialogView.findViewById(R.id.tv_package);
        tvPackageName.setText(packageName);

        final TextView tvDailyCount = dialogView.findViewById(R.id.tv_dl_count);
        final TextView tvWeeklyCount = dialogView.findViewById(R.id.tv_wl_count);
        final TextView tvFestivalCount = dialogView.findViewById(R.id.tv_fl_count);
        final TextView tvDealsCount = dialogView.findViewById(R.id.tv_deals_count);

        final GridView gridView = dialogView.findViewById(R.id.grid_view);

        SubcriptionAdapter adapter = new SubcriptionAdapter(PaymentHistoryActivity.this, accessOptionListValue);
        gridView.setAdapter(adapter);

        tvDailyCount.setText(dailyCount);
        tvWeeklyCount.setText(weeklyCount);
        tvFestivalCount.setText(festivalCount);
        tvDealsCount.setText(dealsCount);

        final Button btnOk = dialogView.findViewById(R.id.btn_ok);

        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(PaymentHistoryActivity.this, SubscriptionActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}