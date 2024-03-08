package com.localkartmarketing.localkart.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.localkartmarketing.localkart.adapter.EventBookTicketAdapter;
import com.localkartmarketing.localkart.model.EventTicket;
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

public class EventBookNowActivity extends AppCompatActivity {
    private String Tag = "EventBookNowActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", indexIntent = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    TextView tvName, tvDate, tvTime, tvTotal, tvQty;

    private ArrayList<EventTicket> tickets = new ArrayList<EventTicket>();
    RecyclerView recyclerView;
    private String instructions = null;
    EventBookTicketAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_book_now);

        utils = new Utils(EventBookNowActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        indexIntent = intent.getStringExtra("index");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(EventBookNowActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Event Booking");

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventBookNowActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(EventBookNowActivity.this, R.drawable.new_divider_line));
        recyclerView.addItemDecoration(dividerItemDecoration);

        tvName = findViewById(R.id.tv_name);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvQty = findViewById(R.id.tv_qty);
        tvTotal = findViewById(R.id.tv_total);

        getEventBook();

        Button btnBookNow = findViewById(R.id.btn_book_now);
        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(tvQty.getText().toString());
                if (qty > 0) {
                    System.out.println(Tag + " book now clicked " + instructions);
                    if (!instructions.equalsIgnoreCase("null")) {
                        showTC();
                    } else {
                        Intent intent = new Intent(EventBookNowActivity.this, EventPayNowActivity.class);
                        intent.putExtra("key", keyIntent);
                        intent.putExtra("index", indexIntent);
                        intent.putExtra("eventName", tvName.getText().toString());
                        intent.putExtra("eventDate", tvDate.getText().toString());
                        intent.putExtra("eventTime", tvTime.getText().toString());
                        intent.putParcelableArrayListExtra("dataList", tickets);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                } else {
                    Toast.makeText(EventBookNowActivity.this, "Please add tickets", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showTC() {
        final Dialog dialog = new Dialog(EventBookNowActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);
        dialog.getWindow().setContentView(R.layout.alert_tc);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        TextView tvTC = dialog.findViewById(R.id.tv_tc);
        tvTC.setText(Html.fromHtml(instructions));

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button btnAccept = dialog.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(EventBookNowActivity.this, EventPayNowActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("index", indexIntent);
                intent.putExtra("eventName", tvName.getText().toString());
                intent.putExtra("eventDate", tvDate.getText().toString());
                intent.putExtra("eventTime", tvTime.getText().toString());
                intent.putParcelableArrayListExtra("dataList", tickets);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    String str_result = "", str_message = "";

    private void getEventBook() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(EventBookNowActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.eventticketavailablity + "?eventid=15", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getEventBook response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getEventBook result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            JSONObject json = obj.getJSONObject("result");

                            tvName.setText(Html.fromHtml(json.getString("eventname")));
                            tvDate.setText(json.getString("date"));
                            tvTime.setText(json.getString("start_time") + " To " + json
                                    .getString("end_time"));

                            tickets.clear();

                            JSONArray jsonArray = json.getJSONArray("ticket");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                EventTicket ticket = new EventTicket(
                                        object.getString("name"),
                                        object.getString("description"),
                                        object.getString("price"),
                                        object.getInt("remaining"),
                                        0
                                );
                                tickets.add(ticket);
                            }

                            instructions = json.getString("instructions");

                            adapter = new EventBookTicketAdapter(EventBookNowActivity.this, tickets, tvQty, tvTotal);
                            recyclerView.setAdapter(adapter);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventBookNowActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(EventBookNowActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(EventBookNowActivity.this, EventBookNowActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, EventBookNowActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<EventTicket> updatedList = getIntent().getParcelableArrayListExtra("dataList");
        if (updatedList != null) {
            // Update the existing itemList with the updatedItemList
            tickets.clear();
            tickets.addAll(updatedList);
            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent;
        if (indexIntent.equalsIgnoreCase("0")) {
            intent = new Intent(EventBookNowActivity.this, EventActivity.class);
        } else {
            intent = new Intent(EventBookNowActivity.this, EventDetailActivity.class);
        }
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
}