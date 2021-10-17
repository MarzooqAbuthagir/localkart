package com.kart.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.gson.Gson;
import com.kart.R;
import com.kart.model.DistrictData;
import com.kart.model.StateData;
import com.kart.model.UserDetail;
import com.kart.support.App;
import com.kart.support.GenericTextWatcher;
import com.kart.support.LoginSharedPreference;
import com.kart.support.RegBusinessIdSharedPreference;
import com.kart.support.RegBusinessSharedPrefrence;
import com.kart.support.RegBusinessTypeSharedPreference;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private String Tag = "RegisterActivity";
    private EditText etName;
    private EditText etMobile;
    private EditText etRefCode;
    private SearchableSpinner spinState;
    private ArrayList<StateData> stateListValue = new ArrayList<StateData>();
    private List<String> stateSpinnerValue = new ArrayList<>();
    private SearchableSpinner spinDistrict;
    private ArrayList<DistrictData> districtListValue = new ArrayList<DistrictData>();
    private List<String> districtSpinnerValue = new ArrayList<>();
    private String str_result = "";
    private String str_message = "";
    private String str_otp = "";
    private String strName = "";
    private String strMobile = "";
    private String strRefCode = "";
    private String strStateId = "";
    private String strDistrictId = "";

    private Dialog dialog;

    Utilis utilis;
    SharedPreferences mPrefs;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        utilis = new Utilis(RegisterActivity.this);
        mPrefs = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);

        app = (App) getApplication();

        etName = findViewById(R.id.et_name);
        etMobile = findViewById(R.id.et_mobile);
        etRefCode = findViewById(R.id.et_ref_code);
        spinState = findViewById(R.id.spin_state);
        spinState.setTitle("Select State");
        spinDistrict = findViewById(R.id.spin_district);
        spinDistrict.setTitle("Select District / Zone");
        final CheckBox checkBox = findViewById(R.id.checkbox);
        TextView tvUserTC = findViewById(R.id.tv_user_tc);
        tvUserTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, UserTCActivity.class));
            }
        });
        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strName = etName.getText().toString().trim();
                strMobile = etMobile.getText().toString().trim();
                strRefCode = etRefCode.getText().toString().trim();

                if (strName.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                } else if (strName.length() < 4) {
                    Toast.makeText(RegisterActivity.this, "Name atleast 4 characters", Toast.LENGTH_SHORT).show();
                } else if (strMobile.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                } else if (strMobile.length() < 10) {
                    Toast.makeText(RegisterActivity.this, "Enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                } else if (strStateId.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Select state", Toast.LENGTH_SHORT).show();
                } else if (strDistrictId.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Select district / zone", Toast.LENGTH_SHORT).show();
                } else if (!checkBox.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Please accept Terms and Conditions.", Toast.LENGTH_SHORT).show();
                }  else {
                    sendOtp();
                }

            }
        });

        getStateList();

        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    spinDistrict.setEnabled(true);
                    strStateId = stateListValue.get(i).getStateId();
                    getDistrictList();
                } else {
                    districtListValue.clear();
                    spinDistrict.setEnabled(false);
                    strStateId = "";
                    DistrictData initDistrictData = new DistrictData();
                    initDistrictData.setDistrictId("-1");
                    initDistrictData.setDistrictName("District / Zone");
                    districtListValue.add(0, initDistrictData);

                    districtSpinnerValue.add(districtListValue.get(0).getDistrictName());

                    ArrayAdapter arrayAdapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_item, districtSpinnerValue);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    spinDistrict.setAdapter(arrayAdapter);
                    spinDistrict.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    strDistrictId = districtListValue.get(i).getDistrictId();
                } else {
                    strDistrictId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sendOtp() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RegisterActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.sendotp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " sendOtp response - " + response);

                        Utilis.dismissProgress();

                        View view = getCurrentFocus();
                        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert methodManager != null && view != null;
                        methodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " sendOtp result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            str_message = obj.getString("message");
                            str_otp = obj.getString("otp");

                            alertOtp(str_otp);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("Phone", strMobile);
                    System.out.println(Tag + " sendOtp inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, RegisterActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void alertOtp(final String str_otp) {
        dialog = new Dialog(RegisterActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setCancelable(true);
        dialog.getWindow().setContentView(R.layout.alert_otp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        Toast.makeText(this, str_otp, Toast.LENGTH_SHORT).show();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        final TextView tvResend = dialog.findViewById(R.id.tv_resend);
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvResend.getText().toString().trim().equalsIgnoreCase("Resend OTP")) {
                    dialog.dismiss();
                    sendOtp();
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
                    if (str.toString().equalsIgnoreCase(str_otp)) {

                        dialog.dismiss();

                        register();


                    } else {
                        Toast.makeText(RegisterActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
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
                if (str.equalsIgnoreCase(str_otp)) {

                    dialog.dismiss();

                    register();

                } else {
                    Toast.makeText(RegisterActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RegisterActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.signup, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " register response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " register result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            str_message = obj.getString("message");

                            JSONObject json = obj.getJSONObject("result");
                            UserDetail userDetail = new UserDetail();
                            userDetail.setId(json.getString("Id"));
                            userDetail.setMobile(json.getString("Phone"));
                            userDetail.setName(json.getString("Name"));
                            userDetail.setStateId(strStateId);
                            userDetail.setDistrictId(strDistrictId);
                            userDetail.setEmail("");
                            userDetail.setImage("");

                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            String jsonPref = gson.toJson(userDetail);
                            prefsEditor.putString("MyObject", jsonPref);
                            prefsEditor.apply();

                            LoginSharedPreference.setLoggedIn(RegisterActivity.this, true);

                            RegBusinessSharedPrefrence.setMenuFlag(RegisterActivity.this, "0");

                            RegBusinessTypeSharedPreference.setBusinessType(RegisterActivity.this, "");
                            RegBusinessIdSharedPreference.setBusinessId(RegisterActivity.this, "");

                            app.initMethod(userDetail.getId());

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("key", "Shopping");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("Phone", strMobile);
                    params.put("Name", strName);
                    params.put("stateId", strStateId);
                    params.put("districtId", strDistrictId);
                    params.put("referal", strRefCode);
                    System.out.println(Tag + " register inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, RegisterActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDistrictList() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RegisterActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.districtList, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getDistrictList response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getDistrictList result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            districtListValue.clear();
                            str_message = obj.getString("message");

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                DistrictData districtData = new DistrictData(
                                        jsonObject.getString("districtId"),
                                        jsonObject.getString("districtName"));

                                districtListValue.add(districtData);
                            }

                            DistrictData initDistrictData = new DistrictData();
                            initDistrictData.setDistrictId("-1");
                            initDistrictData.setDistrictName("District / Zone");
                            districtListValue.add(0, initDistrictData);

                            districtSpinnerValue.clear();
                            for (int i = 0; i < districtListValue.size(); i++) {
                                districtSpinnerValue.add(districtListValue.get(i).getDistrictName());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_item, districtSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinDistrict.setAdapter(arrayAdapter);
                            spinDistrict.setSelection(0);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    params.put("stateId", strStateId);
                    System.out.println(Tag + " getDistrictList inputs " + params);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, RegisterActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getStateList() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(RegisterActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utilis.Api + Utilis.stateList, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getStateList response - " + response);

                        Utilis.dismissProgress();

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getStateList result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {
                            stateListValue.clear();
                            str_message = obj.getString("message");

                            JSONArray json = obj.getJSONArray("result");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = json.getJSONObject(i);
                                StateData stateData = new StateData(
                                        jsonObject.getString("stateId"),
                                        jsonObject.getString("stateName"));

                                stateListValue.add(stateData);
                            }

                            StateData initStateData = new StateData();
                            initStateData.setStateId("-1");
                            initStateData.setStateName("State");
                            stateListValue.add(0, initStateData);

                            stateSpinnerValue.clear();
                            for (int i = 0; i < stateListValue.size(); i++) {
                                stateSpinnerValue.add(stateListValue.get(i).getStateName());
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_item, stateSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinState.setAdapter(arrayAdapter);
                            spinState.setSelection(0);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(RegisterActivity.this, str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(RegisterActivity.this, RegisterActivity.this.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                    return new HashMap<>();
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, RegisterActivity.this.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}