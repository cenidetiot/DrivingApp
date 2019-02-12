package mx.edu.cenidet.app.activities;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mx.edu.cenidet.app.R;

public class AlertMapDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private String subcategory, description, location, severity;
    private double pointLatitude, pointLongitude;

    /**
     * Used to check if the app have a new notification
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_map_detail);
        setToolbar();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.alert_map_detail);
        mapFragment.getMapAsync(this);
        if(getIntent().getStringExtra("subcategory") != null && getIntent().getStringExtra("description") != null && getIntent().getStringExtra("location") != null && getIntent().getStringExtra("severity") != null){
            subcategory = getIntent().getStringExtra("subcategory");
            description = getIntent().getStringExtra("description");
            location = getIntent().getStringExtra("location");
            severity = getIntent().getStringExtra("severity");

            String[] subString;
            subString =  location.split(",");
            pointLatitude = Double.parseDouble(subString[0]);
            pointLongitude = Double.parseDouble(subString[1]);
        }else {
            Log.d("NOTALERT" ,"No contiene los datos");
        }
    }

    /**
     * When the map is ready draw the alert location marker
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        createOrUpdateMarkerByLocation(pointLatitude, pointLongitude, severity, subcategory, description );
    }

    /**
     * Set the toolbar into the view
     */
    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.alert_location);
    }

    /**
     * FUNCTION TO BACK TO THE ACTIVITY WITH THE ARROW
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Create the alert marker on the map adding the respective color
     * @param latitude
     * @param longitude
     * @param severity
     * @param subcategory
     * @param description
     */
    private void createOrUpdateMarkerByLocation(double latitude, double longitude, String severity, String subcategory, String description){
        if(marker == null){
           switch (severity){
                case "informational":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(subcategory).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_informational)));
                    break;
                case "low":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(subcategory).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_low)));
                    break;
                case "medium":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(subcategory).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_medium)));
                    break;
                case "high":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(subcategory).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_high)));
                    break;
                case "critical":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(subcategory).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_critical)));
                    break;
                case "undefined":
                    marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(subcategory).snippet(description).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_undefined)));
                    break;
            }
            zoomToLocation(latitude, longitude);
        }else{
            marker.setPosition(new LatLng(latitude, longitude));
        }
    }

    /**
     * Make the animated zoom effect in the alert location
     * @param latitude
     * @param longitude
     */
    private void zoomToLocation(double latitude, double longitude){
        camera = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(18)
                .bearing(0)
                .tilt(30)
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }
}
