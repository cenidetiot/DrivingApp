package mx.edu.cenidet.drivingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.adapters.MyAdapterAlerts;
import mx.edu.cenidet.drivingapp.fragments.AlertHistoryFragment;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

public class AlertHistoryActivity extends AppCompatActivity {
   /* implements AlertsControllerSdk.AlertsServiceMethods
    private ListView listViewAlertsHistory;
    private ApplicationPreferences applicationPreferences;
    private String zoneId;
    private ArrayList<Alert> listAlerts;
    private Context context;
    private AlertsControllerSdk alertsControllerSdk;
    private MyAdapterAlerts myAdapterAlerts;
    private AdapterView.AdapterContextMenuInfo info;
    private String category, description, location, severity;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_history);
        //setToolbar();
        //Enviar Datos a los Fragment

        /*context = HomeActivity.MAIN_CONTEXT;
        listAlerts = new ArrayList<>();
        applicationPreferences = new ApplicationPreferences();
        alertsControllerSdk = new AlertsControllerSdk(context, this);
        listViewAlertsHistory = (ListView) findViewById(R.id.listViewAlertsHistory);
        registerForContextMenu(listViewAlertsHistory);
        if(applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE) != null){
            zoneId = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
            if(zoneId.equals("undetectedZone")){
                Toast.makeText(context, R.string.message_undetected_zone, Toast.LENGTH_SHORT).show();
            }else {
                alertsControllerSdk.historyAlertByZone(zoneId);
            }
        }*/

    }

    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_return);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*@Override
    public void currentAlertByZone(Response response) {

    }

    @Override
    public void historyAlertByZone(Response response) {
        Log.i("Test: ", "Code Alerts: "+response.getHttpCode());
        switch (response.getHttpCode()) {
            case 200:
                Log.i("Test: ", "Body: " + response.getBodyString());
                Alert alert;
                Log.i("Test: ", "Obtiene Datos...!: " + response.getBodyString());
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                if (jsonArray.length() == 0 || jsonArray == null) {
                    Toast.makeText(context, R.string.message_no_alerts_show, Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            alert = new Alert();
                            JSONObject object = jsonArray.getJSONObject(i);
                            alert.setId(object.getString("id"));
                            alert.setType(object.getString("type"));
                            alert.getAlertSource().setValue(object.getString("alertSource"));
                            alert.getCategory().setValue(object.getString("category"));
                            alert.getDateObserved().setValue(object.getString("dateObserved"));
                            alert.getDescription().setValue(object.getString("description"));
                            alert.getLocation().setValue(object.getString("location"));
                            alert.getSeverity().setValue(object.getString("severity"));
                            alert.getSubCategory().setValue(object.getString("subCategory"));
                            alert.getValidFrom().setValue(object.getString("validFrom"));
                            alert.getValidTo().setValue(object.getString("validTo"));
                            listAlerts.add(alert);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (listAlerts.size() > 0) {
                            myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlerts, listAlerts);
                            listViewAlertsHistory.setAdapter(myAdapterAlerts);
                        }
                    }
                }
                break;
            case 503:
                Log.i("STATUS", "Cuando la clave de la zona no es correcta...!");
                break;

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();

        info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        //menu.setHeaderTitle(listAlerts.get(info.position).getId());
        menu.setHeaderTitle(listAlerts.get(info.position).getCategory().getValue());
        menuInflater.inflate(R.menu.alert_map_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_see_map_alert:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                category = listAlerts.get(info.position).getCategory().getValue();
                description = listAlerts.get(info.position).getDescription().getValue();
                location = listAlerts.get(info.position).getLocation().getValue();
                severity = listAlerts.get(info.position).getSeverity().getValue();
                Intent intent = new Intent(context, AlertMapDetailActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("description", description);
                intent.putExtra("location", location);
                intent.putExtra("severity", severity);
                startActivity(intent);
                return true;
        }
        return super.onContextItemSelected(item);
    }*/
}
