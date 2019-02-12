package mx.edu.cenidet.app.services;

import android.util.Log;
import android.content.Context;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService  {
    private static final String TAG = "Alertas";
    private ApplicationPreferences appPreferences;
    private String fcmToken;
    public MyFirebaseInstanceIDService(){
        appPreferences = new ApplicationPreferences();
    }

    /**
     * Runs when the tokens is refreshed
     */
    @Override
    public void onTokenRefresh() { 
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Used to store the token on the app preferences
     * @param token
     */
    private void sendRegistrationToServer(String token) {
        Context context = getApplicationContext();
        appPreferences.saveOnPreferenceString(
                context,
                ConstantSdk.STATIC_PREFERENCES,
                ConstantSdk.PREFERENCE_KEY_FCMTOKEN,
                token
        );
        Log.d(TAG, "TOKEN" + token);
    }


}