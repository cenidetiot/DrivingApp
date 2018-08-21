package mx.edu.cenidet.app.event;

import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import com.google.android.gms.maps.model.LatLng;
import android.location.Location;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mx.edu.cenidet.app.activities.HomeActivity;

import android.content.Context;

import java.text.SimpleDateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.Functions;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;


class EventsDetectTest implements AlertController.AlertResourceMethods {

    private static Context context = null;
    private static String fileName = "velocidades.csv";

    private static String idDevice ;
    private AlertController alertController;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");


    /*Variables globales de paradas repentinas*/
    private static double gravity = 9.81; // valor en m/s
    private static double frictionCoefficient = .75; //coeficiente de friccion entre los neumaticos y el piso
    private static double initialVelocity = 0 ,finalVelocity = 0;
    private static long initialDate = 0, finalDate = 0, lastUpdate;
    private static boolean isStopping = false, wasStopped = false, suddenAlertSent = false, stopped = false ;
    private static long stoppedSeconds = 0 ;
    private static double speedReached = 0;
    private static long dateSpeedReached = 0;
    private static boolean isSuddenStop = false;


    private long count = 0;
    private float last_x, last_y, last_z;
    private  boolean going = false;

    /*Variables Globales de contra sentido */
    private static double x=0,y=0,z=0;
    private static double totalDistance = 0, startToLastDistance = 0, startToCurrentDistance = 0;
    private static double endToLastDistance = 0, endToCurrentDistance = 0;
    private static LatLng lastPoint =null;
    private static boolean  wrongWayAlertSent = false, isWrongWay = false;
    private static long wrongWaySeconds = 0;

    /*Variables globales de velocidad*/
    private static int speedigSeconds;
    private static boolean speedingAlertSent = false;
    boolean isSpeeding  = false;


    public  EventsDetectTest () {
        context = HomeActivity.MAIN_CONTEXT;
        alertController = new AlertController(this);

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
            //alertController.createEntity(context, alert.getId(), alert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  alert;
    }

    public JSONObject wrongWay(LatLng currentPoint, LatLng startPoint, LatLng endPoint, long currentDate){


        double longitude = currentPoint.longitude;
        double latitude = currentPoint.latitude;
        boolean stop = false;

        JSONObject alert = new JSONObject();

        if (lastPoint != null) {

            totalDistance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
            startToLastDistance = SphericalUtil.computeDistanceBetween(startPoint, lastPoint);
            startToCurrentDistance = SphericalUtil.computeDistanceBetween(startPoint, currentPoint);
            endToLastDistance = SphericalUtil.computeDistanceBetween(endPoint, lastPoint);
            endToCurrentDistance = SphericalUtil.computeDistanceBetween(endPoint, currentPoint);

            DecimalFormat df = new DecimalFormat("0.0000");
            Log.d("Total",  ""+ totalDistance);
            Log.d("STARTTOLAST", "" + df.format(startToLastDistance));
            Log.d("STARTTOCURRENT", "" + df.format(startToCurrentDistance));
            Log.d("ENDTOLAST" , ""+ df.format(endToLastDistance ));
            Log.d("ENDTOCURRENT", "" + df.format(endToCurrentDistance));


            if (!(startToCurrentDistance >= startToLastDistance && endToLastDistance >= endToCurrentDistance)) {
                isWrongWay = true;
            } else {

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
                        //sendAlert("Wrong Way Detection", severity, "wrongWay", latitude, longitude);
                    }
                }
                isWrongWay = false;
                wrongWaySeconds = 0;
                wrongWayAlertSent = false;
            }

            if (isWrongWay) {
                wrongWaySeconds++;
                if (wrongWaySeconds >= 11 && wrongWayAlertSent) {
                    //sendAlert("Wrong Way", "critical", "wrongWay", latitude, longitude);
                    wrongWayAlertSent = true;
                    isWrongWay = false;
                    wrongWaySeconds = 0;
                }
            }



            Log.d("WRON", "" + isWrongWay);
        }

        lastPoint = currentPoint;

        try {
            alert.put("isWrongWay", isWrongWay);
            alert.put("wrongWaySeconds", wrongWaySeconds);
        } catch(Exception e){ }

        return alert;
    }

    public void writeFile( String fileName ,String text){
        Functions.saveToFile(fileName, text);
    }

    public void saveAxis (double x, double y, double z ) {

        this.x = x;
        this.y = y;
        this.z = z;
        return;
    }

    public boolean moving (){
        boolean isMoving = true;
        if (this.z < 9.6 || this.z >= 9.95){
            isMoving = false;
        }
        return isMoving;
    }

    public static List<LatLng> convertStringToPolyline(String stringLocations){
        List<LatLng> polyline = new ArrayList<>();
        try {
            String originalString, clearString;
            String[] subString;

            double latitude ;
            double longitude;
            JSONArray arrayLocation = new JSONArray(stringLocations);
            for (int j = 0; j < arrayLocation.length(); j++) {
                originalString = arrayLocation.get(j).toString();
                clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                subString = clearString.split(",");
                latitude = Double.parseDouble(subString[0]);
                longitude = Double.parseDouble(subString[1]);
                polyline.add(new LatLng(latitude, longitude));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return polyline;
    }

    public static  List<LatLng> detectLineOfSegment(double latitude, double longitude, RoadSegment roadSegment){
        List<LatLng> nearLine = new ArrayList<>();

        try {
            List<LatLng> polyline = convertStringToPolyline(roadSegment.getLocation());
            LatLng myPoint = new LatLng(latitude, longitude);
            Double distances = 0.0;
            int nearIndex = -1;

            for (int j = 0; j < polyline.size() - 1; j++) {

                List<LatLng> tempPolyline = new ArrayList<>();
                tempPolyline.add(polyline.get(j));
                tempPolyline.add(polyline.get(j + 1));

                if (PolyUtil.isLocationOnPath(myPoint, tempPolyline, false, roadSegment.getWidth())) {
                    LatLng tempStart = tempPolyline.get(0);
                    LatLng tempEnd = tempPolyline.get(1);

                    Double tempdistance = SphericalUtil.computeDistanceBetween(tempStart, myPoint);
                    tempdistance += SphericalUtil.computeDistanceBetween(tempEnd, myPoint);
                    if (distances > tempdistance || nearIndex == -1) {
                        distances = tempdistance;
                        nearLine = tempPolyline;
                        nearIndex = j;
                    }
                    Log.d("SEGMENTO", "D1: " + distances + " D2: " + tempdistance);

                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return nearLine;
    }

    public JSONObject suddenStop(double currentSpeed , long currentDate, double latitude, double longitude) {
        String suddenDescription = "Sudden Stop Detection";

        JSONObject alert = new JSONObject();

        String commonData = currentSpeed + ", " + "x: "+x+", y: "+y+", z: "+z+", Fecha Actual: " + sdf.format(currentDate) + ",";

        writeFile( "locations.csv", "" +latitude +", " + longitude + ", "+ sdf.format(currentDate));

        String result = "";

        boolean _isStopped = false;
        boolean _isStopping = false;
        boolean _isAcelerating = false;

        initialVelocity = finalVelocity;
        finalVelocity = currentSpeed;
        initialDate = finalDate;
        finalDate = currentDate;

        if (((finalVelocity < initialVelocity) || (finalVelocity == 0 && initialVelocity == 0)) && stopped == false && suddenAlertSent == false) {

            if (!isStopping) {
                speedReached = initialVelocity;
                dateSpeedReached = initialDate;
                isStopping = true;
            }


            if (finalVelocity == 0) { //&& speedReached > 1.39){
                if (wasStopped) {
                    if (!stopped) {

                        if (speedReached > 13.8889) {
                            frictionCoefficient = .65;
                        }

                        double idealDistance = 0;
                        idealDistance = Math.pow(speedReached, 2) / (2 * frictionCoefficient * gravity);
                        idealDistance += speedReached;

                        double realDistance = 0;
                        long diffDate = finalDate - dateSpeedReached;
                        long time = TimeUnit.MILLISECONDS.toSeconds(diffDate);
                        if (time >= 1) {
                            time = time - 1;
                        }
                        //realDistance = ((finalVelocity + speedReached) / 2) * (time - 1);
                        realDistance = ((finalVelocity + speedReached) / 2) * (time);
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

        } else {

            if (finalVelocity > 0) {
                speedReached = 0;
                dateSpeedReached = 0;
                isStopping = false;
                suddenAlertSent = false;
                wasStopped = false;

                if (stopped) {
                    String severity = "";
                    if (stoppedSeconds > 1 && stoppedSeconds <= 2) { //informational
                        severity = "informational";
                    } else if (stoppedSeconds > 2 && stoppedSeconds <= 4) { // Low
                        severity = "low";
                    } else if (stoppedSeconds > 4 && stoppedSeconds <= 6) { // Medium
                        severity = "medium";
                    } else if (stoppedSeconds > 6 && stoppedSeconds <= 8) { // High
                        severity = "high";
                    } else if (stoppedSeconds > 8) { // Critical
                        severity = "critical";
                    }
                    if (severity != "" && isSuddenStop) {
                        suddenDescription += " " + sdf.format(new Date().getTime());
                        sendAlert(suddenDescription, severity, "suddenStop", latitude, longitude);
                    }
                    stopped = false;
                    stoppedSeconds = 0;
                    isSuddenStop = false;
                }
            }

            if (stopped) {
                stoppedSeconds++;
                if (stoppedSeconds > 8 && !suddenAlertSent) { //Critical
                    if (isSuddenStop) {
                        suddenDescription += " " + sdf.format(new Date().getTime());
                        sendAlert(suddenDescription, "critical", "suddenStop", latitude, longitude);
                    }
                    suddenAlertSent = true;
                    stopped = false;
                    stoppedSeconds = 0;
                    isSuddenStop = false;
                }
            }

        }
        writeFile("velocity.csv",commonData + result);
        if (finalVelocity < initialVelocity)
            _isStopping = true;
        if (finalVelocity > initialVelocity)
            _isAcelerating = true;


        try {
            alert.put("isStopped", stopped);
            alert.put("isStopping", _isStopping);
            alert.put("isAcelerating", _isAcelerating);
            alert.put("stoppedSeconds", stoppedSeconds);
            /*Datos de desarrollo*/
            alert.put("result", result);
            alert.put("isSuddenStop", isSuddenStop);
        } catch (Exception e) {
        }


        return alert;

    }

    /**
     * @param maximumSpeed velocidad maxima del road segment
     * @param speed velocidad anterior.
     * @return la severidad del exceso de velocidad.
     */
    public JSONObject speeding(double minimumSpeed,double maximumSpeed, double speed, double latitude, double longitude){
        String speedDescription = "Unauthorized Speed Detection";

        boolean speedingValue = false;

        boolean under = false, over = false;

        JSONObject alert = new JSONObject();

        if (!(speed > minimumSpeed && speed < maximumSpeed)){

            if (speed < minimumSpeed) {
                under = true;
                over = false;
            }
            if (speed > maximumSpeed) {
                over = true;
                under = false;
            }

            isSpeeding = true;

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
                if (severity != "") {
                    speedDescription += " " + sdf.format(new Date().getTime());
                    sendAlert(speedDescription , severity, "speeding", latitude, longitude);

                }
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
                speedDescription += " " + sdf.format(new Date().getTime());
                sendAlert(speedDescription, "critical", "speeding", latitude, longitude);
                speedingAlertSent = true;
                isSpeeding = false;
                speedigSeconds = 0;
                over = false;
                under = false;
            }
        }

        Log.d("SPEEDING", " - " + isSpeeding);

        try {
            alert.put("isSpeeding", isSpeeding);
            alert.put("under", under);
            alert.put("over", over);
            alert.put("speedSeconds", speedigSeconds);
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


    public static  void main(String ... arg){
        RoadSegment roadSegment = new RoadSegment();
        roadSegment.setLocation("[[18.8820894, -99.2202577],[18.882051, -99.2202933],[18.8820004, -99.2203296],[18.88182, -99.2203856],[18.18422, -99.220432],[18.8817658, -99.2204766],[18.8816988, -99.2205059],[18.8816489, -99.2205192],[18.8815967, -99.2205287],[18.8815162, -99.2205413],[18.8814529, -99.2205463],[18.8813019, -99.2205563],[18.8810672, -99.2205767],[18.8809673, -99.2205904],[18.8809077, -99.2206011],[18.880858, -99.2206177],[18.8808043, -99.2206446],[18.8807519, -99.2206753],[18.8806945, -99.2207151],[18.8806074, -99.22018],[18.8805141, -99.2208607],[18.8804692, -99.2208995],[18.8804147, -99.2209357],[18.8803641, -99.2209634],[18.88184, -99.2209825],[18.8802824, -99.2209917],[18.8802398, -99.2209894],[18.8802014, -99.2209823],[18.8801425, -99.2209659],[18.8799972, -99.2208961],[18.8798668, -99.2208338],[18.8797505, -99.2207844],[18.8796824, -99.2207633],[18.8796333, -99.2207538],[18.8795474, -99.2207526],[18.879508, -99.2207612],[18.8794625, -99.22078],[18.87942, -99.2208032],[18.8793623, -99.2208524],[18.8792666, -99.2209298],[18.81888, -99.22098],[18.8791032, -99.2210289],[18.8790542, -99.2210511],[18.879008, -99.2210691],[18.8789509, -99.2210879],[18.8788942, -99.2211026],[18.878838, -99.2211153],[18.8787885, -99.2211228],[18.878735, -99.221125],[18.8786775, -99.2211268],[18.8786241, -99.221137],[18.87818, -99.2211492],[18.8785537, -99.2211617],[18.8785229, -99.21816],[18.8784985, -99.2212073],[18.8784743, -99.2212386],[18.8784554, -99.2212663],[18.8784363, -99.221306],[18.87186, -99.2213554],[18.8783548, -99.2215944]]");
        roadSegment.setWidth(3);
        convertStringToPolyline("[[18.8820894, -99.2202577],[18.882051, -99.2202933],[18.8820004, -99.2203296],[18.88182, -99.2203856],[18.18422, -99.220432],[18.8817658, -99.2204766],[18.8816988, -99.2205059],[18.8816489, -99.2205192],[18.8815967, -99.2205287],[18.8815162, -99.2205413],[18.8814529, -99.2205463],[18.8813019, -99.2205563],[18.8810672, -99.2205767],[18.8809673, -99.2205904],[18.8809077, -99.2206011],[18.880858, -99.2206177],[18.8808043, -99.2206446],[18.8807519, -99.2206753],[18.8806945, -99.2207151],[18.8806074, -99.22018],[18.8805141, -99.2208607],[18.8804692, -99.2208995],[18.8804147, -99.2209357],[18.8803641, -99.2209634],[18.88184, -99.2209825],[18.8802824, -99.2209917],[18.8802398, -99.2209894],[18.8802014, -99.2209823],[18.8801425, -99.2209659],[18.8799972, -99.2208961],[18.8798668, -99.2208338],[18.8797505, -99.2207844],[18.8796824, -99.2207633],[18.8796333, -99.2207538],[18.8795474, -99.2207526],[18.879508, -99.2207612],[18.8794625, -99.22078],[18.87942, -99.2208032],[18.8793623, -99.2208524],[18.8792666, -99.2209298],[18.81888, -99.22098],[18.8791032, -99.2210289],[18.8790542, -99.2210511],[18.879008, -99.2210691],[18.8789509, -99.2210879],[18.8788942, -99.2211026],[18.878838, -99.2211153],[18.8787885, -99.2211228],[18.878735, -99.221125],[18.8786775, -99.2211268],[18.8786241, -99.221137],[18.87818, -99.2211492],[18.8785537, -99.2211617],[18.8785229, -99.21816],[18.8784985, -99.2212073],[18.8784743, -99.2212386],[18.8784554, -99.2212663],[18.8784363, -99.221306],[18.87186, -99.2213554],[18.8783548, -99.2215944]]");
    }



}
