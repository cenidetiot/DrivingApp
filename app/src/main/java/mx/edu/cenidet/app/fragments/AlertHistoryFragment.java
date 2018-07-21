package mx.edu.cenidet.app.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.app.activities.AlertHistoryActivity;
import mx.edu.cenidet.app.activities.AlertMapDetailActivity;
import mx.edu.cenidet.app.utils.Config;
import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.adapters.MyAdapterAlerts;
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
    private DataListener callback;

    private IntentFilter filter;

    public AlertHistoryFragment() {
        context =  HomeActivity.MAIN_CONTEXT;
        alertsControllerSdk = new AlertsControllerSdk(context, this);
        applicationPreferences = new ApplicationPreferences();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DataListener) context;
        }catch (Exception e){
            throw new ClassCastException(context.toString()+" Should implement DataListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        filter = new IntentFilter(Config.PUSH_NOTIFICATION);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext())
                .registerReceiver(new ResponseReceiver(), filter);
        rootView = inflater.inflate(R.layout.fragment_alert_history, container, false);
        getAlerts();
        return rootView;
    }

    private void getAlerts (){
        Log.d("ALERT", "loading alerts");
        listAlerts = new ArrayList<Alert>();
        if(applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE) != null){
            zoneId = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
            callback.sendDataZoneId(zoneId);
            if(zoneId.equals("undetectedZone")){
                Toast.makeText(context, R.string.message_undetected_zone, Toast.LENGTH_SHORT).show();
            }else {
                String typeUser = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_USER_TYPE);
                String tempQuery = zoneId;
                if (typeUser != null && typeUser !="" && typeUser.equals("mobileUser")){
                    tempQuery += "?id=Alert:Device_Smartphone_.*&location=false";
                }
                alertsControllerSdk.historyAlertByZone(tempQuery);
            }
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ALERT", "Alert in alertFragment");
            Toast.makeText(getActivity(), "Broadcast received!", Toast.LENGTH_SHORT).show();
            if (intent != null) {
                getAlerts();
                Log.d("ALERT", "Alert in alertFragment");
                String alert = intent.getStringExtra("subcategory");
                if ( alert  != null) {
                    Intent alertIntent = new Intent(context, AlertMapDetailActivity.class);
                    alertIntent.putExtra("subcategory", intent.getStringExtra("subcategory"));
                    alertIntent.putExtra("description", intent.getStringExtra("description"));
                    alertIntent.putExtra("location", intent.getStringExtra("location"));
                    alertIntent.putExtra("severity", intent.getStringExtra("severity"));
                }

            }
        }
    }

    public interface DataListener{
        void sendDataListAlerts(ArrayList<Alert> listAlerts);
        void sendDataAlert(Alert alert);
        void sendDataZoneId(String zoneId);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAlertsHistory = (ListView) rootView.findViewById(R.id.listViewAlertsHistory);
    }

    @Override
    public void currentAlertByZone(Response response) {

    }

    @Override
    public void historyAlertByZone(Response response) {
        Log.i("Test: ", "Code Alerts: "+response.getHttpCode()+" 1-------------------------------------");
        //callback.sendData("Code: "+response.getHttpCode()+" 2-------------------------------------");
        switch (response.getHttpCode()) {
            case 200:
                Log.i("Test: ", "Body----------: " + response.getBodyString());
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
                            callback.sendDataAlert(alert);
                            listAlerts.add(alert);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (listAlerts.size() > 0) {
                            //callback.sendData(listAlerts);
                            myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlertsHistory, listAlerts);
                            listViewAlertsHistory.setAdapter(myAdapterAlerts);
                        }
                    }
                    if(listAlerts.size() > 0){
                        callback.sendDataListAlerts(listAlerts);
                    }
                }
                break;
            case 503:
                Log.i("STATUS", "Cuando la clave de la zona no es correcta...!");
                break;

        }
    }


}
