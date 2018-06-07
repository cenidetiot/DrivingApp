package mx.edu.cenidet.app.event;

import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import android.util.Log;
/**
 * Created by Alberne on 04/04/2018.
 */

public class EventsDetect {
    

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

    //private static LatLng lastPoint = null;

    public static String oppositeDirectionDisplacement(LatLng lastPoint ,LatLng currentPoint, LatLng startPoint, LatLng endPoint){
    
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
    }


    public static String suddenStop(double currentSpeed , long currentDate){
        String result = "";

        initialVelocity = finalVelocity;
        finalVelocity = currentSpeed;
        initialDate = finalDate;
        finalDate = currentDate;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        if ( finalVelocity < initialVelocity ){

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
                idealDistance = Math.round(idealDistance);
                realDistance = Math.round(realDistance);
                //double errorConstant = idealDistance / 3 ;

                if (idealDistance > realDistance){
                    result = "PARADA REPENTINA";
                    stoped = true; 
                    stopedSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - finalDate);
                }else {
                    result += "PARADA";
                }

                return result + ",Actual : "+ sdf.format(currentDate) +",Alcazada :" + speedReached + ",Fecha  :" + sdf.format(dateSpeedReached);
            }
        } else{

            speedReached = 0;
            dateSpeedReached = 0;
            isStoping = false;

            if(finalVelocity > 0 ){
                if(stoped){

                    if(stopedSeconds > 0 && stopedSeconds <= 2 ){ // Enviar informational

                    } else if(stopedSeconds > 2 && stopedSeconds <=4){ // Low

                    } else if(stopedSeconds > 4 && stopedSeconds <=6){ // Medium

                    } else if(stopedSeconds > 6 && stopedSeconds <=8){ // High

                    } else { // Critical

                    }
                }
            }else{
                stopedSeconds ++;
                if(stopedSeconds > 10){ // Alerta Critica

                }
            }

        }
        return "";
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

        //Si la velocidad anterior y actual es mayor a cero.
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
