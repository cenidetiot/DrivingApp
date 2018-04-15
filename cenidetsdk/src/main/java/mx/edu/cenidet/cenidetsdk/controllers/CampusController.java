package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 3/18/2018.
 */

public class CampusController implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback {
    private static String URL_BASE_MONGO = ConfigServer.http_host_mongo.getPropiedad();
    private CampusServiceMethods campusServiceMethods;
    private Context context;
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;

    public CampusController(Context context, CampusController.CampusServiceMethods campusServiceMethods){
        this.campusServiceMethods = campusServiceMethods;
        this.context = context;
    }
    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "readCampus":
                campusServiceMethods.readCampus(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {

    }

    public interface CampusServiceMethods{
        void readCampus(Response response);
    }

    public void readCampus(){
        method = "readCampus";
        String URL = URL_BASE_MONGO + ConfigServer.http_campus.getPropiedad();
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
