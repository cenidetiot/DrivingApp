package mx.edu.cenidet.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.controllers.OffStreetParkingControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.RoadControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.RoadSegmentControllerSdk;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.adapters.PagerAdapter;
import mx.edu.cenidet.app.services.DeviceService;
import mx.edu.cenidet.app.services.SendDataService;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;
import www.fiware.org.ngsi.utilities.Tools;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, SendDataService.SendDataMethods, AlertController.AlertResourceMethods, RoadSegmentControllerSdk.RoadSegmentServiceMethods, RoadControllerSdk.RoadServiceMethods, OffStreetParkingControllerSdk.OffStreetParkingServiceMethods{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public static Context MAIN_CONTEXT = null;
    private int numberTab;
    private FrameLayout frameLayout;
    private ApplicationPreferences appPreferences;
    private double latitude, longitude;
    private AlertController alertController;
    private SendDataService sendDataService;
    private RoadControllerSdk roadControllerSdk;
    private RoadSegmentControllerSdk roadSegmentControllerSdk;
    private OffStreetParkingControllerSdk offStreetParkingControllerSdk;
    private SQLiteDrivingApp sqLiteDrivingApp;

    private FloatingActionButton btnFloatingUnknown;
    private FloatingActionButton btnFloatingAccident;
    private FloatingActionButton btnFloatingTraffic;

    //Gestión Road y RoadSegment
    private ArrayList<Road> listRoad;
    private ArrayList<RoadSegment> listRoadSegment;
    private ArrayList<OffStreetParking> listOffStreetParking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MAIN_CONTEXT = HomeActivity.this;

        appPreferences = new ApplicationPreferences();
        sendDataService = new SendDataService(this);
        alertController = new AlertController(this);
        roadControllerSdk = new RoadControllerSdk(MAIN_CONTEXT, this);
        roadSegmentControllerSdk = new RoadSegmentControllerSdk(MAIN_CONTEXT, this);
        offStreetParkingControllerSdk = new OffStreetParkingControllerSdk(MAIN_CONTEXT, this);
        sqLiteDrivingApp = new SQLiteDrivingApp(this);

        //Inicializa los datos de conexión
        try {
            Tools.initialize("config.properties", getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Mandar a llamar el toolbar una vez generado en el activity_main de la actividad
        setToolbar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navView);
        btnFloatingGUI();
        setFragmentDefault();
        //frameLayout = (FrameLayout)findViewById(R.id.headerNavigationDrawer).findViewById(R.id.tvUserName);

        //TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_menu));//setText(R.string.menu_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_speed));//.setText(R.string.menu_speed));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_map));//setText(R.string.menu_campus_map));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_my_alerts));//.setText(R.string.menu_alerts));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_my_campus));//setText(R.string.menu_my_campus));

        //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_my_location));//setText(R.string.menu_my_location));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        numberTab = tabLayout.getTabCount();
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), numberTab);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                changeDrawerMenu(position);
                Log.i("POSITION: ", "-------------------------------------"+position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean fratmentTransaction = false;
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.menu_home:
                        //fragment = new HomeFragment();
                        viewPager.setCurrentItem(0);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_speed:
                        //fragment = new SpeedFragment();
                        viewPager.setCurrentItem(1);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_campus:
                        //fragment = new ZoneFragment();
                        viewPager.setCurrentItem(2);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_alerts:
                        //fragment = new AlertsFragment();
                        viewPager.setCurrentItem(3);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_my_campus:
                        viewPager.setCurrentItem(4);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_history:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(getApplicationContext(), AlertHistoryActivity.class);
                        startActivity(intent);
                        break;
                    /*case R.id.menu_profile:
                        drawerLayout.closeDrawers();
                        Intent intentMyProfile = new Intent(getApplicationContext(), MyProfileActivity.class);
                        startActivity(intentMyProfile);
                        break;*/
                    case R.id.menu_logout:
                        sqLiteDrivingApp.deleteDatabase(MAIN_CONTEXT);
                        appPreferences.removeSharedPreferences(MAIN_CONTEXT, ConstantSdk.PREFERENCE_NAME_GENERAL);
                        Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intentMainActivity);
                        finish();
                        break;
                }

                if(fratmentTransaction){
                    //changeFragment(fragment, item);
                    drawerLayout.closeDrawers();
                }
                return true;
            }
        });

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.header_navigation_drawer, null);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        tvUserName.setText(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME));
        navigationView.addHeaderView(view);

        //Para preguntar si el usuario se encuentra manejando
        //isDrivingUser();

        //Descarga los Road y RoadSegment
        listRoad = sqLiteDrivingApp.getAllRoad();
        if(listRoad.size() > 0){
            Log.i("Datos en los ROADS", "-----------------------------------------------------------------------------");
        }else{
            roadControllerSdk.getAllRoad();
        }
        //roadControllerSdk.getByResponsibleRoad("Zone_1523999247187");
        listRoadSegment = sqLiteDrivingApp.getAllRoadSegment();
        if(listRoadSegment.size() > 0){
            Log.i("Datos en los ", "ROAD_SEGMENT-----------------------------------------------------------------------------");
        }else{
            roadSegmentControllerSdk.getAllRoadSegment();
        }
        listOffStreetParking = sqLiteDrivingApp.getAllOffStreetParking();
        if (listOffStreetParking.size() > 0){
            Log.i("Datos en los ", "PARKING-----------------------------------------------------------------------------");
        }else {
            offStreetParkingControllerSdk.getAllOffStreetParking();
        }
        //roadSegmentControllerSdk.getByrefRoadRoadSegment("Road_1524190240036");

        //Inicia el servicio para la captura de la posición.
        Intent deviceService = new Intent(MAIN_CONTEXT, DeviceService.class);
        startService(deviceService);
        Log.i("onCreate", "-----------------------------------------------------------------------------");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("onStart", "-----------------------------------------------------------------------------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "-----------------------------------------------------------------------------");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause", "-----------------------------------------------------------------------------");
    }

    private boolean setCredentialsIfExist(){
        return !(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_TOKEN).equals("") && appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME).equals(""));
    }

    public void btnFloatingGUI(){
        btnFloatingUnknown = (FloatingActionButton)findViewById(R.id.btnFloatingUnknown);
        btnFloatingUnknown.setOnClickListener(this);
        btnFloatingAccident = (FloatingActionButton)findViewById(R.id.btnFloatingAccident);
        btnFloatingAccident.setOnClickListener(this);
        btnFloatingTraffic = (FloatingActionButton)findViewById(R.id.btnFloatingTraffic);
        btnFloatingTraffic.setOnClickListener(this);
    }

    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFragmentDefault(){
        navigationView.getMenu().getItem(0);
        //changeFragment(new HomeFragment(), navigationView.getMenu().getItem(0));
    }

    private void changeFragment(Fragment fragment, MenuItem item){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle());
    }

    private void changeDrawerMenu(int position){
        MenuItem item = navigationView.getMenu().getItem(position);
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //Abrir el menu lateral
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_notify:
                Intent intent = new Intent(getApplicationContext(), AlertHistoryActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void isDrivingUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_is_driving_user_title)
                .setIcon(R.drawable.ic_car)
                .setNegativeButton(R.string.message_is_driving_user_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //acciones del boton Si
                            }
                        })
                .setPositiveButton(R.string.message_is_driving_user_yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                //acciones del boton Si
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void confirmAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.message_confirm_alert_title)
                .setMessage(R.string.message_confirm_alert_subtitle)
                .setIcon(R.drawable.ic_alert_critical)
                .setNegativeButton(R.string.message_is_driving_user_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //acciones del boton No
                            }
                        })
                .setPositiveButton(R.string.message_is_driving_user_yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                if(latitude != 0 && longitude != 0) {
                                    Alert alert = new Alert();
                                    alert.setId(new DevicePropertiesFunctions().getAlertId(MAIN_CONTEXT));
                                    alert.getAlertSource().setValue(new DevicePropertiesFunctions().getDeviceId(MAIN_CONTEXT));
                                    alert.getCategory().setValue("unknownAlert");
                                    alert.getDateObserved().setValue(Functions.getActualDate());
                                    alert.getDescription().setValue("Unknown Alert");
                                    alert.getLocation().setValue(latitude + ", " + longitude);
                                    alert.getSeverity().setValue("critical");
                                    alert.getSubCategory().setValue("unknown");
                                    alert.getValidFrom().setValue(Functions.getActualDate());
                                    alert.getValidTo().setValue(Functions.getActualDate());
                                    try {
                                        alertController.createEntity(MAIN_CONTEXT, alert.getId(), alert);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFloatingUnknown:
               // startActivity(new Intent(HomeActivity.this, SendManualAlertsActivity.class));
                //Toast.makeText(getApplicationContext(), "btnFloatingUnknown...!", Toast.LENGTH_LONG).show();
                confirmAlert();
                break;
            case R.id.btnFloatingAccident:
                Intent intentAccident = new Intent(HomeActivity.this, SendManualAlertsActivity.class);
                intentAccident.putExtra("typeAlert", 1);
                startActivity(intentAccident);
                //Toast.makeText(getApplicationContext(), "btnFloatingAccident...!", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnFloatingTraffic:
                //Toast.makeText(getApplicationContext(), "btnFloatingTraffic...!", Toast.LENGTH_LONG).show();
                Intent intentTraffic = new Intent(HomeActivity.this, SendManualAlertsActivity.class);
                intentTraffic.putExtra("typeAlert", 2);
                startActivity(intentTraffic);
                break;
        }
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {

    }

    @Override
    public void detectRoadSegment(double latitude, double longitude, RoadSegment roadSegment) {

    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {

    }

    @Override
    public void sendEvent(String event) {
        Toast.makeText(this, event, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateEntityAlert(Response response) {
        if(response.getHttpCode() == 201 || response.getHttpCode() == 200){
            Toast.makeText(MAIN_CONTEXT, R.string.message_successful_sending, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MAIN_CONTEXT, R.string.message_failed_send, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateEntityAlert(Response response) {

    }

    @Override
    public void onGetEntitiesAlert(Response response) {

    }

    @Override
    public void getAllRoadSegment(mx.edu.cenidet.cenidetsdk.httpmethods.Response response) {
        //Log.i("ALLROADSegment: ", "--------------------------------------------------------\n"+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
                RoadSegment roadSegment;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    roadSegment = new RoadSegment();
                    JSONObject object = null;
                    try {
                        object = jsonArray.getJSONObject(i);
                        roadSegment.setIdRoadSegment(object.getString("idRoadSegment"));
                        roadSegment.setType(object.getString("type"));
                        roadSegment.setName(object.getString("name"));
                        roadSegment.setRefRoad(object.getString("refRoad"));
                        roadSegment.setLocation(object.getString("location"));
                        roadSegment.setStartPoint(object.getString("startPoint"));
                        roadSegment.setEndPoint(object.getString("endPoint"));
                        roadSegment.setLaneUsage(object.getString("laneUsage"));
                        roadSegment.setTotalLaneNumber(object.getInt("totalLaneNumber"));
                        roadSegment.setMaximumAllowedSpeed(object.getInt("maximumAllowedSpeed"));
                        roadSegment.setMinimumAllowedSpeed(object.getInt("minimumAllowedSpeed"));
                        roadSegment.setWidth(object.getInt("width"));
                        roadSegment.setStatus(object.getString("status"));
                        sqLiteDrivingApp.createRoadSegment(roadSegment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void getRoadSegmentByRefRoad(mx.edu.cenidet.cenidetsdk.httpmethods.Response response) {
        Log.i(": ", "getRoadSegmentByRefRoad: --------------------------------------------------------\n"+response.getBodyString());
    }

    @Override
    public void getAllRoad(mx.edu.cenidet.cenidetsdk.httpmethods.Response response) {
        //Log.i("ALLROAD: ", "--------------------------------------------------------\n"+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
                Road road;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    road = new Road();
                    JSONObject object;
                    try {
                        object = jsonArray.getJSONObject(i);
                        road.setIdRoad(object.getString("idRoad"));
                        road.setType(object.getString("type"));
                        road.setName(object.getString("name"));
                        road.setDescription(object.getString("description"));
                        road.setResponsible(object.getString("responsible"));
                        road.setStatus(object.getString("status"));
                        sqLiteDrivingApp.createRoad(road);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    @Override
    public void getRoadByResponsible(mx.edu.cenidet.cenidetsdk.httpmethods.Response response) {
        //Log.i(": ", "getRoadByResponsible: --------------------------------------------------------\n"+response.getBodyString());
    }

    @Override
    public void getAllOffStreetParking(mx.edu.cenidet.cenidetsdk.httpmethods.Response response) {
        //Log.i("AllOffStreetParking: ", "--------------------------------------------------------\n"+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
                OffStreetParking offStreetParking;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    offStreetParking = new OffStreetParking();
                    JSONObject object;
                    try {
                        object = jsonArray.getJSONObject(i);
                        offStreetParking.setIdOffStreetParking(object.getString("idOffStreetParking"));
                        offStreetParking.setType(object.getString("type"));
                        offStreetParking.setName(object.getString("name"));
                        offStreetParking.setCategory(object.getString("category"));
                        offStreetParking.setLocation(object.getString("location"));
                        offStreetParking.setDescription(object.getString("description"));
                        offStreetParking.setAreaServed(object.getString("areaServed"));
                        offStreetParking.setStatus(object.getString("status"));
                        sqLiteDrivingApp.createParking(offStreetParking);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    @Override
    public void getOffStreetParkingByAreaServed(mx.edu.cenidet.cenidetsdk.httpmethods.Response response) {

    }


    //.setCancelable(false)

}
