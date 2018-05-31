package mx.edu.cenidet.app.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;

import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import  mx.edu.cenidet.app.event.EventsDetect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

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

public class DeviceService extends Service implements DeviceController.DeviceResourceMethods, AlertController.AlertResourceMethods{
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

    //Medir distancias
    float[] distanceArray;
    public void onCreate() {
        super.onCreate();
        context = HomeActivity.MAIN_CONTEXT;
        hashMapSpeedFromTo = new HashMap<String, Double>();
        hashMapLatLngFromTo = new HashMap<String, Double>();
        distanceArray = new float[2];
        //uLocationService = new UsersLocationService(context,this);
        //id = HomeActivity.ID;
        deviceSensor = new DeviceSensor();
        alertController = new AlertController(this);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);

        //Sensor acelerometro y giroscopio
        /*mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //mSensorManager.registerListener(sensors, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(sensors, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);*/
        return START_NOT_STICKY;
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
                eventDetecion(location);
                //Log.i("STATUS", "hashMapLatLngFromTo EJECUCION----------------:\nlatitudeFrom: " + hashMapLatLngFromTo.get("latitudeFrom") + " longitudeFrom: " + hashMapLatLngFromTo.get("longitudeFrom") + " latitudeTo: " + hashMapLatLngFromTo.get("latitudeTo") + " longitudeTo: " + hashMapLatLngFromTo.get("longitudeTo"));
                //Log.i(STATUS, "GPS latitude: "+location.getLatitude()+" longitude: "+location.getLatitude());
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


    private final LocationListener locationListenerNetwork = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
                eventDetecion(location);
                //Log.i(STATUS, "NETWORK latitude: "+location.getLatitude()+" longitude: "+location.getLongitude());
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

    public static double calculateAcceleration(ArrayList<Double> values) {
        double acceleration = Math.sqrt(Math.pow(values.get(0), 2)
                + Math.pow(values.get(1), 2) + Math.pow(values.get(2), 2));
        return acceleration;
    }

    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "vertical";
            case Surface.ROTATION_90:
                return "horizontal";
            case Surface.ROTATION_180:
                return "vertical inversa";
            default:
                return "horizontal inversa";
        }
    }

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
        if (locationListenerNetwork != null){
            locationManager.removeUpdates(locationListenerNetwork);
        }
    }

    private void eventDetecion(Location location){
        if (location != null) {
            RoadSegment roadSegment;
            latitude = (double) location.getLatitude();
            longitude = (double) location.getLongitude();
            speedMS = (double) location.getSpeed();
            speedKmHr = (double) (location.getSpeed() * 3.6);
            Log.i("TIME", "--------------------------------"+location.getTime());

            Date date = new Date(location.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            Log.i("FECHA", "--------------------------------"+sdf.format(date));

            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(location.getTime());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            Log.i("HORA", "--------------------------------"+hour);

            //Logica para obtener location apartir de (location anterior) y location hasta (location actual)
            if (hashMapLatLngFromTo.isEmpty() || hashMapLatLngFromTo.size() == 0) {
                latitudeFrom = latitude;
                longitudeFrom = longitude;
                latitudeTo = latitude;
                longitudeTo = longitude;
                hashMapLatLngFromTo.put("latitudeFrom", latitudeFrom);
                hashMapLatLngFromTo.put("longitudeFrom", longitudeFrom);
                hashMapLatLngFromTo.put("latitudeTo", latitudeTo);
                hashMapLatLngFromTo.put("longitudeTo", longitudeTo);
            } else {
                latitudeFrom = hashMapLatLngFromTo.get("latitudeTo");
                longitudeFrom = hashMapLatLngFromTo.get("longitudeTo");
                latitudeTo = latitude;
                longitudeTo = longitude;
                hashMapLatLngFromTo.put("latitudeFrom", latitudeFrom);
                hashMapLatLngFromTo.put("longitudeFrom", longitudeFrom);
                hashMapLatLngFromTo.put("latitudeTo", latitudeTo);
                hashMapLatLngFromTo.put("longitudeTo", longitudeTo);
            }

            //Logica para obtener la velocidad anterior y actual
            if (hashMapSpeedFromTo.isEmpty() || hashMapSpeedFromTo.size() == 0) {
                speedFrom = speedKmHr;
                speedTo = speedKmHr;
                hashMapSpeedFromTo.put("speedFrom", speedFrom);
                hashMapSpeedFromTo.put("speedTo", speedTo);
            } else {
                speedFrom = hashMapSpeedFromTo.get("speedTo");
                speedTo = speedKmHr;
                hashMapSpeedFromTo.put("speedFrom", speedFrom);
                hashMapSpeedFromTo.put("speedTo", speedTo);
            }

            //Envía el Modelo de datos Device
            Log.i("COUNTSEND", "DEVICE..!"+countSendDevice+ "speedTo: "+hashMapSpeedFromTo.get("speedTo"));
            if(countSendDevice == 0){
                sendContext(latitude, longitude);
            }if (countSendDevice == 8){
                sendContext(latitude, longitude);
                countSendDevice = 0;
            }
            countSendDevice++;

            Intent intent = new Intent(Constants.SERVICE_CHANGE_LOCATION_DEVICE).putExtra(Constants.SERVICE_RESULT_LATITUDE, latitude)
                    .putExtra(Constants.SERVICE_RESULT_LONGITUDE, longitude).putExtra(Constants.SERVICE_RESULT_SPEED_MS, speedMS).putExtra(Constants.SERVICE_RESULT_SPEED_KMHR, speedKmHr);
            //obtiene el roadSegment en el que se encuentra el dipositivo movil.
            roadSegment = EventsFuntions.detectedRoadSegment(context, latitude, longitude);

            //Seencuentra en el parking entrada principal Apatzingan.
            //roadSegment = EventsFuntions.detectedRoadSegment(context, 18.879855,-99.221599);
            //intent.putExtra(Constants.ROAD_SEGMENT, roadSegment);

            //Apatzingán 1
            //roadSegment = EventsFuntions.detectedRoadSegment(context, 18.87942, -99.2208032);

            EventsDetect.suddenStop(speedMS, location.getTime(), context);
            if(roadSegment != null){
                Response response1 = new Response();
                //Codigo de la deteccion de eventos por cada roadSegment
                //Detección del exceso de velocidad.
                speeding(roadSegment.getMaximumAllowedSpeed(), hashMapSpeedFromTo.get("speedFrom"), hashMapSpeedFromTo.get("speedTo"), latitude, longitude);
               
                //INICIO TEST PRUEBAS-----------
                //Apatzingán 1
                    /*if(countSendAlert == 0){
                        //informacional
                        speedFrom = 20;
                        speedTo = 25;
                        speeding(roadSegment.getMaximumAllowedSpeed(), speedFrom, speedTo, latitude, longitude);
                    }else if(countSendAlert == 5){
                        //low
                        speedFrom = 20;
                        speedTo = 31;
                        speeding(roadSegment.getMaximumAllowedSpeed(), speedFrom, speedTo, latitude, longitude);
                    }else if(countSendAlert == 10){
                        //medium
                        speedFrom = 20;
                        speedTo = 40;
                        speeding(roadSegment.getMaximumAllowedSpeed(), speedFrom, speedTo, latitude, longitude);
                        countSendAlert = 0;
                    }
                    countSendAlert++;*/
                //FIN TEST PRUEBAS------
                intent.putExtra(Constants.ROAD_SEGMENT, roadSegment);
            }else {
                intent.putExtra(Constants.ROAD_SEGMENT, roadSegment);
            }
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(intent);
            //Se encuentra en un parking
            //EventsFuntions.detectedRoadSegment(context, 18.879698, -99.221604);
            //Fuera de los parking y en ningun roadSegment
            //EventsFuntions.detectedRoadSegment(context, 18.878270, -99.219773);
            //Fuera de los parking en un RoadSegment
            //EventsFuntions.detectedRoadSegment(context, 18.879508, -99.220777);
            //Fuera del parking en un RoadSegment
            //EventsFuntions.detectedRoadSegment(context, 18.87942, -99.2208032);




            /*// DETECTAR EVENTOS LOCATION-----
            latLngFrom = new LatLng(hashMapLatLngFromTo.get("latitudeFrom"), hashMapLatLngFromTo.get("longitudeFrom"));
            latLngTo = new LatLng(hashMapLatLngFromTo.get("latitudeTo"), hashMapLatLngFromTo.get("longitudeTo"));
            //latLngTo = new LatLng(18.876807, -99.219968);
            distance = SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);
            Log.i(STATUS, "DISTANCE 1: " + distance + "m");

            location.distanceBetween(latitudeLast, longitudeLast, latitude, longitude, distanceArray);
            Log.i(STATUS, "DISTANCE 2: " + distanceArray[0] + "km");*/
        } else {
            Log.i(STATUS, "Error obtener valores gps o network...!");
            //Toast.makeText(getBaseContext(), "Error GPS...!", Toast.LENGTH_LONG).show();
        }

    }

    public void speeding(double maximumSpeed, double speedFrom, double speedTo, double latitude, double longitude){
        String severitySpeeding =  EventsDetect.speeding(maximumSpeed, speedFrom, speedTo);

        String description = this.getString(R.string.message_alert_description_maximum_speed)+" "+maximumSpeed+"km/h. "+this.getString(R.string.message_alert_description_current_speed)+" "+speedTo+"km/h.";
        String severity = "";

        String subCategory = "UnauthorizedSpeeDetection";

        switch (severitySpeeding){
            case "tolerance":
                break;
            case "informational":
                severity = "informational";
                structureAlert(description, severity, subCategory, latitude, longitude);
                break;
            case "low":
                severity = "low";
                structureAlert(description, severity, subCategory, latitude, longitude);
                break;
            case "medium":
                severity = "medium";
                structureAlert(description, severity, subCategory, latitude, longitude);
                break;
            case "high":
                severity = "high";
                structureAlert(description, severity, subCategory, latitude, longitude);
                break;
            case "critical":
                severity = "critical";
                structureAlert(description, severity, subCategory, latitude, longitude);
                break;
        }
    }

    /**
     * @param description La velocidad máxima permitida es 20 km/h. Velocidad actual del vehiculo es 25 km/h.
     * @param severity
     * @param subCategory UnauthorizedSpeeDetection
     * @param latitude
     * @param longitude
     */
    private void structureAlert(String description, String severity, String subCategory, double latitude, double longitude){
        Alert alert = new Alert();
        alert.setId(new DevicePropertiesFunctions().getAlertId(context));
        alert.getAlertSource().setValue(new DevicePropertiesFunctions().getDeviceId(context));
        alert.getCategory().setValue("security");
        alert.getDateObserved().setValue(Functions.getActualDate());
        alert.getDescription().setValue(description);
        alert.getLocation().setValue(latitude+", "+longitude);
        alert.getSeverity().setValue(severity);
        alert.getSubCategory().setValue(subCategory);
        alert.getValidFrom().setValue(Functions.getActualDate());
        alert.getValidTo().setValue(Functions.getActualDate());
        try {
           alertController.createEntity(context, alert.getId(), alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(context, "Accident, position "+auxPosition+" Lat, Lon :"+latitude+", "+longitude, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param code codigo que informa si se debe generar una nueva alerta (1), actializar el estado de una alerta(2) o dar por finalizada la alerta (3). El codigo 3 solo se utiliza con alertas que perduran en el tiempo
     * @param data Array que contiene los datos con los cuales se debe llenar la alerta. ¡¡¡¡¡ IMPORTANTE !!!!!  debe cambiarse a un objeto de tipo alerta
     */
    private void sentAlert(int code, String ... data){

    }

    private void sendContext(Double latitude, Double longitude){
        //Objeto Device
        device = createDevice(latitude, longitude);
        tblTemp.setKeyword(device.getId());
        deviceValidateExists = sqLiteController.getByKeywordTempCreate(tblTemp);
        if (deviceValidateExists == null) {
            //Obtener los datos para para cargarlos en el Device
            try {
                deviceController.createEntity(context, device.getId(),device);
            } catch (Exception e) {
                Log.i(STATUS, "Exception Device...!");
                //Toast.makeText(getBaseContext(), "Exception Device...!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Objeto Device para actualizarlo
            deviceUpdateModel = updateDevice(latitude, longitude);
            try {
                deviceController.updateEntity(context, device.getId(), deviceUpdateModel);
            } catch (Exception e) {
                Log.i(STATUS, "Exception Device Update...!");
                //Toast.makeText(getBaseContext(), "Exception Device Update...!", Toast.LENGTH_LONG).show();
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
        //Toast.makeText(this, "Code Device: " + response.getHttpCode(), Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(this, "Successful Device Update...!", Toast.LENGTH_SHORT).show();
            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "Successful Device Update...!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            Log.i("UPDATE: ", "Successful Device Update...!");

        } else{
            //Toast.makeText(this, "Error updating...!"+response.getHttpCode(), Toast.LENGTH_SHORT).show();
            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "Error updating...!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            Log.i("ERROR UPDATE: ", "Error updating...!");

        }
    }

    @Override
    public void onUpdateEntitySaveOffline(Response response) {

    }

    @Override
    public void onDeleteEntity(Response response) {

    }

    @Override
    public void onGetEntities(Response response) {

    }

    @Override
    public void onCreateEntityAlert(Response response) {
        Log.i("EVENT: ", "RESPUESTA DEL ENVIO DE LA ALERTA: "+response.getHttpCode());
    }

    @Override
    public void onUpdateEntityAlert(Response response) {

    }

    @Override
    public void onGetEntitiesAlert(Response response) {

    }
}
