package com.kart.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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
import com.kart.R;
import com.kart.adapter.DealsAdapter;
import com.kart.model.AccessOptions;
import com.kart.model.DealsOfferData;
import com.kart.model.DistrictData;
import com.kart.model.StateData;
import com.kart.model.UserDetail;
import com.kart.support.LocationTrack;
import com.kart.support.Utilis;
import com.kart.support.VolleySingleton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayActivity extends Fragment {
    private String Tag = "TodayActivity";

    Utilis utilis;

    LocationTrack currentLocation;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CODE = 101;

    RecyclerView recyclerView;
    TextView tvNoRecords;

    String strType = "", strCatId = "", strSubCatId = "", strSubCatName = "";
    LinearLayout layNearMe, layLocation;
    Button btnNearMe, btnLocation;
    ImageView ivNearMe, ivLocation;

    private SearchableSpinner spinState;
    private ArrayList<StateData> stateListValue = new ArrayList<StateData>();
    private List<String> stateSpinnerValue = new ArrayList<>();

    private SearchableSpinner spinDistrict;
    private ArrayList<DistrictData> districtListValue = new ArrayList<DistrictData>();
    private List<String> districtSpinnerValue = new ArrayList<>();

    private String strStateId = "";
    private String strDistrictId = "";
    String str_result = "", str_message = "";
    private int strProgress = 0;

    UserDetail userDetail;
    static SharedPreferences mPrefs;

    List<DealsOfferData> dataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_today, container, false);
        utilis = new Utilis(getActivity());

        mPrefs = getActivity().getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userDetail = gson.fromJson(json, UserDetail.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            strType = bundle.getString("type");
            strCatId = bundle.getString("catId");
            strSubCatId = bundle.getString("subcatId");
            strSubCatName = bundle.getString("subcatName");
        }

        recyclerView = rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);

        tvNoRecords = rootView.findViewById(R.id.tv_no_records);
        layLocation = rootView.findViewById(R.id.lay_location);
        btnLocation = rootView.findViewById(R.id.btn_location);
        ivLocation = rootView.findViewById(R.id.img_location);
        layNearMe = rootView.findViewById(R.id.lay_near_me);
        btnNearMe = rootView.findViewById(R.id.btn_near_me);
        ivNearMe = rootView.findViewById(R.id.img_near_me);

        layNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearMeDialog(view);
            }
        });

        btnNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearMeDialog(view);
            }
        });

        ivNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearMeDialog(view);
            }
        });

        layLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDialog(view);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDialog(view);
            }
        });

        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDialog(view);
            }
        });
        return rootView;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null && menuVisible) {
            fetchLastLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utilis.callResume == 1 && Utilis.constPostType.equalsIgnoreCase("TODAY")) {
            getListData();
        }
    }

    private void nearMeDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_near_me, viewGroup, false);

        TextView tvHeading = dialogView.findViewById(R.id.tv_heading);
        tvHeading.setText("Show " + strSubCatName + " within radius of Kilometer");

        BubbleSeekBar bubbleSeekBar = dialogView.findViewById(R.id.seek_bar);
        strProgress = Utilis.getNearMeFilter(getActivity());
        bubbleSeekBar.setProgress(strProgress);
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                strProgress = progress;
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
            }
        });

        final Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilis.isInternetOn()) {
                    Utilis.saveNearMeFilter(strProgress);
                    alertDialog.dismiss();
                    getListData();
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    private void locationDialog(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_location, viewGroup, false);

        spinState = dialogView.findViewById(R.id.spin_state);
        spinDistrict = dialogView.findViewById(R.id.spin_district);

        spinState.setTitle("Select State");
        spinDistrict.setTitle("Select District");

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
                    initDistrictData.setDistrictName("District");
                    districtListValue.add(0, initDistrictData);

                    districtSpinnerValue.add(districtListValue.get(0).getDistrictName());

                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, districtSpinnerValue);
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

        final Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strStateId.isEmpty()) {
                    Toast.makeText(getActivity(), "Select state", Toast.LENGTH_SHORT).show();
                } else if (strDistrictId.isEmpty()) {
                    Toast.makeText(getActivity(), "Select district", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utilis.isInternetOn()) {
                        Utilis.saveStateFilter(strStateId);
                        Utilis.saveDistrictFilter(strDistrictId);
                        alertDialog.dismiss();
                        getListData();
                    } else {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        alertDialog.show();
    }

    private void getListData() {
        Utilis.showProgress(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.todaylist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " getListData response - " + response);

                    Utilis.dismissProgress();

                    str_result = obj.getString("errorCode");
                    System.out.print(Tag + " getListData result " + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        dataList.clear();

                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoRecords.setVisibility(View.GONE);

                        JSONArray json = obj.getJSONArray("result");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonObject = json.getJSONObject(i);
                            DealsOfferData dealsOfferData = new DealsOfferData();

                            dealsOfferData.setPostIndexId(jsonObject.getString("postIndexId"));
                            dealsOfferData.setFromDate(jsonObject.getString("fromDate"));
                            dealsOfferData.setToDate(jsonObject.getString("toDate"));
                            dealsOfferData.setName(jsonObject.getString("name"));
                            dealsOfferData.setLogo(jsonObject.getString("logo"));
                            dealsOfferData.setDescription(jsonObject.getString("description"));
                            dealsOfferData.setOfferCount(jsonObject.getString("count"));
                            dealsOfferData.setDistance(jsonObject.getString("distance"));
                            dealsOfferData.setShopIndexId(jsonObject.getString("shopIndexId"));
                            dealsOfferData.setType(jsonObject.getString("type"));
                            dealsOfferData.setLatitude(jsonObject.getString("latitude"));
                            dealsOfferData.setLongitude(jsonObject.getString("longitude"));
                            dealsOfferData.setIsSubscribed(jsonObject.getString("isSubscribed"));
                            dealsOfferData.setOfferHeading(jsonObject.getString("offerHeading"));
                            dealsOfferData.setOffferDesc(jsonObject.getString("offerDescription"));

                            JSONObject js = jsonObject.getJSONObject("accessOptions");
                            AccessOptions accessOptions = new AccessOptions(
                                    js.getString("key"),
                                    js.getString("value")
                            );
                            dealsOfferData.setAccessOptions(accessOptions);
                            dataList.add(dealsOfferData);
                        }

                        DealsAdapter adapter = new DealsAdapter(getActivity(), dataList, strType, 0, latitude, longitude, "TODAY");
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new DealsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {

                                if (Utilis.isInternetOn()) {
                                    String state = Integer.parseInt(dataList.get(position).getIsSubscribed()) == 0 ? "Subscribe" : "UnSubscribe";
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                    builder.setTitle("Confirmation")
                                            .setMessage("Are you sure want to " + state + " the shop?")
                                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing but close the dialog
                                                    dialog.dismiss();

                                                    if (Integer.parseInt(dataList.get(position).getIsSubscribed()) == 0) {
                                                        subscribeShop(position);
                                                    } else {
                                                        unsubscribeShop(position);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Do nothing
                                                    dialog.dismiss();
                                                }
                                            });

                                    androidx.appcompat.app.AlertDialog alert = builder.create();
                                    alert.show();

                                    Button btn_yes = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button btn_no = alert.getButton(DialogInterface.BUTTON_NEGATIVE);

                                    btn_no.setTextColor(Color.parseColor("#000000"));
                                    btn_yes.setTextColor(Color.parseColor("#000000"));

                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("message");
                        Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("message");
                        Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                        recyclerView.setVisibility(View.GONE);
                        tvNoRecords.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utilis.dismissProgress();
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("userIndexId", userDetail.getId());
                params.put("type", strType);
                params.put("catId", strCatId);
                params.put("subCatId", strSubCatId);
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("radius", String.valueOf(strProgress));
                params.put("stateId", Utilis.getStateFilter(getActivity()).isEmpty() ? userDetail.getStateId() : Utilis.getStateFilter(getActivity()));
                params.put("districtId", Utilis.getDistrictFilter(getActivity()).isEmpty() ? userDetail.getDistrictId() : Utilis.getDistrictFilter(getActivity()));
                System.out.println(Tag + " getListData inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void unsubscribeShop(final int position) {
        Utilis.showProgress(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.unsubscribe, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " unsubscribeShop response - " + response);

                    Utilis.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " unsubscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");
                        getListData();

                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utilis.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", dataList.get(position).getType());
                params.put("shopId", dataList.get(position).getShopIndexId());
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " unsubscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void subscribeShop(final int position) {
        Utilis.showProgress(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.savesubscribers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(Tag + " subscribeShop response - " + response);

                    Utilis.dismissProgress();

                    String str_result = obj.getString("errorCode");
                    String str_message = "";
                    System.out.print(Tag + " subscribeShop result" + str_result);

                    if (Integer.parseInt(str_result) == 0) {
                        str_message = obj.getString("Message");

                        getListData();
                    } else if (Integer.parseInt(str_result) == 2) {
                        str_message = obj.getString("Message");
                        Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();

                    } else if (Integer.parseInt(str_result) == 1) {
                        str_message = obj.getString("Message");
                        Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilis.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
                params.put("shopType", dataList.get(position).getType());
                params.put("shopId", dataList.get(position).getShopIndexId());
                params.put("userIndexId", userDetail.getId());
                System.out.println(Tag + " subscribeShop inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void getDistrictList() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(getActivity());

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
                            initDistrictData.setDistrictName("District");
                            districtListValue.add(0, initDistrictData);

                            districtSpinnerValue.clear();
                            for (int i = 0; i < districtListValue.size(); i++) {
                                districtSpinnerValue.add(districtListValue.get(i).getDistrictName());
                            }

                            String prefDistrictId = Utilis.getDistrictFilter(getActivity()).isEmpty() ? userDetail.getDistrictId() : Utilis.getDistrictFilter(getActivity());
                            int prefPos = 0;
                            for (int i = 0; i < districtListValue.size(); i++) {
                                if (districtListValue.get(i).getDistrictId().equalsIgnoreCase(prefDistrictId)) {
                                    prefPos = i;
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, districtSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinDistrict.setAdapter(arrayAdapter);
                            spinDistrict.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void getStateList() {
        if (Utilis.isInternetOn()) {
            Utilis.showProgress(getActivity());

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

                            String prefStateId = Utilis.getStateFilter(getActivity()).isEmpty() ? userDetail.getStateId() : Utilis.getStateFilter(getActivity());
                            int prefPos = 0;
                            for (int i = 0; i < stateListValue.size(); i++) {
                                if (stateListValue.get(i).getStateId().equalsIgnoreCase(prefStateId)) {
                                    prefPos = i;
                                    break;
                                }
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, stateSpinnerValue);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinState.setAdapter(arrayAdapter);
                            spinState.setSelection(prefPos);

                        } else if (Integer.parseInt(str_result) == 2) {
                            str_message = obj.getString("message");
                            Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(str_result) == 1) {
                            str_message = obj.getString("message");
                            Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utilis.dismissProgress();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        if (Utilis.isGpsOn()) {
            currentLocation = new LocationTrack(getActivity());

            if (currentLocation.canGetLocation()) {
                longitude = currentLocation.getLongitude();
                latitude = currentLocation.getLatitude();
                System.out.println("latitude " + latitude + " and longitude " + longitude);

                if (Utilis.isInternetOn()) {
                    getListData();
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(getActivity(), "Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission Granted");
                if (Utilis.isGpsOn()) {
                    fetchLastLocation();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Utilis.isGpsOn()) {
                fetchLastLocation();
            }
        }

    }
}