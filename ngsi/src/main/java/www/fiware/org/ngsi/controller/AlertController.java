package www.fiware.org.ngsi.controller;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpGetAsync;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpPostCreateAsync;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpPostUpdateAsync;
import www.fiware.org.ngsi.utilities.ParameterStringBuilder;

/**
 * Created by Cipriano on 3/23/2018.
 */

public class AlertController implements HttpPostCreateAsync.ExecRequestPost, HttpPostUpdateAsync.ExecRequestPostUpdate, HttpGetAsync.ExecRequestGet{
    private AlertResourceMethods alertResourceMethods;
    private static String type;
    private String parameters = "";

    //Metodos
    private HttpPostCreateAsync httpPostCreateAsync;
    private HttpPostUpdateAsync httpPostUpdateAsync;
    private HttpGetAsync httpGetAsync;

    public AlertController(AlertResourceMethods alertResourceMethods){
        this.alertResourceMethods = alertResourceMethods;
    }
    @Override
    public void executeRequest(Response response) {
        switch (type){
            case "createEntity":
                alertResourceMethods.onCreateEntityAlert(response);
                break;
            case "updateEntity":
                alertResourceMethods.onUpdateEntityAlert(response);
                break;
            case "getEntities":
                alertResourceMethods.onGetEntitiesAlert(response);
                break;
        }
    }

    /**
     * Interface donde se declaran cada uno de los metodos que realizan la petición al servidor,
     * donde cada uno de los metodos obtine la respuesta de la petició.
     */
    public interface AlertResourceMethods {
        void onCreateEntityAlert(Response response);
        void onUpdateEntityAlert(Response response);
        void onGetEntitiesAlert(Response response);
    }

    /**
     * @param context contexto donde se ejecutara el metodo.
     * @param id el identificador de la entidad.
     * @param arg el objeto de la entidad.
     * @throws Exception
     */
    public void createEntity(Context context, String id, Object arg) throws Exception {
        type = "createEntity";
        String json = checkForNewsAttributes(arg);
        httpPostCreateAsync = new HttpPostCreateAsync(this);
        httpPostCreateAsync.execute(json);
    }

    /**
     * @param arg es el objeto para ser convertido a un json y ser enviado al servidor.
     * @param id es el identificador de la entidad con este se realizara la actualización.
     * @throws Exception
     */
    public void updateEntity(Context context, String id, Object arg) throws Exception {
        type = "updateEntity";
        String json = checkForNewsAttributes(arg);
        httpPostUpdateAsync = new HttpPostUpdateAsync(this);
        httpPostUpdateAsync.execute(json, id);
    }


    /**
     * @param params un HashMap con el nombre del atributo y el valor del mismo.
     */
    public void getEntity(Map<String, String> params){
        type = "getEntities";
        if(params != null){
            try {
                parameters = ParameterStringBuilder.getParamsString(params);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            parameters = "empty";
        }

        httpGetAsync = new HttpGetAsync(this);
        httpGetAsync.execute(parameters);
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
