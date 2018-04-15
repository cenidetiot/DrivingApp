package www.fiware.org.ngsi.httpmethodstransaction;

//import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Cipriano on 02/02/2017.
 * Clase donde se obtienen las respuestas de la petición por ejemplo: codigo y el json.
 */
public class Response {
    private String bodyString;
    private int httpCode;
    private String idDB;
    private String keyword;
    private Object bodyObject;

    public String getIdDB() {
        return idDB;
    }

    public void setIdDB(String idDB) {
        this.idDB = idDB;
    }

    public Object getBodyObject() {
        return bodyObject;
    }

    /**
     * Método para obtener el json de la respuesta de la petición.
     * @return el json en un String.
     */
    public String getBodyString() {
        return bodyString;
    }

    /**
     * @param bodyString método que se le pasa un json parseado en un string.
     */
    public void setBodyString(String bodyString) {
        this.bodyString = bodyString;
    }

    /**
     * @return método que obtiene el codigo de respuesta de la petición.
     */
    public int getHttpCode() {
        return httpCode;
    }

    /**
     * @param httpCode método para establecer el codigo de respuesta de la petición.
     */
    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
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

    public void setBodyObject(Object bodyObject) {
        this.bodyObject = bodyObject;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    /*public Object getBodyObject(Class<?> customClass) {
        Gson gson = new Gson();
        this.bodyObject = gson.fromJson(this.getBodyString(), customClass);
        return customClass.cast(this.bodyObject);
    }*/
}
