import org.junit.jupiter.api.Test;
import restServer.RequestParser;
import restServer.HttpRequestContext;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Test_Parsing {


    /**
     * Test case for the processing and extraction of HttpRequestContext from a given input.
     */

    @Test
    void processRequest() {
        // Create a sample HTTP request string
        String httpRequestStr = "GET /messages/cards HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Key: value\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 8\r\n" +
                "\r\n" +
                "{id:123}";

        // Initialize a BufferedReader with the sample HTTP request string
        BufferedReader inputReader = new BufferedReader(new StringReader(httpRequestStr));

        // Create an expected headers map
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("host:", "localhost");
        expectedHeaders.put("key:", "value");
        expectedHeaders.put("content-type:", "application/json");
        expectedHeaders.put("content-length:", "8");

        // Instantiate the RequestInterpreter class with the input reader
        RequestParser parser = new RequestParser(inputReader);

        // Interpret and extract the HttpRequestContext from the input
        HttpRequestContext requestContext = parser.interpretHttpRequest();

        // Validate the extracted HttpRequestContext against the expected values
        assertEquals("GET", requestContext.getMethod());
        assertEquals("/messages/cards", requestContext.getResource());
        assertEquals("HTTP/1.1", requestContext.getVersion());
        assertEquals(expectedHeaders, requestContext.getHeaders());
        assertEquals("{id:123}", requestContext.getBody());
    }
}
