package mx.edu.cenidet.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.event.EventsDetect;
import mx.edu.cenidet.app.services.SendDataService;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.Constants;
import mx.edu.cenidet.app.utils.MyBounceInterpolator;

public class DrivingView extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private IntentFilter filter;
    private ResponseReceiver receiver;

    private View rootView;
    private Context context;
    private TextView textSpeed;
    private TextView textSpeedEvent, textWrongEvent;
    private TextView textEvent;
    private TextView textPruebas;
    private TextView textAcelerometer;

    private FloatingActionButton floatingSpeeding;
    private FloatingActionButton floatingSudden;
    private FloatingActionButton floatingWrong;

    private double latitude, longitude;
    private double speedMS, speedKmHr;

    private static final String STATUS = "Status";
    private EventsDetect events;
    private RoadSegment roadSegment  = null;
    private PulsatorLayout pulsator1;
    private SendDataService sendDataService;
    private DecimalFormat df;
    private ApplicationPreferences appPreferences;
    private boolean well = false;
    private long lastUpdateAcc = 0, lastUpdateGPS = 0;
    private float last_x, last_y, last_z, speed;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_view);
        setToolbar();
        rootView = findViewById(R.id.content_driving).getRootView();
        rootView.setBackgroundColor(Color.parseColor("#2980b9"));
        textSpeed = (TextView) findViewById(R.id.textSpeed);
        textSpeedEvent = (TextView) findViewById(R.id.textSpeedEvent);
        textWrongEvent = (TextView) findViewById(R.id.textWrongWayEvent);
        textEvent = (TextView) findViewById(R.id.textEvent);
        textAcelerometer = (TextView) findViewById(R.id.textAcelerometer);

        floatingSpeeding = (FloatingActionButton) findViewById(R.id.floatingActionSpeeding);
        floatingSudden = (FloatingActionButton) findViewById(R.id.floatingActionSudden);
        floatingWrong = (FloatingActionButton) findViewById(R.id.floatingActionWrong);
        floatingSpeeding.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
        floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
        floatingWrong.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));



        pulsator1 = (PulsatorLayout) findViewById(R.id.pulsator1);
        pulsator1.start();

        appPreferences = new ApplicationPreferences();

        events = new EventsDetect();
        df = new DecimalFormat("0.00");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 000000);
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
                    roadSegment = (RoadSegment) intent.getExtras().get(Constants.ROAD_SEGMENT);
                    //Toast.makeText(getApplicationContext(), "Velocidad" + speedMS, Toast.LENGTH_SHORT).show();
                    Log.d("DRIVINGVIEW", " "+ speedMS);
                    sendLocationSpeed(latitude,longitude,speedMS,speedKmHr);
                    break;
            }
        }
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_speed);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        filter = new IntentFilter(Constants.SERVICE_CHANGE_LOCATION_DEVICE);
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        appPreferences.saveOnPreferenceBoolean(
                getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING,
                true);
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        appPreferences.saveOnPreferenceBoolean(
                getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING,
                false);
    }

    public void anim(FloatingActionButton button) {
        Animation myAnim = AnimationUtils.loadAnimation(this, R.transition.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);
    }

    public void speeding(double speedMS, double longitude, double latitude) {
        String speedText = "";

        JSONObject speedDetection = events.speeding(
                roadSegment.getMinimumAllowedSpeed(),
                roadSegment.getMaximumAllowedSpeed(),
                speedMS, latitude, longitude);

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

            textSpeedEvent.setText(speedText);
        }catch (Exception w){

        }

    }

    public void sudden (double speedMS , double latitude, double longitude) {
        JSONObject suddenStop = events.suddenStop(speedMS, new Date().getTime(), latitude, longitude);

        try {
            //its ok = green
            boolean stopped = suddenStop.getBoolean("isStopped");  //red
            boolean stopping =  suddenStop.getBoolean("isStopping"); //orange
            boolean sudden =  suddenStop.getBoolean("isSuddenStop"); //red
            boolean acelerating =  suddenStop.getBoolean("isAcelerating");//blue


            if (!stopped && !stopping & !sudden){
                if (acelerating) {
                    textEvent.setText("You are acelerating");
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_blue));
                }else {
                    textEvent.setText("You are OK");
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));
                }
                textSpeed.setTextColor(Color.parseColor("#2980b9"));

            }else {
                if (stopping){
                    textEvent.setText("You are stopping");
                    textSpeed.setTextColor(Color.parseColor("#d35400"));
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_orange));
                }
                if (stopped){
                    textEvent.setText("You are stopped");
                    rootView.setBackgroundColor(Color.parseColor("#2c3e50"));
                    textSpeed.setTextColor(Color.parseColor("#2c3e50"));
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_red));
                    anim(floatingSudden);
                }
                if (sudden){
                    //textPruebas.setText(suddenStop.getString("result"));
                    textSpeed.setTextColor(Color.parseColor("#c0392b"));
                    floatingSudden.setBackgroundTintList(getResources().getColorStateList(R.color.driving_red));
                }

            }
        }catch (Exception e){}
    }

    public void wrongWay (LatLng currentLatLng, LatLng startLatLng, LatLng endLatLng ){

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
                textWrongEvent.setText("Wrong Way Detection");
                floatingWrong.setBackgroundTintList(getResources().getColorStateList(R.color.driving_red));
                anim(floatingWrong);
            }else{
                textWrongEvent.setText("");
                floatingWrong.setBackgroundTintList(getResources().getColorStateList(R.color.driving_green));

            }
        }catch (Exception e ){ }
    }

    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {


        textSpeed.setText(df.format(speedKmHr) + " km/hr");

        if (appPreferences.getPreferenceBoolean(getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING)){

            sudden(speedMS, latitude, longitude);

            try {
               if (roadSegment != null){
                    speeding(speedKmHr, longitude, latitude);

                    String start = roadSegment.getStartPoint();
                    String [] startCoords = start.substring(1, start.length() - 1).split(",");
                    String end = roadSegment.getEndPoint();
                    String [] endCoords = end.substring(1, end.length() - 1).split(",");
                    LatLng startLatLng = new LatLng(
                            (double) Double.parseDouble(startCoords[0]),
                            (double) Double.parseDouble(startCoords[1]));
                    LatLng endLatLng = new LatLng(
                            (double) Double.parseDouble(endCoords[0]),
                            (double) Double.parseDouble(endCoords[1]));

                   wrongWay(new LatLng(latitude, longitude), startLatLng, endLatLng);
                }else  {
                    //textSpeedEvent.setText("");
                }

            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }



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
                last_x = x;
                last_y = y;
                last_z = z;
                String going = "Detenido";
                if (last_z < 9.6 || last_z >= 9.95){
                    going = "Moviendose";
                }
                //textAcelerometer.setText(last_x + " : " + last_y + " : " + last_z + "\n"+ going);
                events.saveAxis(last_x,last_y,last_z);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
