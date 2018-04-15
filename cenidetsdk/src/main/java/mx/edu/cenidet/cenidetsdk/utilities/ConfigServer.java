package mx.edu.cenidet.cenidetsdk.utilities;

/**
 * Created by Cipriano on 2/14/2018.
 */

public enum  ConfigServer {
    //#Archivo de configuración de parametros...
    //##########################################################
    //#PARAMETERS
    //#HTTP Connection Config
    http_connectiontimeout("50000"),
    http_sotimeout("3000"),
    http_readtimeout("50000"),

    //#Host
    http_host("https://smartsecurity-webservice.herokuapp.com"),
    http_host_node("https://smartsdk-web-service.herokuapp.com/api/"),
    http_host_login("http://207.249.127.96:5000/v3/auth/"),
    http_host_mongo("https://driving-monitor-service.herokuapp.com/api/"),
    http_host_alert("https://driving-monitor-service.herokuapp.com/api/alertsCampus/"),

    //#Use
    http_api("/api"),
    http_crate("/crate"),
    http_service("/service"),

    //#Entities
    http_user("user"),
    http_organization("organization"),
    http_zone("zone"),
    http_campus("campus"),
    http_device("device"),

    //#methods
    //-----login----
    http_tokens("tokens"),
    http_token("token"),
    http_login("login"),

    //-----Alerts------
    http_current("/alerts/zone/current"),
    http_history("/alerts/zone/history");


    private String propiedad;

    private ConfigServer(String propiedad) {
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
