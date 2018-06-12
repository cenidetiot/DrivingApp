package mx.edu.cenidet.app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.fragments.AlertHistoryFragment;
import mx.edu.cenidet.app.fragments.MapHistoryFragment;
import www.fiware.org.ngsi.datamodel.entity.Alert;

public class AlertHistoryActivity extends AppCompatActivity implements AlertHistoryFragment.DataListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_history);
        setToolbar();
    }

    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_return);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Últimas alertas");
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
