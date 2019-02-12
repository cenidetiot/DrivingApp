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

public class LoginAsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnMobileUser, btnSecurityGuard;
    private ApplicationPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_as);
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
     * Assigns the event onBackPressed when the user use the back arrow of the toolbar
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Get the Color added with alpha
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
     * Add the click event
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent loginUser = new Intent(this, LoginActivity.class);

        switch(v.getId()){
            case R.id.btnMobileUser:
                loginUser.putExtra("userType", "mobileUser");
            break;
            case R.id.btnSecurityGuard:
                loginUser.putExtra("userType", "securityGuard");
            break;
        }

        startActivity(loginUser);

    }

    /**
     * Assigns the toolbar aspect to the view
     */
    private void setToolbar(){
        Toolbar toolbarLogin = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbarLogin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.emptyTitleToolbar);
    }



}

