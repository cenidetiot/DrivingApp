package www.fiware.org.ngsi.datamodel.datatypes;

/**
 * Created by Cipriano on 9/12/2017.
 * Clase de los tipo de datos.
 */

public class NumberObject {
    private String type = "Number";
    private double value;
    private Object metadata;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
}
