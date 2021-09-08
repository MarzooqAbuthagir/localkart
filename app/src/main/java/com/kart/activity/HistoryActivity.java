package com.kart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.kart.R;
import com.kart.adapter.HistoryAdapter;
import com.kart.model.HistoryData;
import com.kart.model.UserDetail;
import com.kart.support.DividerItemDecorator;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    private String Tag = "HistoryActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    String str_result = "", str_message = "";

    private ArrayList<HistoryData> historyData = new ArrayList<HistoryData>();

    RecyclerView recyclerView;
    LinearLayout layHeader;
    TextView tvNoRecords;
    HorizontalScrollView horizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        utilis = new Utilis(HistoryActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(HistoryActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("History");


        horizontalScrollView = findViewById(R.id.hscrll1);
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HistoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        tvNoRecords = findViewById(R.id.tv_no_records);
        layHeader = findViewById(R.id.lay_header);
        getHistory();
    }

    private void getHistory() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(HistoryActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.posthistory, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getHistory response - " + response);

                        Utilis.dismissProgress();

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
                                HistoryData history = new HistoryData(
                                        jsonObject.getString("postIndexId"),
                                        jsonObject.getString("postDate"),
                                        jsonObject.getString("postCode"),
                                        jsonObject.getString("type"),
                                        jsonObject.getString("heading"),
                                        jsonObject.getString("count"),
                                        jsonObject.getString("shopType"),
                                        jsonObject.getString("shopIndexId"),
                                        jsonObject.getString("shopLatitude"),
                                        jsonObject.getString("shopLongitude"),
                                        jsonObject.getString("Status"));

                                historyData.add(history);
                            }

                            HistoryAdapter adapter = new HistoryAdapter(HistoryActivity.this, historyData, keyIntent);
                            recyclerView.setAdapter(adapter);

                            Drawable mDivider = ContextCompat.getDrawable(HistoryActivity.this, R.drawable.divider_line);
                            DividerItemDecoration itemDecoration = new DividerItemDecoration(HistoryActivity.this, DividerItemDecoration.VERTICAL);
                            itemDecoration.setDrawable(mDivider);
                            recyclerView.addItemDecoration(itemDecoration);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(HistoryActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(HistoryActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            layHeader.setVisibility(View.GONE);
                            horizontalScrollView.setVisibility(View.GONE);
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

                    Utilis.dismissProgress();
                    Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    System.out.println(Tag + " getHistory inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, HistoryActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(HistoryActivity.this, ManageBusinessActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}