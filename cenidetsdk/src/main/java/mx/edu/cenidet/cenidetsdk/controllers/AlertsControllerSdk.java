package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/8/2018.
 */

public class AlertsControllerSdk implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback {
    private static String URL_BASE_ALERT = ConfigServer.http_host_alert.getPropiedad();
    private static String URL_BASE_HOST = ConfigServer.http_host.getPropiedad();
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;
    private Context context;
    private AlertsServiceMethods alertsServiceMethods;

    public AlertsControllerSdk(Context context, AlertsControllerSdk.AlertsServiceMethods alertsServiceMethods){
        this.context = context;
        this.alertsServiceMethods = alertsServiceMethods;
    }


    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "currentAlertByZone":
                alertsServiceMethods.currentAlertByZone(response);
                break;
            case "historyAlertByZone":
                alertsServiceMethods.historyAlertByZone(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {

    }

    public interface AlertsServiceMethods{
        void currentAlertByZone(Response response);
        void historyAlertByZone(Response response);
    }

    /**
     * Read the alerts of the campus where the user is DEPRECATED
     */
    public void readAlertsByCampus(){
        method = "readAlertsByCampus";
        String URL = URL_BASE_ALERT + "5a08f54972a5b81a7d040119";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }

    /**
     * Get the current alerts of the zone where the user is from the orion
     * @param zoneId
     */
    public void currentAlertByZone(String zoneId){
        //Zone_1523325691338
        method = "currentAlertByZone";
        String URL = URL_BASE_HOST +ConfigServer.http_service.getPropiedad()+ConfigServer.http_current.getPropiedad()+"/"+zoneId;
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }

    /**
     * Get the history alerts of the zone where the user is from the orion
     * @param zoneId
     */
    public void historyAlertByZone(String zoneId){
        method = "historyAlertByZone";
        String URL = URL_BASE_HOST +ConfigServer.http_service.getPropiedad()+ConfigServer.http_history.getPropiedad()+"/"+zoneId;
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
