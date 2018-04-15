package www.fiware.org.ngsi.datamodel.datatypes;

import java.util.ArrayList;

/**
 * Created by Cipriano on 2/2/2018.
 */

public class ArrayNumber {

    private ArrayList<Double> value;

    public ArrayNumber(){
        value = new ArrayList<>();
    }

    public ArrayList<Double> getValue() {
        return value;
    }

    public void setValue(ArrayList<Double> value) {
        this.value = value;
    }
}
