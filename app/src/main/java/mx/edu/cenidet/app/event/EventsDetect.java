package mx.edu.cenidet.app.event;

import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import com.google.android.gms.maps.model.LatLng;
import android.location.Location;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import mx.edu.cenidet.app.activities.HomeActivity; 

import android.content.Context;

import java.text.SimpleDateFormat;
import android.util.Log;

import org.json.JSONObject;

import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.Functions;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;


public class EventsDetect implements AlertController.AlertResourceMethods {

    private static Context context = null; 
    private static String fileName = "velocidades.csv";

    private static String idDevice ;
    private AlertController alertController;

    /*Variables globales de paradas repentinas*/
    private static double gravity = 9.81; // valor en m/s
    private static double frictionCoefficient = .75; //coeficiente de friccion entre los neumaticos y el piso
    private static double initialVelocity = 0 ,finalVelocity = 0;
    private static long initialDate = 0, finalDate = 0;
    private static boolean isStopping = false, wasStopped = false, suddenAlertSent = false, stopped = false ;
    private static long stoppedSeconds = 0 ;
    private static double speedReached = 0;
    private static long dateSpeedReached = 0;
    private static boolean isSuddenStop = false;

    /*Variables Globales de contra sentido */
    private static double totalDistance = 0;
    private static double startToLastDistance = 0, startToCurrentDistance = 0;
    private static double endToLastDistance = 0, endToCurrentDistance = 0;
    private static LatLng lastPoint;
    private static boolean  wrongWayAlertSent = false, isWrongWay = false;
    private static long wrongWaySeconds = 0;

    /*Variables globales de velocidad*/
    private static int speedigSeconds;
    private static boolean speedingAlertSent = false;


    public  EventsDetect () {
        context = HomeActivity.MAIN_CONTEXT;
        alertController = new AlertController(this);
        //this.idDevice = new DevicePropertiesFunctions().getAlertId(context);

    }

    public Alert sendAlert(String description, String severity, String subCategory, double latitude, double longitude) {

        Alert alert = new Alert();
        alert.setId(new DevicePropertiesFunctions().getAlertId(context));
        alert.getAlertSource().setValue(new DevicePropertiesFunctions().getDeviceId(context));
        alert.getCategory().setValue("traffic");
        alert.getDateObserved().setValue(Functions.getActualDate());
        alert.getDescription().setValue(description);
        alert.getLocation().setValue(latitude + ", " + longitude);
        alert.getSeverity().setValue(severity);
        alert.getSubCategory().setValue(subCategory);
        alert.getValidFrom().setValue(Functions.getActualDate());
        alert.getValidTo().setValue(Functions.getActualDate());
        try {
            alertController.createEntity(context, alert.getId(), alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  alert;
    }

    public JSONObject wrongWay(LatLng currentPoint, LatLng startPoint, LatLng endPoint, long currentDate){

        String[] currentCoords = currentPoint.toString().split(",");
        double longitude = Double.parseDouble(currentCoords[0]);
        double latitude = Double.parseDouble(currentCoords[1]);
        JSONObject alert = new JSONObject();

        totalDistance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
        startToLastDistance = SphericalUtil.computeDistanceBetween(startPoint, lastPoint);
        startToCurrentDistance = SphericalUtil.computeDistanceBetween(startPoint, currentPoint);
        endToLastDistance = SphericalUtil.computeDistanceBetween(endPoint, lastPoint);
        endToCurrentDistance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);


        if(!(startToCurrentDistance > startToLastDistance && endToLastDistance > endToCurrentDistance)){
            if (!isWrongWay) {
                isWrongWay = true;
            }
        }else{

            if (isWrongWay) {
                String severity = "";
                if (wrongWaySeconds < 3) {
                    if (wrongWaySeconds >= 3 && wrongWaySeconds < 5) {
                        severity = "informational";
                    } else if (wrongWaySeconds >= 5 && wrongWaySeconds < 7) {
                        severity = "low";
                    } else if (wrongWaySeconds >= 7 && wrongWaySeconds < 9) {
                        severity = "medium";
                    } else if (wrongWaySeconds >= 9 && wrongWaySeconds < 11) {
                        severity = "high";
                    } else {
                        severity = "critical";
                    }
                }
                if (severity != "") {
                    sendAlert("Wrong Way Detection", severity, "wrongWay", latitude, longitude);
                }
            }
            isWrongWay = false;
            wrongWaySeconds = 0;
            wrongWayAlertSent = false;
        }

        if (isWrongWay){
            wrongWaySeconds ++;
            if (wrongWaySeconds >= 11 && wrongWayAlertSent){
                sendAlert("Wrong Way", "critical", "wrongWay", latitude, longitude);
                wrongWayAlertSent = true;
                isWrongWay = false;
                wrongWaySeconds = 0;
            }
        }

        lastPoint = currentPoint;

        try {
            alert.put("isWrongWay", isWrongWay);
            alert.put("wrongWaySeconds", wrongWaySeconds);
        } catch(Exception e){ }

        return alert;
    }

    public void writeFile(String text){
        Functions.saveToFile(fileName, text);
    } 

    
    public JSONObject suddenStop(double currentSpeed , long currentDate, double latitude, double longitude){

        JSONObject alert = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");

        String commonData =
            currentSpeed + ", " +
            "Fecha Actual: "+ sdf.format(currentDate) + ",";
            

        String result = "";
        
        initialVelocity = finalVelocity;
        finalVelocity = currentSpeed;
        initialDate = finalDate;
        finalDate = currentDate;

        if (((finalVelocity < initialVelocity) || (finalVelocity == 0 && initialVelocity == 0)) && stopped == false && suddenAlertSent == false){
        
            if (!isStopping){
                speedReached = initialVelocity;
                dateSpeedReached = initialDate;
                isStopping = true;
            }

            if (finalVelocity == 0 ){ //&& speedReached > 1.39){
                if(wasStopped){
                    if(!stopped) {

                        if (speedReached > 13.8889){
                            frictionCoefficient = .65;
                        }

                        double idealDistance = 0;
                        idealDistance = Math.pow(speedReached, 2) / (2 * frictionCoefficient * gravity);
                        idealDistance += speedReached;

                        double realDistance = 0;
                        long diffDate = finalDate - dateSpeedReached;
                        long time = TimeUnit.MILLISECONDS.toSeconds(diffDate);
                        realDistance = ((finalVelocity + speedReached) / 2) * (time - 1);

                        if (idealDistance > realDistance) {
                            result += "PARADA REPENTINA, ";
                            isSuddenStop = true;
                        } else {
                            result += "PARADA NORMAL, ";
                        }
                        result += " Distancia Ideal: " + idealDistance + ", " + "Distancia Real: " + realDistance + ", " + "Vel.Alcanzada: " + speedReached + " m/s, " + "Fecha Vel.Alcanzada: " + sdf.format(dateSpeedReached);

                    }
                    stopped = true;
                }
                wasStopped = true;
            }

        } else{
            if(finalVelocity > 0){
                speedReached = 0;
                dateSpeedReached = 0;
                isStopping = false;
                suddenAlertSent = false;
                wasStopped = false;

                if(stopped){
                    String severity =  "";
                    if(stoppedSeconds > 1 && stoppedSeconds <= 2 ){ //informational
                        severity = "informational";
                    } else if(stoppedSeconds > 2 && stoppedSeconds <=4){ // Low
                        severity = "low";
                    } else if(stoppedSeconds > 4 && stoppedSeconds <=6){ // Medium
                        severity = "medium";
                    } else if(stoppedSeconds > 6 && stoppedSeconds <=8){ // High
                        severity = "high";
                    } else if(stoppedSeconds > 8 ) { // Critical
                        severity = "critical";
                    }
                    if (severity != "" && isSuddenStop) {
                        sendAlert( commonData, severity, "suddenStop", latitude,  longitude);
                    }
                    stopped = false;
                    stoppedSeconds = 0;
                    isSuddenStop = false;
                }
            }

            if(stopped){
                stoppedSeconds ++;
                if(stoppedSeconds > 8 && !suddenAlertSent){ //Critical
                    if (isSuddenStop){
                        sendAlert(commonData, "critical", "suddenStop",latitude, longitude);
                    }
                    suddenAlertSent = true;
                    stopped = false;
                    stoppedSeconds = 0;
                    isSuddenStop = false;
                }
            }
            
        }
        writeFile(commonData + result);

        try {
            alert.put("isStopeed", stopped);
            alert.put("isStopping", isStopping);
            alert.put("stoppendSeconds", stoppedSeconds);
            /*Datos de desarrollo*/
            alert.put("result", result);
            alert.put("isSuddenStop", isSuddenStop);
        } catch(Exception e){ }


        return alert;
    }

    /**
     * @param maximumSpeed velocidad maxima del road segment
     * @param speed velocidad anterior.
     * @return la severidad del exceso de velocidad.
     */
    public JSONObject speeding(double minimumSpeed,double maximumSpeed, double speed, double latitude, double longitude){
        boolean isSpeeding  = false;
        boolean under = false, over = false;

        JSONObject alert = new JSONObject();

        if (!(speed > minimumSpeed && speed < maximumSpeed) && !speedingAlertSent){
            if (!isSpeeding) {
                if (speed < minimumSpeed) {
                    under = true;
                    over = false;
                }
                if (speed > maximumSpeed) {
                    over = true;
                    under = false;
                }
                isSpeeding = true;
            }

        }else{

            if (isSpeeding) {
                String severity = "";
                if (speedigSeconds < 3) {
                    if (speedigSeconds >= 3 && speedigSeconds < 5) {
                        severity = "informational";
                    } else if (speedigSeconds >= 5 && speedigSeconds < 7) {
                        severity = "low";
                    } else if (speedigSeconds >= 7 && speedigSeconds < 9) {
                        severity = "medium";
                    } else if (speedigSeconds >= 9 && speedigSeconds < 11) {
                        severity = "high";
                    } else {
                        severity = "critical";
                    }
                }
                if (severity != "")
                    sendAlert("Speeding", severity, "spedding", latitude, longitude);
            }
            isSpeeding = false;
            speedigSeconds = 0;
            over = false;
            under = false;
            speedingAlertSent= false;
        }

        if (isSpeeding){
            speedigSeconds ++;
            if (speedigSeconds >= 11 && !speedingAlertSent){
                sendAlert("Speeding", "critical", "spedding", latitude, longitude);
                speedingAlertSent = true;
                isSpeeding = false;
                speedigSeconds = 0;
                over = false;
                under = false;
            }
        }

        try {
            alert.put("isSpeeding", isSpeeding);
            alert.put("under", under);
            alert.put("over", over);
            alert.put("over", speedigSeconds);
        } catch(Exception e){ }


        return alert;
    }


    @Override
    public void onCreateEntityAlert(Response response) {

    }

    @Override
    public void onUpdateEntityAlert(Response response) {

    }

    @Override
    public void onGetEntitiesAlert(Response response) {

    }
}
