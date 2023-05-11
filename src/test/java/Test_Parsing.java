import org.junit.jupiter.api.Test;
import restServer.RequestParser;
import restServer.HttpRequest;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Test_Parsing {

    /**
     * This is a unit test case to validate the functionality of HttpRequest extraction and processing from an input.
     */
    @Test
    void httpRequestExtraction() {
        // Arrange: Setup the test conditions

        // Generate a sample HTTP request string
        String exampleHTTPRequest = generateExampleHTTPRequest();

        // Create a BufferedReader linked to the sample HTTP request string
        BufferedReader readerForInput = new BufferedReader(new StringReader(exampleHTTPRequest));

        // Generate a map of the headers we expect to find in this request
        Map<String, String> anticipatedHeaders = generateAnticipatedHeaders();

        // Create an instance of RequestParser using the input reader
        RequestParser requestInterpreter = new RequestParser(readerForInput);

        // Act: Execute the functionality that we're testing

        // Use the parser to interpret the HTTP request and extract the HttpRequest
        HttpRequest derivedRequestContext = requestInterpreter.interpretHttpRequest();

        // Assert: Check the test results

        // Confirm that the extracted HttpRequest matches our expectations
        verifyHttpRequest(derivedRequestContext, anticipatedHeaders);
    }

    // Helper method to create a sample HTTP request
    private String generateExampleHTTPRequest() {
        return "GET /messages/cards HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Key: value\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 8\r\n" +
                "\r\n" +
                "{id:123}";
    }

    // Helper method to create a map of the expected headers
    private Map<String, String> generateAnticipatedHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("host:", "localhost");
        headers.put("key:", "value");
        headers.put("content-type:", "application/json");
        headers.put("content-length:", "8");
        return headers;
    }

    // Helper method to verify the properties of the HttpRequest
    private void verifyHttpRequest(HttpRequest derivedRequestContext, Map<String, String> anticipatedHeaders) {
        assertEquals("GET", derivedRequestContext.getMethod());
        assertEquals("/messages/cards", derivedRequestContext.getResource());
        assertEquals("HTTP/1.1", derivedRequestContext.getVersion());
        assertEquals(anticipatedHeaders, derivedRequestContext.getHeaders());
        assertEquals("{id:123}", derivedRequestContext.getBody());
    }

}

