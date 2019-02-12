package mx.edu.cenidet.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.controllers.DeviceTokenControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.ZoneControllerSdk;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;


public class SplashActivity extends AppCompatActivity implements
        DeviceTokenControllerSdk.DeviceTokenServiceMethods,
        ZoneControllerSdk.ZoneServiceMethods {
    private Intent mIntent;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<Zone> listZone;
    private ZoneControllerSdk zoneControllerSdk;
    private DeviceTokenControllerSdk deviceTokenControllerSdk;
    private String fcmToken;
    private Context context;
    private ApplicationPreferences appPreferences;
    private ProgressBar progressBar;

    /**
     * Used to check if have data alert to show or if is should to redirect a user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        context = this;
        appPreferences = new ApplicationPreferences();

        if (setCredentialsIfExist()){
            String alert = getIntent().getStringExtra("alert");
            if ( alert  != null || getIntent().getStringExtra("subcategory") != null) {
                checkAlert();
            }
        }else{
            Intent redirectUser = new Intent(this, MainActivity.class);
            startActivity(redirectUser);
            this.finish();
        }
    }

    /**
     * Used to load the zones and send the device token to the server
     */
    @Override
    protected void onStart() {
        super.onStart();
        loadZones();
        sendDeviceToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    /**
     * Retrieve the data alert
     * @return
     */
    private boolean checkAlert () {
        boolean result = false;
        Log.d("CHEKINGALERT","CHEKINGALERT");
        Intent redirectUser = new Intent(this, HomeActivity.class);
        startActivity(redirectUser);
        this.finish();
        String alert = getIntent().getStringExtra("alert");
        if ( alert  != null || getIntent().getStringExtra("subcategory") != null) {
            Log.d("CHEKINGALERT","EXISTALERT");

            try {
                if(alert  != null){
                    JSONObject jsonObject = new JSONObject(alert);
                    Intent alertIntent = new Intent(this, AlertMapDetailActivity.class);
                    alertIntent.putExtra("subcategory", jsonObject.getString("subCategory"));
                    alertIntent.putExtra("description", jsonObject.getString("description"));
                    alertIntent.putExtra("location", jsonObject.getString("location"));
                    alertIntent.putExtra("severity", jsonObject.getString("severity"));
                    Log.d("CHEKINGALERT" , "ALERTA CON APP CERRADA");
                    startActivity(alertIntent);
                }

                if(getIntent().getStringExtra("subcategory") != null){
                    Intent alertIntent = new Intent(this, AlertMapDetailActivity.class);
                    alertIntent.putExtra("subcategory", getIntent().getStringExtra("subcategory"));
                    alertIntent.putExtra("description", getIntent().getStringExtra("description"));
                    alertIntent.putExtra("location", getIntent().getStringExtra("location"));
                    alertIntent.putExtra("severity", getIntent().getStringExtra("severity"));
                    startActivity(alertIntent);
                    Log.d("CHEKINGALERT" , "ALERTA CON APP ABIERTA");

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            result = true;
        }
        return result;
    }

    /**
     * Check if the GPS is enabled
     */
    private void checkGPS(){
        Log.d("LOADZONES", "CHEKING GPS");

        LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            showGPSDisabledAlert();
        }
        return;
    }

    /**
     * Used to retrieve the zones from database or get them from the server
     */
    private void loadZones(){
        sqLiteDrivingApp = new SQLiteDrivingApp(this);
        zoneControllerSdk = new ZoneControllerSdk(context, this);
        listZone = sqLiteDrivingApp.getAllZone();
        if(listZone.size() <= 0){
            zoneControllerSdk.readAllZone();
        }else{
            checkGPS();
        }
        return;
    }

    /**
     * Show an Alert dialog informing that the GPS is disabled
     */
    private void showGPSDisabledAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_alert_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.button_enable_alert_gps,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Send the device token to update in the server
     */
    public void sendDeviceToken(){
        appPreferences = new ApplicationPreferences();
        deviceTokenControllerSdk = new DeviceTokenControllerSdk(context, this);
        fcmToken = appPreferences.getPreferenceString(
                getApplicationContext(),
                ConstantSdk.STATIC_PREFERENCES,
                ConstantSdk.PREFERENCE_KEY_FCMTOKEN
        );
        if (!fcmToken.equals("") || fcmToken != null){
            String userType = appPreferences.getPreferenceString(getApplicationContext(),ConstantSdk.PREFERENCE_NAME_GENERAL,ConstantSdk.PREFERENCE_USER_TYPE);
            String preference = "All";
            if (userType.equals("mobileUser")){
                preference = "traffic";
            }
            deviceTokenControllerSdk.createDeviceToken(fcmToken, new DevicePropertiesFunctions().getDeviceId(context), preference);
        }
    }

    /**
     * Receives the server response when update the device token
     * @param response
     */
    @Override
    public void createDeviceToken(Response response) {
        Log.i("STATUS", "Firebase Service Create: CODE: "+ response.getHttpCode());
        Log.i("STATUS", "Firebase Service Create: BODY: "+ response.getBodyString());
        switch (response.getHttpCode()){
            case 201:
            case 200:
                Log.i("STATUS: ", "El token se genero exitosamente...!");
                break;
            case 400:
                Log.i("STATUS: ", "Tokenk incorrecto...!");
                break;
        }

    }

    @Override
    public void readDeviceToken(Response response) {

    }

    @Override
    public void updateDeviceToken(Response response) {

    }

    /**
     * Receive the server response from the server can get the zones if the data base is empty
     * @param response
     */
    @Override
    public void readAllZone(mx.edu.cenidet.cenidetsdk.httpmethods.Response response) {
        Log.d("LOADZONES", "LOADING ZONES");
        switch (response.getHttpCode()){
            case 200:
                Zone zone;
                Log.d("ZONES", response.getBodyString());
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        Log.i("Status: ", "Body "+i+" :"+jsonArray.getJSONObject(i));
                        zone = new Zone();
                        JSONObject object = jsonArray.getJSONObject(i);
                        zone.setIdZone(object.getString("idZone"));
                        zone.setType(object.getString("type"));
                        zone.getName().setValue(object.getString("owner"));
                        zone.getAddress().setValue(object.getString("address"));
                        zone.getCategory().setValue(""+object.getString("category"));
                        zone.getLocation().setValue(""+object.getJSONArray("location"));
                        zone.getCenterPoint().setValue(""+object.getJSONArray("centerPoint"));
                        zone.getDescription().setValue(object.getString("description"));
                        zone.getDateCreated().setValue(object.getString("dateCreated"));
                        zone.getDateModified().setValue(object.getString("dateModified"));
                        zone.getDateModified().setValue(object.getString("dateModified"));
                        zone.getStatus().setValue(object.getString("status"));

                        if(sqLiteDrivingApp.createZone(zone) == true){
                            Log.i("ZONES", "Dato insertado correctamente Zone...!" + zone.getIdZone());
                        }else{
                            Log.i("ZONES", "Error al insertar Zone...!" + zone.getIdZone());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("LOADZONES", "ZONES ARE READY");
                checkGPS();
                break;
        }
    }

    /**
     * Used to check if the user is logged
     * @return
     */
    private boolean setCredentialsIfExist(){
        return !(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN).equals("") && appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME).equals(""));
    }
}
