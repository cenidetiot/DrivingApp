package mx.edu.cenidet.drivingapp.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import  mx.edu.cenidet.drivingapp.event.EventsDetect;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import www.fiware.org.ngsi.controller.DeviceController;
import www.fiware.org.ngsi.controller.SQLiteController;
import www.fiware.org.ngsi.datamodel.entity.Device;
import www.fiware.org.ngsi.datamodel.entity.DeviceModel;
import www.fiware.org.ngsi.datamodel.entity.DeviceSensor;
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
    private double longitudeGPS, latitudeGPS;
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


    //Giroscopio y acelerometro
    private double ax, ay, az, gx, gy, gz;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private ArrayList<Double> listValueSensor;
    private String deviceId, androidId;
    DeviceSensor deviceSensor;
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListenerNetwork);

        //Sensor acelerometro y giroscopio
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(sensors, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensors, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        return START_NOT_STICKY;
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                eventDetecion(location);

                /*UserLocation userLocation = updateUserLocation(HomeActivity.ID, latitudeGPS, longitudeGPS);
                try {
                    uLocationService.updateUserLocation(userLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                Log.i(STATUS, "GPS latitude: "+location.getLatitude()+" longitude: "+location.getLatitude());
            }else {
                Log.i(STATUS, "Error GPS...!");
                //Toast.makeText(getBaseContext(), "Error GPS...!", Toast.LENGTH_LONG).show();
            }
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
            if (location != null) {
                eventDetecion(location);
                /*UserLocation userLocation = updateUserLocation(HomeActivity.ID, latitudeNetwork, longitudeNetwork);
                try {
                    uLocationService.updateUserLocation(userLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                 Log.i(STATUS, "NETWORK latitude: "+location.getLatitude()+" longitude: "+location.getLongitude());
            }else{
                Log.i(STATUS, "Error Network...!");
            }
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

    //obtener los datos del sensor de acelerometro y giroscopio
    private final SensorEventListener sensors = new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            synchronized(this) {
                //Fecha
                Date date = new Date();
                DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                if (sensorEvent.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
                    //int id = sensorEvent.sensor.getId();
                    String name = sensorEvent.sensor.getName();
                    int type = sensorEvent.sensor.getType();
                    String typeString = sensorEvent.sensor.getStringType();
                    String vendor = sensorEvent.sensor.getVendor();
                    int version = sensorEvent.sensor.getVersion();
                    float power = sensorEvent.sensor.getPower();
                    long current_time = sensorEvent.timestamp;

                    ax = sensorEvent.values[0];
                    ay = sensorEvent.values[1];
                    az = sensorEvent.values[2];

                    listValueSensor = new ArrayList<Double>();
                    listValueSensor.add(ax);
                    listValueSensor.add(ay);
                    listValueSensor.add(az);

                    //DeviceSensor deviceSensor = new DeviceSensor();

                    DeviceSensor deviceSensor = new DeviceSensor();
                    deviceSensor.setId("Accelerometer_Smartphone_"+vendor+"_"+version+"_"+androidId);
                    deviceSensor.setType("Device");
                    deviceSensor.getCategory().setValue("sensor");
                    deviceSensor.getFunction().setValue("sensing");
                    deviceSensor.getControlledProperty().setValue(name);
                    deviceSensor.getData().setValue(listValueSensor);
                    deviceSensor.getDateCreated().setValue(""+formatDate.format(date));
                    deviceSensor.getRefDevice().setValue(deviceId);

                    //almacenar la información en la DB local del dispositivo movil

                    //LOGICA PARA REALIZAR LOS CALCULOS CON EL ACELEROMETRO....

                    Intent localIntent = new Intent(Constants.SERVICE_RUNNING_SENSORS).putExtra(Constants.ACCELEROMETER_RESULT_SENSORS, deviceSensor);
                    LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);

                    //Log.i("json ACCELEROMETER: ", ""+functions.checkForNewsAttributes(deviceSensor));
                    //Log.i("ACCELEROMETER", "AX "+ax+" AY "+ay+" AZ "+az);
                    //Log.i("ACCELEROMETER", "AX "+ax+" AY "+ay+" AZ "+az +" -time: "+current_time+" -Id: "+id+ " -name: "+name+" -type: "+type+" -typeString: "+typeString+" -vendor: "+vendor+" -versión: "+version+" -power: "+power);

                }else if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE){
                    //int id = sensorEvent.sensor.getId();
                    String name = sensorEvent.sensor.getName();
                    int type = sensorEvent.sensor.getType();
                    String typeString = sensorEvent.sensor.getStringType();
                    String vendor = sensorEvent.sensor.getVendor();
                    int version = sensorEvent.sensor.getVersion();
                    float power = sensorEvent.sensor.getPower();
                    long current_time = sensorEvent.timestamp;
                    gx = sensorEvent.values[0];
                    gy = sensorEvent.values[1];
                    gz = sensorEvent.values[2];

                    listValueSensor = new ArrayList<Double>();
                    listValueSensor.add(gx);
                    listValueSensor.add(gy);
                    listValueSensor.add(gz);

                    DeviceSensor deviceSensor = new DeviceSensor();
                    deviceSensor.setId("Gyroscope_Smartphone_"+vendor+"_"+version+"_"+androidId);
                    deviceSensor.setType("Device");
                    deviceSensor.getCategory().setValue("sensor");
                    deviceSensor.getFunction().setValue("sensing");
                    deviceSensor.getControlledProperty().setValue(name);
                    deviceSensor.getData().setValue(listValueSensor);
                    deviceSensor.getDateCreated().setValue(""+formatDate.format(date));
                    deviceSensor.getRefDevice().setValue(deviceId);

                    //almacenar la información en la DB local del dispositivo movil

                    Intent localIntent = new Intent(Constants.SERVICE_RUNNING_SENSORS).putExtra(Constants.GYROSCOPE_RESULT_SENSORS, deviceSensor);
                    LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
                    //Log.i("GYROSCOPE", "AX "+gx+" AY "+gy+" AZ "+gz);
                     //Log.i("GYROSCOPE", "AX "+gx+" AY "+gy+" AZ "+gz +" -time: "+current_time+" -Id: "+id+ " -name: "+name+" -type: "+type+" -typeString: "+typeString+" -vendor: "+vendor+" -versión: "+version+" -power: "+power+" ORIENTATION: "+getRotation(context));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

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
            if(isInArea){
                if(isInParking){
                    speedKmHr = (double) (location.getSpeed() * 3.6);
                    if(isDrivingUser){
                        isMonitoring=true;
                    } else if(speedKmHr>minimumSpeedToAsk && speedKmHr< minimumSpeedForAutomaticCalculation){
                        // Activar funcion de preguntar
                        isMonitoring=true;
                    }else if(speedKmHr>= minimumSpeedForAutomaticCalculation){
                        isMonitoring=true;
                    }else{
                        isMonitoring=false;
                    }
                }else{
                    //LOGICA PARA CUANDO LA PERSONA SE ENCUENTRA EN EL AREA PERO NO ESTA EN UNA ZONA QUE TENGA CALLES
                    isMonitoring=false;
                }
            }
            else{
                // LOGICA PARA CUANDO EL TELEFONO NO SE ENCUENTRA EN EL AREA
                isMonitoring=false;
            }



            if(isMonitoring) {

                latitudeGPS = (double) location.getLatitude();
                longitudeGPS = (double) location.getLongitude();
                speedMS = (double) location.getSpeed();



                Intent intent = new Intent(Constants.SERVICE_CHANGE_LOCATION_DEVICE).putExtra(Constants.SERVICE_RESULT_LATITUDE, latitudeGPS)
                        .putExtra(Constants.SERVICE_RESULT_LONGITUDE, longitudeGPS).putExtra(Constants.SERVICE_RESULT_SPEED_MS, speedMS).putExtra(Constants.SERVICE_RESULT_SPEED_KMHR, speedKmHr);
                LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(intent);

                //Envía el Modelo de datos Device
                sendContext(latitudeGPS, longitudeGPS);

                //Log.i(STATUS, "GPS LATITUDE: " + latitudeGPS + " longitude: " + longitudeGPS);
                //Logica para obtener location apartir de (location anterior) y location hasta (location actual)
                if (hashMapLatLngFromTo.isEmpty() || hashMapLatLngFromTo.size() == 0) {
                    latitudeFrom = latitudeGPS;
                    longitudeFrom = longitudeGPS;
                    latitudeTo = latitudeGPS;
                    longitudeTo = longitudeGPS;
                    hashMapLatLngFromTo.put("latitudeFrom", latitudeFrom);
                    hashMapLatLngFromTo.put("longitudeFrom", longitudeFrom);
                    hashMapLatLngFromTo.put("latitudeTo", latitudeTo);
                    hashMapLatLngFromTo.put("longitudeTo", longitudeTo);
                    Log.i("STATUS", "hashMapLatLngFromTo INICIO VACIO:\nlatitudeFrom: " + hashMapLatLngFromTo.get("latitudeFrom") + " longitudeFrom: " + hashMapLatLngFromTo.get("longitudeFrom") + " latitudeTo: " + hashMapLatLngFromTo.get("latitudeTo") + " longitudeTo: " + hashMapLatLngFromTo.get("longitudeTo"));
                } else {
                    latitudeFrom = hashMapLatLngFromTo.get("latitudeTo");
                    longitudeFrom = hashMapLatLngFromTo.get("longitudeTo");
                    latitudeTo = latitudeGPS;
                    longitudeTo = longitudeGPS;
                    hashMapLatLngFromTo.put("latitudeFrom", latitudeFrom);
                    hashMapLatLngFromTo.put("longitudeFrom", longitudeFrom);
                    hashMapLatLngFromTo.put("latitudeTo", latitudeTo);
                    hashMapLatLngFromTo.put("longitudeTo", longitudeTo);

                    Log.i("STATUS", "hashMapLatLngFromTo NO VACIO:\nlatitudeFrom: " + hashMapLatLngFromTo.get("latitudeFrom") + " longitudeFrom: " + hashMapLatLngFromTo.get("longitudeFrom") + " latitudeTo: " + hashMapLatLngFromTo.get("latitudeTo") + " longitudeTo: " + hashMapLatLngFromTo.get("longitudeTo"));
                }

                String salida= EventsDetect.oppositeDirectionDisplacement(new LatLng(hashMapLatLngFromTo.get("latitudeFrom"), hashMapLatLngFromTo.get("longitudeFrom")),
                        new LatLng(hashMapLatLngFromTo.get("latitudeTo"), hashMapLatLngFromTo.get("longitudeTo")),new LatLng(18.8797180,-99.2216666),new LatLng(18.8794591,-99.22154322));

                //if(salida.equals("wrongWay")){
                    Intent intentWrongWay = new Intent(Constants.SERVICE_CHANGE_WRONG_WAY).putExtra(Constants.SERVICE_RESULT_WRONG_WAY_OUTPUT, salida);
                    LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(intentWrongWay);
              /*  }else{
                    Intent intentWrongWay = new Intent(Constants.SERVICE_CHANGE_WRONG_WAY).putExtra(Constants.SERVICE_RESULT_WRONG_WAY_OUTPUT, "false");
                    LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(intentWrongWay);

                }*/
                Log.i(STATUS, "SPEED: " + speedKmHr);
                //Logica para obtener la velocidad anterior y actual
                if (hashMapSpeedFromTo.isEmpty() || hashMapSpeedFromTo.size() == 0) {
                    speedFrom = speedKmHr;
                    speedTo = speedKmHr;
                    hashMapSpeedFromTo.put("speedFrom", speedFrom);
                    hashMapSpeedFromTo.put("speedTo", speedTo);
                    Log.i("STATUS", "SPEED INICIO VACIO: speedFrom: " + hashMapSpeedFromTo.get("speedFrom") + " speedTo: " + hashMapSpeedFromTo.get("speedTo"));
                } else {
                    speedFrom = hashMapSpeedFromTo.get("speedTo");
                    speedTo = speedKmHr;
                    hashMapSpeedFromTo.put("speedFrom", speedFrom);
                    hashMapSpeedFromTo.put("speedTo", speedTo);
                    Log.i("STATUS", "SPEED NO VACIO: speedFrom: " + hashMapSpeedFromTo.get("speedFrom") + " speedTo: " + hashMapSpeedFromTo.get("speedTo"));
                }

                // DETECTAR EVENTOS LOCATION-----

                //Variables para verificar el tiempo de las ultimas mediciones
                double secondsPast=0.0;
                double timeStampNewReadingGPS=location.getTime();
                secondsPast=(timeStampNewReadingGPS-timeStampLastReadingGPS)/1000;
                timeStampLastReadingGPS=location.getTime();

                //Detectar excesos de velocidad
                int controlSpeed=0;

                if (speedKmHr > speedMax) {
                    controlSpeed=1;
                    timeStampLastMinSpeedReading=-1.0;
                } else if (speedKmHr < speedMin) {
                    if(timeStampLastMinSpeedReading>0){
                       if(timeStampLastMinSpeedReading-location.getTime()>timeMinInferiorSpeed){
                           controlSpeed=2;
                       }
                    }else {
                        timeStampLastMinSpeedReading=location.getTime();
                        controlSpeed = 0;
                    }
                } else {
                    controlSpeed=0;
                    timeStampLastMinSpeedReading=-1.0;
                    if(isUnauthorizedSpeed){
                        isUnauthorizedSpeed=false;
                        sentAlert(3,"Alerta de velocidad no autorizada finalizada");
                    }
                }

                if(!isUnauthorizedSpeed && controlSpeed>0){
                    switch (controlSpeed){
                        case 1:
                            sentAlert(1,"Exceso de velocidad detectado");
                            isUnauthorizedSpeed=true;
                            break;
                        case 2:
                            sentAlert(1,"Velocidad por debajo del minimo establecido");
                            isUnauthorizedSpeed=true;
                            break;
                    }
                }else{
                    if(secondsPast>timeUpdateAlert){
                        switch (controlSpeed){
                            case 1:
                                sentAlert(2,"Exceso de velocidad detectado");
                                isUnauthorizedSpeed=true;
                                break;
                            case 2:
                                sentAlert(2,"Velocidad por debajo del minimo establecido");
                                isUnauthorizedSpeed=true;
                                break;
                        }
                    }
                }




                //PARADA REPENTINAS-----
                if (!hashMapSpeedFromTo.isEmpty()) {
                    if (hashMapSpeedFromTo.get("speedFrom") != 0 && hashMapSpeedFromTo.get("speedTo") == 0) {
                        //Calculo de la distancia
                        // double distance = 0;
                    } else {

                    }
                }
                latLngFrom = new LatLng(hashMapLatLngFromTo.get("latitudeFrom"), hashMapLatLngFromTo.get("longitudeFrom"));
                latLngTo = new LatLng(hashMapLatLngFromTo.get("latitudeTo"), hashMapLatLngFromTo.get("longitudeTo"));
                //latLngTo = new LatLng(18.876807, -99.219968);
                distance = SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);
                Log.i(STATUS, "DISTANCE 1: " + distance + "m");

                location.distanceBetween(latitudeLast, longitudeLast, latitudeGPS, longitudeGPS, distanceArray);
                Log.i(STATUS, "DISTANCE 2: " + distanceArray[0] + "km");
                //location.distanceBetween();
                // FIN DETECTAR EVENTOS LOCATION-----



                /*UserLocation userLocation = updateUserLocation(HomeActivity.ID, latitudeGPS, longitudeGPS);
                try {
                    uLocationService.updateUserLocation(userLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                //Log.i(STATUS, "GPS latitude: "+latitudeGPS+" longitude: "+longitudeGPS);

            }
        } else {
            Log.i(STATUS, "Error GPS...!");
            //Toast.makeText(getBaseContext(), "Error GPS...!", Toast.LENGTH_LONG).show();
        }

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
            //Toast.makeText(this, "Entity Device created successfully", Toast.LENGTH_SHORT).show();
            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "Entity Device created successfully...!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            sqLiteController.updateStatusActiveByKeywordTempCreate(device.getId());
            Log.i("CONTEXT 201: ", "Entity Device created successfully...!");
            Log.i("ID device: ", device.getId());
        }else if(response.getHttpCode() == 422){
            //Toast.makeText(this, "The Device already exists....!", Toast.LENGTH_SHORT).show();
            Intent localIntent = new Intent(Constants.SERVICE_RUNNING_DEVICE).putExtra(Constants.SERVICE_RESULT_DEVICE, "The Device already exists....!");
            LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
            sqLiteController.updateStatusActiveByKeywordTempCreate(device.getId());
            Log.i("CONTEXT 422: ", "The Device already exists....!");
            Log.i("ID device: ", device.getId());
        }else{
            //Toast.makeText(this, "Error sending data...!", Toast.LENGTH_SHORT).show();
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
}
