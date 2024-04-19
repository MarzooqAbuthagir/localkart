package com.localkartmarketing.localkart.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
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
import com.google.zxing.WriterException;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.GSTAdapter;
import com.localkartmarketing.localkart.adapter.TicketAdapter;
import com.localkartmarketing.localkart.model.GST;
import com.localkartmarketing.localkart.model.Ticket;
import com.localkartmarketing.localkart.support.DividerItemDecorator;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class BookingDetailsActivity extends AppCompatActivity {
    private String Tag = "BookingDetailsActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", indexIntent = "", eventId = "";

    boolean isToggle = false;
    ImageView layConvenienceToggle;
    View myDividerView;
    RelativeLayout layRelcon;
    LinearLayout layConvenience, layConvenienceAmount, layDetailCon;
    String str_result = "", str_message = "";
    TextView tvName, tvDate, tvTime, tvAddress1, tvAddress2, tvAddress3, tvDistrict, tvState, tvTotalQty, tvTotal, tvTickets, tvTotConvFee;
    private ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    private ArrayList<Ticket> ticketsConvFee = new ArrayList<Ticket>();
    private ArrayList<GST> gstArrayList = new ArrayList<GST>();
    RecyclerView recyclerView, recyclerViewConvFee, recyclerViewGST;
    String bookingId = "", mapLink = "";
    ImageView imageViewQR;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    Button btnBack;
    boolean isConvenience = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        isToggle = false;

        utils = new Utils(BookingDetailsActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        indexIntent = intent.getStringExtra("index");
        eventId = intent.getStringExtra("eventId");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(BookingDetailsActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Booking Details");

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

        tvName = findViewById(R.id.tv_name);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvAddress1 = findViewById(R.id.tv_address1);
        tvAddress2 = findViewById(R.id.tv_address2);
        tvAddress3 = findViewById(R.id.tv_address3);
        tvDistrict = findViewById(R.id.tv_district);
        tvState = findViewById(R.id.tv_state);
        tvTotalQty = findViewById(R.id.tv_total_qty);
        tvTotal = findViewById(R.id.tv_total);
        tvTickets = findViewById(R.id.tv_tickets);
        imageViewQR = findViewById(R.id.iv_qr);
        tvTotConvFee = findViewById(R.id.tv_conv_fee_total);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BookingDetailsActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewConvFee = findViewById(R.id.recycler_view_conv_fee);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(BookingDetailsActivity.this);
        recyclerViewConvFee.setHasFixedSize(true);
        recyclerViewConvFee.setNestedScrollingEnabled(false);
        recyclerViewConvFee.setLayoutManager(layoutManager1);

        recyclerViewGST = findViewById(R.id.recycler_view_gst);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(BookingDetailsActivity.this);
        recyclerViewGST.setHasFixedSize(true);
        recyclerViewGST.setNestedScrollingEnabled(false);
        recyclerViewGST.setLayoutManager(layoutManager2);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(BookingDetailsActivity.this, R.drawable.new_divider_line));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerViewConvFee.addItemDecoration(dividerItemDecoration);

        getBookingDetail();

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

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

    private void getBookingDetail() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(BookingDetailsActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.eventbookingdetails + "?id=" + eventId, new Response.Listener<String>() {
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
                            tvName.setText(Html.fromHtml(json.getString("event_name")));
                            tvDate.setText(json.getString("event_date"));
                            tvTime.setText(json.getString("event_time"));
                            tvAddress1.setText(json.getString("address1") + ",");
                            tvAddress2.setText(json.getString("address2") + ",");
                            tvAddress3.setText(json.getString("address3") + ",");
                            tvDistrict.setText(json.getString("district") + ",");
                            tvState.setText(json.getString("state") + " - " + json.getString("pincode"));
                            bookingId = json.getString("bookingid");
                            TextView tvBookingId = findViewById(R.id.tv_booking_id);
                            tvBookingId.setText(bookingId);
                            mapLink = json.getString("map");

                            isConvenience = json.getInt("is_confee") == 0 ? false : true;

                            tvTotConvFee.setText(json.getString("con_fees_total"));

                            if (isConvenience) {
                                layRelcon.setVisibility(View.VISIBLE);
                                myDividerView.setVisibility(View.VISIBLE);
                            } else {
                                layRelcon.setVisibility(View.GONE);
                                myDividerView.setVisibility(View.GONE);
                            }

                            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                            // initializing a variable for default display.
                            Display display = manager.getDefaultDisplay();
                            // creating a variable for point which
                            // is to be displayed in QR Code.
                            Point point = new Point();
                            display.getSize(point);
                            // getting width and
                            // height of a point
                            int width = point.x;
                            int height = point.y;
                            // generating dimension from width and height.
                            int dimen = width < height ? width : height;
                            dimen = dimen * 3 / 4;
                            // setting this dimensions inside our qr code
                            // encoder to generate our qr code.
                            qrgEncoder = new QRGEncoder(bookingId, null, QRGContents.Type.TEXT, dimen);
                            try {
                                // getting our qrcode in the form of bitmap.
                                bitmap = qrgEncoder.encodeAsBitmap();
                                // the bitmap is set inside our image
                                // view using .setimagebitmap method.
                                imageViewQR.setImageBitmap(bitmap);
                            } catch (WriterException e) {
                                // this method is called for
                                // exception handling.
                                Log.e("Tag", e.toString());
                            }

                            tickets.clear();
//                            int qty = 0;
//                            int total = 0;

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
//                                qty = qty + Integer.parseInt(object.getString("qty"));
//                                total = total + Integer.parseInt(object.getString("total"));
                            }
                            System.out.println("Tickets size " + tickets.size());

                            tvTotalQty.setText(json.getString("quantity"));
                            tvTotal.setText(json.getString("grand_total"));

                            tvTickets.setText(json.getString("quantity") + " Tickets");

                            TicketAdapter adapter = new TicketAdapter(BookingDetailsActivity.this, tickets);
                            recyclerView.setAdapter(adapter);

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

                            }

                            TicketAdapter adapter1 = new TicketAdapter(BookingDetailsActivity.this, ticketsConvFee);
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
                            }

                            GSTAdapter adapter2 = new GSTAdapter(BookingDetailsActivity.this, gstArrayList);
                            recyclerViewGST.setAdapter(adapter2);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(BookingDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(BookingDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(BookingDetailsActivity.this, BookingDetailsActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, BookingDetailsActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        if (indexIntent.equalsIgnoreCase("0")) {
            Intent intent = new Intent(BookingDetailsActivity.this, EventActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(BookingDetailsActivity.this, MyBookingActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }
}