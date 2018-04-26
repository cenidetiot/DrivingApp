package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/20/2018.
 */

public class RoadSegmentControllerSdk implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback {
    private static String URL_BASE_HOST = ConfigServer.http_host.getPropiedad();
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;
    private Context context;
    private RoadSegmentServiceMethods roadSegmentServiceMethods;

    public RoadSegmentControllerSdk(Context context, RoadSegmentServiceMethods roadSegmentServiceMethods){
        this.context = context;
        this.roadSegmentServiceMethods = roadSegmentServiceMethods;
    }

    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "getAllRoadSegment":
                roadSegmentServiceMethods.getAllRoadSegment(response);
                break;
            case "getRoadSegmentByRefRoad":
                roadSegmentServiceMethods.getRoadSegmentByRefRoad(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {

    }

    public interface RoadSegmentServiceMethods{
        void getAllRoadSegment(Response response);
        void getRoadSegmentByRefRoad(Response response);
    }

    public void getAllRoadSegment(){
        method = "getAllRoadSegment";
        String URL = URL_BASE_HOST +ConfigServer.http_api.getPropiedad()+"/"+ConfigServer.http_road_segment.getPropiedad()+"?status=1";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }

    public void getRoadSegmentByRefRoad(String refRoad){
        method = "getRoadSegmentByRefRoad";
        String URL = URL_BASE_HOST +ConfigServer.http_api.getPropiedad()+"/"+ConfigServer.http_road_segment.getPropiedad()+"?refRoad="+refRoad+"&status=1";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }

}
