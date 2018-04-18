package mx.edu.cenidet.cenidetsdk.httpmethods;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cipriano on 3/16/2018.
 */

public class Response {
    private String bodyString;
    private int httpCode;
    private Object bodyObject;
    private String xSubjectToken;

    public String getBodyString() {
        return bodyString;
    }

    public void setBodyString(String bodyString) {
        this.bodyString = bodyString;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public Object getBodyObject() {
        return bodyObject;
    }

    public void setBodyObject(Object bodyObject) {
        this.bodyObject = bodyObject;
    }

    public String getxSubjectToken() {
        return xSubjectToken;
    }

    public void setxSubjectToken(String xSubjectToken) {
        this.xSubjectToken = xSubjectToken;
    }

    /**
     * Convierte un json String a un JSONArray
     * @param json en tipo String
     * @return json en un JSONArray
     */
    public JSONArray parseJsonArray(String json){
        JSONArray mJsonArray = null;
        if(!json.isEmpty()) {
            try {
                mJsonArray = new JSONArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mJsonArray;
    }
    public JSONObject parseJsonObject(String json){
        JSONObject jsonObject = null;
        if(!json.isEmpty()) {
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public String parseObjectToJsonString(Object arg){
        Gson gson = new Gson();
        Log.i("JSON_gson: ", gson.toJson(arg));
        return gson.toJson(arg);
    }
    public Object parseToObject(Class<?> mClass, String jsonObjectString){
        Gson gson = new Gson();
        return gson.fromJson(jsonObjectString, mClass);
    }

    /**
     * @param arg es el objeto para ser convertido a un json y ser enviado al servidor.
     * @return retorna el objeto json en un String.
     */
    public String checkForNewsAttributes(Object arg){
        Gson gson = new Gson();
        Log.i("JSON_gson: ", gson.toJson(arg));
        return gson.toJson(arg);
    }
}
