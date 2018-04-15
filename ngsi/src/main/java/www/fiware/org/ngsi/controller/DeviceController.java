package www.fiware.org.ngsi.controller;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import www.fiware.org.ngsi.db.sqlite.SQLiteHelper;
import www.fiware.org.ngsi.db.sqlite.entity.Tbl_Data_Temp;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpDeleteAsync;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpGetAsync;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpPostCreateAsync;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpPostCreateOfflineAsync;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpPostUpdateAsync;
import www.fiware.org.ngsi.httpmethodstransaction.methods.HttpPostUpdateOfflineAsync;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;
import www.fiware.org.ngsi.utilities.ParameterStringBuilder;

import static www.fiware.org.ngsi.utilities.Constants.PREFERENCE_MOBILE_DATA_KEY;
import static www.fiware.org.ngsi.utilities.Constants.PREFERENCE_OFFLINE_MODE_KEY;
import static www.fiware.org.ngsi.utilities.Constants.PREFERENCE_STATUS_MOBILE_DATA;
import static www.fiware.org.ngsi.utilities.Constants.PREFERENCE_STATUS_OFFLINE_MODE;

/**
 * Created by Cipriano on 26/04/2017.
 */

public class DeviceController implements HttpPostCreateAsync.ExecRequestPost, HttpPostUpdateAsync.ExecRequestPostUpdate, HttpDeleteAsync.ExecRequestDelete, HttpGetAsync.ExecRequestGet, HttpPostUpdateOfflineAsync.ExecRequestPostUpdateOffline, HttpPostCreateOfflineAsync.ExecRequestPostOffline{
    DeviceResourceMethods aRMethods;
    private static String type;
    private String parameters = "";

    private HttpPostCreateAsync cPOSTAsync;
    private HttpPostUpdateAsync cPOSTUPDATEAsync;
    private HttpPostUpdateOfflineAsync cPOSTUPDATEOFFLINEAsync;
    private HttpPostCreateOfflineAsync cPOSTCREATEOFFLINEAsync;
    private HttpDeleteAsync cDELETEAsync;
    private HttpGetAsync cGETAsync;

    private ApplicationPreferences appPreferences = new ApplicationPreferences();
    private DevicePropertiesFunctions functions = new DevicePropertiesFunctions();
    private SQLiteHelper sqlHelper;
    private Tbl_Data_Temp tblTemp;

    public DeviceController(DeviceResourceMethods eRMethods) {
        this.aRMethods = eRMethods;
    }

    /**
     * @param response es la respuesta que se obtiene de la petición al servidor
     */
    @Override
    public void executeRequest(Response response) {
        switch (type){
            case "createEntity":
                aRMethods.onCreateEntity(response);
                break;
            case "createEntitySaveOffline":
                aRMethods.onCreateEntitySaveOffline(response);
                break;
            case "updateEntity":
                aRMethods.onUpdateEntity(response);
                break;
            case "updateEntitySaveOffline":
                aRMethods.onUpdateEntitySaveOffline(response);
                break;
            case "deleteEntity":
                aRMethods.onDeleteEntity(response);
                break;
            case "getEntities":
                aRMethods.onGetEntities(response);
                break;
        }
    }

    /**
     * Interface donde se declaran cada uno de los metodos que realizan la petición al servidor,
     * donde cada uno de los metodos obtine la respuesta de la petició.
     */
    public interface DeviceResourceMethods {
        void onCreateEntity(Response response);
        void onCreateEntitySaveOffline(Response response);
        void onUpdateEntity(Response response);
        void onUpdateEntitySaveOffline(Response response);
        void onDeleteEntity(Response response);
        void onGetEntities(Response response);
    }


    /**
     * @param context contexto donde se ejecutara el metodo.
     * @param id el identificador de la entidad.
     * @param arg el objeto de la entidad.
     * @throws Exception
     */
    public void createEntityDevice(Context context, String id, Object arg) throws Exception {
        type = "createEntityDevice";
        sqlHelper = new SQLiteHelper(context);
        tblTemp = new Tbl_Data_Temp();
        String json = checkForNewsAttributes(arg);
        tblTemp.setKeyword(id);
        tblTemp.setJson(json);
        Tbl_Data_Temp dataValidateExists = sqlHelper.getByKeywordAndStatusActiveTempCreate(tblTemp);
        if(dataValidateExists != null ){
            Log.i("Status deviceModel", "It already exists");
        }else {
            if (appPreferences.getPreferenceBoolean(context, PREFERENCE_OFFLINE_MODE_KEY, PREFERENCE_STATUS_OFFLINE_MODE) == true) {
                if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                    sqlHelper.createTempCreate(tblTemp);
                }
                Log.d("Status", "MODE OFFLINE1!");
            } else {
                if (appPreferences.getPreferenceBoolean(context, PREFERENCE_MOBILE_DATA_KEY, PREFERENCE_STATUS_MOBILE_DATA) == true) {
                    if (functions.isNetworkType(context) == "WIFI" || functions.isNetworkType(context) == "MOBILE") {
                        Log.d("Status", "WIFI or MOBILE...!");
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        cPOSTAsync = new HttpPostCreateAsync(this);
                        cPOSTAsync.execute(json);
                    } else {
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        Log.d("Status", "MODE OFFLINE12");
                    }
                } else {
                    if (functions.isNetworkType(context) == "WIFI") {
                        Log.d("Status", "WIFI!");
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        cPOSTAsync = new HttpPostCreateAsync(this);
                        cPOSTAsync.execute(json);
                    } else {
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        Log.d("Status", "MODE OFFLINE13");
                    }
                }
            }
        }
    }

    /**
     * @param arg es el objeto para ser convertido a un json y ser enviado al servidor.
     * @throws Exception
     */
    public void createEntityDeviceModel(Context context, String id, Object arg) throws Exception {
        type = "createEntityDeviceModel";
        sqlHelper = new SQLiteHelper(context);
        tblTemp = new Tbl_Data_Temp();
        String json = checkForNewsAttributes(arg);
        tblTemp.setKeyword(id);
        tblTemp.setJson(json);
        Tbl_Data_Temp dataValidateExists = sqlHelper.getByKeywordAndStatusActiveTempCreate(tblTemp);
        if(dataValidateExists != null){
            Log.d("Status deviceModel", "It already exists");
        }else {
            if (appPreferences.getPreferenceBoolean(context, PREFERENCE_OFFLINE_MODE_KEY, PREFERENCE_STATUS_OFFLINE_MODE) == true) {
                if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                    sqlHelper.createTempCreate(tblTemp);
                }
                Log.d("Status deviceModel", "MODE OFFLINE1!");
            } else {
                //Se valida que el vio de los datos almacenados en la DB se envien con datos moviles,
                // si valor es true al enviar los datos se utilizan tanto los datos moviles como la red wifi,
                // por default esta en false eso significa que solo envía los datos cuando se conecta a una red wifi.
                if (appPreferences.getPreferenceBoolean(context, PREFERENCE_MOBILE_DATA_KEY, PREFERENCE_STATUS_MOBILE_DATA) == true) {
                    if (functions.isNetworkType(context) == "WIFI" || functions.isNetworkType(context) == "MOBILE") {
                        Log.d("Status deviceModel", "WIFI or MOBILE...!");
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        cPOSTAsync = new HttpPostCreateAsync(this);
                        cPOSTAsync.execute(json);
                    } else {
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        Log.d("Status deviceModel", "MODE OFFLINE12");
                    }
                } else {
                    if (functions.isNetworkType(context) == "WIFI") {
                        Log.d("Status deviceModel", "WIFI!");
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        cPOSTAsync = new HttpPostCreateAsync(this);
                        cPOSTAsync.execute(json);
                    } else {
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        Log.d("Status deviceModel", "MODE OFFLINE13");
                    }
                }

            }
        }
    }

    public void createEntity(Context context, String id, Object arg){
        type = "createEntity";
        sqlHelper = new SQLiteHelper(context);
        tblTemp = new Tbl_Data_Temp();
        String json = checkForNewsAttributes(arg);
        tblTemp.setKeyword(id);
        tblTemp.setJson(json);
        Tbl_Data_Temp dataValidateExists = sqlHelper.getByKeywordAndStatusActiveTempCreate(tblTemp);
        if(dataValidateExists != null ){
            Log.i("Status deviceModel", "It already exists");
        }else {
            if (appPreferences.getPreferenceBoolean(context, PREFERENCE_OFFLINE_MODE_KEY, PREFERENCE_STATUS_OFFLINE_MODE) == true) {
                if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                    sqlHelper.createTempCreate(tblTemp);
                }
                Log.d("Status", "MODE OFFLINE1!");
            } else {
                if (appPreferences.getPreferenceBoolean(context, PREFERENCE_MOBILE_DATA_KEY, PREFERENCE_STATUS_MOBILE_DATA) == false) {
                    if (functions.isNetworkType(context) == "WIFI" || functions.isNetworkType(context) == "MOBILE") {
                        Log.d("Status", "WIFI or MOBILE...!");
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        cPOSTAsync = new HttpPostCreateAsync(this);
                        cPOSTAsync.execute(json);
                    } else {
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        Log.d("Status", "MODE OFFLINE12");
                    }
                } else {
                    if (functions.isNetworkType(context) == "WIFI") {
                        Log.d("Status", "WIFI!");
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        cPOSTAsync = new HttpPostCreateAsync(this);
                        cPOSTAsync.execute(json);
                    } else {
                        if (sqlHelper.getByKeywordTempCreate(tblTemp) == null) {
                            sqlHelper.createTempCreate(tblTemp);
                        }
                        Log.d("Status", "MODE OFFLINE13");
                    }
                }
            }
        }
    }

    /**
     * @param arg es el objeto para ser convertido a un json y ser enviado al servidor.
     * @param id es el identificador de la entidad con este se realizara la actualización.
     * @throws Exception
     */
    public void updateEntity(Context context, String id, Object arg) throws Exception {
        type = "updateEntity";
        sqlHelper = new SQLiteHelper(context);
        tblTemp = new Tbl_Data_Temp();
        String json = checkForNewsAttributes(arg);
        tblTemp.setKeyword(id);
        tblTemp.setJson(json);

        if (appPreferences.getPreferenceBoolean(context, PREFERENCE_OFFLINE_MODE_KEY, PREFERENCE_STATUS_OFFLINE_MODE) == true) {
            sqlHelper.createTempUpdate(tblTemp);
            Log.d("Status deviceModel", "MODE OFFLINE!");
        } else {
            //Se valida que el vio de los datos almacenados en la DB se envien con datos moviles,
            // si valor es true al enviar los datos se utilizan tanto los datos moviles como la red wifi,
            // por default esta en false eso significa que solo envía los datos cuando se conecta a una red wifi.
            if (appPreferences.getPreferenceBoolean(context, PREFERENCE_MOBILE_DATA_KEY, PREFERENCE_STATUS_MOBILE_DATA) == false) {
                if (functions.isNetworkType(context) == "WIFI" || functions.isNetworkType(context) == "MOBILE") {
                    Log.d("Status deviceModel", "WIFI or MOBILE...!");
                    cPOSTUPDATEAsync = new HttpPostUpdateAsync(this);
                    cPOSTUPDATEAsync.execute(json, id);
                } else {
                    sqlHelper.createTempUpdate(tblTemp);
                    Log.d("Status deviceModel", "MODE OFFLINE2");
                }
            } else {
                if (functions.isNetworkType(context) == "WIFI") {
                    Log.d("Status deviceModel", "WIFI!");

                    cPOSTUPDATEAsync = new HttpPostUpdateAsync(this);
                    cPOSTUPDATEAsync.execute(json, id);
                } else {
                    sqlHelper.createTempUpdate(tblTemp);
                    Log.d("Status deviceModel", "MODE OFFLINE3");
                }
            }

        }
    }

    /**
     * Función que genera o crea la entidad almacenada en la db (tbl_data_temp_create) que no han sido enviada al Context Broker
     * @param id identificador con el que se guardo en la base de datos el registro.
     * @param keyword palabra clave con la que se guarda el registro por lo regular el id de la endidad.
     * @param json objeto con el que fue almacenado el registro.
     */
    public void createEntitySaveOffline(String id, String keyword, String json){
        type = "createEntitySaveOffline";
        cPOSTCREATEOFFLINEAsync = new HttpPostCreateOfflineAsync(this);
        cPOSTCREATEOFFLINEAsync.execute(id, keyword, json);
    }

    /**
     * Función que actualiza las entidades almacenadas en la db (tbl_data_temp_update) que no han sido enviadas al Context Broker
     * @param id identificador con el que se guardo en la base de datos el registro.
     * @param keyword palabra clave con la que se guarda el registro es el id de la endidad.
     * @param json objeto con el que fue almacenado el registro.
     */
    public void updateEntitySaveOffline(String id, String keyword, String json){
        type = "updateEntitySaveOffline";
        cPOSTUPDATEOFFLINEAsync = new HttpPostUpdateOfflineAsync(this);
        cPOSTUPDATEOFFLINEAsync.execute(id, keyword, json);
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

        cGETAsync = new HttpGetAsync(this);
        cGETAsync.execute(parameters);
    }

    public void deleteEntityById(String id){
        type = "deleteEntity";
        cDELETEAsync = new HttpDeleteAsync(this);
        cDELETEAsync.execute(id);
    }

    /**
     * @param arg es el objeto para ser convertido a un json y ser enviado al servidor.
     * @return retorna el objeto json en un String.
     */
    public String checkForNewsAttributes(Object arg){
        Gson gson = new Gson();
        //Log.i("JSON_gson: ", gson.toJson(arg));
        return gson.toJson(arg);
    }
}
