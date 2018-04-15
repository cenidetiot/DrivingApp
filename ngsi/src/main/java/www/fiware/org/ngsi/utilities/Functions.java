package www.fiware.org.ngsi.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.R.attr.path;
/**
 * Created by Cipriano on 28/02/2017.
 * Clase donde se agregan funciones comunes que se utilizan en una aplicación.
 */
public class Functions {

    public static String getActualDate(){
        Date date = new Date();
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        String strUTCDate = isoFormat.format(date);
        return strUTCDate;
    }
    /**
     * @return retorna la hora y fecha del sistema en un String.
     */
    public static String getDataTime(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return  currentDateTimeString;
    }

    /**
     * @return la hora del sistema en un String.
     */
    public static String getHora(){
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formatteHour = df.format(dt.getTime());
        return formatteHour;
    }

    /**
     * @return la fecha del sistema en un String.
     */
    public static String getFecha(){
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteFecha = df.format(dt.getTime());
        return formatteFecha;
    }

    /**
     * @param string se ingresa la cadena con paratentesis a remplazar por espacios en blanco.
     * @return una cadena sin parentesis.
     */
    public static String getReplaceParent(String string){
        String data = string.replaceAll("\\(|\\)"," ");
        return data.replace(" ", "");
    }
    //FUNCIONES PARA LA GESTIÓN DE ARCHIVOS...

    /**
     * Checks if external storage is available for read and write
     * @return un valor verdadero o falso.
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    /**
     * Checks if external storage is available to at least read
     * @return un valor verdadero o falso.
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean createWriteFile(String fileName, String value, Context context){
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            // Write the string to the file
            osw.write(value);
            // save and close
            osw.flush();
            osw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * @return verdadero si el directorio existe.
     */
    public static boolean directoryExists(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fiware/";
        File dir = new File(path);
        if(dir.exists()){
            return true;
        }else{
           return false;
        }
    }

    /**
     * @param fileName nombre del archivo.
     * @param data dato a escribir en el archivo.
     * @return verdadero si el directorio y el archivo fueron generados con su respectiva información.
     */
    public static boolean saveToFile(String fileName, String data){
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fiware/";
            new File(path).mkdir();
            File file = new File(path+fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        }  catch(FileNotFoundException ex) {
            return false;
            //ex.printStackTrace();
        }  catch(IOException ex) {
            return false;
           // ex.printStackTrace();
           // Log.d(TAG, ex.getMessage());
        }
        return  true;
    }

    public static String readFileConfiguration(String fileName){
        String line = null;
        String path = "";
        try {
            FileInputStream fileInputStream = new FileInputStream (new File(fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                Log.i("JSON_gson:", line);
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d("Error...!", ex.getMessage());
        }
        catch(IOException ex) {
            Log.d("Error...!", ex.getMessage());
        }
        return line;
    }

    public static String readFile(String fileName){
        String line = null;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fiware/";
        try {
            FileInputStream fileInputStream = new FileInputStream (new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                //Log.i("JSON_gson:", line);
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d("Error...!", ex.getMessage());
        }
        catch(IOException ex) {
            Log.d("Error...!", ex.getMessage());
        }
        return line;
    }

    /**
     * @param fileName nombre del archivo.
     * @return un arrayList con los datos leidos desde el archivo.
     */
    public static ArrayList<String> readFileArrayList(String fileName){
        ArrayList<String> listFile = new ArrayList<String>();
        String line = null;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fiware/";
        try {
            FileInputStream fileInputStream = new FileInputStream (new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                //Log.i("JSON_gson:", line);
                listFile.add(line);
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            //line = stringBuilder.toString();
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d("Error...!", ex.getMessage());
        }
        catch(IOException ex) {
            Log.d("Error...!", ex.getMessage());
        }
        return listFile;
    }

    public static String readFile (String fileName, Context context){

        try {
            StringBuffer outStringBuf = new StringBuffer();
            String inputLine = "";
            FileInputStream fIn = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader inBuff = new BufferedReader(isr);
            while ((inputLine = inBuff.readLine()) != null) {
                outStringBuf.append(inputLine);
                outStringBuf.append("\n");
            }
            inBuff.close();
            return outStringBuf.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean deleteFile(String fileName, Context context) {
        context.deleteFile(fileName);
        return true;
    }

    /**
     * @return verdadero si el directorio fue eliminado exitosamente.
     */
    public static boolean deleteDirectory() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fiware";
        File dir = new File(path);
        File[] files = dir.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteDirectory();
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
        return true;
    }

    /**
     * @param arg es el objeto para ser convertido a un json y ser enviado al servidor.
     * @return retorna el objeto json en un String.
     */
    public static String checkForNewsAttributes(Object arg){
        Gson gson = new Gson();
        Log.i("JSON_gson: ", gson.toJson(arg));
        return gson.toJson(arg);
    }

}
