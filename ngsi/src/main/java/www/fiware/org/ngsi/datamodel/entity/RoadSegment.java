package www.fiware.org.ngsi.datamodel.entity;

import java.io.Serializable;

/**
 * Created by Cipriano on 4/20/2018.
 */

public class RoadSegment implements Serializable {
    private String idRoadSegment;
    private String type = "RoadSegment";
    private String name;
    private String refRoad;
    private String location;
    private String startPoint;
    private String endPoint;
    private String laneUsage;
    private int totalLaneNumber;
    private int maximumAllowedSpeed;
    private int minimumAllowedSpeed;
    private int width;
    private String dateCreated;
    private String dateModified;
    private String status;

    public String getIdRoadSegment() {
        return idRoadSegment;
    }

    public void setIdRoadSegment(String idRoadSegment) {
        this.idRoadSegment = idRoadSegment;
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

    public String getRefRoad() {
        return refRoad;
    }

    public void setRefRoad(String refRoad) {
        this.refRoad = refRoad;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getLaneUsage() {
        return laneUsage;
    }

    public void setLaneUsage(String laneUsage) {
        this.laneUsage = laneUsage;
    }

    public int getTotalLaneNumber() {
        return totalLaneNumber;
    }

    public void setTotalLaneNumber(int totalLaneNumber) {
        this.totalLaneNumber = totalLaneNumber;
    }

    public int getMaximumAllowedSpeed() {
        return maximumAllowedSpeed;
    }

    public void setMaximumAllowedSpeed(int maximumAllowedSpeed) {
        this.maximumAllowedSpeed = maximumAllowedSpeed;
    }

    public int getMinimumAllowedSpeed() {
        return minimumAllowedSpeed;
    }

    public void setMinimumAllowedSpeed(int minimumAllowedSpeed) {
        this.minimumAllowedSpeed = minimumAllowedSpeed;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
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
