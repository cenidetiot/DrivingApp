package mx.edu.cenidet.app.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.edu.cenidet.cenidetsdk.controllers.UserController;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.cenidetsdk.utilities.FunctionSdk;
import mx.edu.cenidet.app.R;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

import static mx.edu.cenidet.app.activities.MainActivity.getColorWithAlpha;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        UserController.UsersServiceMethods{

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final int RESOLVE_HINT = 12;
    private EditText etPhone, etEmailSG;
    private EditText etLoginPassword;
    private ImageButton btnPhone;
    private Button btnLogin;
    private Button btnSignUp;
    private ApplicationPreferences appPreferences;
    private UserController userController;
    private String email, emailSG;
    private String token;
    private Intent mIntent;
    private Context context;
    public static Context LOGIN_CONTEXT = null;
    private GoogleApiClient mGoogleApiClient;
    private String phone;
    private String userType;
    private boolean emptyEmail = true;
    private boolean emptyPhone = true;
    private boolean emptyPassword = true;
    private boolean emailIsValid = false;

    private boolean etPhoneTouched = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        RelativeLayout lat = (RelativeLayout) findViewById(R.id.toLayout);
        lat.setBackgroundColor(getColorWithAlpha(Color.parseColor("#2c3e50"), 0.7f));

        LOGIN_CONTEXT = LoginActivity.this;
        context = LOGIN_CONTEXT;

        setToolbar();

        userType = getIntent().getStringExtra("userType");
        appPreferences = new ApplicationPreferences();

        if(setCredentialsIfExist()){
            mIntent = new Intent(this, SplashActivity.class);
            startActivity(mIntent);
            this.finish();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        bindUI();
        userController = new UserController(getApplicationContext(), this);



    }

    /**
     * Assign the toolbar
     */
    private void setToolbar(){
        Toolbar toolbarLogin = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbarLogin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.emptyTitleToolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Assigns the UI to the view
     */
    private void bindUI(){

        etEmailSG = (EditText) findViewById (R.id.etEmailSG);
        etPhone = (EditText) findViewById(R.id.etPhone);
        //etPhone.setEnabled(false);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        btnPhone = (ImageButton) findViewById (R.id.btnPhone);
        btnLogin = (Button) findViewById (R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnLogin.setEnabled(false);

        /**
         * Used to check if the email is empty when the text change
         */
        etEmailSG.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                isValidEmail(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    emptyEmail = false;
                else
                    emptyEmail = true;

                checkEmptyText();
            }
        });
        /**
         * Used to check if the phone number is empty when the text change
         */
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 13)
                    etPhone.setError(getResources().getString(R.string.validation_phone_error));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    emptyPhone = false;
                else
                    emptyPhone = true;

                checkEmptyText();
            }
        });
        /**
         * Used to check if the password is empty when the text change
         */
        etLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {


            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    emptyPassword = false;
                else
                    emptyPassword = true;
                checkEmptyText();
            }
        });
        /**
         * Used to get the phone number when the user touch it
         */
        etPhone.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                if (etPhoneTouched == false) {
                    requestHint();
                }
                return false;
            }
        });

        if(userType.equals("mobileUser")){
            etEmailSG.setVisibility(View.INVISIBLE);
            etPhone.setVisibility(View.VISIBLE);
            btnPhone.setVisibility(View.VISIBLE);
        }
        else{
            etPhone.setVisibility(View.INVISIBLE);
            btnPhone.setVisibility(View.INVISIBLE);
            etEmailSG.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Retrieve the phone number
     */
    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder().setShowCancelButton(true).build())
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
        etPhoneTouched =  true;
    }

    /**
     * Check if the text is empy
     */
    public  void  checkEmptyText(){
        boolean accountIsOk = false;
        if(userType.equals("mobileUser")){
            if (emptyPhone == false)
                accountIsOk = true;
        }else {
            if (emptyEmail == false && emailIsValid)
                accountIsOk = true;
        }
        if (emptyPassword == false && accountIsOk )
            btnLogin.setEnabled(true);
        else
            btnLogin.setEnabled(false);

    }

    /**
     * Run when try to retrieve the phone number
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1002){
            if (FunctionSdk.getPhoneNumber(getApplicationContext()) != null)
                etPhone.setText(FunctionSdk.getPhoneNumber(getApplicationContext()));
            else
                etPhone.setText("+52");
            etPhone.requestFocus(View.FOCUS_LEFT);
        }else {
            // if is not possible retrieve the phone number use the number that the user registered
            if (requestCode == RESOLVE_HINT) {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    etPhone.setText(credential.getId());
                }
            }
        }
    }

    /**
     * Used to validate the email
     * @param email
     * @return
     */
    private boolean isValidEmail(String email){
        boolean valid = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (valid == false) {
            etEmailSG.setError(getResources().getString(R.string.error_invalid_email));
            emailIsValid = false;
        }else {
            emailIsValid = true;
        }
        return valid;
    }


    /**
     * Change the Activity to the SplashActivity
     */
    private void goToHome(){
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("fromLogin", "YES");
        startActivity(intent);
    }


    /**
     * Check the credencials(if the user is logged)
     * @return
     */
    private boolean setCredentialsIfExist(){
        return !(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN).equals("") && appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME).equals(""));
    }

    /**
     * Used to assign the click event to the button btnLogin
     * @param v
     */
    @Override
    public void onClick(View v) {
        String password = etLoginPassword.getText().toString();
        switch(v.getId()){
            case R.id.btnLogin:

                if(userType.equals("mobileUser")){
                    phone = etPhone.getText().toString();
                    String subPhone = phone.substring(1, phone.length());
                    userController.logInUser(subPhone, password, userType);
                } else{
                    emailSG = etEmailSG.getText().toString();
                    userController.logInUser(emailSG, password, userType);
                }
                break;
            case R.id.btnPhone :
                requestHint();
                break;

            case R.id.etPhone:

                break;
        }
    }

    @Override
    public void createUser(Response response) {

    }

    /**
     * Run when the user could log in
     * @param response
     */
    @Override
    public void readUser(Response response) {
        switch (response.getHttpCode()){
            case 200:
                if((response.getBodyString().equals("") || response.getBodyString() == null)){
                    Toast.makeText(getApplicationContext(), R.string.message_login_not_found, Toast.LENGTH_SHORT).show();
                }else{
                    JSONObject jsonObject = response.parseJsonObject(response.getBodyString());
                    try {
                        int id = jsonObject.getInt("id");
                        String userName = jsonObject.getString("first_name")+" "+jsonObject.getString("last_name");
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN, token);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_ID, ""+id);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME, userName);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_EMAIL, email);
                        goToHome();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), R.string.message_login_not_found, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void updateUser(Response response) {

    }

    @Override
    public void deleteUser(Response response) {

    }

    /**
     * Run when the user try to login
     * @param response
     */
    @Override
    public void logInUser(Response response) {
        Log.i("Status:", "code: "+response.getHttpCode()+" body: "+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
            case 201:
                if(response.getBodyString().equals("") || response.getBodyString() == null){
                    Toast.makeText(getApplicationContext(), R.string.message_login_not_found, Toast.LENGTH_SHORT).show();
                }else{
                    JSONObject jsonObject = response.parseJsonObject(response.getBodyString());
                    Log.i("Status:", "JSON-------------------------------:\n"+jsonObject);
                    try {
                        token = jsonObject.getString("token");
                        email = jsonObject.getJSONObject("user").getString("email");
                        String id = jsonObject.getJSONObject("user").getString("id");
                        String userName = jsonObject.getJSONObject("user").getString("firstName")+" "+jsonObject.getJSONObject("user").getString("lastName");
                        //email = etLoginEmail.getText().toString();
                        Log.i("Status:", "token: "+token+" email: "+email +" id: "+id+" userName: "+userName);

                        //SAVE THE PREFERENCES CONSTANTS OF THE USER
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN, token);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_ID, ""+id);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME, userName);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_EMAIL, email);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_PHONE, phone);

                        //SAVE THE PREFERENCE USER_TYPE IN THE CONSTANTS
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_USER_TYPE, userType);
                        //Toast.makeText(context, appPreferences.getPreferenceString(getApplicationContext(),ConstantSdk.PREFERENCE_NAME_GENERAL,ConstantSdk.PREFERENCE_USER_TYPE), Toast.LENGTH_LONG).show();

                        goToHome();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 500:
            case 401:
            case 404:
                Toast.makeText(getApplicationContext(), R.string.message_user_not_found, Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    @Override
    public void logOutUser(Response response) {

    }


    /**
     * Message shows that permits be required for the app work
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(R.string.message_title_showMessageOKCancel)
                .setMessage(message)
                .setIcon(R.drawable.ic_no_encryption)
                //.setNegativeButton(R.string.message_cancel_showMessageOKCancel, null)
                .setPositiveButton(R.string.message_accept_showMessageOKCancel, okListener)
                .create()
                .show();
    }

    /**
     * Run for each call to requestPermissions
     * @param requestCode
     * @param permissions
     * @param grantResults PERMISSION_GANTED o PERMISSION_DENIED.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    this.finish();
                    Toast.makeText(LoginActivity.this, R.string.message_permission_denied, Toast.LENGTH_SHORT).show();
                }

            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
