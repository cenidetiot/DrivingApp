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
import mx.edu.cenidet.cenidetsdk.controllers.OffStreetParkingControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.RoadControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.RoadSegmentControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.ZoneControllerSdk;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;


public class SplashActivity extends AppCompatActivity implements
        DeviceTokenControllerSdk.DeviceTokenServiceMethods,
        ZoneControllerSdk.ZoneServiceMethods ,
        RoadSegmentControllerSdk.RoadSegmentServiceMethods,
        RoadControllerSdk.RoadServiceMethods,
        OffStreetParkingControllerSdk.OffStreetParkingServiceMethods{
    private Intent mIntent;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<Zone> listZone;
    private ZoneControllerSdk zoneControllerSdk;
    private RoadControllerSdk roadControllerSdk;
    private RoadSegmentControllerSdk roadSegmentControllerSdk;
    private OffStreetParkingControllerSdk offStreetParkingControllerSdk;

    //Envío del token de firebase
    private DeviceTokenControllerSdk deviceTokenControllerSdk;
    private String fcmToken;
    private Context context;
    private ApplicationPreferences appPreferences;
    private ProgressBar progressBar;

    private ArrayList<Road> listRoad;
    private ArrayList<RoadSegment> listRoadSegment;
    private ArrayList<OffStreetParking> listOffStreetParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        context = this;
        appPreferences = new ApplicationPreferences();
        sqLiteDrivingApp = new SQLiteDrivingApp(this);


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

    @Override
    protected void onStart() {
        super.onStart();
        loadZones();
        loadRoads();
        loadParkings();
        loadSegments();
        checkGPS();
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

    private void loadZones(){
        Log.d("LOADING", "ZONES");
        zoneControllerSdk = new ZoneControllerSdk(context, this);
        listZone = sqLiteDrivingApp.getAllZone();
        if(listZone.size() <= 0){
            zoneControllerSdk.readAllZone();
        }
        return;
    }

    private void loadRoads (){
        Log.d("LOADING", "Roads");

        roadControllerSdk = new RoadControllerSdk(context, this);
        listRoad = sqLiteDrivingApp.getAllRoad();
        if(listZone.size() <= 0){
            roadControllerSdk.getAllRoad();
        }

        return;
    }

    private void loadSegments() {
        Log.d("LOADING", "segments");

        roadSegmentControllerSdk = new RoadSegmentControllerSdk(context, this);
        listRoadSegment = sqLiteDrivingApp.getAllRoadSegment();
        if(listRoadSegment.size() <= 0){
            roadSegmentControllerSdk.getAllRoadSegment();
        }
        return;

    }

    private void loadParkings () {
        Log.d("LOADING", "parkings");

        offStreetParkingControllerSdk = new OffStreetParkingControllerSdk(context, this);
        listOffStreetParking = sqLiteDrivingApp.getAllOffStreetParking();
        if (listOffStreetParking.size() <= 0){
            offStreetParkingControllerSdk.getAllOffStreetParking();
        }
        return;

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
                //checkGPS();
                break;
        }
    }
    private boolean setCredentialsIfExist(){
        return !(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN).equals("") && appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME).equals(""));
    }

    @Override
    public void getAllOffStreetParking(Response response) {
        //Log.i("AllOffStreetParking: ", "--------------------------------------------------------\n"+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
                OffStreetParking offStreetParking;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    offStreetParking = new OffStreetParking();
                    JSONObject object;
                    try {
                        object = jsonArray.getJSONObject(i);
                        offStreetParking.setIdOffStreetParking(object.getString("idOffStreetParking"));
                        offStreetParking.setType(object.getString("type"));
                        offStreetParking.setName(object.getString("name"));
                        offStreetParking.setCategory(object.getString("category"));
                        offStreetParking.setLocation(object.getString("location"));
                        offStreetParking.setDescription(object.getString("description"));
                        offStreetParking.setAreaServed(object.getString("areaServed"));
                        offStreetParking.setStatus(object.getString("status"));
                        sqLiteDrivingApp.createParking(offStreetParking);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    @Override
    public void getOffStreetParkingByAreaServed(Response response) {

    }

    @Override
    public void getAllRoad(Response response) {
        //Log.i("ALLROAD: ", "--------------------------------------------------------\n"+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
                Road road;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    road = new Road();
                    JSONObject object;
                    try {
                        object = jsonArray.getJSONObject(i);
                        road.setIdRoad(object.getString("idRoad"));
                        road.setType(object.getString("type"));
                        road.setName(object.getString("name"));
                        road.setDescription(object.getString("description"));
                        road.setResponsible(object.getString("responsible"));
                        road.setStatus(object.getString("status"));
                        sqLiteDrivingApp.createRoad(road);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    @Override
    public void getRoadByResponsible(Response response) {

    }

    @Override
    public void getAllRoadSegment(Response response) {
        switch (response.getHttpCode()){
            case 200:
                RoadSegment roadSegment;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    roadSegment = new RoadSegment();
                    JSONObject object = null;
                    try {
                        object = jsonArray.getJSONObject(i);
                        roadSegment.setIdRoadSegment(object.getString("idRoadSegment"));
                        roadSegment.setType(object.getString("type"));
                        roadSegment.setName(object.getString("name"));
                        roadSegment.setRefRoad(object.getString("refRoad"));
                        roadSegment.setLocation(object.getString("location"));
                        roadSegment.setStartPoint(object.getString("startPoint"));
                        roadSegment.setEndPoint(object.getString("endPoint"));
                        roadSegment.setLaneUsage(object.getString("laneUsage"));
                        roadSegment.setTotalLaneNumber(object.getInt("totalLaneNumber"));
                        roadSegment.setMaximumAllowedSpeed(object.getInt("maximumAllowedSpeed"));
                        roadSegment.setMinimumAllowedSpeed(object.getInt("minimumAllowedSpeed"));
                        roadSegment.setWidth(object.getInt("width"));
                        roadSegment.setStatus(object.getString("status"));
                        sqLiteDrivingApp.createRoadSegment(roadSegment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void getRoadSegmentByRefRoad(Response response) {

    }
}
