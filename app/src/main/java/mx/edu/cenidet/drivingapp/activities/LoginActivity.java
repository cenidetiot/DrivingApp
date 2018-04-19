package mx.edu.cenidet.drivingapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

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
import mx.edu.cenidet.drivingapp.R;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.Constants;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, UserController.UsersServiceMethods {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final int RESOLVE_HINT = 12;
    private EditText etPhone;
    private EditText etLoginPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private ApplicationPreferences appPreferences;
    private UserController userController;
    private String email;
    private String token;
    private Intent mIntent;
    private Context context;
    public static Context LOGIN_CONTEXT = null;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LOGIN_CONTEXT = LoginActivity.this;
        context = LOGIN_CONTEXT;
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
       // setCredentialsIfExist();

        //Comprobando la version de Android...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();
            final List<String> permissionsList = new ArrayList<String>();
            if(!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)){
                String gps = this.getString(R.string.message_gps);
                permissionsNeeded.add(gps);
            }

           /* if(!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("WRITE STORAGE");
            if(!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
                permissionsNeeded.add("READ PHONE");*/

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

    private void bindUI(){
        etPhone = (EditText) findViewById(R.id.etPhone);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
    }

    // Construct a request for phone numbers and show the picker
    private void requestHint() {
        //CredentialsClient mCredentialsClient = Credentials.getClient(this);
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                etPhone.setText(credential.getId()); //Will need to process phone number string
                //credential.getProfilePictureUri()
            }
        }
    }

    private boolean login(String email, String password){
        /*if(!isValidEmail(email)){
            Toast.makeText(getApplicationContext(), R.string.message_valid_email, Toast.LENGTH_SHORT).show();
            return false;
        }else */
        if(!isValidPassword(password)){
            Toast.makeText(getApplicationContext(), R.string.message_valid_password, Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password){
        return !TextUtils.isEmpty(password);
    }


    private void goToHome(){
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private boolean setCredentialsIfExist(){
        return !(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN).equals("") && appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME).equals(""));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLogin:
                String phone = etPhone.getText().toString();
                String password = etLoginPassword.getText().toString();
                String subPhone = phone.substring(1, phone.length());
                Log.i("SubPhone", subPhone);
                if(login(subPhone, password)){
                    userController.logInUser(subPhone, password);
                }
                break;
            case R.id.btnSignUp:
                Intent intentCreateAccount = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intentCreateAccount);
                break;
            case R.id.btnPhone:
                requestHint();
                break;
        }
    }



    @Override
    public void createUser(Response response) {

    }

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

    @Override
    public void logInUser(Response response) {
        Log.i("Status:", "code: "+response.getHttpCode()+" body: "+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
            case 201:
                //Toast.makeText(getApplicationContext(), "CODE:"+response.getHttpCode(), Toast.LENGTH_SHORT).show();
                /*if((response.getBodyString().equals("") || response.getBodyString() == null)){
                    Toast.makeText(getApplicationContext(), R.string.message_login_not_found, Toast.LENGTH_SHORT).show();
                }else{
                    JSONObject jsonObject = response.parseJsonObject(response.getBodyString());

                    try {
                        token = jsonObject.getString("token");
                        email = etLoginEmail.getText().toString();
                        Log.i("Status:", "token: "+token+" email: "+email);
                        userController.readUser(email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
                /*if((response.getBodyString().equals("") || response.getBodyString() == null) || (response.getxSubjectToken().equals("") || response.getxSubjectToken() == null)){
                    Toast.makeText(getApplicationContext(), R.string.message_login_not_found, Toast.LENGTH_SHORT).show();
                }else{
                    token = response.getxSubjectToken();
                    email = etLoginEmail.getText().toString();
                    userController.readUser(email);
                }*/
               // Log.i("STATUS: ", "Code: "+response.getHttpCode());
                //Log.i("STATUS: ", "Body: "+response.getBodyString());
                if(response.getBodyString().equals("") || response.getBodyString() == null){
                    Toast.makeText(getApplicationContext(), R.string.message_login_not_found, Toast.LENGTH_SHORT).show();
                }else{
                    JSONObject jsonObject = response.parseJsonObject(response.getBodyString());
                    Log.i("Status:", "JSON: "+jsonObject);
                    try {
                        token = jsonObject.getString("token");
                        email = jsonObject.getJSONObject("user").getString("email");
                        String id = jsonObject.getJSONObject("user").getString("id");
                        String userName = jsonObject.getJSONObject("user").getString("firstName")+" "+jsonObject.getJSONObject("user").getString("lastName");
                        //email = etLoginEmail.getText().toString();
                        Log.i("Status:", "token: "+token+" email: "+email +" id: "+id+" userName: "+userName);

                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN, token);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_ID, ""+id);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME, userName);
                        appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_EMAIL, email);
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
     * Metodo que comprueba si tenemos activo algun determinado permiso.
     * @param permission los permisos que se verifican si estan activos o no.
     * @return verdadero si los permisos se encuentran activos.
     */
    private boolean checkPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Añade los permisos a una lista en caso de que no esten autorizados por el usuario.
     * @param permissionsList lista de los permisos
     * @param permission los permisos que se van a permitir.
     * @return verdadero si los permisos ya fueron autorizados por el usuario.
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

    //Mensaje que muestra que permisos son requeridos para que la aplicación funcione.
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
     * Devolución de llamada para el resultado de la solicitud de permisos. Este método se invoca para cada llamada en requestPermissions(android.app.Activity, String[], int).
     * @param requestCode El código de solicitud pasó en requestPermissions (android.app.Activity, String [], int)
     * @param permissions String: Los permisos solicitados. Nunca nulo
     * @param grantResults int: Los resultados de la concesión para los permisos correspondientes son PERMISSION_GANTED o PERMISSION_DENIED. Nunca nulo
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                //perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    this.finish();
                    Toast.makeText(LoginActivity.this, R.string.message_permission_denied, Toast.LENGTH_SHORT).show();
                }
                /*if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(LoginActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }*/
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
