package com.kart.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.kart.R;
import com.kart.adapter.AddServiceAdapter;
import com.kart.adapter.ImageAdapter;
import com.kart.model.AddressDetailsData;
import com.kart.model.BasicDetailsData;
import com.kart.model.ContactDetailsData;
import com.kart.model.LocationData;
import com.kart.model.UploadImages;
import com.kart.model.UserDetail;
import com.kart.support.MyGridView;
import com.kart.support.RegBusinessIdSharedPreference;
import com.kart.support.RegBusinessSharedPrefrence;
import com.kart.support.RegBusinessTypeSharedPreference;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;
import com.theartofdev.edmodo.cropper.CropImage;

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

public class AdvertiseBusinessActivity5 extends AppCompatActivity {
    private String Tag = "AdvertiseBusinessActivity5";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strBusinessId = "";
    BasicDetailsData basicDetailsData;
    AddressDetailsData addressDetailsData;
    ContactDetailsData contactDetailsData;
    LocationData locationData;

    private EditText etKeyword;
    private ChipGroup chipGroup;

    private MyGridView gridView;

    private List<UploadImages> arrayList;
    //    NestedScrollView nestedScrollView;
    LinearLayout layGrid;
    String strChip;

    UserDetail obj;
    static SharedPreferences mPrefs;

    File photoFile = null;
    String mCurrentPhotoPath;
    Uri photoURI;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    int CAPTURE_IMAGE_REQUEST = 1;
    int SELECT_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_business5);

        utilis = new Utilis(AdvertiseBusinessActivity5.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strBusinessId = intent.getStringExtra("businessType");
        basicDetailsData = Utilis.getBasicDetails(AdvertiseBusinessActivity5.this);
        addressDetailsData = Utilis.getAddressDetails(AdvertiseBusinessActivity5.this);
        contactDetailsData = Utilis.getContactDetails(AdvertiseBusinessActivity5.this);
        locationData = Utilis.getLocDetails(AdvertiseBusinessActivity5.this);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AdvertiseBusinessActivity5.this, R.color.colorPrimaryDark));

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
        if (strBusinessId.equalsIgnoreCase("1")) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 10);
            progressView.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(900, 10);
            progressView.setLayoutParams(lp);
        }

        etKeyword = findViewById(R.id.et_keyword);
        Button btnAddTag = findViewById(R.id.btn_add_tags);
        chipGroup = findViewById(R.id.chipGroup);

        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewChip();
            }
        });

//        nestedScrollView = findViewById(R.id.nested_scroll_view);
        layGrid = findViewById(R.id.lay_grid);
        arrayList = new ArrayList<>();
        arrayList = Utilis.getImageList("imageList");
        if (arrayList != null) {
            System.out.println("array list contains images");
        } else {
            arrayList = new ArrayList<>();
        }
        if (arrayList.size() > 0) {
//            nestedScrollView.setVisibility(View.VISIBLE);
            layGrid.setVisibility(View.VISIBLE);
        } else {
//            nestedScrollView.setVisibility(View.GONE);
            layGrid.setVisibility(View.GONE);
        }
        gridView = findViewById(R.id.grid_image);
        final ImageAdapter adapter = new ImageAdapter(AdvertiseBusinessActivity5.this, arrayList, 0);
        gridView.setAdapter(adapter);

        Button btnUploadPhoto = findViewById(R.id.btn_upload_photo);
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.size() < 2) {
                    if (checkAndRequestPermissions(AdvertiseBusinessActivity5.this)) {
                        chooseImage(AdvertiseBusinessActivity5.this);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdvertiseBusinessActivity5.this);
                    builder.setMessage("Maximum Photo Limit (2) Reached. You can upload more photos in your profile section after successful registration.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnNext = findViewById(R.id.btn_next);
        if (strBusinessId.equalsIgnoreCase("1")) {
            btnNext.setText("Register");
        } else {
            btnNext.setText("Next");
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = chipGroup.getChildCount();

                strChip = "";
                for (int i = 0; i < count; i++) {
                    Chip child = (Chip) chipGroup.getChildAt(i);

                    if (!child.isChecked()) {
                        continue;
                    }

                    if (strChip.isEmpty()) {
                        strChip = child.getText().toString();
                    } else {
                        strChip += ", " + child.getText().toString();
                    }
                }

                if (strChip.isEmpty()) {
                    etKeyword.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity5.this, "Enter keyword", Toast.LENGTH_SHORT).show();
                } else if (arrayList.size() == 0) {
                    Toast.makeText(AdvertiseBusinessActivity5.this, "Select photo", Toast.LENGTH_SHORT).show();
                } else {
                    if (strBusinessId.equalsIgnoreCase("1")) {
                        registerBusiness();
                    } else {
                        Utilis.saveChip(strChip);
                        Utilis.clearImageList(AdvertiseBusinessActivity5.this);
                        Utilis.setImageList("imageList", arrayList, AdvertiseBusinessActivity5.this);
                        Intent intent = new Intent(AdvertiseBusinessActivity5.this, AdvertiseBusinessActivity6.class);
                        intent.putExtra("key", keyIntent);
                        intent.putExtra("businessType", strBusinessId);
                        intent.putExtra("chips", strChip);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }
        });

        setChips();
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void setChips() {
        String prefChips = Utilis.getChips(AdvertiseBusinessActivity5.this);

        if (prefChips.isEmpty()) {
            System.out.println("chips are empty");
        } else {
            String[] arrPrefChips = prefChips.split(",");

            for (String arrPrefChip : arrPrefChips) {

                LayoutInflater inflater = LayoutInflater.from(AdvertiseBusinessActivity5.this);

                // Create a Chip from Layout.
                Chip newChip = (Chip) inflater.inflate(R.layout.chip_entry, chipGroup, false);
                newChip.setText(arrPrefChip);

                chipGroup.addView(newChip);
                chipGroup.setVisibility(View.VISIBLE);

                newChip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleChipCloseIconClicked((Chip) v);
                    }
                });
            }
        }
    }

    String str_result = "", str_message = "";

    private void registerBusiness() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(AdvertiseBusinessActivity5.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.businesssave, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " registerBusiness response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " registerBusiness result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");
                            String str_index_id = obj.getString("indexId");
                            uploadImages(str_index_id);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity5.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(AdvertiseBusinessActivity5.this, AdvertiseBusinessActivity5.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("latitude", locationData.getLatitude());
                    params.put("longitude", locationData.getLongitude());
                    params.put("address", locationData.getAddress());
                    params.put("tags", strChip);
                    System.out.println(Tag + " registerBusiness inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, AdvertiseBusinessActivity5.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    ProgressDialog progressDialog;

    private void uploadImages(final String str_index_id) {
        progressDialog = ProgressDialog.show(AdvertiseBusinessActivity5.this, "Uploading Images",
                "Please wait...", true);

        for (int i = 0; i < arrayList.size(); i++) {

            final int currentPos = i;
            StringRequest request = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.uploadimage, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {

                    if (currentPos + 1 == arrayList.size()) {
                        System.out.println("on upload " + currentPos + " array size " + arrayList.size());
                        progressDialog.dismiss();

                        RegBusinessSharedPrefrence.setMenuFlag(AdvertiseBusinessActivity5.this, "1");

                        RegBusinessTypeSharedPreference.setBusinessType(AdvertiseBusinessActivity5.this, "Shopping");
                        RegBusinessIdSharedPreference.setBusinessId(AdvertiseBusinessActivity5.this, str_index_id);

                        Utilis.clearRegPref(AdvertiseBusinessActivity5.this);

                        Intent intent = new Intent(AdvertiseBusinessActivity5.this, MainActivity.class);
                        intent.putExtra("key", keyIntent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Toast.makeText(AdvertiseBusinessActivity5.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("Image", arrayList.get(currentPos).getImage());
                    parameters.put("type", basicDetailsData.getBusinessType());
                    parameters.put("indexId", str_index_id);
                    System.out.println(Tag + " ImageUploadTask inputs " + parameters);
                    return parameters;
                }
            };

            RequestQueue rQueue = Volley.newRequestQueue(AdvertiseBusinessActivity5.this);
            rQueue.add(request);

        }
    }

    private void addNewChip() {
        String keyword = etKeyword.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(AdvertiseBusinessActivity5.this, "Enter keyword", Toast.LENGTH_LONG).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(AdvertiseBusinessActivity5.this);

        // Create a Chip from Layout.
        Chip newChip = (Chip) inflater.inflate(R.layout.chip_entry, chipGroup, false);
        newChip.setText(keyword);


        chipGroup.addView(newChip);
        chipGroup.setVisibility(View.VISIBLE);

        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipCloseIconClicked((Chip) v);
            }
        });

        etKeyword.setText("");
    }

    private void handleChipCloseIconClicked(Chip chip) {
        ChipGroup parent = (ChipGroup) chip.getParent();
        parent.removeView(chip);

        int count = chipGroup.getChildCount();
        if (count > 0) {
            chipGroup.setVisibility(View.VISIBLE);
        } else {
            chipGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(AdvertiseBusinessActivity5.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Camera.", Toast.LENGTH_SHORT)
                        .show();
            } else if (ContextCompat.checkSelfPermission(AdvertiseBusinessActivity5.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Your Storage.",
                        Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(AdvertiseBusinessActivity5.this);
            }
        }
    }

    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Cancel"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Take Photo")) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        try {
                            photoFile = createImageFile();

                            photoURI = FileProvider.getUriForFile(
                                    AdvertiseBusinessActivity5.this,
                                    "com.kart.fileprovider",
                                    photoFile
                            );
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);

                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
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
            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(AdvertiseBusinessActivity5.this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            onSelectFromGalleryResult(resultUri);
        } else if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(AdvertiseBusinessActivity5.this);
        }
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

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("Gallery image " + base64img);

        UploadImages uploadImages = new UploadImages(
                base64img,
                bm
        );
        arrayList.add(0, uploadImages);

        final ImageAdapter adapter = new ImageAdapter(AdvertiseBusinessActivity5.this, arrayList, 0);
        gridView.setAdapter(adapter);

        if (arrayList.size() > 0) {
//                        nestedScrollView.setVisibility(View.VISIBLE);
            layGrid.setVisibility(View.VISIBLE);
        } else {
//                        nestedScrollView.setVisibility(View.GONE);
            layGrid.setVisibility(View.GONE);
        }

        adapter.setOnItemClickListener(new AddServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                arrayList.remove(position);
                adapter.notifyDataSetChanged();
                if (arrayList.size() > 0) {
//                                nestedScrollView.setVisibility(View.VISIBLE);
                    layGrid.setVisibility(View.VISIBLE);
                } else {
//                                nestedScrollView.setVisibility(View.GONE);
                    layGrid.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        int count = chipGroup.getChildCount();

        strChip = "";
        for (int i = 0; i < count; i++) {
            Chip child = (Chip) chipGroup.getChildAt(i);

            if (!child.isChecked()) {
                continue;
            }

            if (strChip.isEmpty()) {
                strChip = child.getText().toString();
            } else {
                strChip += ", " + child.getText().toString();
            }
        }

        Utilis.saveChip(strChip);
        Utilis.clearImageList(AdvertiseBusinessActivity5.this);
        Utilis.setImageList("imageList", arrayList, AdvertiseBusinessActivity5.this);
        finish();
    }
}