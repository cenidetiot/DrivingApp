package www.fiware.org.ngsi.datamodel.datatypes;

import java.util.Date;

/**
 * Created by Cipriano on 9/26/2017.
 * Clase de los tipo de datos.
 */

public class DateTimeObject {
    private String type = "DateTime";
    private String value;
    private Object metadata;

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

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
}
