package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;
import java.util.ArrayList;

import www.fiware.org.ngsi.datamodel.datatypes.ArrayNumber;
import www.fiware.org.ngsi.datamodel.datatypes.DateTimeObject;
import www.fiware.org.ngsi.datamodel.datatypes.TextObject;

/**
 * Created by Cipriano on 2/1/2018.
 */

public class DeviceSensor implements Serializable {
    private String id;
    private String type;
    private TextObject category;
    private TextObject function;
    private TextObject controlledProperty;
    private ArrayNumber data;
    private DateTimeObject dateCreated;
    private TextObject refDevice;


    public DeviceSensor(){
        category = new TextObject();
        function = new TextObject();
        controlledProperty = new TextObject();
        data = new ArrayNumber();
        dateCreated = new DateTimeObject();
        refDevice = new TextObject();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TextObject getCategory() {
        return category;
    }

    public void setCategory(TextObject category) {
        this.category = category;
    }

    public TextObject getFunction() {
        return function;
    }

    public void setFunction(TextObject function) {
        this.function = function;
    }

    public TextObject getControlledProperty() {
        return controlledProperty;
    }

    public void setControlledProperty(TextObject controlledProperty) {
        this.controlledProperty = controlledProperty;
    }

    public ArrayNumber getData() {
        return data;
    }

    public void setData(ArrayNumber data) {
        this.data = data;
    }


    public DateTimeObject getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateTimeObject dateCreated) {
        this.dateCreated = dateCreated;
    }

    public TextObject getRefDevice() {
        return refDevice;
    }

    public void setRefDevice(TextObject refDevice) {
        this.refDevice = refDevice;
    }
}
