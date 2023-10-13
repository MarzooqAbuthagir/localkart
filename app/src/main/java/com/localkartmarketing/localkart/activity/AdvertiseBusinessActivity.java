package com.localkartmarketing.localkart.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.localkartmarketing.localkart.model.BasicDetailsData;
import com.localkartmarketing.localkart.model.BusinessTypeData;
import com.localkartmarketing.localkart.model.CategoryData;
import com.localkartmarketing.localkart.model.SubCategoryData;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdvertiseBusinessActivity extends AppCompatActivity {
    private String Tag = "AdvertiseBusinessActivity";
    Utils utils;
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
    String strCategoryId = "", strCategory ="";

    SearchableSpinner spinSubCategory;
    private ArrayList<SubCategoryData> subCategoryListValue = new ArrayList<SubCategoryData>();
    private List<String> subCategorySpinnerValue = new ArrayList<>();
    String strSubCategoryId = "", strSubCategory = "";

    String str_result = "", str_message = "", strBusinessName = "", strDesc = "";

    String base64img = "";
    ImageView ivLogo;

    EditText etDesc;

    File photoFile = null;
    String mCurrentPhotoPath;
    Uri photoURI;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    int CAPTURE_IMAGE_REQUEST = 1;
    int SELECT_IMAGE_REQUEST = 2;

    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_business);

        utils = new Utils(AdvertiseBusinessActivity.this);

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
                        getCategoryData(Utils.shopcategories);
                    } else {
                        getCategoryData(Utils.servicecategories);
                    }
                } else {
                    spinCategory.setEnabled(false);
                    spinSubCategory.setEnabled(false);
                    strBusinessId = "";
                    strBusiness = "";
                    strCategoryId = "";
                    strCategory = "";
                    strSubCategoryId = "";
                    strSubCategory = "";
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
                    strCategory = categoryListValue.get(i).getCategoryName();
                    spinSubCategory.setEnabled(true);
                    if (strBusinessId.equalsIgnoreCase("1")) {
                        getSubCategoryData(Utils.shopsubcat);
                    } else {
                        getSubCategoryData(Utils.servicesubcat);
                    }
                } else {
                    strCategoryId = "";
                    strCategory = "";
                    strSubCategoryId = "";
                    strSubCategory = "";
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
                    strSubCategory = subCategoryListValue.get(i).getSubCategoryName();
                } else {
                    strSubCategoryId = "";
                    strSubCategory = "";
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
                if (checkAndRequestPermissions(AdvertiseBusinessActivity.this)) {
                    chooseImage(AdvertiseBusinessActivity.this);
                }
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
                            strDesc,
                            "0",
                            strCategory,
                            strSubCategory,
                            ""
                    );
                    Utils.saveBasicDetails(basicDetailsData);
                    Intent intent = new Intent(AdvertiseBusinessActivity.this, AdvertiseBusinessActivity2.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Utils.getAndroidOs());
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Utils.getAndroidOs());
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void getSubCategoryData(String apiName) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(AdvertiseBusinessActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + apiName, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getSubCategoryData response - " + response);

                        Utils.dismissProgress();

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

                    Utils.dismissProgress();
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
                protected Map<String, String> getParams() {
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
        if (Utils.isInternetOn()) {
            Utils.showProgress(AdvertiseBusinessActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + apiName, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getCategoryData response - " + response);

                        Utils.dismissProgress();

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

                    Utils.dismissProgress();
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
                protected Map<String, String> getParams() {
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
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(AdvertiseBusinessActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Camera.", Toast.LENGTH_SHORT)
                        .show();
            } else if (ContextCompat.checkSelfPermission(AdvertiseBusinessActivity.this,
                    Utils.getAndroidOs()) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Your Storage.",
                        Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(AdvertiseBusinessActivity.this);
            }
        }
    }

    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Cancel"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Take Photo")) {

//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                        try {
//                            photoFile = createImageFile();
//
//                            photoURI = FileProvider.getUriForFile(
//                                    AdvertiseBusinessActivity.this,
//                                    "com.localkartmarketing.localkart.fileprovider",
//                                    photoFile
//                            );
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
//
//                        } catch (IOException ex) {
//                            // Error occurred while creating the File
//                        }
//                    }

                    fileName = System.currentTimeMillis() + ".jpg";
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }

                } else if (optionsMenu[i].equals("Choose from Gallery")) {
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, SELECT_IMAGE_REQUEST);
                } else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(AdvertiseBusinessActivity.this, getPackageName() + ".provider", image);
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(1, 1).setRequestedSize(300, 300).start(AdvertiseBusinessActivity.this);
            cropImage(getCacheImagePath(fileName));
//        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            Uri resultUri = result.getUri();
            Uri resultUri = UCrop.getOutput(data);
            onSelectFromGalleryResult(resultUri);
        } else if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(1, 1).setRequestedSize(300, 300).start(AdvertiseBusinessActivity.this);
            cropImage(photoURI);
        }
    }

    private void cropImage(Uri sourceUri) {
        int IMAGE_COMPRESSION = 100;
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        options.setHideBottomControls(true);
        options.withAspectRatio(1, 1);

        options.setToolbarTitle("Crop Photo");
        options.setToolbarColor(ContextCompat.getColor(this, R.color.adv_bus_color));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.adv_bus_color));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.adv_bus_color));

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void onSelectFromGalleryResult(Uri data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        ivLogo.setImageBitmap(bm);
        Glide.with(AdvertiseBusinessActivity.this).asBitmap().load(bm)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivLogo);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
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