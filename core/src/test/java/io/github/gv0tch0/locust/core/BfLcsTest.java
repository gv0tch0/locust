package io.github.gv0tch0.locust.core;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Tests the {@link BfLcs}.
 * @author Nik Kolev
 */
public class BfLcsTest {
    private static Lcs LCS;
    
    @BeforeClass
    public static void injectLcs() {
        LCS = new BfLcs();
    }
    
    @Test(expected=NullPointerException.class)
    public void nullWords() {
        LCS.lcs(null);
    }
    
    @Test(expected=NullPointerException.class)
    public void nullWord() {
        Collection<String> words = new ArrayList<String>();
        words.add(null);
        LCS.lcs(words);
    }

    @Test
    public void emptyWords() {
        assertEquals(Collections.<String>emptySet(), LCS.lcs(Collections.<String>emptyList()));
    }
    
    @Test
    public void oneWord() {
        Collection<String> words = new ArrayList<String>();
        words.add("foo");
        assertEquals(new HashSet<String>(Arrays.asList("foo")), LCS.lcs(words));
    }

    @Test
    public void sameWord() {
        Collection<String> words = new ArrayList<String>();
        words.add("foo");
        words.add("foo");
        assertEquals(new HashSet<String>(Arrays.asList("foo")), LCS.lcs(words));
    }

    @Test
    public void oneLcs() {
        Collection<String> words = new ArrayList<String>();
        words.add("comcast");
        words.add("comcastic");
        words.add("broadcaster");
        assertEquals(new HashSet<String>(Arrays.asList("cast")), LCS.lcs(words));
    }

    @Test
    public void multiLcs() {
        Collection<String> words = new ArrayList<String>();
        words.add("stormcast");
        words.add("storycaster");
        assertEquals(new HashSet<String>(Arrays.asList("stor", "cast")), LCS.lcs(words));
    }
}
