package www.fiware.org.ngsi.datamodel.datatypes;

import java.util.ArrayList;

/**
 * Created by Cipriano on 26/04/2017.
 * Clase de los tipo de datos.
 */

public class LocationPointObject{
    private String type="geo:point";
    private String value;

    public LocationPointObject(){
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
