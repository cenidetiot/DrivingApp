package www.fiware.org.ngsi.httpmethodstransaction.methods;

import android.os.AsyncTask;
import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import www.fiware.org.ngsi.httpmethodstransaction.Response;

/**
 * Created by Cipriano on 10/11/2017.
 * Clase para enviar la información al servidor (POST, GET, DELETE, etc.)
 * La AsyncTask requiere tres tipos parametrizados:
 * El primero indica de qué tipo será la lista de objetos que le llegue al método doInBackground().
 * El segundo al tipo de los que se pasarán a onProgressUpdate.
 * El último se corresponde con el resultado final, y por tanto será el valor que retorne doInBackground y que reciba onPostExecute.
 * No todos los tipos son utilizados siempre por una tarea asíncrona. Para marcar un tipo como no utilizado, simplemente use el tipo Void:
 */

public class HttpGetAsync extends AsyncTask<String, String, Response> {
    String host = System.getProperty("http.host");
    String version = System.getProperty("http.apiversion");
    String connectionTimeout = System.getProperty("http.connectiontimeout");
    String readTimeout = System.getProperty("http.readtimeout");
    String uri = System.getProperty("http.entities");
    ExecRequestGet execRequest;
    Response response;

    public HttpGetAsync(ExecRequestGet execRequest) {
        this.execRequest = execRequest;
    }

    public interface ExecRequestGet{
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
        URL url = null;
        response = new Response();
        //Log.i("URL Post:", ""+host + "/" + version + queryString[0]);
        try {
            if(queryString[0].equals("empty")){
                url = new URL(host + "/" + version + "/" + uri);
            }else{
                url = new URL(host + "/" + version + "/" + uri + "?" + queryString[0]);
            }
            Log.i("URL GET:", ""+url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-length", "0");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(Integer.parseInt(connectionTimeout));
            conn.setReadTimeout(Integer.parseInt(readTimeout));

            //open
            conn.connect();
            //BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();

            response.setBodyString(jsonString);
            response.setHttpCode(conn.getResponseCode());
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


