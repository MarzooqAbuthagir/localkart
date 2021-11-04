package com.kart.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.kart.R;
import com.kart.adapter.ViewPagerOfferListAdapter;
import com.kart.model.AddOfferData;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewOfferActivity extends AppCompatActivity {
    private String Tag = "PreviewOfferActivity";

    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;
    TextView toolBarTitle;

    String keyIntent = "", dealCount = "", strPostId = "", strShopId = "", strShopType = "";
    String str_result = "", str_message = "";
    String strShopName = "";

    private static ViewPager mPager;
    LinearLayout sliderDotspanel;

    ViewPagerOfferListAdapter viewPagerAdapter;

    LinearLayout layMain, layLeft, layRight, layBtm;
    TextView tvOfferTitle, tvOfferDesc, tvDate;
    List<AddOfferData> offerDataList = new ArrayList<>();
    int dealOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_offer);

        utilis = new Utilis(PreviewOfferActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        dealCount = intent.getStringExtra("dealCount");
        strShopId = intent.getStringExtra("shopId");
        strPostId = intent.getStringExtra("postId");
        strShopType = intent.getStringExtra("shopType");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(PreviewOfferActivity.this, R.color.colorPrimaryDark));

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

        toolBarTitle = findViewById(R.id.toolbar_title);
        int deal = Integer.parseInt(dealCount) + 1;
        toolBarTitle.setText("Deal " + deal);

        mPager = findViewById(R.id.view_pager);
        sliderDotspanel = findViewById(R.id.slider_dots);

        layMain = findViewById(R.id.lay_main);
        layBtm = findViewById(R.id.lay_btm);
        layLeft = findViewById(R.id.lay_left);
        layRight = findViewById(R.id.lay_right);

        tvOfferTitle = findViewById(R.id.tv_offer_title);
        tvOfferDesc = findViewById(R.id.tv_offer_desc);
        tvDate = findViewById(R.id.tv_date);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Button btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "Valid From " + offerDataList.get(dealOffer).getFromDate() + " To " + offerDataList.get(dealOffer).getToDate() + "\n\n" + strShopName + " \n\n" + offerDataList.get(dealOffer).getHeading() + " \n\n" + offerDataList.get(dealOffer).getDesc() + " \n\nDownload Local Kart App Now ";
                    shareMessage = shareMessage + Utilis.shareUrl;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getApiCall();
    }

    private void getApiCall() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(PreviewOfferActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.viewdeals, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utilis.dismissProgress();

                        layMain.setVisibility(View.VISIBLE);

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            layMain.setVisibility(View.VISIBLE);
                            layBtm.setVisibility(View.VISIBLE);

                            str_message = obj.getString("message");
                            strShopName = obj.getString("shopName");

                            offerDataList.clear();
                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject object = json.getJSONObject(i);
                                AddOfferData offerData = new AddOfferData(
                                        object.getString("heading"),
                                        object.getString("description"),
                                        object.getString("offerImage"),
                                        object.getString("fromDate"),
                                        object.getString("toDate")
                                );
                                offerDataList.add(offerData);
                            }

                            dealOffer = Integer.parseInt(dealCount);
                            setView(dealOffer);

                            if (offerDataList.size() == 1) {
                                layRight.setVisibility(View.GONE);
                                layLeft.setVisibility(View.GONE);
                            } else if ((dealOffer + 1) == offerDataList.size()) {
                                layRight.setVisibility(View.GONE);
                                layLeft.setVisibility(View.VISIBLE);
                            } else if ((dealOffer + 1) == 1) {
                                layRight.setVisibility(View.VISIBLE);
                                layLeft.setVisibility(View.GONE);
                            } else {
                                layRight.setVisibility(View.VISIBLE);
                                layLeft.setVisibility(View.VISIBLE);
                            }

                            layLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dealOffer = dealOffer - 1;
                                    setView(dealOffer);
                                    if ((dealOffer + 1) == 1) {
                                        layRight.setVisibility(View.VISIBLE);
                                        layLeft.setVisibility(View.GONE);
                                    } else {
                                        layRight.setVisibility(View.VISIBLE);
                                        layLeft.setVisibility(View.VISIBLE);
                                    }
                                    toolBarTitle.setText("Deal " + (dealOffer + 1));
                                }
                            });

                            layRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dealOffer = dealOffer + 1;
                                    setView(dealOffer);
                                    if ((dealOffer + 1) == offerDataList.size()) {
                                        layRight.setVisibility(View.GONE);
                                        layLeft.setVisibility(View.VISIBLE);
                                    } else {
                                        layLeft.setVisibility(View.VISIBLE);
                                        layRight.setVisibility(View.VISIBLE);
                                    }
                                    toolBarTitle.setText("Deal " + (dealOffer + 1));
                                }
                            });

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(PreviewOfferActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PreviewOfferActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(PreviewOfferActivity.this, PreviewOfferActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("postIndexId", strPostId);
                    params.put("businessIndexId", strShopId);
                    params.put("businessType", strShopType);
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PreviewOfferActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setView(int dealOffer) {
        tvOfferTitle.setText(offerDataList.get(dealOffer).getHeading());
        tvOfferDesc.setText(offerDataList.get(dealOffer).getDesc());
        tvDate.setText(offerDataList.get(dealOffer).getFromDate() + " To " + offerDataList.get(dealOffer).getToDate());

        viewPagerAdapter = new ViewPagerOfferListAdapter(offerDataList, PreviewOfferActivity.this);
        mPager.setAdapter(viewPagerAdapter);

        mPager.setCurrentItem(dealOffer);

        addDot(dealOffer);
    }

    private void addDot(int dotPosition) {
        int dotscount = viewPagerAdapter.getCount();
        ImageView[] dots = new ImageView[dotscount];

        sliderDotspanel.removeAllViews();

        for (int j = 0; j < dotscount; j++) {

            dots[j] = new ImageView(PreviewOfferActivity.this);
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
        finish();
    }
}