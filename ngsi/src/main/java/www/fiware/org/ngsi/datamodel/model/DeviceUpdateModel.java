package www.fiware.org.ngsi.datamodel.model;

import java.io.Serializable;

import www.fiware.org.ngsi.datamodel.datatypes.LocationPointObject;
import www.fiware.org.ngsi.datamodel.datatypes.NumberObject;
import www.fiware.org.ngsi.datamodel.datatypes.TextObject;

/**
 * Created by Cipriano on 9/13/2017.
 * Modelo con los campos a modificar de la Entidades.
 */

public class DeviceUpdateModel implements Serializable {

    private TextObject category;
    private TextObject osVersion;
    private NumberObject batteryLevel;
    private TextObject dateModified;
    private TextObject ipAddress;
    private LocationPointObject location;
    private TextObject refDeviceModel;
    private TextObject serialNumber;
    private TextObject owner;

    public DeviceUpdateModel(){
        category = new TextObject();
        osVersion = new TextObject();
        batteryLevel = new NumberObject();
        dateModified = new TextObject();
        ipAddress = new TextObject();
        location = new LocationPointObject();
        refDeviceModel = new TextObject();
        serialNumber = new TextObject();
        owner = new TextObject();
    }

    public TextObject getCategory() {
        return category;
    }

    public void setCategory(TextObject category) {
        this.category = category;
    }

    public TextObject getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(TextObject osVersion) {
        this.osVersion = osVersion;
    }

    public NumberObject getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(NumberObject batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public TextObject getDateModified() {
        return dateModified;
    }

    public void setDateModified(TextObject dateModified) {
        this.dateModified = dateModified;
    }

    public TextObject getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(TextObject ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocationPointObject getLocation() {
        return location;
    }

    public void setLocation(LocationPointObject location) {
        this.location = location;
    }

    public TextObject getRefDeviceModel() {
        return refDeviceModel;
    }

    public void setRefDeviceModel(TextObject refDeviceModel) {
        this.refDeviceModel = refDeviceModel;
    }

    public TextObject getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(TextObject serialNumber) {
        this.serialNumber = serialNumber;
    }

    public TextObject getOwner() {
        return owner;
    }

    public void setOwner(TextObject owner) {
        this.owner = owner;
    }
}
