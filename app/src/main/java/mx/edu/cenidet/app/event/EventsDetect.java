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
    private static double gravity=9.81; // valor en m/s
    private static double frictionCoefficient=0.95; //coeficiente de friccion entre los neumaticos y el piso
    private static double initialVelocity = 0 ,finalVelocity = 0;
    private static long initialDate = 0, finalDate = 0;
    private static boolean isStopping = false, wasStopped = false, suddenAlertSent = false, stopped = false ;
    private static long stoppedSeconds = 0 ;
    private static double speedReached = 0;
    private static long dateSpeedReached = 0;

    /*Variables Globales de contra sentido */
    private static double totalDistance = 0;
    private static double startToLastDistance = 0, startToCurrentDistance = 0;
    private static double endToLastDistance = 0, endToCurrentDistance = 0;
    private static LatLng lastPoint;
    private static boolean  wrongWayAlertSent = false, isWrongWay;
    private static long wrongWayDate = 0;


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

    public Alert wrongWay(LatLng currentPoint, LatLng startPoint, LatLng endPoint, long currentDate){
        
        Alert alert = null;

        totalDistance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
        startToLastDistance = SphericalUtil.computeDistanceBetween(startPoint, lastPoint);
        startToCurrentDistance = SphericalUtil.computeDistanceBetween(startPoint, currentPoint);
        endToLastDistance = SphericalUtil.computeDistanceBetween(endPoint, lastPoint);
        endToCurrentDistance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);

        /* Determina contrasentido */
        if(!(startToCurrentDistance > startToLastDistance && endToLastDistance > endToCurrentDistance)){
            isWrongWay = true;
            wrongWayDate = currentDate;
        }
        long wrongWayTmp = new Date().getTime()- wrongWayDate;
        long wrongWaySeconds = TimeUnit.MILLISECONDS.toSeconds(wrongWayTmp);
        if (wrongWaySeconds > 3){
            String severity =  "";
            if(wrongWaySeconds > 3 && wrongWaySeconds <= 5){ // informational
                severity = "informational";
            } else if(wrongWaySeconds > 5 && wrongWaySeconds <= 7){ // low
                severity = "low";
            } else if(wrongWaySeconds > 7 && wrongWaySeconds <= 9){ // medium
                severity = "medium";
            } if(wrongWaySeconds > 9 && wrongWaySeconds <= 11){ // high 
                severity =  "high";
            } else { // critical
                severity = "critical";
            }
            String[] currentCoords = currentPoint.toString().split(",");
            alert  = makeAlert( 
                "Wrong Way Automatic Detection", 
                severity, 
                "wrongWay", 
                Double.parseDouble(currentCoords[0]), 
                Double.parseDouble(currentCoords[1])
            );

        }
        lastPoint = currentPoint;
        return alert;
    }
    
    /**
     * @param description La velocidad máxima permitida es 20 km/h. Velocidad actual del vehiculo es 25 km/h.
     * @param severity
     * @param subCategory UnauthorizedSpeeDetection
     * @param latitude
     * @param longitude
     */

    private Alert makeAlert(String description, String severity, String subCategory, double latitude, double longitude){
        
        Alert alert = new Alert();
        alert.getDescription().setValue(description);
        alert.getLocation().setValue(latitude+", "+longitude);
        alert.getSeverity().setValue(severity);
        alert.getSubCategory().setValue(subCategory);
        return alert;
    }

    public void writeFile(String text){
        Functions.saveToFile(fileName, text);
    } 

    
    public Alert suddenStop(double currentSpeed , long currentDate, double latitude, double longitude){


        Alert alert = null ;
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
                        double idealDistance = 0;
                        idealDistance = Math.pow(speedReached, 2) / (2 * frictionCoefficient * gravity);
                        double realDistance = 0;
                        long diffDate = finalDate - dateSpeedReached;
                        long time = TimeUnit.MILLISECONDS.toSeconds(diffDate);

                        realDistance = ((finalVelocity + speedReached) / 2) * (time);

                        if (idealDistance > realDistance) {
                            result += "PARADA REPENTINA, ";
                        } else {
                            result += "PARADA NORMAL, ";
                        }
                        result += " Distancia Ideal: " + idealDistance + ", " + "Distancia Real: " + realDistance + ", " + "Vel.Alcanzada: " + speedReached + " m/s, " + "Fecha Vel.Alcanzada: " + sdf.format(dateSpeedReached);
                        //alert = sendAlert(commonData + result, "", "suddenStop", latitude,longitude);
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
                    if (severity != "") {
                        alert  = sendAlert( commonData, severity, "primero", latitude,  longitude);
                    }
                    stopped = false;
                    stoppedSeconds = 0;
                }
            }

            if(stopped){
                stoppedSeconds ++;
                if(stoppedSeconds > 8 && !suddenAlertSent){ //Critical
                    alert  = sendAlert(commonData, "critical", "segundo " + stoppedSeconds,latitude, longitude);
                    suddenAlertSent = true;
                    stopped = false;
                    stoppedSeconds = 0;
                }
            }
            
        }
        writeFile(commonData + result);

        return alert;
    }

    /**
     * @param maximumSpeed velocidad maxima del road segment
     * @param speedFrom velocidad anterior.
     * @param speedTo velocidad actual.
     * @return la severidad del exceso de velocidad.
     */
    public static String speeding(double maximumSpeed, double speedFrom, double speedTo){
        double averageSpeed;
        double subtractSpeed;

        if(speedFrom > 4.5 && speedTo > 4.5){
            averageSpeed = ( speedFrom + speedTo ) / 2;
            subtractSpeed = averageSpeed - maximumSpeed;
            if(subtractSpeed < 1){
                return "tolerance";
            }else if (subtractSpeed<=5){
                return "informational";
            }else if (subtractSpeed<=8){
                return "low";
            }else if (subtractSpeed<=12){
                return "medium";
            }else if (subtractSpeed<=16){
                return "high";
            }else {
                return "critical";
            }
        }
        return "";
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
