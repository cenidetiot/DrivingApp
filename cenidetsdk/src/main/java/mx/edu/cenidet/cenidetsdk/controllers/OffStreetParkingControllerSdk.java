package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/25/2018.
 */

public class OffStreetParkingControllerSdk implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback {
    private static String URL_BASE_HOST = ConfigServer.http_host.getPropiedad();
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;
    private Context context;
    private OffStreetParkingServiceMethods offStreetParkingServiceMethods;

    public OffStreetParkingControllerSdk(Context context, OffStreetParkingServiceMethods offStreetParkingServiceMethods){
        this.context = context;
        this.offStreetParkingServiceMethods = offStreetParkingServiceMethods;
    }

    /**
     * Runs when the GET method is used
     * @param response
     */
    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "getAllOffStreetParking":
                offStreetParkingServiceMethods.getAllOffStreetParking(response);
                break;
            case "getOffStreetParkingByAreaServed":
                offStreetParkingServiceMethods.getOffStreetParkingByAreaServed(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {

    }

    public interface OffStreetParkingServiceMethods{
        void getAllOffStreetParking(Response response);
        void getOffStreetParkingByAreaServed(Response response);
    }

    /**
     * Retrieve all the parking registered from the server
     */
    public void getAllOffStreetParking(){
        method = "getAllOffStreetParking";
        String URL = URL_BASE_HOST +ConfigServer.http_api.getPropiedad()+"/"+ConfigServer.http_parking.getPropiedad()+"?status=1";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }

    /**
     * Retrieve the parking on a specific zone DEPRECATED
     * @param areaServed
     */
    public void getOffStreetParkingByAreaServed(String areaServed){
        method = "getOffStreetParkingByAreaServed";
        String URL = URL_BASE_HOST +ConfigServer.http_api.getPropiedad()+"/"+ConfigServer.http_parking.getPropiedad()+"?areaServed="+areaServed+"&status=1";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
