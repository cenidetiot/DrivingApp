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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.app.activities.AlertsActivity;
import mx.edu.cenidet.app.activities.DrivingView;
import mx.edu.cenidet.app.activities.MainActivity;
import mx.edu.cenidet.app.services.SendDataService;
import mx.edu.cenidet.app.utils.Config;
import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.AlertMapDetailActivity;
import mx.edu.cenidet.app.activities.HomeActivity;
import mx.edu.cenidet.app.adapters.MyAdapterAlerts;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertsFragment extends Fragment implements
        AlertsControllerSdk.AlertsServiceMethods,
        SendDataService.SendDataMethods{
    private View rootView;
    private Context context;
    private ListView listViewAlerts;
    private AlertsControllerSdk alertsControllerSdk;
    private ArrayList<Alert> listAlerts;
    private MyAdapterAlerts myAdapterAlerts;
    private AdapterView.AdapterContextMenuInfo info;
    private String subcategory, description, location, severity, dateObserved, idAlert;
    private ApplicationPreferences applicationPreferences;
    private FloatingActionButton speedButtonAlerts;
    private String zoneId;
    private IntentFilter filter;
    private ResponseReceiver receiver;
    private boolean _hasLoadedOnce = false;
    private View header;

    private TextView textTitle;
    private TextView textSubTitle;
    private SendDataService sendDataService;
    private Zone currentZone = null;
    private SQLiteDrivingApp sqLiteDrivingApp;







    public AlertsFragment() {
        context = AlertsActivity.MAIN_CONTEXT;
        alertsControllerSdk = new AlertsControllerSdk(context, this);
        sendDataService = new SendDataService(this);
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        applicationPreferences = new ApplicationPreferences();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alerts, container, false);

        filter = new IntentFilter(Config.PUSH_NOTIFICATION);
        receiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        listAlerts = new ArrayList<Alert>();

        myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlerts, listAlerts);
        header  = getLayoutInflater().inflate(R.layout.empty_alerts_list, listViewAlerts, false);

        //ListView listAlerts = (ListView) rootView.findViewById(R.id.listViewAlerts);
        //listAlerts.set

        textTitle = (TextView) rootView.findViewById(R.id.textTitle);
        textTitle.setText(R.string.menu_alerts);
        textSubTitle = (TextView) rootView.findViewById(R.id.textSubtitle);

        getFirstAlerts();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
           getFirstAlerts();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    public void getFirstAlerts() {
        if(applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE) != null){
            zoneId = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
            if(!zoneId.equals("undetectedZone")) {

                Zone zone  = sqLiteDrivingApp.getZoneById(zoneId);
                textSubTitle.setText(zone.getName().getValue());

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
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {

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
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAlerts = (ListView) rootView.findViewById(R.id.listViewAlerts);
        listViewAlerts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listViewAlerts.getItemAtPosition(position);
                Log.d("LIST", "" + listAlerts.get(position).getId());

                idAlert = listAlerts.get(position).getId();
                subcategory = listAlerts.get(position).getSubCategory().getValue();
                description = listAlerts.get(position).getDescription().getValue();
                location = listAlerts.get(position).getLocation().getValue();
                severity = listAlerts.get(position).getSeverity().getValue();
                dateObserved = listAlerts.get(position).getDateObserved().getValue();

                Intent intent = new Intent(context, AlertMapDetailActivity.class);

                intent.putExtra("id", idAlert);
                intent.putExtra("subcategory", subcategory);
                intent.putExtra("description", description);
                intent.putExtra("location", location);
                intent.putExtra("severity", severity);
                intent.putExtra("dateObserved", dateObserved);


                startActivity(intent);

            }
        });
        registerForContextMenu(listViewAlerts);
    }

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();

        info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(listAlerts.get(info.position).getCategory().getValue());
        menuInflater.inflate(R.menu.alert_map_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_see_map_alert:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                idAlert = listAlerts.get(info.position).getId();
                subcategory = listAlerts.get(info.position).getSubCategory().getValue();
                description = listAlerts.get(info.position).getDescription().getValue();
                location = listAlerts.get(info.position).getLocation().getValue();
                severity = listAlerts.get(info.position).getSeverity().getValue();
                dateObserved = listAlerts.get(info.position).getDateObserved().getValue();
                Intent intent = new Intent(context, AlertMapDetailActivity.class);

                intent.putExtra("id", idAlert);
                intent.putExtra("subcategory", subcategory);
                intent.putExtra("description", description);
                intent.putExtra("location", location);
                intent.putExtra("severity", severity);
                intent.putExtra("dateObserved", dateObserved);

                startActivity(intent);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
