package com.localkartmarketing.localkart.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.EventTicketAdapter;
import com.localkartmarketing.localkart.model.EventTicket;
import com.localkartmarketing.localkart.support.DividerItemDecorator;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageEventDetailActivity extends AppCompatActivity {
    private String Tag = "ManageEventDetailActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", eventId = "";

    String str_result = "", str_message = "", mapLink = "";
    private ArrayList<EventTicket> tickets = new ArrayList<EventTicket>();
    RecyclerView recyclerView;

    ImageView imageView;
    TextView tvName, tvDate, tvTime, tvDesc, tvNotes, tvAddress1, tvAddress2, tvAddress3, tvDistrict, tvState;
    TextView tvMobile, tvAltMobile, tvWhatsapp, tvEmail;

    LinearLayout laySummary, layBookings;
    TextView tvDescTitle, tvNotesTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event_detail);

        utils = new Utils(ManageEventDetailActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        eventId = intent.getStringExtra("eventId");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ManageEventDetailActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Event Details");


        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ManageEventDetailActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

//        Drawable mDivider = ContextCompat.getDrawable(ManageEventDetailActivity.this, R.drawable.divider_line);
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(ManageEventDetailActivity.this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(mDivider);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(ManageEventDetailActivity.this, R.drawable.new_divider_line));
        recyclerView.addItemDecoration(itemDecoration);

        imageView = findViewById(R.id.imageView);
        tvName = findViewById(R.id.tv_name);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvDesc = findViewById(R.id.tv_desc);
        tvNotes = findViewById(R.id.tv_notes);
        tvAddress1 = findViewById(R.id.tv_address1);
        tvAddress2 = findViewById(R.id.tv_address2);
        tvAddress3 = findViewById(R.id.tv_address3);
        tvDistrict = findViewById(R.id.tv_district);
        tvState = findViewById(R.id.tv_state);
        tvDescTitle = findViewById(R.id.tv_desc_title);
        tvNotesTitle = findViewById(R.id.tv_notes_title);

        tvMobile = findViewById(R.id.tv_mobile);
        tvAltMobile = findViewById(R.id.tv_alt_mobile);
        tvWhatsapp = findViewById(R.id.tv_whatsapp);
        tvEmail = findViewById(R.id.tv_email);

        laySummary = findViewById(R.id.lay_summary);
        layBookings = findViewById(R.id.lay_bookings);

        laySummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventDetailActivity.this, SummaryActivity.class);
                intent.putExtra("key", "Events");
                intent.putExtra("eventId", eventId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        layBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageEventDetailActivity.this, ManageEventBookingActivity.class);
                intent.putExtra("key", "Events");
                intent.putExtra("eventId", eventId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        getEventDetail();

        LinearLayout layDirection = findViewById(R.id.lay_direction);
        TextView tvDirection = findViewById(R.id.tv_direction);
        layDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect();
            }
        });
        tvDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect();
            }
        });

        RelativeLayout layMobile = findViewById(R.id.lay_mobile);
        RelativeLayout layAltMobile = findViewById(R.id.lay_alt_mobile);
        RelativeLayout layWa = findViewById(R.id.lay_wa);
        RelativeLayout layEmail = findViewById(R.id.lay_email);

        layMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telPhone = "tel:" + tvMobile.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(telPhone));
                startActivity(callIntent);
            }
        });

        layAltMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telPhone = "tel:" + tvAltMobile.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(telPhone));
                startActivity(callIntent);
            }
        });

        layWa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=+91" + tvWhatsapp.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        layEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                            Uri.fromParts("mailto", tvEmail.getText().toString(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
                } catch (android.content.ActivityNotFoundException e) {
                    System.out.println("There is no email client installed.");
                }
            }
        });
    }

    private void redirect() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapLink));
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

    private void getEventDetail() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(ManageEventDetailActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.eventdetails + "?id=" + eventId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getBookingDetail response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getBookingDetail result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {


                            JSONObject json = obj.getJSONObject("result");

                            Glide.with(ManageEventDetailActivity.this).load(json.getString("image"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

                            tvName.setText(Html.fromHtml(json.getString("eventname")));
                            tvDate.setText(json.getString("date"));
                            tvTime.setText(json.getString("start_time") + " To " + json.getString("end_time"));
                            tvDesc.setText(Html.fromHtml(json.getString("description")));
                            tvNotes.setText(Html.fromHtml(json.getString("notes")));
                            tvDescTitle.setText(Html.fromHtml(json.getString("description_title")));
                            tvNotesTitle.setText(Html.fromHtml(json.getString("notes_title")));
                            tvAddress1.setText(json.getString("address1") + ",");
                            tvAddress2.setText(json.getString("address2") + ",");
                            tvAddress3.setText(json.getString("address3") + ",");
                            tvDistrict.setText(json.getString("district") + ",");
                            tvState.setText(json.getString("state_name") + " - " + json.getString("pincode") + ".");

                            tvMobile.setText(json.getString("contact_mobile"));
                            tvAltMobile.setText(json.getString("contact_alt_mobile"));
                            tvWhatsapp.setText(json.getString("contact_whatsapp"));
                            tvEmail.setText(json.getString("contact_email"));

                            mapLink = json.getString("map");

                            tickets.clear();

                            JSONArray jsonArray = json.getJSONArray("ticket");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                EventTicket ticket = new EventTicket(
                                        object.getString("name"),
                                        object.getString("description"),
                                        object.getString("price")
                                );
                                tickets.add(ticket);
                            }
                            System.out.println("Tickets size " + tickets.size());

                            EventTicketAdapter adapter = new EventTicketAdapter(ManageEventDetailActivity.this, tickets);
                            recyclerView.setAdapter(adapter);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(ManageEventDetailActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(ManageEventDetailActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(ManageEventDetailActivity.this, ManageEventDetailActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, ManageEventDetailActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(ManageEventDetailActivity.this, ManageEventActivity.class);
        intent.putExtra("key", "Events");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}