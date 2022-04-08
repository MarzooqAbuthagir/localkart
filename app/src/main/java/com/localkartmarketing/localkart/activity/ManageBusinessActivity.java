package com.localkartmarketing.localkart.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
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
import com.localkartmarketing.localkart.adapter.ManageBusinessMenuAdapter;
import com.localkartmarketing.localkart.model.ManageBusinessMenu;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.LocationTrack;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageBusinessActivity extends AppCompatActivity {
    private final String Tag = "ManageBusinessActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    LocationTrack currentLocation;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CODE = 101;

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    RecyclerView recyclerView;
    List<ManageBusinessMenu> manageBusinessMenuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_business);

        utils = new Utils(ManageBusinessActivity.this);

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
        window.setStatusBarColor(ContextCompat.getColor(ManageBusinessActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Manage Business");

        CardView cardMyBusiness = findViewById(R.id.card_business_layout);
        CardView cardSubscription = findViewById(R.id.card_subscription_layout);
        CardView cardPost = findViewById(R.id.card_post_layout);
        CardView cardHistory = findViewById(R.id.card_history_layout);
        CardView cardHelp = findViewById(R.id.card_help_layout);
        CardView cardDigitalVcard = findViewById(R.id.card_digital_vcard);

        cardMyBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.clearRegPref(ManageBusinessActivity.this);
                Intent intent = new Intent(ManageBusinessActivity.this, MyBusinessActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, SubscriptionActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, CreatePostActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, HistoryActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageBusinessActivity.this, FAQActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        cardDigitalVcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchLastLocation();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ManageBusinessActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        getManageBusinessMenuList();
    }

    private void getManageBusinessMenuList() {
        Utils.showProgress(ManageBusinessActivity.this);

        if (Utils.isInternetOn()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.getmegasales, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getManageBusinessMenuList response - " + response);

                        Utils.dismissProgress();

                        String str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getManageBusinessMenuList result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            manageBusinessMenuList.clear();

                            recyclerView.setVisibility(View.VISIBLE);

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                ManageBusinessMenu manageBusinessMenu = new ManageBusinessMenu();

                                manageBusinessMenu.setMegaSalesIndexId(jsonObject.getString("megaSalesIndexId"));
                                manageBusinessMenu.setOfferTitle(jsonObject.getString("offerTitle"));
                                manageBusinessMenu.setTotalDeals(jsonObject.getString("totalDeals"));
                                manageBusinessMenu.setFromDate(jsonObject.getString("fromDate"));
                                manageBusinessMenu.setToDate(jsonObject.getString("toDate"));
                                manageBusinessMenu.setAllowFrom(jsonObject.getString("allowFrom"));
                                manageBusinessMenu.setIcon(jsonObject.getString("icon"));
                                manageBusinessMenu.setShowMenu(jsonObject.getString("showMenu"));
                                manageBusinessMenu.setIsAllow(jsonObject.getString("isAllow"));
                                manageBusinessMenu.setErrorMessage(jsonObject.getString("errorMessage"));
                                manageBusinessMenu.setIsAlready(jsonObject.getString("isAlready"));
                                manageBusinessMenu.setAlreadyMessage(jsonObject.getString("alreadyMessage"));
                                manageBusinessMenu.setfDate(jsonObject.getString("fDate"));
                                manageBusinessMenu.settDate(jsonObject.getString("tDate"));

                                manageBusinessMenuList.add(manageBusinessMenu);
                            }
                            ManageBusinessMenuAdapter adapter = new ManageBusinessMenuAdapter(ManageBusinessActivity.this, manageBusinessMenuList, keyIntent);
                            recyclerView.setAdapter(adapter);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utils.dismissProgress();
                    Toast.makeText(ManageBusinessActivity.this, ManageBusinessActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
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
                    System.out.println(Tag + " getManageBusinessMenuList inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, ManageBusinessActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(ManageBusinessActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ManageBusinessActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ManageBusinessActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        if (Utils.isGpsOn()) {
            currentLocation = new LocationTrack(ManageBusinessActivity.this);

            if (currentLocation.canGetLocation()) {
                longitude = currentLocation.getLongitude();
                latitude = currentLocation.getLatitude();
                System.out.println("latitude " + latitude + " and longitude " + longitude);

                Intent intent = new Intent(ManageBusinessActivity.this, DigitalVCardActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("latitude", String.valueOf(latitude));
                intent.putExtra("longitude", String.valueOf(longitude));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        } else {
            Toast.makeText(ManageBusinessActivity.this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission Granted");
                if (Utils.isGpsOn()) {
                    fetchLastLocation();
                }
            } else {
                Toast.makeText(ManageBusinessActivity.this, "Enable GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Utils.isGpsOn()) {
                fetchLastLocation();
            } else {
                Toast.makeText(ManageBusinessActivity.this, "Enable GPS", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(ManageBusinessActivity.this, MainActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}