package mx.edu.cenidet.app.services;

import android.util.Log;
import android.content.Context;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "Alertas";
    private ApplicationPreferences appPreferences;
    private String fcmToken;
    public MyFirebaseInstanceIDService(){
        appPreferences = new ApplicationPreferences();
    }
    @Override
    public void onTokenRefresh() { 
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        fcmToken = appPreferences.getPreferenceString(getApplicationContext(),ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_FCMTOKEN);
        if(refreshedToken != null){
                appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_FCMTOKEN, refreshedToken);
        }

        sendRegistrationToServer(refreshedToken);
    }
    
    private void sendRegistrationToServer(String token) {
        Context context = getApplicationContext();
    }
}