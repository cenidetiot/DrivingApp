package mx.edu.cenidet.app.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private SendDataService sendDataService;
    private DecimalFormat df;
    private ApplicationPreferences appPreferences;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.suddenStopButton);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.wrongWayButton);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.speedButton);
        sendDataService = new SendDataService(this);
        appPreferences = new ApplicationPreferences();
        events = new EventsDetect();
        df = new DecimalFormat("0.00");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);




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
            JSONObject suddenStop = events.suddenStop(speedMS, new Date().getTime(), latitude,  longitude);

            try {
                boolean well = true;

                if(suddenStop.getBoolean("isStopping")){
                    textEvent.setText("You are stopping");
                    well = false;
                }

                if (suddenStop.getBoolean("isStopeed")) {
                    textEvent.setText("You are stopped");
                    well = false;
                }

                if(suddenStop.getBoolean("isSuddenStop")){
                    textPruebas.setText(suddenStop.getString("result"));
                    well = false;
                }

                if (well){
                    textEvent.setText("You are driving well");
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
        textAcelerometer.setText("Sensando...!");


        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            last_x = event.values[0];
            last_y = event.values[1];
            last_z = event.values[2];
            textAcelerometer.setText(last_x + " : " + last_y + " : " + last_z);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
