package www.fiware.org.ngsi.utilities;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Cipriano on 02/02/2017.
 */

public class Tools {
    /**
     * Método que carga los parametros de configuración, en el contexto de la actividad que se este utilizando.
     * @param file nombre del archivo (config.properties)
     * @param context contexto de la actividad donde se ejecuta el metodo.
     * @throws Exception
     */
    public static void initialize(String file, Context context)throws Exception {
        Properties properties = new Properties();
        InputStream stream =  context.getAssets().open(file);
        properties.load(stream);
        Properties sys = System.getProperties();
        sys.setProperty("http.host", properties.getProperty("http.host")+":"+properties.getProperty("http.port"));
        sys.setProperty("http.entities", properties.getProperty("http.entities"));
        sys.setProperty("http.hostnotify", properties.getProperty("http.host")+":"+properties.getProperty("http.portnotify"));
        sys.setProperty("http.apiversion", properties.getProperty("http.apiversion"));
        sys.setProperty("http.connectiontimeout", properties.getProperty("http.connectiontimeout"));
        sys.setProperty("http.sotimeout", properties.getProperty("http.sotimeout"));
        sys.setProperty("http.readtimeout", properties.getProperty("http.readtimeout"));
        sys.setProperty("http.attrs", properties.getProperty("http.attrs"));
        sys.setProperty("http.notify", properties.getProperty("http.notify"));
    }
}
