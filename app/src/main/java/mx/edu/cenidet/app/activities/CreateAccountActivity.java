package mx.edu.cenidet.app.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import mx.edu.cenidet.cenidetsdk.controllers.UserController;
import mx.edu.cenidet.cenidetsdk.entities.User;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.cenidetsdk.utilities.FunctionSdk;
import mx.edu.cenidet.app.R;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;

import static mx.edu.cenidet.app.activities.MainActivity.getColorWithAlpha;

public class CreateAccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, UserController.UsersServiceMethods {
    private static final int RESOLVE_HINT = 12;
    private GoogleApiClient mGoogleApiClient;
    private EditText etFirstName, etLastName, etPhone, etEmail, etPassword, etConfirmPassword;
    private CheckBox ckTerms;
    private Button createAccount;
    private  UserController userController;
    private ApplicationPreferences appPreferences;
    private String phone;
    private boolean etPhoneTouched = false;

    /**/

    private boolean emptyFirstName = true;
    private boolean emptyLastName = true;
    private boolean emptyPhone = true;
    private boolean emptyEmail =  true;
    private boolean emptyPassword = true;
    private boolean emptyConfirmation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setToolbar();
        RelativeLayout lat = (RelativeLayout) findViewById(R.id.toLayout);
        lat.setBackgroundColor(getColorWithAlpha(Color.parseColor("#2c3e50"), 0.7f));

        appPreferences = new ApplicationPreferences();
        bindUI();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        userController = new UserController(getApplicationContext(), this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

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

    private boolean createAccount(User user, String confirmPassword){
        boolean band = false;

        if(isEmptyText(user, confirmPassword)){
            Toast.makeText(getApplicationContext(), R.string.message_empty_fields, Toast.LENGTH_SHORT).show();
        }else if(!isValidEmail(user.getEmail())){
            Toast.makeText(getApplicationContext(), R.string.message_valid_email, Toast.LENGTH_SHORT).show();
        }else if(!isValidPassword(user.getPassword(), confirmPassword)){
            Toast.makeText(getApplicationContext(), R.string.message_passwords_incorrect, Toast.LENGTH_SHORT).show();
        }else if (!ckTerms.isChecked()) {
            Toast.makeText(getApplicationContext(), R.string.message_no_accept_terms, Toast.LENGTH_SHORT).show();
        }
        else{
            band = true;
        }
        return band;
    }
    private boolean isValidEmail(String email){
        boolean valid = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (valid == false) {
            etEmail.setError(getResources().getString(R.string.error_invalid_email));
        }
        return valid;
    }

    private boolean isValidPassword(String password, String confirmPassword){
        if(password.equals(confirmPassword)){
            return true;
        }else{
            return false;
        }
    }

    private boolean isEmptyText(User user, String confirmPassword){
        boolean temp =  false;
        if (TextUtils.isEmpty(user.getFirstName())) {
            etFirstName.setError(getResources().getString(R.string.validation_empty_required_field));
            temp = true;
        }
        if(TextUtils.isEmpty(user.getLastName())) {
            etLastName.setError(getResources().getString(R.string.validation_empty_required_field));
            temp = true;
        }
        if(TextUtils.isEmpty(user.getPhoneNumber())) {
            etPhone.setError(getResources().getString(R.string.validation_empty_required_field));
            temp = true;
        }
        if(TextUtils.isEmpty(user.getEmail())) {
            etEmail.setError(getResources().getString(R.string.validation_empty_required_field));
            temp = true;
        }
        if(TextUtils.isEmpty(user.getPassword())) {
            etPassword.setError(getResources().getString(R.string.validation_empty_required_field));
            temp = true;
        }
        if(TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError(getResources().getString(R.string.validation_empty_required_field));
            temp = true;
        }


        return temp;
    }

    private void bindUI() {
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etPhone = (EditText) findViewById(R.id.etPhoneCreate);
        //etPhone.setEnabled(false);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        ckTerms = (CheckBox) findViewById(R.id.checkBox);

        createAccount = (Button) findViewById(R.id.btnCreateAccount);
        createAccount.setEnabled(false);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 13) {
                    etPhone.setError(getResources().getString(R.string.validation_phone_error));
                    emptyPhone = true;
                }else{
                    emptyPhone = false;
                }

                checkEmptyFields();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0)
                    requestHint();
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(isValidEmail(s.toString()))
                    emptyEmail = false;
                else{
                    emptyEmail = true;
                }
                checkEmptyFields();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });



        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidPassword(s.toString(), etPassword.getText().toString())){
                    emptyConfirmation = true;
                    etConfirmPassword.setError(getResources().getString(R.string.validation_dont_match_passwords));
                }else{
                    emptyConfirmation = false;
                }

                checkEmptyFields();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });



        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidPassword(s.toString(), etConfirmPassword.getText().toString())){
                    emptyConfirmation = true;
                    etConfirmPassword.setError(getResources().getString(R.string.validation_dont_match_passwords));
                }else{
                    emptyConfirmation = false;
                    etConfirmPassword.setError(null);
                }

                if (s.toString().length() < 1)
                    emptyPassword = true;
                else
                    emptyPassword = false;

                checkEmptyFields();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 1){
                    emptyFirstName = true;
                  }else{
                    emptyFirstName =  false;
                }

                checkEmptyFields();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 1){
                    emptyLastName = true;
                }else{
                    emptyLastName =  false;
                }

                checkEmptyFields();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        etPhone.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                if (etPhoneTouched == false) {
                    requestHint();
                }
                return false;
            }
        });


        etFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(etFirstName.getText().toString().length() <= 0)
                        etFirstName.setError(getResources().getString(R.string.validation_empty_required_field));
                }
            }
        });

        etLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(etLastName.getText().toString().length() <= 0)
                        etLastName.setError(getResources().getString(R.string.validation_empty_required_field));
                }
            }
        });

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(etEmail.getText().toString().length() <= 0)
                        etEmail.setError(getResources().getString(R.string.validation_empty_required_field));
                }
            }
        });

        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(etPhone.getText().toString().length() <= 0)
                        requestHint();
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(etPassword.getText().toString().length() <= 0)
                        etPassword.setError(getResources().getString(R.string.validation_empty_required_field));
                }
            }
        });
        etConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(etConfirmPassword.getText().toString().length() <= 0)
                        etConfirmPassword.setError(getResources().getString(R.string.validation_empty_required_field));
                }
            }
        });





    }



    private void checkEmptyFields(){
        Log.d("EMPTYFIELDS", ""+ emptyFirstName + "-" + emptyLastName + "-" + emptyPhone +"-"+ emptyEmail + "-" + emptyPassword + "-" + emptyConfirmation + "-" + !ckTerms.isChecked());
        if (!(emptyFirstName || emptyLastName || emptyPhone || emptyEmail || emptyPassword || emptyConfirmation || !ckTerms.isChecked() )){
            createAccount.setEnabled(true);
        }else {
            createAccount.setEnabled(false);
        }
    }
    // Construct a request for phone numbers and show the picker
    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
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

    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RESULT: ", "------------------------------Result Code: "+resultCode+" Request Code: "+requestCode);
        if(resultCode == 1002){

            if (FunctionSdk.getPhoneNumber(getApplicationContext()) != null)
                etPhone.setText(FunctionSdk.getPhoneNumber(getApplicationContext()));
            else
                etPhone.setText("+52");
            etPhone.requestFocus(View.FOCUS_LEFT);

        }else {
            if (requestCode == RESOLVE_HINT) {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    etPhone.setText(credential.getId()); //Will need to process phone number string
                }
            }
        }
    }

    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAccount:
                User user = new User();
                user.setFirstName(etFirstName.getText().toString());
                user.setLastName(etLastName.getText().toString());
                phone = etPhone.getText().toString();
                user.setPhoneNumber(phone);
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

            case R.id.checkBox:
                if (ckTerms.isChecked() == true) {
                    Intent intentTerms = new Intent(CreateAccountActivity.this, TermsAndConditions.class);
                    startActivity(intentTerms);
                }
                checkEmptyFields();
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
        Log.i("CODE: ", "CREATE USER:------------------------------------: "+response.getHttpCode()+" PHONE: "+phone);
        if(response.getHttpCode() == 201){
            appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_PHONE, phone);
            Toast.makeText(getApplicationContext(), R.string.message_account_generated, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();

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
