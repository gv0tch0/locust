package io.github.gv0tch0.locust.core;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Exercises the {@link BfLcs.wordToSubstrings}.
 * @author Nik Kolev
 */
public class WordToSubstringsTest {
    private static BfLcs LCS;
    
    @BeforeClass
    public static void injectLcs() {
        LCS = new BfLcs();
    }
    
    @Test(expected=NullPointerException.class)
    public void nullWord() {
        LCS.wordToSubstrings(null);
    }

    @Test
    public void emptyWord() {
        assertEquals(Collections.<String>emptySet(), LCS.wordToSubstrings(""));
    }

    @Test
    public void oneChar() {
        assertEquals(new HashSet<String>(Arrays.asList("a")), LCS.wordToSubstrings("a"));
    }
}
