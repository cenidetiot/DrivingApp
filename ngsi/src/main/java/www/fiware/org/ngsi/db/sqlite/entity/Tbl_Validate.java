package www.fiware.org.ngsi.db.sqlite.entity;

/**
 * Created by Cipriano on 10/18/2017.
 */

public class Tbl_Validate {
    private Integer id;
    private String name;
    private String status;

    public Tbl_Validate(){

    }

    public Tbl_Validate(Integer id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
