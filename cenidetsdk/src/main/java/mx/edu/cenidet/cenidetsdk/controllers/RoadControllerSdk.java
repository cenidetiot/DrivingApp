package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/20/2018.
 */

public class RoadControllerSdk implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback {
    private static String URL_BASE_HOST = ConfigServer.http_host.getPropiedad();
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;
    private Context context;
    private RoadServiceMethods roadServiceMethods;

    public RoadControllerSdk(Context context, RoadServiceMethods roadServiceMethods){
        this.context = context;
        this.roadServiceMethods = roadServiceMethods;

    }
    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "getAllRoad":
                roadServiceMethods.getAllRoad(response);
                break;
            case "getRoadByResponsible":
                roadServiceMethods.getRoadByResponsible(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {

    }

    public interface RoadServiceMethods{
        void getAllRoad(Response response);
        void getRoadByResponsible(Response response);
    }

    public void getAllRoad(){
        method = "getAllRoad";
        String URL = URL_BASE_HOST +ConfigServer.http_api.getPropiedad()+"/"+ConfigServer.http_road.getPropiedad()+"?status=1";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }

    public void getRoadByResponsible(String responsible){
        method = "getRoadByResponsible";
        String URL = URL_BASE_HOST +ConfigServer.http_api.getPropiedad()+"/"+ConfigServer.http_road.getPropiedad()+"?responsible="+responsible+"&status=1";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
