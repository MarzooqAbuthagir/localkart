package com.localkartmarketing.localkart.support;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDex;

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
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    public static final String channelId = "com.localkartmarketing.localkart";
    Utilis utilis;
    public static final String TAG = App.class.getSimpleName();
    private static Context context;
    private static App mInstance;
    String str_result = "";

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        mInstance = this;

        utilis = new Utilis(this);
        context = getApplicationContext();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Example",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public void initMethod(final String indexId) {

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();


        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                System.out.println("MyApplication userID " + userId);
                System.out.println("MyUser IndexID" + indexId);

                sendUserID(userId, indexId);

                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);
            }
        });
    }

    private void sendUserID(final String deviceId, final String indexId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.updatedeviceid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(TAG + " sendUserID response - " + response);

                    str_result = obj.getString("errorCode");
                    System.out.print(TAG + " sendUserID result" + str_result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                params.put("deviceId", deviceId);
                params.put("userIndexId", indexId);
                System.out.println(TAG + " sendUserID inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void notifyToUsers(final String strPostIndexId, final String strShopIndexId, final String strShopType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utilis.Api + Utilis.sendpush, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(response);

                    System.out.println(TAG + " notifyToUsers response - " + response);

                    str_result = obj.getString("errorCode");
                    System.out.print(TAG + " notifyToUsers result" + str_result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                params.put("shopId", strShopIndexId);
                params.put("shopType", strShopType);
                params.put("postIndexId", strPostIndexId);
                System.out.println(TAG + " notifyToUsers inputs " + params);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
