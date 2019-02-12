package mx.edu.cenidet.app.activities;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnMobileUser, btnSecurityGuard;
    private ApplicationPreferences appPreferences;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private List<String> permissionsList;
    private String permission;

    /**
     * Used to assigns the UI, check the android version and check the permits
     * @param savedInstanceState
     */
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
        RelativeLayout lat = (RelativeLayout) findViewById(R.id.toLayout);
        lat.setBackgroundColor(getColorWithAlpha(Color.parseColor("#2c3e50"), 0.7f));

        // Retrieve the android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();
            final List<String> permissionsList = new ArrayList<String>();
            if(!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)){
                String gps = this.getString(R.string.message_gps);
                permissionsNeeded.add(gps);
            }
            if(!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE)){
                String readPhone = this.getString(R.string.message_read_phone);
                permissionsNeeded.add(readPhone);
            }

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String needRationale = this.getString(R.string.message_need_rationale);
                    String message = needRationale+" "+ permissionsNeeded.get(0);
                    //String message =  permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }

        }
    }


    /**
     * Add the permits to a list when the ussed not allowed
     * @param permissionsList
     * @param permission
     * @return
     */
    private boolean addPermission(List<String> permissionsList, String permission) {
        if(checkPermission(permission) == false){
            permissionsList.add(permission);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    /**
     * Shows the Required permissions
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.message_title_showMessageOKCancel)
                .setMessage(message)
                .setIcon(R.drawable.ic_no_encryption)
                //.setNegativeButton(R.string.message_cancel_showMessageOKCancel, null)
                .setPositiveButton(R.string.message_accept_showMessageOKCancel, okListener)
                .create()
                .show();
    }

    /**
     * Add alpha to the color
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
     * Check if some permit is enabled
     * @param permission
     * @return
     */
    private boolean checkPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Assigns the click event
     * @param v
     */
     @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnMobileUser:
                Intent loginMobileUser = new Intent(MainActivity.this, LoginAsActivity.class);
                loginMobileUser.putExtra("userType", "mobileUser");
                startActivity(loginMobileUser);
                break;
            case R.id.btnSecurityGuard:
                Intent loginSecurityGuard = new Intent(MainActivity.this, CreateAccountAsActivity.class);
                loginSecurityGuard.putExtra("userType", "securityGuard");
                startActivity(loginSecurityGuard);
                break;
        }
    }
    private boolean setCredentialsIfExist(){
        return !(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN).equals("") && appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME).equals(""));
    }
}

