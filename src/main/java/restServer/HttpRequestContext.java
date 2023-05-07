package restServer;

import java.util.HashMap;
import java.util.Map;

// Store request context
// A significantly modified version of the RequestContext class with added comments
public class HttpRequestContext {

    // Instance variables to store the HTTP request components
    private String method;
    private String resource;
    private String version;
    private Map<String, String> headers;
    private String body;

    // Constructor to initialize the headers map
    public HttpRequestContext() {
        headers = new HashMap<>();
    }

    // Getter and setter methods for the instance variables
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // Method to add a key-value pair to the headers map
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    // Method to get the content length from the headers map
    public int contentLength() {
        if (headers != null && headers.containsKey("content-length:")) {
            return Integer.parseInt(headers.get("content-length:"));
        }
        return 0;
    }
}

