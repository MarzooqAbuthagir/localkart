package com.kart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kart.R;
import com.kart.model.AddressDetailsData;
import com.kart.model.BasicDetailsData;
import com.kart.model.ContactDetailsData;
import com.kart.model.LocationData;
import com.kart.model.UploadImages;
import com.kart.model.UserDetail;
import com.kart.support.RegBusinessTypeSharedPreference;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBusinessActivity extends AppCompatActivity {
    private String Tag = "MyBusinessActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    UserDetail obj;
    static SharedPreferences mPrefs;

    SearchableSpinner spinBusinessType;
    SearchableSpinner spinCategory;
    SearchableSpinner spinSubCategory;

    private List<String> businessTypeSpinnerValue = new ArrayList<>();
    private List<String> categorySpinnerValue = new ArrayList<>();
    private List<String> subCategorySpinnerValue = new ArrayList<>();

    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business);

        utilis = new Utilis(MyBusinessActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MyBusinessActivity.this, R.color.colorPrimaryDark));

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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200, 10);
        progressView.setLayoutParams(lp);

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBusinessActivity.this, MyBusinessActivity2.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        getMyBusinessData();
    }

    String str_result = "", str_message = "";
    private void getMyBusinessData() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(MyBusinessActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.editbusiness, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");

                            JSONObject basicDetail = json.getJSONObject("basicDetails");
                            BasicDetailsData basicDetailsData = new BasicDetailsData(
                                    basicDetail.getString("businessType"),
                                    basicDetail.getString("businessName"),
                                    basicDetail.getString("category"),
                                    basicDetail.getString("subCategory"),
                                    basicDetail.getString("shopLogo"),
                                    basicDetail.getString("description")
                            );
                            Utilis.saveBasicDetails(basicDetailsData);

                            JSONObject addressDetail = json.getJSONObject("addressDetails");
                            AddressDetailsData addressDetailsData = new AddressDetailsData(
                                    addressDetail.getString("doorNo"),
                                    addressDetail.getString("locality"),
                                    addressDetail.getString("area"),
                                    addressDetail.getString("taulk"),
                                    addressDetail.getString("landMark"),
                                    addressDetail.getString("pincode"),
                                    addressDetail.getString("state"),
                                    addressDetail.getString("district")
                            );
                            Utilis.saveAddressDetails(addressDetailsData);

                            JSONObject contactDetail = json.getJSONObject("contactDetails");
                            ContactDetailsData contactDetailsData = new ContactDetailsData(
                                    contactDetail.getString("phoneNumber"),
                                    contactDetail.getString("mobileNumber"),
                                    contactDetail.getString("alternateNumber"),
                                    contactDetail.getString("watsappNumber"),
                                    contactDetail.getString("emailAddress"),
                                    contactDetail.getString("website"),
                                    contactDetail.getString("facebook"),
                                    contactDetail.getString("digitalVcard")
                            );
                            Utilis.saveContactDetails(contactDetailsData);

                            JSONObject locationData = json.getJSONObject("locationDetails");
                            LocationData locationDetailsData = new LocationData(
                                    locationData.getString("address"),
                                    locationData.getString("latitude"),
                                    locationData.getString("longitude")
                            );
                            Utilis.saveLocDetails(locationDetailsData);

                            List<UploadImages> uploadImagesList = new ArrayList<>();
                            JSONArray jsonArray = json.getJSONArray("imageDetails");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                UploadImages uploadImages = new UploadImages();
                                uploadImages.setImage(object.getString("imageUrl"));
                                uploadImagesList.add(uploadImages);
                            }
                            Utilis.clearImageList(MyBusinessActivity.this);
                            Utilis.setImageList("imageList", uploadImagesList, MyBusinessActivity.this);

                            List<String> tagList = new ArrayList<>();
                            JSONArray jsonArray1 = json.getJSONArray("tags");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                tagList.add(object.getString("tagName"));
                            }
                            Utilis.saveTagList(tagList);

                            if (RegBusinessTypeSharedPreference.getBusinessType(MyBusinessActivity.this).equalsIgnoreCase("Services")) {
                                List<String> serviceList = new ArrayList<>();
                                JSONArray jsonArray2 = json.getJSONArray("serviceDetails");
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    JSONObject object = jsonArray2.getJSONObject(i);
                                    serviceList.add(object.getString("serviceName"));
                                }
                                Utilis.saveServiceList(serviceList);
                            }

                            setViews();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MyBusinessActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(MyBusinessActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(MyBusinessActivity.this, MyBusinessActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", obj.getId());
                    params.put("type", RegBusinessTypeSharedPreference.getBusinessType(MyBusinessActivity.this));
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, MyBusinessActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViews() {
        final EditText etBusinessName = findViewById(R.id.et_business_name);
        final EditText etDesc = findViewById(R.id.et_desc);

        spinBusinessType = findViewById(R.id.spin_business_type);
        spinBusinessType.setTitle("Select Business Type");
        spinBusinessType.setEnabled(false);
        spinBusinessType.setClickable(false);

        spinCategory = findViewById(R.id.spin_category);
        spinCategory.setTitle("Select Category");
        spinCategory.setEnabled(false);
        spinCategory.setClickable(false);

        spinSubCategory = findViewById(R.id.spin_sub_category);
        spinSubCategory.setTitle("Select Subcategory");
        spinSubCategory.setEnabled(false);
        spinSubCategory.setClickable(false);

        ivLogo = findViewById(R.id.iv_logo);

        BasicDetailsData prefBasicDetail = Utilis.getBasicDetails(MyBusinessActivity.this);
        etBusinessName.setText(prefBasicDetail.getBusinessName());
        etDesc.setText(prefBasicDetail.getDesc());

        Glide.with(MyBusinessActivity.this).load(prefBasicDetail.getLogo())
                .placeholder(R.mipmap.ic_launcher_round)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivLogo);

        businessTypeSpinnerValue.add(prefBasicDetail.getBusinessType());
        ArrayAdapter arrayAdapter = new ArrayAdapter(MyBusinessActivity.this, R.layout.spinner_item, businessTypeSpinnerValue);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinBusinessType.setAdapter(arrayAdapter);
        spinBusinessType.setSelection(0);

        categorySpinnerValue.add(prefBasicDetail.getCategoryId());
        ArrayAdapter arrayAdapter1 = new ArrayAdapter(MyBusinessActivity.this, R.layout.spinner_item, categorySpinnerValue);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinCategory.setAdapter(arrayAdapter1);
        spinCategory.setSelection(0);

        subCategorySpinnerValue.add(prefBasicDetail.getSubCategoryId());
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(MyBusinessActivity.this, R.layout.spinner_item, subCategorySpinnerValue);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinSubCategory.setAdapter(arrayAdapter2);
        spinSubCategory.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(MyBusinessActivity.this, ManageBusinessActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}