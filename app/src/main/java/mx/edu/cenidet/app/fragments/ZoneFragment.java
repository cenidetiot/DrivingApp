package mx.edu.cenidet.app.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

import mx.edu.cenidet.app.activities.DrivingView;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.services.SendDataService;
import www.fiware.org.ngsi.datamodel.datatypes.LocationGeoJsonObject;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

import static mx.edu.cenidet.app.activities.MainActivity.getColorWithAlpha;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZoneFragment extends Fragment implements OnMapReadyCallback, SendDataService.SendDataMethods {
    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;
    private Marker marker, markerZone;
    private double latitude = 0, longitude = 0;
    private CameraPosition camera;
    private Context context;
    private String name, location,  centerPoint;
    private JSONArray arrayLocation, arrayPoint;
    private double pointLatitude, pointLongitude;
    private SendDataService sendDataService;
    private int count = 1;
    private ApplicationPreferences applicationPreferences;
    private Zone currentZone = null;

    //Pintar todos los Campus
    //private ArrayList<Zone> listZone;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<LatLng> listLocation, listLocationParking;
    private LatLng centerLatLngParking = null;
    private ArrayList<OffStreetParking> listOffStreetParking;
    private JSONArray arrayLocationParking;
    private FloatingActionButton speedButtonZone;
    private boolean areDrawn = false;
    private boolean mapDrawn = false;

    public ZoneFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        applicationPreferences = new ApplicationPreferences();
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
        rootView = inflater.inflate(R.layout.fragment_zone, container, false);
        speedButtonZone = (FloatingActionButton) rootView.findViewById(R.id.speedButtonZone);
        /**
         * Add the onClickListener to change the activity to DrawView
         */
        speedButtonZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drivingView = new Intent(getContext(), DrivingView.class);
                startActivity(drivingView);
            }
        });
        return rootView;
    }

    /**
     * Used to initialize the map
     * @param view
     * @param savedInstanceState
     */
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

    /**
     * Runs when the View is visible to the user
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            zoomToLocation(latitude, longitude);
        }
    }


    /**
     * Runs qhen the map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
    }


    /**
     * Used to store the location and call the zoom
     * @param latitude
     * @param longitude
     */
    private void createOrUpdateMarkerByLocation(double latitude, double longitude){
        if (this.latitude == 0 && this.longitude == 0) {
            zoomToLocation(latitude, longitude);
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * USed to draw an maker inside the parking DEPRECATED
     * @param latitude
     * @param longitude
     * @param name
     */
    private void createMarkerParking(double latitude, double longitude, String name){
        marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    /**
     * Make the zoom animation
     * @param latitude
     * @param longitude
     */
    private void zoomToLocation(double latitude, double longitude){
        camera = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(18)       //limit -> 21
                .bearing(0)    //orientación de la camara hacia el este 0°-365°
                .tilt(30)       //efecto 3D 0-90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    public void drawZones(){
        ArrayList<Zone> Zones = sqLiteDrivingApp.getAllZone();
        for (Zone zone : Zones){
            drawZone(zone);
        }
    }

    /**
     * Draw the zone where the user is
     * Dibuja la zona en el que se encuentra el dispositivo.
     * @param zone el identificador de la zona.
     */
    public void drawZone(Zone zone){
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            listLocation = new ArrayList<>();
            try {
                arrayLocation = new JSONArray(zone.getLocation().getValue());
                for (int j=0; j<arrayLocation.length(); j++){
                    originalString = arrayLocation.get(j).toString();
                    clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                    subString =  clearString.split(",");
                    latitude = Double.parseDouble(subString[0]);
                    longitude = Double.parseDouble(subString[1]);
                    listLocation.add(new LatLng(latitude,longitude));
                }
                gMap.addPolygon(new PolygonOptions()
                        .fillColor(getColorWithAlpha(Color.parseColor("#2ecc71"), 0.1f))
                        .addAll(listLocation).strokeColor(Color.parseColor("#2ecc71")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    /**
     * Draw the parking of the zone where the user is
     * Dibuja el parking de la zona.
     * @param zoneId el identificador de la zona.
     */
    public void drawParking(String zoneId){
            listOffStreetParking = sqLiteDrivingApp.getAllOffStreetParkingByAreaServed(zoneId);
            if (listOffStreetParking.size() > 0) {
                String originalStringParking, clearStringParking;
                double latitudeParking, longitudeParking;
                String[] subStringParking;
                LatLngBounds.Builder builder;
                for (int i = 0; i < listOffStreetParking.size(); i++) {
                    builder = new LatLngBounds.Builder();
                    listLocationParking = new ArrayList<>();
                    try {
                        arrayLocationParking = new JSONArray(listOffStreetParking.get(i).getLocation());
                        for (int j = 0; j < arrayLocationParking.length(); j++) {
                            originalStringParking = arrayLocationParking.get(j).toString();
                            clearStringParking = originalStringParking.substring(originalStringParking.indexOf("[") + 1, originalStringParking.indexOf("]"));
                            subStringParking = clearStringParking.split(",");
                            latitudeParking = Double.parseDouble(subStringParking[0]);
                            longitudeParking = Double.parseDouble(subStringParking[1]);
                            //listLocationParking.add(new LatLng(latitudeParking, longitudeParking));
                            LatLng tmp = new LatLng(latitudeParking, longitudeParking);
                            listLocationParking.add(tmp);
                            builder.include(tmp); //Le agregas los puntos del poligono
                        }
                        LatLngBounds bounds = builder.build(); //Obtienes los limites del poligono
                        centerLatLngParking = bounds.getCenter(); //Obtienes el centro de los limites del poligono
                        if (gMap != null) {
                            gMap.addPolygon(
                                    new PolygonOptions().
                                            addAll(listLocationParking)
                                            .fillColor(getColorWithAlpha(Color.parseColor("#3498db"), 0.1f))
                                            .strokeColor(Color.parseColor("#3498db")))                             ;

                        }
                        //createMarkerParking(centerLatLngParking.latitude, centerLatLngParking.longitude, listOffStreetParking.get(i).getName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    drawRoadSegmentByParking(listOffStreetParking.get(i).getIdOffStreetParking());
                }
            }

    }

    /**
     * Draw the roadSegments inside the parking where the user is
     * @param responsible
     */
    public void drawRoadSegmentByParking(String responsible){
        ArrayList<Road> listRoadByResponsible = sqLiteDrivingApp.getRoadByResponsible(responsible); //obtiene la lista de los road por el responsable.
        if( listRoadByResponsible.size() > 0) {
            for(int i=0; i<listRoadByResponsible.size(); i++) {
                ArrayList<RoadSegment> getAllRoadSegmentByRefRoad = sqLiteDrivingApp.getAllRoadSegmentByRefRoad(listRoadByResponsible.get(i).getIdRoad());
                if (getAllRoadSegmentByRefRoad.size() > 0) {
                    String originalStringParking, clearStringParking;
                    double latitude, longitude;
                    String[] subStringParking;
                    JSONArray arrayLocationRoadSegment;
                    for (RoadSegment iteratorRoadSegment : getAllRoadSegmentByRefRoad) {
                        ArrayList<LatLng> listLocationRoadSegment = new ArrayList<>();
                        try {
                            arrayLocationRoadSegment = new JSONArray(iteratorRoadSegment.getLocation());
                            for (int j = 0; j < arrayLocationRoadSegment.length(); j++) {
                                originalStringParking = arrayLocationRoadSegment.get(j).toString();
                                clearStringParking = originalStringParking.substring(originalStringParking.indexOf("[") + 1, originalStringParking.indexOf("]"));
                                subStringParking = clearStringParking.split(",");
                                latitude = Double.parseDouble(subStringParking[0]);
                                longitude = Double.parseDouble(subStringParking[1]);
                                listLocationRoadSegment.add(new LatLng(latitude, longitude));
                            }
                            if (gMap != null) {
                                gMap.addPolyline(new PolylineOptions()
                                        .addAll(listLocationRoadSegment)
                                        .width(8)
                                        .color(Color.RED));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Runs when he location change
     * @param latitude
     * @param longitude
     * @param speedMS
     * @param speedKmHr
     */
    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        createOrUpdateMarkerByLocation(latitude, longitude);
    }

    /**
     * Runs when change the zone where the user is
     * @param zone
     * @param statusLocation
     */
    @Override
    public void detectZone(Zone zone, boolean statusLocation) {
        if (statusLocation){
            if (currentZone != null){

                if (!currentZone.getIdZone().equals(zone.getIdZone())){
                    drawZone(zone);
                    drawParking(zone.getIdZone());
                    //drawRoadSegmentByParking(zone.getIdZone());
                }

            }else {
                drawZone(zone);
                drawParking(zone.getIdZone());
                //drawRoadSegmentByParking(zone.getIdZone());

            }
            currentZone = zone;
        }

    }

    /**
     * Runs when change the roadSegment where the user is
     * @param latitude
     * @param longitude
     * @param roadSegment
     */
    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {
        if (roadSegment != null){
            Log.d("ROADSTEST", roadSegment.getName());
        }else {
            Log.d("ROADSTEST", "Sin roadSegmetn");
        }
    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
    }

    @Override
    public void sendEvent(String event) {

    }
}
