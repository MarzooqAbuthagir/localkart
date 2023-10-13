package com.localkartmarketing.localkart.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.AddressDetailsData;
import com.localkartmarketing.localkart.model.BasicDetailsData;
import com.localkartmarketing.localkart.model.ContactDetailsData;
import com.localkartmarketing.localkart.model.LocationData;
import com.localkartmarketing.localkart.model.UploadImages;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.RegBusinessTypeSharedPreference;
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

public class MyBusinessActivity extends AppCompatActivity {
    private String Tag = "MyBusinessActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    UserDetail obj;
    static SharedPreferences mPrefs;

    SearchableSpinner spinBusinessType;
    SearchableSpinner spinCategory;
    SearchableSpinner spinSubCategory;

    private final List<String> businessTypeSpinnerValue = new ArrayList<>();
    private final List<String> categorySpinnerValue = new ArrayList<>();
    private final List<String> subCategorySpinnerValue = new ArrayList<>();

    ImageView ivLogo;
    String base64img = "";

    File photoFile = null;
    String mCurrentPhotoPath;
    Uri photoURI;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    int CAPTURE_IMAGE_REQUEST = 1;
    int SELECT_IMAGE_REQUEST = 2;

    EditText etDesc;
    String strDesc = "", strBusinessId = "";

    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business);

        utils = new Utils(MyBusinessActivity.this);

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

        getMyBusinessData();

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strDesc = etDesc.getText().toString().trim();
                int descCount = strDesc.isEmpty() ? 0 : strDesc.split("\\s+").length;
                if (strDesc.isEmpty()) {
                    Toast.makeText(MyBusinessActivity.this, "Enter about business", Toast.LENGTH_SHORT).show();
                } else if (descCount > 30) {
                    Toast.makeText(MyBusinessActivity.this, "About not exceeding 30 words", Toast.LENGTH_SHORT).show();
                } else {
                    BasicDetailsData bd = Utils.getBasicDetails(MyBusinessActivity.this);
                    strBusinessId = bd.getBusinessType().equalsIgnoreCase("Shopping") ? "1" : "2";
                    BasicDetailsData basicDetailsData = new BasicDetailsData(
                            bd.getBusinessType(),
                            bd.getBusinessName(),
                            bd.getCategoryId(),
                            bd.getSubCategoryId(),
                            base64img,
                            strDesc,
                            bd.getIndexId(),
                            bd.getCategory(),
                            bd.getSubCategory(),
                            bd.getLogoUrl()
                    );
                    Utils.saveBasicDetails(basicDetailsData);
                    Intent intent = new Intent(MyBusinessActivity.this, MyBusinessActivity2.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        Button btnSelectLogo = findViewById(R.id.btn_logo);
        btnSelectLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions(MyBusinessActivity.this)) {
                    chooseImage(MyBusinessActivity.this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(MyBusinessActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Camera.", Toast.LENGTH_SHORT)
                        .show();
            } else if (ContextCompat.checkSelfPermission(MyBusinessActivity.this,
                    Utils.getAndroidOs()) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Your Storage.",
                        Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(MyBusinessActivity.this);
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
//                                    MyBusinessActivity.this,
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
        return getUriForFile(MyBusinessActivity.this, getPackageName() + ".provider", image);
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
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(1, 1).setRequestedSize(300, 300).start(MyBusinessActivity.this);
            cropImage(getCacheImagePath(fileName));
//        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            Uri resultUri = result.getUri();
            Uri resultUri = UCrop.getOutput(data);
            onSelectFromGalleryResult(resultUri);
        } else if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(1, 1).setRequestedSize(300, 300).start(MyBusinessActivity.this);
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
        Glide.with(MyBusinessActivity.this).asBitmap().load(bm)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivLogo);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("Gallery image " + base64img);

    }

    String str_result = "", str_message = "";

    private void getMyBusinessData() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(MyBusinessActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.editbusiness, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");

                            JSONObject basicDetail = json.getJSONObject("basicDetails");
                            BasicDetailsData basicDetailsData = new BasicDetailsData(
                                    basicDetail.getString("businessType"),
                                    basicDetail.getString("businessName"),
                                    basicDetail.getString("categoryId"),
                                    basicDetail.getString("subCategoryId"),
                                    base64img,
                                    basicDetail.getString("description"),
                                    basicDetail.getString("indexId"),
                                    basicDetail.getString("category"),
                                    basicDetail.getString("subCategory"),
                                    basicDetail.getString("shopLogo")
                            );
                            Utils.saveBasicDetails(basicDetailsData);

                            JSONObject addressDetail = json.getJSONObject("addressDetails");
                            AddressDetailsData addressDetailsData = new AddressDetailsData(
                                    addressDetail.getString("doorNo"),
                                    addressDetail.getString("locality"),
                                    addressDetail.getString("area"),
                                    addressDetail.getString("taulk"),
                                    addressDetail.getString("landMark"),
                                    addressDetail.getString("pincode"),
                                    addressDetail.getString("stateId"),
                                    addressDetail.getString("districtId"),
                                    addressDetail.getString("state"),
                                    addressDetail.getString("district")
                            );
                            Utils.saveAddressDetails(addressDetailsData);

                            JSONObject contactDetail = json.getJSONObject("contactDetails");
                            ContactDetailsData contactDetailsData = new ContactDetailsData(
                                    contactDetail.getString("phoneNumber"),
                                    contactDetail.getString("mobileNumber"),
                                    contactDetail.getString("alternateNumber"),
                                    contactDetail.getString("watsappNumber"),
                                    contactDetail.getString("emailAddress"),
                                    contactDetail.getString("website"),
                                    contactDetail.getString("facebook"),
                                    contactDetail.getString("digitalVcard"),
                                    contactDetail.getString("cod")
                            );
                            Utils.saveContactDetails(contactDetailsData);

                            JSONObject locationData = json.getJSONObject("locationDetails");
                            LocationData locationDetailsData = new LocationData(
                                    locationData.getString("address"),
                                    locationData.getString("latitude"),
                                    locationData.getString("longitude")
                            );
                            Utils.saveLocDetails(locationDetailsData);

                            List<UploadImages> uploadImagesList = new ArrayList<>();
                            JSONArray jsonArray = json.getJSONArray("imageDetails");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                UploadImages uploadImages = new UploadImages();
                                uploadImages.setImage(object.getString("imageUrl"));
                                uploadImages.setImageIndexId(object.getString("imageIndexId"));
                                uploadImagesList.add(uploadImages);
                            }
                            Utils.clearImageList(MyBusinessActivity.this);
                            Utils.setImageList("imageList", uploadImagesList, MyBusinessActivity.this);

                            List<String> tagList = new ArrayList<>();
                            JSONArray jsonArray1 = json.getJSONArray("tags");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                tagList.add(object.getString("tagName"));
                            }
                            String strChip = "";
                            for (int i = 0; i < tagList.size(); i++) {

                                if (strChip.isEmpty()) {
                                    strChip = tagList.get(i);
                                } else {
                                    strChip += ", " + tagList.get(i);
                                }
                            }

                            Utils.saveChip(strChip);

                            if (RegBusinessTypeSharedPreference.getBusinessType(MyBusinessActivity.this).equalsIgnoreCase("Services")) {
                                List<String> serviceList = new ArrayList<>();
                                JSONArray jsonArray2 = json.getJSONArray("serviceDetails");
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    JSONObject object = jsonArray2.getJSONObject(i);
                                    serviceList.add(object.getString("serviceName"));
                                }
                                Utils.saveServiceList(serviceList);
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

                    Utils.dismissProgress();
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
                protected Map<String, String> getParams() {
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
        etDesc = findViewById(R.id.et_desc);

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

        BasicDetailsData prefBasicDetail = Utils.getBasicDetails(MyBusinessActivity.this);
        etBusinessName.setText(prefBasicDetail.getBusinessName());
        etDesc.setText(prefBasicDetail.getDesc());


        if(prefBasicDetail.getLogo().isEmpty()) {
            Glide.with(MyBusinessActivity.this).load(prefBasicDetail.getLogoUrl())
                    .placeholder(R.mipmap.ic_launcher_round)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivLogo);
        } else {
            Glide.with(MyBusinessActivity.this).asBitmap().load(base64img)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivLogo);
        }

        businessTypeSpinnerValue.add(prefBasicDetail.getBusinessType());
        ArrayAdapter arrayAdapter = new ArrayAdapter(MyBusinessActivity.this, R.layout.spinner_item, businessTypeSpinnerValue);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinBusinessType.setAdapter(arrayAdapter);
        spinBusinessType.setSelection(0);

        categorySpinnerValue.add(prefBasicDetail.getCategory());
        ArrayAdapter arrayAdapter1 = new ArrayAdapter(MyBusinessActivity.this, R.layout.spinner_item, categorySpinnerValue);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinCategory.setAdapter(arrayAdapter1);
        spinCategory.setSelection(0);

        subCategorySpinnerValue.add(prefBasicDetail.getSubCategory());
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