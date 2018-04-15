package mx.edu.cenidet.drivingapp.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.services.SendDataService;
import www.fiware.org.ngsi.datamodel.entity.Zone;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyLocationFragment extends Fragment implements OnMapReadyCallback, SendDataService.SendDataMethods{
    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private static final String STATUS = "Status";
    private Context context;
    private ArrayList<Zone> listZone;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<LatLng> listLocation;
    private ArrayList<LatLng> listPolyline;
    private SendDataService sendDataService;
    public MyLocationFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        sendDataService = new SendDataService(this);
        //sendDataService = HomeFragment.SENDDATASERVICE;
        listPolyline = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_location, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.mapMyLocation);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        listZone = sqLiteDrivingApp.getAllZone();
        if(listZone.size() == 0){

        }else{
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            for(int i=0; i<listZone.size(); i++){
                listLocation = new ArrayList<>();
                try {
                    arrayLocation = new JSONArray(listZone.get(i).getLocation().getValue());
                    for (int j=0; j<arrayLocation.length(); j++){
                        originalString = arrayLocation.get(j).toString();
                        clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                        subString =  clearString.split(",");
                        latitude = Double.parseDouble(subString[0]);
                        longitude = Double.parseDouble(subString[1]);
                        listLocation.add(new LatLng(latitude,longitude));
                        Log.i("Status: ", "Latitude: "+latitude+ " Longitude: "+longitude);
                    }
                    gMap.addPolygon(new PolygonOptions()
                            .addAll(listLocation).strokeColor(Color.RED));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("Status ", "Lista con datos");
        }
       // if(listPolyline.size() > 1){
           /* gMap.addPolyline(new PolylineOptions()
                    .addAll(listPolyline)
                    .width(5)
                    .color(Color.BLUE));*/
            /*gMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(18.879757, -99.221625),
                            new LatLng(18.879592, -99.221646),
                            new LatLng(18.879363, -99.221208))
                    .width(5)
                    .color(Color.BLUE));*/
        //}

        gMap.setMyLocationEnabled(true);
        //Ocultar el boton
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void createOrUpdateMarkerByLocation(double latitude, double longitude){
        if(marker == null){
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(true));
            zoomToLocation(latitude, longitude);
        }else{
            marker.setPosition(new LatLng(latitude, longitude));
            //Polyline
        }
    }

    private void zoomToLocation(double latitude, double longitude){
        camera = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)       //limit -> 21
                .bearing(0)    //orientación de la camara hacia el este 0°-365°
                .tilt(30)       //efecto 3D 0-90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        createOrUpdateMarkerByLocation(latitude, longitude);
        //listPolyline.add(new LatLng(latitude,longitude));

        //sLog.i("STATUS: ","MyLocationFragment-sendLocationSpeed");
        //Log.i("SPEED: ", "VIEW Latitude: " + latitude + " Longitude: " + longitude + " Velocidad: " + speedMS + "m/s  Velocidad: " + speedKmHr + "km/hr");
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {

    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
        //Log.i("STATUS 4: ","ax: "+ax+" ay: "+ay+" az: "+az);
    }

    @Override
    public void sendEvent(String event) {

    }
}
