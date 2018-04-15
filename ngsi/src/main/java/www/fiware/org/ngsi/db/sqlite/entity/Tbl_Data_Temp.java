package www.fiware.org.ngsi.db.sqlite.entity;

/**
 * Created by Cipriano on 10/20/2017.
 */

public class Tbl_Data_Temp {
    private Integer id;
    private String keyword;
    private String json;
    private String status;

    public Tbl_Data_Temp(){

    }

    public Tbl_Data_Temp(Integer id, String keyword, String json, String status) {
        this.id = id;
        this.keyword = keyword;
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
