/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package www.fiware.org.ngsi.datamodel.datatypes;

/**
 *
 * @author Cipriano
 * Clase de los tipo de datos.
 */
public class BooleanObject {
   private String type = "Boolean";
   private boolean value = true;
   private Object metadata;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
   
   
}
