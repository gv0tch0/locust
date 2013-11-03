package io.github.gv0tch0.locust.webapi;

import javax.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * The longest common substring request.
 * @author Nik Kolev
 */
public class LcsRequest {
    @XmlElement(name="setOfStrings")
    private List<LcsValue> _words;
    
    public List<LcsValue> getWords() {
        return _words;
    }
}
