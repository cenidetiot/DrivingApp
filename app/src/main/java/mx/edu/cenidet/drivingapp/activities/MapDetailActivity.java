package mx.edu.cenidet.drivingapp.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.drivingapp.R;

public class MapDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private String name, location,  centerPoint;
    private JSONArray arrayLocation, arrayPoint;
    private ArrayList<LatLng> listLocation;
    private double pointLatitude, pointLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(getIntent().getStringExtra("name") != null && getIntent().getStringExtra("location") != null && getIntent().getStringExtra("centerPoint") != null){
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            centerPoint = getIntent().getStringExtra("centerPoint");
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
                arrayPoint = new JSONArray(centerPoint);
                pointLatitude = arrayPoint.getDouble(0);
                pointLongitude = arrayPoint.getDouble(1);
                /*JSONObject jsonObject = arrayPoint.getJSONObject(0);
                pointLatitude = jsonObject.getDouble("latitude");
                pointLongitude = jsonObject.getDouble("longitude");*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("Map1 ", "Map Detail Name: "+name);
            Log.i("Map1 ", "Map Detail location: "+location);
            Log.i("Map1 ", "Map Detail pointMap: "+centerPoint);
            Log.i("Map1 ", "Map Detail poin1: "+pointLatitude);
            Log.i("Map1 ", "Map Detail poin2: "+pointLongitude);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.addPolygon(new PolygonOptions()
                .addAll(listLocation).strokeColor(Color.RED));
        createOrUpdateMarkerByLocation(pointLatitude, pointLongitude);
    }

    private void createOrUpdateMarkerByLocation(double latitude, double longitude){
        if(marker == null){
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
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
