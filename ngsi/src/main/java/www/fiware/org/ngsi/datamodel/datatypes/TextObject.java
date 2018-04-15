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

public class TextObject {
   private String type = "Text";
   private String value;
   private Object metadata;

    public TextObject(){
        metadata = new Object();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
   
   
    
}
