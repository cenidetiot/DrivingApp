package mx.edu.cenidet.app.event;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
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
     * Used to detects the area where the user is
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
     * Used to detect the zone where the user is
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
                zone.setName(listZone.get(i).getName());
                zone.setAddress(listZone.get(i).getAddress());
                zone.setCategory(listZone.get(i).getCategory());
                zone.setLocation(listZone.get(i).getLocation());
                zone.setCenterPoint(listZone.get(i).getCenterPoint());
                zone.setDescription(listZone.get(i).getDescription());
                zone.setDateCreated(listZone.get(i).getDateCreated());
                zone.setDateModified(listZone.get(i).getDateModified());
                zone.setStatus(listZone.get(i).getStatus());
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
               if(EventsFuntions.detectedArea(latitude, longitude, listLatLng)){
                    auxZone = zone;
                    return auxZone;
                }
            }
        }
        return  auxZone;
    }

    /**
     * Used to detect which parking isede of the zones is the user
     * @param latitude
     * @param longitude
     * @param listOffStreetParking
     * @return
     */
    public static OffStreetParking detectedOffStreetParking(double latitude, double longitude, ArrayList<OffStreetParking> listOffStreetParking){
        OffStreetParking auxOffStreetParking = null;
        if(listOffStreetParking.size() > 0){
            OffStreetParking offStreetParking;
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitudePolygon, longitudePolygon;
            ArrayList<LatLng> listLatLng;
            String[] subString;
            for(int i=0; i<listOffStreetParking.size(); i++){
                listLatLng = new ArrayList<>();
                offStreetParking = new OffStreetParking();
                offStreetParking.setIdOffStreetParking(listOffStreetParking.get(i).getIdOffStreetParking());
                offStreetParking.setType(listOffStreetParking.get(i).getType());
                offStreetParking.setName(listOffStreetParking.get(i).getName());
                offStreetParking.setCategory(listOffStreetParking.get(i).getCategory());
                offStreetParking.setLocation(listOffStreetParking.get(i).getLocation());
                offStreetParking.setDescription(listOffStreetParking.get(i).getDescription());
                offStreetParking.setAreaServed(listOffStreetParking.get(i).getAreaServed());
                offStreetParking.setStatus(listOffStreetParking.get(i).getStatus());
                try{
                    arrayLocation = new JSONArray(listOffStreetParking.get(i).getLocation());
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
                if(EventsFuntions.detectedArea(latitude, longitude, listLatLng)){
                    auxOffStreetParking = offStreetParking;
                }
            }

        }
        return auxOffStreetParking;

    }

    /**
     * Used to get the road segments inside of the zone or parking where the user is
     * @param context
     * @param currentLatitude
     * @param currentLongitude
     * @return
     */
    public static RoadSegment detectedRoadSegment(Context context, double currentLatitude, double currentLongitude){
        SQLiteDrivingApp sqLiteDrivingApp; //Para acceder a los metodos que gestionan la DB interna del dispositivo movil.
        String currentZone = applicationPreferences.getPreferenceString(context, ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_CURRENT_ZONE);
        //String currentZone = "Zone_1524284309191";//cenidet
        RoadSegment roadSegment = null;
        ArrayList<OffStreetParking> listOffStreetParking; //obtendra la lista de los parking que pertenecen a la zona.
        ArrayList<Road> listRoadByIdParking; //obtendra la lista de los road que pertenecen al parking.
        OffStreetParking offStreetParking;//para almacenar el parking donde se encuentra el dispositivo movil.
        if(!currentZone.equals("undetectedZone")){
            sqLiteDrivingApp = new SQLiteDrivingApp(context);
            listOffStreetParking = sqLiteDrivingApp.getAllOffStreetParkingByAreaServed(currentZone);//obtiene la lista de parking de la zona donde se encuentra.
            if(listOffStreetParking.size() > 0){
                //Determina si se encuentra dentro de un parking
                offStreetParking = EventsFuntions.detectedOffStreetParking(currentLatitude, currentLongitude, listOffStreetParking);
                if (offStreetParking != null){//Si se encuentra dentro de un parking
                    //Obtener todos los road del parking
                    //listRoadByIdParking = sqLiteDrivingApp.getRoadByResponsible(offStreetParking.getIdOffStreetParking());
                    Log.i("STATUS: ","SI SE ENCUENTRA EN UN PARKING------------------------------------------------");
                    roadSegment = EventsFuntions.getRoadSegmentParking(currentLatitude, currentLongitude, offStreetParking, sqLiteDrivingApp);
                }else {//Si no se encuentra dentro de un parking.
                    roadSegment = EventsFuntions.getRoadSegmentZone(currentLatitude, currentLongitude, currentZone, sqLiteDrivingApp);

                }//FIN Si no se encuentra dentro de un parking.
            }else{//si no se encuentra ningun parking asignado a la zona.
                Log.i("STATUS: ","ESTA ZONA NO CUENTA CON PARKING----------------------------------------------");
                roadSegment = EventsFuntions.getRoadSegmentZone(currentLatitude, currentLongitude, currentZone, sqLiteDrivingApp);
            }

        }
        return roadSegment;
    }

    /**
     * Used to detect the road segment inside the parking where the user is
     * @param currentLatitude latitude actual en la que se encuentra el dispositivo.
     * @param currentLongitude longitude actual en la que se encuentra el dispositivo.
     * @param offStreetParking objeto del parking en el que se encuentra el dispositivo.
     * @param sqLiteDrivingApp objeto para realizar consultas en la DB interna del dispositivo movil(SQLite).
     * @return retorna el RoadSegment en el que se encuentra el dispositivo movil.
     */
    public static RoadSegment getRoadSegmentParking(double currentLatitude, double currentLongitude, OffStreetParking offStreetParking, SQLiteDrivingApp sqLiteDrivingApp){
        RoadSegment auxRoadSegment = null;
        List<LatLng> polyline; //obtiene la polilinea del RoadSegment.
        ArrayList<Road> listRoadByResponsible = sqLiteDrivingApp.getRoadByResponsible(offStreetParking.getIdOffStreetParking()); //obtiene la lista de los road por el responsable.
        ArrayList<RoadSegment> listRoadSegmentByRefRoad;//obtendra la lista de los roadSegment de acuerdo a los Road.
        LatLng point = new LatLng(currentLatitude,currentLongitude);
        Log.d("ROADS", "" +listRoadByResponsible.size());
        if( listRoadByResponsible.size() > 0){
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            for(int i=0; i<listRoadByResponsible.size(); i++) {
                //Obtiene los Road segment correspondiente a un determinado Road.
                listRoadSegmentByRefRoad = sqLiteDrivingApp.getAllRoadSegmentByRefRoad(listRoadByResponsible.get(i).getIdRoad());
                //listRoadSegmentByRefRoad = sqLiteDrivingApp.getAllRoadSegmentByRefRoad(offStreetParking.getIdOffStreetParking());
                Log.d("ROADSEGMENT", ""+ listRoadSegmentByRefRoad.size());
                if (listRoadSegmentByRefRoad.size() > 0) {
                    for (RoadSegment iteratorRoadSegment : listRoadSegmentByRefRoad) {
                        polyline = new ArrayList<>();
                        RoadSegment roadSegment = new RoadSegment();
                        roadSegment.setIdRoadSegment(iteratorRoadSegment.getIdRoadSegment());
                        roadSegment.setType(iteratorRoadSegment.getType());
                        roadSegment.setName(iteratorRoadSegment.getName());
                        roadSegment.setLocation(iteratorRoadSegment.getLocation());
                        roadSegment.setRefRoad(iteratorRoadSegment.getRefRoad());
                        roadSegment.setStartPoint(iteratorRoadSegment.getStartPoint());
                        roadSegment.setEndPoint(iteratorRoadSegment.getEndPoint());
                        roadSegment.setTotalLaneNumber(iteratorRoadSegment.getTotalLaneNumber());
                        roadSegment.setMaximumAllowedSpeed(iteratorRoadSegment.getMaximumAllowedSpeed());
                        roadSegment.setMinimumAllowedSpeed(iteratorRoadSegment.getMinimumAllowedSpeed());
                        roadSegment.setLaneUsage(iteratorRoadSegment.getLaneUsage());
                        roadSegment.setWidth(iteratorRoadSegment.getWidth());
                        roadSegment.setStatus(iteratorRoadSegment.getStatus());
                        try {
                            arrayLocation = new JSONArray(roadSegment.getLocation());
                            for (int j = 0; j < arrayLocation.length(); j++) {
                                originalString = arrayLocation.get(j).toString();
                                clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                                subString = clearString.split(",");
                                latitude = Double.parseDouble(subString[0]);
                                longitude = Double.parseDouble(subString[1]);
                                polyline.add(new LatLng(latitude, longitude));
                            }

                            if (EventsFuntions.detectRoadSegment(point, polyline, roadSegment.getWidth()) == true) {
                                auxRoadSegment = roadSegment;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        }



        return auxRoadSegment;
    }

    /**
     * Used to detect the road segment inside the zone where the user is
     * @param currentLatitude latitude actual en la que se encuentra el dispositivo.
     * @param currentLongitude longitude actual en la que se encuentra el dispositivo.
     * @param currentZone el identificador de la zona en la que se encuentra el dispositivo movil.
     * @param sqLiteDrivingApp objeto para realizar consultas en la DB interna del dispositivo movil(SQLite).
     * @return retorna el RoadSegment en el que se encuentra el dispositivo movil.
     */
    public static RoadSegment getRoadSegmentZone(double currentLatitude, double currentLongitude, String currentZone, SQLiteDrivingApp sqLiteDrivingApp){
        RoadSegment auxRoadSegment = null;
        List<LatLng> polyline; //obtiene la polilinea del RoadSegment.
        ArrayList<Road> listRoadByResponsible = sqLiteDrivingApp.getRoadByResponsible(currentZone); //obtiene la lista de los road por el responsable.
        ArrayList<RoadSegment> listRoadSegmentByRefRoad;//obtendra la lista de los roadSegment de acuerdo a los Road.
        LatLng point = new LatLng(currentLatitude,currentLongitude);
        if(listRoadByResponsible.size() > 0){
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            for(int i=0; i<listRoadByResponsible.size(); i++){
                //Obtiene los Road segment correspondiente a un determinado Road.
                listRoadSegmentByRefRoad = sqLiteDrivingApp.getAllRoadSegmentByRefRoad(listRoadByResponsible.get(i).getIdRoad());
                if(listRoadSegmentByRefRoad.size() > 0){
                    for (RoadSegment iteratorRoadSegment: listRoadSegmentByRefRoad){
                        polyline = new ArrayList<>();
                        RoadSegment roadSegment = new RoadSegment();
                        roadSegment.setIdRoadSegment(iteratorRoadSegment.getIdRoadSegment());
                        roadSegment.setType(iteratorRoadSegment.getType());
                        roadSegment.setName(iteratorRoadSegment.getName());
                        roadSegment.setLocation(iteratorRoadSegment.getLocation());
                        roadSegment.setRefRoad(iteratorRoadSegment.getRefRoad());
                        roadSegment.setStartPoint(iteratorRoadSegment.getStartPoint());
                        roadSegment.setEndPoint(iteratorRoadSegment.getEndPoint());
                        roadSegment.setTotalLaneNumber(iteratorRoadSegment.getTotalLaneNumber());
                        roadSegment.setMaximumAllowedSpeed(iteratorRoadSegment.getMaximumAllowedSpeed());
                        roadSegment.setMinimumAllowedSpeed(iteratorRoadSegment.getMinimumAllowedSpeed());
                        roadSegment.setLaneUsage(iteratorRoadSegment.getLaneUsage());
                        roadSegment.setWidth(iteratorRoadSegment.getWidth());
                        roadSegment.setStatus(iteratorRoadSegment.getStatus());
                        //Log.i("STATUS: ","LOCATION: "+iteratorRoadSegment.getLocation());
                        try {
                            arrayLocation = new JSONArray(roadSegment.getLocation());
                            for (int j=0; j<arrayLocation.length(); j++){
                                originalString = arrayLocation.get(j).toString();
                                clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                                subString =  clearString.split(",");
                                latitude = Double.parseDouble(subString[0]);
                                longitude = Double.parseDouble(subString[1]);
                                polyline.add(new LatLng(latitude, longitude));
                            }

                            if (EventsFuntions.detectRoadSegment(point, polyline,roadSegment.getWidth()) == true){
                                Log.i("STATUS: ","Se encuentra en el RoadSegment: "+roadSegment.getIdRoadSegment()+" --------------------------------------");
                                auxRoadSegment =roadSegment;
                                return auxRoadSegment;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
        return auxRoadSegment;
    }

    /**
     * Used to determinate if the user is in te roadSegment
     * @param point
     * @param polyline
     * @param tolerance
     * @return
     */
    public static boolean detectRoadSegment(LatLng point, List<LatLng> polyline, double tolerance){
        return PolyUtil.isLocationOnPath(point, polyline, false, tolerance);
    }


}
