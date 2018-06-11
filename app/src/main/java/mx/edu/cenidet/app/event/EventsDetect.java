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
import www.fiware.org.ngsi.utilities.Functions;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;



/**
 * Created by Alberne on 04/04/2018.
 */

public class EventsDetect {

    private static Context context = null; 
    private static String fileName = "velocidades.csv";

    private static String idDevice ;
    private AlertController alertController;

    private static double gravity=9.81; // valor en m/s
    private static double frictionCoefficient=0.95; //coeficiente de friccion entre los neumaticos y el piso
    private static double initialVelocity = 0;
    private static double finalVelocity = 0;
    private static long initialDate = 0;
    private static long finalDate = 0;
    private static boolean isStoping = false;
    private static boolean stoped = false;
    private static long stopedSeconds = 0 ;
    private static double speedReached = 0;
    private static long dateSpeedReached = 0;

    private static double distancePoints = 0; 
    private static LatLng lastPoint = null;

    public  EventsDetect () {
        context = HomeActivity.MAIN_CONTEXT;
        //this.idDevice = new DevicePropertiesFunctions().getAlertId(context);
    }
    
    

    /**
     * @param description La velocidad máxima permitida es 20 km/h. Velocidad actual del vehiculo es 25 km/h.
     * @param severity
     * @param subCategory UnauthorizedSpeeDetection
     * @param latitude
     * @param longitude
     */

    private void sendAlert(String description, String severity, String subCategory, double latitude, double longitude){
        
        Alert alert = new Alert();
        alert.setId(new DevicePropertiesFunctions().getAlertId(context));
        alert.getAlertSource().setValue(new DevicePropertiesFunctions().getDeviceId(context));
        alert.getCategory().setValue("Traffic");
        alert.getDateObserved().setValue(Functions.getActualDate());
        alert.getDescription().setValue(description);
        alert.getLocation().setValue(latitude+", "+longitude);
        alert.getSeverity().setValue(severity);
        alert.getSubCategory().setValue(subCategory);
        alert.getValidFrom().setValue(Functions.getActualDate());
        alert.getValidTo().setValue(Functions.getActualDate());
        try {
           alertController.createEntity(context, alert.getId(), alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String text){
        Functions.saveToFile(fileName, text);
    } 

    
    public String suddenStop(double currentSpeed , long currentDate, Location currentP){
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");

        

        String comunData = 
            currentP.getLatitude() + "," +
            currentP.getLongitude() + "," +
            currentSpeed + "," +
            "Fecha Actual : "+ sdf.format(currentDate) + ",";
            

        String result = "";
        

        initialVelocity = finalVelocity;
        finalVelocity = currentSpeed;
        initialDate = finalDate;
        finalDate = currentDate;


        LatLng currentPoint = new LatLng(currentP.getLatitude(), currentP.getLongitude());

        if ( finalVelocity < initialVelocity ){

            sendAlert("SUDDEN", "low", "SuddenStop", currentP.getLatitude(), currentP.getLongitude());

            distancePoints += SphericalUtil.computeDistanceBetween(lastPoint, currentPoint);
        
            if (isStoping == false){
                speedReached = initialVelocity;
                dateSpeedReached = initialDate;
                isStoping = true;
            }

            if (finalVelocity == 0){ 
                double idealDistance = 0;
                idealDistance =((Math.pow(speedReached, 2) / (2 * frictionCoefficient * gravity)));
                double realDistance = 0;
                long diffDate = finalDate - dateSpeedReached;
                long time = TimeUnit.MILLISECONDS.toSeconds(diffDate);
                realDistance = ((finalVelocity + speedReached )  / 2) * (time) ;
                //idealDistance = Math.round(idealDistance);
                //realDistance = Math.round(realDistance);

                if (idealDistance > realDistance){
                    result += "PARADA REPENTINA,";
                    stoped = true; 
                    stopedSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - finalDate);
                }else {
                    result += "PARADA NORMAL,";
                }

                result += 
                    "Distancia ideal : " +  idealDistance + "," +
                    "Distancia Real : " + realDistance + "," + 
                    "Distancia Puntos : " + distancePoints + "," +
                    "Alcanzada : " + speedReached + "," +
                    "Fecha Alcanzada:" + sdf.format(dateSpeedReached);

                    
                
            }

        } else{

            distancePoints = 0;
            if(finalVelocity > 0){
                speedReached = 0;
                dateSpeedReached = 0;
                isStoping = false;

                if(!stoped){
                    String severity =  "";
                    stopedSeconds = 6 ;
                    if(stopedSeconds > 1 && stopedSeconds <= 2 ){ //informational
                        severity = "informational";
                    } else if(stopedSeconds > 2 && stopedSeconds <=4){ // Low
                        severity = "low";
                    } else if(stopedSeconds > 4 && stopedSeconds <=6){ // Medium
                        severity = "medium";
                    } else if(stopedSeconds > 6 && stopedSeconds <=8){ // High
                        severity = "high";
                    } else if(stopedSeconds > 8 ) { // Critical
                        severity = "critical";
                    }
                    if (severity != "") {
                        
                    }
                    stoped = false;
                }
            }

            if(stoped){
                stopedSeconds ++;
                if(stopedSeconds > 8 ){ //Critical
                    
                    stoped = false;
                }
            }
            
        }
        writeFile(comunData + result);
        lastPoint = currentPoint;
        return result;
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

    

   
}
