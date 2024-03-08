package com.localkartmarketing.localkart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.EventPayTicketAdapter;
import com.localkartmarketing.localkart.adapter.GSTAdapter;
import com.localkartmarketing.localkart.adapter.TicketAdapter;
import com.localkartmarketing.localkart.model.EventTicket;
import com.localkartmarketing.localkart.model.GST;
import com.localkartmarketing.localkart.model.Ticket;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.DividerItemDecorator;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class EventPayNowActivity extends AppCompatActivity implements PaymentResultListener {

    private String Tag = "EventPayNowActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", indexIntent = "", eventName = "", eventDate = "", eventTime = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    private ArrayList<EventTicket> tickets = new ArrayList<EventTicket>();
    private ArrayList<Ticket> ticketsConvFee = new ArrayList<Ticket>();
    private ArrayList<GST> gstArrayList = new ArrayList<GST>();
    RecyclerView recyclerView, recyclerViewConvFee, recyclerViewGST;
    boolean isConvenience = false;
    View myDividerView;
    boolean isToggle = false;
    ImageView layConvenienceToggle;
    RelativeLayout layRelcon;
    LinearLayout layConvenience, layConvenienceAmount, layDetailCon;
    TextView tvTotal, tvTotConvFee;
    double total = 0;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pay_now);

        utils = new Utils(EventPayNowActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);
        tickets = getIntent().getParcelableArrayListExtra("dataList");
        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        indexIntent = intent.getStringExtra("index");
        eventName = intent.getStringExtra("eventName");
        eventDate = intent.getStringExtra("eventDate");
        eventTime = intent.getStringExtra("eventTime");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(EventPayNowActivity.this, R.color.colorPrimaryDark));

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

        TextView tvName = findViewById(R.id.tv_name);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvTotQty = findViewById(R.id.tv_total_qty);
        tvTotal = findViewById(R.id.tv_total);
        TextView tvEdit = findViewById(R.id.tv_edit);
        tvTotConvFee = findViewById(R.id.tv_conv_fee_total);

        tvName.setText(eventName);
        tvDate.setText(eventDate);
        tvTime.setText(eventTime);

        int qty = 0;
        for (int i = 0; i < tickets.size(); i++) {
            qty = qty + tickets.get(i).getQty();
            total = total + (tickets.get(i).getQty() * Integer.parseInt(tickets.get(i).getPrice()));
        }
        tvTotQty.setText("" + qty);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EventPayNowActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewConvFee = findViewById(R.id.recycler_view_conv_fee);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(EventPayNowActivity.this);
        recyclerViewConvFee.setHasFixedSize(true);
        recyclerViewConvFee.setNestedScrollingEnabled(false);
        recyclerViewConvFee.setLayoutManager(layoutManager1);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(EventPayNowActivity.this, R.drawable.new_divider_line));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerViewConvFee.addItemDecoration(itemDecoration);

        recyclerViewGST = findViewById(R.id.recycler_view_gst);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(EventPayNowActivity.this);
        recyclerViewGST.setHasFixedSize(true);
        recyclerViewGST.setNestedScrollingEnabled(false);
        recyclerViewGST.setLayoutManager(layoutManager2);

        myDividerView = findViewById(R.id.id_view);
        layRelcon = findViewById(R.id.rel_con_lay);
        layDetailCon = findViewById(R.id.lay_detail_con);
        layConvenienceToggle = findViewById(R.id.img_convenience_toggle);
        layConvenience = findViewById(R.id.lay_convenience);
        layConvenienceAmount = findViewById(R.id.lay_convenience_amount);

        layDetailCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isToggle = !isToggle;
                if (isToggle) {
                    layConvenienceAmount.setVisibility(View.GONE);
                    layConvenience.setVisibility(View.VISIBLE);
                    layConvenienceToggle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24));
                } else {
                    layConvenience.setVisibility(View.GONE);
                    layConvenienceAmount.setVisibility(View.VISIBLE);
                    layConvenienceToggle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24));
                }
            }
        });

        EventPayTicketAdapter adapter = new EventPayTicketAdapter(EventPayNowActivity.this, tickets);
        recyclerView.setAdapter(adapter);

        getConvenienceDetail();

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnPayNow = findViewById(R.id.btn_pay_now);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // rounding off the amount.
                double amountToPay = Math.round(total * 100);

                // initialize Razorpay account.
                Checkout checkout = new Checkout();

                // set your id as below
//                            checkout.setKeyID(Utils.razorPayTestKey);
                checkout.setKeyID(Utils.razorPayLiveKey);

                // initialize json object
                JSONObject object = new JSONObject();
                try {
                    // to put name
                    object.put("name", userDetail.getName());

                    // put description
                    object.put("description", "Purchasing subscription package");

                    // to set theme color
                    object.put("theme.color", "");

                    // put the currency
                    object.put("currency", "INR");

                    // put amount
                    object.put("amount", amountToPay);

                    // put mobile number
                    object.put("prefill.contact", userDetail.getMobile());

                    // put email
                    object.put("prefill.email", userDetail.getEmail().isEmpty() || userDetail.getEmail() == null ? "localkartapp@gmail.com" : userDetail.getEmail());

                    // open razorpay to checkout activity
                    checkout.open(EventPayNowActivity.this, object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    String str_result = "";

    private void getConvenienceDetail() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(EventPayNowActivity.this);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", "6");
                jsonObject.put("eventId", "15");
                JSONArray bookTicketArray = new JSONArray();
                for (int i = 0; i < tickets.size(); i++) {
                    JSONObject ticket1 = new JSONObject();
                    ticket1.put("name", tickets.get(i).getName());
                    ticket1.put("price", tickets.get(i).getPrice());
                    ticket1.put("qty", tickets.get(i).getQty());
                    bookTicketArray.put(ticket1);
                }
//                bookTicketArray.put(tickets);
                jsonObject.put("bookTicket", bookTicketArray);

                System.out.println(Tag + " conv fee inputs==>" + jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.Api + Utils.confeecalculation, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject obj) {
                    System.out.println("success");
                    try {
                        Utils.dismissProgress();
                        str_result = obj.getString("errorCode");

                        if (Integer.parseInt(str_result) == 0) {

                            JSONObject json = obj.getJSONObject("result");

                            isConvenience = json.getInt("is_confee") == 0 ? false : true;

                            if (isConvenience) {
                                layRelcon.setVisibility(View.VISIBLE);
                                myDividerView.setVisibility(View.VISIBLE);
                            } else {
                                layRelcon.setVisibility(View.GONE);
                                myDividerView.setVisibility(View.GONE);
                            }

                            tvTotConvFee.setText(json.getString("con_fees_total"));
//                            tvTotal.setText(json.getString("grand_total"));

                            ticketsConvFee.clear();

                            JSONArray jsonArray1 = json.getJSONArray("con_fees");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                Ticket ticket = new Ticket(
                                        object.getString("name"),
                                        object.getString("price"),
                                        object.getString("total"),
                                        object.getString("qty"),
                                        ""
                                );
                                ticketsConvFee.add(ticket);
                                total = total + Integer.parseInt(object.getString("total"));
                            }
                            System.out.println(Tag + "conv fee size==>" + ticketsConvFee.size());
                            TicketAdapter adapter1 = new TicketAdapter(EventPayNowActivity.this, ticketsConvFee);
                            recyclerViewConvFee.setAdapter(adapter1);

                            gstArrayList.clear();

                            JSONArray jsonArray2 = json.getJSONArray("gst");
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject object = jsonArray2.getJSONObject(i);
                                GST gst = new GST(
                                        object.getString("label"),
                                        object.getString("percent"),
                                        object.getString("amount")
                                );
                                gstArrayList.add(gst);
                                total = total + Double.parseDouble(object.getString("amount"));
                            }

                            GSTAdapter adapter2 = new GSTAdapter(EventPayNowActivity.this, gstArrayList);
                            recyclerViewGST.setAdapter(adapter2);

                            df.setRoundingMode(RoundingMode.DOWN);
                            tvTotal.setText("" + df.format(total));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("failure");
                    Utils.dismissProgress();
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(request);

        } else {
            Toast.makeText(this, EventPayNowActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
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
    public void onPaymentSuccess(String s) {
//        Toast.makeText(this, "Payment Success : " + s, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EventPayNowActivity.this, PaymentStatusActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("payStatus", "success");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
//        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EventPayNowActivity.this, PaymentStatusActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("payStatus", "fail");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}