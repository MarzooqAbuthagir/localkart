package com.kart.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;

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
import com.kart.support.RegBusinessSharedPrefrence;
import com.kart.support.RegBusinessTypeSharedPreference;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    int PICK_IMAGE_MULTIPLE = 101;
    private List<UploadImages> arrayList;
    //    NestedScrollView nestedScrollView;
    LinearLayout layGrid;
    String strChip;

    UserDetail obj;
    static SharedPreferences mPrefs;

    private static final int CROP_IMG = 1;

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
                    boolean result = Utilis.checkPermission(AdvertiseBusinessActivity5.this);
                    if (result)
                        galleryIntent();
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

    private void galleryIntent() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_MULTIPLE);

        Intent GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Utilis.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(AdvertiseBusinessActivity5.this, "Grant Permission to update profile image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_MULTIPLE) {

//            if (data.getClipData() != null) {
//
//                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
//
//                    try {
//                        InputStream is = getContentResolver().openInputStream(imageUri);
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                        assert bitmap != null;
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
//                        byte[] byteArray = byteArrayOutputStream.toByteArray();
//                        String base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//                        UploadImages uploadImages = new UploadImages(
//                                base64img,
//                                bitmap
//                        );
//                        arrayList.add(0, uploadImages);
//
//                        final ImageAdapter adapter = new ImageAdapter(AdvertiseBusinessActivity5.this, arrayList, 0);
//                        gridView.setAdapter(adapter);
//
//                        if (arrayList.size() > 0) {
////                            nestedScrollView.setVisibility(View.VISIBLE);
//                            layGrid.setVisibility(View.VISIBLE);
//                        } else {
////                            nestedScrollView.setVisibility(View.GONE);
//                            layGrid.setVisibility(View.GONE);
//                        }
//
//                        adapter.setOnItemClickListener(new AddServiceAdapter.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(View view, int position) {
//                                arrayList.remove(position);
//                                adapter.notifyDataSetChanged();
//                                if (arrayList.size() > 0) {
////                                    nestedScrollView.setVisibility(View.VISIBLE);
//                                    layGrid.setVisibility(View.VISIBLE);
//                                } else {
////                                    nestedScrollView.setVisibility(View.GONE);
//                                    layGrid.setVisibility(View.GONE);
//                                }
//                            }
//                        });
//
//
//                    } catch (Exception e) {
//                        Log.e(Tag, "File select error", e);
//
//                    }
//
//                }
//
//            } else {
                Uri imageUri = data.getData();
                ImageCropFunction(imageUri);

//            try {
//                InputStream is = getContentResolver().openInputStream(imageUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                assert bitmap != null;
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream.toByteArray();
//                String base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//                UploadImages uploadImages = new UploadImages(
//                        base64img,
//                        bitmap
//                );
//                arrayList.add(0, uploadImages);
//
//                final ImageAdapter adapter = new ImageAdapter(AdvertiseBusinessActivity5.this, arrayList, 0);
//                gridView.setAdapter(adapter);
//
//                if (arrayList.size() > 0) {
////                        nestedScrollView.setVisibility(View.VISIBLE);
//                    layGrid.setVisibility(View.VISIBLE);
//                } else {
////                        nestedScrollView.setVisibility(View.GONE);
//                    layGrid.setVisibility(View.GONE);
//                }
//
//                adapter.setOnItemClickListener(new AddServiceAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        arrayList.remove(position);
//                        adapter.notifyDataSetChanged();
//                        if (arrayList.size() > 0) {
////                                nestedScrollView.setVisibility(View.VISIBLE);
//                            layGrid.setVisibility(View.VISIBLE);
//                        } else {
////                                nestedScrollView.setVisibility(View.GONE);
//                            layGrid.setVisibility(View.GONE);
//                        }
//                    }
//                });
//
//
//            } catch (Exception e) {
//                Log.e(Tag, "File select error", e);
//
//            }
//            }

            } else if (requestCode == CROP_IMG) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = null;
                    bitmap = bundle.getParcelable("data");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    UploadImages uploadImages = new UploadImages(
                            base64img,
                            bitmap
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
            }
        } else {
            Toast.makeText(this, "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ImageCropFunction(Uri imageUri) {
        try {
            Intent CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(imageUri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 400);
            CropIntent.putExtra("outputY", 600); //180
            CropIntent.putExtra("aspectX", 16); //3
            CropIntent.putExtra("aspectY", 9); //4
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, CROP_IMG);
        } catch (ActivityNotFoundException e) {
            System.out.println(e.getMessage());
        }
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