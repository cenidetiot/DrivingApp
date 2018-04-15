package mx.edu.cenidet.drivingapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import mx.edu.cenidet.drivingapp.activities.AlertMapDetailActivity;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.adapters.MyAdapterAlerts;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertsFragment extends Fragment implements AlertsControllerSdk.AlertsServiceMethods{
    private View rootView;
    private Context context;
    private ListView listViewAlerts;
    private AlertsControllerSdk alertsControllerSdk;
    private ArrayList<Alert> listAlerts;
    private MyAdapterAlerts myAdapterAlerts;
    private AdapterView.AdapterContextMenuInfo info;
    private String category, description, location, severity;
    private ApplicationPreferences applicationPreferences;
    private String zoneId;

    public AlertsFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        alertsControllerSdk = new AlertsControllerSdk(context, this);
        applicationPreferences = new ApplicationPreferences();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        //alertsControllerSdk.readAlertsByCampus();
        if(applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE) != null){
            zoneId = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
            if(zoneId.equals("undetectedZone")){
                Toast.makeText(context, R.string.message_undetected_zone, Toast.LENGTH_SHORT).show();
            }else {
                alertsControllerSdk.currentAlertByZone(zoneId);
            }
        }
        listAlerts = new ArrayList<Alert>();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAlerts = (ListView) rootView.findViewById(R.id.listViewAlerts);
        registerForContextMenu(listViewAlerts);
    }

    @Override
    public void currentAlertByZone(Response response) {
        Log.i("Test: ", "Code Alerts: "+response.getHttpCode());
        switch (response.getHttpCode()){
            case 200:
                Log.i("Test: ", "Body: "+response.getBodyString());
                Alert alert;
                    Log.i("Test: ", "Obtiene Datos...!: "+response.getBodyString());
                    JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                    if(jsonArray.length() == 0 || jsonArray == null){
                        Toast.makeText(context, R.string.message_no_alerts_show, Toast.LENGTH_SHORT).show();
                    }else{
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
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                            if(listAlerts.size() > 0){
                                myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlerts, listAlerts);
                                listViewAlerts.setAdapter(myAdapterAlerts);
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
    public void historyAlertByZone(Response response) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();

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
    }
}
