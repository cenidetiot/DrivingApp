package mx.edu.cenidet.app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import mx.edu.cenidet.app.R;

public class WebViewSmartSecurity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_smart_security);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("https://smartsdksecurity.com.mx");
    }

}
