package mx.edu.cenidet.drivingapp.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import mx.edu.cenidet.drivingapp.R;

public class AlertMapDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private String category, description, location, severity;
    private double pointLatitude, pointLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_map_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.alert_map_detail);
        mapFragment.getMapAsync(this);
        if(getIntent().getStringExtra("category") != null && getIntent().getStringExtra("description") != null && getIntent().getStringExtra("location") != null && getIntent().getStringExtra("severity") != null){
            category = getIntent().getStringExtra("category");
            description = getIntent().getStringExtra("description");
            location = getIntent().getStringExtra("location");
            severity = getIntent().getStringExtra("severity");

            String[] subString;
            subString =  location.split(",");
            pointLatitude = Double.parseDouble(subString[0]);
            pointLongitude = Double.parseDouble(subString[1]);
            Log.i("Status ", "Map Detail category: "+category);
            Log.i("Status ", "Map Detail description: "+description);
            Log.i("Status ", "Map Detail location: "+location);
            Log.i("Status ", "Map Detail pointLatitude: "+pointLatitude+" pointLongitude: "+pointLongitude);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        createOrUpdateMarkerByLocation(pointLatitude, pointLongitude);
        /*gMap.addPolygon(new PolygonOptions()
                .addAll(listLocation).strokeColor(Color.RED));
        createOrUpdateMarkerByLocation(pointLatitude, pointLongitude);*/
    }

    private void createOrUpdateMarkerByLocation(double latitude, double longitude){
        if(marker == null){
           // marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(category+"\n"+description));
            //marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(category).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alerts_menu)).anchor((float) 0.5, (float) 0.5).rotation((float) 90.0));
            switch (severity){
                case "informational":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(category).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alert_informational)));
                    break;
                case "low":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(category).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alert_low)));
                    break;
                case "medium":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(category).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alert_medium)));
                    break;
                case "high":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(category).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alert_high)));
                    break;
                case "critical":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(category).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alert_critical)));
                    break;
            }
            //marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
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
}
