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
    
    private static String LCS_URL;
    
    @BeforeClass
    public static void initLcsUrl() {
        Properties properties = new Properties();
        try {
            properties.load(LcsFuncTest.class.getResourceAsStream(PROPERTIES_FILENAME));
        }
        catch (IOException e) {
            // Ignore. Assume properties file issue. Fall back to LCS_URL_DEFAULT.
        }
        LCS_URL = properties.getProperty(LCS_URL_KEY);
        if (LCS_URL.isEmpty()) {
            LCS_URL = LCS_URL_DEFAULT;
        }
    }
    
    @Test
    public void unsupportedMediaType() throws IOException {
        assertEquals(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.CONTENT_TYPE, "application/xml")
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .getKey().intValue());
    }
    
    @Test
    public void noRequestBody() throws IOException {
        postLcs("", HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void syntacticallyIncorrectJson() throws IOException {
        postLcs("foo", HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void noWords() throws IOException {
        postLcs("{\"setOfStrings\": []}", HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void emptyWord() throws IOException {
        postLcs("{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"\"} ]}",
                HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void duplicateWords() throws IOException {
        postLcs("{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"foo\"} ]}",
                HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void oneWord() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"foo\"} ]}";
        assertEquals("foo", postLcs(entity).getValue().lcs.get(0).value);
    }
    
    @Test
    public void noLcs() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"bar\"} ]}";
        assertEquals(0, postLcs(entity).getValue().lcs.size());
    }
    
    @Test
    public void oneLcs() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"abc\"} , {\"value\": \"bcd\"} ]}";
        Entry<Integer,LcsResponse> response = postLcs(entity);
        assertEquals("bc", response.getValue().lcs.get(0).value);
    }
    
    @Test
    public void multiLcs() throws IOException {
        String entity = "{\"setOfStrings\": [ {\"value\": \"bartender\"} , {\"value\": \"banter\"} ]}";
        Entry<Integer,LcsResponse> response = postLcs(entity);
        List<LcsResponsePair> lcs = response.getValue().lcs;
        assertEquals(3, lcs.size());
        assertEquals("ba", lcs.get(0).value);
        assertEquals("er", lcs.get(1).value);
        assertEquals("te", lcs.get(2).value);
    }
    
    private Entry<Integer,LcsResponse> postLcs(String entity) throws IOException {
        return postLcs(entity, HttpStatus.SC_OK);
    }
    
    private Entry<Integer,LcsResponse> postLcs(String entity, int expectedCode) throws IOException {
        Entry<Integer,LcsResponse> response = Request.Post(LCS_URL)
                                                     .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                                                     .bodyString(entity, ContentType.APPLICATION_JSON)
                                                     .execute()
                                                     .handleResponse(new LcsResponseHandler());
        assertEquals(expectedCode, response.getKey().intValue());
        return response;
    }

    // Using Map.Entry to represent the "response code"-"parsed response entity" tuple. Not really
    // a key value pair, just did not feel like creating a Tuple2 class.
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
