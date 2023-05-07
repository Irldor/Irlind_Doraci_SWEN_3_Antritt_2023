package restServer;


import java.io.BufferedReader;
import java.io.IOException;

public class RequestParser {

    // Instance variable for the BufferedReader
    BufferedReader reqReader;

    // Constructor that initializes the BufferedReader
    public RequestParser(BufferedReader reqReader) {
        this.reqReader = reqReader;
    }

    // Public method to interpret the HTTP request
    public HttpRequestContext interpretHttpRequest() {
        HttpRequestContext reqContext;

        try {
            // Analyze and process the HTTP header
            reqContext = analyzeHttpHeader(reqReader);

            // If the header is valid, analyze and process the HTTP body
            if (reqContext != null) {
                int bodyLength = reqContext.contentLength();
                reqContext.setBody(analyzeHttpBody(reqReader, bodyLength));
                return reqContext;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Private method to analyze and process the HTTP header
    private HttpRequestContext analyzeHttpHeader(BufferedReader reqReader) throws IOException {
        // Create a new RequestContext object
        HttpRequestContext reqContext = new HttpRequestContext();
        String currentLine;

        // Read and process the HTTP method, requested resource, and HTTP version
        currentLine = reqReader.readLine();
        if (currentLine == null) {
            return null;
        }
        String[] elements = currentLine.split(" ");
        if (elements.length == 3) {
            reqContext.setMethod(elements[0]);
            reqContext.setResource(elements[1]);
            reqContext.setVersion(elements[2]);
        } else {
            return null;
        }

        // Read and process the other header values
        while ((currentLine = reqReader.readLine()) != null) {
            if (currentLine.isEmpty()) {
                break;
            }
            elements = currentLine.split(" ", 2);
            if (elements.length == 2) {
                reqContext.addHeader(elements[0].toLowerCase(), elements[1]);
            }
        }

        return reqContext;
    }

    // Private method to analyze and process the HTTP body
    private String analyzeHttpBody(BufferedReader reqReader, int bodyLength) throws IOException {
        StringBuilder bodyData = new StringBuilder(10000);
        int readCount = 0;
        int inputData;

        // Read the HTTP body content while the reader is ready and there are characters to read
        while (reqReader.ready() && (inputData = reqReader.read()) != -1) {
            bodyData.append((char) inputData);
            readCount++;

            // Stop reading when the content length is reached
            if (readCount >= bodyLength) {
                break;
            }
        }

        return bodyData.toString();
    }
}


