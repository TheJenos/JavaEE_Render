/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Render;

/**
 * This is an encapsulated class that holds DataType and Classes
 *
 * @author Thanura
 */
public class DataPill {
    Class datatype = null;
    Object object = null;
    
    public DataPill(Class datatype,Object object) {
        this.datatype = datatype;
        this.object = object;
    }
    
    public void setDatatype(Class datatype) {
        this.datatype = datatype;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Class getDatatype() {
        return datatype;
    }

    public Object getObject() {
        return object;
    }
}

