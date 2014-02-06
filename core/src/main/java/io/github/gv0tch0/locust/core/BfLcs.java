package io.github.gv0tch0.locust.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Brute forces its way through the longest common substrings computation.
 * @author Nik Kolev
 */
public class BfLcs implements Lcs {
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
        
        TreeSet<String> commonSubstrings = new TreeSet<String>(new LongerFirst());
        boolean first = true;
        for (String word : sWords) {
            Set<String> wordSubstrings = wordToSubstrings(word);
            if (first) {
                first = false;
                commonSubstrings.addAll(wordSubstrings);
            }
            else {
                commonSubstrings.retainAll(wordSubstrings);
            }
            
            if (commonSubstrings.isEmpty()) {
                break;
            }
        }
        return longest(commonSubstrings);
    }
    
    // The members that follow are package-protected to allow for direct unit test access.
    
    /**
     * @param word A string.
     * @return The set of all substrings of the given string. An empty set when the given
     *         string is empty.
     */
    Set<String> wordToSubstrings(String word) {
        Set<String> substrings = new HashSet<String>();
        for (int i = 0; i < word.length(); i++) {
            for (int j = i+1; j <= word.length(); j++) {
                substrings.add(word.substring(i, j));
            }
        }
        return substrings;
    }
    
    /**
     * @param ts A tree set of strings. Assumes that the set comparator orders the strings
     *           by their length in descending order.
     * @return A set that contains the longest member(s) of the given set.
     */
    Set<String> longest(TreeSet<String> ts) {
        Set<String> result = new HashSet<String>();
        int l = -1;
        for (String s : ts) {
            if (l == -1) {
                l = s.length();
                result.add(s);
            }
            else {
                if (l > s.length()) {
                    break;
                }
                result.add(s);
            }
        }
        return result;
    }
    
    /**
     * Orders strings by length in descending order. Strings with the same
     * length are ordered in reverse natural order.
     */
    static class LongerFirst implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            // We don't need to worry about nulls as our user's contract explicitly
            // states that a NullPointerException is thrown for null words.
            int d =  s2.length() - s1.length();
            if (d == 0) {
                // Necessary as TreeSet-s take compareTo as gospel in place of equals. Without it different
                // strings of the same length have no way of being members of the TreeSet and we definitely
                // want them to be.
                d = s2.compareTo(s1);
            }
            return d;
        }
    }

}
