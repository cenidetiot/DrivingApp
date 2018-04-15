package mx.edu.cenidet.drivingapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import mx.edu.cenidet.cenidetsdk.controllers.CampusController;
import mx.edu.cenidet.cenidetsdk.controllers.DeviceTokenControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.ZoneControllerSdk;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.drivingapp.R;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;

public class SplashActivity extends AppCompatActivity implements DeviceTokenControllerSdk.DeviceTokenServiceMethods, ZoneControllerSdk.ZoneServiceMethods {
    private Intent mIntent;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<Zone> listZone;
    private ZoneControllerSdk zoneControllerSdk;

    //Envío del token de firebase
    private DeviceTokenControllerSdk deviceTokenControllerSdk;
    private String fcmToken;
    private Context context;
    private ApplicationPreferences appPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        sqLiteDrivingApp = new SQLiteDrivingApp(this);
        zoneControllerSdk = new ZoneControllerSdk(context, this);
        listZone = sqLiteDrivingApp.getAllZone();

        //objeto que utilizaremos para llamar a los metodos de la gestion del token de firebase
        appPreferences = new ApplicationPreferences();
        deviceTokenControllerSdk = new DeviceTokenControllerSdk(context, this);
        fcmToken = appPreferences.getPreferenceString(getApplicationContext(),ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_FCMTOKEN);
        if (!fcmToken.equals("") || fcmToken != null){
            deviceTokenControllerSdk.createDeviceToken(fcmToken, new DevicePropertiesFunctions().getDeviceId(context));
        }
        if(listZone.size()== 0){
            zoneControllerSdk.readAllZone();
        }

        if(isEnableGPS()){
            mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            showGPSDisabledAlert();
            Log.i("Status ", "Inactivo gps");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isEnableGPS()){
            //Inicia el servicio del GPS
            mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            Log.i("Status ", "Inactivo gps");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume splash", "-----------------------------------------------------------------------------");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause splash", "-----------------------------------------------------------------------------");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("onPostResume splash", "-----------------------------------------------------------------------------");
    }

    private boolean isEnableGPS(){
        LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            return true;
        }else{
            return false;
        }
    }

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

    @Override
    public void readAllZone(Response response) {
        Log.i("Status: ", "CodeAllZone: "+response.getHttpCode());
        Log.i("Status: ", "BODYAllZone: "+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
                Zone zone;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                //Log.i("Status: ", "----------");
                //Log.i("Status: ", "BODY Array: "+jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        zone = new Zone();
                        JSONObject object = jsonArray.getJSONObject(i);
                        zone.setIdZone(object.getString("idZone"));
                        zone.setType(object.getString("type"));
                        zone.getRefBuildingType().setValue(object.getString("refBuildingType"));
                        zone.getName().setValue(object.getString("name"));
                        zone.getAddress().setValue(object.getString("address"));
                        zone.getCategory().setValue(""+object.getJSONArray("category"));
                        zone.getLocation().setValue(""+object.getJSONArray("location"));
                        zone.getCenterPoint().setValue(""+object.getJSONArray("centerPoint"));
                        zone.getDescription().setValue(object.getString("description"));
                        zone.getDateCreated().setValue(object.getString("dateCreated"));
                        zone.getDateModified().setValue(object.getString("dateModified"));
                        zone.getDateModified().setValue(object.getString("dateModified"));
                        zone.getStatus().setValue(object.getString("status"));
                        /*Log.i("Status: ", "ID: "+zone.getIdZone());
                        Log.i("Status: ", "type: "+zone.getType());
                        Log.i("Status: ", "refBuildingType: "+zone.getRefBuildingType().getValue());
                        Log.i("Status: ", "name: "+zone.getName().getValue());
                        Log.i("Status: ", "address: "+zone.getAddress().getValue());
                        Log.i("Status: ", "category: "+zone.getCategory().getValue());
                        Log.i("Status: ", "location: "+zone.getLocation().getValue());
                        Log.i("Status: ", "centerPoint: "+zone.getCenterPoint().getValue());
                        Log.i("Status: ", "description: "+zone.getDescription().getValue());
                        Log.i("Status: ", "Create: "+zone.getDateCreated().getValue());
                        Log.i("Status: ", "Modified: "+zone.getDateModified().getValue());
                        Log.i("Status: ", "status: "+zone.getStatus().getValue());*/

                        if(sqLiteDrivingApp.createZone(zone) == true){
                            Log.i("Status: ", "Dato insertado correctamente Zone...!");
                        }else{
                            Log.i("Status: ", "Error al insertar Zone...!");
                        }
                        Log.i("--------: ", "--------------------------------------");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }
}
