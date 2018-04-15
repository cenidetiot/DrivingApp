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
    private NumberObject latitude;
    private NumberObject longitude;
    private TextObject mnc;
    private TextObject mcc;
    private TextObject macAddress;
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
        latitude = new NumberObject();
        longitude = new NumberObject();
        mnc = new TextObject();
        mcc = new TextObject();
        macAddress = new TextObject();
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

    public NumberObject getLatitude() {
        return latitude;
    }

    public void setLatitude(NumberObject latitude) {
        this.latitude = latitude;
    }

    public NumberObject getLongitude() {
        return longitude;
    }

    public void setLongitude(NumberObject longitude) {
        this.longitude = longitude;
    }

    public TextObject getMnc() {
        return mnc;
    }

    public void setMnc(TextObject mnc) {
        this.mnc = mnc;
    }

    public TextObject getMcc() {
        return mcc;
    }

    public void setMcc(TextObject mcc) {
        this.mcc = mcc;
    }

    public TextObject getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(TextObject macAddress) {
        this.macAddress = macAddress;
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
