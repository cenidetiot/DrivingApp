package mx.edu.cenidet.drivingapp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mx.edu.cenidet.cenidetsdk.controllers.UserController;
import mx.edu.cenidet.cenidetsdk.entities.User;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.drivingapp.R;
import www.fiware.org.ngsi.datamodel.entity.Alert;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;

public class CreateAccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, UserController.UsersServiceMethods {
    private static final int RESOLVE_HINT = 12;
    private GoogleApiClient mGoogleApiClient;
    private EditText etFirstName, etLastName, etPhone, etEmail, etPassword, etConfirmPassword;
    private  UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        userController = new UserController(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        bindEditText();
        super.onResume();
    }

    private boolean createAccount(User user, String confirmPassword){
        boolean band = false;
        if(isEmptyText(user, confirmPassword)){
            Toast.makeText(getApplicationContext(), R.string.message_empty_fields, Toast.LENGTH_SHORT).show();
            band = false;
        }else if(!isValidEmail(user.getEmail())){
            Toast.makeText(getApplicationContext(), R.string.message_valid_email, Toast.LENGTH_SHORT).show();
            band = false;
        }else if(isValidPassword(user.getPassword(), confirmPassword)){
            band = true;
        }else{
            Toast.makeText(getApplicationContext(), R.string.message_passwords_incorrect, Toast.LENGTH_SHORT).show();
            band = false;
        }

        return band;
    }
    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password, String confirmPassword){
        if(password.equals(confirmPassword)){
            return true;
        }else{
            return false;
        }
    }

    private boolean isEmptyText(User user, String confirmPassword){
        if(TextUtils.isEmpty(user.getFirstName()) || TextUtils.isEmpty(user.getLastName()) || TextUtils.isEmpty(user.getPhoneNumber()) || TextUtils.isEmpty(user.getEmail()) || TextUtils.isEmpty(user.getPassword()) || TextUtils.isEmpty(confirmPassword)){
            return true;
        }else {
            return false;
        }
    }

    private void bindEditText() {
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
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
            }
        }
    }

    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAccount:
                //CODIGO PARA CREAR CUENTA
                //CODIGO PARA CREAR CUENTA
                //CODIGO PARA CREAR CUENTA
                Log.i("STATUS: ", "MENSAJE -----------: "+TextUtils.isEmpty(etFirstName.getText().toString()));
                User user = new User();
                user.setFirstName(etFirstName.getText().toString());
                user.setLastName(etLastName.getText().toString());
                user.setPhoneNumber(etPhone.getText().toString());
                user.setEmail(etEmail.getText().toString());
                user.setPassword(etPassword.getText().toString());
                String confirmPassword = etConfirmPassword.getText().toString();
                if(createAccount(user, confirmPassword) == true){
                    userController.createUser(user);
                }
                break;
            case R.id.btnPhone:
                requestHint();
                break;
            default:
                //DEFAULT
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void createUser(Response response) {
        Log.i("CODE: ", "CREATE USER:------------------------------------: "+response.getHttpCode());
        if(response.getHttpCode() == 201){
            Toast.makeText(getApplicationContext(), R.string.message_account_generated, Toast.LENGTH_LONG).show();
        }else if(response.getHttpCode() == 409){
            Toast.makeText(getApplicationContext(), R.string.message_user_exists, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void readUser(Response response) {

    }

    @Override
    public void updateUser(Response response) {

    }

    @Override
    public void deleteUser(Response response) {

    }

    @Override
    public void logInUser(Response response) {

    }

    @Override
    public void logOutUser(Response response) {

    }
}
