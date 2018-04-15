package mx.edu.cenidet.drivingapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.fragments.HomeFragment;
import www.fiware.org.ngsi.datamodel.entity.DeviceSensor;
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
    //Dectar Campus
    //private ArrayList<Campus> listCampus;
    private ArrayList<Zone> listZone;
    private ArrayList<LatLng> listLocation;
    private SQLiteDrivingApp sqLiteDrivingApp;
    //private Campus campus = null, auxCampus = null;
    private Zone zone = null, auxZone = null;
    private boolean auxStatusLocation = false;
    private ApplicationPreferences applicationPreferences;
    private Context context;

    public SendDataService(SendDataService.SendDataMethods sendDataMethods){
        context = context = HomeActivity.MAIN_CONTEXT;
        this.sendDataMethods = sendDataMethods;
        filter = new IntentFilter(Constants.SERVICE_CHANGE_LOCATION_DEVICE);
        filter.addAction(Constants.SERVICE_RUNNING_SENSORS);
        filter.addAction(Constants.SERVICE_CHANGE_WRONG_WAY);
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        applicationPreferences = new ApplicationPreferences();
        //Detectar Campus
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        listZone =  sqLiteDrivingApp.getAllZone();
        //campus = new Campus();
    }


    public interface SendDataMethods{
        void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr);
        void detectZone(Zone zone, boolean statusLocation);
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
                    sendDataMethods.sendLocationSpeed(latitude, longitude, speedMS, speedKmHr);
                    //Detecta Zona
                    detectZone();
                    //Log.i("STATUS 1", "VIEW Latitude: "+latitude+" Longitude: "+longitude+" Velocidad: "+speedMS+"m/s  Velocidad: "+speedKmHr+"km/hr");
                    break;
                case Constants.SERVICE_RUNNING_SENSORS:
                    if ((DeviceSensor) intent.getExtras().get(Constants.ACCELEROMETER_RESULT_SENSORS) != null) {
                        DeviceSensor deviceSensor = (DeviceSensor) intent.getExtras().get(Constants.ACCELEROMETER_RESULT_SENSORS);
                        //Log.i("json ACCELEROMETER: ", ""+functions.checkForNewsAttributes(deviceSensor));
                        //Log.i("Receiver acce: ", " ax: " + deviceSensor.getData().getValue().get(0) + " ay: " + deviceSensor.getData().getValue().get(1) + " az: " + deviceSensor.getData().getValue().get(2)+" id: " + deviceSensor.getId() + " type: " + deviceSensor.getType());
                       // Log.i("Receiver acce: ", " ax: " + deviceSensor.getData().getValue().get(0) + " ay: " + deviceSensor.getData().getValue().get(1) + " az: " + deviceSensor.getData().getValue().get(2));
                        deviceSensor = null;
                    }else if((DeviceSensor) intent.getExtras().get(Constants.GYROSCOPE_RESULT_SENSORS) != null){
                        DeviceSensor deviceSensor = (DeviceSensor) intent.getExtras().get(Constants.GYROSCOPE_RESULT_SENSORS);
                        //Log.i("Receiver gyro: ", " gx: " + deviceSensor.getData().getValue().get(0) + " gy: " + deviceSensor.getData().getValue().get(1) + " gz: " + deviceSensor.getData().getValue().get(2)+" id: " + deviceSensor.getId() + " type: " + deviceSensor.getType());
                        //Log.i("Receiver gyro: ", " gx: " + deviceSensor.getData().getValue().get(0) + " gy: " + deviceSensor.getData().getValue().get(1) + " gz: " + deviceSensor.getData().getValue().get(2));
                    }
                    break;
                case Constants.SERVICE_CHANGE_WRONG_WAY:
                        if(intent.getExtras().getString(Constants.SERVICE_RESULT_WRONG_WAY_OUTPUT) != null){
                            sendDataMethods.sendEvent(intent.getExtras().getString(Constants.SERVICE_RESULT_WRONG_WAY_OUTPUT));
                        }else {

                        }
                    break;
            }
        }
    }

    private void detectZone(){
        if(listZone.size() > 0){
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitudePolygon, longitudePolygon;
            String[] subString;
            boolean statusLocation;
            auxZone = null;
            auxStatusLocation = false;
            for(int i=0; i<listZone.size(); i++){
                listLocation = new ArrayList<>();
                zone = new Zone();
                zone.setIdZone(listZone.get(i).getIdZone());
                zone.setType(listZone.get(i).getType());
                zone.setRefBuildingType(listZone.get(i).getRefBuildingType());
                zone.setName(listZone.get(i).getName());
                zone.setAddress(listZone.get(i).getAddress());
                zone.setCategory(listZone.get(i).getCategory());
                zone.setLocation(listZone.get(i).getLocation());
                zone.setCenterPoint(listZone.get(i).getCenterPoint());
                zone.setDescription(listZone.get(i).getDescription());
                zone.setDateCreated(listZone.get(i).getDateCreated());
                zone.setDateModified(listZone.get(i).getDateModified());
                zone.setStatus(listZone.get(i).getStatus());
                //Log.i("Status: ", "Campus name: "+listCampus.get(i).getName());
                try{
                    arrayLocation = new JSONArray(listZone.get(i).getLocation().getValue());
                    for (int j=0; j<arrayLocation.length(); j++){
                        originalString = arrayLocation.get(j).toString();
                        clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                        subString =  clearString.split(",");
                        latitudePolygon = Double.parseDouble(subString[0]);
                        longitudePolygon = Double.parseDouble(subString[1]);
                        listLocation.add(new LatLng(latitudePolygon,longitudePolygon));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                statusLocation = PolyUtil.containsLocation(new LatLng(latitude,longitude), listLocation, false);
                //statusLocation = PolyUtil.containsLocation(new LatLng(18.870032,-99.211869), listLocation, false);
                if(statusLocation == true){
                    auxZone = zone;
                    auxStatusLocation = statusLocation;
                }
            }
            //Logica para enviar el si se encuentra dentro del campus...
            if(auxZone == null && auxStatusLocation == false){
                sendDataMethods.detectZone(auxZone, auxStatusLocation);
                applicationPreferences.saveOnPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE, "undetectedZone");
                Log.i("CAMPUS: ","EXAMPLE 1......!"+applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE));
            }else{
                sendDataMethods.detectZone(auxZone, auxStatusLocation);
                applicationPreferences.saveOnPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE, auxZone.getIdZone());
                Log.i("CAMPUS: ","EXAMPLE 2......!"+applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE));
            }

        }else{
            //Log.i("STATUS: ","Carga los campus en el primer inicio de sesión");
            listZone =  sqLiteDrivingApp.getAllZone();

        }
    }
}
