package mx.edu.cenidet.cenidetsdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.entities.Campus;
import www.fiware.org.ngsi.datamodel.entity.Zone;

/**
 * Created by Cipriano on 3/17/2018.
 */

public class SQLiteDrivingApp extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "db_driving.db";

    interface  Tables{
        String TBL_CAMPUS = "tbl_campus";
        String TBL_ZONE = "tbl_zone";
    }

    //Campos de la tabla tbl_campus:
    public static final String TBL_CAMPUS_ID = "id";
    public static final String TBL_CAMPUS_TYPE = "type";
    public static final String TBL_CAMPUS_NAME = "name";
    public static final String TBL_CAMPUS_ADDRESS = "address";
    public static final String TBL_CAMPUS_LOCATION = "location";
    public static final String TBL_CAMPUS_POINTMAP = "pointMap";
    public static final String TBL_CAMPUS_DATECREATED = "dateCreated";
    public static final String TBL_CAMPUS_DATEMODIFIED = "dateModified";
    public static final String TBL_CAMPUS_STATUS = "status";

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

    //Sentencia SQL para crear la tabla tbl_campus
    String sqlCreateTblCampus = "CREATE TABLE "+ Tables.TBL_CAMPUS +" ("+ TBL_CAMPUS_ID + " VARCHAR(200) NOT NULL, "+ TBL_CAMPUS_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_CAMPUS_NAME +" TEXT NOT NULL, "+ TBL_CAMPUS_ADDRESS +" TEXT NOT NULL, "+ TBL_CAMPUS_LOCATION + " TEXT NOT NULL, "+ TBL_CAMPUS_POINTMAP +" TEXT NOT NULL, "+ TBL_CAMPUS_DATECREATED + " TEXT, "+ TBL_CAMPUS_DATEMODIFIED +" TEXT, " + TBL_CAMPUS_STATUS+ " VARCHAR(2))";
    String sqlCreateTblZone = "CREATE TABLE "+ Tables.TBL_ZONE +" ("+ TBL_ZONE_ID + " VARCHAR(200) NOT NULL, "+ TBL_ZONE_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_ZONE_REFBUILDINGTYPE +" TEXT, "+ TBL_ZONE_NAME +" TEXT NOT NULL, "+ TBL_ZONE_ADDRESS + " TEXT NOT NULL, "+ TBL_ZONE_CATEGORY +" TEXT NOT NULL, "+ TBL_ZONE_LOCATION + " TEXT NOT NULL, "+ TBL_ZONE_CENTERPOINT +" TEXT NOT NULL, "+TBL_ZONE_DESCRIPTION+" TEXT, "+TBL_ZONE_DATECREATED+" TEXT, "+TBL_ZONE_DATEMODIFIED+" TEXT, "+ TBL_ZONE_STATUS+ " VARCHAR(2))";

    public SQLiteDrivingApp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("Result Insert: ", "QUERY: " + sqlCreateTblZone);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTblCampus);
        db.execSQL(sqlCreateTblZone);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla;
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_CAMPUS);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_ZONE);
        //Se crea la nueva versión de la tabla.
        db.execSQL(sqlCreateTblCampus);
        db.execSQL(sqlCreateTblZone);
    }

    /**
     * Elimina la base de datos.
     * @param context el contexto de donde se desea eliminar a DB.
     */
    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
    //------------------------Inicio de los metodos para la gestión de Campus------------------------------//
    public boolean createCampus(Campus campus){
        boolean band = false;
        if(campus == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_CAMPUS_ID, campus.getId());
            values.put(TBL_CAMPUS_TYPE, campus.getType());
            values.put(TBL_CAMPUS_NAME, campus.getName());
            values.put(TBL_CAMPUS_ADDRESS, campus.getAddress());
            values.put(TBL_CAMPUS_LOCATION, campus.getLocation());
            values.put(TBL_CAMPUS_POINTMAP, campus.getPointMap());
            values.put(TBL_CAMPUS_DATECREATED, campus.getDateCreated());
            values.put(TBL_CAMPUS_DATEMODIFIED, campus.getDateModified());
            campus.setStatus("0");
            values.put(TBL_CAMPUS_STATUS, campus.getStatus());
            if (campus.getId() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_CAMPUS, null, values);
                Log.i("Result Insert: ", "" + newRowId);
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
    public ArrayList<Campus> getAllCampus() {
        ArrayList<Campus> list = new ArrayList<Campus>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_CAMPUS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Campus campus = new Campus();
                campus.setId(cursor.getString(0));
                campus.setType(cursor.getString(1));
                campus.setName(cursor.getString(2));
                campus.setAddress(cursor.getString(3));
                campus.setLocation(cursor.getString(4));
                campus.setPointMap(cursor.getString(5));
                campus.setDateCreated(cursor.getString(6));
                campus.setDateModified(cursor.getString(7));
                campus.setStatus(cursor.getString(8));
                // Adding to list
                list.add(campus);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
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
            values.put(TBL_ZONE_REFBUILDINGTYPE, zone.getRefBuildingType().getValue());
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
                Log.i("Result Insert: ", "" + newRowId);
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
                zone.getRefBuildingType().setValue(cursor.getString(2));
                zone.getName().setValue(cursor.getString(3));
                zone.getAddress().setValue(cursor.getString(4));
                zone.getCategory().setValue(cursor.getString(5));
                zone.getLocation().setValue(cursor.getString(6));
                zone.getCenterPoint().setValue(cursor.getString(7));
                zone.getDescription().setValue(cursor.getString(8));
                zone.getDateCreated().setValue(cursor.getString(9));
                zone.getDateModified().setValue(cursor.getString(10));
                zone.getStatus().setValue(cursor.getString(11));
                // Adding to list
                list.add(zone);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }
}
