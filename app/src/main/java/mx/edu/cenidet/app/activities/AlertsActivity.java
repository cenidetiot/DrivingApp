package mx.edu.cenidet.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.fragments.AlertsFragment;
import mx.edu.cenidet.app.fragments.ZoneFragment;
import mx.edu.cenidet.app.services.SendDataService;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;

public class AlertsActivity extends AppCompatActivity implements View.OnClickListener,

        SendDataService.SendDataMethods,
        AlertController.AlertResourceMethods {
    private AlertController alertController;
    private SendDataService sendDataService;

    private double latitude, longitude;
    public static Context MAIN_CONTEXT = null;
    private FloatingActionButton btnFloatingUnknown;
    private FloatingActionButton btnFloatingAccident;
    private FloatingActionButton btnFloatingTraffic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);
        MAIN_CONTEXT = this;
        sendDataService = new SendDataService(this);
        alertController = new AlertController(this);
        setToolbar();
        btnFloatingGUI();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new AlertsFragment());
        ft.commit();
    }

    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_alerts);
    }

    public void btnFloatingGUI(){
        btnFloatingUnknown = (FloatingActionButton)findViewById(R.id.btnFloatingUnknown);
        btnFloatingUnknown.setOnClickListener(this);
        btnFloatingAccident = (FloatingActionButton)findViewById(R.id.btnFloatingAccident);
        btnFloatingAccident.setOnClickListener(this);
        btnFloatingTraffic = (FloatingActionButton)findViewById(R.id.btnFloatingTraffic);
        btnFloatingTraffic.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onCreateEntityAlert(Response response) {
        if(response.getHttpCode() == 201 || response.getHttpCode() == 200){
            Toast.makeText(MAIN_CONTEXT, R.string.message_successful_sending, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MAIN_CONTEXT, R.string.message_failed_send, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateEntityAlert(Response response) {

    }

    @Override
    public void onGetEntitiesAlert(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFloatingUnknown:
                confirmAlert();
                break;
            case R.id.btnFloatingAccident:
                Intent intentAccident = new Intent(MAIN_CONTEXT, SendManualAlertsActivity.class);
                intentAccident.putExtra("typeAlert", 1);
                startActivity(intentAccident);
                break;
            case R.id.btnFloatingTraffic:
                Intent intentTraffic = new Intent(MAIN_CONTEXT, SendManualAlertsActivity.class);
                intentTraffic.putExtra("typeAlert", 2);
                startActivity(intentTraffic);
                break;
        }
    }

    private void confirmAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.message_confirm_alert_title)
                .setMessage(R.string.message_confirm_alert_subtitle)
                .setIcon(R.drawable.ic_alert_critical)
                .setNegativeButton(R.string.message_is_driving_user_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //acciones del boton No
                            }
                        })
                .setPositiveButton(R.string.message_is_driving_user_yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                if(latitude != 0 && longitude != 0) {
                                    Alert alert = new Alert();
                                    alert.setId(new DevicePropertiesFunctions().getAlertId(MAIN_CONTEXT));
                                    alert.getAlertSource().setValue(new DevicePropertiesFunctions().getDeviceId(MAIN_CONTEXT));
                                    alert.getCategory().setValue("unknownAlert");
                                    alert.getDateObserved().setValue(Functions.getActualDate());
                                    alert.getDescription().setValue("Unknown Alert");
                                    alert.getLocation().setValue(latitude + ", " + longitude);
                                    alert.getSeverity().setValue("critical");
                                    alert.getSubCategory().setValue("unknown");
                                    alert.getValidFrom().setValue(Functions.getActualDate());
                                    alert.getValidTo().setValue(Functions.getActualDate());
                                    try {
                                        alertController.createEntity(MAIN_CONTEXT, alert.getId(), alert);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}
