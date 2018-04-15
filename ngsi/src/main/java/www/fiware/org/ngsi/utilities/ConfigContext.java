package www.fiware.org.ngsi.utilities;

/**
 * Created by Cipriano on 2/14/2018.
 */

public enum ConfigContext {

    //#Archivo de configuración de parametros...
    //##########################################################
    //#PARAMETERS
    //#HTTP Connection Config
    http_connectiontimeout("50000"),
    http_sotimeout("3000"),
    http_readtimeout("50000"),

    //#Host
    http_host("http://207.249.127.149"),

    //#Entities
    http_entities("entities"),

    //#Port
    http_port("1026"),
    http_portnotify("5050"),


    //#notify
    http_notify("notify"),

    //#API Version
    http_apiversion("v2"),

    //#attrs
    http_attrs("attrs");


    private String propiedad;

    private ConfigContext(String propiedad) {
        this.setPropiedad(propiedad);
    }

    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }

  /* public static void main(String[] args) {
       System.out.println(Util.http_attrs.getPropiedad());
    }*/
}
