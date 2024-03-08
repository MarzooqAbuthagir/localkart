package com.localkartmarketing.localkart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.localkartmarketing.localkart.adapter.ScanTicketAdapter;
import com.localkartmarketing.localkart.model.Ticket;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.DividerItemDecorator;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScanResultActivity extends AppCompatActivity {

    private final String Tag = "ScanResultActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", eventName = "", eventId = "", bookingId = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    LinearLayout lay_main_failure, lay_main_sucess, lay_btm_sucess, lay_btm_failure;
    TextView tv_result, tv_name, tv_date, tv_time, tv_total_ticket;
    Button btn_back, btn_admit, btn_rescan;
    RecyclerView recycler_view;
    private ArrayList<Ticket> tickets = new ArrayList<Ticket>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        utils = new Utils(ScanResultActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        eventName = intent.getStringExtra("eventName");
        eventId = intent.getStringExtra("eventId");
        bookingId = intent.getStringExtra("bookingId");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ScanResultActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Scan");

        lay_main_failure = findViewById(R.id.lay_main_failure);
        lay_main_sucess = findViewById(R.id.lay_main_sucess);
        lay_btm_sucess = findViewById(R.id.lay_btm_sucess);
        lay_btm_failure = findViewById(R.id.lay_btm_failure);
        tv_result = findViewById(R.id.tv_result);
        tv_name = findViewById(R.id.tv_name);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        tv_total_ticket = findViewById(R.id.tv_total_ticket);
        btn_back = findViewById(R.id.btn_back);
        btn_admit = findViewById(R.id.btn_admit);
        btn_rescan = findViewById(R.id.btn_rescan);
        recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ScanResultActivity.this);
        recycler_view.setHasFixedSize(true);
        recycler_view.setNestedScrollingEnabled(false);
        recycler_view.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(ScanResultActivity.this, R.drawable.new_divider_line));
        recycler_view.addItemDecoration(dividerItemDecoration);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        btn_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        getScanResult();
    }

    String str_result = "", str_message = "";

    private void getScanResult() {

        if (Utils.isInternetOn()) {
            Utils.showProgress(ScanResultActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.checkeventbooking + "?userId=6&eventId=" + eventId + "&bookingId="+bookingId , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getScanResult response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getScanResult result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            lay_main_sucess.setVisibility(View.VISIBLE);
                            lay_btm_sucess.setVisibility(View.VISIBLE);

                            JSONObject json = obj.getJSONObject("result");
                            tv_name.setText(Html.fromHtml(json.getString("eventname")));
                            tv_date.setText(json.getString("date"));
                            tv_time.setText(json.getString("start_time") + " To " + json.getString("end_time"));

                            tickets.clear();
                            int qty = 0;

                            JSONArray jsonArray = json.getJSONArray("ticket");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Ticket ticket = new Ticket(
                                        object.getString("name"),
                                        object.getString("price"),
                                        object.getString("total"),
                                        object.getString("qty"),
                                        object.getString("description")
                                );
                                tickets.add(ticket);
                                qty = qty + Integer.parseInt(object.getString("qty"));

                            }
                            System.out.println("Tickets size " + tickets.size());

                            tv_total_ticket.setText("" + qty);

                            ScanTicketAdapter adapter = new ScanTicketAdapter(ScanResultActivity.this, tickets);
                            recycler_view.setAdapter(adapter);


                        } else {
                            lay_main_failure.setVisibility(View.VISIBLE);
                            lay_btm_failure.setVisibility(View.VISIBLE);
                            str_message = obj.getString("Message");
//                            Toast.makeText(ScanResultActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            tv_result.setText(str_message);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ScanResultActivity.this, ScanResultActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, ScanResultActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }

    }

    private void back() {
        Intent intent = new Intent(ScanResultActivity.this, EventScanActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventId", eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }
}