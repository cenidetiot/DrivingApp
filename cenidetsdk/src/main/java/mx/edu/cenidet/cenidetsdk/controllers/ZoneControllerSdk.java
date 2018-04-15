package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/11/2018.
 */

public class ZoneControllerSdk implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback{
    private static String URL_BASE_HOST = ConfigServer.http_host.getPropiedad();
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;
    private Context context;
    private ZoneServiceMethods zoneServiceMethods;

    public ZoneControllerSdk(Context context, ZoneServiceMethods zoneServiceMethods){
        this.context = context;
        this.zoneServiceMethods = zoneServiceMethods;
    }
    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "readAllZone":
                zoneServiceMethods.readAllZone(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {

    }

    public interface ZoneServiceMethods{
        void readAllZone(Response response);
    }

    public void readAllZone(){
        method = "readAllZone";
        String URL = URL_BASE_HOST +ConfigServer.http_api.getPropiedad()+"/"+ConfigServer.http_zone.getPropiedad();
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
