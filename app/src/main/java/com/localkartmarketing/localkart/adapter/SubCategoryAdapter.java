package com.localkartmarketing.localkart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.localkartmarketing.localkart.activity.DealsActivity;
import com.localkartmarketing.localkart.activity.MainActivity;
import com.localkartmarketing.localkart.model.SilderData;
import com.localkartmarketing.localkart.model.SubCategoryData;
import com.localkartmarketing.localkart.support.RegBusinessSharedPrefrence;
import com.localkartmarketing.localkart.support.Utils;
import com.localkartmarketing.localkart.support.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubCategoryAdapter extends ArrayAdapter<SubCategoryData> {
    private String Tag = "SubCategoryAdapter";
    private final Context con;
    private final String identity;
    private final String catId;
    private final String catName;
    Utils utils;

    public SubCategoryAdapter(Context context, ArrayList<SubCategoryData> subCategoryDataArrayList, String btnId, String categoryId, String categoryName) {
        super(context, 0, subCategoryDataArrayList);
        con = context;
        identity = btnId;
        catId = categoryId;
        catName = categoryName;
        utils = new Utils(con);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.category_layout, parent, false);
        }
        final SubCategoryData subCategoryData = getItem(position);
        TextView categoryName = listitemView.findViewById(R.id.tv_categoryName);
        ImageView categoryImg = listitemView.findViewById(R.id.iv_categoryImg);
        assert subCategoryData != null;
//        System.out.println("category image "+subCategoryData.getSubCategoryImage());
        categoryName.setText(subCategoryData.getSubCategoryName());
        Glide.with(con).load(subCategoryData.getSubCategoryImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(categoryImg);
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String menuFlag = RegBusinessSharedPrefrence.getMenuFlag(con);
                if (menuFlag.equalsIgnoreCase("0")) {
                    Utils.callResume = 0;
                    Utils.clearFilterPref(con);
                    Intent intent = new Intent(con, DealsActivity.class);
                    intent.putExtra("key", identity);
                    intent.putExtra("categoryId", catId);
                    intent.putExtra("categoryName", catName);
                    intent.putExtra("subcategoryName", subCategoryData.getSubCategoryName());
                    intent.putExtra("subcategoryId", subCategoryData.getSubCategoryId());
                    intent.putExtra("megasalesIndexId", "");
                    intent.putExtra("offerTitle", "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    con.startActivity(intent);
                    ((Activity) con).finish();
                } else {
                    getDynamicMenu(subCategoryData.getSubCategoryName(), subCategoryData.getSubCategoryId());
                }
            }
        });
        return listitemView;
    }

    String str_result = "", str_message = "";

    private void getDynamicMenu(final String subCategoryName, final String subCategoryId) {
        if (Utils.isInternetOn()) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utils.Api + Utils.currentmegasales, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        System.out.println(Tag + " getDynamicMenu response - " + response);

                        str_result = obj.getString("errorCode");
                        System.out.print(Tag + " getDynamicMenu result " + str_result);

                        if (Integer.parseInt(str_result) == 0) {

                            JSONObject json = obj.getJSONObject("result");
                            String megasalesIndexId = json.getString("megasalesIndexId");
                            String offerTitle = json.getString("offerTitle");

                            Utils.callResume = 0;
                            Utils.clearFilterPref(con);
                            Intent intent = new Intent(con, DealsActivity.class);
                            intent.putExtra("key", identity);
                            intent.putExtra("categoryId", catId);
                            intent.putExtra("categoryName", catName);
                            intent.putExtra("subcategoryName", subCategoryName);
                            intent.putExtra("subcategoryId", subCategoryId);
                            intent.putExtra("megasalesIndexId", megasalesIndexId);
                            intent.putExtra("offerTitle", offerTitle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            con.startActivity(intent);
                            ((Activity) con).finish();

                        } else {
                            Utils.callResume = 0;
                            Utils.clearFilterPref(con);
                            Intent intent = new Intent(con, DealsActivity.class);
                            intent.putExtra("key", identity);
                            intent.putExtra("categoryId", catId);
                            intent.putExtra("categoryName", catName);
                            intent.putExtra("subcategoryName", subCategoryName);
                            intent.putExtra("subcategoryId", subCategoryId);
                            intent.putExtra("megasalesIndexId", "");
                            intent.putExtra("offerTitle", "");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            con.startActivity(intent);
                            ((Activity) con).finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(con, con.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
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
            VolleySingleton.getInstance(con).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(con, con.getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
    }
}
