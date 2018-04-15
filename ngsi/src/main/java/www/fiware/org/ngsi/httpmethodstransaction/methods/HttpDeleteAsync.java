package www.fiware.org.ngsi.httpmethodstransaction.methods;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import www.fiware.org.ngsi.httpmethodstransaction.Response;


/**
 * Created by Cipriano on 9/6/2017.
 */

public class HttpDeleteAsync extends AsyncTask<String, String, Response> {
    String host = System.getProperty("http.host");
    String version = System.getProperty("http.apiversion");
    String connectionTimeout = System.getProperty("http.connectiontimeout");
    String readTimeout = System.getProperty("http.readtimeout");
    String uri = System.getProperty("http.entities");

    ExecRequestDelete execRequest;
    Response response = null;

    public HttpDeleteAsync(ExecRequestDelete execRequest){
        this.execRequest = execRequest;
    }
    public interface ExecRequestDelete {
        void executeRequest(Response response);
    }

    @Override
    protected Response doInBackground(String... queryString) {
        response = new Response();
       /* HttpURLConnection conn = null;
        URL url = null;
        response = new Response();

        try {
            url = new URL("http://207.249.127.149:1026/v2/entities/MotoG5Plus");
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        try {

            Log.i("URL Delete: ", ""+url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("DELETE");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
            conn.connect();
            //conn.getInputStream();
            response.setHttpCode(conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn!=null)
                conn.disconnect();
        }*/
        URL url = null;
        try {
            url = new URL("http://207.249.127.149:1026/v2/entities/MotoG5Plus");
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod("DELETE");
            response.setHttpCode(httpURLConnection.getResponseCode());
            System.out.println(httpURLConnection.getResponseCode());
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return response;
    }


    @Override
    public void onPostExecute(Response response) {
        this.execRequest.executeRequest(response);
    }
}
