package mx.edu.cenidet.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import mx.edu.cenidet.app.R;
import mx.edu.cenidet.cenidetsdk.entities.User;

public class TermsAndConditions extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton close ;

    /**
     * Used to initialize the UI
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        loadWebViewLoad(myWebView);
        close = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        close.setOnClickListener(this);

    }

    /**
     * Load the Terms and conditions from some web site
     * @param webview
     */
    private void loadWebViewLoad(WebView webview) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl("https://www.dropbox.com/s/55lmxcqtykyzks5/Terminos%20y%20Condiciones%20del%20uso%20de%20Servicios%20de%20DrivingApp.docx?dl=0https://drivingapp-29059.firebaseapp.com/");

    }


    /**
     * Add the back pressed event
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /**
     * Used to assign the click event to the button
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
