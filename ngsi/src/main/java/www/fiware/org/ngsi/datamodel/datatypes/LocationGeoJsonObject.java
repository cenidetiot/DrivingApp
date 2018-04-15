package www.fiware.org.ngsi.datamodel.datatypes;

import java.io.Serializable;

/**
 * Created by Cipriano on 4/10/2018.
 */

public class LocationGeoJsonObject implements Serializable {
    private String type="geo:json";
    private String value;

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
