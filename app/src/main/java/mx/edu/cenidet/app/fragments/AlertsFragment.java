package mx.edu.cenidet.app.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import mx.edu.cenidet.app.activities.DrivingView;
import mx.edu.cenidet.app.utils.Config;
import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.AlertMapDetailActivity;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.adapters.MyAdapterAlerts;
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
    private String subcategory, description, location, severity;
    private ApplicationPreferences applicationPreferences;
    private FloatingActionButton speedButtonAlerts;
    private String zoneId;
    private IntentFilter filter;
    private ResponseReceiver receiver;
    private boolean _hasLoadedOnce = false;
    private View header;

    /**
     * Constructor used to initialize the Alerts controller and the application preferences
     */
    public AlertsFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        alertsControllerSdk = new AlertsControllerSdk(context, this);
        applicationPreferences = new ApplicationPreferences();

    }

    /**
     * Used to initialize the UI and the registry the Broadcast receiver
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        speedButtonAlerts = (FloatingActionButton) rootView.findViewById(R.id.speedButtonAlerts);
        speedButtonAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drivingView = new Intent(getContext(), DrivingView.class);
                startActivity(drivingView);
            }
        });
        filter = new IntentFilter(Config.PUSH_NOTIFICATION);
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        listAlerts = new ArrayList<Alert>();
        myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlerts, listAlerts);
        header  = getLayoutInflater().inflate(R.layout.empty_alerts_list, listViewAlerts, false);

        return rootView;
    }

    /**
     * Used to reload the alerts when this fragments is visible to the user
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
           getFirstAlerts();
        }
    }

    /**
     * Used to unregister the Broadcast receiver
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
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

    /**
     * The response receiver receive new alert data from the Firebase Messaging service
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String alert = intent.getStringExtra("subcategory");
                if ( alert  != null) {
                    getFirstAlerts();
                }
            }
        }
    }

    /**
     * Used to initialize the UI and registry the ListViewAlerts as a context menu
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAlerts = (ListView) rootView.findViewById(R.id.listViewAlerts);
        registerForContextMenu(listViewAlerts);
    }

    /**
     * Receive the server response when get the current alerts
     * @param response
     */
    @Override
    public void currentAlertByZone(Response response) {

        listAlerts.clear();
        listViewAlerts.removeHeaderView(header);
        switch (response.getHttpCode()){
            case 0:
                Log.i("STATUS", "Internal Server Error 1...!");
                break;
            case 200:
                Alert alert;
                    JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                    if(jsonArray.length() == 0 || jsonArray == null){
                        Toast.makeText(context, R.string.message_no_alerts_show, Toast.LENGTH_SHORT).show();
                        listViewAlerts.addHeaderView(header);
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

                        }
                    }

                myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlerts, listAlerts);
                myAdapterAlerts.notifyDataSetChanged();
                listViewAlerts.setAdapter(myAdapterAlerts);
                break;
            case 500:
                Log.i("STATUS", "Internal Server Error 2...!");
                break;
            case 503:
                Log.i("STATUS", "Cuando la clave de la zona no es correcta...!");
                break;
        }
    }

    @Override
    public void historyAlertByZone(Response response) {

    }

    /**
     * Runs when a new Context menu is registered
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();

        info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(listAlerts.get(info.position).getCategory().getValue());
        menuInflater.inflate(R.menu.alert_map_menu, menu);
    }

    /**
     * Get the alert data and put to AlertMapDetailActivity
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_see_map_alert:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                subcategory = listAlerts.get(info.position).getSubCategory().getValue();
                description = listAlerts.get(info.position).getDescription().getValue();
                location = listAlerts.get(info.position).getLocation().getValue();
                severity = listAlerts.get(info.position).getSeverity().getValue();
                Intent intent = new Intent(context, AlertMapDetailActivity.class);

                intent.putExtra("subcategory", subcategory);
                intent.putExtra("description", description);
                intent.putExtra("location", location);
                intent.putExtra("severity", severity);
                startActivity(intent);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
