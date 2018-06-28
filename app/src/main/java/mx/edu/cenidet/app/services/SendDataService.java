package mx.edu.cenidet.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.event.EventsFuntions;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.Constants;

/**
 * Created by Cipriano on 3/18/2018.
 */

public class SendDataService {
    private SendDataMethods sendDataMethods;
    private double latitude, longitude;
    private double speedMS, speedKmHr;
    private IntentFilter filter;
    private String stopingStatus;
    //Dectar Campus
    //private ArrayList<Campus> listCampus;
    private ArrayList<Zone> listZone;
    private ArrayList<LatLng> listLocation;
    private SQLiteDrivingApp sqLiteDrivingApp;
    //private Campus campus = null, auxCampus = null;
    private Zone zone = null;
    private boolean auxStatusLocation = false;
    private ApplicationPreferences applicationPreferences;
    private Context context;

    public SendDataService(SendDataService.SendDataMethods sendDataMethods){
        context = HomeActivity.MAIN_CONTEXT;
        this.sendDataMethods = sendDataMethods;
        filter = new IntentFilter(Constants.SERVICE_CHANGE_LOCATION_DEVICE);
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        applicationPreferences = new ApplicationPreferences();
        //Detectar Campus
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        listZone =  sqLiteDrivingApp.getAllZone();
        //campus = new Campus();
        Log.d("SEND DATA", ""+ speedMS);
    }


    public interface SendDataMethods{
        void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr);
        void detectZone(Zone zone, boolean statusLocation);
        void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment);
        void sendDataAccelerometer(double ax, double ay, double az);
        void sendEvent(String event);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.SERVICE_CHANGE_LOCATION_DEVICE:
                    latitude = intent.getDoubleExtra(Constants.SERVICE_RESULT_LATITUDE, 0);
                    longitude = intent.getDoubleExtra(Constants.SERVICE_RESULT_LONGITUDE, 0);
                    speedMS = intent.getDoubleExtra(Constants.SERVICE_RESULT_SPEED_MS, 0);
                    speedKmHr = intent.getDoubleExtra(Constants.SERVICE_RESULT_SPEED_KMHR, 0);

                    stopingStatus = intent.getStringExtra(Constants.SERVICE_RESULT_STOPING);
                    if(stopingStatus != "") {
                        sendDataMethods.sendEvent(stopingStatus);
                    }

                    sendDataMethods.sendLocationSpeed(latitude, longitude, speedMS, speedKmHr);

                    //Detecta Zona
                    if(listZone.size() > 0) {
                        detectZone(latitude, longitude, listZone);
                    }else {
                        listZone =  sqLiteDrivingApp.getAllZone();
                    }
                    RoadSegment roadSegment = (RoadSegment) intent.getExtras().get(Constants.ROAD_SEGMENT);
                    if(roadSegment != null){
                        sendDataMethods.detectRoadSegment(latitude, longitude, roadSegment);
                    }else {
                        sendDataMethods.detectRoadSegment(latitude, longitude, roadSegment);
                    }
                    break;
            }
        }
    }
    private void detectZone(double latitude, double longitude, ArrayList<Zone> listZone){
        zone = EventsFuntions.detectedZone(latitude, longitude, listZone);
        if(zone == null){
            sendDataMethods.detectZone(zone, false);
            applicationPreferences.saveOnPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE, "undetectedZone");
        }else{
            sendDataMethods.detectZone(zone, true);
            applicationPreferences.saveOnPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE, zone.getIdZone());
        }
    }
}
