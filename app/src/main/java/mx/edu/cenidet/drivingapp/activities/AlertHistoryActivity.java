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
import java.util.Map;

import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.adapters.MyAdapterAlerts;
import mx.edu.cenidet.drivingapp.fragments.AlertHistoryFragment;
import mx.edu.cenidet.drivingapp.fragments.MapHistoryFragment;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

public class AlertHistoryActivity extends AppCompatActivity implements AlertHistoryFragment.DataListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_history);
    }

    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_return);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void sendDataListAlerts(ArrayList<Alert> listAlerts) {
        MapHistoryFragment mapHistoryFragment = (MapHistoryFragment) getSupportFragmentManager().findFragmentById(R.id.mapHistoryFragment);
        mapHistoryFragment.renderListAlerts(listAlerts);
    }

    @Override
    public void sendDataAlert(Alert alert) {
        MapHistoryFragment mapHistoryFragment = (MapHistoryFragment) getSupportFragmentManager().findFragmentById(R.id.mapHistoryFragment);
        mapHistoryFragment.renderAlert(alert);
    }

    @Override
    public void sendDataZoneId(String zoneId) {
        MapHistoryFragment mapHistoryFragment = (MapHistoryFragment) getSupportFragmentManager().findFragmentById(R.id.mapHistoryFragment);
        mapHistoryFragment.renderZone(zoneId);
    }

}
