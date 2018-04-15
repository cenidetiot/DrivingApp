package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;

import www.fiware.org.ngsi.datamodel.datatypes.ArrayTextObject;
import www.fiware.org.ngsi.datamodel.datatypes.DateTimeObject;
import www.fiware.org.ngsi.datamodel.datatypes.LocationGeoJsonObject;
import www.fiware.org.ngsi.datamodel.datatypes.LocationPointObject;
import www.fiware.org.ngsi.datamodel.datatypes.TextObject;

/**
 * Created by Cipriano on 4/9/2018.
 */

public class Zone implements Serializable {
    private String idZone;
    private String type;
    private TextObject refBuildingType;
    private TextObject name;
    private TextObject address;
    private TextObject description;
    private TextObject category;
    private LocationGeoJsonObject location;
    private LocationPointObject centerPoint;
    private DateTimeObject dateCreated;
    private DateTimeObject dateModified;
    private TextObject status;

    public Zone(){
        refBuildingType = new TextObject();
        name = new TextObject();
        address = new TextObject();
        description = new TextObject();
        category = new TextObject();
        location = new LocationGeoJsonObject();
        centerPoint = new LocationPointObject();
        dateCreated = new DateTimeObject();
        dateModified = new DateTimeObject();
        status = new TextObject();
    }

    public String getIdZone() {
        return idZone;
    }

    public void setIdZone(String idZone) {
        this.idZone = idZone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TextObject getRefBuildingType() {
        return refBuildingType;
    }

    public void setRefBuildingType(TextObject refBuildingType) {
        this.refBuildingType = refBuildingType;
    }

    public TextObject getName() {
        return name;
    }

    public void setName(TextObject name) {
        this.name = name;
    }

    public TextObject getAddress() {
        return address;
    }

    public void setAddress(TextObject address) {
        this.address = address;
    }

    public TextObject getDescription() {
        return description;
    }

    public void setDescription(TextObject description) {
        this.description = description;
    }

    public TextObject getCategory() {
        return category;
    }

    public void setCategory(TextObject category) {
        this.category = category;
    }

    public LocationGeoJsonObject getLocation() {
        return location;
    }

    public void setLocation(LocationGeoJsonObject location) {
        this.location = location;
    }

    public LocationPointObject getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(LocationPointObject centerPoint) {
        this.centerPoint = centerPoint;
    }

    public DateTimeObject getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateTimeObject dateCreated) {
        this.dateCreated = dateCreated;
    }

    public DateTimeObject getDateModified() {
        return dateModified;
    }

    public void setDateModified(DateTimeObject dateModified) {
        this.dateModified = dateModified;
    }

    public TextObject getStatus() {
        return status;
    }

    public void setStatus(TextObject status) {
        this.status = status;
    }
}
