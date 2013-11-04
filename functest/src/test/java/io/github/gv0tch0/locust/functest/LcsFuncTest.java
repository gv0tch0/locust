package io.github.gv0tch0.locust.functest;

import static org.junit.Assert.assertEquals;

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
import java.util.Properties;

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
                            .intValue());
    }
    
    @Test
    public void noRequestBody() throws IOException {
        assertEquals(HttpStatus.SC_BAD_REQUEST,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void syntacticallyIncorrectJson() throws IOException {
        assertEquals(HttpStatus.SC_BAD_REQUEST,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString("foo", ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void noWords() throws IOException {
        String noWords = "{\"setOfStrings\": []}";
        assertEquals(HttpStatus.SC_BAD_REQUEST,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString(noWords, ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void emptyWord() throws IOException {
        String emptyWord = "{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"\"} ]}";
        assertEquals(HttpStatus.SC_BAD_REQUEST,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString(emptyWord, ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void duplicateWords() throws IOException {
        String dupWord = "{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"foo\"} ]}";
        assertEquals(HttpStatus.SC_BAD_REQUEST,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString(dupWord, ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void oneWord() throws IOException {
        String oneWord = "{\"setOfStrings\": [ {\"value\": \"foo\"} ]}";
        assertEquals(HttpStatus.SC_OK,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString(oneWord, ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void noLcs() throws IOException {
        String oneWord = "{\"setOfStrings\": [ {\"value\": \"foo\"} , {\"value\": \"bar\"} ]}";
        assertEquals(HttpStatus.SC_OK,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString(oneWord, ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void oneLcs() throws IOException {
        String oneLcs = "{\"setOfStrings\": [ {\"value\": \"abc\"} , {\"value\": \"bcd\"} ]}";
        assertEquals(HttpStatus.SC_OK,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString(oneLcs, ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    @Test
    public void multiLcs() throws IOException {
        String multiLcs = "{\"setOfStrings\": [ {\"value\": \"bartender\"} , {\"value\": \"banter\"} ]}";
        assertEquals(HttpStatus.SC_OK,
                     Request.Post(LCS_URL)
                            .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON)
                            .bodyString(multiLcs, ContentType.APPLICATION_JSON)
                            .execute()
                            .handleResponse(new LcsResponseHandler())
                            .intValue());
    }
    
    // TODO Parse the response body when the reponse is a 200 OK to pull out the
    //      longest common substring(s) result and thus facilitate deeper validation
    //      (well, on the other hand the guts of the service are covered by unit
    //      tests in the "guts" modules).
    private static class LcsResponseHandler implements ResponseHandler<Integer> {
        @Override
        public Integer handleResponse(HttpResponse response) throws ClientProtocolException,
                                                                    IOException {
            return response.getStatusLine().getStatusCode();
        }
        
    }
}
