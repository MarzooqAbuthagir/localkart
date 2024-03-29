package com.localkartmarketing.localkart.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.AccessOptionAdapter;
import com.localkartmarketing.localkart.adapter.ServiceOfferedAdapter;
import com.localkartmarketing.localkart.adapter.ViewPagerShopBannerAdapter;
import com.localkartmarketing.localkart.model.AccessOptions;
import com.localkartmarketing.localkart.model.DirectoryMoreDetailsData;
import com.localkartmarketing.localkart.model.ShopBanner;
import com.localkartmarketing.localkart.support.RegBusinessIdSharedPreference;
import com.localkartmarketing.localkart.support.RegBusinessTypeSharedPreference;
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

public class DigitalVCardActivity extends AppCompatActivity {
    private final String Tag = "DigitalVCardActivity";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strLatitude = "", strLongitude = "";
    String str_result = "", str_message = "", strShareUrl = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_vcard);

        utils = new Utils(DigitalVCardActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strLatitude = intent.getStringExtra("latitude");
        strLongitude = intent.getStringExtra("longitude");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(DigitalVCardActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Digital Vcard");

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

        mPager = findViewById(R.id.view_pager);
        sliderDotspanel = findViewById(R.id.slider_dots);


//        tvViewMap = findViewById(R.id.tv_view_map);
//        tvViewMap.setPaintFlags(tvViewMap.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        tvViewMap.setText(Html.fromHtml(getString(R.string.view_map)));

        layService = findViewById(R.id.lay_service);

        getApiCall();

        LinearLayout layShare = findViewById(R.id.lay_share);
        layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] optionsMenu = {"Text", "Image", "Cancel"}; // create a menuOption Array
                // create a dialog for showing the optionsMenu
                AlertDialog.Builder builder = new AlertDialog.Builder(DigitalVCardActivity.this);
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

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title_"+ Calendar.getInstance().getTime(), null);
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

    private void getApiCall() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(DigitalVCardActivity.this);

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
                            strShareUrl = json.getString("shareUrl");

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
                            Toast.makeText(DigitalVCardActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(DigitalVCardActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(DigitalVCardActivity.this, DigitalVCardActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("shopIndexId", RegBusinessIdSharedPreference.getBusinessId(DigitalVCardActivity.this));
                    params.put("shopType", RegBusinessTypeSharedPreference.getBusinessType(DigitalVCardActivity.this));
                    params.put("latitude", strLatitude);
                    params.put("longitude", strLongitude);
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, DigitalVCardActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViews() {
        Glide.with(DigitalVCardActivity.this).load(detailsData.getShopLogo())
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
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DigitalVCardActivity.this);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new ServiceOfferedAdapter(this, serviceList);
            recyclerView.setAdapter(adapter);
        } else {
            layService.setVisibility(View.GONE);
        }

        rvAccessOption = findViewById(R.id.rv_access_option);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DigitalVCardActivity.this);
        rvAccessOption.setLayoutManager(layoutManager);

        accessOptionAdapter = new AccessOptionAdapter(this, accessOptionsList);
        rvAccessOption.setAdapter(accessOptionAdapter);
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
        viewPagerAdapter = new ViewPagerShopBannerAdapter(shopBannerList, DigitalVCardActivity.this);
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

            dots[j] = new ImageView(DigitalVCardActivity.this);
            dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[j], params);

        }
        dots[dotPosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(DigitalVCardActivity.this, ManageBusinessActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}