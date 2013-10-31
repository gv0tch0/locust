package io.github.gv0tch0.locust.service;

import io.github.gv0tch0.locust.core.Lcs;

import javax.inject.Inject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class LocustService {
    // Package-protected for unit test usage.
    static final String BAD_ARG_MESSAGE = "The collection of strings {0}.";
    static final String BAD_ARG_EMPTY_COLLECTION = "is empty";
    static final String BAD_ARG_EMPTY_WORD = "contains an empty word";
    static final String BAD_ARG_DUPLICATES = "contains duplicates";
    
    @Inject
    private Lcs _lcs;
    
    /**
     * @return The collection containing the longest common substring(s) of the given words.
     *         The collection does not contain any duplicates and is ordered by its members'
     *         natural ordering. The collection is empty when the given words share no common
     *         substring. If there are more than one common substrings of longest length the
     *         collection contains them all and orders then in alphabetic order.
     * @throws IllegalArgumentException When the given collection of words is null, empty, or
     *                                  contains a <code>null</code> or an empty word.
     *                                  When the given collection of words contains duplicate
     *                                  words.
     */
    public Collection<String> longestCommonSubstrings(Collection<String> words) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException(String.format(BAD_ARG_MESSAGE, BAD_ARG_EMPTY_COLLECTION));
        }
        
        Set<String> sWords = new HashSet<String>(words);
        if (sWords.size() < words.size()) {
            throw new IllegalArgumentException(String.format(BAD_ARG_MESSAGE, BAD_ARG_DUPLICATES));
        }
        if (sWords.contains(null) || sWords.contains("")) {
            throw new IllegalArgumentException(String.format(BAD_ARG_MESSAGE, BAD_ARG_EMPTY_WORD));
        }
        
        return new TreeSet<String>(_lcs.lcs(sWords));
    }
}
