package com.localkartmarketing.localkart.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.localkartmarketing.localkart.adapter.AddOfferAdapter;
import com.localkartmarketing.localkart.model.AddOfferData;
import com.localkartmarketing.localkart.model.PostInitialData;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.App;
import com.localkartmarketing.localkart.support.LocationTrack;
import com.localkartmarketing.localkart.support.RegBusinessTypeSharedPreference;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;
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

public class MegaSalesCreatePostActivity extends AppCompatActivity {
    private String Tag = "MegaSalesCreatePostActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", fromDate = "", toDate = "", offerTitle = "", offerId = "", dealCount = "", fDate="", tDate="";

    EditText etFromDate, etToDate, etOfferTitle;

    AddOfferAdapter offerAdapter;
    RecyclerView recyclerView;
    List<AddOfferData> listOfOffer = new ArrayList<>();

    String base64img = "";
    Bitmap bitmap;
    ImageView imageView;

    private SearchableSpinner spinAccessOption;
    String strAccessOption = "", str_message = "", str_result = "";
    private ArrayList<PostInitialData> accessOptionListValue = new ArrayList<PostInitialData>();
    private List<String> accessOptionSpinnerValue = new ArrayList<>();

    UserDetail obj;
    static SharedPreferences mPrefs;

    LocationTrack currentLocation;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CODE = 102;

    File photoFile = null;
    String mCurrentPhotoPath;
    Uri photoURI;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    int CAPTURE_IMAGE_REQUEST = 1;
    int SELECT_IMAGE_REQUEST = 2;

    App app;

    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mega_sales_post);

        utils = new Utils(MegaSalesCreatePostActivity.this);

        app = (App) getApplication();

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        obj = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        offerId = intent.getStringExtra("megaSalesIndexId");
        offerTitle = intent.getStringExtra("megaSalesTitle");
        fromDate = intent.getStringExtra("fromDate");
        toDate = intent.getStringExtra("toDate");
        fDate = intent.getStringExtra("fDate");
        tDate = intent.getStringExtra("tDate");
        dealCount = intent.getStringExtra("dealCount");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MegaSalesCreatePostActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText(offerTitle);

        etOfferTitle = findViewById(R.id.et_offer_title);
        spinAccessOption = findViewById(R.id.spin_access_option);
        spinAccessOption.setTitle("Access Option");
        etFromDate = findViewById(R.id.from_date_txt);
        etToDate = findViewById(R.id.to_date_txt);

        etOfferTitle.setText(offerTitle);
        etFromDate.setText(fDate);
        etToDate.setText(tDate);

        Button btnAddOffer = findViewById(R.id.btn_add_offer);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MegaSalesCreatePostActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                base64img = "";
                if (Integer.parseInt(dealCount) > listOfOffer.size()) {
                    addOfferDialog(view);
                } else {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Deal count exceeds for the post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        offerAdapter = new AddOfferAdapter(this, listOfOffer, 1, "");
        recyclerView.setAdapter(offerAdapter);

        offerAdapter.setOnItemDeleteClickListener(new AddOfferAdapter.OnItemDeleteClickListener() {
            @Override
            public void onItemDeleteClick(View view, int position) {
                listOfOffer.remove(position);
                offerAdapter.notifyDataSetChanged();
            }
        });

        offerAdapter.setOnItemEditClickListener(new AddOfferAdapter.OnItemEditClickListener() {
            @Override
            public void onItemEditClick(View view, int position) {
                AddOfferData offerData = listOfOffer.get(position);
                editOfferDialog(view, offerData, position);
            }
        });

        getApiCall();

        spinAccessOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strAccessOption = accessOptionListValue.get(i).getKey();
                } else {
                    strAccessOption = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (strAccessOption.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                } else if (listOfOffer.size() == 0) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Add Deal / Offer", Toast.LENGTH_SHORT).show();
                } else {
                    fetchLastLocation();
                }
            }
        });

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strAccessOption.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Select access option", Toast.LENGTH_SHORT).show();
                } else if (listOfOffer.size() == 0) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Add Deal / Offer", Toast.LENGTH_SHORT).show();
                } else {
                    sendPost();
                }
            }
        });
    }

    private void sendPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MegaSalesCreatePostActivity.this);

        builder.setMessage("Post details cannot be changed once saved. Are you sure you want to save and show this post to customer?")
                .setPositiveButton(MegaSalesCreatePostActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        if (Utils.isInternetOn()) {
                            Utils.showProgress(MegaSalesCreatePostActivity.this);

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.createmegasalespost, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        //converting response to json object
                                        JSONObject obj = new JSONObject(response);

                                        System.out.println(Tag + " sendPost response - " + response);

                                        Utils.dismissProgress();

                                        str_result = obj.getString("errorCode");
                                        System.out.print(Tag + " sendPost result " + str_result);

                                        if (Integer.parseInt(str_result) == 0) {

                                            str_message = obj.getString("message");
                                            String str_post_index_id = obj.getString("postIndexId");
                                            sendOffer(str_post_index_id);

                                        } else if (Integer.parseInt(str_result) == 2) {
                                            str_message = obj.getString("message");
                                            Toast.makeText(MegaSalesCreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Utils.dismissProgress();
                                    Toast.makeText(MegaSalesCreatePostActivity.this, MegaSalesCreatePostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                                    params.put("megasalesIndexId", offerId);
                                    params.put("fromDate", fromDate);
                                    params.put("toDate", toDate);
                                    params.put("accessOptions", strAccessOption);
                                    params.put("megasalesTitle", offerTitle);
                                    System.out.println(Tag + " sendPost inputs " + params);
                                    return params;
                                }
                            };

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            VolleySingleton.getInstance(MegaSalesCreatePostActivity.this).addToRequestQueue(stringRequest);
                        } else {
                            Toast.makeText(MegaSalesCreatePostActivity.this, MegaSalesCreatePostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(MegaSalesCreatePostActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

        Button btn_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        Button btn_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);

        btn_no.setTextColor(Color.parseColor("#000000"));
        btn_yes.setTextColor(Color.parseColor("#000000"));
    }

    private void sendOffer(final String str_post_index_id) {
        if (Utils.isInternetOn()) {
            Utils.showProgress(MegaSalesCreatePostActivity.this);

            for (int i = 0; i < listOfOffer.size(); i++) {

                final int currentPos = i;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.createmegasalesoffers, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            System.out.println(Tag + " sendOffer response - " + response);

                            str_result = obj.getString("errorCode");
                            System.out.print(Tag + " sendOffer result " + str_result);

                            if (Integer.parseInt(str_result) == 0) {
                                str_message = obj.getString("message");
                                if (currentPos + 1 == listOfOffer.size()) {

                                    Utils.dismissProgress();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MegaSalesCreatePostActivity.this);
                                    builder.setMessage("Your post has been successfully saved.")
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    back();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                                    btnOk.setTextColor(Color.parseColor("#000000"));
                                }

                            } else if (Integer.parseInt(str_result) == 2) {
                                Utils.dismissProgress();
                                str_message = obj.getString("message");
                                Toast.makeText(MegaSalesCreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Utils.dismissProgress();
                        Toast.makeText(MegaSalesCreatePostActivity.this, MegaSalesCreatePostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                        params.put("heading", listOfOffer.get(currentPos).getHeading());
                        params.put("description", listOfOffer.get(currentPos).getDesc());
                        params.put("offerImage", listOfOffer.get(currentPos).getImage());
                        params.put("postIndexId", str_post_index_id);
                        System.out.println(Tag + " sendOffer inputs " + params);
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }

        } else {
            Toast.makeText(this, MegaSalesCreatePostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        if (Utils.isGpsOn()) {
            currentLocation = new LocationTrack(MegaSalesCreatePostActivity.this);

            if (currentLocation.canGetLocation()) {
                longitude = currentLocation.getLongitude();
                latitude = currentLocation.getLatitude();
                System.out.println("latitude " + latitude + " and longitude " + longitude);

                previewPost();
            }

        } else {
            Toast.makeText(this, "Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    private void previewPost() {
        Utils.clearOfferList(MegaSalesCreatePostActivity.this);
        Utils.setOfferList("offerList", listOfOffer, MegaSalesCreatePostActivity.this);
        Intent intent = new Intent(MegaSalesCreatePostActivity.this, MegaSalesViewPostActivity.class);
        intent.putExtra("key", keyIntent);
        intent.putExtra("fDate", etFromDate.getText().toString().trim());
        intent.putExtra("tDate", etToDate.getText().toString().trim());
        intent.putExtra("fromDate", fromDate);
        intent.putExtra("toDate", toDate);
        intent.putExtra("accessOption", strAccessOption);
        intent.putExtra("megaSalesIndexId", offerId);
        intent.putExtra("megaSalesTitle", offerTitle);
        intent.putExtra("dealCount", dealCount);
        intent.putExtra("latitude", String.valueOf(latitude));
        intent.putExtra("longitude", String.valueOf(longitude));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void editOfferDialog(View view, AddOfferData offerData, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MegaSalesCreatePostActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_offer, viewGroup, false);
        final EditText etHeading = dialogView.findViewById(R.id.et_heading);
        final EditText etDesc = dialogView.findViewById(R.id.et_desc);
        final TextView tvTitle = dialogView.findViewById(R.id.tv_offer);
        imageView = dialogView.findViewById(R.id.iv_img);

//        final int size = offerData.getOffCount();
        final int size = position + 1;
        tvTitle.setText("Deal " + size);
        etHeading.setText(offerData.getHeading());
        etDesc.setText(offerData.getDesc());
        imageView.getLayoutParams().height = 500;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setImageBitmap(offerData.getBitmap());
        base64img = offerData.getImage();
        bitmap = offerData.getBitmap();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions(MegaSalesCreatePostActivity.this)) {
                    chooseImage(MegaSalesCreatePostActivity.this);
                }
            }
        });

        final Button btnAdd = dialogView.findViewById(R.id.btn_add);
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
                String strHeading = etHeading.getText().toString().trim();
                String strDesc = etDesc.getText().toString().trim();
                int headingCount = strHeading.isEmpty() ? 0 : strHeading.split("\\s+").length;
                int descCount = strDesc.isEmpty() ? 0 : strDesc.split("\\s+").length;
                if (strHeading.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Enter heading", Toast.LENGTH_SHORT).show();
                } else if (headingCount > 8) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Heading not exceeding 8 words", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 20) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Description not exceeding 20 words", Toast.LENGTH_SHORT).show();
                } else if (base64img.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Select image", Toast.LENGTH_SHORT).show();
                } else {
                    listOfOffer.remove(position);
                    AddOfferData offerData = new AddOfferData(size, strHeading, strDesc, base64img, bitmap);
                    listOfOffer.add(position, offerData);
                    offerAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void addOfferDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MegaSalesCreatePostActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_add_offer, viewGroup, false);
        final EditText etHeading = dialogView.findViewById(R.id.et_heading);
        final EditText etDesc = dialogView.findViewById(R.id.et_desc);
        final TextView tvTitle = dialogView.findViewById(R.id.tv_offer);
        imageView = dialogView.findViewById(R.id.iv_img);

        if (listOfOffer.isEmpty()) {
            tvTitle.setText("Deal 1");
        } else {
            int size = listOfOffer.size() + 1;
            tvTitle.setText("Deal " + size);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions(MegaSalesCreatePostActivity.this)) {
                    chooseImage(MegaSalesCreatePostActivity.this);
                }
            }
        });

        final Button btnAdd = dialogView.findViewById(R.id.btn_add);
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
                String strHeading = etHeading.getText().toString().trim();
                String strDesc = etDesc.getText().toString().trim();
                int headingCount = strHeading.isEmpty() ? 0 : strHeading.split("\\s+").length;
                int descCount = strDesc.isEmpty() ? 0 : strDesc.split("\\s+").length;

                if (strHeading.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Enter heading", Toast.LENGTH_SHORT).show();
                } else if (headingCount > 8) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Heading not exceeding 8 words", Toast.LENGTH_SHORT).show();
                } else if (strDesc.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                } else if (descCount > 20) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Description not exceeding 20 words", Toast.LENGTH_SHORT).show();
                } else if (base64img.isEmpty()) {
                    Toast.makeText(MegaSalesCreatePostActivity.this, "Select image", Toast.LENGTH_SHORT).show();
                } else {
                    AddOfferData offerData = new AddOfferData(listOfOffer.size() + 1, strHeading, strDesc, base64img, bitmap);
                    listOfOffer.add(offerData);
                    offerAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
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
            if (ContextCompat.checkSelfPermission(MegaSalesCreatePostActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Camera.", Toast.LENGTH_SHORT)
                        .show();
            } else if (ContextCompat.checkSelfPermission(MegaSalesCreatePostActivity.this,
                    Utils.getAndroidOs()) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Permission Requires to Access Your Storage.",
                        Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(MegaSalesCreatePostActivity.this);
            }
        } else if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission Granted");
                if (Utils.isGpsOn()) {
                    fetchLastLocation();
                }
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

//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                        try {
//                            photoFile = createImageFile();
//
//                            photoURI = FileProvider.getUriForFile(
//                                    MegaSalesCreatePostActivity.this,
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
        return getUriForFile(MegaSalesCreatePostActivity.this, getPackageName() + ".provider", image);
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
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(MegaSalesCreatePostActivity.this);
            cropImage(getCacheImagePath(fileName));
//        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            Uri resultUri = result.getUri();
            Uri resultUri = UCrop.getOutput(data);
            onSelectFromGalleryResult(resultUri);
        } else if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
//            CropImage.activity(photoURI).setCropMenuCropButtonTitle("OK").setAspectRatio(16, 9).setRequestedSize(400, 600).start(MegaSalesCreatePostActivity.this);
            cropImage(photoURI);
        }
    }

    private void cropImage(Uri sourceUri) {
        int IMAGE_COMPRESSION = 100;
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        options.setHideBottomControls(true);
        options.withAspectRatio(16, 9);

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
        bitmap = bm;

        imageView.getLayoutParams().height = 500;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bm != null;
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        base64img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        System.out.println("Gallery image " + base64img);

    }

    private void getApiCall() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(MegaSalesCreatePostActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.listarray, new Response.Listener<String>() {
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

                            str_message = obj.getString("Message");
                            accessOptionListValue.clear();

                            JSONArray jsonArray2 = obj.getJSONArray("accessOption");
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                                PostInitialData accessOptions = new PostInitialData(
                                        jsonObject.getString("keyName"),
                                        jsonObject.getString("value")
                                );
                                accessOptionListValue.add(accessOptions);
                            }

                            PostInitialData initPostData1 = new PostInitialData();
                            initPostData1.setKey("-1");
                            initPostData1.setValue("Access Option");
                            accessOptionListValue.add(0, initPostData1);

                            accessOptionSpinnerValue.clear();
                            for (int i = 0; i < accessOptionListValue.size(); i++) {
                                accessOptionSpinnerValue.add(accessOptionListValue.get(i).getValue());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(MegaSalesCreatePostActivity.this, R.layout.spinner_item, accessOptionSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinAccessOption.setAdapter(arrayAdapter);
                            spinAccessOption.setSelection(0);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MegaSalesCreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(MegaSalesCreatePostActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(MegaSalesCreatePostActivity.this, MegaSalesCreatePostActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("type", RegBusinessTypeSharedPreference.getBusinessType(MegaSalesCreatePostActivity.this));
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, MegaSalesCreatePostActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(MegaSalesCreatePostActivity.this, ManageBusinessActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}