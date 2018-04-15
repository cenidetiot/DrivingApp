package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPUT;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/9/2018.
 */

public class DeviceTokenControllerSdk implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback, MethodPUT.MethodPUTCallback {
    private static String URL_BASE_HOST = ConfigServer.http_host.getPropiedad();
    private DeviceTokenServiceMethods deviceTokenServiceMethods;
    private Context context;
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;
    private MethodPUT mPUT;

    public DeviceTokenControllerSdk (Context context, DeviceTokenServiceMethods deviceTokenServiceMethods){
        this.context = context;
        this.deviceTokenServiceMethods = deviceTokenServiceMethods;
    }
    @Override
    public void onMethodGETCallback(Response response) {

    }

    @Override
    public void onMethodPOSTCallback(Response response) {
        switch (method){
            case "createDeviceToken":
                deviceTokenServiceMethods.createDeviceToken(response);
                break;
        }
    }

    @Override
    public void onMethodPUTCallback(Response response) {
        switch (method){
            case "updateDeviceToken":
                deviceTokenServiceMethods.updateDeviceToken(response);
                break;
        }
    }

    public interface DeviceTokenServiceMethods{
        void createDeviceToken(Response response);
        void readDeviceToken(Response response);
        void updateDeviceToken(Response response);
    }

    public void createDeviceToken(String fcmToken, String refDevice){
        method = "createDeviceToken";
        String URL = URL_BASE_HOST + ConfigServer.http_api.getPropiedad() +"/"+ ConfigServer.http_device.getPropiedad() +"/"+ ConfigServer.http_token.getPropiedad();
        Response response = new Response();
        String json = "{\n" +
                "\t\"fcmToken\":\""+fcmToken+"\",\n" +
                "\t\"refDevice\":\""+refDevice+"\"\n" +
                "}";
        JSONObject jsonDeviceToken = response.parseJsonObject(json);
        Log.i("Status", "JSON jsonDeviceToken: "+jsonDeviceToken);
        mPOST = new MethodPOST(this);
        mPOST.execute(URL, jsonDeviceToken.toString());
    }

    public void updateDeviceToken(String fcmToken, String refDevice){
        method = "updateDeviceToken";
        String URL = URL_BASE_HOST + ConfigServer.http_api.getPropiedad() +"/"+ ConfigServer.http_device.getPropiedad() +"/"+ ConfigServer.http_token.getPropiedad()+"/"+refDevice;
        Response response = new Response();
        String json = "{\n" +
                "\t\"fcmToken\":\""+fcmToken+"\"\n" +
                "}";
        JSONObject jsonDeviceToken = response.parseJsonObject(json);
        Log.i("Status", "JSON jsonDeviceToken: "+jsonDeviceToken);
        mPUT = new MethodPUT(this);
        mPUT.execute(URL, jsonDeviceToken.toString());

    }
}
