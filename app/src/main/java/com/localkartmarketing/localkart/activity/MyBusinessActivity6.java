package com.localkartmarketing.localkart.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.AddServiceAdapter;
import com.localkartmarketing.localkart.model.AddressDetailsData;
import com.localkartmarketing.localkart.model.BasicDetailsData;
import com.localkartmarketing.localkart.model.ContactDetailsData;
import com.localkartmarketing.localkart.model.LocationData;
import com.localkartmarketing.localkart.model.UploadImages;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.RegBusinessIdSharedPreference;
import com.localkartmarketing.localkart.support.RegBusinessSharedPrefrence;
import com.localkartmarketing.localkart.support.RegBusinessTypeSharedPreference;
import com.localkartmarketing.localkart.support.Utilis;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBusinessActivity6 extends AppCompatActivity {
    private String Tag = "MyBusinessActivity6";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";
    BasicDetailsData basicDetailsData;
    AddressDetailsData addressDetailsData;
    ContactDetailsData contactDetailsData;
    LocationData locationData;
    String chips = "";
    List<UploadImages> uploadImagesArrayList;

    AddServiceAdapter addServiceAdapter;
    RecyclerView recyclerView;
    List<String> listOfService = new ArrayList<>();

    UserDetail obj;
    static SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business6);

        utilis = new Utilis(MyBusinessActivity6.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        basicDetailsData = Utilis.getBasicDetails(MyBusinessActivity6.this);
        addressDetailsData = Utilis.getAddressDetails(MyBusinessActivity6.this);
        contactDetailsData = Utilis.getContactDetails(MyBusinessActivity6.this);
        locationData = Utilis.getLocDetails(MyBusinessActivity6.this);
        chips = intent.getStringExtra("chips");
        uploadImagesArrayList = Utilis.getImageList("imageList");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MyBusinessActivity6.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("My Business");

        View progressView = findViewById(R.id.progress_view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 10);
        progressView.setLayoutParams(lp);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyBusinessActivity6.this);
        recyclerView.setLayoutManager(layoutManager);

        Drawable mDivider = ContextCompat.getDrawable(MyBusinessActivity6.this, R.drawable.divider_line);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(MyBusinessActivity6.this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(mDivider);
        recyclerView.addItemDecoration(itemDecoration);

        listOfService = Utilis.getServiceList(MyBusinessActivity6.this);

        addServiceAdapter = new AddServiceAdapter(this, listOfService, 1);
        recyclerView.setAdapter(addServiceAdapter);

        TextView tvBusTC = findViewById(R.id.tv_bus_tc);
        tvBusTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyBusinessActivity6.this, BusinessTCActivity.class));
            }
        });

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnNext = findViewById(R.id.btn_register);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();

                for (String str : listOfService) {
                    if (!stringBuilder.toString().isEmpty()) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(str);
                }
                String strService = stringBuilder.toString();
                updateBusiness(strService, view);
            }
        });
    }

    String str_result = "", str_message = "";

    private void updateBusiness(final String strService, final View view) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(MyBusinessActivity6.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.businessupdate, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " updateBusiness response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " updateBusiness result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");
                            String str_index_id = obj.getString("indexId");
                            uploadImages(str_index_id, view);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MyBusinessActivity6.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(MyBusinessActivity6.this, MyBusinessActivity6.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("type", basicDetailsData.getBusinessType());
                    params.put("indexId", basicDetailsData.getIndexId());
                    params.put("name", basicDetailsData.getBusinessName());
                    params.put("catId", basicDetailsData.getCategoryId());
                    params.put("subCatId", basicDetailsData.getSubCategoryId());
                    params.put("Image", basicDetailsData.getLogo());
                    params.put("description", basicDetailsData.getDesc());
                    params.put("doorNo", addressDetailsData.getDoorNo());
                    params.put("locality", addressDetailsData.getLocality());
                    params.put("area", addressDetailsData.getArea());
                    params.put("taulk", addressDetailsData.getPost());
                    params.put("landMark", addressDetailsData.getLandmark());
                    params.put("pincode", addressDetailsData.getPinCode());
                    params.put("state", addressDetailsData.getStateId());
                    params.put("district", addressDetailsData.getDistrictId());
                    params.put("phoneNo", contactDetailsData.getPhoneNo());
                    params.put("mobileNo", contactDetailsData.getMobileNo());
                    params.put("alternateNo", contactDetailsData.getAltNo());
                    params.put("watsappNo", contactDetailsData.getWhatsappNo());
                    params.put("emailAddress", contactDetailsData.getEmailId());
                    params.put("website", contactDetailsData.getWebsite());
                    params.put("facebook", contactDetailsData.getFacebook());
                    params.put("digitalVcard", contactDetailsData.getVcard());
                    params.put("cod", contactDetailsData.getCod());
                    params.put("latitude", locationData.getLatitude());
                    params.put("longitude", locationData.getLongitude());
                    params.put("address", locationData.getAddress());
                    params.put("tags", chips);
                    params.put("serviceList", strService);
                    System.out.println(Tag + " updateBusiness inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, MyBusinessActivity6.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    ProgressDialog progressDialog;

    private void uploadImages(final String str_index_id, final View view) {
        final List<UploadImages> list = new ArrayList<>();
        for (int i = 0; i < uploadImagesArrayList.size(); i++) {
            if (uploadImagesArrayList.get(i).getImageIndexId().isEmpty()) {
                list.add(uploadImagesArrayList.get(i));
            }
        }

        if (list.size() > 0) {
            progressDialog = ProgressDialog.show(MyBusinessActivity6.this, "Uploading Images",
                    "Please wait...", true);

            for (int i = 0; i < list.size(); i++) {

                final int currentPos = i;
                StringRequest request = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.uploadimage, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        if (currentPos + 1 == list.size()) {
                            System.out.println("on upload " + currentPos + " array size " + list.size());
                            progressDialog.dismiss();

                            successfulUpdate(view, str_index_id);

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(MyBusinessActivity6.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("Image", list.get(currentPos).getImage());
                        parameters.put("type", basicDetailsData.getBusinessType());
                        parameters.put("indexId", str_index_id);
                        System.out.println(Tag + " ImageUploadTask inputs " + parameters);
                        return parameters;
                    }
                };

                RequestQueue rQueue = Volley.newRequestQueue(MyBusinessActivity6.this);
                rQueue.add(request);

            }

        } else {
            successfulUpdate(view, str_index_id);
        }
    }

    private void successfulUpdate(View view, final String str_index_id) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyBusinessActivity6.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_business_update, viewGroup, false);
        Button btnManageMyBusiness = dialogView.findViewById(R.id.btn_manage_my_business);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        btnManageMyBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Utilis.clearRegPref(MyBusinessActivity6.this);

                RegBusinessSharedPrefrence.setMenuFlag(MyBusinessActivity6.this, "1");

                RegBusinessTypeSharedPreference.setBusinessType(MyBusinessActivity6.this, "Services");
                RegBusinessIdSharedPreference.setBusinessId(MyBusinessActivity6.this, str_index_id);

                Intent intent = new Intent(MyBusinessActivity6.this, ManageBusinessActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
        Utilis.saveServiceList(listOfService);
        finish();
    }
}