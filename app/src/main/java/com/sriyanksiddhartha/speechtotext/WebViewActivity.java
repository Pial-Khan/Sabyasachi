package com.sriyanksiddhartha.speechtotext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent=getIntent();
        String message = intent.getStringExtra("message");

        webView = (WebView)findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.pipilika.com/search?q=" + message);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
