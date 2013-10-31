package io.github.gv0tch0.locust.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Exercises the {@link BfLcs.LongerFirst} comparator.
 * @author nkolev
 */
public class LongerFirstTest {
    Set<String> _lfs;
    
    @Before
    public void setup() {
        _lfs = new TreeSet<String>(new BfLcs.LongerFirst());
    }
    
    @Test(expected=NullPointerException.class)
    public void nullElem() {
        _lfs.add("1");
        _lfs.add(null);
    }
    
    @Test
    public void order() {
        List<String> expected = Arrays.asList("444", "333", "222", "bb", "a", "");
        _lfs.addAll(expected);
        assertEquals(expected, new ArrayList<String>(_lfs));
    }
}
