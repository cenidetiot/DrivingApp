package mx.edu.cenidet.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.DrivingView;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.services.SendDataService;

import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SendDataService.SendDataMethods {
    private View rootView;
    private double latitude, longitude;
    private double speedMS, speedKmHr;
    private IntentFilter filter;
    private static final String STATUS = "Status";
    private Context context;
    private SendDataService sendDataService;
    private TextView tvDetailCampus;
    private TextView tvRoadSegment;
    private ImageView imagenViewDetailCampus;
    private FloatingActionButton speedButtonHome;
    //private FloatingActionButton btnFloating;
    private AlertController alertController;

    //Foto de contacto
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID; // contacts unique ID
    private String id;

    private ApplicationPreferences appPreferences;

    /**
     * Used to inialize the context and the senDataService
     */
    public HomeFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);
    }

    /**
     * USed to initialize the UI and add click listener to the floating button
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        tvDetailCampus = (TextView) rootView.findViewById(R.id.tvDetailCampus);
        imagenViewDetailCampus = (ImageView) rootView.findViewById(R.id.imagenViewDetailCampus);
        tvRoadSegment = (TextView) rootView.findViewById(R.id.tvRoadSegment);
        speedButtonHome = (FloatingActionButton) rootView.findViewById(R.id.speedButtonHome);
        speedButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drivingView = new Intent(context, DrivingView.class);
                startActivity(drivingView);
            }
        });

        //contactPhoto = (ImageView) rootView.findViewById(R.id.contactPhoto);
        /*if (contactPhoto != null)
            contactPhoto.setImageBitmap(retrieveContactPhoto(context));*/

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Used to store the location
     * @param latitude
     * @param longitude
     * @param speedMS
     * @param speedKmHr
     */
    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        this.latitude = latitude;
        this.longitude = longitude;
        // Log.i("STATUS: ","HomeFragment-sendLocationSpeed");
    }

    /**
     * Used to show the zone data where the user is and change the main image
     * @param zone
     * @param statusLocation
     */
    @Override
    public void detectZone(Zone zone, boolean statusLocation) {
        if (statusLocation == true) {
            //Log.i("STATUS: ","Campus Name: "+campus.getName()+" statusLocation: "+statusLocation);
            if (imagenViewDetailCampus != null && tvDetailCampus != null) {
                imagenViewDetailCampus.setImageResource(R.mipmap.ic_inside_foreground);
                tvDetailCampus.setText(zone.getName().getValue() + "\n" + zone.getAddress().getValue());
                //tvDetailCampus.setText(context.getString(R.string.message_name_campus) + ": " + zone.getName().getValue() + "\n" + context.getString(R.string.message_address_campus) + ": " + zone.getAddress().getValue());
            }
        } else {
            Log.i("STATUS 1: ", "DetectCampus...!" + statusLocation);
            if (imagenViewDetailCampus != null && tvDetailCampus != null) {
                imagenViewDetailCampus.setImageResource(R.mipmap.ic_outside_foreground);
                tvDetailCampus.setText(context.getString(R.string.message_any_campus));
            }
        }
    }

    /**
     * Runs when detects that the user is on a roadsegment
     * @param latitude
     * @param longitude
     * @param roadSegment
     */
    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {
        if(tvRoadSegment != null){
            if(roadSegment != null){
                //tvRoadSegment.setText("ID: "+roadSegment.getIdRoadSegment()+"\n"+context.getString(R.string.name)+": "+roadSegment.getName()+"\n"+context.getString(R.string.message_minimum)+": "+roadSegment.getMinimumAllowedSpeed()+"km/h\n"+context.getString(R.string.message_maximum)+": "+roadSegment.getMaximumAllowedSpeed()+"km/h");
                //tvRoadSegment.setText(roadSegment.getName()+"\n"+context.getString(R.string.message_minimum)+": "+roadSegment.getMinimumAllowedSpeed()+"km/h\n"+context.getString(R.string.message_maximum)+": "+roadSegment.getMaximumAllowedSpeed()+"km/h");
            }else{
                //tvRoadSegment.setText(context.getString(R.string.message_not_road_segment));
            }
        }
    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
        //Log.i("STATUS 1: ","ax: "+ax+" ay: "+ay+" az: "+az);
    }

    @Override
    public void sendEvent(String event) {

    }


}
