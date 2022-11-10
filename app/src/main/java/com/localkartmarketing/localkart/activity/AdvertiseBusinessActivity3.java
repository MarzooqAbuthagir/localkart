package com.localkartmarketing.localkart.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.alimuzaffar.lib.pin.PinEntryEditText;
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
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.model.ContactDetailsData;
import com.localkartmarketing.localkart.support.GenericTextWatcher;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdvertiseBusinessActivity3 extends AppCompatActivity {
    private String Tag = "AdvertiseBusinessActivity3";
    Utils utils;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", strBusinessId = "";
    String strPhoneNo = "", strMobileNo = "", strAltNo = "", strWhatsAppNo = "", strEmailId = "", strWebsite = "", strFacebook = "", strVcard = "", strCod = "";

    EditText etPhoneNo;
    EditText etMobileNo;
    EditText etAltNo;
    EditText etWhatsAppNo;
    EditText etEmail;
    EditText etWebsite;
    EditText etFacebook;
    EditText etVcard;
    EditText etCod;
    boolean isAllFieldsChecked = false;

    Button btnVerify, btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_business3);

        utils = new Utils(AdvertiseBusinessActivity3.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        strBusinessId = intent.getStringExtra("businessType");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AdvertiseBusinessActivity3.this, R.color.colorPrimaryDark));

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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(500, 10);
        progressView.setLayoutParams(lp);

        etPhoneNo = findViewById(R.id.et_phone_no);
        etMobileNo = findViewById(R.id.et_mobile_no);
        etAltNo = findViewById(R.id.et_alt_num);
        etWhatsAppNo = findViewById(R.id.et_whatsapp_no);
        etEmail = findViewById(R.id.et_email);
        etWebsite = findViewById(R.id.et_website);
        etFacebook = findViewById(R.id.et_facebook);
        etVcard = findViewById(R.id.et_vcard);
        etCod = findViewById(R.id.et_cod);

        Button btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

//        etPhoneNo.setText("04312714632");
//        etMobileNo.setText("9566596901");
//        etAltNo.setText("9789716833");
//        etWhatsAppNo.setText("9566596901");
//        etEmail.setText("abuthagiryuvraj@gmail.com");
//        etWebsite.setText("www.google.com");
//        etFacebook.setText("www.google.com");
//        etVcard.setText("www.google.com");

        ContactDetailsData cd = Utils.getContactDetails(AdvertiseBusinessActivity3.this);
        if (cd != null) {
            etPhoneNo.setText(cd.getPhoneNo());
            etMobileNo.setText(cd.getMobileNo());
            etAltNo.setText(cd.getAltNo());
            etWhatsAppNo.setText(cd.getWhatsappNo());
            etEmail.setText(cd.getEmailId());
            etWebsite.setText(cd.getWebsite());
            etFacebook.setText(cd.getFacebook());
            etVcard.setText(cd.getVcard());
            etCod.setText(cd.getCod());
        }

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPhoneNo = etPhoneNo.getText().toString().trim();
                strMobileNo = etMobileNo.getText().toString().trim();
                strAltNo = etAltNo.getText().toString().trim();
                strWhatsAppNo = etWhatsAppNo.getText().toString().trim();
                strEmailId = etEmail.getText().toString().trim();
                strWebsite = etWebsite.getText().toString().trim();
                strFacebook = etFacebook.getText().toString().trim();
                strVcard = etVcard.getText().toString().trim();
                strCod = etCod.getText().toString().trim();

                isAllFieldsChecked = validateString();

                if (isAllFieldsChecked) {
                    ContactDetailsData contactDetailsData = new ContactDetailsData(
                            strPhoneNo,
                            strMobileNo,
                            strAltNo,
                            strWhatsAppNo,
                            strEmailId,
                            strWebsite,
                            strFacebook,
                            strVcard,
                            strCod
                    );
                    Utils.saveContactDetails(contactDetailsData);
                    Intent intent = new Intent(AdvertiseBusinessActivity3.this, AdvertiseBusinessActivity4.class);
                    intent.putExtra("key", keyIntent);
                    intent.putExtra("businessType", strBusinessId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });

        btnVerify = findViewById(R.id.btn_verify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strMobileNo = etMobileNo.getText().toString().trim();
                if (strMobileNo.isEmpty()) {
                    etMobileNo.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity3.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                } else if (strMobileNo.length() < 10) {
                    etMobileNo.requestFocus();
                    Toast.makeText(AdvertiseBusinessActivity3.this, "Enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    verifyBusRegMobNumb();
                }
            }
        });

        btnChange = findViewById(R.id.btn_change);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etMobileNo.setEnabled(true);
                etMobileNo.setText("");
                strMobileNo = "";
                Utils.setBusRegMobNum(false);
                btnVerify.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
            }
        });

        toggleButton();
    }

    private void toggleButton() {
        if (!Utils.getBusRegMobNum(AdvertiseBusinessActivity3.this)) {
            btnVerify.setVisibility(View.VISIBLE);
            btnChange.setVisibility(View.GONE);
        } else {
            btnVerify.setVisibility(View.GONE);
            btnChange.setVisibility(View.VISIBLE);
            etMobileNo.setEnabled(false);
        }
    }

    String str_result = "", str_message = "", str_otp = "";

    private void verifyBusRegMobNumb() {
        if (Utils.isInternetOn()) {
            Utils.showProgress(AdvertiseBusinessActivity3.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.Api + Utils.sendotpnew, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " verifyBusRegMobNumb response - " + response);

                        Utils.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " verifyBusRegMobNumb result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            str_message = obj.getString("message");
                            str_otp = obj.getString("otp");

                            alertOtp(str_otp);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity3.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(AdvertiseBusinessActivity3.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utils.dismissProgress();
                    Toast.makeText(AdvertiseBusinessActivity3.this, AdvertiseBusinessActivity3.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("Phone", strMobileNo);
                    System.out.println(Tag + " verifyBusRegMobNumb inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, AdvertiseBusinessActivity3.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog dialog;

    private void alertOtp(final String otpCode) {
        dialog = new Dialog(AdvertiseBusinessActivity3.this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setCancelable(true);
        dialog.getWindow().setContentView(R.layout.alert_otp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

//        Toast.makeText(this, str_otp, Toast.LENGTH_SHORT).show();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        final TextView tvResend = dialog.findViewById(R.id.tv_resend);
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvResend.getText().toString().trim().equalsIgnoreCase("Resend OTP")) {
                    dialog.dismiss();
                    verifyBusRegMobNumb();
                }
            }
        });

        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResend.setTextColor(Color.parseColor("#7F7F7F"));
                tvResend.setText("Resend OTP in " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                tvResend.setTextColor(Color.parseColor("#E4287C"));
                tvResend.setText("Resend OTP");
            }

        }.start();

        final PinEntryEditText pinEntry = dialog.findViewById(R.id.txt_pin_entry);
        pinEntry.requestFocus();
        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    if (str.toString().equalsIgnoreCase(otpCode)) {

                        dialog.dismiss();
                        Utils.setBusRegMobNum(true);
                        toggleButton();

                    } else {
                        Toast.makeText(AdvertiseBusinessActivity3.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                        pinEntry.setText(null);
                    }
                }
            });
        }

        final EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four;
        otp_textbox_one = dialog.findViewById(R.id.otp_edit_box1);
        otp_textbox_one.setSelection(0);
        otp_textbox_one.requestFocus();
        otp_textbox_two = dialog.findViewById(R.id.otp_edit_box2);
        otp_textbox_three = dialog.findViewById(R.id.otp_edit_box3);
        otp_textbox_four = dialog.findViewById(R.id.otp_edit_box4);

        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));

        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = otp_textbox_one.getText().toString().trim() + otp_textbox_two.getText().toString().trim() + otp_textbox_three.getText().toString().trim() + otp_textbox_four.getText().toString().trim();

                if (str.equalsIgnoreCase(otpCode)) {

                    dialog.dismiss();
                    Utils.setBusRegMobNum(true);
                    toggleButton();
                } else {
                    Toast.makeText(AdvertiseBusinessActivity3.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean validateString() {
        if (strMobileNo.isEmpty()) {
            etMobileNo.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strMobileNo.length() < 10) {
            etMobileNo.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Utils.getBusRegMobNum(AdvertiseBusinessActivity3.this)) {
            Toast.makeText(this, "Please verify mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strAltNo.length() > 0 && strAltNo.length() < 10) {
            etAltNo.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter 10 digit alternate / appointment number", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (strWhatsAppNo.isEmpty()) {
//            etWhatsAppNo.requestFocus();
//            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter whatsApp number", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (strCod.length() > 0 && strCod.length() < 10) {
            etCod.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter 10 digit cod number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strWhatsAppNo.length() > 0 && strWhatsAppNo.length() < 10) {
            etWhatsAppNo.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter 10 digit WhatsApp number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strEmailId.length() > 0 && Utils.eMailValidation(strEmailId)) {
            etEmail.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strWebsite.length() > 0 && Utils.webURLValidation(strWebsite)) {
            etWebsite.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter valid website address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strFacebook.length() > 0 && Utils.webURLValidation(strFacebook)) {
            etFacebook.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter valid facebook URL", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (strVcard.length() > 0 && Utils.webURLValidation(strVcard)) {
            etVcard.requestFocus();
            Toast.makeText(AdvertiseBusinessActivity3.this, "Enter valid digital vcard", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        strPhoneNo = etPhoneNo.getText().toString().trim();
        strMobileNo = etMobileNo.getText().toString().trim();
        strAltNo = etAltNo.getText().toString().trim();
        strWhatsAppNo = etWhatsAppNo.getText().toString().trim();
        strEmailId = etEmail.getText().toString().trim();
        strWebsite = etWebsite.getText().toString().trim();
        strFacebook = etFacebook.getText().toString().trim();
        strVcard = etVcard.getText().toString().trim();
        strCod = etCod.getText().toString().trim();
        ContactDetailsData contactDetailsData = new ContactDetailsData(
                strPhoneNo,
                strMobileNo,
                strAltNo,
                strWhatsAppNo,
                strEmailId,
                strWebsite,
                strFacebook,
                strVcard,
                strCod
        );
        Utils.saveContactDetails(contactDetailsData);
        finish();
    }
}