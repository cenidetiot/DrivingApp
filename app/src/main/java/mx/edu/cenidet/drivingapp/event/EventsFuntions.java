package mx.edu.cenidet.drivingapp.event;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;

/**
 * Created by Cipriano on 4/19/2018.
 */

public class EventsFuntions {
    public static ApplicationPreferences applicationPreferences = new ApplicationPreferences();
    /**
     * @param latitude actual en la que se encuentra el dispositivo.
     * @param longitude actual en la que se encuentra el dispositivo.
     * @param listLatLng lista de latitudes y longitudes del poligono.
     * @return true si se encuentra dentro del poligono, false en caso contrario.
     */
    public static boolean detectedArea(double latitude, double longitude, ArrayList<LatLng> listLatLng){
        if(PolyUtil.containsLocation(new LatLng(latitude,longitude), listLatLng, false)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * @param latitude actual en la que se encuentra el dispositivo.
     * @param longitude actual en la que se encuentra el dispositivo.
     * @param listZone lista de las zonas que se encuentran en el sistema.
     * @return un objeto zona, null si no se encuentra en ninguna zona.
     */
    public static Zone detectedZone(double latitude, double longitude, ArrayList<Zone> listZone){
        Zone auxZone = null;
        if(listZone.size() > 0){
            Zone zone;
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitudePolygon, longitudePolygon;
            ArrayList<LatLng> listLatLng;
            String[] subString;
            for(int i=0; i<listZone.size(); i++){
                listLatLng = new ArrayList<>();
                zone = new Zone();
                zone.setIdZone(listZone.get(i).getIdZone());
                zone.setType(listZone.get(i).getType());
                zone.setRefBuildingType(listZone.get(i).getRefBuildingType());
                zone.setName(listZone.get(i).getName());
                zone.setAddress(listZone.get(i).getAddress());
                zone.setCategory(listZone.get(i).getCategory());
                zone.setLocation(listZone.get(i).getLocation());
                zone.setCenterPoint(listZone.get(i).getCenterPoint());
                zone.setDescription(listZone.get(i).getDescription());
                zone.setDateCreated(listZone.get(i).getDateCreated());
                zone.setDateModified(listZone.get(i).getDateModified());
                zone.setStatus(listZone.get(i).getStatus());
                //Log.i("Status: ", "Campus name: "+listCampus.get(i).getName());
                try{
                    arrayLocation = new JSONArray(listZone.get(i).getLocation().getValue());
                    for (int j=0; j<arrayLocation.length(); j++){
                        originalString = arrayLocation.get(j).toString();
                        clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                        subString =  clearString.split(",");
                        latitudePolygon = Double.parseDouble(subString[0]);
                        longitudePolygon = Double.parseDouble(subString[1]);
                        listLatLng.add(new LatLng(latitudePolygon,longitudePolygon));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //statusLocation = PolyUtil.containsLocation(new LatLng(latitude,longitude), listLocation, false);
                //statusLocation = PolyUtil.containsLocation(new LatLng(18.870032,-99.211869), listLocation, false);
                if(EventsFuntions.detectedArea(latitude, longitude, listLatLng)){
                    auxZone = zone;
                    return auxZone;
                }
            }
            return auxZone;
        }else{
            return auxZone;
        }
    }

    /**
     * Este metodo servira para obtener todos los roadSegment de una zona.
     * Estos roadSegment serviran para detectar en cual se encuentra
     * @param context
     * @return
     */
    public static ArrayList<RoadSegment> getAllByRefRoadRoadSegment(Context context){
        String currentZone = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
        ArrayList<Road> listRoad = null;
        ArrayList<RoadSegment> listRoadSegment = null;
        if (currentZone.equals("undetectedZone")){

        }else{
            //obtener el valor del road en que se encuentra el usuario.
        }
        return listRoadSegment;
    }
    public static RoadSegment detectedRoadSegment(){
        RoadSegment roadSegment =  new RoadSegment();

        return roadSegment;
    }
    public static boolean detectRoadSegment(LatLng point, List<LatLng> polyline, double tolerance){
        if(PolyUtil.isLocationOnPath(point, polyline, false, tolerance) == true) {
            return true;
        }else{
            return false;
        }
    }
    public static String detectRoad(){
        String status = "";
        //punto en el que se encuentra el dispositivo correcto.
        //distancia de la linea 0.29730066012580564,
        LatLng point = new LatLng(18.869783,-99.211887);

        //distancia 3.7120693262823634, tolerancia 3.72 lado casa
        //LatLng point = new LatLng(18.869818,-99.211945);

        //Ecoplastica, 3.5458707853128764 de la calle, tolerancia 3.55
        //LatLng point = new LatLng(18.869763,-99.211384);

        //Delante de un punto
        //LatLng point = new LatLng(18.869762,-99.212167);
        List<LatLng> polyline = new ArrayList<>();
        polyline.add(new LatLng(18.869799,-99.21116));
        polyline.add(new LatLng(18.869781,-99.212142));

        //tolerancia en metros.
        boolean isLocationOnPath = PolyUtil.isLocationOnPath(point, polyline, false, 1);
        if (isLocationOnPath == true){
            status = "Se encuentra en la polyline: "+PolyUtil.distanceToLine(point, polyline.get(0), polyline.get(1));
        }else{
            status = "Fuera del rango de la polyline: "+PolyUtil.distanceToLine(point, polyline.get(0), polyline.get(1));
        }
        return status;
    }

    public static ArrayList<Road> getRoadByResponsible(Context context){
        String currentZone = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
        ArrayList<Road> listRoad = null;
        if (currentZone.equals("undetectedZone")){

        }else{

        }
        return listRoad;
    }

    public static void main(String ... arg){
        System.out.println(EventsFuntions.detectRoad());
    }
}
