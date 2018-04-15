package mx.edu.cenidet.cenidetsdk.httpmethods.methods;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by TesistaSDN on 22/02/2018.
 */

public class MethodGET extends AsyncTask<String, String, Response> {
    private String connectionTimeout = ConfigServer.http_connectiontimeout.getPropiedad();
    private String readTimeout = ConfigServer.http_readtimeout.getPropiedad();
    private MethodGETCallback mGETCallback;
    public interface MethodGETCallback {
        void onMethodGETCallback(Response response);
    }
    public MethodGET(MethodGETCallback mGETCallback){
        this.mGETCallback = mGETCallback;
    }
    @Override
    protected Response doInBackground(String... vars) {
        String sURL = vars[0];
        Log.i("Status Method GET", sURL);
        HttpURLConnection hURLConnection = null;
        Response mResponse = new Response();
        try {
            URL mUrl = new URL(sURL);
            hURLConnection = (HttpURLConnection) mUrl.openConnection();
            hURLConnection.setRequestMethod("GET");

            hURLConnection.setConnectTimeout(Integer.parseInt(connectionTimeout));
            hURLConnection.setReadTimeout(Integer.parseInt(readTimeout));

            hURLConnection.connect();

            BufferedReader bReader = new BufferedReader(new InputStreamReader(hURLConnection.getInputStream()));
            StringBuilder sBuilder = new StringBuilder();

            String sLine;
            while ((sLine = bReader.readLine()) != null) {
                sBuilder.append(sLine + "\n");
            }
            bReader.close();
            mResponse.setHttpCode(hURLConnection.getResponseCode());
            mResponse.setBodyString(sBuilder.toString());

            Log.i("Status Method GET", "Status code: " + mResponse.getHttpCode());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(hURLConnection!=null)
                hURLConnection.disconnect();
        }
        return mResponse;
    }
    public void onPostExecute(Response response) {
        this.mGETCallback.onMethodGETCallback(response);
    }

}
