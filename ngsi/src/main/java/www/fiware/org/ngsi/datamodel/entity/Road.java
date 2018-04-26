package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;

/**
 * Created by Cipriano on 4/20/2018.
 */

public class Road implements Serializable {
    private String idRoad;
    private String type = "Road";
    private String name;
    private String description;
    private String responsible;
    private String dateCreated;
    private String dateModified;
    private String status;

    public String getIdRoad() {
        return idRoad;
    }

    public void setIdRoad(String idRoad) {
        this.idRoad = idRoad;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
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
