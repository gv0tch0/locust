package io.github.gv0tch0.locust.webapi;

import io.github.gv0tch0.locust.service.LocustService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The longest common substring "resource". This is the application's
 * main and only resource and as such is available at the application's
 * context root.
 * @author Nik Kolev
 */
@Path("/")
public class LcsResource {
    /** The API docs {@code URI} that populates the Location header for the {@code GET /lcs} redirect. */
    private final static URI API_DOC_URI;
    static {
        try {
            API_DOC_URI = new URI("https://github.com/gv0tch0/locust/blob/master/README.md#api");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("This should never happen.", e);
        }
    }
    
    /** The longest common substring calculator. */
    @Inject
    private LocustService _locustService;
    
    /**
     * @param request The {@link LcsRequest}.
     * @return A 200 (OK) response with a body that contains the JSON document representation of a
     *         {@link LcsResponse} when the given {@code request} does not violate the validation
     *         rules discussed next.
     *         A 415 (Unsupported Media Type) response when the request's {@code Content-type} header
     *         does not carry a value of {@code application/json}.
     *         A 400 (Bad Request) response when the request does not contain a JSON document in its
     *         body, contains a JSON document that is syntactically invalid, or represents a request
     *         that contains no words, one or more empty words, or duplicate words.
     *         The 4xx responses are generated as a result of the {@code LocustService} throwing
     *         validation errors, with the {@code ValidationViolationMapper} executing the
     *         error to response translation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
    
    /**
     * @return A 302 (Found) response with Location header set to the API documentation's URI.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response apiDocRedirect()  {
        return Response.temporaryRedirect(API_DOC_URI).build();
    }
}
