package mx.edu.cenidet.app.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import  mx.edu.cenidet.app.event.EventsDetect;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.event.EventsFuntions;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.controller.DeviceController;
import www.fiware.org.ngsi.controller.SQLiteController;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.Device;
import www.fiware.org.ngsi.datamodel.entity.DeviceSensor;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.model.DeviceUpdateModel;
import www.fiware.org.ngsi.db.sqlite.entity.Tbl_Data_Temp;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.Constants;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;

/**
 * Created by Cipriano on 3/3/2018.
 */

public class DeviceService extends Service implements DeviceController.DeviceResourceMethods{
    private Context context;
    private static final String STATUS = "STATUS";
    //private double longitudeGPS, latitudeGPS;
    private double latitude, longitude;
    private double longitudeNetwork, latitudeNetwork;
    private double speedMS;
    private double speedKmHr;
    private LocationManager locationManager;
    //private UsersLocationService uLocationService;
    private int id;
    private List<ArrayList> locationCoordsRoadSeg;

    //Modelo de datos Device, DeviceModel.
    private DeviceController deviceController;
    private DevicePropertiesFunctions deviceProperties;
    private DeviceUpdateModel deviceUpdateModel;
    private ApplicationPreferences appPreferences;
    private String owner;
    private SQLiteController sqLiteController;
    private Tbl_Data_Temp tblTemp;
    private Tbl_Data_Temp deviceValidateExists;
    private Device device;
    private int countSendDevice = 0;
    private int countSendAlert = 0;


    //Giroscopio y acelerometro
    private double ax, ay, az, gx, gy, gz;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private ArrayList<Double> listValueSensor;
    private String deviceId, androidId;
    private DeviceSensor deviceSensor;
    private AlertController alertController;
    private double speedMin = 0.0, speedMax = 3.0, speedLast = 0.0;
    private  double latitudeLast, longitudeLast;
    //variables que se utilizaran en el calculo paradas repentinas
    private LatLng latLngFrom, latLngTo;
    private double latitudeFrom, longitudeFrom, latitudeTo, longitudeTo;
    private double distance;
    private double speedFrom, speedTo;
    private HashMap<String, Double> hashMapSpeedFromTo;
    private HashMap<String, Double> hashMapLatLngFromTo;
    private long lastUpdateAcc = 0, lastUpdateGPS = 0;



    /** variables de control y configuración**/
    private boolean isDrivingUser=true; // Variable para determinar si una persona va manejando
    private boolean isMonitoring=false; // Variable para determinar si se deben monitorear los eventos relacionados con la velocidad
    private boolean isInArea=true; //Variable para saber si una persona se encuentra dentro de un area
    private boolean isInParking=true; //Variable para verificar si la persona se encuentra en un area que tiene calles
    private boolean isUnauthorizedSpeed=false;

    private double minimumSpeedToAsk=4.5; // Valor minimo de velocidad al que se preguntara si una persona va manejando.
    private double minimumSpeedForAutomaticCalculation=7.5; // Valor minimo de la velocidad al que se asumira que la persona va manejando
    private double timeStampLastReadingGPS=0.0; //Marca de tiempo que permite identificar el tiempo de la ultima lectura realizada, el valor esta en milisegundos.
    private double timeUpdateAlert=5; // Tiempo en segundos para actualizar una alerta
    private double timeMinInferiorSpeed=180; //Tiempo minimo en segundos para determinarlo como una velocidad por debajo del limite minimo establecido
    private double timeStampLastMinSpeedReading=-1.0; //Marca de tiempo que permite identificar el tiempo de la ultima lectura realizada, el valor esta en milisegundos.

    private EventsDetect events ; 
    
    //Medir distancias
    float[] distanceArray;

    public void onCreate() {
        super.onCreate();
        context = HomeActivity.MAIN_CONTEXT;
        hashMapSpeedFromTo = new HashMap<String, Double>();
        hashMapLatLngFromTo = new HashMap<String, Double>();
        distanceArray = new float[2];
        deviceSensor = new DeviceSensor();
        //Modelo de datos Device, DeviceModel.
        deviceProperties = new DevicePropertiesFunctions();
        deviceUpdateModel = new DeviceUpdateModel();
        appPreferences = new ApplicationPreferences();
        deviceController = new DeviceController(this);
        tblTemp = new Tbl_Data_Temp();
        sqLiteController = new SQLiteController(context);
        device = new Device();

        if (appPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_ID) != null){
            owner = appPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_ID);
        }else{
            owner = "undefined";
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListenerGPS);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListenerNetwork);
        
        
        return START_NOT_STICKY;
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
                eventDetecion(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    /*private final LocationListener locationListenerNetwork = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
                eventDetecion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(STATUS, "Service destroyed...!");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationListenerGPS != null){
            locationManager.removeUpdates(locationListenerGPS);
        }
        /*if (locationListenerNetwork != null){
            locationManager.removeUpdates(locationListenerNetwork);
        }*/
    }

    private void eventDetecion(Location location){

        Alert suddenStopAlert = null;
        String StopingStatus = "";

        if (location != null) {
            
            RoadSegment roadSegment;
            latitude = (double) location.getLatitude();
            longitude = (double) location.getLongitude();
            speedMS = (double) location.getSpeed();
            speedKmHr = (double) (location.getSpeed() * 3.6);

            //Envía el Modelo de datos Device
            if(countSendDevice == 0){
                sendContext(latitude, longitude);
            }if (countSendDevice == 8){
                sendContext(latitude, longitude);
                countSendDevice = 0;
            }

            countSendDevice++;

            Intent intent = new Intent(Constants.SERVICE_CHANGE_LOCATION_DEVICE)
                .putExtra(Constants.SERVICE_RESULT_LATITUDE, latitude)
                .putExtra(Constants.SERVICE_RESULT_LONGITUDE, longitude)
                .putExtra(Constants.SERVICE_RESULT_SPEED_MS, speedMS)
                .putExtra(Constants.SERVICE_RESULT_SPEED_KMHR, speedKmHr);
            
            roadSegment = EventsFuntions.detectedRoadSegment(context, latitude, longitude);

            if(roadSegment != null){

                String[] laneUsages, locationRoadSegment;
                Log.d("ROAD SEGMENT LOCATION", roadSegment.getLocation());
                laneUsages = roadSegment.getLaneUsage().split(",");
                locationRoadSegment = roadSegment.getLocation().substring(1, roadSegment.getLocation().length()-1).split(",");

                for (String item:locationRoadSegment) {
                    Log.d("ROAD SEGMENT ITEM", item);
                }
                if(laneUsages.length == 1) {
                    String[] startStrings, endStrings;
                    startStrings =  roadSegment.getStartPoint().split(",");
                    endStrings =  roadSegment.getEndPoint().split(",");

                    LatLng startPoint = new LatLng(
                        Double.parseDouble(startStrings[0].substring(1,startStrings[0].length())),
                        Double.parseDouble(startStrings[1].substring(0,startStrings[1].length()-1)));

                    LatLng endPoint = new LatLng(
                        Double.parseDouble(endStrings[0].substring(1, endStrings[0].length())),
                        Double.parseDouble(endStrings[1].substring(0,endStrings[1].length()-1)));


                }
            }
            intent.putExtra(Constants.SERVICE_RESULT_STOPING, StopingStatus);
            intent.putExtra(Constants.ROAD_SEGMENT, roadSegment);
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(intent);

        } else {
            Log.i(STATUS, "Error obtener valores gps o network...!");
        }

    }


    private void sendContext(Double latitude, Double longitude){
        device = createDevice(latitude, longitude);
        tblTemp.setKeyword(device.getId());
        deviceValidateExists = sqLiteController.getByKeywordTempCreate(tblTemp);
        if (deviceValidateExists == null) {
            //Obtener los datos para para cargarlos en el Device
            try {
                deviceController.createEntity(context, device.getId(),device);
            } catch (Exception e) {
                Log.i(STATUS, "Exception Device...!");
            }
        } else {
            //Objeto Device para actualizarlo
            deviceUpdateModel = updateDevice(latitude, longitude);
            try {
                deviceController.updateEntity(context, device.getId(), deviceUpdateModel);
            } catch (Exception e) {
                Log.i(STATUS, "Exception Device Update...!");
            }
        }
    }

    private Device createDevice(Double latitude, Double longitude){
        String actualDate = Functions.getActualDate();
        Device device = new Device();
        device.setId(deviceProperties.getDeviceId(context));
        device.getCategory().setValue("smartphone");
        device.getOsVersion().setValue(deviceProperties.getOSVersion());
        device.getBatteryLevel().setValue(deviceProperties.getBatteryLevel(context));
        device.getDateCreated().setValue(actualDate);
        device.getDateModified().setValue(actualDate);
        device.getIpAddress().setValue(deviceProperties.getIPAddress(true));
        device.getRefDeviceModel().setValue(deviceProperties.getDeviceModelId());
        device.getSerialNumber().setValue(deviceProperties.getSerialNumber());
        device.getLocation().setValue(latitude + ", " + longitude);
        device.getOwner().setValue(owner);
        return device;
    }

    private DeviceUpdateModel updateDevice(Double latitude, Double longitude){
        String actualDate = Functions.getActualDate();
        DeviceUpdateModel deviceUpdateModel = new DeviceUpdateModel();
        deviceUpdateModel.getCategory().setValue("smartphone");
        deviceUpdateModel.getOsVersion().setValue(deviceProperties.getOSVersion());
        deviceUpdateModel.getBatteryLevel().setValue(deviceProperties.getBatteryLevel(context));
        deviceUpdateModel.getDateModified().setValue(""+actualDate);
        deviceUpdateModel.getIpAddress().setValue(deviceProperties.getIPAddress(true));
        deviceUpdateModel.getRefDeviceModel().setValue(deviceProperties.getDeviceModelId());
        deviceUpdateModel.getSerialNumber().setValue(deviceProperties.getSerialNumber());
        deviceUpdateModel.getOwner().setValue(owner);
        deviceUpdateModel.getLocation().setValue(latitude + ", " + longitude);
        return  deviceUpdateModel;
    }

    @Override
    public void onCreateEntity(Response response) {
        if(response.getHttpCode() == 201){

            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "Entity Device created successfully...!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            sqLiteController.updateStatusActiveByKeywordTempCreate(device.getId());
            Log.i("CONTEXT 201: ", "Entity Device created successfully...!");
            Log.i("ID device: ", device.getId());

        }else if(response.getHttpCode() == 422){

            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "The Device already exists....!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            sqLiteController.updateStatusActiveByKeywordTempCreate(device.getId());
            Log.i("CONTEXT 422: ", "The Device already exists....!");
            Log.i("ID device: ", device.getId());

        }else{

            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "Error sending data...!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            Log.i("ERROR CREATE: ", "Error sending data...!");

        }
    }

    @Override
    public void onCreateEntitySaveOffline(Response response) {
    }

    @Override
    public void onUpdateEntity(Response response) {

        if(response.getHttpCode()==204 || response.getHttpCode()==200){
            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "Successful Device Update...!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            Log.i("UPDATE: ", "Successful Device Update...!");

        } else{
            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "Error updating...!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            Log.i("ERROR UPDATE: ", "Error updating...!");

        }
    }

    @Override
    public void onUpdateEntitySaveOffline(Response response) {}
    @Override
    public void onDeleteEntity(Response response) {}
    @Override
    public void onGetEntities(Response response) {}

}
