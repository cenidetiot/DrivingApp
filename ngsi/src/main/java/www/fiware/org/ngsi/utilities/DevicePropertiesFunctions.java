package www.fiware.org.ngsi.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Cipriano on 10/25/2017.
 */

public class DevicePropertiesFunctions {
    public DevicePropertiesFunctions(){}

    /**
     * @param context contexto donde se ejecutara el metodo.
     * @return el identificador unico del dispositivo movil.
     */
    public String getDeviceId(Context context){
        return "Device_Smartphone_"+getAndroidId(context);
    }

    public String getDeviceModelId(){
        Functions functions = new Functions();
        return "DeviceModel_"+functions.getReplaceParent(getBrand())+"_"+functions.getReplaceParent(getModel());
    }

    /**
     * @param context contexto donde se ejecutara el metodo.
     * @return el identificador unico de la alerta.
     */
    public String getAlertId(Context context){
        Date currentDate = new Date();
        Long date = currentDate.getTime() / 1000;
        return "Alert:Device_Smartphone_"+new DevicePropertiesFunctions().getAndroidId(context)+":"+date;
    }

    /**
     * @return la versión de la API en la que fue desarrollada la aplicación.
     */
    public int getApiVersion() {
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion;
    }

    /**
     * @return la versión del sistema operativo
     */
    public String getOSVersion(){
        String release = Build.VERSION.RELEASE;
        return release;
    }

    /**
     * @return el serial del dispositivo movil.
     */
    public String getSerialNumber(){
        String serial = Build.SERIAL;
        return serial;
    }

    /**
     * @return  el fabricante del dispositivo movil.
     */
    public String getManufacturer(){
        String factory = Build.MANUFACTURER;
        return factory;
    }

    /**
     * @return el modelo del dipositivo.
     */
    public String getModel(){
        String modelo = Build.MODEL;
        return modelo;
    }


    /**
     * @return la marca del dispositivo.
     */
    public String getBrand(){
        String factory = Build.BRAND;
        return factory;
    }


    /**
     * @param context es el contexto de la actividad donde se ejecuta el metodo.
     * @return el nivel de bateria del dispositivo movil.
     */
    public float getBatteryLevel(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        return  batteryPct*100;
    }

    public String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    /**
     * @param interfaceName por ejemplo "wlan0".
     * @return la MACAddress del dispositivo.
     */
    public String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    /**
     * @param useIPv4 es un valor booleano para obtener la dirección IP en este caso es "true"
     * @return la dirección Ip en el protocolo IPv4 del dispositivo movil.
     */
    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    /**
     * @param context es el contexto de la actividad donde se ejecuta el metodo.
     * @return el codigo mnc (Mobile Network Code).
     */
    public String getmnc(Context context){
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator.substring(3);
        }return "";
    }

    /**
     * @param context es el contexto de la actividad donde se ejecuta el metodo.
     * @return el codigo mcc (Mobile Country Code).
     *
     */
    public String getmcc(Context context){
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator.substring(0, 3);
        }
        return "";
    }

    /**
     * @param context contexto donde se ejecutara el metodo
     * @return el identificador unico de android
     */
    public String getAndroidId(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    /**
     * @param context es el contexto de la actividad donde se ejecuta el metodo.
     * @return Si retorna true si esta conectado y false si no lo esta...
     */
    public boolean isNetworkAvailable(Context context){
        boolean band = false;
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) { // connected to the internet
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                band = true;
            }else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                band = true;
            }
        }else{
            band = false;// not connected to the internet
        }
        return band;
    }

    /**
     * @param context es el contexto de la actividad donde se ejecuta el metodo.
     * @return Retorna el tipo de conexion y verifica si se encuentra conectado o no.
     */
    public String isNetworkType(Context context){
        String band = "DISCONNECTED";
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) { // connected to the internet
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                band = "WIFI";
            }else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                band = "MOBILE";
            }
        }else{
            band = "DISCONNECTED";// not connected to the internet
        }
        return band;
    }
}
