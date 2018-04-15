package www.fiware.org.ngsi.datamodel.datatypes;

import java.io.Serializable;

/**
 * Created by Cipriano on 4/10/2018.
 */

public class ArrayTextObject extends ArrayString implements Serializable {
    private String type = "Text";

   /* public ArrayTextObject(){
        value = new ArrayString();
    }*/

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*public ArrayString getValue() {
        return value;
    }

    public void setValue(ArrayString value) {
        this.value = value;
    }*/
}
