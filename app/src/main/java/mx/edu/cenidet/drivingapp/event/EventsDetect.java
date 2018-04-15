package mx.edu.cenidet.drivingapp.event;

import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.geometry.Point;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Alberne on 04/04/2018.
 */

public class EventsDetect {

    private static double perceptionTime=0.0; //valor en segundos del tiempo que se tarda en reaccionar el actor despues de percibir el evento
    private static double reactionTime=1; //valor en segundos del tiempo que se tarda en reaccionar el actor despues de percibir el evento
    private static double gravity=9.81; // valor en m/s
    private static double frictionCoefficient=0.8; //coeficiente de friccion entre los neumaticos y el piso
    private static double segmentInclination=1; // pendiente que tiene la calle
    private static double brakingTolerance=0; //% de tolerancia que se aplicara a la distancia de frenado

    public static String oppositeDirectionDisplacement(LatLng lastPoint, LatLng currentPoint, LatLng startPoint, LatLng endPoint){
        String flag="undefined";
        double distanceTotal=SphericalUtil.computeDistanceBetween(startPoint, endPoint);

        double distance1Endpoint=SphericalUtil.computeDistanceBetween(lastPoint,endPoint);
        double distance2Endpoint=SphericalUtil.computeDistanceBetween(currentPoint,endPoint);
        double distance2StartPoint=SphericalUtil.computeDistanceBetween(currentPoint,startPoint);
        double distance1StartPoint=SphericalUtil.computeDistanceBetween(lastPoint,startPoint);
        if(PolyUtil.distanceToLine(currentPoint,startPoint,endPoint)<5) {
            if ( distanceTotal + 3 >= distance2StartPoint + distance2Endpoint){
                if(distance2Endpoint > distance1Endpoint){
                    flag="wrongWay";
                }else if(distance2Endpoint < distance1Endpoint){
                    flag="correctWay";
                }
            }

        }
        return flag;
    }

    public static boolean suddenStop(double lastSpeed, double currentSpeed, LatLng lastPoint,LatLng currrentPoint){
        boolean flag=false;
        double distanceTraveled= SphericalUtil.computeDistanceBetween(lastPoint,currrentPoint);
        double reactionDistance = lastSpeed*reactionTime; //La distancia de reacción es igual a la velocidad de dezplazamiento por el tiempo de reaccion del conductor
        double perceptionDistance = lastSpeed*perceptionTime;
        double brakedDistance=(Math.pow(lastSpeed,2)*segmentInclination)/(2*frictionCoefficient*gravity);
        double distanceTolerance=(reactionDistance+brakedDistance)*brakingTolerance;
        double minimumDistanceAcceptable=reactionDistance+perceptionDistance+brakedDistance-distanceTolerance;

        if(distanceTraveled<minimumDistanceAcceptable) flag=true;

        System.out.println("Distancia Recorrida:  " +distanceTraveled);
        System.out.println("Distancia de percepción: "+perceptionDistance);
        System.out.println("Distancia de reaccion: "+reactionDistance);
        System.out.println("Distancia de frenado: " + brakedDistance);
        System.out.println("Distancia de tolerancia: " + distanceTolerance);
        System.out.println("Distancia total de frenado: "+minimumDistanceAcceptable);
        System.out.println("Frenado repentino?: " + flag);


        return flag;
    }

    public static void main(String ... arg){
        System.out.println("Hola Mundo");
        LatLng latLng1 =new LatLng(0,2);
        LatLng latLng2 =new LatLng(0,1);
        LatLng start =new LatLng(0,0);
        LatLng end =new LatLng(0,3);
        double lastSpeed=2.77; //metros por segundo
        double currentSpeed=20; //metros por segundos

       // System.out.println(oppositeDirectionDisplacement(latLng1,latLng2,end));
        System.out.println(suddenStop(lastSpeed,currentSpeed,latLng1,latLng2));
    }
}
