package com.localkartmarketing.localkart.support;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.adapter.OfferData;
import com.localkartmarketing.localkart.model.AddOfferData;
import com.localkartmarketing.localkart.model.AddressDetailsData;
import com.localkartmarketing.localkart.model.BasicDetailsData;
import com.localkartmarketing.localkart.model.ContactDetailsData;
import com.localkartmarketing.localkart.model.LocationData;
import com.localkartmarketing.localkart.model.UploadImages;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    @SuppressLint("StaticFieldLeak")
    static Context con;
    // Custom Progress Dialog
    public static ProgressDialog mProgressDialog;

    // Shared Preference
    public static SharedPreferences sharedPreferences;

    // Base Url
    public static String Api = "https://www.localkart.app/portal/api/"; //"https://miyacloud.in/localkart/demo/api/";
    public static String stateList = "state";
    public static String districtList = "district";
    public static String sendotp = "sendotp";
    public static String signup = "signup";
    public static String login = "login";
    public static String getbanner = "getbanner";
    public static String shopcategories = "shopcategories";
    public static String servicecategories = "servicecategories";
    public static String shopsubcat = "shopsubcat";
    public static String servicesubcat = "servicesubcat";
    public static String businesssave = "businesssave";
    public static String uploadimage = "uploadimage";
    public static String viewplan = "subscription";
    public static String listarray = "listarray";
    public static String createpost = "createpost";
    public static String createoffers = "createoffers";
    public static String viewoffer = "viewoffer";
    public static String directorylist = "directorylist";
    public static String todaylist = "todaylist";
    public static String festivallist = "festivallist";
    public static String weeklylist = "weeklylist";
    public static String directorymoredetails = "directorymoredetails";
    public static String viewpostdetails = "viewpostdetails";
    public static String editbusiness = "editbusiness";
    public static String posthistory = "posthistory";
    public static String getrepost = "getrepost";
    public static String createrepostoffers = "createrepostoffers";
    public static String getpricing = "getpricing";
    public static String buynow = "buynow";
    public static String paysuccess = "paysuccess";
    public static String postvalidation = "postvalidation";
    public static String viewplandetails = "viewplandetails";
    public static String savefeedback = "savefeedback";
    public static String viewdeals = "viewdeals";
    public static String getprofiledetails = "getprofiledetails";
    public static String updateprofile = "updateprofile";
    public static String updatedeviceid = "updatedeviceid";
    public static String savesubscribers = "savesubscribers";
    public static String unsubscribe = "unsubscribe";
    public static String sendpush = "sendpush";
    public static String notificationlist = "notificationlist";
    public static String applycode = "applycode";
    public static String viewreferral = "viewreferral";
    public static String amountcalculation = "amountcalculation";
    public static String paymenthistory = "paymenthistory";
    public static String paymenthistorydetails = "paymenthistorydetails";
    public static String savereports = "savereports";
    public static String deletebusinessbanner = "deletebusinessbanner";
    public static String businessupdate = "businessupdate";
    public static String appversion = "appversion";
    public static String paymentsuccess = "paymentsuccess";
    public static String directorylistnearme = "directorylist_nearme";
    public static String todaylistnearme = "todaylist_nearme";
    public static String festivallistnearme = "festivallist_nearme";
    public static String weeklylistnearme = "weeklylist_nearme";
    public static String search = "search";
    public static String searchnearme = "search_nearme";
    public static String getmegasales = "getmegasales";
    public static String createmegasalespost = "createmegasalespost";
    public static String createmegasalesoffers = "createmegasalesoffers";
    public static String currentmegasales = "currentmegasales";
    public static String megasaleslist = "megasaleslist";
    public static String megasaleslistnearme = "megasaleslist_nearme";
    public static String viewmegasalespostdetails = "viewmegasalespostdetails";
    public static String viewmegasalesdeals = "viewmegasalesdeals";
    public static String homeslider = "homeslider";
    public static String categoryslider = "categoryslider";
    public static String userdelete = "userdelete";
    public static String businessdelete = "businessdelete";
    public static String deletepost = "deletepost";
    public static String jobpostvalidation = "jobpostvalidation";
    public static String createjobpost = "createjobpost";
    public static String createjoboffers = "createjoboffers";
    public static String joblist = "joblist";
    public static String joblistnearme = "joblist_nearme";
    public static String viewjobpostdetails = "viewjobpostdetails";
    public static String viewjobdeals = "viewjobdeals";
    public static String viewnews = "viewnews";
    public static String sendotpnew = "sendotpnew";

    public static String shoprating = "shoprating";
    public static String servicerating = "servicerating";
    public static String shopviewcount = "shopviewcount";
    public static String serviceviewcount = "serviceviewcount";
    public static String shopservicedetailcount = "shopservicedetailcount";
    public static String posthistoryviewcount = "posthistoryviewcount";

    // Help and Support Url
    public static String helpUrl = "https://localkart.app/app/help-and-support.php";
    public static String aboutUs = "https://localkart.app/app/about-us.php";
    public static String becomeFranchise = "https://localkart.app/app/become-a-franchise.php";
    public static String shareUrl = "https://bit.ly/3Bo6WNb";//"https://localkart.app/app/download.php";
    public static String rateUsUrl = "https://localkart.app/app/rate-us.php";
    public static String privacyPolicyUrl = "https://localkart.app/app/privacy-policy.php";
    public static String userTCUrl = "https://localkart.app/user-terms-and-conditions.php";
    public static String businessTCUrl = "https://localkart.app/business-terms-and-conditions.php";
    public static String howItWorksUrl = "https://localkart.app/app/how-it-works.php";
    public static String ticketUrl = "https://localkart.app/events/";
    public static String eventUrl = "https://localkart.app/portal/events";

    public static String customereventlist = "customereventlist";
    public static String eventdetails = "eventdetails";
    public static String mybookings = "mybookings";
    public static String eventbookingdetails = "eventbookingdetails";
    public static String eventticketavailablity = "eventticketavailablity";

    public static String checkorgregister = "checkorgregister";
    public static String bussinesseventlist = "bussinesseventlist";
    public static String eventsummary = "eventsummary";
    public static String eventbookinglist = "eventbookinglist";
    public static String checkeventbooking = "checkeventbooking";
    public static String confeecalculation = "confeecalculation";

    public static int callResume = 0;
    public static String constPostType = "";

    public static String razorPayTestKey = "rzp_test_APRjuSYwwwfdnH";//"rzp_test_FPzlMXNvEACWSC";
    public static String razorPayLiveKey = "rzp_live_dPU9HUhjVuJg54";//""rzp_live_pcJLSfZjYmemRL";

    public Utils(Context con) {
        Utils.con = con;
    }

    public static void showProgress(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.progresstitle));
        mProgressDialog.show();
    }

    public static void dismissProgress() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInternetOn() {
        ConnectivityManager conMgr = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conMgr.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isGpsOn() {
        LocationManager manager = (LocationManager) con
                .getSystemService(Context.LOCATION_SERVICE);
        return !(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public static boolean eMailValidation(CharSequence emailAddress) {
        final String EMAIL_PATTERN = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailAddress);
        return !matcher.matches();
    }

    public static boolean webURLValidation(CharSequence webAddress) {
        String WEB_PATTERN = "^((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?$";

        Pattern pattern = Pattern.compile(WEB_PATTERN);
        Matcher matcher = pattern.matcher(webAddress);
        return !matcher.matches();
    }

    public static <T> void setImageList(String key, List<T> list, Context con) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SavePreferences(key, json, con);
    }

    public static void SavePreferences(String key, String value, Context con) {
        sharedPreferences = con.getSharedPreferences("MY_IMG", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static List<UploadImages> getImageList(String key) {
        sharedPreferences = con.getSharedPreferences("MY_IMG", MODE_PRIVATE);
        Gson gson = new Gson();
        List<UploadImages> uploadImagesList;
        String string = sharedPreferences.getString(key, null);
        Type type = new TypeToken<List<UploadImages>>() {
        }.getType();
        uploadImagesList = gson.fromJson(string, type);
        return uploadImagesList;
    }

    public static void clearImageList(Context con) {
        sharedPreferences = con.getSharedPreferences("MY_IMG", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static void saveBasicDetails(BasicDetailsData basicDetailsData) {
        sharedPreferences = con.getSharedPreferences("BASIC_DET", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(basicDetailsData);
        prefsEditor.putString("BasicDetObject", json);
        prefsEditor.apply();
    }

    public static BasicDetailsData getBasicDetails(Context con) {
        sharedPreferences = con.getSharedPreferences("BASIC_DET", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("BasicDetObject", "");
        return gson.fromJson(json, BasicDetailsData.class);
    }

    public static void saveAddressDetails(AddressDetailsData addressDetailsData) {
        sharedPreferences = con.getSharedPreferences("ADDR_DET", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addressDetailsData);
        prefsEditor.putString("AddrDetObject", json);
        prefsEditor.apply();
    }

    public static AddressDetailsData getAddressDetails(Context con) {
        sharedPreferences = con.getSharedPreferences("ADDR_DET", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("AddrDetObject", "");
        return gson.fromJson(json, AddressDetailsData.class);
    }

    public static void saveContactDetails(ContactDetailsData contactDetailsData) {
        sharedPreferences = con.getSharedPreferences("CONT_DET", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contactDetailsData);
        prefsEditor.putString("ContDetObject", json);
        prefsEditor.apply();
    }

    public static ContactDetailsData getContactDetails(Context con) {
        sharedPreferences = con.getSharedPreferences("CONT_DET", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("ContDetObject", "");
        return gson.fromJson(json, ContactDetailsData.class);
    }

    public static void saveLocDetails(LocationData locationData) {
        sharedPreferences = con.getSharedPreferences("LOC_DATA", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationData);
        prefsEditor.putString("LocObject", json);
        prefsEditor.apply();
    }

    public static LocationData getLocDetails(Context con) {
        sharedPreferences = con.getSharedPreferences("LOC_DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("LocObject", "");
        return gson.fromJson(json, LocationData.class);
    }

    public static void saveChip(String chips) {
        sharedPreferences = con.getSharedPreferences("CHIPS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("chips", chips);
        editor.apply();
    }

    public static String getChips(Context con) {
        sharedPreferences = con.getSharedPreferences("CHIPS", MODE_PRIVATE);
        return sharedPreferences.getString("chips", "");
    }

    public static void setBusRegMobNum(boolean value) {
        sharedPreferences = con.getSharedPreferences("BUS_REG_MOB_NUM", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("busRegMobNum", value);
        editor.apply();
    }

    public static boolean getBusRegMobNum(Context con) {
        sharedPreferences = con.getSharedPreferences("BUS_REG_MOB_NUM", MODE_PRIVATE);
        return sharedPreferences.getBoolean("busRegMobNum", false);
    }

    public static void saveServiceList(List<String> services) {
        sharedPreferences = con.getSharedPreferences("SERVICES", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(services);
        prefsEditor.putString("service", json);
        prefsEditor.apply();
    }

    public static List<String> getServiceList(Context con) {
        sharedPreferences = con.getSharedPreferences("SERVICES", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("service", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void clearRegPref(Context con) {
        sharedPreferences = con.getSharedPreferences("BASIC_DET", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("ADDR_DET", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("CONT_DET", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("LOC_DATA", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("CHIPS", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("TAGS", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("BUS_REG_MOB_NUM", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        clearImageList(con);

        sharedPreferences = con.getSharedPreferences("SERVICES", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static <T> void setOfferList(String key, List<T> list, Context con) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SavePreferencesOffer(key, json, con);
    }

    public static void SavePreferencesOffer(String key, String value, Context con) {
        sharedPreferences = con.getSharedPreferences("MY_OFFERS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static List<AddOfferData> getOfferList(String key) {
        sharedPreferences = con.getSharedPreferences("MY_OFFERS", MODE_PRIVATE);
        Gson gson = new Gson();
        List<AddOfferData> offerDataList;
        String string = sharedPreferences.getString(key, null);
        Type type = new TypeToken<List<AddOfferData>>() {
        }.getType();
        offerDataList = gson.fromJson(string, type);
        return offerDataList;
    }

    public static void clearOfferList(Context con) {
        sharedPreferences = con.getSharedPreferences("MY_OFFERS", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static void saveStateFilter(String stateId) {
        sharedPreferences = con.getSharedPreferences("FILTER_STATE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("stateId", stateId);
        editor.apply();
    }

    public static String getStateFilter(Context con) {
        sharedPreferences = con.getSharedPreferences("FILTER_STATE", MODE_PRIVATE);
        return sharedPreferences.getString("stateId", "");
    }

    public static void saveDistrictFilter(String districtId) {
        sharedPreferences = con.getSharedPreferences("FILTER_DISTRICT", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("districtId", districtId);
        editor.apply();
    }

    public static String getDistrictFilter(Context con) {
        sharedPreferences = con.getSharedPreferences("FILTER_DISTRICT", MODE_PRIVATE);
        return sharedPreferences.getString("districtId", "");
    }

    public static void saveNearMeFilter(int progress) {
        sharedPreferences = con.getSharedPreferences("FILTER_NEAR_ME", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("progress", progress);
        editor.apply();
    }

    public static int getNearMeFilter(Context con) {
        sharedPreferences = con.getSharedPreferences("FILTER_NEAR_ME", MODE_PRIVATE);
        return sharedPreferences.getInt("progress", 0);
    }

    public static void clearFilterPref(Context con) {
        sharedPreferences = con.getSharedPreferences("FILTER_NEAR_ME", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("FILTER_STATE", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        sharedPreferences = con.getSharedPreferences("FILTER_DISTRICT", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }


    public static <T> void setReOfferList(String key, List<T> list, Context con) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SavePreferencesReOffer(key, json, con);
    }

    public static void SavePreferencesReOffer(String key, String value, Context con) {
        sharedPreferences = con.getSharedPreferences("MY_RE_OFFERS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static List<OfferData> getReOfferList(String key) {
        sharedPreferences = con.getSharedPreferences("MY_RE_OFFERS", MODE_PRIVATE);
        Gson gson = new Gson();
        List<OfferData> offerDataList;
        String string = sharedPreferences.getString(key, null);
        Type type = new TypeToken<List<OfferData>>() {
        }.getType();
        offerDataList = gson.fromJson(string, type);
        return offerDataList;
    }

    public static void clearReOfferList(Context con) {
        sharedPreferences = con.getSharedPreferences("MY_RE_OFFERS", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static String getAndroidOs() {
        String permissionRequest = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequest = Manifest.permission.READ_MEDIA_IMAGES;
        }
        return permissionRequest;
    }
}
