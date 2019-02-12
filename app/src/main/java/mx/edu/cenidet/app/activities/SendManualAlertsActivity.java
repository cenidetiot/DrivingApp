package mx.edu.cenidet.app.activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.adapters.MyViewPagerAdapter;
import mx.edu.cenidet.app.services.SendDataService;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;

public class SendManualAlertsActivity extends AppCompatActivity implements SendDataService.SendDataMethods, View.OnClickListener, AlertController.AlertResourceMethods  {
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    //private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    //private PrefManager prefManager;
    private int typeAlert;
    private Context context;
    private int auxPosition;
    private double latitude, longitude;
    private SendDataService sendDataService;
    private AlertController alertController;
    //ImageButton btnReturn;
    // Elementos GUI
    private ImageView imageViewSendAlert;
    private TextView tvTitle;
    private TextView tvSeverity;
    private EditText etDescriptionAlert;
    private Button btnSendAlert;

    private FloatingActionButton btnFloatingInformational;
    private FloatingActionButton btnFloatingLow;
    private FloatingActionButton btnFloatingMedium;
    private FloatingActionButton btnFloatingHigh;
    private FloatingActionButton btnFloatingCritical;


    /**
     * Used to retrieve the typeAlert to change the appearance
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_manual_alerts);
        context = HomeActivity.MAIN_CONTEXT;

        setToolbar();
        sendDataService = new SendDataService(this);
        alertController = new AlertController(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4,
                R.layout.welcome_slide5};

        // adding bottom dots
        //addBottomDots(0);

        // making notification bar transparent
        //changeStatusBarColor();
        elementsGUI();
        if(getIntent().getIntExtra("typeAlert", 0) != 0){
            typeAlert = getIntent().getIntExtra("typeAlert", 0);
            viewTypeAlertGUI(typeAlert);
            Log.i("TYPE_ALERT: ", ""+typeAlert);
        }

        myViewPagerAdapter = new MyViewPagerAdapter(context, layouts);
        viewPager.setAdapter(myViewPagerAdapter);

       // viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    /**
     * Initialize the UI
     */
    private void elementsGUI(){
        imageViewSendAlert = (ImageView) findViewById(R.id.imageViewSendAlert);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvSeverity = (TextView) findViewById(R.id.tvSeverity);
        etDescriptionAlert = (EditText) findViewById(R.id.etDescriptionAlert);

        btnSendAlert = (Button) findViewById(R.id.btnSendAlert);
        btnSendAlert.setOnClickListener(this);
        //btnReturn = (ImageButton) findViewById(R.id.btnReturn);
        //btnReturn.setOnClickListener(this);
        btnFloatingInformational = (FloatingActionButton) findViewById(R.id.btnFloatingInformational);
        btnFloatingInformational.setOnClickListener(this);
        btnFloatingLow = (FloatingActionButton) findViewById(R.id.btnFloatingLow);
        btnFloatingLow.setOnClickListener(this);
        btnFloatingMedium = (FloatingActionButton) findViewById(R.id.btnFloatingMedium);
        btnFloatingMedium.setOnClickListener(this);
        btnFloatingHigh = (FloatingActionButton) findViewById(R.id.btnFloatingHigh);
        btnFloatingHigh.setOnClickListener(this);
        btnFloatingCritical = (FloatingActionButton) findViewById(R.id.btnFloatingCritical);
        btnFloatingCritical.setOnClickListener(this);
    }

    /**
     * Set the respective image and title depending the alert type
     * @param typeAlert
     */
    private  void viewTypeAlertGUI(int typeAlert){
        switch (typeAlert){
            case 1:
                imageViewSendAlert.setImageResource(R.mipmap.ic_accident);
                tvTitle.setText(R.string.message_title_accident_slide);
                tvSeverity.setText(this.getString(R.string.message_severity_slide)+": "+this.getString(R.string.message_severity_informational_slide));
                break;
            case 2:
                imageViewSendAlert.setImageResource(R.mipmap.ic_traffict);
                tvTitle.setText(R.string.message_title_traffic_slide);
                tvSeverity.setText(this.getString(R.string.message_severity_informational_slide)+": "+this.getString(R.string.message_severity_informational_slide));
                break;
        }
    }

    /**
     * Change the text of the severity depending the position
     * @param position
     */
    private void viewPositionSlide(int position){
        auxPosition = position;
        switch (position){
            case 0:
                tvSeverity.setText(this.getString(R.string.message_severity_slide)+": "+this.getString(R.string.message_severity_informational_slide));
                break;
            case 1:
                tvSeverity.setText(this.getString(R.string.message_severity_slide)+": "+this.getString(R.string.message_severity_low_slide));
                break;
            case 2:
                tvSeverity.setText(this.getString(R.string.message_severity_slide)+": "+this.getString(R.string.message_severity_medium_slide));
                break;
            case 3:
                tvSeverity.setText(this.getString(R.string.message_severity_slide)+": "+this.getString(R.string.message_severity_high_slide));
                break;
            case 4:
                tvSeverity.setText(this.getString(R.string.message_severity_slide)+": "+this.getString(R.string.message_severity_critical_slide));
                break;
        }
    }

    /**
     * Assign a severity depending the position
     * @param position
     * @param typeAlert
     */
    private void sendAlert(int position, int typeAlert){
        String severity = "";
        switch (position){
            case 0:
                severity = "informational";
                structureAlert(typeAlert, severity);
                break;
            case 1:
                severity = "low";
                structureAlert(typeAlert, severity);
                break;
            case 2:
                severity = "medium";
                structureAlert(typeAlert, severity);
                break;
            case 3:
                severity = "high";
                structureAlert(typeAlert, severity);
                break;
            case 4:
                severity = "critical";
                structureAlert(typeAlert, severity);
                break;
        }
    }

    /**
     * Used to structure and send the Alert
     * @param typeAlert
     * @param severity
     */
    private void structureAlert(int typeAlert, String severity){
        Alert alert = new Alert();

        alert.setId(new DevicePropertiesFunctions().getAlertId(context));
        alert.getAlertSource().setValue(new DevicePropertiesFunctions().getDeviceId(context));
        alert.getCategory().setValue("traffic");
        alert.getDateObserved().setValue(Functions.getActualDate());
        alert.getDescription().setValue(etDescriptionAlert.getText().toString());
        alert.getLocation().setValue(latitude+", "+longitude);
        alert.getSeverity().setValue(severity);
        alert.getValidFrom().setValue(Functions.getActualDate());
        alert.getValidTo().setValue(Functions.getActualDate());

        if (typeAlert == 1){
            alert.getSubCategory().setValue("carAccident");
          }else{
            alert.getSubCategory().setValue("trafficJam");
        }

        try {
            alertController.createEntity(context, alert.getId(), alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onSupportNavigateUp();

    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAlerts);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.send_alert);
    }

    /**
     * Add the Back pressed event to the back arrow
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }



    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            //auxPosition = position;
            //addBottomDots(position);
            viewPositionSlide(position);
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Add the click event to the buttons
     * @param v
     */
    @Override
    public void onClick(View v) {
        int position;
        switch (v.getId()){
            case R.id.btnSendAlert:
                sendAlert(auxPosition, typeAlert);
                //Toast.makeText(getApplicationContext(), "Position: "+auxPosition, Toast.LENGTH_SHORT).show();
                break;
            /*case R.id.btnReturn:
                startActivity(new Intent(SendManualAlertsActivity.this, HomeActivity.class));
                this.finish();
                break;*/
            case R.id.btnFloatingInformational:
                position = 0;
                //addBottomDots(position);
                viewPositionSlide(position);
                myViewPagerAdapter = new MyViewPagerAdapter(context, layouts, position);
                viewPager.setAdapter(myViewPagerAdapter);
                break;
            case R.id.btnFloatingLow:
                position = 1;
                //addBottomDots(position);
                viewPositionSlide(position);
                myViewPagerAdapter = new MyViewPagerAdapter(context, layouts, position);
                viewPager.setAdapter(myViewPagerAdapter);
                break;
            case R.id.btnFloatingMedium:
                position = 2;
                viewPositionSlide(position);
                //addBottomDots(position);
                myViewPagerAdapter = new MyViewPagerAdapter(context, layouts, position);
                viewPager.setAdapter(myViewPagerAdapter);
                break;
            case R.id.btnFloatingHigh:
                position = 3;
                viewPositionSlide(position);
                //addBottomDots(position);
                myViewPagerAdapter = new MyViewPagerAdapter(context, layouts, position);
                viewPager.setAdapter(myViewPagerAdapter);
                break;
            case R.id.btnFloatingCritical:
                position = 4;
                viewPositionSlide(position);
                //addBottomDots(position);
                myViewPagerAdapter = new MyViewPagerAdapter(context, layouts, position);
                viewPager.setAdapter(myViewPagerAdapter);
                break;
        }
    }


    /**
     * Used to store the location when change
     * @param latitude
     * @param longitude
     * @param speedMS
     * @param speedKmHr
     */
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

    }

    /**
     * Receives the server response
     * @param response
     */
    @Override
    public void onCreateEntityAlert(Response response) {
        Log.i("SEND", "---------------------------------------"+response.getHttpCode());
        if(response.getHttpCode() == 201 || response.getHttpCode() == 200){
            Toast.makeText(context, R.string.message_successful_sending, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, R.string.message_failed_send, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateEntityAlert(Response response) {

    }

    @Override
    public void onGetEntitiesAlert(Response response) {

    }
}
