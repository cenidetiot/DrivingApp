package mx.edu.cenidet.cenidetsdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.entities.Campus;
import www.fiware.org.ngsi.datamodel.entity.OffStreetParking;
import www.fiware.org.ngsi.datamodel.entity.Road;
import www.fiware.org.ngsi.datamodel.entity.RoadSegment;
import www.fiware.org.ngsi.datamodel.entity.Zone;

/**
 * Created by Cipriano on 3/17/2018.
 */

public class SQLiteDrivingApp extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "db_driving.db";

    interface  Tables{
        //String TBL_CAMPUS = "tbl_campus";
        String TBL_ZONE = "tbl_zone";
        String TBL_ROAD = "tbl_road";
        String TBL_ROAD_SEGMENT = "tbl_road_segment";
        String TBL_PARKING = "tbl_parking";
    }

    //Campos de la tabla tbl_zone
    public static final String TBL_ZONE_ID = "idZone";
    public static final String TBL_ZONE_TYPE = "type";
    public static final String TBL_ZONE_REFBUILDINGTYPE = "refBuildingType";
    public static final String TBL_ZONE_NAME = "name";
    public static final String TBL_ZONE_ADDRESS = "address";
    public static final String TBL_ZONE_CATEGORY = "category";
    public static final String TBL_ZONE_LOCATION = "location";
    public static final String TBL_ZONE_CENTERPOINT = "centerPoint";
    public static final String TBL_ZONE_DESCRIPTION = "description";
    public static final String TBL_ZONE_DATECREATED = "dateCreated";
    public static final String TBL_ZONE_DATEMODIFIED = "dateModified";
    public static final String TBL_ZONE_STATUS = "status";

    //Campos de la tabla tbl_parking
    public static final String TBL_PARKING_ID = "idOffStreetParking";
    public static final String TBL_PARKING_TYPE = "type";
    public static final String TBL_PARKING_NAME = "name";
    public static final String TBL_PARKING_CATEGORY = "category";
    public static final String TBL_PARKING_LOCATION = "location";
    public static final String TBL_PARKING_DESCRIPTION = "description";
    public static final String TBL_PARKING_AREASERVED = "areaServed";
    public static final String TBL_PARKING_STATUS = "status";


    //Campos de la tabla tbl_road
    public static final String TBL_ROAD_ID = "idRoad";
    public static final String TBL_ROAD_TYPE = "type";
    public static final String TBL_ROAD_NAME = "name";
    public static final String TBL_ROAD_DESCRIPTION = "description";
    public static final String TBL_ROAD_RESPONSIBLE = "responsible";
    public static final String TBL_ROAD_STATUS = "status";

    //Campos de la tabla tbl_road_segment
    public static final String TBL_ROAD_SEGMENT_ID = "idRoadSegment";
    public static final String TBL_ROAD_SEGMENT_TYPE = "type";
    public static final String TBL_ROAD_SEGMENT_NAME = "name";
    public static final String TBL_ROAD_SEGMENT_LOCATION = "location";
    public static final String TBL_ROAD_SEGMENT_REFROAD = "refRoad";
    public static final String TBL_ROAD_SEGMENT_STARTPOINT = "startPoint";
    public static final String TBL_ROAD_SEGMENT_ENDPOINT = "endPoint";
    public static final String TBL_ROAD_SEGMENT_TOTALLANENUMBER = "totalLaneNumber";
    public static final String TBL_ROAD_SEGMENT_MAXIMUMALLOWEBSPEED = "maximumAllowedSpeed";
    public static final String TBL_ROAD_SEGMENT_MINIMUMALLOWEDSPEED = "minimumAllowedSpeed";
    public static final String TBL_ROAD_SEGMENT_LANEUSAGE = "laneUsage";
    public static final String TBL_ROAD_SEGMENT_WIDTH = "width";
    public static final String TBL_ROAD_SEGMENT_STATUS = "status";

    //Sentencia SQL para crear la tabla tbl_campus
    //String sqlCreateTblCampus = "CREATE TABLE "+ Tables.TBL_CAMPUS +" ("+ TBL_CAMPUS_ID + " VARCHAR(200) PRIMARY KEY NOT NULL, "+ TBL_CAMPUS_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_CAMPUS_NAME +" TEXT NOT NULL, "+ TBL_CAMPUS_ADDRESS +" TEXT NOT NULL, "+ TBL_CAMPUS_LOCATION + " TEXT NOT NULL, "+ TBL_CAMPUS_POINTMAP +" TEXT NOT NULL, "+ TBL_CAMPUS_DATECREATED + " TEXT, "+ TBL_CAMPUS_DATEMODIFIED +" TEXT, " + TBL_CAMPUS_STATUS+ " VARCHAR(2))";
    String sqlCreateTblZone = "CREATE TABLE "+ Tables.TBL_ZONE +" ("+ TBL_ZONE_ID + " VARCHAR(200) PRIMARY KEY NOT NULL, "+ TBL_ZONE_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_ZONE_REFBUILDINGTYPE +" TEXT, "+ TBL_ZONE_NAME +" TEXT NOT NULL, "+ TBL_ZONE_ADDRESS + " TEXT NOT NULL, "+ TBL_ZONE_CATEGORY +" TEXT NOT NULL, "+ TBL_ZONE_LOCATION + " TEXT NOT NULL, "+ TBL_ZONE_CENTERPOINT +" TEXT NOT NULL, "+TBL_ZONE_DESCRIPTION+" TEXT, "+TBL_ZONE_DATECREATED+" TEXT, "+TBL_ZONE_DATEMODIFIED+" TEXT, "+ TBL_ZONE_STATUS+ " VARCHAR(2))";
    String sqlCreateTblRoad = "CREATE TABLE "+ Tables.TBL_ROAD +" ("+ TBL_ROAD_ID + " VARCHAR(200) PRIMARY KEY NOT NULL, "+ TBL_ROAD_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_ROAD_NAME+" TEXT, "+ TBL_ROAD_DESCRIPTION +" TEXT, "+ TBL_ROAD_RESPONSIBLE + " TEXT NOT NULL, "+ TBL_ROAD_STATUS+ " VARCHAR(2))";
    String sqlCreateTblRoadSegment = "CREATE TABLE "+ Tables.TBL_ROAD_SEGMENT +" ("+ TBL_ROAD_SEGMENT_ID + " VARCHAR(200) PRIMARY KEY NOT NULL, "+ TBL_ROAD_SEGMENT_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_ROAD_SEGMENT_NAME +" TEXT, "+ TBL_ROAD_SEGMENT_LOCATION +" TEXT NOT NULL, "+ TBL_ROAD_SEGMENT_REFROAD + " TEXT NOT NULL, "+ TBL_ROAD_SEGMENT_STARTPOINT +" TEXT NOT NULL, "+ TBL_ROAD_SEGMENT_ENDPOINT + " TEXT NOT NULL, "+ TBL_ROAD_SEGMENT_TOTALLANENUMBER +" INTEGER NOT NULL, "+TBL_ROAD_SEGMENT_MAXIMUMALLOWEBSPEED+" INTEGER NOT NULL, "+TBL_ROAD_SEGMENT_MINIMUMALLOWEDSPEED+" INTEGER NOT NULL, "+TBL_ROAD_SEGMENT_LANEUSAGE+" TEXT NOT NULL, "+TBL_ROAD_SEGMENT_WIDTH+" INTEGER NOT NULL, "+ TBL_ROAD_SEGMENT_STATUS+ " VARCHAR(2))";
    String sqlCreateTblParking = "CREATE TABLE "+ Tables.TBL_PARKING +" ("+ TBL_PARKING_ID + " VARCHAR(200) PRIMARY KEY NOT NULL, "+ TBL_PARKING_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_PARKING_NAME+" TEXT, "+ TBL_PARKING_CATEGORY +" TEXT, "+ TBL_PARKING_LOCATION + " TEXT NOT NULL, "+ TBL_PARKING_DESCRIPTION +" TEXT, "+TBL_PARKING_AREASERVED+" TEXT NOT NULL, "+ TBL_PARKING_STATUS+ " VARCHAR(2))";

    public SQLiteDrivingApp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Log.i("Result Insert: ", "QUERY ZONE:\n " + sqlCreateTblZone);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(sqlCreateTblCampus);
        db.execSQL(sqlCreateTblZone);
        db.execSQL(sqlCreateTblRoad);
        db.execSQL(sqlCreateTblRoadSegment);
        db.execSQL(sqlCreateTblParking);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla;
        //db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_CAMPUS);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_ZONE);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_ROAD);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_ROAD_SEGMENT);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_PARKING);
        //Se crea la nueva versión de la tabla.
        //db.execSQL(sqlCreateTblCampus);
        db.execSQL(sqlCreateTblZone);
        db.execSQL(sqlCreateTblRoad);
        db.execSQL(sqlCreateTblRoadSegment);
        db.execSQL(sqlCreateTblParking);
    }

    /**
     * Elimina la base de datos.
     * @param context el contexto de donde se desea eliminar a DB.
     */
    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    //------------------------Inicio de los metodos para la gestión de Zone------------------------------//
    public boolean createZone(Zone zone){
        boolean band = false;
        if(zone == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_ZONE_ID, zone.getIdZone());
            values.put(TBL_ZONE_TYPE, zone.getType());
            values.put(TBL_ZONE_NAME, zone.getName().getValue());
            values.put(TBL_ZONE_ADDRESS, zone.getAddress().getValue());
            values.put(TBL_ZONE_CATEGORY, zone.getCategory().getValue());
            values.put(TBL_ZONE_LOCATION, zone.getLocation().getValue());
            values.put(TBL_ZONE_CENTERPOINT, zone.getCenterPoint().getValue());
            values.put(TBL_ZONE_DESCRIPTION, zone.getDescription().getValue());
            values.put(TBL_ZONE_DATECREATED, zone.getDateCreated().getValue());
            values.put(TBL_ZONE_DATEMODIFIED, zone.getDateModified().getValue());
            values.put(TBL_ZONE_STATUS, zone.getStatus().getValue());
            if (zone.getIdZone() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_ZONE, null, values);
                Log.i("Result Insert: ", "TBL_ZONE: " + newRowId);
                if (newRowId == -1) {
                    band = false;
                } else {
                    band = true;
                }
            }
            db.close();
        }
        return band;
    }

    public ArrayList<Zone> getAllZone() {
        ArrayList<Zone> list = new ArrayList<Zone>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_ZONE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Zone zone = new Zone();
                zone.setIdZone(cursor.getString(0));
                zone.setType(cursor.getString(1));
                zone.getName().setValue(cursor.getString(2));
                zone.getAddress().setValue(cursor.getString(3));
                zone.getCategory().setValue(cursor.getString(4));
                zone.getLocation().setValue(cursor.getString(5));
                zone.getCenterPoint().setValue(cursor.getString(6));
                zone.getDescription().setValue(cursor.getString(7));
                zone.getDateCreated().setValue(cursor.getString(8));
                zone.getDateModified().setValue(cursor.getString(9));
                zone.getStatus().setValue(cursor.getString(10));
                // Adding to list
                list.add(zone);
            } while (cursor.moveToNext());
        }
        db.close();
        // return list
        return list;
    }

    public Zone getZoneById(String zoneId) {
        Zone zone = new Zone();

        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TBL_ZONE_ID,
                TBL_ZONE_TYPE,
                TBL_ZONE_NAME,
                TBL_ZONE_ADDRESS,
                TBL_ZONE_CATEGORY,
                TBL_ZONE_LOCATION,
                TBL_ZONE_CENTERPOINT,
                TBL_ZONE_DESCRIPTION,
                TBL_ZONE_DATECREATED,
                TBL_ZONE_DATEMODIFIED,
                TBL_ZONE_STATUS
        };
        String selection = TBL_ZONE_ID + " = ?";
        String[] selectionArgs = {zoneId};
        Cursor cursor = db.query(
                Tables.TBL_ZONE,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                       // The sort order
        );

        if(cursor != null) {
            if(cursor.moveToFirst()){
                zone.setIdZone(cursor.getString(0));
                zone.setType(cursor.getString(1));
                zone.getName().setValue(cursor.getString(2));
                zone.getAddress().setValue(cursor.getString(3));
                zone.getCategory().setValue(cursor.getString(4));
                zone.getLocation().setValue(cursor.getString(5));
                zone.getCenterPoint().setValue(cursor.getString(6));
                zone.getDescription().setValue(cursor.getString(7));
                zone.getDateCreated().setValue(cursor.getString(8));
                zone.getDateModified().setValue(cursor.getString(9));
                zone.getStatus().setValue(cursor.getString(10));
            }else {
                zone = null;
            }
        }else {
            zone = null;
        }
        db.close();
        // return objeto
        return zone;
    }

    //------------------------Inicio de los metodos para la gestión de Parking------------------------------//
    public boolean createParking(OffStreetParking offStreetParking){
        boolean band = false;
        if(offStreetParking == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_PARKING_ID, offStreetParking.getIdOffStreetParking());
            values.put(TBL_PARKING_TYPE, offStreetParking.getType());
            values.put(TBL_PARKING_NAME, offStreetParking.getName());
            values.put(TBL_PARKING_CATEGORY, offStreetParking.getCategory());
            values.put(TBL_PARKING_LOCATION, offStreetParking.getLocation());
            values.put(TBL_PARKING_DESCRIPTION, offStreetParking.getDescription());
            values.put(TBL_PARKING_AREASERVED, offStreetParking.getAreaServed());
            values.put(TBL_PARKING_STATUS, offStreetParking.getStatus());
            if (offStreetParking.getIdOffStreetParking() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_PARKING, null, values);
                Log.i("Result Insert: ", "TBL_PARKING: " + newRowId);
                if (newRowId == -1) {
                    band = false;
                } else {
                    band = true;
                }
            }
            db.close();
        }
        return band;
    }

    public ArrayList<OffStreetParking> getAllOffStreetParking() {
        ArrayList<OffStreetParking> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_PARKING;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OffStreetParking offStreetParking = new OffStreetParking();
                offStreetParking.setIdOffStreetParking(cursor.getString(0));
                offStreetParking.setType(cursor.getString(1));
                offStreetParking.setName(cursor.getString(2));
                offStreetParking.setCategory(cursor.getString(3));
                offStreetParking.setLocation(cursor.getString(4));
                offStreetParking.setDescription(cursor.getString(5));
                offStreetParking.setAreaServed(cursor.getString(6));
                offStreetParking.setStatus(cursor.getString(7));
                // Adding to list
                list.add(offStreetParking);
            } while (cursor.moveToNext());
        }
        db.close();
        // return list
        return list;
    }

    public ArrayList<OffStreetParking> getAllOffStreetParkingByAreaServed(String areaServed) {
        ArrayList<OffStreetParking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TBL_PARKING_ID,
                TBL_PARKING_TYPE,
                TBL_PARKING_NAME,
                TBL_PARKING_CATEGORY,
                TBL_PARKING_LOCATION,
                TBL_PARKING_DESCRIPTION,
                TBL_PARKING_AREASERVED,
                TBL_PARKING_STATUS
        };
        String selection = TBL_PARKING_AREASERVED + " = ?";
        String[] selectionArgs = {areaServed};
        Cursor cursor = db.query(
                Tables.TBL_PARKING,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                       // The sort order
        );
        if (cursor.moveToFirst()) {
            do {
                OffStreetParking offStreetParking = new OffStreetParking();
                offStreetParking.setIdOffStreetParking(cursor.getString(0));
                offStreetParking.setType(cursor.getString(1));
                offStreetParking.setName(cursor.getString(2));
                offStreetParking.setCategory(cursor.getString(3));
                offStreetParking.setLocation(cursor.getString(4));
                offStreetParking.setDescription(cursor.getString(5));
                offStreetParking.setAreaServed(cursor.getString(6));
                offStreetParking.setStatus(cursor.getString(7));
                // Adding to list
                list.add(offStreetParking);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public OffStreetParking getOffStreetParkingByAreaServed(String areaServed){
        OffStreetParking offStreetParking = new OffStreetParking();

        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TBL_PARKING_ID,
                TBL_PARKING_TYPE,
                TBL_PARKING_NAME,
                TBL_PARKING_CATEGORY,
                TBL_PARKING_LOCATION,
                TBL_PARKING_DESCRIPTION,
                TBL_PARKING_AREASERVED,
                TBL_PARKING_STATUS
        };

        String selection = TBL_PARKING_AREASERVED + " = ?";
        String[] selectionArgs = {areaServed};
        Cursor cursor = db.query(
                Tables.TBL_PARKING,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                       // The sort order
        );

        if(cursor != null) {
            if(cursor.moveToFirst()){
                offStreetParking.setIdOffStreetParking(cursor.getString(0));
                offStreetParking.setType(cursor.getString(1));
                offStreetParking.setName(cursor.getString(2));
                offStreetParking.setCategory(cursor.getString(3));
                offStreetParking.setLocation(cursor.getString(4));
                offStreetParking.setDescription(cursor.getString(5));
                offStreetParking.setAreaServed(cursor.getString(6));
                offStreetParking.setStatus(cursor.getString(7));
            }else{
                offStreetParking = null;
            }
        }else{
            offStreetParking = null;
        }
        db.close();

        return offStreetParking;
    }

    //------------------------Inicio de los metodos para la gestión de Road------------------------------//
    public boolean createRoad(Road road){
        boolean band = false;
        if(road == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_ROAD_ID, road.getIdRoad());
            values.put(TBL_ROAD_TYPE, road.getType());
            values.put(TBL_ROAD_NAME, road.getName());
            values.put(TBL_ROAD_DESCRIPTION, road.getDescription());
            values.put(TBL_ROAD_RESPONSIBLE, road.getResponsible());
            values.put(TBL_ROAD_STATUS, road.getResponsible());
            if (road.getIdRoad() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_ROAD, null, values);
                Log.i("Result Insert: ", "TBL_ROAD: " + newRowId);
                if (newRowId == -1) {
                    band = false;
                } else {
                    band = true;
                }
            }
            db.close();
        }
        return band;
    }

    public ArrayList<Road> getAllRoad() {
        ArrayList<Road> list = new ArrayList<Road>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_ROAD;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Road road = new Road();
                road.setIdRoad(cursor.getString(0));
                road.setType(cursor.getString(1));
                road.setName(cursor.getString(2));
                road.setDescription(cursor.getString(3));
                road.setResponsible(cursor.getString(4));
                road.setDescription(cursor.getString(5));
                // Adding to list
                list.add(road);
            } while (cursor.moveToNext());
        }
        db.close();
        // return list
        return list;
    }

    public ArrayList<Road> getRoadByResponsible(String responsible) {
        ArrayList<Road> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TBL_ROAD_ID,
                TBL_ROAD_TYPE,
                TBL_ROAD_NAME,
                TBL_ROAD_DESCRIPTION,
                TBL_ROAD_RESPONSIBLE,
                TBL_ROAD_STATUS
        };
        String selection = TBL_ROAD_RESPONSIBLE + " = ?";
        String[] selectionArgs = {responsible};
        Cursor cursor = db.query(
                Tables.TBL_ROAD,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                       // The sort order
        );
        if (cursor.moveToFirst()) {
            do {
                Road road = new Road();
                road.setIdRoad(cursor.getString(0));
                road.setType(cursor.getString(1));
                road.setName(cursor.getString(2));
                road.setDescription(cursor.getString(3));
                road.setResponsible(cursor.getString(4));
                road.setDescription(cursor.getString(5));
                // Adding to list
                list.add(road);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    //------------------------Inicio de los metodos para la gestión de RoadSegment------------------------------//
    public boolean createRoadSegment(RoadSegment roadSegment){
        boolean band = false;
        if(roadSegment == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_ROAD_SEGMENT_ID, roadSegment.getIdRoadSegment());
            values.put(TBL_ROAD_SEGMENT_TYPE, roadSegment.getType());
            values.put(TBL_ROAD_SEGMENT_NAME, roadSegment.getName());
            values.put(TBL_ROAD_SEGMENT_LOCATION, roadSegment.getLocation());
            values.put(TBL_ROAD_SEGMENT_REFROAD, roadSegment.getRefRoad());
            values.put(TBL_ROAD_SEGMENT_STARTPOINT, roadSegment.getStartPoint());
            values.put(TBL_ROAD_SEGMENT_ENDPOINT, roadSegment.getEndPoint());
            values.put(TBL_ROAD_SEGMENT_TOTALLANENUMBER, roadSegment.getTotalLaneNumber());
            values.put(TBL_ROAD_SEGMENT_MAXIMUMALLOWEBSPEED, roadSegment.getMaximumAllowedSpeed());
            values.put(TBL_ROAD_SEGMENT_MINIMUMALLOWEDSPEED, roadSegment.getMinimumAllowedSpeed());
            values.put(TBL_ROAD_SEGMENT_LANEUSAGE, roadSegment.getLaneUsage());
            values.put(TBL_ROAD_SEGMENT_WIDTH, roadSegment.getWidth());
            values.put(TBL_ROAD_SEGMENT_STATUS, roadSegment.getStatus());
            if (roadSegment.getIdRoadSegment() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_ROAD_SEGMENT, null, values);
                Log.i("Result Insert: ", "TBL_ROAD_SEGMENT: " + newRowId);
                if (newRowId == -1) {
                    band = false;
                } else {
                    band = true;
                }
            }
            db.close();
        }
        return band;
    }

    public ArrayList<RoadSegment> getAllRoadSegment() {
        ArrayList<RoadSegment> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_ROAD_SEGMENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RoadSegment roadSegment = new RoadSegment();
                roadSegment.setIdRoadSegment(cursor.getString(0));
                roadSegment.setType(cursor.getString(1));
                roadSegment.setName(cursor.getString(2));
                roadSegment.setLocation(cursor.getString(3));
                roadSegment.setRefRoad(cursor.getString(4));
                roadSegment.setStartPoint(cursor.getString(5));
                roadSegment.setEndPoint(cursor.getString(6));
                roadSegment.setTotalLaneNumber(cursor.getInt(7));
                roadSegment.setMaximumAllowedSpeed(cursor.getInt(8));
                roadSegment.setMinimumAllowedSpeed(cursor.getInt(9));
                roadSegment.setLaneUsage(cursor.getString(10));
                roadSegment.setWidth(cursor.getInt(11));
                roadSegment.setStatus(cursor.getString(12));
                // Adding to list
                list.add(roadSegment);
            } while (cursor.moveToNext());
        }
        db.close();
        // return list
        return list;
    }

    public ArrayList<RoadSegment> getAllRoadSegmentByRefRoad(String refRoad) {
        ArrayList<RoadSegment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TBL_ROAD_SEGMENT_ID,
                TBL_ROAD_SEGMENT_TYPE,
                TBL_ROAD_SEGMENT_NAME,
                TBL_ROAD_SEGMENT_LOCATION,
                TBL_ROAD_SEGMENT_REFROAD,
                TBL_ROAD_SEGMENT_STARTPOINT,
                TBL_ROAD_SEGMENT_ENDPOINT,
                TBL_ROAD_SEGMENT_TOTALLANENUMBER,
                TBL_ROAD_SEGMENT_MAXIMUMALLOWEBSPEED,
                TBL_ROAD_SEGMENT_MINIMUMALLOWEDSPEED,
                TBL_ROAD_SEGMENT_LANEUSAGE,
                TBL_ROAD_SEGMENT_WIDTH,
                TBL_ROAD_SEGMENT_STATUS
        };

        String selection = TBL_ROAD_SEGMENT_REFROAD + " = ?";
        String[] selectionArgs = {refRoad};
        Cursor cursor = db.query(
                Tables.TBL_ROAD_SEGMENT,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                       // The sort order
        );
        if (cursor.moveToFirst()) {
            do {
                RoadSegment roadSegment = new RoadSegment();
                roadSegment.setIdRoadSegment(cursor.getString(0));
                roadSegment.setType(cursor.getString(1));
                roadSegment.setName(cursor.getString(2));
                roadSegment.setLocation(cursor.getString(3));
                roadSegment.setRefRoad(cursor.getString(4));
                roadSegment.setStartPoint(cursor.getString(5));
                roadSegment.setEndPoint(cursor.getString(6));
                roadSegment.setTotalLaneNumber(cursor.getInt(7));
                roadSegment.setMaximumAllowedSpeed(cursor.getInt(8));
                roadSegment.setMinimumAllowedSpeed(cursor.getInt(9));
                roadSegment.setLaneUsage(cursor.getString(10));
                roadSegment.setWidth(cursor.getInt(11));
                roadSegment.setStatus(cursor.getString(12));
                // Adding to list
                list.add(roadSegment);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public RoadSegment getRoadSegmentByRefRoad(String refRoad){
        RoadSegment roadSegment = new RoadSegment();

        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TBL_ROAD_SEGMENT_ID,
                TBL_ROAD_SEGMENT_TYPE,
                TBL_ROAD_SEGMENT_NAME,
                TBL_ROAD_SEGMENT_LOCATION,
                TBL_ROAD_SEGMENT_REFROAD,
                TBL_ROAD_SEGMENT_STARTPOINT,
                TBL_ROAD_SEGMENT_ENDPOINT,
                TBL_ROAD_SEGMENT_TOTALLANENUMBER,
                TBL_ROAD_SEGMENT_MAXIMUMALLOWEBSPEED,
                TBL_ROAD_SEGMENT_MINIMUMALLOWEDSPEED,
                TBL_ROAD_SEGMENT_LANEUSAGE,
                TBL_ROAD_SEGMENT_WIDTH,
                TBL_ROAD_SEGMENT_STATUS
        };

        String selection = TBL_ROAD_SEGMENT_REFROAD + " = ?";
        String[] selectionArgs = {refRoad};
        Cursor cursor = db.query(
                Tables.TBL_ROAD_SEGMENT,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                       // The sort order
        );

        if(cursor != null) {
            if(cursor.moveToFirst()){
                roadSegment.setIdRoadSegment(cursor.getString(0));
                roadSegment.setType(cursor.getString(1));
                roadSegment.setName(cursor.getString(2));
                roadSegment.setLocation(cursor.getString(3));
                roadSegment.setRefRoad(cursor.getString(4));
                roadSegment.setStartPoint(cursor.getString(5));
                roadSegment.setEndPoint(cursor.getString(6));
                roadSegment.setTotalLaneNumber(cursor.getInt(7));
                roadSegment.setMaximumAllowedSpeed(cursor.getInt(8));
                roadSegment.setMinimumAllowedSpeed(cursor.getInt(9));
                roadSegment.setLaneUsage(cursor.getString(10));
                roadSegment.setWidth(cursor.getInt(11));
                roadSegment.setStatus(cursor.getString(12));
            }else{
                roadSegment = null;
            }
        }else{
            roadSegment = null;
        }
        db.close();

        return roadSegment;
    }
}
