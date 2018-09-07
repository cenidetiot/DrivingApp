package mx.edu.cenidet.app.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.AlertsActivity;
import mx.edu.cenidet.app.activities.DrivingView;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.services.SendDataService;
import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

import static mx.edu.cenidet.app.activities.MainActivity.getColorWithAlpha;

public class AlertsMapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        SendDataService.SendDataMethods,
        AlertsControllerSdk.AlertsServiceMethods{
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
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<LatLng> listLocation, listLocationParking;
    private LatLng centerLatLngParking = null;
    private ArrayList<OffStreetParking> listOffStreetParking;
    private JSONArray arrayLocationParking;
    private FloatingActionButton speedButtonZone;
    private boolean areDrawn = false;
    private boolean mapDrawn = false;

    private TextView textZone;
    private TextView textAddressZone;
    private AlertsControllerSdk alertsControllerSdk;
    private String zoneId;

    private ArrayList<Alert> listAlerts;




    public AlertsMapFragment() {
        context = AlertsActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);
        alertsControllerSdk = new AlertsControllerSdk(context, this);
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        applicationPreferences = new ApplicationPreferences();
        getFirstAlerts();

    }

    public void getFirstAlerts() {
        if(applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE) != null){
            zoneId = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
            if(!zoneId.equals("undetectedZone")) {
                String typeUser = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_USER_TYPE);
                String tempQuery = zoneId;
                if (typeUser != null && typeUser !="" && typeUser.equals("mobileUser")){
                    tempQuery += "?id=Alert:Device_Smartphone_.*&location=false";
                }
                alertsControllerSdk.currentAlertByZone(tempQuery);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_zone, container, false);

        textZone = (TextView) rootView.findViewById(R.id.textNameZone);
        textAddressZone = (TextView) rootView.findViewById(R.id.textAddressZone);
        speedButtonZone = (FloatingActionButton) rootView.findViewById(R.id.speedButtonZone);
        speedButtonZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drivingView = new Intent(getContext(), DrivingView.class);
                startActivity(drivingView);
                //drivingMode();
            }
        });
        LinearLayout card = (LinearLayout) rootView.findViewById(R.id.cardTitle);
        speedButtonZone = (FloatingActionButton) rootView.findViewById(R.id.speedButtonZone);
        speedButtonZone.setVisibility(View.INVISIBLE);
        //card.setBackgroundColor(getColorWithAlpha(Color.parseColor("#bdc3c7"), 0.9f));
        //card.setBackgroundColor(getColorWithAlpha(Color.parseColor("#bdc3c7"), 0.9f));
        listAlerts = new ArrayList<Alert>();
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

        try {
            boolean success = googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( context, R.raw.map_style_retro));

        } catch (Resources.NotFoundException e) {
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
        //gMap.setTrafficEnabled(true);
        gMap.setOnMarkerClickListener(this);
        gMap.setIndoorEnabled(true);
    }


    private void createOrUpdateMarkerByLocation(double latitude, double longitude){
        if (this.latitude == 0 && this.longitude == 0) {
            zoomToLocation(latitude, longitude);
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private void createMarkerAlert(double latitude, double longitude, String name, String severity, String subCategory){

        Bitmap markerBySeverity;
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

        markerBySeverity = resizeMapIcons(markerName,80,80);

        marker = gMap.addMarker(
                new MarkerOptions()
                        .position(
                                new LatLng(latitude, longitude))
                        .snippet(name)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerBySeverity))

        );





    }

    private Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
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


    private void drivingMode() {

        camera = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(21)       //limit -> 21
                .bearing(0)    //orientación de la camara hacia el este 0°-365°
                .tilt(90)       //efecto 3D 0-90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

        gMap.addMarker(
                new MarkerOptions()
                        .position(
                                new LatLng(latitude, longitude)
                        )
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_navigation_white_36))
        );
    }

    public void drawZones(){
        ArrayList<Zone> Zones = sqLiteDrivingApp.getAllZone();
        for (Zone zone : Zones){
            drawZone(zone);
        }
    }

    /**
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

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        createOrUpdateMarkerByLocation(latitude, longitude);
    }



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

            textZone.setText(currentZone.getName().getValue());
            textAddressZone.setText(currentZone.getAddress().getValue());
        }

    }

    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {

    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
    }

    @Override
    public void sendEvent(String event) {

    }

    @Override
    public void currentAlertByZone(Response response) {
        switch (response.getHttpCode()) {
            case 0:
                Log.i("STATUS", "Internal Server Error 1...!");
                break;
            case 200:
                listAlerts.clear();
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                if(jsonArray.length() == 0 || jsonArray == null){
                    Toast.makeText(context, R.string.message_no_alerts_show, Toast.LENGTH_SHORT).show();
                }else{
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Log.d("GETTINGALERTS", object.getString("id"));
                            String[] subString;
                            subString = object.getString("location").split(",");
                            double centerLatitude = Double.parseDouble(subString[0]);
                            double centerLongitude = Double.parseDouble(subString[1]);
                            createMarkerAlert(centerLatitude, centerLongitude,  object.getString("id"), object.getString("severity"), object.getString("subCategory"));
                            Alert tempAlert = new Alert();
                            tempAlert.setId(object.getString("id"));
                            tempAlert.setType(object.getString("type"));
                            tempAlert.getAlertSource().setValue(object.getString("alertSource"));
                            tempAlert.getCategory().setValue(object.getString("category"));
                            tempAlert.getDateObserved().setValue(object.getString("dateObserved"));
                            tempAlert.getDescription().setValue(object.getString("description"));
                            tempAlert.getLocation().setValue(object.getString("location"));
                            tempAlert.getSeverity().setValue(object.getString("severity"));
                            tempAlert.getSubCategory().setValue(object.getString("subCategory"));
                            tempAlert.getValidFrom().setValue(object.getString("validFrom"));
                            tempAlert.getValidTo().setValue(object.getString("validTo"));
                            listAlerts.add(tempAlert);
                            Log.d("MAKERS", tempAlert.getId());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void historyAlertByZone(Response response) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("MAKERS", marker.getSnippet());

        Dialog makerDescription = new Dialog(context);
        makerDescription.setContentView(R.layout.alert_dialog_description);


        makerDescription.show();

        return false;
    }
}
