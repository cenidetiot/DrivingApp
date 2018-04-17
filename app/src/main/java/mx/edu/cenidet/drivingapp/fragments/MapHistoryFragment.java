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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.Zone;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapHistoryFragment extends Fragment implements OnMapReadyCallback {
    View rootView;
    private MapView mapView;
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private Context context;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<LatLng> listLocation;
    private String zoneId;
    public MapHistoryFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_history, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.mapHistoryAlerts);

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
        gMap.setMyLocationEnabled(true);
        //Ocultar el boton
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        drawZone(this.zoneId);
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
                .zoom(19)       //limit -> 21
                .bearing(0)    //orientación de la camara hacia el este 0°-365°
                .tilt(30)       //efecto 3D 0-90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    public void renderZone(String zoneId){
        this.zoneId = zoneId;
        //this.zoneId = "Zone_1523933778251";
    }
    //Buscar la zona en la DB interna y pintar en el mapa.
    public void drawZone(String zoneId){
        if(!zoneId.equals("undetectedZone")){
            Zone zone = sqLiteDrivingApp.getZoneById(zoneId);
            JSONArray arrayLocation, arrayPoint;
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;

            try {
                String centerPoint = zone.getCenterPoint().getValue();
                arrayLocation = new JSONArray(zone.getLocation().getValue());
                listLocation = new ArrayList<>();
                for (int j=0; j<arrayLocation.length(); j++){
                    originalString = arrayLocation.get(j).toString();
                    clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                    subString =  clearString.split(",");
                    latitude = Double.parseDouble(subString[0]);
                    longitude = Double.parseDouble(subString[1]);
                    listLocation.add(new LatLng(latitude,longitude));
                }
                arrayPoint = new JSONArray(centerPoint);
                double centerLatitude = arrayPoint.getDouble(0);
                double centerLongitude = arrayPoint.getDouble(1);
                zoomToLocation(centerLatitude, centerLongitude);
                if(gMap != null){
                    gMap.addPolygon(new PolygonOptions()
                            .addAll(listLocation).strokeColor(Color.RED));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Log.i("NO:", "SE DETECTO NINGUNA ZONA -----------------------------------------------------");
        }
    }
    public void renderListAlerts(ArrayList<Alert> listAlerts){
        if(listAlerts.size() > 0){
            Log.i("MapHistoryFragment:", "MAYOR QUE 0");
        }else{
            Log.i("MapHistoryFragment:", "IGUAL  QUE 0");
        }
        //Log.i("MapHistoryFragment: 3", text);
    }

    public void renderAlert(Alert alert){

        if(alert != null){
            String severity = alert.getSeverity().getValue();
            String category = alert.getCategory().getValue();
            String description = alert.getDescription().getValue();
            String[] subString;
            subString =  alert.getLocation().getValue().split(",");
            double centerLatitude = Double.parseDouble(subString[0]);
            double centerLongitude = Double.parseDouble(subString[1]);
            switch (severity){
                case "informational":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(centerLatitude, centerLongitude)).title(category).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_informational)));
                    break;
                case "low":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(centerLatitude, centerLongitude)).title(category).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_low)));
                    break;
                case "medium":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(centerLatitude, centerLongitude)).title(category).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_medium)));
                    break;
                case "high":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(centerLatitude, centerLongitude)).title(category).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_high)));
                    break;
                case "critical":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(centerLatitude, centerLongitude)).title(category).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_critical)));
                    break;
            }
            //marker = gMap.addMarker(new MarkerOptions().position(new LatLng(centerLatitude, centerLongitude)));
            Log.i("MapHistoryFragment:", "renderAlert: "+alert.getId()+ " category: "+alert.getCategory().getValue()+" location: "+alert.getLocation().getValue()+" Latitude: "+centerLatitude+" Longitude: "+centerLongitude);
        }
    }
}
