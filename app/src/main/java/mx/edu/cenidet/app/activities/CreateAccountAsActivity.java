package mx.edu.cenidet.app.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

public class CreateAccountAsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnMobileUser, btnSecurityGuard;
    private ApplicationPreferences appPreferences;

    /**
     * Used to initialize the UI
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_as);
        appPreferences = new ApplicationPreferences();
        //Instance buttons of activities
        btnMobileUser = (Button) findViewById(R.id.btnMobileUser);
        btnSecurityGuard = (Button) findViewById(R.id.btnSecurityGuard);
        btnSecurityGuard.setOnClickListener(this);
        btnMobileUser.setOnClickListener(this);
        RelativeLayout lat = (RelativeLayout) findViewById(R.id.toLayout);
        lat.setBackgroundColor(getColorWithAlpha(Color.parseColor("#2c3e50"), 0.7f));
        setToolbar();
    }

    /**
     * Get color With alpha
     * @param color
     * @param ratio
     * @return
     */
    public static int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    /**
     * Assigns the click listener to the buttons
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnMobileUser:
                Intent createMobileUser = new Intent(this, CreateAccountActivity.class);
                createMobileUser.putExtra("userType", "mobileUser");
                startActivity(createMobileUser);
                break;
            case R.id.btnSecurityGuard:
                Intent createSecurityGuard = new Intent(this, WebViewSmartSecurity.class);
                createSecurityGuard.putExtra("userType", "securityGuard");
                startActivity(createSecurityGuard);
                break;
        }
    }

    /**
     * Add the toolbar into the view
     */
    private void setToolbar(){
        Toolbar toolbarLogin = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbarLogin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.emptyTitleToolbar);
    }

    /**
     * Add the event onBackPressed to the back arrow
     * @return
     */
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
