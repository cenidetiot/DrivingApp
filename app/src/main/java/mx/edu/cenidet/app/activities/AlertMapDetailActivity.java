package mx.edu.cenidet.app.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.event.EventsFuntions;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;

import static mx.edu.cenidet.app.activities.MainActivity.getColorWithAlpha;


public class AlertMapDetailActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private String subcategory, description, location, severity, id, date;
    private double pointLatitude, pointLongitude;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<Zone> listZone;
    private Zone zone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_map_detail);
        setToolbar();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.alert_map_detail);
        mapFragment.getMapAsync(this);
        sqLiteDrivingApp = new SQLiteDrivingApp(this);

        if(getIntent().getStringExtra("subcategory") != null && getIntent().getStringExtra("description") != null && getIntent().getStringExtra("location") != null && getIntent().getStringExtra("severity") != null){
            id = getIntent().getStringExtra("id");
            subcategory = getIntent().getStringExtra("subcategory");
            description = getIntent().getStringExtra("description");
            location = getIntent().getStringExtra("location");
            severity = getIntent().getStringExtra("severity");
            date = getIntent().getStringExtra("dateObserved");

            String[] subString;
            subString =  location.split(",");
            pointLatitude = Double.parseDouble(subString[0]);
            pointLongitude = Double.parseDouble(subString[1]);

            listZone = sqLiteDrivingApp.getAllZone();
            zone = EventsFuntions.detectedZone(pointLatitude, pointLongitude, listZone);

            Log.d("MARKERS", " _ _ " + date);



        }else {
            Log.d("NOTALERT" ,"No contiene los datos");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        try {
            boolean success = googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( this, R.raw.map_style_retro));

        } catch (Resources.NotFoundException e) {
        }
        //createOrUpdateMarkerByLocation(pointLatitude, pointLongitude, severity, subcategory, description );

        createMarkerAlert(
                pointLatitude,
                pointLongitude,
                id,
                getImageOfAlert(
                        severity,
                        subcategory,
                        80,
                        80
                )
        );

        if (zone !=  null){
            Log.d("MARKERMAP", "Se encontro campus");
            drawZone(zone);
            drawParking(zone.getIdZone());
        }

        zoomToLocation(pointLatitude,pointLongitude);
        gMap.setOnMarkerClickListener(this);
    }

    // SET TOOLBAR METHOD
    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.alert_location);
    }
    //FUNCTION TO BACK TO THE ACTIVITY WITH THE ARROW
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void checkGPS(){
        Log.d("LOADZONES", "CHEKING GPS");

        LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            Intent mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            showGPSDisabledAlert();
        }
        return;
    }

    private void showGPSDisabledAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_alert_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.button_enable_alert_gps,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

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

    private void createMarkerAlert(double latitude, double longitude, String name, Bitmap markerBySeverity){

        marker = gMap.addMarker(
                new MarkerOptions()
                        .position(
                                new LatLng(latitude, longitude))
                        .snippet(name)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerBySeverity))

        );

    }

    private Bitmap getImageOfAlert (String severity, String subCategory , int width, int height ) {

        String markerName = "";

        if (subCategory.equals("trafficJam")){
            markerName = "traffic";
        }else if(subCategory.equals("carAccident")){
            markerName = "accident";
        } else if(subCategory.equals("suddenStop")){
            markerName = "sudden";
        }else if(subCategory.equals("wrongWay")){
            markerName = "wrong";
        }else if(subCategory.equals("speeding")){
            markerName = "speed";
        } else {
            markerName = "warning";
            severity = "";
        }

        switch (severity) {
            case "informational" :
                markerName += "_low";
                break;
            case "low":
                markerName += "_info";
                break;
            case "medium":
                markerName += "_med";
                break;
            case "high":
                markerName += "_high";
                break;
            case "critical" :
                markerName += "_critical";
                break;
            default:
                break;
        }



        return resizeMapIcons(markerName,80,80);
    }

    private Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void zoomToLocation(double latitude, double longitude){
        camera = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(18)
                .bearing(0)
                .tilt(30)
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }



    int getColorBySeverity (String severity){
        int color;
        switch (severity){
            case "informational":
                color  = R.color.driving_blue;
                Log.d("COLOR", "" + 1);
                break;
            case "low":
                color  = R.color.driving_dark_blue;
                Log.d("COLOR", "" + 2);
                break;
            case "medium":
                color  = R.color.driving_yellow;
                Log.d("COLOR", "" + 3);
                break;
            case "high":
                color  = R.color.driving_orange;
                Log.d("COLOR", "" + 4);
                break;
            case "critical":
                color  = R.color.driving_red;
                Log.d("COLOR", "" + 5);
                break;
            default:
                color = R.color.white50;
                Log.d("COLOR", "" + 6);
                break;
        }
        return color;


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("MARKERS", "CLIKED");

        Dialog makerDescription = new Dialog(this);
        makerDescription.setContentView(R.layout.alert_dialog_description);
        ImageView alertIcon = (ImageView) makerDescription.findViewById(R.id.alertIcon);
        alertIcon.setImageBitmap(
                getImageOfAlert(
                        severity,
                        subcategory,
                        200,
                        200
                )
        );
        TextView textSubCategory = (TextView) makerDescription.findViewById(R.id.txtSubcategory) ;
        TextView textDescription = (TextView) makerDescription.findViewById(R.id.txtDescription) ;
        TextView textDate = (TextView) makerDescription.findViewById(R.id.txtDate) ;
        TextView textSeverity = (TextView) makerDescription.findViewById(R.id.txtSeverity );

        //String severity = alert.getSeverity().getValue();
        textSeverity.setText(severity.toUpperCase());
        textSeverity.setTextColor(getResources().getColor(getColorBySeverity(severity)));


        textSubCategory.setText(subcategory.toUpperCase());
        textDescription.setText(description);
        textDate.setText(date.substring(11,16));


        makerDescription.show();

        return false;
    }

    public void drawZone(Zone zone){
        ArrayList listLocation;
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


    public void drawParking(String zoneId){
        ArrayList<OffStreetParking>  listOffStreetParking = sqLiteDrivingApp.getAllOffStreetParkingByAreaServed(zoneId);
        JSONArray arrayLocationParking;
        ArrayList<LatLng> listLocationParking;
        LatLng centerLatLngParking = null;

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
}
