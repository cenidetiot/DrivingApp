package mx.edu.cenidet.cenidetsdk.httpmethods.methods;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/9/2018.
 */

public class MethodPUT extends AsyncTask<String, String, Response> {
    private String connectionTimeout = ConfigServer.http_connectiontimeout.getPropiedad();
    private String readTimeout = ConfigServer.http_readtimeout.getPropiedad();
    private MethodPUT.MethodPUTCallback mPUTCallback;
    public interface MethodPUTCallback {
        void onMethodPUTCallback(Response response);
    }
    public MethodPUT(MethodPUT.MethodPUTCallback mPUTCallback){
        this.mPUTCallback = mPUTCallback;
    }
    @Override
    protected Response doInBackground(String... vars) {
        String sURL = vars[0];
        String mBody = vars[1];
        Log.i("Status Method PUT", sURL);
        Log.i("Status Method PUT", mBody);

        HttpURLConnection hURLConnection = null;
        Response mResponse = new Response();
        try {
            URL mUrl = new URL(sURL);
            hURLConnection = (HttpURLConnection) mUrl.openConnection();
            hURLConnection.setReadTimeout(Integer.parseInt(readTimeout));
            hURLConnection.setConnectTimeout(Integer.parseInt(connectionTimeout));
            hURLConnection.setRequestMethod("PUT");
            hURLConnection.setDoInput(true);
            hURLConnection.setDoOutput(true);
            hURLConnection.setUseCaches(false);

            //make some HTTP header nicety
            hURLConnection.setRequestProperty("Content-Type", "application/json");
            //open
            hURLConnection.connect();
            DataOutputStream dOutputStream = new DataOutputStream(hURLConnection.getOutputStream());
            dOutputStream.writeBytes(mBody);
            dOutputStream.flush();
            mResponse.setHttpCode(hURLConnection.getResponseCode());
            if (hURLConnection.getHeaderField(0) != null){
                mResponse.setxSubjectToken(hURLConnection.getHeaderField(0));
            }
            if (mResponse.getHttpCode()==201 || mResponse.getHttpCode()==200) {
                try {
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(hURLConnection.getInputStream()));
                    String sLine;
                    StringBuffer sBuffer = new StringBuffer();
                    while ((sLine = bReader.readLine()) != null) {
                        sBuffer.append(sLine);
                    }
                    mResponse.setBodyString(sBuffer.toString());
                    bReader.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            dOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(hURLConnection!=null)
                hURLConnection.disconnect();
        }
        return mResponse;
    }

    @Override
    public void onPostExecute(Response response) {
        this.mPUTCallback.onMethodPUTCallback(response);
    }
}
