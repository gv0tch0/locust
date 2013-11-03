package io.github.gv0tch0.locust.webapi;

import io.github.gv0tch0.locust.service.LocustService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The longest common substring "resource".
 * @author Nik Kolev
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LcsResource {
    /** The longest common substring calculator. */
    @Inject
    private LocustService _locustService;
    
    /**
     * @param request The {@link LcsRequest}.
     * @return A 200 (OK) HTTP response with a body that contains the JSON document representation
     *         of a {@link LcsResponse} when the given {@code request} does not violate the validation
     *         rules discussed next.
     */
    @POST
    public Response longestCommonSubstring(LcsRequest request) {
        return Response.ok(coerce(_locustService.longestCommonSubstrings(coerce(request)))).build();
    }
    
    /**
     * @param request The {@link LcsRequest}.
     * @return The given {@code request} as a {@code Collection<String>}. The members of the
     *         collection represent the set of words that longest common substring(s) can be
     *         computed for.
     */
    private Collection<String> coerce(LcsRequest request) {
        if (request == null) {
            return null;
        }
        
        List<LcsValue> words = request.getWords();
        if (words == null) {
            return null;
        }
        
        Collection<String> coerced = new ArrayList<String>();
        for (LcsValue word : words) {
            coerced.add(word.getValue());
        }
        return coerced;
    }
    
    /**
     * @param substrings A {@code Collection<String>} representing the longest common
     *                   substring(s) for a set of words.
     * @return The given {@code substrings} as a {@code LcsResponse}.
     */
    private LcsResponse coerce(Collection<String> substrings) {
        List<LcsValue> values = new ArrayList<LcsValue>();
        for (String substring : substrings) {
            values.add(new LcsValue(substring));
        }
        return new LcsResponse(values);
    }
}
