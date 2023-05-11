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
    public HttpRequest interpretHttpRequest() {
        HttpRequest reqContext;

        try {
            // Analyze and process the HTTP header
            reqContext = analyzeHeader(reqReader);

            // If the header is valid, analyze and process the HTTP body
            if (reqContext != null) {
                int bodyLength = reqContext.contentLength();
                reqContext.setBody(analyzeBody(reqReader, bodyLength));
                return reqContext;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // This method processes the HTTP header.
    private HttpRequest analyzeHeader(BufferedReader reqReader) throws IOException {
        // We create a new HttpRequest object.
        HttpRequest reqContext = new HttpRequest();

        // We read the first line from the client's request.
        String currentLine = reqReader.readLine();

        // If the line is null, we return null.
        if (currentLine == null) {
            return null;
        }

        // We split the line into its components.
        String[] elements = currentLine.split(" ");

        // If the line doesn't have exactly three components, we return null.
        if (elements.length != 3) {
            return null;
        }

        // We set the HTTP method, resource and version in our context object.
        reqContext.setMethod(elements[0]);
        reqContext.setResource(elements[1]);
        reqContext.setVersion(elements[2]);

        // We then process the rest of the header lines.
        while ((currentLine = reqReader.readLine()) != null && !currentLine.isEmpty()) {
            // We split each line into its components.
            elements = currentLine.split(" ", 2);

            // If the line has exactly two components, we add them to our headers map.
            if (elements.length == 2) {
                reqContext.addHeader(elements[0].toLowerCase(), elements[1]);
            }
        }

        // We return our filled context object.
        return reqContext;
    }


    // Method to process the HTTP body
    private String analyzeBody(BufferedReader reqReader, int bodyLength) throws IOException {
        // Create a char array of the required size
        char[] bodyChars = new char[bodyLength];
        int readSoFar = 0;

        // Keep reading until we've read exactly bodyLength characters
        while(readSoFar < bodyLength) {
            int readThisTime = reqReader.read(bodyChars, readSoFar, bodyLength - readSoFar);
            if(readThisTime == -1) {
                // The client closed the connection before sending enough data.
                // We should handle this error appropriately.
                throw new IOException("Client sent less data than expected");
            }
            readSoFar += readThisTime;
        }

        // Convert the char array to a string and return
        return new String(bodyChars);
    }

}


