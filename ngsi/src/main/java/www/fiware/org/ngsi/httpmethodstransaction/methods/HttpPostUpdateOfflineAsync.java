package www.fiware.org.ngsi.httpmethodstransaction.methods;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import www.fiware.org.ngsi.httpmethodstransaction.Response;

/**
 * Created by Cipriano on 10/24/2017.
 */

public class HttpPostUpdateOfflineAsync  extends AsyncTask<String, String, Response> {

    String host = System.getProperty("http.host");
    String version = System.getProperty("http.apiversion");
    String attrs = System.getProperty("http.attrs");
    String connectionTimeout = System.getProperty("http.connectiontimeout");
    String readTimeout = System.getProperty("http.readtimeout");
    String uri = System.getProperty("http.entities");

    ExecRequestPostUpdateOffline execRequest;
    Response response = null;


    public HttpPostUpdateOfflineAsync(ExecRequestPostUpdateOffline execRequest) {
        this.execRequest = execRequest;
    }

    public interface ExecRequestPostUpdateOffline {
        void executeRequest(Response response);
    }
    /**
     * Ejecuta la tarea en un hilo diferente al de ejecución con el fin de no bloquear la interfaz.
     * Es el único método que es necesario sobrescribir, y muchas veces será el único que utilizaremos.
     * @param queryString son parametros que se pasa al metodo execute() y el doInBackground() lo recibe en un arreglo.
     * @return la respuesta de la petición al servidor en un response ya sea el codigo o el mismo json.
     */
    @Override
    protected Response doInBackground(String... queryString) {
        HttpURLConnection conn = null;
        response = new Response();
        String id, keyword, json;
        try {
            id = queryString[0];
            keyword = queryString[1];
            json = queryString[2];
            URL url = new URL(host + "/" + version + "/" + uri + "/"+ keyword+"/"+attrs);
            Log.i("URL", ""+url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Integer.parseInt(readTimeout));
            conn.setConnectTimeout(Integer.parseInt(connectionTimeout));
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json");
            //conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //open
            conn.connect();
            response.setBodyString(json);
            DataOutputStream localDataOutputStream = new DataOutputStream(conn.getOutputStream());
            localDataOutputStream.writeBytes(response.getBodyString());
            localDataOutputStream.flush();
            localDataOutputStream.close();

            response.setHttpCode(conn.getResponseCode());
            if(response.getHttpCode()==204 || response.getHttpCode()==200){
                response.setIdDB(id);
                response.setKeyword(keyword);
                Log.i("Status json...", " " + id + " -- " + keyword + " -- " + json);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn!=null)
                conn.disconnect();
        }
        return response;
    }
    /**
     * Se llama cuando finaliza el método doInBackground() y recibe el resultado para tratarlo
     *y actualizar la interfaz de usuario en consecuencia.
     * @param response recibe la respuesta que retorna el doInBackground().
     */
    @Override
    public void onPostExecute(Response response) {
        this.execRequest.executeRequest(response);
    }
}
