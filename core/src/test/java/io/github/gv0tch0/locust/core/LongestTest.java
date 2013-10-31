package io.github.gv0tch0.locust.core;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Exercises the {@link BfLcs.longest}.
 * @author Nik Kolev
 */
public class LongestTest {
    private static BfLcs LCS;
    
    @BeforeClass
    public static void injectLcs() {
        LCS = new BfLcs();
    }
    
    @Test(expected=NullPointerException.class)
    public void nullTree() {
        LCS.longest(null);
    }

    @Test
    public void emptyTree() {
        assertEquals(Collections.<String>emptySet(), LCS.longest(new TreeSet<String>()));
    }
    
    @Test
    public void oneLongest() {
        TreeSet<String> ts = new TreeSet<String>(new BfLcs.LongerFirst());
        ts.add("");
        ts.add("bb");
        ts.add("a");
        ts.add("ccc");
        assertEquals(new HashSet<String>(Arrays.asList("ccc")), LCS.longest(ts));
    }
    
    @Test
    public void multiLongest() {
        TreeSet<String> ts = new TreeSet<String>(new BfLcs.LongerFirst());
        ts.add("");
        ts.add("333");
        ts.add("bb");
        ts.add("a");
        ts.add("ccc");
        ts.add("CCC");
        assertEquals(new HashSet<String>(Arrays.asList("CCC", "ccc", "333")), LCS.longest(ts));
    }
}
