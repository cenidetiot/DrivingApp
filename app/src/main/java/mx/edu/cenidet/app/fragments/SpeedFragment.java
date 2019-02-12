package mx.edu.cenidet.app.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.event.EventsDetect;
import mx.edu.cenidet.app.services.SendDataService;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpeedFragment extends Fragment implements SendDataService.SendDataMethods {
    private View rootView;
    private Context context;
    private TextView tvSpeed;
    private TextView tvAcceleration;
    private TextView tvLocation;
    private ProgressBar pbSpeed;
    //private TextView tvSpeed;
    private static final String STATUS = "Status";
    private DecimalFormat df;
    private SendDataService sendDataService;
    private ApplicationPreferences appPreferences;
    private Alert suddenStopAlert = null;
    private String StopingStatus = "";
    private EventsDetect events;
    private RoadSegment roadSegment  = null;

    /**
     * Constructor used to initialize the context, sendDataService, appPreferences and the events detection
     */
    public SpeedFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);
        appPreferences = new ApplicationPreferences();
        events = new EventsDetect();
        df = new DecimalFormat("0.00");
    }


    /**
     * Used to initialize the UI
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_speed, container, false);
        tvSpeed = (TextView) rootView.findViewById(R.id.tvSpeed);
        tvAcceleration = (TextView) rootView.findViewById(R.id.tvAcceleration);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Used to show the user speed
     * @param latitude
     * @param longitude
     * @param speedMS
     * @param speedKmHr
     */
    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        tvSpeed.setText(df.format(speedMS)+"m/s, "+df.format(speedKmHr)+"km/hr");
        if (roadSegment != null){
            tvAcceleration.setText(roadSegment.getLocation().substring(1, roadSegment.getLocation().length() -1));
        }
    }

    /**
     * Show an Alert Dialog to ask if the user is driving DEPRECATED
     */
    private void isDrivingUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.message_is_driving_user_title)
                .setIcon(R.drawable.ic_car)
                .setNegativeButton(R.string.message_is_driving_user_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //driving = false;
                            }
                        })
                .setPositiveButton(R.string.message_is_driving_user_yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                //driving = true;
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {

    }

    /**
     * Used to store the roadSegment when change
     * @param latitude
     * @param longitude
     * @param roadSegment
     */
    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {
        if (roadSegment != null){
            this.roadSegment =  roadSegment;
        }
    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
        tvAcceleration.setText("ax: "+ax+"\nay: "+ay+"\naz: "+az);
    }

    @Override
    public void sendEvent(String event) {

    }

}
