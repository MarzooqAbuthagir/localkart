package com.localkartmarketing.localkart.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.AccessOptionAdapter;
import com.localkartmarketing.localkart.adapter.ServiceOfferedAdapter;
import com.localkartmarketing.localkart.adapter.ViewPagerShopBannerAdapter;
import com.localkartmarketing.localkart.model.AccessOptions;
import com.localkartmarketing.localkart.model.DirectoryMoreDetailsData;
import com.localkartmarketing.localkart.model.ShopBanner;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryMoreDetailsActivity extends AppCompatActivity {
    private final String Tag = "DirectoryMoreDetailsActivity";

    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strShopIndexId = "", strShopType = "", strLatitude = "", strLongitude = "", strIsSubscribed = "", strConstPostType = "", strShareUrl = "";
    String str_result = "", str_message = "";

    DirectoryMoreDetailsData detailsData;

    LinearLayout layMain;
    ImageView ivShopLogo;
    TextView tvShopName, tvShopDesc,/* tvPhone, tvMobile, tvWhatsapp, tvEmail, tvWebsite,*/
            tvDirection, tvAddress /*, tvViewMap*/;

    private static ViewPager mPager;
    LinearLayout sliderDotspanel;
    private static int currentPage = 0;
    //    Timer swipeTimer;
//    final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
//    final long PERIOD_MS = 4500; // time in milliseconds between successive task executions.
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    List<ShopBanner> shopBannerList = new ArrayList<>();
    ViewPagerShopBannerAdapter viewPagerAdapter;

    LinearLayout layService;
    List<String> serviceList = new ArrayList<>();
    RecyclerView recyclerView;
    ServiceOfferedAdapter adapter;

    List<AccessOptions> accessOptionsList = new ArrayList<>();

    RecyclerView rvAccessOption;
    AccessOptionAdapter accessOptionAdapter;

    LinearLayout layNotify, layShare;
    ImageView imgNotify;
    Button btnSubscribe;

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    TextView tvReport, tvRate;
    String countApiName = "", rateApiName = "";
    RatingBar ratingBar;
    TextView tvViewCount, tvRating;
    String strRating="", strViewCount ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_more_details);

        utils = new Utils(DirectoryMoreDetailsActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strShopIndexId = intent.getStringExtra("shopId");
        strShopType = intent.getStringExtra("shopType");
        strLatitude = intent.getStringExtra("latitude");
        strLongitude = intent.getStringExtra("longitude");
        strIsSubscribed = intent.getStringExtra("isSubscribed");
        strConstPostType = intent.getStringExtra("constPostType");
        strShareUrl = intent.getStringExtra("shareUrl");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(DirectoryMoreDetailsActivity.this, R.color.colorPrimaryDark));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        layMain = findViewById(R.id.lay_main);
        ivShopLogo = findViewById(R.id.iv_shop_logo);
        tvShopName = findViewById(R.id.tv_shop_name);
        tvShopDesc = findViewById(R.id.tv_shop_desc);
//        tvPhone = findViewById(R.id.tv_phone);
//        tvMobile = findViewById(R.id.tv_mobile);
//        tvWhatsapp = findViewById(R.id.tv_whatsapp);
//        tvEmail = findViewById(R.id.tv_email);
//        tvWebsite = findViewById(R.id.tv_website);
        tvDirection = findViewById(R.id.tv_direction);
        tvAddress = findViewById(R.id.tv_address);
        tvReport = findViewById(R.id.tv_report);
        tvRate = findViewById(R.id.tv_rate);

        ratingBar = findViewById(R.id.ratingBar);
        Drawable drawable = ratingBar.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FFDF00"),PorterDuff.Mode.SRC_ATOP);
        tvRating = findViewById(R.id.tv_avg_rating);
        tvViewCount = findViewById(R.id.tv_views);

        mPager = findViewById(R.id.view_pager);
        sliderDotspanel = findViewById(R.id.slider_dots);


//        tvViewMap = findViewById(R.id.tv_view_map);
//        tvViewMap.setPaintFlags(tvViewMap.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        tvViewMap.setText(Html.fromHtml(getString(R.string.view_map)));

        layService = findViewById(R.id.lay_service);

        getApiCall();

        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("More Details");

        layNotify = findViewById(R.id.lay_subscribe);
        btnSubscribe = findViewById(R.id.btn_subscribe);
        imgNotify = findViewById(R.id.img_subscribe);

        if (Integer.parseInt(strIsSubscribed) == 0) {
            btnSubscribe.setText("Subscribe");
            imgNotify.setBackgroundResource(R.drawable.ic_outline_notifications_active_24);
        } else {
            btnSubscribe.setText("UnSubscribe");
            imgNotify.setBackgroundResource(R.drawable.ic_bell_subscribe);
        }

        layNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isInternetOn()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DirectoryMoreDetailsActivity.this);
                    String state = Integer.parseInt(strIsSubscribed) == 0 ? "Subscribe" : "UnSubscribe";
                    builder.setTitle(state)
                            .setMessage(Html.fromHtml("You'll receive notifications when <b>" + detailsData.getShopName() + "</b> posts new Deals and Offers. Are you sure want to " + state + "?"))
                            .setPositiveButton(DirectoryMoreDetailsActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing but close the dialog
                                    dialog.dismiss();
                                    if (Integer.parseInt(strIsSubscribed) == 0) {
                                        subscribeShop();
                                    } else {
                                        unsubscribeShop();
                                    }
                                }
                            })
                            .setNegativeButton(DirectoryMoreDetailsActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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

                } else {
                    Toast.makeText(DirectoryMoreDetailsActivity.this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        layShare = findViewById(R.id.lay_share);
        layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] optionsMenu = {"Text", "Image", "Cancel"}; // create a menuOption Array
                // create a dialog for showing the optionsMenu
                AlertDialog.Builder builder = new AlertDialog.Builder(DirectoryMoreDetailsActivity.this);
                // set the items in builder
                builder.setTitle("Share Via");
                builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (optionsMenu[i].equals("Text")) {
                            try {
                                String shareMessage = detailsData.getShopName() + "\n\nHere is my Digital vCard " + strShareUrl.replaceAll("(?<!(http:|https:))/+", "/");
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (optionsMenu[i].equals("Image")) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getBitmapFromView(layMain)));
                            String shareMessage = "Download LocalKart App Now ";
                            shareMessage = shareMessage + Utils.shareUrl;
                            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            try {
                                startActivity(Intent.createChooser(intent, "My Profile ..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                ex.printStackTrace();
                            }
                        } else if (optionsMenu[i].equals("Exit")) {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void unsubscribeShop() {
        Utils.showProgress(DirectoryMoreDetailsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.unsubscribe, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " unsubscribeShop response - " + response);

                    Utils.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " unsubscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");

                        strIsSubscribed = "0";
                        btnSubscribe.setText("Subscribe");
                        imgNotify.setBackgroundResource(R.drawable.ic_outline_notifications_active_24);

                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.dismissProgress();
                Toast.makeText(DirectoryMoreDetailsActivity.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", strShopType);
                params.put("shopId", strShopIndexId);
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " unsubscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(DirectoryMoreDetailsActivity.this).addToRequestQueue(stringRequest);
    }

    private void subscribeShop() {
        Utils.showProgress(DirectoryMoreDetailsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.savesubscribers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " subscribeShop response - " + response);

                    Utils.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " subscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");

                        strIsSubscribed = "1";
                        btnSubscribe.setText("UnSubscribe");
                        imgNotify.setBackgroundResource(R.drawable.ic_bell_subscribe);
                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismissProgress();
                Toast.makeText(DirectoryMoreDetailsActivity.this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", strShopType);
                params.put("shopId", strShopIndexId);
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " subscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(DirectoryMoreDetailsActivity.this).addToRequestQueue(stringRequest);
    }

    private void getApiCall() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(DirectoryMoreDetailsActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.directorymoredetails, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utils.dismissProgress();

                        layMain.setVisibility(View.VISIBLE);

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");

                            detailsData = new DirectoryMoreDetailsData();
                            detailsData.setShopName(json.getString("shopName"));
                            detailsData.setShopLogo(json.getString("shopLogo"));
                            detailsData.setShopDesc(json.getString("shopDesc"));
                            detailsData.setShopPhone(json.getString("shopPhone"));
                            detailsData.setShopMobile(json.getString("shopMobile"));
                            detailsData.setShopWhatsapp(json.getString("shopWhatsapp"));
                            detailsData.setShopEmail(json.getString("shopEmail"));
                            detailsData.setShopWebsite(json.getString("shopWebsite"));
                            detailsData.setDistance(json.getString("distance"));
                            detailsData.setShopLatitude(json.getString("shopLatitude"));
                            detailsData.setShopLongitude(json.getString("shopLongitude"));
                            detailsData.setShopDoorNo(json.getString("shopDoorNo"));
                            detailsData.setShopArea(json.getString("shopArea"));
                            detailsData.setShopLocality(json.getString("shopLocality"));
                            detailsData.setShopLandmark(json.getString("shopLandmark"));
                            detailsData.setShopPost(json.getString("shopPost"));
                            detailsData.setShopState(json.getString("shopState"));
                            detailsData.setShopDistrict(json.getString("shopDistrict"));
                            detailsData.setShopPincode(json.getString("shopPincode"));

                            shopBannerList.clear();

                            JSONArray jsonArray = json.getJSONArray("shopImageList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                ShopBanner shopBanner = new ShopBanner(
                                        object.getString("imageUrl")
                                );
                                shopBannerList.add(shopBanner);
                            }

                            serviceList.clear();

                            JSONArray jsonArray1 = json.getJSONArray("shopServiceList");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject object = jsonArray1.getJSONObject(i);
                                serviceList.add(object.getString("serviceName"));
                            }

                            accessOptionsList.clear();

                            JSONArray jsonArray2 = json.getJSONArray("accessOptions");
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                                AccessOptions accessOptions = new AccessOptions(
                                        jsonObject.getString("keyName"),
                                        jsonObject.getString("value")
                                );
                                accessOptionsList.add(accessOptions);
                            }

                            setViews();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(DirectoryMoreDetailsActivity.this, DirectoryMoreDetailsActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("shopIndexId", strShopIndexId);
                    params.put("shopType", strShopType);
                    params.put("latitude", strLatitude);
                    params.put("longitude", strLongitude);
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, DirectoryMoreDetailsActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViews() {
        Glide.with(DirectoryMoreDetailsActivity.this).load(detailsData.getShopLogo())
                .placeholder(R.mipmap.ic_launcher_round)
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivShopLogo);

        tvShopName.setText(detailsData.getShopName());
        tvShopDesc.setText(detailsData.getShopDesc());
//        tvPhone.setText(detailsData.getShopPhone());
//        tvMobile.setText(detailsData.getShopMobile());
//        tvWhatsapp.setText(detailsData.getShopWhatsapp());
//        tvEmail.setText(detailsData.getShopEmail());
//        tvWebsite.setText(detailsData.getShopWebsite());
        tvDirection.setText(detailsData.getDistance() + " • Map");
        System.out.println("detailsData.getShopLandmark()" + detailsData.getShopLandmark());
        String shopAddress = "";
        if (detailsData.getShopLandmark().isEmpty()) {
            shopAddress = detailsData.getShopDoorNo() + ", " + detailsData.getShopLocality() + ", \n" +
                    detailsData.getShopArea() + "," + detailsData.getShopPost() + ", \n" +
                    detailsData.getShopDistrict() + " - " + detailsData.getShopPincode() + ".\n" +
                    detailsData.getShopState() + ".";
        } else {
            shopAddress = detailsData.getShopDoorNo() + ", " + detailsData.getShopLocality() + ", \n" +
                    detailsData.getShopArea() + "," + detailsData.getShopPost() + ", \n" +
                    detailsData.getShopLandmark() + ", \n" + detailsData.getShopDistrict() + " - " + detailsData.getShopPincode() + ".\n" +
                    detailsData.getShopState() + ".";
        }


        tvAddress.setText(shopAddress);

        setShopBanners(shopBannerList);

//        tvViewMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DirectoryMoreDetailsActivity.this, ShopLocationActivity.class);
//                intent.putExtra("key", keyIntent);
//                intent.putExtra("latitude", detailsData.getShopLatitude());
//                intent.putExtra("longitude", detailsData.getShopLongitude());
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });

        ImageView ivViewMap = findViewById(R.id.iv_view_map);
        ivViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLocation();
            }
        });

        RelativeLayout layDirection = findViewById(R.id.lay_direction);
        layDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLocation();
            }
        });

        if (serviceList.size() > 0) {
            layService.setVisibility(View.VISIBLE);
            recyclerView = findViewById(R.id.recycler_view);

            // setting recyclerView layoutManager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DirectoryMoreDetailsActivity.this);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new ServiceOfferedAdapter(this, serviceList);
            recyclerView.setAdapter(adapter);

            tvReport.setText(Html.fromHtml("<u>Report This Service</u>"));
            tvRate.setText(Html.fromHtml("<u>Rate This Service</u>"));

        } else {
            layService.setVisibility(View.GONE);
            tvReport.setText(Html.fromHtml("<u>Report This Shop</u>"));
            tvRate.setText(Html.fromHtml("<u>Rate This Shop</u>"));
        }

        rvAccessOption = findViewById(R.id.rv_access_option);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DirectoryMoreDetailsActivity.this);
        rvAccessOption.setLayoutManager(layoutManager);

        accessOptionAdapter = new AccessOptionAdapter(this, accessOptionsList);
        rvAccessOption.setAdapter(accessOptionAdapter);

        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DirectoryMoreDetailsActivity.this, ReportActivity.class);
                intent.putExtra("key", keyIntent);
                intent.putExtra("title", tvReport.getText().toString().trim());
                intent.putExtra("shopIndexId", strShopIndexId);
                intent.putExtra("postIndexId", "");
                intent.putExtra("type", strShopType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        tvRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DirectoryMoreDetailsActivity.this, R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_rating_bar, viewGroup, false);
                TextView tvRateTitle = dialogView.findViewById(R.id.tv_rate_title);
                final RatingBar ratingbar = dialogView.findViewById(R.id.ratingBar);
                Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                builder.setView(dialogView);
                builder.setCancelable(false);

                ratingbar.setRating(Float.parseFloat(strRating));

                if (strShopType.equalsIgnoreCase("Shopping")) {
                    tvRateTitle.setText("Rate this shop");
                    rateApiName = Utils.shoprating;
                } else {
                    tvRateTitle.setText("Rate this service");
                    rateApiName = Utils.servicerating;
                }
                final android.app.AlertDialog alertDialog = builder.create();
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rating = String.valueOf(ratingbar.getRating());
                        if (ratingbar.getRating() == 0.0) {
                            Toast.makeText(DirectoryMoreDetailsActivity.this, "Select rating to submit", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Utils.isInternetOn()) {
                                rateApi(rating, alertDialog, rateApiName);
                            } else {
                                Toast.makeText(DirectoryMoreDetailsActivity.this, DirectoryMoreDetailsActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                alertDialog.show();

            }
        });

        updateViewCount();
    }

    private void updateViewCount() {

        JSONObject JObj = new JSONObject();
        try {
            JObj.put("user_id", userDetail.getId());
            if (strShopType.equalsIgnoreCase("Shopping")) {
                JObj.put("shop_id", strShopIndexId);
                countApiName = Utils.shopviewcount;
            } else {
                JObj.put("service_id", strShopIndexId);
                countApiName = Utils.serviceviewcount;
            }
        } catch (Exception e) {
            System.out.println(Tag + " input exception " + e.getMessage());
        }

        Utils.showProgress(DirectoryMoreDetailsActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.Api + countApiName, JObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    System.out.println(Tag + " updateViewCount response - " + obj);

                    Utils.dismissProgress();

                    str_result = obj.getString("httpCode");
                    System.out.print(Tag + " updateViewCount result " + str_result);

                    if (Integer.parseInt(str_result) == 200) {

                        setRateAndView();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismissProgress();
                Toast.makeText(DirectoryMoreDetailsActivity.this, DirectoryMoreDetailsActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void setRateAndView() {
        JSONObject JObj = new JSONObject();
        try {
            JObj.put("shop_service_id", strShopIndexId);
            JObj.put("shopType", strShopType);

        } catch (Exception e) {
            System.out.println(Tag + " input exception " + e.getMessage());
        }

        Utils.showProgress(DirectoryMoreDetailsActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.Api + Utils.shopservicedetailcount, JObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    System.out.println(Tag + " setRateAndView response - " + obj);

                    Utils.dismissProgress();

                    strViewCount = obj.getString("viewCount");
                    strRating = obj.getString("averageRating");

                    tvViewCount.setText(strViewCount);
                    tvRating.setText(strRating);
                    ratingBar.setRating(Float.parseFloat(strRating));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismissProgress();
                Toast.makeText(DirectoryMoreDetailsActivity.this, DirectoryMoreDetailsActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void rateApi(final String rating, final android.app.AlertDialog alertDialog, String rateApiName) {
        JSONObject JObj = new JSONObject();
        try {
            JObj.put("user_id", userDetail.getId());
            JObj.put("rating", rating);
            if (strShopType.equalsIgnoreCase("Shopping")) {
                JObj.put("shop_id", strShopIndexId);
            } else {
                JObj.put("service_id", strShopIndexId);
            }
        } catch (Exception e) {
            System.out.println(Tag + " input exception " + e.getMessage());
        }

        Utils.showProgress(DirectoryMoreDetailsActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utils.Api + rateApiName, JObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    System.out.println(Tag + " rateApi response - " + obj);

                    Utils.dismissProgress();

                    str_result = obj.getString("httpCode");
                    System.out.print(Tag + " rateApi result " + str_result);

                    if (Integer.parseInt(str_result) == 200) {

                        str_message = obj.getString("message");
                        alertDialog.dismiss();
                        Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();

                        setRateAndView();

                    } else {
                        str_message = obj.getString("message");
                        Toast.makeText(DirectoryMoreDetailsActivity.this, str_message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismissProgress();
                Toast.makeText(DirectoryMoreDetailsActivity.this, DirectoryMoreDetailsActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void openLocation() {
        String geoUri = "http://maps.google.com/maps?q=loc:" + detailsData.getShopLatitude() + "," + detailsData.getShopLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    private void setShopBanners(final List<ShopBanner> shopBannerList) {
        viewPagerAdapter = new ViewPagerShopBannerAdapter(shopBannerList, DirectoryMoreDetailsActivity.this);
        mPager.setAdapter(viewPagerAdapter);

//        // Auto start of viewpager
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//
//                if (currentPage == shopBannerList.size()) {
//                    currentPage = 0;
//                }
//                mPager.setCurrentItem(currentPage++, true);
//            }
//        };
//        handler.postDelayed(Update, 1000);
//        swipeTimer = new Timer();
//        swipeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, DELAY_MS, PERIOD_MS);

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                if (currentPage == shopBannerList.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        }, delay);

        addDot(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void addDot(int dotPosition) {
        int dotscount = viewPagerAdapter.getCount();
        ImageView[] dots = new ImageView[dotscount];

        sliderDotspanel.removeAllViews();

        for (int j = 0; j < dotscount; j++) {

            dots[j] = new ImageView(DirectoryMoreDetailsActivity.this);
            dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[j], params);

        }
        dots[dotPosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Utils.callResume = 1;
        Utils.constPostType = strConstPostType;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}