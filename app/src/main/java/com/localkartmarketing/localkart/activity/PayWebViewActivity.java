package com.localkartmarketing.localkart.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.localkartmarketing.localkart.R;
import com.localkartmarketing.localkart.support.Utilis;

public class PayWebViewActivity extends AppCompatActivity {
    private String Tag = "PayWebViewActivity";
    Utilis utilis;
    Toolbar toolbar;
    ActionBar actionBar = null;

    String keyIntent = "", planId = "", webUrl = "", amount = "", planName = "";

    WebView webView;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_web_view);
        utilis = new Utilis(PayWebViewActivity.this);

        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("key");
        planId = intent.getStringExtra("planId");
        webUrl = intent.getStringExtra("webUrl");
        amount = intent.getStringExtra("amount");
        planName = intent.getStringExtra("planName");

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(PayWebViewActivity.this, R.color.colorPrimaryDark));

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
        toolBarTitle.setText("Payment");

        webView = findViewById(R.id.web_view);

        pDialog = new ProgressDialog(PayWebViewActivity.this);
        pDialog.setMessage(PayWebViewActivity.this.getResources().getString(R.string.progresstitle));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);


        webView.setWebViewClient(new MyWebViewClient(this));
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(webUrl);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        Intent intent = new Intent(PayWebViewActivity.this, PlanActivity.class);
        intent.putExtra("key", keyIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private class MyWebViewClient extends WebViewClient {
        private Context context;

        public MyWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("url" + url);
            String paySuccess = Utilis.Api + Utilis.paysuccess;
            if (url.contains(paySuccess)) {
                reDirectToApp(context);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pDialog.dismiss();
        }
    }

    private void reDirectToApp(Context context) {

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Payment Successful!")
//                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                        Intent intent = new Intent(PayWebViewActivity.this, PlanActivity.class);
//                        intent.putExtra("key", keyIntent);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//
//
//        AlertDialog alert = builder.create();
//        alert.show();

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PayWebViewActivity.this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(PayWebViewActivity.this).inflate(R.layout.alert_payment_successful, viewGroup, false);
        TextView tvContent = dialogView.findViewById(R.id.tv_content);
        String textContent = "An amount of <b> Rs. " + amount + "</b> has been paid successfully towards subscription charges for the plan <b>" + planName + "</b> and is active now.";
        tvContent.setText(Html.fromHtml(textContent));
        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(PayWebViewActivity.this, PlanActivity.class);
                intent.putExtra("key", keyIntent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        alertDialog.show();
    }
}