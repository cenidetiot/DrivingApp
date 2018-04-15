package www.fiware.org.ngsi.db.sqlite.entity;


/**
 * Created by Cipriano on 2/3/2018.
 */

public class Tbl_Data_Sensor {
    private Integer id;
    private String keyword;
    private double x;
    private double y;
    private double z;
    private String json;
    private String status;

    public Tbl_Data_Sensor(){

    }

    public Tbl_Data_Sensor(Integer id, String keyword, double x, double y, double z, String json, String status) {
        this.id = id;
        this.keyword = keyword;
        this.x = x;
        this.y = y;
        this.z = z;
        this.json = json;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
