package www.fiware.org.ngsi.utilities;

/**
 * Created by Cipriano on 9/28/2017.
 * Clase donde se añadiran las diferentes constantes que se utilizaran en algun determinado proyecto.
 */
public class Constants {
    //CONSTANTES PARA ALMACENAMIENTO EN ARCHIVOS...
    public static final String FILE_NAME_DATA = "data.txt";

    //CONSTANTES PARA LAS CONFIGURACIONES DE LAS PREFERENCIAS DEL DISPOSITIVO...
    public static final String PREFERENCE_NAME_GENERAL = "preference_name_general";
    public static final String PREFERENCE_KEY_EMAIL = "email";
    public static final String PREFERENCE_KEY_PASSWORD = "password";
    public static final String PREFERENCE_KEY_REMEMBER = "remember";

    //VARIABLE PARA EL LOGIN
    public static final String PREFERENCE_KEY_TOKEN = "token";
    public static final String PREFERENCE_KEY_MAXSPEED = "maxspeed";
    public static final String PREFERENCE_KEY_MINSPEED = "minspeed";

    public static final String PREFERENCE_OFFLINE_MODE_KEY = "offline_mode";
    public static final String PREFERENCE_STATUS_OFFLINE_MODE  = "status_offline_mode";
    public static final String PREFERENCE_MOBILE_DATA_KEY = "mobile_data";
    public static final String PREFERENCE_STATUS_MOBILE_DATA = "status_mobile_data";

    //CONSTANTES PARA LOS Intent DE LOS SERVICIOS - DE RESPALDO DE INFORMACIÓN.
    public static final String SERVICE_RUNNING_BACKING_UP = "service_running_backing_up";
    public static final String SERVICE_RESULT_BACKING_UP = "service_result_backing_up";
    public static final String SERVICE_RUNNING_MODE_OFFLINE = "service_running_mode_offline";
    public static final String SERVICE_RESULT_MODE_OFFLINE = "service_result_mode_offline";

    //CONSTANTES PARA LOS Intent DE LOS SERVICIOS DE LOS SENSORES
    public static final String SERVICE_CHANGE_LOCATION_DEVICE = "service_change_location_device";
    public static final String SERVICE_RESULT_LATITUDE = "latitude";
    public static final String SERVICE_RESULT_LONGITUDE = "longitude";
    public static final String SERVICE_RESULT_SPEED_MS = "device_speed_ms";
    public static final String SERVICE_RESULT_SPEED_KMHR = "device_speed_kmhr";
    public static final String SERVICE_RUNNING_DEVICE = "service_running_device";
    public static final String SERVICE_RESULT_DEVICE = "service_result_device";
    public static final String DEVICE_LATITUDE = "device_latitude";
    public static final String DEVICE_LONGITUDE = "device_longitude";
    public static final String SERVICE_RUNNING_DEVICEMODEL= "service_running_devicemodel";
    public static final String SERVICE_RESULT_DEVICEMODEL = "service_result_devicemodel";
    public static final String SERVICE_RUNNING_SENSORS = "service_running_sensors";
    public static final String ACCELEROMETER_RESULT_SENSORS = "accelerometer_result_sensors";
    public static final String GYROSCOPE_RESULT_SENSORS = "gyroscope_result_sensors";

    //Events
    public static final String SERVICE_CHANGE_WRONG_WAY = "wrong_way";
    public static final String SERVICE_RESULT_WRONG_WAY_OUTPUT = "wrong_way_output";
}
