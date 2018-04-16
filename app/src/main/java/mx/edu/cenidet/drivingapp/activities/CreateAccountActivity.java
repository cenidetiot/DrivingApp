package mx.edu.cenidet.drivingapp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import mx.edu.cenidet.drivingapp.R;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;

public class CreateAccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RESOLVE_HINT = 12;
    private GoogleApiClient mGoogleApiClient;
    private EditText etFirstName, etLastName, etPhone, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
    }

    @Override
    protected void onResume() {
        bindEditText();
        super.onResume();
    }

    private void bindEditText() {
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etPassword);
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
}
