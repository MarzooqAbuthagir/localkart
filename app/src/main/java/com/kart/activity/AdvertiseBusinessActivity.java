package com.kart.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.kart.R;
import com.kart.model.BasicDetailsData;
import com.kart.model.BusinessTypeData;
import com.kart.model.CategoryData;
import com.kart.model.SubCategoryData;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertiseBusinessActivity extends AppCompatActivity {
    private String Tag = "AdvertiseBusinessActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    SearchableSpinner spinBusinessType;
    private ArrayList<BusinessTypeData> businessTypeDataListValue = new ArrayList<BusinessTypeData>();
    private List<String> businessTypeSpinnerValue = new ArrayList<>();
    String strBusinessId = "", strBusiness = "";

    SearchableSpinner spinCategory;
    private ArrayList<CategoryData> categoryListValue = new ArrayList<CategoryData>();
    private List<String> categorySpinnerValue = new ArrayList<>();
    String strCategoryId = "";

    SearchableSpinner spinSubCategory;
    private ArrayList<SubCategoryData> subCategoryListValue = new ArrayList<SubCategoryData>();
    private List<String> subCategorySpinnerValue = new ArrayList<>();
    String strSubCategoryId = "";

    String str_result = "", str_message = "", strBusinessName = "", strDesc = "";

    int SELECT_FILE = 102;
    String base64img = "";
    ImageView ivLogo;

    EditText etDesc;
    private static final int CROP_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_business);

        utilis = new Utilis(AdvertiseBusinessActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AdvertiseBusinessActivity.this, R.color.colorPrimaryDark));

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

        View progressView = findViewById(R.id.progress_view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200, 10);
        progressView.setLayoutParams(lp);

        spinBusinessType = findViewById(R.id.spin_business_type);
        spinBusinessType.setTitle("Select Business Type");

        spinCategory = findViewById(R.id.spin_category);
        spinCategory.setTitle("Select Category");

        spinSubCategory = findViewById(R.id.spin_sub_category);
        spinSubCategory.setTitle("Select Subcategory");

        etDesc = findViewById(R.id.et_desc);

        setBusinessTypeData();

        spinBusinessType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strBusinessId = businessTypeDataListValue.get(i).getBusinessId();
                    strBusiness = businessTypeDataListValue.get(i).getBusinessName();
                    spinCategory.setEnabled(true);
                    spinSubCategory.setEnabled(false);
                    if (strBusinessId.equalsIgnoreCase("1")) {
                        getCategoryData(Utilis.shopcategories);
                    } else {
                        getCategoryData(Utilis.servicecategories);
                    }
                } else {
                    spinCategory.setEnabled(false);
                    spinSubCategory.setEnabled(false);
                    strBusinessId = "";
                    strBusiness = "";
                    strCategoryId = "";
                    strSubCategoryId = "";
                    setCategoryData();
                    setSubCategoryData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strCategoryId = categoryListValue.get(i).getCategoryId();
                    spinSubCategory.setEnabled(true);
                    if (strBusinessId.equalsIgnoreCase("1")) {
                        getSubCategoryData(Utilis.shopsubcat);
                    } else {
                        getSubCategoryData(Utilis.servicesubcat);
                    }
                } else {
                    strCategoryId = "";
                    strSubCategoryId = "";
                    spinSubCategory.setEnabled(false);
                    setSubCategoryData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strSubCategoryId = subCategoryListValue.get(i).getSubCategoryId();
                } else {
                    strSubCategoryId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final EditText etBusinessName = findViewById(R.id.et_business_name);

        ivLogo = findViewById(R.id.iv_logo);

        Button btnSelectLogo = findViewById(R.id.btn_logo);
        btnSelectLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = Utilis.checkPermission(AdvertiseBusinessActivity.this);
                if (result)
                    galleryIntent();
            }
        });

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strBusinessName = etBusinessName.getText().toString().trim();
                strDesc = etDesc.getText().toString().trim();
                int descCount = strDesc.isEmpty() ? 0 : strDesc.split("\\s+").length;
                if (strBusinessId.equalsIgnoreCase("")) {
                    Toast.makeText(AdvertiseBusinessActivity.this, "Select business type", Toast.LENGTH_SHORT).show();
                } else if (strBusinessName.isEmpty()) {
                    etBusinessName.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity.this, "Enter business name", Toast.LENGTH_SHORT).show();
                } else if (strBusinessName.length() < 4) {
                    etBusinessName.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity.this, "Business name atleast 4 characters", Toast.LENGTH_SHORT).show();
                } else if (strCategoryId.equalsIgnoreCase("")) {
                    Toast.makeText(AdvertiseBusinessActivity.this, "Select category", Toast.LENGTH_SHORT).show();
                } else if (strSubCategoryId.equalsIgnoreCase("")) {
                    Toast.makeText(AdvertiseBusinessActivity.this, "Select subcategory", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(AdvertiseBusinessActivity.this, "Enter about business", Toast.LENGTH_SHORT).show();
                } else if (descCount > 30) {
                    Toast.makeText(AdvertiseBusinessActivity.this, "About not exceeding 30 words", Toast.LENGTH_SHORT).show();
                } /*else if (base64img.equalsIgnoreCase("")) {
                    Toast.makeText(AdvertiseBusinessActivity.this, "Select logo", Toast.LENGTH_SHORT).show();
                }*/ else {
                    BasicDetailsData basicDetailsData = new BasicDetailsData(
                            strBusiness,
                            strBusinessName,
                            strCategoryId,
                            strSubCategoryId,
                            base64img,
                            strDesc
                    );
                    Utilis.saveBasicDetails(basicDetailsData);
                    Intent intent = new Intent(AdvertiseBusinessActivity.this, AdvertiseBusinessActivity2.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private void galleryIntent() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

        Intent GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), SELECT_FILE);
    }

    private void getSubCategoryData(String apiName) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(AdvertiseBusinessActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + apiName, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getSubCategoryData response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getSubCategoryData result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            subCategoryListValue.clear();

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                SubCategoryData subCategoryData = new SubCategoryData(
                                        jsonObject.getString("subCategoryName"),
                                        jsonObject.getString("Image"),
                                        jsonObject.getString("Id"));

                                subCategoryListValue.add(subCategoryData);
                            }

                            SubCategoryData subCateData = new SubCategoryData("Subcategory", "-1", "");
                            subCategoryListValue.add(0, subCateData);

                            subCategorySpinnerValue.clear();
                            for (int i = 0; i < subCategoryListValue.size(); i++) {
                                subCategorySpinnerValue.add(subCategoryListValue.get(i).getSubCategoryName());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity.this, R.layout.spinner_item, subCategorySpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinSubCategory.setAdapter(arrayAdapter);
                            spinSubCategory.setSelection(0);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(AdvertiseBusinessActivity.this, AdvertiseBusinessActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("Id", strCategoryId);
                    System.out.println(Tag + " getSubCategoryData inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, AdvertiseBusinessActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setSubCategoryData() {
        subCategoryListValue.clear();
        SubCategoryData subCateData = new SubCategoryData("Subcategory", "-1", "");
        subCategoryListValue.add(0, subCateData);

        subCategorySpinnerValue.clear();
        for (int i = 0; i < subCategoryListValue.size(); i++) {
            subCategorySpinnerValue.add(subCategoryListValue.get(i).getSubCategoryName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity.this, R.layout.spinner_item, subCategorySpinnerValue);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinSubCategory.setAdapter(arrayAdapter);
        spinSubCategory.setSelection(0);
    }

    private void setCategoryData() {
        categoryListValue.clear();
        CategoryData cateData = new CategoryData("Category", "-1", "");
        categoryListValue.add(0, cateData);

        categorySpinnerValue.clear();
        for (int i = 0; i < categoryListValue.size(); i++) {
            categorySpinnerValue.add(categoryListValue.get(i).getCategoryName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity.this, R.layout.spinner_item, categorySpinnerValue);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinCategory.setAdapter(arrayAdapter);
        spinCategory.setSelection(0);
    }

    private void getCategoryData(String apiName) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(AdvertiseBusinessActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utilis.Api + apiName, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getCategoryData response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getCategoryData result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            categoryListValue.clear();

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                CategoryData categoryData = new CategoryData(
                                        jsonObject.getString("Category"),
                                        jsonObject.getString("Id"),
                                        jsonObject.getString("Image"));

                                categoryListValue.add(categoryData);
                            }

                            CategoryData cateData = new CategoryData("Category", "-1", "");
                            categoryListValue.add(0, cateData);

                            categorySpinnerValue.clear();
                            for (int i = 0; i < categoryListValue.size(); i++) {
                                categorySpinnerValue.add(categoryListValue.get(i).getCategoryName());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity.this, R.layout.spinner_item, categorySpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinCategory.setAdapter(arrayAdapter);
                            spinCategory.setSelection(0);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(AdvertiseBusinessActivity.this, AdvertiseBusinessActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    return new HashMap<>();
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, AdvertiseBusinessActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setBusinessTypeData() {
        businessTypeDataListValue.clear();
        BusinessTypeData businessTypeData = new BusinessTypeData();
        businessTypeData.setBusinessId("-1");
        businessTypeData.setBusinessName("Business Type");
        businessTypeDataListValue.add(businessTypeData);

        BusinessTypeData businessTypeData2 = new BusinessTypeData();
        businessTypeData2.setBusinessId("1");
        businessTypeData2.setBusinessName("Shopping");
        businessTypeDataListValue.add(businessTypeData2);

        BusinessTypeData businessTypeData3 = new BusinessTypeData();
        businessTypeData3.setBusinessId("2");
        businessTypeData3.setBusinessName("Services");
        businessTypeDataListValue.add(businessTypeData3);

        businessTypeSpinnerValue.clear();

        for (int i = 0; i < businessTypeDataListValue.size(); i++) {
            businessTypeSpinnerValue.add(businessTypeDataListValue.get(i).getBusinessName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(AdvertiseBusinessActivity.this, R.layout.spinner_item, businessTypeSpinnerValue);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinBusinessType.setAdapter(arrayAdapter);
        spinBusinessType.setSelection(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Utilis.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(AdvertiseBusinessActivity.this, "Grant Permission to update profile image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
//                onSelectFromGalleryResult(data);
                Uri uri = data.getData();
                ImageCropFunction(uri);
            } else if (requestCode == CROP_IMG) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bm = null;
                    bm = bundle.getParcelable("data");

//                    ivLogo.setImageBitmap(bm);
                    Glide.with(AdvertiseBusinessActivity.this).asBitmap().load(bm)
                            .placeholder(R.drawable.placeholder_profile)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivLogo);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    assert bm != null;
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    System.out.println("Gallery image " + base64img);
                }
            }
        }
    }

    private void ImageCropFunction(Uri uri) {
        try {
            Intent CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 300);
            CropIntent.putExtra("outputY", 300); //180
            CropIntent.putExtra("aspectX", 1); //3
            CropIntent.putExtra("aspectY", 1); //4
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, CROP_IMG);
        } catch (ActivityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        ivLogo.setImageBitmap(bm);
        Glide.with(AdvertiseBusinessActivity.this).asBitmap().load(bm)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivLogo);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("Gallery image " + base64img);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(AdvertiseBusinessActivity.this, MainActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}