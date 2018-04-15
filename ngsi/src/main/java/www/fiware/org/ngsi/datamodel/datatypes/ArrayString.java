package www.fiware.org.ngsi.datamodel.datatypes;

import java.util.ArrayList;

/**
 * Created by Cipriano on 2/1/2018.
 */

public class ArrayString  {
    private ArrayList<String> value;

    public ArrayString(){
        value = new ArrayList<String>();
    }

    public ArrayList<String> getValue() {
        return value;
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }
}
