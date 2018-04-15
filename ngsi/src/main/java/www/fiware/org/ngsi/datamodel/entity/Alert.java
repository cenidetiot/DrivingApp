package www.fiware.org.ngsi.datamodel.entity;

import www.fiware.org.ngsi.datamodel.datatypes.DateTimeObject;
import www.fiware.org.ngsi.datamodel.datatypes.LocationPointObject;
import www.fiware.org.ngsi.datamodel.datatypes.TextObject;

/**
 * Created by Cipriano on 3/23/2018.
 */

public class Alert {
    private String id;
    private String type = "Alert";
    private TextObject alertSource;
    private TextObject category;
    private DateTimeObject dateObserved;
    private TextObject description;
    private LocationPointObject location;
    private TextObject severity;
    private TextObject subCategory;
    private DateTimeObject validFrom;
    private DateTimeObject validTo;

    public Alert() {
        alertSource = new TextObject();
        category = new TextObject();
        dateObserved = new DateTimeObject();
        description = new TextObject();
        location = new LocationPointObject();
        severity = new TextObject();
        subCategory = new TextObject();
        validFrom = new DateTimeObject();
        validTo = new DateTimeObject();
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

    public TextObject getAlertSource() {
        return alertSource;
    }

    public void setAlertSource(TextObject alertSource) {
        this.alertSource = alertSource;
    }

    public TextObject getCategory() {
        return category;
    }

    public void setCategory(TextObject category) {
        this.category = category;
    }

    public DateTimeObject getDateObserved() {
        return dateObserved;
    }

    public void setDateObserved(DateTimeObject dateObserved) {
        this.dateObserved = dateObserved;
    }

    public TextObject getDescription() {
        return description;
    }

    public void setDescription(TextObject description) {
        this.description = description;
    }

    public LocationPointObject getLocation() {
        return location;
    }

    public void setLocation(LocationPointObject location) {
        this.location = location;
    }

    public TextObject getSeverity() {
        return severity;
    }

    public void setSeverity(TextObject severity) {
        this.severity = severity;
    }

    public TextObject getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(TextObject subCategory) {
        this.subCategory = subCategory;
    }

    public DateTimeObject getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(DateTimeObject validFrom) {
        this.validFrom = validFrom;
    }

    public DateTimeObject getValidTo() {
        return validTo;
    }

    public void setValidTo(DateTimeObject validTo) {
        this.validTo = validTo;
    }
}
