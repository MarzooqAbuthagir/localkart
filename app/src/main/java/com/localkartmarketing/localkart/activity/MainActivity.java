package com.localkartmarketing.localkart.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.localkartmarketing.localkart.BuildConfig;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.CategoryAdapter;
import com.localkartmarketing.localkart.adapter.ViewPagerAdapter;
import com.localkartmarketing.localkart.model.CategoryData;
import com.localkartmarketing.localkart.model.SilderData;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.App;
import com.localkartmarketing.localkart.support.LoginSharedPreference;
import com.localkartmarketing.localkart.support.RegBusinessSharedPrefrence;
import com.localkartmarketing.localkart.support.Utilis;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String Tag = "MainActivity";
    Utilis utilis;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    View header;
    DrawerLayout drawer;

    private static ViewPager mPager;
    LinearLayout sliderDotspanel;
    private ArrayList<SilderData> silders;
    private static int currentPage = 0;
    //    Timer swipeTimer;
//    final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
//    final long PERIOD_MS = 4500; // time in milliseconds between successive task executions.
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    ViewPagerAdapter viewPagerAdapter;
    String str_result = "", str_message = "";

    private Button btnShopping, btnServices;
    private ArrayList<CategoryData> categoryListValue = new ArrayList<CategoryData>();
    private GridView gridView;

    private String keyIntent = "";

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utilis = new Utilis(MainActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        app = (App) getApplication();

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.my_statusbar_color));

        appBarLayout = findViewById(R.id.view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.headmenu);

        navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
            MenuItem menuItem = menu.getItem(menuItemIndex);
            String menuFlag = RegBusinessSharedPrefrence.getMenuFlag(MainActivity.this);
            if (menuFlag.equalsIgnoreCase("0")) {
                if (menuItem.getItemId() == R.id.nav_reg_business) {
                    menuItem.setVisible(true);
                }
                if (menuItem.getItemId() == R.id.nav_manage_business) {
                    menuItem.setVisible(false);
                }
            } else if (menuFlag.equalsIgnoreCase("1")) {
                if (menuItem.getItemId() == R.id.nav_reg_business) {
                    menuItem.setVisible(false);
                }
                if (menuItem.getItemId() == R.id.nav_manage_business) {
                    menuItem.setVisible(true);
                }
            }
        }
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        header = navigationView.getHeaderView(0);
        ImageView imgDp = header.findViewById(R.id.img_dp);

        TextView tvName = header.findViewById(R.id.tv_name);
        TextView tvMob = header.findViewById(R.id.tv_mob);

        tvName.setText(userDetail.getName());
        tvMob.setText(userDetail.getMobile());

        Glide.with(MainActivity.this).load(userDetail.getImage())
                .placeholder(R.drawable.placeholder_profile)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgDp);

        mPager = findViewById(R.id.view_pager);

        sliderDotspanel = findViewById(R.id.slider_dots);

        silders = new ArrayList<>();

        getBannerImages();

        app.initMethod(userDetail.getId());

        btnShopping = findViewById(R.id.btn_shopping);
        btnServices = findViewById(R.id.btn_services);
        gridView = findViewById(R.id.grid_category);

        if (keyIntent.equalsIgnoreCase("Shopping")) {
            btnServices.setTextColor(Color.parseColor("#7F7F7F"));
            btnServices.setBackgroundResource(R.drawable.right_curve_unsel);
            btnShopping.setTextColor(Color.parseColor("#ffffff"));
            btnShopping.setBackgroundResource(R.drawable.left_curve_sel);
            getCategoryData(Utilis.shopcategories);
        } else {
            btnShopping.setTextColor(Color.parseColor("#7F7F7F"));
            btnShopping.setBackgroundResource(R.drawable.left_curve_unsel);
            btnServices.setTextColor(Color.parseColor("#ffffff"));
            btnServices.setBackgroundResource(R.drawable.right_curve_sel);
            getCategoryData(Utilis.servicecategories);
        }

        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!keyIntent.equalsIgnoreCase("Shopping")) {
                    keyIntent = "Shopping";
                    btnServices.setTextColor(Color.parseColor("#7F7F7F"));
                    btnServices.setBackgroundResource(R.drawable.right_curve_unsel);
                    btnShopping.setTextColor(Color.parseColor("#ffffff"));
                    btnShopping.setBackgroundResource(R.drawable.left_curve_sel);
                    getCategoryData(Utilis.shopcategories);
                }
            }
        });

        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilis.clearImageList(MainActivity.this);
                if (!keyIntent.equalsIgnoreCase("Services")) {
                    keyIntent = "Services";
                    btnShopping.setTextColor(Color.parseColor("#7F7F7F"));
                    btnShopping.setBackgroundResource(R.drawable.left_curve_unsel);
                    btnServices.setTextColor(Color.parseColor("#ffffff"));
                    btnServices.setBackgroundResource(R.drawable.right_curve_sel);
                    getCategoryData(Utilis.servicecategories);
                }
            }
        });

        TextView tvVersion = findViewById(R.id.tv_version);
        tvVersion.setText("Version " + BuildConfig.VERSION_NAME);
        tvVersion.setTextColor(Color.GRAY);
    }

    private void getCategoryData(String apiName) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(MainActivity.this);

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

                            CategoryAdapter adapter = new CategoryAdapter(MainActivity.this, categoryListValue, keyIntent);
                            gridView.setAdapter(adapter);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MainActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(MainActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, MainActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getBannerImages() {
        if (Utilis.isInternetOn()) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utilis.Api + Utilis.getbanner, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getBannerImages response - " + response);

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getBannerImages result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                SilderData silderData = new SilderData(
                                        jsonObject.getString("Image"),
                                        jsonObject.getString("actionType"),
                                        jsonObject.getString("dataLink"));

                                silders.add(silderData);
                            }
                            setViewPager(json.length());

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(MainActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(MainActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, MainActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViewPager(final int length) {
        viewPagerAdapter = new ViewPagerAdapter(silders, MainActivity.this);
        mPager.setAdapter(viewPagerAdapter);

//        // Auto start of viewpager
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//
//                if (currentPage == length) {
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
                if (currentPage == length) {
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

            dots[j] = new ImageView(MainActivity.this);
            dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[j], params);

        }
        dots[dotPosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } /*else if (id == R.id.nav_settings) {

        }*/ else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_reg_business) {
            Utilis.clearRegPref(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, AdvertiseBusinessActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_manage_business) {
            Intent intent = new Intent(MainActivity.this, ManageBusinessActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_how_it_works) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utilis.howItWorksUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null);
                startActivity(intent);
            }
        } else if (id == R.id.nav_franchise) {
            Intent intent = new Intent(MainActivity.this, FranchiseActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_refer) {
            Intent intent = new Intent(MainActivity.this, ReferralActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_share) {

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage = "Local Kart \nWhy Shop Online? Shop Nearby !\n\nGet More Deals and Benefits !\n\nDownload Local Kart App Now ";
                shareMessage = shareMessage + Utilis.shareUrl;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_rate_us) {
            Intent intent = new Intent(MainActivity.this, RateUsActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_privacy_policy) {
            Intent intent = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            intent.putExtra("key", keyIntent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_Logout) {
            drawer.closeDrawers();
            toLogoutCustomer();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toLogoutCustomer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(MainActivity.this.getResources().getString(R.string.logout_title))
                .setMessage(MainActivity.this.getResources().getString(R.string.logout_msg))
                .setPositiveButton(MainActivity.this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();

                        LoginSharedPreference.setLoggedIn(MainActivity.this, false);

                        SharedPreferences preferences = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton(MainActivity.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

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

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}