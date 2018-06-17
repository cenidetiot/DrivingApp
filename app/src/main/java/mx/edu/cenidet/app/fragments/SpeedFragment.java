package mx.edu.cenidet.app.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.event.EventsDetect;
import mx.edu.cenidet.app.services.SendDataService;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;

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

    private Alert suddenStopAlert = null;
    private String StopingStatus = "";
    private EventsDetect events ;

    public SpeedFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);

        //sendDataService = HomeFragment.SENDDATASERVICE;
        df = new DecimalFormat("0.00");
        events = new EventsDetect();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_speed, container, false);
        //tvSpeed = rootView.findViewById(R.id.tvSpeed);
        tvSpeed = (TextView) rootView.findViewById(R.id.tvSpeed);
        tvAcceleration = (TextView) rootView.findViewById(R.id.tvAcceleration);
        //tvLocation = (TextView) rootView.findViewById(R.id.tvLocation);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //tvSpeed.setText(getArguments().getString("speed"));
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        tvSpeed.setText(df.format(speedMS)+"m/s, "+df.format(speedKmHr)+"km/hr");

        if (events.suddenStop(speedMS, new Date().getTime(), latitude,  longitude)){
            rootView.setBackgroundColor(Color.parseColor("#e74c3c"));
        }else {
            rootView.setBackgroundColor(Color.parseColor("#2980b9"));
        }



    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {

    }

    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {
        if (roadSegment != null){
            tvAcceleration.setText(roadSegment.getName());
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
