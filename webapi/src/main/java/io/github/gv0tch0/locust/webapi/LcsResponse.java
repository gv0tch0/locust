package io.github.gv0tch0.locust.webapi;

import javax.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * The longest common substring response.
 * @author Nik Kolev
 */
public class LcsResponse {
    @XmlElement(name="lcs")
    private List<LcsValue> _substrings;

    public LcsResponse() {
    }
    
    public LcsResponse(List<LcsValue> substrings) {
        _substrings = substrings;
    }
}
