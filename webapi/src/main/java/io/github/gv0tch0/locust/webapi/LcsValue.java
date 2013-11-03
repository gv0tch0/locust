package io.github.gv0tch0.locust.webapi;

import javax.xml.bind.annotation.XmlElement;

/**
 * A value in a longest common substring request/response.
 * @author Nik Kolev
 */
public class LcsValue {
    @XmlElement(name="value")
    private String _value;
    
    public LcsValue() {
    }
    
    public LcsValue(String value) {
        _value = value;
    }
    
    public String getValue() {
        return _value;
    }
}
