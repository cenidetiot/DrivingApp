package mx.edu.cenidet.drivingapp.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.adapters.MyAdapterAlerts;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertHistoryFragment extends Fragment implements AlertsControllerSdk.AlertsServiceMethods{
    View rootView;
    private Context context;
    private ListView listViewAlertsHistory;
    private AlertsControllerSdk alertsControllerSdk;
    private ArrayList<Alert> listAlerts;
    private MyAdapterAlerts myAdapterAlerts;
    private AdapterView.AdapterContextMenuInfo info;
    private String category, description, location, severity;
    private ApplicationPreferences applicationPreferences;
    private String zoneId;
    public AlertHistoryFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        alertsControllerSdk = new AlertsControllerSdk(context, this);
        applicationPreferences = new ApplicationPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alert_history, container, false);
        if(applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE) != null){
            zoneId = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
            if(zoneId.equals("undetectedZone")){
                Toast.makeText(context, R.string.message_undetected_zone, Toast.LENGTH_SHORT).show();
            }else {
                alertsControllerSdk.historyAlertByZone(zoneId);
            }
        }
        //zoneId = "Zone_1523325691338";
        //alertsControllerSdk.historyAlertByZone(zoneId);
        listAlerts = new ArrayList<Alert>();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAlertsHistory = (ListView) rootView.findViewById(R.id.listViewAlertsHistory);
        //registerForContextMenu(listViewAlertsHistory);
    }

    @Override
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
                            myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlertsHistory, listAlerts);
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
}
