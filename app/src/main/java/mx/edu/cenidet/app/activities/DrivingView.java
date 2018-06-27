package mx.edu.cenidet.app.activities;

import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class DrivingView extends AppCompatActivity implements SendDataService.SendDataMethods, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private View rootView;
    private Context context;
    private TextView textSpeed;
    private TextView textEvent;
    private TextView textPruebas;
    private TextView textAcelerometer;

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
        textEvent = (TextView) findViewById(R.id.textEvent);
        textPruebas = (TextView) findViewById(R.id.textPruebas);
        textAcelerometer = (TextView) findViewById(R.id.textAcelerometer);
        textAcelerometer.setText("Acelerometro");

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.suddenStopButton);
        //FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.wrongWayButton);
        //FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.speedButton);

        pulsator1 = (PulsatorLayout) findViewById(R.id.pulsator1);
        pulsator1.start();
        /*PulsatorLayout pulsator2 = (PulsatorLayout) findViewById(R.id.pulsator2);
        pulsator2.start();
        PulsatorLayout pulsator3 = (PulsatorLayout) findViewById(R.id.pulsator3);
        pulsator3.start();*/

        sendDataService = new SendDataService(this);
        appPreferences = new ApplicationPreferences();
        events = new EventsDetect();
        df = new DecimalFormat("0.00");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 000000);
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
        events = new EventsDetect();
        appPreferences.saveOnPreferenceBoolean(
                getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING,
                true);
    }

    @Override
    public void onStop() {
        super.onStop();
        appPreferences.saveOnPreferenceBoolean(
                getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING,
                false);
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        textSpeed.setText(df.format(speedKmHr) + " km/h");
        if (appPreferences.getPreferenceBoolean(getApplicationContext(),
                ConstantSdk.PREFERENCE_NAME_GENERAL,
                ConstantSdk.PREFERENCE_USER_IS_DRIVING)){

                JSONObject suddenStop = events.suddenStop(speedMS, new Date().getTime(), latitude, longitude);

                try {
                    well = true;

                    if (well){
                        textEvent.setText("You are driving well");
                        //azul
                        //pulsator1.setBackgroundColor(Color.parseColor("#55efc4"));

                        pulsator1.setColor(Color.parseColor("#55efc4"));
                        pulsator1.start();

                        //pulsator1.setInterpolator(PulsatorLayout.INTERP_ACCELERATE_DECELERATE);
                    }

                    if(suddenStop.getBoolean("isStopping")) {
                        textEvent.setText("You are stopping");
                        //amarillo
                        //pulsator1.setBackgroundColor(Color.parseColor("#f1c40f"));

                        pulsator1.setColor(Color.parseColor("#f1c40f"));
                        pulsator1.start();
                        well = false;
                    }
                    if (suddenStop.getBoolean("isStopped")) {
                        textEvent.setText("You are stopped");
                        //naranja
                        //pulsator1.setBackgroundColor(Color.parseColor("#e67e22"));

                        pulsator1.setColor(Color.parseColor("#e67e22"));
                        pulsator1.start();
                        well = false;
                    }

                    if(suddenStop.getBoolean("isSuddenStop")){
                        textPruebas.setText(suddenStop.getString("result"));
                        //rojo
                        //pulsator1.setBackgroundColor(Color.parseColor("#e74c3c"));

                        pulsator1.setColor(Color.parseColor("#e74c3c"));
                        pulsator1.start();
                        well = false;
                    }

                }catch (Exception e){}

        }
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {

    }

    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {

    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {

    }

    @Override
    public void sendEvent(String event) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;


        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float  z = event.values[2];
            long curTime = System.currentTimeMillis();
            //if((curTime-lastUpdateAcc)>= 1000){
                lastUpdateAcc = curTime;
                last_x = x;
                last_y = y;
                last_z = z;
                String going = "Detenido";
                if (last_y < 9.6 || last_y >= 9.9){
                    going = "Moviendose";
                }
                textAcelerometer.setText(last_x + " : " + last_y + " : " + last_z + "\n"+ going);
            //}

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
