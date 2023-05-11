package restServer;

import java.util.HashMap;
import java.util.Map;

// Store request context
// A significantly modified version of the RequestContext class with added comments
public class HttpRequest {

    // Instance variables to store the HTTP request components
    private String method;
    private String resource;
    private String version;
    private Map<String, String> headers;
    private String body;

    // Constructor to initialize the headers map
    public HttpRequest() {
        this.headers = new HashMap<>();
    }

    // Getter and setter methods for the instance variables
    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // Method to add a key-value pair to the headers map
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    // Method to get the content length from the headers map
    public int contentLength() {
        if (this.headers.containsKey("content-length:")) {
            try {
                return Integer.parseInt(this.headers.get("content-length:"));
            } catch (NumberFormatException e) {
                System.out.println("Content length is not a valid integer");
            }
        }
        return 0;
    }
}


