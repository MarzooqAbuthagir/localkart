package com.localkartmarketing.localkart.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import com.localkartmarketing.localkart.adapter.PlanAdapter;
import com.localkartmarketing.localkart.adapter.PlanPriceAdapter;
import com.localkartmarketing.localkart.model.AccessOptions;
import com.localkartmarketing.localkart.model.PlanDynamicKeys;
import com.localkartmarketing.localkart.model.PlanPrices;
import com.localkartmarketing.localkart.model.PlanResponse;
import com.localkartmarketing.localkart.model.UserDetail;
import com.localkartmarketing.localkart.support.DividerItemDecorator;
import com.localkartmarketing.localkart.support.Utilis;
import com.localkartmarketing.localkart.support.VolleySingleton;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlanActivity extends AppCompatActivity implements PaymentResultListener {
    private final String Tag = "PlanActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "";

    RecyclerView recyclerView;
    PlanAdapter planAdapter;

    LinearLayout rootLayout;
//    RecyclerView recyclerViewPlan;
    List<PlanPrices> planPrices = new ArrayList<>();
    PlanPriceAdapter planPriceAdapter;
    List<AccessOptions> accessOptionsList = new ArrayList<>();

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    HorizontalScrollView horizontalScrollView, hsvPrice;
    boolean isSubcribed = false;
    TextView tvNoPlans;

    String planId;
    String planName;
    String[] amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        utilis = new Utilis(PlanActivity.this);

        mPrefs = getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(PlanActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Plans");

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

//        recyclerViewPlan = findViewById(R.id.recycler_view_plan);

//         setting recyclerView layoutManager
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PlanActivity.this);
//        recyclerViewPlan.setLayoutManager(layoutManager);
//
//        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(
//                PlanActivity.this,
//                LinearLayoutManager.HORIZONTAL,
//                false);
//        recyclerViewPlan.setLayoutManager(HorizontalLayout);

        horizontalScrollView = findViewById(R.id.hscrll1);
        hsvPrice = findViewById(R.id.hsv_price);
        tvNoPlans = findViewById(R.id.tv_no_plans);

        recyclerView = findViewById(R.id.recycler_view);

        // setting recyclerView layoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PlanActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(PlanActivity.this, R.drawable.divider_line));
        recyclerView.addItemDecoration(dividerItemDecoration);

        getApiCall();

    }

    private void getApiCall() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(PlanActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.getpricing, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getApiCall response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            planPrices.clear();

                            isSubcribed = obj.getBoolean("isSubscribed");

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                PlanPrices plan = new PlanPrices(
                                        jsonObject.getString("planId"),
                                        jsonObject.getString("planName"),
                                        jsonObject.getString("planPrice"),
                                        jsonObject.getString("planValidity"),
                                        jsonObject.getBoolean("isCurrentPlan"));

                                planPrices.add(plan);
                            }

                            accessOptionsList.clear();
                            JSONArray jsonArray = obj.getJSONArray("description");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                AccessOptions accessOptions = new AccessOptions(
                                        jsonObject.getString("key"),
                                        jsonObject.getString("value")
                                );
                                accessOptionsList.add(accessOptions);
                            }

                            getPlanList();

                        } else if (Integer.parseInt(str_result) == 2) {
                            tvNoPlans.setVisibility(View.VISIBLE);
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(PlanActivity.this, PlanActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PlanActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    String str_result = "", str_message = "";
    private ArrayList<HashMap<String, String>> hashMapArrayList = new ArrayList<>();
    private JSONObject jsonObjectHeader = new JSONObject();

    private void getPlanList() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(PlanActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.viewplan, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getPlanList response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        if (Integer.parseInt(str_result) == 0) {
                            tvNoPlans.setVisibility(View.GONE);
                            horizontalScrollView.setVisibility(View.VISIBLE);
                            hsvPrice.setVisibility(View.VISIBLE);
                            jsonObjectHeader = generateDynamicData(response);
                            hashMapArrayList = generateListFromJsonObject(jsonObjectHeader);
                            /*recyclerView = findViewById(R.id.recycler_view);

                            // setting recyclerView layoutManager
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PlanActivity.this);
                            recyclerView.setLayoutManager(layoutManager);

//                            Drawable mDivider = ContextCompat.getDrawable(PlanActivity.this, R.drawable.divider_line);
//                            DividerItemDecoration itemDecoration = new DividerItemDecoration(PlanActivity.this, DividerItemDecoration.VERTICAL);
//                            itemDecoration.setDrawable(mDivider);
//                            recyclerView.addItemDecoration(itemDecoration);

                            RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(PlanActivity.this, R.drawable.divider_line));
                            recyclerView.addItemDecoration(dividerItemDecoration);*/

                            planAdapter = new PlanAdapter(PlanActivity.this, hashMapArrayList, jsonObjectHeader, accessOptionsList);
                            recyclerView.setAdapter(planAdapter);

                            Collections.sort(planPrices, new Comparator<PlanPrices>() {
                                @Override
                                public int compare(PlanPrices planPrices1, PlanPrices planPrices2) {
                                    return Boolean.compare(planPrices2.isCurrentPlan(), planPrices1.isCurrentPlan());
                                }
                            });

//                            planPriceAdapter = new PlanPriceAdapter(PlanActivity.this, planPrices);
//                            recyclerViewPlan.setAdapter(planPriceAdapter);
//
//                            planPriceAdapter.setOnItemClickListener(new PlanPriceAdapter.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(View view, int position) {
//                                    String planId = planPrices.get(position).getPlanId();
//                                    getPaymentApiCall(planId);
//                                }
//                            });

                            setPlanPriceView();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(PlanActivity.this, PlanActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    System.out.println(Tag + " getApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PlanActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    int code = 0;
    Button btnApply;
    EditText etCode;
    TextView tvAmount, tvFinalAmount;
    String strRefType = "";

    private void showCodeDialog(View view, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_referral_coupon_code, viewGroup, false);
        final TextView tvPlanName = dialogView.findViewById(R.id.tv_plan_name);
        tvAmount = dialogView.findViewById(R.id.tv_amount);
        tvFinalAmount = dialogView.findViewById(R.id.tv_final_amount);
        RelativeLayout layCode = dialogView.findViewById(R.id.lay_code);
        etCode = dialogView.findViewById(R.id.et_code);
        btnApply = dialogView.findViewById(R.id.btn_apply);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        builder.setView(dialogView);
        builder.setCancelable(false);

        if (Integer.parseInt(planPrices.get(position).getPlanPrice()) != 0) {
            layCode.setVisibility(View.VISIBLE);
        } else {
            code = 0;
            etCode.setText("");
            etCode.setEnabled(true);
            strRefType = "";
            layCode.setVisibility(View.GONE);
        }

        tvAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + planPrices.get(position).getPlanPrice());
        tvPlanName.setText(planPrices.get(position).getPlanName());
        tvFinalAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + strAmount);

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = editable.toString();
                if (code.length() > 0) {
                    btnApply.setVisibility(View.VISIBLE);
                    //                    btnApply.setBackgroundColor(getResources().getColor(R.color.apply));
                } else {
                    btnApply.setVisibility(View.GONE);
                    //                    btnApply.setBackgroundColor(getResources().getColor(R.color.apply));
                }
                btnApply.setText("Apply");
                btnApply.setBackgroundResource(R.drawable.apply_curve);
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code == 0) {
                    applyCode(etCode.getText().toString().trim(), planPrices.get(position).getPlanPrice(), strAmount);

                } else {
                    code = 0;
                    etCode.setText("");
                    etCode.setEnabled(true);
                    etCode.requestFocus();
                    btnApply.setText("Apply");
//                    btnApply.setBackgroundColor(getResources().getColor(R.color.apply));
                    btnApply.setBackgroundResource(R.drawable.apply_curve);

                    strRefType = "";
//                    tvAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + planPrices.get(position).getPlanPrice());
                    tvFinalAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + strAmount);
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                planId = planPrices.get(position).getPlanId();
                planName = planPrices.get(position).getPlanName();
//                String[] amount = tvAmount.getText().toString().trim().split(" ");
                amount = tvFinalAmount.getText().toString().trim().split(" ");
                getPaymentApiCall(planId, amount[1], planName);
            }
        });
        alertDialog.show();
    }

    private void applyCode(final String refCode, final String planPrice, final String strAmount) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(PlanActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.applycode, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " applyCode response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " applyCode result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");

                            code = 1;
                            etCode.setEnabled(false);
                            btnApply.setText("Remove");
//                            btnApply.setBackgroundColor(getResources().getColor(R.color.remove));
                            btnApply.setBackgroundResource(R.drawable.remove_curve);

//                            tvAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + obj.getString("finalAmount"));
                            tvFinalAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + obj.getString("finalAmount"));
                            strRefType = obj.getString("referralType");

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
                            builder.setMessage(str_message)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            code = 0;
                                            etCode.setText("");
                                            etCode.setEnabled(true);
                                            etCode.requestFocus();
                                            btnApply.setText("Apply");
//                                            btnApply.setBackgroundColor(getResources().getColor(R.color.apply));
                                            btnApply.setBackgroundResource(R.drawable.apply_curve);

                                            strRefType = "";
//                                            tvAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + planPrice);
                                            tvFinalAmount.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)) + "" + strAmount);

                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                            btnOk.setTextColor(Color.parseColor("#000000"));

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(PlanActivity.this, PlanActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    params.put("referralCode", refCode);
                    params.put("planAmount", strAmount);
                    System.out.println(Tag + " applyCode inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PlanActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setPlanPriceView() {
        rootLayout = findViewById(R.id.root_layout);

        for (int i = 0; i < planPrices.size(); i++) {

            LinearLayout parent = new LinearLayout(PlanActivity.this);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parent.setLayoutParams(params);
            if (i == planPrices.size() - 1) {
                params.setMargins(10, 10, 20, 10);
            } else {
                params.setMargins(10, 10, 10, 10);
            }
            parent.setPadding(15, 15, 15, 15);

            parent.setOrientation(LinearLayout.HORIZONTAL);
            parent.setGravity(Gravity.CENTER);

            ImageView imageView = new ImageView(PlanActivity.this);
            imageView.setTextAlignment(ViewGroup.TEXT_ALIGNMENT_CENTER);

            LinearLayout linearLayout = new LinearLayout(PlanActivity.this);
//            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(10, 10, 10, 10);

            TextView tvPlanState = new TextView(PlanActivity.this);
            tvPlanState.setTextSize(11);
            tvPlanState.setTextColor(getResources().getColor(R.color.desc_text));
            tvPlanState.setSingleLine(true);
            tvPlanState.setEllipsize(TextUtils.TruncateAt.END);

            Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_regular);
            Typeface typefaceBold = ResourcesCompat.getFont(this, R.font.roboto_bold);
            tvPlanState.setTypeface(typeface);

            if (isSubcribed) {
                if (planPrices.get(i).isCurrentPlan()) {
                    tvPlanState.setText("Current Plan");
                    parent.setBackground(this.getResources().getDrawable(R.drawable.upgrade_plan_curve));
                    imageView.setImageResource(R.drawable.ic_outline_radio_button_checked_24);
                } else {
                    tvPlanState.setText("Upgrade To");
                    parent.setBackground(this.getResources().getDrawable(R.drawable.current_plan_curve));
                    imageView.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                }
            } else {
                tvPlanState.setText("Buy Now");
                parent.setBackground(this.getResources().getDrawable(R.drawable.current_plan_curve));
                imageView.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
            }
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(10, 0, 10, 0);

            TextView tvPlanName = new TextView(PlanActivity.this);
            tvPlanName.setText(planPrices.get(i).getPlanName());
            tvPlanName.setTextSize(11);
            tvPlanName.setSingleLine(true);
            tvPlanName.setEllipsize(TextUtils.TruncateAt.END);
            tvPlanName.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tvPlanName.setTypeface(typefaceBold);

            linearLayout.addView(tvPlanState);
            linearLayout.addView(tvPlanName);

            LinearLayout newLay1 = new LinearLayout(this);
//            newLay1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            newLay1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            newLay1.setOrientation(LinearLayout.HORIZONTAL);
            TextView tvPlanSymbol = new TextView(this);
            TextView tvPlanPrice = new TextView(this);
            TextView tvPlanValidity = new TextView(this);

            tvPlanPrice.setTypeface(typefaceBold);
            tvPlanValidity.setTypeface(typeface);
            tvPlanSymbol.setTypeface(typeface);

            tvPlanPrice.setSingleLine(true);
            tvPlanPrice.setEllipsize(TextUtils.TruncateAt.END);
            tvPlanSymbol.setSingleLine(true);
            tvPlanSymbol.setEllipsize(TextUtils.TruncateAt.END);
            tvPlanValidity.setSingleLine(true);
            tvPlanValidity.setEllipsize(TextUtils.TruncateAt.END);

            tvPlanSymbol.setText(Html.fromHtml(PlanActivity.this.getResources().getString(R.string.sup)));
            tvPlanSymbol.setTextSize(14);
            tvPlanSymbol.setTextColor(getResources().getColor(R.color.desc_text));

            tvPlanPrice.setText(planPrices.get(i).getPlanPrice());
            tvPlanPrice.setTextSize(18);
            tvPlanPrice.setTextColor(getResources().getColor(R.color.desc_text));
            tvPlanValidity.setText(" / " + planPrices.get(i).getPlanValidity() + " Days");
            tvPlanValidity.setTextSize(14);
            tvPlanValidity.setTextColor(getResources().getColor(R.color.desc_text));

            newLay1.addView(tvPlanSymbol);
            newLay1.addView(tvPlanPrice);
            newLay1.addView(tvPlanValidity);
            linearLayout.addView(newLay1);

            parent.addView(imageView);
            parent.addView(linearLayout);

            final int finalI = i;
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (planPrices.get(finalI).isCurrentPlan()) {
                        String planName = planPrices.get(finalI).getPlanName();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
                        builder.setMessage("Your " + planName + " plan is already subscribed. Upgrade any other plans.")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                        btnOk.setTextColor(Color.parseColor("#000000"));
                    } else {
                        getAmountCalculationFromApi(view, finalI);
//                        showCodeDialog(view, finalI);
                    }
                }
            });

            rootLayout.addView(parent);
        }
    }

    String strAmount = "0", strStaticAmount = "0";

    private void getAmountCalculationFromApi(final View view, final int position) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(PlanActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.amountcalculation, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getAmountCalculationFromApi response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getAmountCalculationFromApi result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");
                            strAmount = obj.getString("amount");
                            strStaticAmount = obj.getString("staticAmount");

                            if (Integer.parseInt(strStaticAmount) > Integer.parseInt(planPrices.get(position).getPlanPrice())) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
                                builder.setMessage("You can't downgrade to a lower plan when a higher plan is currently active. Please select any higher plan. (You can subscribe to a lower plan only after the current plan is expired.)")
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                strAmount = "0";
                                                strStaticAmount = "0";
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                                btnOk.setTextColor(Color.parseColor("#000000"));
                            } else {
                                showCodeDialog(view, position);
                            }

                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(PlanActivity.this, PlanActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    params.put("planId", planPrices.get(position).getPlanId());
                    System.out.println(Tag + " getAmountCalculationFromApi inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PlanActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getPaymentApiCall(final String planId, final String amount, final String planName) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(PlanActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.buynow, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getPaymentApiCall response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getPaymentApiCall result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("Message");
//                            String webUrl = obj.getString("result");
//
//                            Intent intent = new Intent(PlanActivity.this, PayWebViewActivity.class);
//                            intent.putExtra("key", keyIntent);
//                            intent.putExtra("planId", planId);
//                            intent.putExtra("amount", amount);
//                            intent.putExtra("planName", planName);
//                            intent.putExtra("webUrl", webUrl);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();

                            // rounding off the amount.
                            int amountToPay = Math.round(Float.parseFloat(amount) * 100);

                            // initialize Razorpay account.
                            Checkout checkout = new Checkout();

                            // set your id as below
                            checkout.setKeyID(Utilis.razorPayTestKey);
//                            checkout.setKeyID(Utilis.razorPayLiveKey);

                            // initialize json object
                            JSONObject object = new JSONObject();
                            try {
                                // to put name
                                object.put("name", userDetail.getName());

                                // put description
                                object.put("description", "Purchasing subscription package");

                                // to set theme color
                                object.put("theme.color", "");

                                // put the currency
                                object.put("currency", "INR");

                                // put amount
                                object.put("amount", amountToPay);

                                // put mobile number
                                object.put("prefill.contact", userDetail.getMobile());

                                // put email
                                object.put("prefill.email", userDetail.getEmail().isEmpty() || userDetail.getEmail() == null ? "localkartapp@gmail.com" : userDetail.getEmail());

                                // open razorpay to checkout activity
                                checkout.open(PlanActivity.this, object);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (Integer.parseInt(str_result) == 3) {
                            Intent intent = new Intent(PlanActivity.this, PlanActivity.class);
                            intent.putExtra("key", keyIntent);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(PlanActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(PlanActivity.this, PlanActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    params.put("planId", planId);
                    params.put("referalCode", code == 1 ? etCode.getText().toString().trim() : "");
                    params.put("referalType", code == 1 ? strRefType : "");
                    params.put("amount", amount);
                    System.out.println(Tag + " getPaymentApiCall inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PlanActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<HashMap<String, String>> generateListFromJsonObject(JSONObject mainObj) {
        ArrayList<HashMap<String, String>> adapterList = new ArrayList<>();
        HashMap<String, String> list = new HashMap<>();
        try {
            for (int k = 0; k < mainObj.getJSONArray("Headers").length(); k++) {
                list.put(mainObj.getJSONArray("Headers").getString(k), mainObj.getJSONArray("Headers").getString(k));
            }
            adapterList.add(list);
            for (int j = 0; j < mainObj.getJSONArray("BenefitKeys").length(); j++) {
                list = new HashMap<>();
                JSONObject dataObj = mainObj.getJSONObject(mainObj.getJSONArray("BenefitKeys").getString(j));
                for (int k = 0; k < mainObj.getJSONArray("Headers").length(); k++) {
                    list.put(mainObj.getJSONArray("Headers").getString(k), dataObj.getString(mainObj.getJSONArray("Headers").getString(k)));
                }
                adapterList.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adapterList;
    }

    private JSONObject generateDynamicData(String response) {
        JSONObject mainObj = new JSONObject();
        JSONArray headerKeys = new JSONArray();
        JSONArray BenefitKeys = new JSONArray();
        PlanResponse model;

        try {
            headerKeys.put("Benefits");
            JSONObject data_obj = new JSONObject(response);
            model = new Gson().fromJson(String.valueOf(data_obj), PlanResponse.class);
            for (Map.Entry<String, List<PlanDynamicKeys>> entry : model.getResult().entrySet()) {
                String key = entry.getKey();
                headerKeys.put(key);
                for (Map.Entry<String, String> benefits_entry : Objects.requireNonNull(model.getResult().get(key)).get(0).getBenifits().entrySet()) {
                    String benefits_key = benefits_entry.getKey();
                    if (model.getResult().get(key).get(0).getBenifits().keySet().size() != BenefitKeys.length()) {
                        BenefitKeys.put(benefits_key);
                    }
                    if (mainObj.has(benefits_key)) {
                        JSONObject dataObjj = mainObj.getJSONObject(benefits_key);
                        dataObjj.put(key, model.getResult().get(key).get(0).getBenifits().get(benefits_key));
                        mainObj.put(benefits_key, dataObjj);
                    } else {
                        JSONObject dataObjj = new JSONObject();
                        dataObjj.put("Benefits", benefits_key);
                        dataObjj.put(key, model.getResult().get(key).get(0).getBenifits().get(benefits_key));
                        mainObj.put(benefits_key, dataObjj);
                    }

                }
            }
            mainObj.put("Headers", headerKeys);
            mainObj.put("BenefitKeys", BenefitKeys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainObj;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(PlanActivity.this, SubscriptionActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentSuccess(String s) {
        paymentSuccessUpdate(s);
    }

    private void paymentSuccessUpdate(final String paymentId) {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(PlanActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.paymentsuccess, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " paymentSuccessUpdate response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " paymentSuccessUpdate result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            str_message = obj.getString("Message");

                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlanActivity.this, R.style.CustomAlertDialog);
                            ViewGroup viewGroup = findViewById(android.R.id.content);
                            View dialogView = LayoutInflater.from(PlanActivity.this).inflate(R.layout.alert_payment_successful, viewGroup, false);
                            TextView tvContent = dialogView.findViewById(R.id.tv_content);
                            String textContent = "An amount of <b> Rs. " + amount[1] + "</b> has been paid successfully towards subscription charges for the plan <b>" + planName + "</b> and is active now.";
                            tvContent.setText(Html.fromHtml(textContent));
                            Button btnOk = dialogView.findViewById(R.id.btn_ok);
                            builder.setView(dialogView);
                            builder.setCancelable(false);
                            final android.app.AlertDialog alertDialog = builder.create();
                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    Intent intent = new Intent(PlanActivity.this, PlanActivity.class);
                                    intent.putExtra("key", keyIntent);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            alertDialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(PlanActivity.this, PlanActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("userIndexId", userDetail.getId());
                    params.put("planId", planId);
                    params.put("payment_id", paymentId);
                    params.put("amount", amount[1]);
                    params.put("referalCode", code == 1 ? etCode.getText().toString().trim() : "");
                    params.put("referalType", code == 1 ? strRefType : "");
                    System.out.println(Tag + " paymentSuccessUpdate inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, PlanActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
//        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
        try {
            JSONObject obj = new JSONObject(s);
            JSONObject json = obj.getJSONObject("error");
            String description = json.getString("description");

            AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
            builder.setTitle("Payment Failed")
                    .setMessage(description)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            Button btnOk = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
            btnOk.setTextColor(Color.parseColor("#000000"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}