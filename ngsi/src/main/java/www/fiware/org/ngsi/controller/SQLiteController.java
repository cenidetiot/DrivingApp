package www.fiware.org.ngsi.controller;

import android.content.Context;

import java.util.ArrayList;

import www.fiware.org.ngsi.db.sqlite.SQLiteHelper;
import www.fiware.org.ngsi.db.sqlite.entity.Tbl_Data_Sensor;
import www.fiware.org.ngsi.db.sqlite.entity.Tbl_Data_Temp;

/**
 * Created by Cipriano on 10/26/2017.
 */

public class SQLiteController {
    Context context;
    SQLiteHelper sqLiteHelper;
    public SQLiteController(Context context){
        sqLiteHelper = new SQLiteHelper(context);
    }

    //--------------------------------METODOS PARA LA TABLA TBL_DATA_TEMP_CREATE------------------------------------------------//
    /**
     * @param dataTemp el objeto que sera insertado en la DB
     * @return verdadero si el objeto se inserto correctamente en caso contrario retorna false.
     */
    public boolean createTempCreate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.createTempCreate(dataTemp);
    }

    /**
     * @param dataTemp el objeto con el que se realizara la busqueda en este caso es con el atributo keyword del objeto.
     * @return el objeto en caso de que se encuentre, en caso sontrario retorna null.
     */
    public Tbl_Data_Temp getByKeywordTempCreate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.getByKeywordTempCreate(dataTemp);
    }

    public Tbl_Data_Temp getByKeywordAndStatusActiveTempCreate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.getByKeywordAndStatusActiveTempCreate(dataTemp);
    }

    public Tbl_Data_Temp getByKeywordAndStatusInactiveTempCreate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.getByKeywordAndStatusInactiveTempCreate(dataTemp);
    }

    public boolean updateStatusActiveByKeywordTempCreate(String keyword){
        return sqLiteHelper.updateStatusActiveByKeywordTempCreate(keyword);
    }

    /**
     * Cambia status a 0
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusInactiveByIdTempCreate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.updateStatusInactiveByIdTempCreate(dataTemp);
    }

    /**
     * Cambia status a 1
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusActiveByIdTempCreate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.updateStatusActiveByIdTempCreate(dataTemp);
    }

    public boolean deleteByKeywordTemCreate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.deleteByKeywordTemCreate(dataTemp);
    }

    /**
     * Elimina elimentos de la tabla con status 0.
     * @return verdadero si los datos son eliminados en caso contrario false.
     */
    public boolean deleteByStatusInactiveTempCreate(){
        return sqLiteHelper.deleteByStatusInactiveTempCreate();
    }

    /**
     * Obtiene todos los datos de la tabla.
     * @return la lista con su respectiva información.
     */
    // Getting All Tbl_Data_Temp_Create
    public ArrayList<Tbl_Data_Temp> getAllTempCreate() {
        return  sqLiteHelper.getAllTempCreate();
    }

    public ArrayList<Tbl_Data_Temp> getAllByStatusInactiveTempCreate() {
        return sqLiteHelper.getAllByStatusInactiveTempCreate();
    }

    //--------------------------------METODOS PARA LA TABLA TBL_DATA_TEMP_UPDATE------------------------------------------------//

    /**
     * @param dataTemp el objeto que sera insertado en la DB
     * @return verdadero si el objeto se inserto correctamente en caso contrario retorna false.
     */
    public boolean createTempUpdate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.createTempUpdate(dataTemp);
    }

    /**
     * Cambia status a 0
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusInactiveByIdTempUpdate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.updateStatusInactiveByIdTempUpdate(dataTemp);
    }

    /**
     * Cambia status a 1
     * @param dataTemp el objeto con el que se realizara la actualización en este caso solo se realiza con id del objeto.
     * @return verdadero si se realiza la actualización en caso contrario retorna false.
     */
    public boolean updateStatusActiveByIdTempUpdate(Tbl_Data_Temp dataTemp){
        return sqLiteHelper.updateStatusActiveByIdTempUpdate(dataTemp);
    }

    /**
     * Elimina elimentos de la tabla con status 1.
     * @return verdadero si los datos son eliminados en caso contrario false.
     */
    public boolean deleteByStatusActiveTempUpdate(){
        return sqLiteHelper.deleteByStatusActiveTempUpdate();
    }

    /**
     * Obtiene todos los datos de la tabla.
     * @return la lista con su respectiva información.
     */
    // Getting All Tbl_Data_Temp_Create
    public ArrayList<Tbl_Data_Temp> getAllTempUpdate() {
        return sqLiteHelper.getAllTempUpdate();
    }

    public ArrayList<Tbl_Data_Temp> getAllStatusInactiveTempUpdate() {
        return sqLiteHelper.getAllStatusInactiveTempUpdate();
    }

    public boolean createDataSensor(Tbl_Data_Sensor dataSensor){
        return sqLiteHelper.createDataSensor(dataSensor);
    }

    public ArrayList<Tbl_Data_Sensor> getAllDataSensor() {
        return sqLiteHelper.getAllDataSensor();
    }
}
