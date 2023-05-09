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
     * This is a unit test case to validate the functionality of HttpRequestContext extraction and processing from an input.
     */

    @Test
    void httpRequestExtraction() {
        // Construct a representative HTTP request as a string
        String exampleHTTPRequest = "GET /messages/cards HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Key: value\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 8\r\n" +
                "\r\n" +
                "{id:123}";

        // Establish a BufferedReader linked to the sample HTTP request string
        BufferedReader readerForInput = new BufferedReader(new StringReader(exampleHTTPRequest));

        // Assemble a map of the headers we expect to find in this request
        Map<String, String> anticipatedHeaders = new HashMap<>();
        anticipatedHeaders.put("host:", "localhost");
        anticipatedHeaders.put("key:", "value");
        anticipatedHeaders.put("content-type:", "application/json");
        anticipatedHeaders.put("content-length:", "8");

        // Generate an instance of RequestParser using the input reader as a parameter
        RequestParser requestInterpreter = new RequestParser(readerForInput);

        // Use the parser to interpret the HTTP request and extract the HttpRequestContext
        HttpRequestContext derivedRequestContext = requestInterpreter.interpretHttpRequest();

        // Confirm that the extracted HttpRequestContext matches our expectations
        assertEquals("GET", derivedRequestContext.getMethod());
        assertEquals("/messages/cards", derivedRequestContext.getResource());
        assertEquals("HTTP/1.1", derivedRequestContext.getVersion());
        assertEquals(anticipatedHeaders, derivedRequestContext.getHeaders());
        assertEquals("{id:123}", derivedRequestContext.getBody());
    }
}

