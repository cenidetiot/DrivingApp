package mx.edu.cenidet.drivingapp.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.services.SendDataService;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.Constants;

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

    private SendDataService sendDataService;

    public SpeedFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);
        //sendDataService = HomeFragment.SENDDATASERVICE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_speed, container, false);
        //tvSpeed = rootView.findViewById(R.id.tvSpeed);
        tvSpeed = (TextView) rootView.findViewById(R.id.tvSpeed);
        tvAcceleration = (TextView) rootView.findViewById(R.id.tvAcceleration);
        tvLocation = (TextView) rootView.findViewById(R.id.tvLocation);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //tvSpeed.setText(getArguments().getString("speed"));
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        tvLocation.setText("Lat. "+latitude+", Lon. "+longitude);
        tvSpeed.setText(speedMS+"m/s, "+speedKmHr+"km/hr");

        //tvSpeed.setText("Latitude: " + latitude + " Longitude: " + longitude + " Velocidad: " + speedMS + "m/s  Velocidad: " + speedKmHr + "km/hr");
        //Log.i("SPEED: ", "VIEW Latitude: " + latitude + " Longitude: " + longitude + " Velocidad: " + speedMS + "m/s  Velocidad: " + speedKmHr + "km/hr");
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {

    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
        tvAcceleration.setText("ax: "+ax+"\nay: "+ay+"\naz: "+az);
        //Log.i("STATUS 2: ","ax: "+ax+" ay: "+ay+" az: "+az);
    }

    @Override
    public void sendEvent(String event) {
        if(tvAcceleration != null){
            tvAcceleration.setText(event);
        }
       /* if(event.equals("true")){
            Toast.makeText(getContext(), "Usted va en sentido contrario", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Usted va en dirección correcta", Toast.LENGTH_SHORT).show();
        }*/
    }

}
