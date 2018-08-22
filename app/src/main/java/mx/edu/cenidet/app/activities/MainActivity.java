package mx.edu.cenidet.app.activities;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

import android.content.Intent;
import android.view.View;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnMobileUser, btnSecurityGuard;
    private ApplicationPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appPreferences = new ApplicationPreferences();
        //Instance buttons of activities
        btnMobileUser = (Button) findViewById(R.id.btnMobileUser);
        btnSecurityGuard = (Button) findViewById(R.id.btnSecurityGuard);
        btnSecurityGuard.setOnClickListener(this);
        btnMobileUser.setOnClickListener(this);



    }
     @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnMobileUser:
                Intent loginMobileUser = new Intent(MainActivity.this, LoginActivity.class);
                loginMobileUser.putExtra("userType", "mobileUser");
                startActivity(loginMobileUser);
                break;
            case R.id.btnSecurityGuard:
                Intent loginSecurityGuard = new Intent(MainActivity.this, LoginActivity.class);
                loginSecurityGuard.putExtra("userType", "securityGuard");
                startActivity(loginSecurityGuard);
                break;
        }
    }
    private boolean setCredentialsIfExist(){
        return !(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN).equals("") && appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME).equals(""));
    }
}

