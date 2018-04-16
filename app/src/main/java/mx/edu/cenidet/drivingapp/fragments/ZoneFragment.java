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
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.services.SendDataService;
import www.fiware.org.ngsi.datamodel.entity.Zone;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZoneFragment extends Fragment implements OnMapReadyCallback, SendDataService.SendDataMethods {
    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private Context context;
    private String name, location,  centerPoint;
    private JSONArray arrayLocation, arrayPoint;
    private double pointLatitude, pointLongitude;
    private SendDataService sendDataService;
    private int count = 1;

    //Pintar todos los Campus
    private ArrayList<Zone> listZone;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<LatLng> listLocation;

    public ZoneFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_zone, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.mapZone);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
        //Ocultar el boton
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        //createOrUpdateMarkerByLocation(pointLatitude, pointLongitude);
        //Pintar todos los campus
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

    }

    private void createOrUpdateMarkerByLocation(double latitude, double longitude){
        if(marker == null){
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(true));
            zoomToLocation(latitude, longitude);
        }else{
            marker.setPosition(new LatLng(latitude, longitude));
        }
    }

    private void zoomToLocation(double latitude, double longitude){
        camera = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(18)       //limit -> 21
                .bearing(0)    //orientación de la camara hacia el este 0°-365°
                .tilt(30)       //efecto 3D 0-90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        createOrUpdateMarkerByLocation(latitude, longitude);
        //zoomToLocation(latitude, longitude);
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {
        if(statusLocation == true){
            //Log.i("STATUS: ","Count "+count++);
            name = zone.getName().getValue();
            location = zone.getLocation().getValue();
            centerPoint = zone.getCenterPoint().getValue();
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            listLocation = new ArrayList<>();
            try {
                arrayLocation = new JSONArray(location);
                for (int j=0; j<arrayLocation.length(); j++){
                    originalString = arrayLocation.get(j).toString();
                    clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                    subString =  clearString.split(",");
                    latitude = Double.parseDouble(subString[0]);
                    longitude = Double.parseDouble(subString[1]);
                    listLocation.add(new LatLng(latitude,longitude));
                }
               /* arrayPoint = new JSONArray(centerPoint);
                pointLatitude = arrayPoint.getDouble(0);
                pointLongitude = arrayPoint.getDouble(1);*/
                /*JSONObject jsonObject = arrayPoint.getJSONObject(0);
                pointLatitude = jsonObject.getDouble("latitude");
                pointLongitude = jsonObject.getDouble("longitude");*/
                //Dibuja el poligono
                /*gMap.addPolygon(new PolygonOptions()
                        .addAll(listLocation).strokeColor(Color.RED));*/
                //Centra el mapa
               // zoomToLocation(pointLatitude, pointLongitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{

        }
    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
        //Log.i("STATUS 3: ","ax: "+ax+" ay: "+ay+" az: "+az);
    }

    @Override
    public void sendEvent(String event) {

    }
}
