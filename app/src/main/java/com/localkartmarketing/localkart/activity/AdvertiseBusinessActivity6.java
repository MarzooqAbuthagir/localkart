package com.localkartmarketing.localkart.activity;

import android.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertiseBusinessActivity6 extends AppCompatActivity {
    private String Tag = "AdvertiseBusinessActivity6";
    Utils utils;
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
    private EditText etBusRefCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_business6);

        utils = new Utils(AdvertiseBusinessActivity6.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        basicDetailsData = Utils.getBasicDetails(AdvertiseBusinessActivity6.this);
        addressDetailsData = Utils.getAddressDetails(AdvertiseBusinessActivity6.this);
        contactDetailsData = Utils.getContactDetails(AdvertiseBusinessActivity6.this);
        locationData = Utils.getLocDetails(AdvertiseBusinessActivity6.this);
        chips = intent.getStringExtra("chips");
        uploadImagesArrayList = Utils.getImageList("imageList");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AdvertiseBusinessActivity6.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Register Your Business");

        etBusRefCode = findViewById(R.id.et_bus_refcode);

        View progressView = findViewById(R.id.progress_view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 10);
        progressView.setLayoutParams(lp);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AdvertiseBusinessActivity6.this);
        recyclerView.setLayoutManager(layoutManager);

        Drawable mDivider = ContextCompat.getDrawable(AdvertiseBusinessActivity6.this, R.drawable.divider_line);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(AdvertiseBusinessActivity6.this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(mDivider);
        recyclerView.addItemDecoration(itemDecoration);

        Button btnAddService = findViewById(R.id.btn_add_service);
        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServiceDialog(view);
            }
        });

        listOfService = Utils.getServiceList(AdvertiseBusinessActivity6.this);
        if (listOfService != null) {
            System.out.println("array list contains images");
        } else {
            listOfService = new ArrayList<>();
        }

        addServiceAdapter = new AddServiceAdapter(this, listOfService, 0);
        recyclerView.setAdapter(addServiceAdapter);

        addServiceAdapter.setOnItemClickListener(new AddServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                listOfService.remove(position);
                addServiceAdapter.notifyDataSetChanged();
            }
        });

        final CheckBox checkBox = findViewById(R.id.checkbox);
        TextView tvBusTC = findViewById(R.id.tv_bus_tc);
        tvBusTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdvertiseBusinessActivity6.this, BusinessTCActivity.class));
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
                if (listOfService.size() == 0) {
                    Toast.makeText(AdvertiseBusinessActivity6.this, "Add service", Toast.LENGTH_SHORT).show();
                } else if (!checkBox.isChecked()) {
                    Toast.makeText(AdvertiseBusinessActivity6.this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show();
                }else {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String str : listOfService) {
                        if (!stringBuilder.toString().isEmpty()) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(str);
                    }
                    String strService = stringBuilder.toString();
                    registerBusiness(strService, view);
                }
            }
        });
    }

    String str_result = "", str_message = "";

    private void registerBusiness(final String strService, final View view) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(AdvertiseBusinessActivity6.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.businesssave, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " registerBusiness response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " registerBusiness result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");
                            String str_index_id = obj.getString("indexId");
                            uploadImages(str_index_id, view);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity6.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            String str_index_id = obj.getString("indexId");
                            uploadImages(str_index_id, view);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(AdvertiseBusinessActivity6.this, AdvertiseBusinessActivity6.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("busRefCode", etBusRefCode.getText().toString().trim());
                    System.out.println(Tag + " registerBusiness inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, AdvertiseBusinessActivity6.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    ProgressDialog progressDialog;

    private void uploadImages(final String str_index_id, final View view) {
        progressDialog = ProgressDialog.show(AdvertiseBusinessActivity6.this, "Uploading Images",
                "Please wait...", true);

        for (int i = 0; i < uploadImagesArrayList.size(); i++) {

            final int currentPos = i;
            StringRequest request = new StringRequest(Request.Method.POST, Utils.Api + Utils.uploadimage, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {

                    if (currentPos + 1 == uploadImagesArrayList.size()) {
                        System.out.println("on upload " + currentPos + " array size " + uploadImagesArrayList.size());
                        progressDialog.dismiss();

                        successfulRegister(view, str_index_id);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Toast.makeText(AdvertiseBusinessActivity6.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("Image", uploadImagesArrayList.get(currentPos).getImage());
                    parameters.put("type", basicDetailsData.getBusinessType());
                    parameters.put("indexId", str_index_id);
                    System.out.println(Tag + " ImageUploadTask inputs " + parameters);
                    return parameters;
                }
            };

            RequestQueue rQueue = Volley.newRequestQueue(AdvertiseBusinessActivity6.this);
            rQueue.add(request);

        }
    }

    private void successfulRegister(View view, final String str_index_id) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdvertiseBusinessActivity6.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_business_register, viewGroup, false);
        Button btnManageMyBusiness = dialogView.findViewById(R.id.btn_manage_my_business);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        btnManageMyBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Utils.clearRegPref(AdvertiseBusinessActivity6.this);

                RegBusinessSharedPrefrence.setMenuFlag(AdvertiseBusinessActivity6.this, "1");

                RegBusinessTypeSharedPreference.setBusinessType(AdvertiseBusinessActivity6.this, "Services");
                RegBusinessIdSharedPreference.setBusinessId(AdvertiseBusinessActivity6.this, str_index_id);

                Intent intent = new Intent(AdvertiseBusinessActivity6.this, ManageBusinessActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        alertDialog.show();
    }

    private void addServiceDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AdvertiseBusinessActivity6.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_service, viewGroup, false);
        final EditText etService = dialogView.findViewById(R.id.et_service);
        Button btnAdd = dialogView.findViewById(R.id.btn_add);
        Button btnClose = dialogView.findViewById(R.id.btn_close);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strServiceName = etService.getText().toString().trim();
                if (strServiceName.isEmpty()) {
                    Toast.makeText(AdvertiseBusinessActivity6.this, "Enter service", Toast.LENGTH_SHORT).show();
                } else if (strServiceName.length() < 4) {
                    Toast.makeText(AdvertiseBusinessActivity6.this, "service name atleast 4 characters", Toast.LENGTH_SHORT).show();
                } else {
                    listOfService.add(0, strServiceName);
                    addServiceAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
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
        Utils.saveServiceList(listOfService);
        finish();
    }
}