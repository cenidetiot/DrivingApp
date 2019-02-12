package mx.edu.cenidet.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.event.EventsDetect;
import mx.edu.cenidet.app.services.SendDataService;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.Constants;
import mx.edu.cenidet.app.utils.MyBounceInterpolator;

import static mx.edu.cenidet.app.activities.MainActivity.getColorWithAlpha;

public class DrivingView extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback, SendDataService.SendDataMethods {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private IntentFilter filter;
    private ResponseReceiver receiver;

    private View rootView;
    private Context context;
    private TextView textSpeed;
    private TextView textWrongEvent; //, textSpeedEvent;
    //private TextView textEvent;
    private TextView textPruebas;
    private TextView textAcelerometer;
    private SupportMapFragment mfDrivingView;

    private FloatingActionButton floatingSpeeding;
    private FloatingActionButton floatingSudden;
    private FloatingActionButton floatingWrong;

    private double latitude, longitude, lastLatitude, lastLongitude, lastLastLatitude, lastLastLongitude;
    private double speedMS, speedKmHr;

    private static final String STATUS = "Status";
    private EventsDetect events;
    private RoadSegment roadSegment  = null;
    //private PulsatorLayout pulsator1;
    private SendDataService sendDataService;
    private DecimalFormat df;
    private ApplicationPreferences applicationPreferences;
    private boolean well = false;
    private long lastUpdateAcc = 0, lastUpdateGPS = 0;
    private float last_x, last_y, last_z, speed;
    private static final int SHAKE_THRESHOLD = 600;
    private GoogleMap googleMapDrivingView;
    private CameraPosition cameraPositionDrivingView;
    private Marker markerDrivingView;
    private Circle circleDrivingView;
    private long currentTimeMillis, previousTimeMillis;
    private float bearing, previousBearing;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private Zone currentZone = null;
    private ArrayList<LatLng> listLocation, listLocationParking;
    private ArrayList<OffStreetParking> listOffStreetParking;
    private JSONArray arrayLocationParking;
    private LatLng centerLatLngParking = null;
    private String currentTitle;

    /**
     * Used to initialize the UI
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_view);
        setToolbar();
        rootView = findViewById(R.id.content_driving).getRootView();
        rootView.setBackgroundColor(Color.parseColor("#2980b9"));
        textSpeed = (TextView) findViewById(R.id.textSpeed);
        //textSpeedEvent = (TextView) findViewById(R.id.textSpeedEvent);
        textWrongEvent = (TextView) findViewById(R.id.textWrongWayEvent);
        //textEvent = (TextView) findViewById(R.id.textEvent);
        textAcelerometer = (TextView) findViewById(R.id.textAcelerometer);

        mfDrivingView = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mfDrivingView);

        floatingSpeeding = (FloatingActionButton) findViewById(R.id.floatingActionSpeeding);
        floatingSudden = (FloatingActionButton) findViewById(R.id.floatingActionSudden);
        floatingWrong = (FloatingActionButton) findViewById(R.id.floatingActionWrong);
        floatingSpeeding.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
        floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
        floatingWrong.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));

        //pulsator1 = (PulsatorLayout) findViewById(R.id.pulsator1);
        //pulsator1.start();

        applicationPreferences = new ApplicationPreferences();


        events = new EventsDetect();
        df = new DecimalFormat("0.00");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 000000);

        if (mfDrivingView != null) {
            mfDrivingView.getMapAsync(this);
        }

        sendDataService = new SendDataService(this);
        sqLiteDrivingApp = new SQLiteDrivingApp(getApplicationContext());

    }

    /**
     * Initialize the map
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapDrivingView = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMapDrivingView.setMyLocationEnabled(false);
        googleMapDrivingView.getUiSettings().setMyLocationButtonEnabled(false);
        //googleMapDrivingView.getUiSettings().setScrollGesturesEnabled(false);

    }

    /**
     * Draw the user marker on the map
     * @param latitude
     * @param longitude
     * @param title
     * @param bearing
     */
    private void createOrUpdateMarkerByLocation(double latitude, double longitude, String title, float bearing){

        if(markerDrivingView == null){
            //circleDrivingView = googleMapDrivingView.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(10).strokeColor(Color.argb(255, 20, 160, 255)).fillColor(Color.argb(80,20,160, 255)));
            //circleDrivingView.setStrokeWidth(1);
            markerDrivingView = googleMapDrivingView.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title));

            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.car_icon);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Bitmap bitmapSmall = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
            markerDrivingView.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapSmall));
            markerDrivingView.setFlat(true);
            markerDrivingView.setAnchor(0.5f, 0.5f);
            zoomToLocation(latitude, longitude, bearing);
        }else{
            //circleDrivingView.setCenter(new LatLng(latitude, longitude));
            markerDrivingView.setPosition(new LatLng(latitude, longitude));
            markerDrivingView.setRotation(bearing);
            CameraPosition cameraPosition = googleMapDrivingView.getCameraPosition();

            CameraPosition cameraPositionTmp = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom((cameraPosition.zoom < 8.0f) ? 17.0f : cameraPosition.zoom)
                    .bearing(bearing)    //orientación de la camara hacia el este 0°-365°
                    .tilt(cameraPosition.tilt)       //efecto 3D 0-90
                    .build();
            //googleMapDrivingView.animateCamera();
            //zoomToLocation(latitude, longitude, bearing);
            //googleMapDrivingView.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            cameraPositionDrivingView = cameraPositionTmp;
            googleMapDrivingView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionTmp));


        }
    }

    /**
     * Make the animated zoom effect in the location
     * @param latitude
     * @param longitude
     * @param bearing
     */
    private void zoomToLocation(double latitude, double longitude, float bearing){

        cameraPositionDrivingView = new CameraPosition.Builder()
            .target(new LatLng(latitude, longitude))
            .zoom(17.0f)       //limit -> 21
            .bearing(bearing)    //orientación de la camara hacia el este 0°-365°
            .tilt(50.0f)       //efecto 3D 0-90
            .build();

        googleMapDrivingView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionDrivingView));
    }

    /**
     * Used to receive the location from the DeviceService and use the method sendLocationSpeed
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.SERVICE_CHANGE_LOCATION_DEVICE:
                    latitude = intent.getDoubleExtra(Constants.SERVICE_RESULT_LATITUDE, 0);
                    longitude = intent.getDoubleExtra(Constants.SERVICE_RESULT_LONGITUDE, 0);
                    speedMS = intent.getDoubleExtra(Constants.SERVICE_RESULT_SPEED_MS, 0);
                    speedKmHr = intent.getDoubleExtra(Constants.SERVICE_RESULT_SPEED_KMHR, 0);
                    roadSegment = (RoadSegment) intent.getExtras().get(Constants.ROAD_SEGMENT);

                    sendLocationSpeed(latitude,longitude,speedMS,speedKmHr);
                    break;
            }
        }
    }

    /**
     * Set the toolbar into the view
     */
    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED));
        getSupportActionBar().setTitle(R.string.menu_speed);
    }

    /**
     * Add the onBackPressed to the back arrow and add the finish to stop the broadcast receiver in the class
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }


    /**
     * Used to registry the Broadcast receiver
     */
    @Override
    public void onStart() {
        super.onStart();
        filter = new IntentFilter(Constants.SERVICE_CHANGE_LOCATION_DEVICE);
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        applicationPreferences.saveOnPreferenceBoolean(
                getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING,
                true);
    }

    /**
     * When the activity stops unregister the broadcast receiver
     */
    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        applicationPreferences.saveOnPreferenceBoolean(
                getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING,
                false);
    }

    /**
     * Add the animation to the events buttons
     * @param button
     */
    public void anim(FloatingActionButton button) {
        Animation myAnim = AnimationUtils.loadAnimation(this, R.transition.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);
    }

    /**
     * Check if the user is speeding
     * @param speedMS
     * @param longitude
     * @param latitude
     */
    public void speeding(double speedMS, double longitude, double latitude) {
        String speedText = "";

        /**
         * Get a JSON with the detection data
         */
        JSONObject speedDetection = events.speeding(
                roadSegment.getMinimumAllowedSpeed(),
                roadSegment.getMaximumAllowedSpeed(),
                speedMS, latitude, longitude);

        textWrongEvent.setText(
                "Minimum Speed allowed: " + roadSegment.getMinimumAllowedSpeed() +" km/hr \n" +
                        "Maximun Speed allowed: " + roadSegment.getMaximumAllowedSpeed() +" km/hr"
        );

        try {
            //its ok  =  green
            boolean isSpeeding = speedDetection.getBoolean("isSpeeding");
            boolean under = speedDetection.getBoolean("under"); // orange
            boolean over = speedDetection.getBoolean("over");  // red

            if (isSpeeding) {
                speedText = "Unauthorized Speed Detection ";
                anim(floatingSpeeding);
            } else {
                speedText = "speed allowed ";
                floatingSpeeding.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
            }

            if (under) {
                speedText += "under";
                floatingSpeeding.setBackgroundTintList(getResources().getColorStateList(R.color.driving_orange));
            }
            if (over) {
                speedText += "over";
                floatingSpeeding.setBackgroundTintList(getResources().getColorStateList(R.color.driving_red));
            }

            //textSpeedEvent.setText(speedText);
        }catch (Exception w){

        }

    }

    /**
     * Check if the user have a sudden stop
     * @param speedMS
     * @param latitude
     * @param longitude
     */
    public void sudden (double speedMS , double latitude, double longitude) {

        /**
         * Get the stop data in a JSON
         */
        JSONObject suddenStop = events.suddenStop(speedMS, new Date().getTime(), latitude, longitude);

        try {
            boolean stopped = suddenStop.getBoolean("isStopped");  //red
            boolean stopping =  suddenStop.getBoolean("isStopping"); //orange
            boolean sudden =  suddenStop.getBoolean("isSuddenStop"); //red
            boolean acelerating =  suddenStop.getBoolean("isAcelerating");//blue


            if (!stopped && !stopping & !sudden){
                if (acelerating) {
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_blue));
                }else {
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
                }
                textSpeed.setTextColor(Color.parseColor("#2980b9"));

            }else {
                if (stopping){
                    textSpeed.setTextColor(Color.parseColor("#d35400"));
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_orange));
                }
                if (stopped){
                    rootView.setBackgroundColor(Color.parseColor("#2c3e50"));
                    textSpeed.setTextColor(Color.parseColor("#2c3e50"));
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_red));
                    anim(floatingSudden);
                }
                if (sudden){
                    textSpeed.setTextColor(Color.parseColor("#c0392b"));
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_red));
                }

            }
        }catch (Exception e){}
    }

    /**
     * Check if the user is in opposite direction
     * @param currentLatLng
     * @param startLatLng
     * @param endLatLng
     */
    public void wrongWay (LatLng currentLatLng, LatLng startLatLng, LatLng endLatLng ){

        /**
         * Get the wronway data in a JSON
         */
        JSONObject wrong = events.wrongWay(
                currentLatLng,
                startLatLng,
                endLatLng,
                new Date().getTime()
        );

        try {
            // its ok = green
            boolean isWrong =  wrong.getBoolean("isWrongWay"); // red

            if (isWrong){
                floatingWrong.setBackgroundTintList(getResources().getColorStateList(R.color.driving_red));
                anim(floatingWrong);
            }else{
                floatingWrong.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
            }
        }catch (Exception e ){ }
    }

    /**
     * Used when the location change
     * @param latitude
     * @param longitude
     * @param speedMS
     * @param speedKmHr
     */
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        String sTextSpeed = df.format(speedKmHr) + " km/hr";
        textSpeed.setText(sTextSpeed);
        /**
         * Used to draw the car maker when the user change location
         */
        if (latitude!=0 && longitude!=0){
            if(lastLatitude==0 && lastLongitude==0){
                lastLatitude = latitude;
                lastLongitude = longitude;
                bearing = 0;
                createOrUpdateMarkerByLocation(latitude, longitude, sTextSpeed, bearing);
            }else{
                if (lastLatitude!=latitude && lastLongitude!=longitude) { //SE COMPARA CON LA UBICACION ANTERIOR
                    if (lastLastLatitude==0  && lastLastLongitude==0){
                        lastLastLatitude = lastLatitude;
                        lastLastLongitude = lastLongitude;
                        createOrUpdateMarkerByLocation(latitude, longitude, sTextSpeed, bearing);
                    }else { //SE COMPARA CON LA UBICACION ANTERIOR A LA ANTERIOR
                        if (lastLastLatitude != latitude && lastLastLongitude != longitude) {
                            currentTimeMillis = System.currentTimeMillis();
                            if (previousTimeMillis==0){
                                previousTimeMillis = currentTimeMillis;
                            }else{
                                if ((currentTimeMillis-previousTimeMillis)/1000>10){
                                    Location firstLocation = new Location("First location");
                                    firstLocation.setLatitude(lastLatitude);
                                    firstLocation.setLongitude(lastLongitude);
                                    Location secondLocation = new Location("Second location");
                                    secondLocation.setLatitude(latitude);
                                    secondLocation.setLongitude(longitude);
                                    bearing = firstLocation.bearingTo(secondLocation);
                                    if (bearing>0.0f && bearing<=180.0f){
                                        bearing = bearing;
                                    }else{
                                        bearing = 180 + (bearing + 180);
                                    }
                                    previousTimeMillis = currentTimeMillis;
                                }
                            }
                            lastLastLatitude = lastLatitude;
                            lastLastLongitude = lastLongitude;
                            lastLatitude = latitude;
                            lastLongitude = longitude;
                            createOrUpdateMarkerByLocation(latitude, longitude, sTextSpeed, bearing);
                        }
                    }

                }
            }
        }

        /**
         * Used to detects if the user is speeding, have a sudden stop or is wrongway(opppsite direction)
         */
        if (applicationPreferences.getPreferenceBoolean(getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING)){

            sudden(speedMS, latitude, longitude);

            try {
                if (roadSegment != null){

                    speeding(speedKmHr, longitude, latitude);
                    String originalString, clearString;
                    String[] subString;
                    List<LatLng> polyline = new ArrayList<>();
                    LatLng myPoint = new LatLng(latitude, longitude);
                    Log.d("SEGMENTO", roadSegment.getLocation());
                    JSONArray arrayLocation = new JSONArray(roadSegment.getLocation());
                    for (int j=0; j<arrayLocation.length(); j++){
                        originalString = arrayLocation.get(j).toString();
                        clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                        subString =  clearString.split(",");
                        latitude = Double.parseDouble(subString[0]);
                        longitude = Double.parseDouble(subString[1]);
                        polyline.add(new LatLng(latitude, longitude));
                    }

                    Double distances = 0.0;
                    List<LatLng> nearLine = new ArrayList<>();
                    int nearIndex = -1;

                    for (int j=0; j < polyline.size() - 1; j++){

                        List<LatLng> tempPolyline = new ArrayList<>();
                        tempPolyline.add(polyline.get(j));
                        tempPolyline.add(polyline.get(j + 1));

                        if(PolyUtil.isLocationOnPath(myPoint, tempPolyline, false, roadSegment.getWidth())) {
                            LatLng tempStart = tempPolyline.get(0);
                            LatLng tempEnd = tempPolyline.get(1);

                            Double tempdistance = SphericalUtil.computeDistanceBetween(tempStart, myPoint);
                            tempdistance += SphericalUtil.computeDistanceBetween(tempEnd, myPoint);
                            if (distances > tempdistance  || nearIndex == -1) {
                                distances = tempdistance;
                                nearLine = tempPolyline;
                                nearIndex = j;
                            }
                            Log.d("SEGMENTO", "D1: "+ distances + " D2: " + tempdistance);

                        }

                    }

                    wrongWay(myPoint,nearLine.get(0),nearLine.get(1));
                }else  {
                    Log.d("SEGMENTO" , "Fuera de segmento");
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * Detects the zone where the user is
     * @param zone
     * @param statusLocation
     */
    @Override
    public void detectZone(Zone zone, boolean statusLocation) {
        if (statusLocation){
            if (currentZone != null){

                if (!currentZone.getIdZone().equals(zone.getIdZone())){
                    drawZone(zone);
                    drawParking(zone.getIdZone());
                    changeActionBarTitle(zone.getName().getValue() + " - " + zone.getAddress().getValue());
                    //drawRoadSegmentByParking(zone.getIdZone());
                }

            }else {
                drawZone(zone);
                drawParking(zone.getIdZone());
                changeActionBarTitle(zone.getName().getValue() + " - " + zone.getAddress().getValue());
                //drawRoadSegmentByParking(zone.getIdZone());

            }
            currentZone = zone;
        }else{
            changeActionBarTitle(getString(R.string.menu_speed));
        }

    }

    /**
     * Change the action bar title
     * @param title
     */
    private void changeActionBarTitle(String title){
        if (currentTitle==null || currentTitle.isEmpty() || !title.equalsIgnoreCase(currentTitle)) {
            currentTitle = title;
            try {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Detects if the user is on a road Segment
     * @param latitude
     * @param longitude
     * @param roadSegment
     */
    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {
        if (roadSegment != null){
            Log.d("ROADSTEST", roadSegment.getName());
        }else {
            Log.d("ROADSTEST", "Sin roadSegmetn");
        }
    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {

    }

    @Override
    public void sendEvent(String event) {

    }

    /**
     * Receives the sensor changes
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float  z = event.values[2];
            long curTime = System.currentTimeMillis();
            if((curTime-lastUpdateAcc)>= 1000){
                lastUpdateAcc = curTime;
                events.saveAxis(x,y,z);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Draw the zone where the user is.
     * @param zone el identificador de la zona.
     */
    public void drawZone(Zone zone){
        JSONArray arrayLocation;
        String originalString, clearString;
        double latitude, longitude;
        String[] subString;
        listLocation = new ArrayList<>();
        try {
            arrayLocation = new JSONArray(zone.getLocation().getValue());
            for (int j=0; j<arrayLocation.length(); j++){
                originalString = arrayLocation.get(j).toString();
                clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                subString =  clearString.split(",");
                latitude = Double.parseDouble(subString[0]);
                longitude = Double.parseDouble(subString[1]);
                listLocation.add(new LatLng(latitude,longitude));
            }
            googleMapDrivingView.addPolygon(new PolygonOptions()
                    .fillColor(getColorWithAlpha(Color.parseColor("#2ecc71"), 0.1f))
                    .addAll(listLocation).strokeColor(Color.parseColor("#2ecc71")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Draw the parkings where the user is
     * @param zoneId
     */
    public void drawParking(String zoneId){
        listOffStreetParking = sqLiteDrivingApp.getAllOffStreetParkingByAreaServed(zoneId);
        if (listOffStreetParking.size() > 0) {
            String originalStringParking, clearStringParking;
            double latitudeParking, longitudeParking;
            String[] subStringParking;
            LatLngBounds.Builder builder;
            for (int i = 0; i < listOffStreetParking.size(); i++) {
                builder = new LatLngBounds.Builder();
                listLocationParking = new ArrayList<>();
                try {
                    arrayLocationParking = new JSONArray(listOffStreetParking.get(i).getLocation());
                    for (int j = 0; j < arrayLocationParking.length(); j++) {
                        originalStringParking = arrayLocationParking.get(j).toString();
                        clearStringParking = originalStringParking.substring(originalStringParking.indexOf("[") + 1, originalStringParking.indexOf("]"));
                        subStringParking = clearStringParking.split(",");
                        latitudeParking = Double.parseDouble(subStringParking[0]);
                        longitudeParking = Double.parseDouble(subStringParking[1]);
                        //listLocationParking.add(new LatLng(latitudeParking, longitudeParking));
                        LatLng tmp = new LatLng(latitudeParking, longitudeParking);
                        listLocationParking.add(tmp);
                        builder.include(tmp); //Le agregas los puntos del poligono
                    }
                    LatLngBounds bounds = builder.build(); //Obtienes los limites del poligono
                    centerLatLngParking = bounds.getCenter(); //Obtienes el centro de los limites del poligono
                    if (googleMapDrivingView != null) {
                        googleMapDrivingView.addPolygon(
                                new PolygonOptions().
                                        addAll(listLocationParking)
                                        .fillColor(getColorWithAlpha(Color.parseColor("#3498db"), 0.1f))
                                        .strokeColor(Color.parseColor("#3498db")))                             ;

                    }
                    //createMarkerParking(centerLatLngParking.latitude, centerLatLngParking.longitude, listOffStreetParking.get(i).getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                drawRoadSegmentByParking(listOffStreetParking.get(i).getIdOffStreetParking());
            }
        }

    }

    /**
     * Draw the Roads Segments in the parking
     * @param responsible
     */
    public void drawRoadSegmentByParking(String responsible){
        ArrayList<Road> listRoadByResponsible = sqLiteDrivingApp.getRoadByResponsible(responsible); //obtiene la lista de los road por el responsable.
        if( listRoadByResponsible.size() > 0) {
            for(int i=0; i<listRoadByResponsible.size(); i++) {
                ArrayList<RoadSegment> getAllRoadSegmentByRefRoad = sqLiteDrivingApp.getAllRoadSegmentByRefRoad(listRoadByResponsible.get(i).getIdRoad());
                if (getAllRoadSegmentByRefRoad.size() > 0) {
                    String originalStringParking, clearStringParking;
                    double latitude, longitude;
                    String[] subStringParking;
                    JSONArray arrayLocationRoadSegment;
                    for (RoadSegment iteratorRoadSegment : getAllRoadSegmentByRefRoad) {
                        ArrayList<LatLng> listLocationRoadSegment = new ArrayList<>();
                        try {
                            arrayLocationRoadSegment = new JSONArray(iteratorRoadSegment.getLocation());
                            for (int j = 0; j < arrayLocationRoadSegment.length(); j++) {
                                originalStringParking = arrayLocationRoadSegment.get(j).toString();
                                clearStringParking = originalStringParking.substring(originalStringParking.indexOf("[") + 1, originalStringParking.indexOf("]"));
                                subStringParking = clearStringParking.split(",");
                                latitude = Double.parseDouble(subStringParking[0]);
                                longitude = Double.parseDouble(subStringParking[1]);
                                listLocationRoadSegment.add(new LatLng(latitude, longitude));
                            }
                            if (googleMapDrivingView != null) {
                                googleMapDrivingView.addPolyline(new PolylineOptions()
                                        .addAll(listLocationRoadSegment)
                                        .width(8)
                                        .color(Color.RED));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
