package io.github.gv0tch0.locust.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * Computes longest common substrings using a generalised suffix tree.
 * @author Nik Kolev
 */
public class GstLcs implements Lcs {
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> lcs(Collection<String> words) {
        if (words.isEmpty()) {
            return Collections.emptySet();
        }
        
        Set<String> sWords = new HashSet<String>(words);
        if (sWords.contains(null)) {
            throw new NullPointerException();
        }
        
        // FIXME The real implementation is missing. For now we just randomly pull one of the input words.
        return new HashSet<String>(Arrays.asList(words.iterator().next()));
    }

}
