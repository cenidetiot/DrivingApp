package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;

/**
 * Created by Cipriano on 4/25/2018.
 */

public class OffStreetParking implements Serializable {
    private String idOffStreetParking;
    private String type = "OffStreetParking";
    private String name;
    private String category;
    private String location;
    private String description;
    private String areaServed;
    private String dateCreated;
    private String dateModified;
    private String status;

    public String getIdOffStreetParking() {
        return idOffStreetParking;
    }

    public void setIdOffStreetParking(String idOffStreetParking) {
        this.idOffStreetParking = idOffStreetParking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAreaServed() {
        return areaServed;
    }

    public void setAreaServed(String areaServed) {
        this.areaServed = areaServed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
