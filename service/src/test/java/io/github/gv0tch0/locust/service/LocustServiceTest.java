package io.github.gv0tch0.locust.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static io.github.gv0tch0.locust.service.LocustService.BAD_ARG_MESSAGE;
import static io.github.gv0tch0.locust.service.LocustService.BAD_ARG_EMPTY_COLLECTION;
import static io.github.gv0tch0.locust.service.LocustService.BAD_ARG_EMPTY_WORD;
import static io.github.gv0tch0.locust.service.LocustService.BAD_ARG_DUPLICATES;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.github.gv0tch0.locust.core.Lcs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class LocustServiceTest {
    @Mock
    private Lcs _lcsMock;
    
    @InjectMocks
    private LocustService _ls;
    
    @Test
    public void nullWords() {
        validateError(null, BAD_ARG_EMPTY_COLLECTION);
    }
    
    @Test
    public void emptyWords() {
        validateError(Collections.<String>emptyList(), BAD_ARG_EMPTY_COLLECTION);
    }

    @Test
    public void nullWord() {
        validateError(Arrays.asList(null, "abc"), BAD_ARG_EMPTY_WORD);
    }
    
    @Test
    public void emptyWord() {
        validateError(Arrays.asList("abc", ""), BAD_ARG_EMPTY_WORD);
    }
    
    @Test
    public void duplicates() {
        validateError(Arrays.asList("abc", "def", "abc"), BAD_ARG_DUPLICATES);
    }
    
    private void validateError(Collection<String> words, String cause) {
        try {
            _ls.longestCommonSubstrings(words);
            fail();
        }
        catch (IllegalArgumentException iae) {
            assertEquals(String.format(BAD_ARG_MESSAGE, cause), iae.getMessage());
        }
    }
    
    @Test
    public void allGood() {
        Set<String> s = new HashSet<String>(Arrays.asList("abc", "bcd", "cde"));
        _ls.longestCommonSubstrings(s);
        verify(_lcsMock).lcs(s);
    }
}
