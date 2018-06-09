package mx.edu.cenidet.app.event;

import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import com.google.android.gms.maps.model.LatLng;
import android.location.Location;

import java.util.Date;
import java.util.concurrent.TimeUnit;


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

    private static Context context; 
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

    public EventsDetect (Context _context) {
        context = _context ;
        idDevice = new DevicePropertiesFunctions().getAlertId(context);
    }
    
    /**
     * @param context contexto donde se ejecutara el metodo.
     * @return el identificador unico de la alerta.
    */
    public String getAlertId(){
        Date currentDate = new Date();
        Long date = currentDate.getTime() / 1000;
        return "Alert:Device_Smartphone_" + idDevice + ":" + date; 
    }

    /**
     * @param description La velocidad máxima permitida es 20 km/h. Velocidad actual del vehiculo es 25 km/h.
     * @param severity
     * @param subCategory UnauthorizedSpeeDetection
     * @param latitude
     * @param longitude
     */
    private void sendAlert(
        String description, 
        String severity, 
        String subCategory, 
        double latitude, 
        double longitude){

        String date = Functions.getActualDate();
        Alert alert = new Alert();
        alert.setId(getAlertId());
        alert.getAlertSource().setValue(idDevice);
        alert.getCategory().setValue("Traffic");
        alert.getDateObserved().setValue(date);
        alert.getDescription().setValue(description);
        alert.getLocation().setValue(latitude+", "+longitude);
        alert.getSeverity().setValue(severity);
        alert.getSubCategory().setValue(subCategory);
        alert.getValidFrom().setValue(date);
        alert.getValidTo().setValue(date);

        try {
           alertController.createEntity(context, alert.getId(), alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static String oppositeDirectionDisplacement(LatLng lastPoint ,LatLng currentPoint, LatLng startPoint, LatLng endPoint){
    
        String flag = "";

        if (lastPoint != null){
            
        }
        double distanceTotal = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
        
        double distance1Endpoint = SphericalUtil.computeDistanceBetween(lastPoint, endPoint);
        double distance2Endpoint = SphericalUtil.computeDistanceBetween(currentPoint, endPoint);
        double distance2StartPoint = SphericalUtil.computeDistanceBetween(currentPoint, startPoint);
        double distance1StartPoint = SphericalUtil.computeDistanceBetween(lastPoint, startPoint);

        if( PolyUtil.distanceToLine(currentPoint, startPoint, endPoint) < 5) {

            if (distanceTotal + 3 >= distance2StartPoint + distance2Endpoint){

                if(distance2Endpoint > distance1Endpoint){
                    flag = "wrongWay";
                }else if(distance2Endpoint < distance1Endpoint){
                    flag = "correctWay";
                }

            }

        }
        return flag;
    }*/

    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    public static float calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                        (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));

    }

    public static String suddenStop(double currentSpeed , long currentDate, Location currentP){
        String result = "";

        initialVelocity = finalVelocity;
        finalVelocity = currentSpeed;
        initialDate = finalDate;
        finalDate = currentDate;


        LatLng currentPoint = new LatLng(currentP.getLatitude(), currentP.getLongitude());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");

        if ( finalVelocity < initialVelocity ){

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
                    result = "PARADA REPENTINA";
                    stoped = true; 
                    stopedSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - finalDate);
                }else {
                    result += "PARADA a TIEMPO";
                }

                return result + 
                    ",Distancia ideal : " +  idealDistance+
                    ",Distancia Real : " + realDistance +
                    ",Distancia Puntos : " + distancePoints + 
                    ",Fecha Actual : "+ sdf.format(currentDate) + 
                    ",Alcanzada : " + speedReached + ",Fecha A :" + 
                    sdf.format(dateSpeedReached);
            }

        } else{

            distancePoints = 0;

            if(finalVelocity > 0){
                speedReached = 0;
                dateSpeedReached = 0;
                isStoping = false;

                if(stoped){
                    if(stopedSeconds > 1 && stopedSeconds <= 2 ){ // Enviar informational
                    } else if(stopedSeconds > 2 && stopedSeconds <=4){ // Low
                    } else if(stopedSeconds > 4 && stopedSeconds <=6){ // Medium
                    } else if(stopedSeconds > 6 && stopedSeconds <=8){ // High
                    } else if(stopedSeconds > 8 ) { // Critical
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
        lastPoint = currentPoint;
        return result;
    }


    public static void Write(String text){
        Functions.saveToFile(fileName, cadena);
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
            averageSpeed = (speedFrom+speedTo)/2;
            subtractSpeed = averageSpeed-maximumSpeed;
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
