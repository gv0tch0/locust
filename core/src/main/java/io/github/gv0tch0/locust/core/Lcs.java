package io.github.gv0tch0.locust.core;

import java.util.Collection;
import java.util.Set;

/**
 * Computes longest common substrings.
 * @author Nik Kolev
 */
public interface Lcs {
    /**
     * @param words The collection of words longest common substrings are computed for.
     * @return The longest common substrings for the given words. The returned
     *         {@link Set} contains more than one entry when there are more than one
     *         distinct longest common substring of the same length. When the given
     *         words collection is empty the result is an empty set as well.
     *         When the given words collection contains a single element the result
     *         is essentially a copy of the given words collection. When the members of
     *         the words collection do not sport a common substring the result is an empty
     *         set. White space is not treated differently (than non-white space).
     * @throws NullPointerException When the given words collection is <code>null</code>
     *         or when it contains one or more <code>null</code> words.
     */
    Set<String> lcs(Collection<String> words);
}
