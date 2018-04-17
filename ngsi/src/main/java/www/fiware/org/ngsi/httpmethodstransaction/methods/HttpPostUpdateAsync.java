package www.fiware.org.ngsi.httpmethodstransaction.methods;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import www.fiware.org.ngsi.httpmethodstransaction.Response;


/**
 * Created by Cipriano on 26/04/2017.
 * Clase para enviar la información al servidor (POST, GET, DELETE, etc.)
 * La AsyncTask requiere tres tipos parametrizados:
 * El primero indica de qué tipo será la lista de objetos que le llegue al método doInBackground().
 * El segundo al tipo de los que se pasarán a onProgressUpdate.
 * El último se corresponde con el resultado final, y por tanto será el valor que retorne doInBackground y que reciba onPostExecute.
 * No todos los tipos son utilizados siempre por una tarea asíncrona. Para marcar un tipo como no utilizado, simplemente use el tipo Void:
 */
public class HttpPostUpdateAsync extends AsyncTask<String, String, Response> {
    String host = System.getProperty("http.host");
    String version = System.getProperty("http.apiversion");
    String attrs = System.getProperty("http.attrs");
    String connectionTimeout = System.getProperty("http.connectiontimeout");
    String readTimeout = System.getProperty("http.readtimeout");
    String uri = System.getProperty("http.entities");

    ExecRequestPostUpdate execRequest;
    Response response = null;


    public HttpPostUpdateAsync(ExecRequestPostUpdate execRequest) {
        this.execRequest = execRequest;
    }

    public interface ExecRequestPostUpdate {
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
        try {
            URL url = new URL(host + "/" + version + "/" + uri + "/"+ queryString[1]+"/"+attrs);
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
            response.setBodyString(queryString[0]);
            /*DataOutputStream localDataOutputStream = new DataOutputStream(conn.getOutputStream());
            localDataOutputStream.writeBytes(response.getBodyString());
            localDataOutputStream.flush();
            localDataOutputStream.close();*/
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            Writer writer = new BufferedWriter(outputStreamWriter);
            writer.write(response.getBodyString());
            outputStreamWriter.flush();
            writer.close();
            outputStreamWriter.close();

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
