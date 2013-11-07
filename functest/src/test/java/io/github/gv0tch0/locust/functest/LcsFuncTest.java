package io.github.gv0tch0.locust.functest;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static java.util.Map.Entry;
import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

/**
 * Exercises the service in its full glory.
 * @author Nik Kolev
 */
public class LcsFuncTest {
    private static final String PROPERTIES_FILENAME = "functest.properties";
    private static final String LCS_URL_KEY         = "lcs.url";
    private static final String LCS_URL_DEFAULT     = "http://localhost:8080/lcs/";
    private static final String APPLICATION_JSON    = ContentType.APPLICATION_JSON.getMimeType();
    private static final String APPLICATION_XML     = ContentType.APPLICATION_XML.getMimeType();
    private static final String TEXT_HTML           = ContentType.TEXT_HTML.getMimeType();
    private static final String HANDLE_REDIRECTS    = "http.protocol.handle-redirects";

    private static URI LCS_URI;
    
    @BeforeClass
    public static void initLcsUrl() throws URISyntaxException {
        Properties properties = new Properties();
        try {
            properties.load(LcsFuncTest.class.getResourceAsStream(PROPERTIES_FILENAME));
        }
        catch (IOException e) {
            // Ignore. Assume properties file issue. Fall back to LCS_URL_DEFAULT.
        }
        
        String lcsUrl = properties.getProperty(LCS_URL_KEY);
        if (lcsUrl == null || lcsUrl.trim().isEmpty()) {
            lcsUrl = LCS_URL_DEFAULT;
        }
        LCS_URI = new URI(lcsUrl.trim());
    }
    
    @Test
    public void get() throws ClientProtocolException, IOException {
        HttpResponse httpResponse = Request.Get(LCS_URI)
                                           .config(HANDLE_REDIRECTS, false)
                                           .addHeader(HttpHeaders.ACCEPT, TEXT_HTML)
                                           .execute()
                                           .returnResponse();
        assertEquals(HttpStatus.SC_TEMPORARY_REDIRECT,
                     httpResponse.getStatusLine().getStatusCode());
        assertEquals("https://github.com/gv0tch0/locust/blob/master/README.md#api",
                     httpResponse.getHeaders(HttpHeaders.LOCATION)[0].getValue());
    }
    
    @Test
    public void unsupportedMediaType() throws IOException {
        assertEquals(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE,
                     Request.Post(LCS_URI)
                            .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_XML)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .getKey().intValue());
    }
    
    @Test
    public void noRequestBody() throws IOException {
        validatePost("", HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void syntacticallyIncorrectJson() throws IOException {
        validatePost("foo", HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void noWords() throws IOException {
        validatePost("{\"setOfStrings\": []}", HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void emptyWord() throws IOException {
        validatePost("{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"\"} ]}",
                HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void duplicateWords() throws IOException {
        validatePost("{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"foo\"} ]}",
                HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void oneWord() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"foo\"} ]}";
        assertEquals("foo", validatePost(entity).getValue().lcs.get(0).value);
    }
    
    @Test
    public void noLcs() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"bar\"} ]}";
        assertEquals(0, validatePost(entity).getValue().lcs.size());
    }
    
    @Test
    public void oneLcs() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"abc\"} , {\"value\": \"bcd\"} ]}";
        Entry<Integer,LcsResponse> response = validatePost(entity);
        assertEquals("bc", response.getValue().lcs.get(0).value);
    }
    
    @Test
    public void multiLcs() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"bartender\"} , {\"value\": \"banter\"} ]}";
        Entry<Integer,LcsResponse> response = validatePost(entity);
        List<LcsResponsePair> lcs = response.getValue().lcs;
        assertEquals(3, lcs.size());
        assertEquals("ba", lcs.get(0).value);
        assertEquals("er", lcs.get(1).value);
        assertEquals("te", lcs.get(2).value);
    }
    
    private Entry<Integer,LcsResponse> validatePost(String entity) throws IOException {
        return validatePost(entity, HttpStatus.SC_OK);
    }
    
    private Entry<Integer,LcsResponse> validatePost(String entity, int expectedCode) throws IOException {
        Entry<Integer,LcsResponse> response = Request.Post(LCS_URI)
                                                     .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                                                     .bodyString(entity, ContentType.APPLICATION_JSON)
                                                     .execute()
                                                     .handleResponse(new LcsResponseHandler());
        assertEquals(expectedCode, response.getKey().intValue());
        return response;
    }

    /** Parses locust {@code HttpResponse}s into {@code LcsResponse}s. */
    private static class LcsResponseHandler implements ResponseHandler<Entry<Integer,LcsResponse>> {
        @Override
        public Entry<Integer,LcsResponse> handleResponse(HttpResponse response)
                        throws ClientProtocolException, IOException {
            int rc = response.getStatusLine().getStatusCode();
            LcsResponse re = null;
            if (rc == HttpStatus.SC_OK) {
                InputStreamReader isr = null;
                try {
                    isr = new InputStreamReader(response.getEntity().getContent());
                    re = new Gson().fromJson(isr, LcsResponse.class);
                }
                finally {
                    if (isr != null) {
                        isr.close();
                    }
                }
            }
            return new SimpleImmutableEntry<Integer,LcsResponse>(rc, re);
        }
    }
    
    private static class LcsResponse {
        List<LcsResponsePair> lcs;
    }
    
    private static class LcsResponsePair {
        String value;
    }
}
