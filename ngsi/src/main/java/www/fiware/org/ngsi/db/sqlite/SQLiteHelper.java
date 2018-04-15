package www.fiware.org.ngsi.db.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import www.fiware.org.ngsi.db.sqlite.entity.Tbl_Data_Sensor;
import www.fiware.org.ngsi.db.sqlite.entity.Tbl_Data_Temp;
import www.fiware.org.ngsi.db.sqlite.entity.Tbl_Validate;

/**
 * Created by Cipriano on 10/18/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Fiware.db";

    interface  Tables{
        String TBL_VALIDATE = "tbl_validate";
        String TBL_DATA_TEMP_CREATE = "tbl_data_temp_create";
        String TBL_DATA_TEMP_UPDATE = "tbl_data_temp_update";
        String TBL_DATA_SENSOR = "tbl_data_sensor";
    }

    //Campos de la tabla tbl_validate:
    public static final String TBL_VALIDATE_ID = "id";
    public static final String TBL_VALIDATE_NAME = "name";
    public static final String TBL_VALIDATE_STATUS = "status";

    //Campos de la tabla tbl_data_temp_create
    public static final String TBL_DATA_TEMP_CREATE_ID = "id";
    public static final String TBL_DATA_TEMP_CREATE_KEYWORD = "keyword";
    public static final String TBL_DATA_TEMP_CREATE_JSON = "json";
    public static final String TBL_DATA_TEMP_CREATE_STATUS = "status";

    //Campos de la tabla tbl_data_temp_update
    public static final String TBL_DATA_TEMP_UPDATE_ID = "id";
    public static final String TBL_DATA_TEMP_UPDATE_KEYWORD = "keyword";
    public static final String TBL_DATA_TEMP_UPDATE_JSON = "json";
    public static final String TBL_DATA_TEMP_UPDATE_STATUS = "status";

    //campos de la tabla tbl_data_sensor
    public static final String TBL_DATA_SENSOR_ID = "id";
    public static final String TBL_DATA_SENSOR_KEYWORD = "keyword";
    public static final String TBL_DATA_SENSOR_X = "x";
    public static final String TBL_DATA_SENSOR_Y = "y";
    public static final String TBL_DATA_SENSOR_Z = "z";
    public static final String TBL_DATA_SENSOR_JSON = "json";
    public static final String TBL_DATA_SENSOR_STATUS = "status";


    //Sentencia SQL para crear la tabla validate.
    String sqlCreateTblValidate = "CREATE TABLE "+ Tables.TBL_VALIDATE +" ("+ TBL_VALIDATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ TBL_VALIDATE_NAME +" TEXT NOT NULL, "+ TBL_VALIDATE_STATUS +" TEXT)";

    //Sentencia SQL para crear la tabla tbl_data_temp_create
    String sqlCreateTblDataTempCreate = "CREATE TABLE "+ Tables.TBL_DATA_TEMP_CREATE +" ("+ TBL_DATA_TEMP_CREATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ TBL_DATA_TEMP_CREATE_KEYWORD +" TEXT NOT NULL, "+ TBL_DATA_TEMP_CREATE_JSON +" TEXT NOT NULL, "+ TBL_DATA_TEMP_CREATE_STATUS +" VARCHAR(2))";

    //Sentencia SQL para crear la tabla tbl_data_temp_update
    String sqlCreateTblDataTempUpdate = "CREATE TABLE "+ Tables.TBL_DATA_TEMP_UPDATE +" ("+ TBL_DATA_TEMP_UPDATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ TBL_DATA_TEMP_UPDATE_KEYWORD +" TEXT NOT NULL, "+ TBL_DATA_TEMP_UPDATE_JSON +" TEXT NOT NULL, "+ TBL_DATA_TEMP_UPDATE_STATUS +" VARCHAR(2))";

    //Sentencia SQL para crear la tabla tbl_data_sensor
    //String sqlCreateTblDataSensor = "CREATE TABLE "+ Tables.TBL_DATA_SENSOR +" ("+ TBL_DATA_TEMP_UPDATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ TBL_DATA_TEMP_UPDATE_KEYWORD +" TEXT NOT NULL, "+ TBL_DATA_TEMP_UPDATE_JSON +" TEXT NOT NULL, "+ TBL_DATA_TEMP_UPDATE_STATUS +" VARCHAR(2))";
    String sqlCreateTblDataSensor = "CREATE TABLE "+ Tables.TBL_DATA_SENSOR +" ("+ TBL_DATA_SENSOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ TBL_DATA_SENSOR_KEYWORD +" TEXT NOT NULL, "+TBL_DATA_SENSOR_X+" REAL, "+TBL_DATA_SENSOR_Y+" REAL, "+TBL_DATA_SENSOR_Z+" REAL, "+ TBL_DATA_SENSOR_JSON +" TEXT NOT NULL, "+ TBL_DATA_SENSOR_STATUS +" VARCHAR(2))";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("SQL-Query-Up: ", sqlCreateTblDataTempUpdate);
        Log.i("SQL-Query: ", sqlCreateTblDataSensor);
    }


   /* public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTblValidate);
        db.execSQL(sqlCreateTblDataTempCreate);
        db.execSQL(sqlCreateTblDataTempUpdate);
        db.execSQL(sqlCreateTblDataSensor);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int preVersion, int newVersion) {
        /*if(newVersion>preVersion)
            copyDatabase();
        }*/
        //Se elimina la versión anterior de la tabla;
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_VALIDATE);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_DATA_TEMP_CREATE);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_DATA_TEMP_UPDATE);
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_DATA_SENSOR);
        //Se crea la nueva versión de la tabla.
        db.execSQL(sqlCreateTblValidate);
        db.execSQL(sqlCreateTblDataTempCreate);
        db.execSQL(sqlCreateTblDataTempUpdate);
        db.execSQL(sqlCreateTblDataSensor);
    }


    /**
     * Elimina la base de datos.
     * @param context el contexto de donde se desea eliminar a DB.
     */
    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }


//--------------------------------METODOS PARA LA TABLA TBL_DATA_TEMP_CREATE------------------------------------------------//
    /**
     * @param dataTemp el objeto que sera insertado en la DB
     * @return verdadero si el objeto se inserto correctamente en caso contrario retorna false.
     */
    public boolean createTempCreate(Tbl_Data_Temp dataTemp){
        boolean band = false;
        if(dataTemp == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_DATA_TEMP_CREATE_KEYWORD, dataTemp.getKeyword());
            values.put(TBL_DATA_TEMP_CREATE_JSON, dataTemp.getJson());
            dataTemp.setStatus("0");
            values.put(TBL_DATA_TEMP_CREATE_STATUS, dataTemp.getStatus());
            if (dataTemp.getKeyword() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_DATA_TEMP_CREATE, null, values);
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

    /**
     * @param dataTemp el objeto con el que se realizara la busqueda en este caso es con el atributo keyword del objeto.
     * @return el objeto en caso de que se encuentre, en caso sontrario retorna null.
     */
    public Tbl_Data_Temp getByKeywordTempCreate(Tbl_Data_Temp dataTemp){
        if (dataTemp == null) {
            dataTemp = null;
        }else{
            SQLiteDatabase db = this.getReadableDatabase();
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TBL_DATA_TEMP_CREATE_ID,
                    TBL_DATA_TEMP_CREATE_KEYWORD,
                    TBL_DATA_TEMP_CREATE_JSON,
                    TBL_DATA_TEMP_CREATE_STATUS
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = TBL_DATA_TEMP_CREATE_KEYWORD + " = ?";
            String[] selectionArgs = {dataTemp.getKeyword()};

            Cursor cursor = db.query(
                    Tables.TBL_DATA_TEMP_CREATE,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                       // The sort order
            );

            if(cursor != null) {
                if(cursor.moveToFirst()){
                    dataTemp.setId(cursor.getInt(0));
                    dataTemp.setKeyword(cursor.getString(1));
                    dataTemp.setJson(cursor.getString(2));
                    dataTemp.setStatus(cursor.getString(3));
                    Log.i("Details of the query: ", "Id:" + dataTemp.getId() + " keyword:" + dataTemp.getKeyword() +" json:"+dataTemp.getJson()+" Status:" + dataTemp.getStatus());
                }else{
                    dataTemp = null;
                }
            }else{
                dataTemp = null;
            }
            db.close();
        }
        return dataTemp;
    }

    public Tbl_Data_Temp getByKeywordAndStatusActiveTempCreate(Tbl_Data_Temp dataTemp){
        if (dataTemp == null) {
            dataTemp = null;
        }else{
            SQLiteDatabase db = this.getReadableDatabase();
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TBL_DATA_TEMP_CREATE_ID,
                    TBL_DATA_TEMP_CREATE_KEYWORD,
                    TBL_DATA_TEMP_CREATE_JSON,
                    TBL_DATA_TEMP_CREATE_STATUS
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = TBL_DATA_TEMP_CREATE_KEYWORD + " = ? AND "+ TBL_DATA_TEMP_CREATE_STATUS + "= 1";
            String[] selectionArgs = {dataTemp.getKeyword()};

            Cursor cursor = db.query(
                    Tables.TBL_DATA_TEMP_CREATE,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                       // The sort order
            );

            if(cursor != null) {
                if(cursor.moveToFirst()){
                    dataTemp.setId(cursor.getInt(0));
                    dataTemp.setKeyword(cursor.getString(1));
                    dataTemp.setJson(cursor.getString(2));
                    dataTemp.setStatus(cursor.getString(3));
                    //Log.i("Details of the query: ", "Id:" + dataTemp.getId() + " keyword:" + dataTemp.getKeyword() +" json:"+dataTemp.getJson()+" Status:" + dataTemp.getStatus());
                }else{
                    dataTemp = null;
                }
            }else{
                dataTemp = null;
            }
            db.close();
        }
        return dataTemp;
    }

    public Tbl_Data_Temp getByKeywordAndStatusInactiveTempCreate(Tbl_Data_Temp dataTemp){
        if (dataTemp == null) {
            dataTemp = null;
        }else{
            SQLiteDatabase db = this.getReadableDatabase();
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TBL_DATA_TEMP_CREATE_ID,
                    TBL_DATA_TEMP_CREATE_KEYWORD,
                    TBL_DATA_TEMP_CREATE_JSON,
                    TBL_DATA_TEMP_CREATE_STATUS
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = TBL_DATA_TEMP_CREATE_KEYWORD + " = ? AND "+ TBL_DATA_TEMP_CREATE_STATUS + "= 0";
            String[] selectionArgs = {dataTemp.getKeyword()};

            Cursor cursor = db.query(
                    Tables.TBL_DATA_TEMP_CREATE,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                       // The sort order
            );

            if(cursor != null) {
                if(cursor.moveToFirst()){
                    dataTemp.setId(cursor.getInt(0));
                    dataTemp.setKeyword(cursor.getString(1));
                    dataTemp.setJson(cursor.getString(2));
                    dataTemp.setStatus(cursor.getString(3));
                    //Log.i("Details of the query: ", "Id:" + dataTemp.getId() + " keyword:" + dataTemp.getKeyword() +" json:"+dataTemp.getJson()+" Status:" + dataTemp.getStatus());
                }else{
                    dataTemp = null;
                }
            }else{
                dataTemp = null;
            }
            db.close();
        }
        return dataTemp;
    }

    public boolean updateStatusActiveByKeywordTempCreate(String keyword){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        String status = "1";
        values.put(TBL_DATA_TEMP_CREATE_STATUS, status);

        // Which row to update, based on the title
        String selection = TBL_DATA_TEMP_CREATE_KEYWORD + " LIKE ?";
        String [] selectionArgs = {keyword};

        int count = db.update(
                Tables.TBL_DATA_TEMP_CREATE,
                values,
                selection,
                selectionArgs);
        Log.i("Update By Keyword: ", "" + count);
        if(count >= 1){
            band = true;
        }else{
            band = false;
        }
        db.close();

        return band;
    }

    /**
     * Cambia status a 0
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusInactiveByIdTempCreate(Tbl_Data_Temp dataTemp){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        dataTemp.setStatus("0");
        values.put(TBL_DATA_TEMP_CREATE_STATUS, dataTemp.getStatus());

        // Which row to update, based on the title
        String selection = TBL_DATA_TEMP_CREATE_ID + " LIKE ?";
        String [] selectionArgs = {""+dataTemp.getId()};

        int count = db.update(
                Tables.TBL_DATA_TEMP_CREATE,
                values,
                selection,
                selectionArgs);
        Log.i("Result Update: ", "" + count);
        if(count >= 1){
            band = true;
        }else{
            band = false;
        }
        db.close();

        return band;
    }
    /**
     * Cambia status a 1
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusActiveByIdTempCreate(Tbl_Data_Temp dataTemp){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        dataTemp.setStatus("1");
        values.put(TBL_DATA_TEMP_CREATE_STATUS, dataTemp.getStatus());

        // Which row to update, based on the title
        String selection = TBL_DATA_TEMP_CREATE_ID + " LIKE ?";
        String [] selectionArgs = {""+dataTemp.getId()};

        int count = db.update(
                Tables.TBL_DATA_TEMP_CREATE,
                values,
                selection,
                selectionArgs);
        Log.i("Result Update: ", "" + count);
        if(count >= 1){
            band = true;
        }else{
            band = false;
        }
        db.close();

        return band;
    }

    public boolean deleteByKeywordTemCreate(Tbl_Data_Temp dataTemp){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        try{
            // Define 'where' part of query.
            String selection = TBL_DATA_TEMP_CREATE_KEYWORD + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = {dataTemp.getKeyword()};
            // Issue SQL statement.
            int count = db.delete(Tables.TBL_DATA_TEMP_CREATE, selection, selectionArgs);
            Log.i("Delete codigo", ""+count);
            if(count >= 1){
                band = true;
            }else{
                band = false;
            }
            db.close();
        }catch (Exception ex){
            band = false;
        }

        return band;
    }

    /**
     * Elimina elimentos de la tabla con status 0.
     * @return verdadero si los datos son eliminados en caso contrario false.
     */
    public boolean deleteByStatusInactiveTempCreate(){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        try{
            // Define 'where' part of query.
            String selection = TBL_DATA_TEMP_CREATE_STATUS + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = {"0"};
            // Issue SQL statement.
            int count = db.delete(Tables.TBL_DATA_TEMP_CREATE, selection, selectionArgs);
            Log.i("Delete codigo", ""+count);
            if(count >= 1){
                band = true;
            }else{
                band = false;
            }
            db.close();
        }catch (Exception ex){
            band = false;
        }

        return band;
    }

    /**
     * Obtiene todos los datos de la tabla.
     * @return la lista con su respectiva información.
     */
    // Getting All Tbl_Data_Temp_Create
    public ArrayList<Tbl_Data_Temp> getAllTempCreate() {
        ArrayList<Tbl_Data_Temp> list = new ArrayList<Tbl_Data_Temp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_DATA_TEMP_CREATE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tbl_Data_Temp dataTemp = new Tbl_Data_Temp();
                dataTemp.setId(cursor.getInt(0));
                dataTemp.setKeyword(cursor.getString(1));
                dataTemp.setJson(cursor.getString(2));
                dataTemp.setStatus(cursor.getString(3));
                // Adding contact to list
                list.add(dataTemp);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    public ArrayList<Tbl_Data_Temp> getAllByStatusInactiveTempCreate() {
        ArrayList<Tbl_Data_Temp> list = new ArrayList<Tbl_Data_Temp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_DATA_TEMP_CREATE + " WHERE "+ TBL_DATA_TEMP_CREATE_STATUS + " = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tbl_Data_Temp dataTemp = new Tbl_Data_Temp();
                dataTemp.setId(cursor.getInt(0));
                dataTemp.setKeyword(cursor.getString(1));
                dataTemp.setJson(cursor.getString(2));
                dataTemp.setStatus(cursor.getString(3));
                // Adding contact to list
                list.add(dataTemp);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

//--------------------------------METODOS PARA LA TABLA TBL_DATA_TEMP_UPDATE------------------------------------------------//
    /**
     * @param dataTemp el objeto que sera insertado en la DB
     * @return verdadero si el objeto se inserto correctamente en caso contrario retorna false.
     */
    public boolean createTempUpdate(Tbl_Data_Temp dataTemp){
        boolean band = false;
        if(dataTemp == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_DATA_TEMP_UPDATE_KEYWORD, dataTemp.getKeyword());
            values.put(TBL_DATA_TEMP_UPDATE_JSON, dataTemp.getJson());
            dataTemp.setStatus("0");
            values.put(TBL_DATA_TEMP_UPDATE_STATUS, dataTemp.getStatus());
            if (dataTemp.getKeyword() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_DATA_TEMP_UPDATE, null, values);
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

    /**
     * Cambia status a 0
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusInactiveByIdTempUpdate(Tbl_Data_Temp dataTemp){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        dataTemp.setStatus("0");
        values.put(TBL_DATA_TEMP_UPDATE_STATUS, dataTemp.getStatus());

        // Which row to update, based on the title
        String selection = TBL_DATA_TEMP_UPDATE_ID + " LIKE ?";
        String [] selectionArgs = {""+dataTemp.getId()};

        int count = db.update(
                Tables.TBL_DATA_TEMP_UPDATE,
                values,
                selection,
                selectionArgs);
        Log.i("Result Update: ", "" + count);
        if(count >= 1){
            band = true;
        }else{
            band = false;
        }
        db.close();

        return band;
    }
    /**
     * Cambia status a 1
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusActiveByIdTempUpdate(Tbl_Data_Temp dataTemp){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        dataTemp.setStatus("1");
        values.put(TBL_DATA_TEMP_UPDATE_STATUS, dataTemp.getStatus());

        // Which row to update, based on the title
        String selection = TBL_DATA_TEMP_UPDATE_ID + " LIKE ?";
        String [] selectionArgs = {""+dataTemp.getId()};

        int count = db.update(
                Tables.TBL_DATA_TEMP_UPDATE,
                values,
                selection,
                selectionArgs);
        Log.i("Result Update: ", "" + count);
        if(count >= 1){
            band = true;
        }else{
            band = false;
        }
        db.close();

        return band;
    }

    /**
     * Elimina elimentos de la tabla con status 1.
     * @return verdadero si los datos son eliminados en caso contrario false.
     */
    public boolean deleteByStatusActiveTempUpdate(){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        try{
            // Define 'where' part of query.
            String selection = TBL_DATA_TEMP_UPDATE_STATUS + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = {"1"};
            // Issue SQL statement.
            int count = db.delete(Tables.TBL_DATA_TEMP_UPDATE, selection, selectionArgs);
            Log.i("Delete codigo", ""+count);
            if(count >= 1){
                band = true;
            }else{
                band = false;
            }
            db.close();
        }catch (Exception ex){
            band = false;
        }

        return band;
    }

    /**
     * Obtiene todos los datos de la tabla.
     * @return la lista con su respectiva información.
     */
    // Getting All Tbl_Data_Temp_Create
    public ArrayList<Tbl_Data_Temp> getAllTempUpdate() {
        ArrayList<Tbl_Data_Temp> list = new ArrayList<Tbl_Data_Temp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_DATA_TEMP_UPDATE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tbl_Data_Temp dataTemp = new Tbl_Data_Temp();
                dataTemp.setId(cursor.getInt(0));
                dataTemp.setKeyword(cursor.getString(1));
                dataTemp.setJson(cursor.getString(2));
                dataTemp.setStatus(cursor.getString(3));
                // Adding contact to list
                list.add(dataTemp);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    public ArrayList<Tbl_Data_Temp> getAllStatusInactiveTempUpdate() {
        ArrayList<Tbl_Data_Temp> list = new ArrayList<Tbl_Data_Temp>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_DATA_TEMP_UPDATE +" WHERE "+ TBL_DATA_TEMP_UPDATE_STATUS +" = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tbl_Data_Temp dataTemp = new Tbl_Data_Temp();
                dataTemp.setId(cursor.getInt(0));
                dataTemp.setKeyword(cursor.getString(1));
                dataTemp.setJson(cursor.getString(2));
                dataTemp.setStatus(cursor.getString(3));
                // Adding contact to list
                list.add(dataTemp);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }
    //--------------------------Gestios de la tbl_data_sensor-----------------------------------------//
    /**
     * @param dataSensor el objeto que sera insertado en la DB
     * @return verdadero si el objeto se inserto correctamente en caso contrario retorna false.
     */

    public boolean createDataSensor(Tbl_Data_Sensor dataSensor){
        boolean band = false;
        if(dataSensor == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_DATA_SENSOR_KEYWORD, dataSensor.getKeyword());
            values.put(TBL_DATA_SENSOR_X, ""+dataSensor.getX());
            values.put(TBL_DATA_SENSOR_Y, ""+dataSensor.getY());
            values.put(TBL_DATA_SENSOR_Z, ""+dataSensor.getZ());
            values.put(TBL_DATA_SENSOR_JSON, dataSensor.getJson());
            dataSensor.setStatus("0");
            values.put(TBL_DATA_SENSOR_STATUS, dataSensor.getStatus());
            if (dataSensor.getKeyword() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_DATA_SENSOR, null, values);
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

    /**
     * Obtiene todos los datos de la tabla.
     * @return la lista con su respectiva información.
     */
    public ArrayList<Tbl_Data_Sensor> getAllDataSensor() {
        ArrayList<Tbl_Data_Sensor> list = new ArrayList<Tbl_Data_Sensor>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_DATA_SENSOR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tbl_Data_Sensor dataSensor = new Tbl_Data_Sensor();
                dataSensor.setId(cursor.getInt(0));
                dataSensor.setKeyword(cursor.getString(1));
                dataSensor.setX(cursor.getDouble(2));
                dataSensor.setY(cursor.getDouble(3));
                dataSensor.setZ(cursor.getDouble(4));
                dataSensor.setJson(cursor.getString(5));
                dataSensor.setStatus(cursor.getString(6));
                // Adding contact to list
                list.add(dataSensor);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

//--------------------------------METODOS PARA LA TABLA VALIDATEDATA------------------------------------------------//
    public boolean createValidateData(Tbl_Validate validate){
        boolean band = false;
        if(validate == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_VALIDATE_NAME, validate.getName());
            validate.setStatus("1");
            values.put(TBL_VALIDATE_STATUS, validate.getStatus());
            if (validate.getName() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_VALIDATE, null, values);
                //Log.i("Result Insert: ", "" + newRowId);
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

    public Tbl_Validate getByNameValidate(Tbl_Validate validate){
        if (validate == null) {
            validate = null;
        }else{
            SQLiteDatabase db = this.getReadableDatabase();
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TBL_VALIDATE_ID,
                    TBL_VALIDATE_NAME,
                    TBL_VALIDATE_STATUS
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = TBL_VALIDATE_NAME + " = ?";
            String[] selectionArgs = {validate.getName()};

            Cursor cursor = db.query(
                    Tables.TBL_VALIDATE,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                       // The sort order
            );

            if(cursor != null) {
                if(cursor.moveToFirst()){
                    validate.setId(cursor.getInt(0));
                    validate.setName(cursor.getString(1));
                    validate.setStatus(cursor.getString(2));
                    //Log.i("Details of the query: ", "Id:" + validate.getId() + " Name:" + validate.getName() + " Status:" + validate.getStatus());
                }else{
                    validate = null;
                }
            }else{
                validate = null;
            }
            db.close();
        }
        return validate;
    }

    public boolean updateStatusInactiveValidate(Tbl_Validate validate){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        validate.setStatus("0");
        values.put(TBL_VALIDATE_STATUS, validate.getStatus());

        // Which row to update, based on the title
        String selection = TBL_VALIDATE_NAME + " LIKE ?";
        String[] selectionArgs = {validate.getName()};

        int count = db.update(
                Tables.TBL_VALIDATE,
                values,
                selection,
                selectionArgs);
        Log.i("Result Update: ", "" + count);
        if(count >= 1){
            band = true;
        }else{
            band = false;
        }
        db.close();

        return band;
    }

    public boolean updateStatusActiveValidate(Tbl_Validate validate){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        validate.setStatus("1");
        values.put(TBL_VALIDATE_STATUS, validate.getStatus());

        // Which row to update, based on the title
        String selection = TBL_VALIDATE_NAME + " LIKE ?";
        String[] selectionArgs = {validate.getName()};

        int count = db.update(
                Tables.TBL_VALIDATE,
                values,
                selection,
                selectionArgs);
        Log.i("Result Update: ", "" + count);
        if(count >= 1){
            band = true;
        }else{
            band = false;
        }
        db.close();

        return band;
    }


    public boolean deleteValidate(Tbl_Validate validate){
        boolean band = false;
        SQLiteDatabase db = this.getReadableDatabase();

        try{
            // Define 'where' part of query.
            String selection = TBL_VALIDATE_NAME + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = {validate.getName()};
            // Issue SQL statement.
            int count = db.delete(Tables.TBL_VALIDATE, selection, selectionArgs);
            Log.i("Delete codigo", ""+count);
            if(count >= 1){
                band = true;
            }else{
                band = false;
            }
            db.close();
        }catch (Exception ex){
            band = false;
        }

        return band;
    }

    // Getting All Valiate
    public ArrayList<Tbl_Validate> getAllValidate() {
        ArrayList<Tbl_Validate> list = new ArrayList<Tbl_Validate>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_VALIDATE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tbl_Validate validate = new Tbl_Validate();
                validate.setId(cursor.getInt(0));
                validate.setName(cursor.getString(1));
                validate.setStatus(cursor.getString(2));
                // Adding contact to list
                list.add(validate);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

}
