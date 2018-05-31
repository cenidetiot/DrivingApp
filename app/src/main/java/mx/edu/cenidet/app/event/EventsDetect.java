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
    

    //private static double perceptionTime=0.0; //valor en segundos del tiempo que se tarda en reaccionar el actor despues de percibir el evento
    private static double reactionTime=1; //valor en segundos del tiempo que se tarda en reaccionar el actor despues de percibir el evento
    private static double gravity=9.81; // valor en m/s
    private static double frictionCoefficient=0.95; //coeficiente de friccion entre los neumaticos y el piso
    //private static double segmentInclination=1; // pendiente que tiene la calle
    //private static double brakingTolerance=0; //% de tolerancia que se aplicara a la distancia de frenado

    private static double initialVelocity = 0;
    private static double finalVelocity = 0;
    private static Date initialDate = new Date();
    private static Date finalDate = new Date();
    private static boolean isStoping = false;
    
    private static double speedReached = 0;
    private static Date dateSpeedReached = new Date();

    public static String oppositeDirectionDisplacement(LatLng lastPoint, LatLng currentPoint, LatLng startPoint, LatLng endPoint){
    
        String flag = "undefined";
        
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


    public static boolean suddenStop(double currentSpeed , Long current){
       
        Date currentDate = new Date(current);
     
        initialVelocity = finalVelocity;
        finalVelocity = currentSpeed;
        initialDate = finalDate;
        finalDate = currentDate;

        //Long acelerationDetonating = (finalVelocity - initialVelocity) / (TimeUnit.MILLISECONDS.toSeconds( finalDate.getTime() - initialDate.getTime() )); 
        
        if (finalVelocity < initialVelocity){
             
            if (!isStoping){
                speedReached = initialVelocity;
                dateSpeedReached = initialDate;
                isStoping = true;
            }
        
            if (finalVelocity == 0){ // Se detuvo
                //Calcular Distacia de frenado ideal
                double idealDistance = 0;
                idealDistance = (speedReached * reactionTime) + ((Math.pow(speedReached, 2) / (2 * frictionCoefficient * gravity)));
                double aceleration = 0;
                aceleration = (finalVelocity - speedReached) / (TimeUnit.MILLISECONDS.toSeconds( finalDate.getTime() - initialDate.getTime() ));
                double realDistance = 0;
                realDistance = (speedReached * reactionTime) + (Math.pow(speedReached, 2) / (2 * aceleration) );
                double errorConstant = idealDistance / 3 ;
        
                if (realDistance <= errorConstant ){
                    Log.i("SUDDEN", "Parada repetina CRITICAL ");
                } else if(realDistance < (errorConstant * 2) && realDistance > errorConstant ) {
                    Log.i("SUDDEN", "Parada repetina HIGH ");
                }else if(realDistance < (errorConstant * 2) && realDistance > (errorConstant  * 2)) {
                    Log.i("SUDDEN", "Parada repetina MEDIUM ");
                }
            }
        }
        return true;

        /*boolean flag = false;
        double distanceTraveled= SphericalUtil.computeDistanceBetween(lastPoint, currrentPoint);
       
        double reactionDistance = lastSpeed * reactionTime; //La distancia de reacción es igual a la velocidad de dezplazamiento por el tiempo de reaccion del conductor
        
        double perceptionDistance = lastSpeed * perceptionTime;
        double brakedDistance=( Math.pow(lastSpeed, 2) * segmentInclination) / (2 * frictionCoefficient * gravity);
        double distanceTolerance=( reactionDistance + brakedDistance) * brakingTolerance;
        double minimumDistanceAcceptable= reactionDistance + perceptionDistance + brakedDistance - distanceTolerance;

        if(distanceTraveled < minimumDistanceAcceptable) flag = true;

        System.out.println("Distancia Recorrida:  " +distanceTraveled);
        System.out.println("Distancia de percepción: "+perceptionDistance);
        System.out.println("Distancia de reaccion: "+reactionDistance);
        System.out.println("Distancia de frenado: " + brakedDistance);
        System.out.println("Distancia de tolerancia: " + distanceTolerance);
        System.out.println("Distancia total de frenado: "+minimumDistanceAcceptable);
        System.out.println("Frenado repentino?: " + flag);


        return flag;
        */
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

    public static void main(String ... arg){

        double maximumSpeed=20;
        double speedFrom=31;
        double speedTo=20;
        System.out.println(speeding(maximumSpeed, speedFrom, speedTo));
        /*System.out.println("Hola Mundo");
        LatLng latLng1 =new LatLng(0,2);
        LatLng latLng2 =new LatLng(0,1);
        LatLng start =new LatLng(0,0);
        LatLng end =new LatLng(0,3);
        double lastSpeed=2.77; //metros por segundo
        double currentSpeed=20; //metros por segundos

       // System.out.println(oppositeDirectionDisplacement(latLng1,latLng2,end));
        System.out.println(suddenStop(lastSpeed,currentSpeed,latLng1,latLng2));*/
    }
}
