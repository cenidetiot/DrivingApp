package mx.edu.cenidet.app.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.app.R;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;

public class MapDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private String idZone, name, location,  centerPoint;
    private JSONArray arrayLocation, arrayPoint, arrayLocationParking;
    private ArrayList<LatLng> listLocation, listLocationParking;
    private double pointLatitude, pointLongitude;
    private LatLng centerLatLng = null, centerLatLngParking = null;
    private ArrayList<OffStreetParking> listOffStreetParking;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        context = HomeActivity.MAIN_CONTEXT;
        setToolbar();
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(getIntent().getStringExtra("idZone") != null && getIntent().getStringExtra("name") != null && getIntent().getStringExtra("location") != null && getIntent().getStringExtra("centerPoint") != null){
            idZone = getIntent().getStringExtra("idZone");
            name = getIntent().getStringExtra("name");
            location = getIntent().getStringExtra("location");
            centerPoint = getIntent().getStringExtra("centerPoint");
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            listLocation = new ArrayList<>();

            LatLngBounds.Builder builder = new LatLngBounds.Builder(); //Creas un builder
            try {
                arrayLocation = new JSONArray(location);
                for (int j=0; j<arrayLocation.length(); j++){
                    originalString = arrayLocation.get(j).toString();
                    clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                    subString =  clearString.split(",");
                    latitude = Double.parseDouble(subString[0]);
                    longitude = Double.parseDouble(subString[1]);
                    LatLng tmp = new LatLng(latitude,longitude);
                    listLocation.add(tmp);
                    builder.include(tmp); //Le agregas los puntos del poligono
                }
                /*arrayPoint = new JSONArray(centerPoint);
                pointLatitude = arrayPoint.getDouble(0);
                pointLongitude = arrayPoint.getDouble(1);*/

                LatLngBounds bounds = builder.build(); //Obtienes los limites del poligono
                centerLatLng =  bounds.getCenter(); //Obtienes el centro de los limites del poligono
                /*JSONObject jsonObject = arrayPoint.getJSONObject(0);
                pointLatitude = jsonObject.getDouble("latitude");
                pointLongitude = jsonObject.getDouble("longitude");*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("Map1 ", "Map Detail ID: "+idZone);
            Log.i("Map1 ", "Map Detail Name: "+name);
            Log.i("Map1 ", "Map Detail location: "+location);
            Log.i("Map1 ", "Map Detail pointMap: "+centerPoint);
            Log.i("Map1 ", "Map Detail poin1: "+pointLatitude);
            Log.i("Map1 ", "Map Detail poin2: "+pointLongitude);
        }
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_zone_detail);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.addPolygon(new PolygonOptions()
                .addAll(listLocation).strokeColor(Color.RED));
        createOrUpdateMarkerByLocation(centerLatLng.latitude, centerLatLng.longitude, name);

        drawParking(idZone);
        //createOrUpdateMarkerByLocation(pointLatitude, pointLongitude);
    }

    private void createOrUpdateMarkerByLocation(double latitude, double longitude, String name){
        if(marker == null) {
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
            zoomToLocation(latitude, longitude);
        }else{
            marker.setPosition(new LatLng(latitude, longitude));
        }
    }
    private void createMarkerParking(double latitude, double longitude, String name){
        marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
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

    public void drawParking(String idZone){
        listOffStreetParking = sqLiteDrivingApp.getAllOffStreetParkingByAreaServed(idZone);
        if(listOffStreetParking.size() > 0){
            String originalStringParking, clearStringParking;
            double latitudeParking, longitudeParking;
            String[] subStringParking;
            LatLngBounds.Builder builder;
            for (int i=0; i<listOffStreetParking.size(); i++){
                builder = new LatLngBounds.Builder();
                listLocationParking = new ArrayList<>();
                try {
                    arrayLocationParking = new JSONArray(listOffStreetParking.get(i).getLocation());
                    for (int j=0; j<arrayLocationParking.length(); j++){
                        originalStringParking = arrayLocationParking.get(j).toString();
                        clearStringParking = originalStringParking.substring(originalStringParking.indexOf("[") + 1, originalStringParking.indexOf("]"));
                        subStringParking =  clearStringParking.split(",");
                        latitudeParking = Double.parseDouble(subStringParking[0]);
                        longitudeParking = Double.parseDouble(subStringParking[1]);
                        //listLocationParking.add(new LatLng(latitudeParking, longitudeParking));
                        LatLng tmp = new LatLng(latitudeParking,longitudeParking);
                        listLocationParking.add(tmp);
                        builder.include(tmp); //Le agregas los puntos del poligono
                    }
                    LatLngBounds bounds = builder.build(); //Obtienes los limites del poligono
                    centerLatLngParking =  bounds.getCenter(); //Obtienes el centro de los limites del poligono
                    if(gMap != null){
                        gMap.addPolygon(new PolygonOptions()
                                .addAll(listLocationParking).strokeColor(Color.BLUE));
                    }
                    createMarkerParking(centerLatLngParking.latitude, centerLatLngParking.longitude, listOffStreetParking.get(i).getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
