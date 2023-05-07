package restServer;

// Store request context
// A significantly modified version of the ResponseContext class with added comments
public class HttpResponseContext {

    // Instance variables to store the HTTP response components
    private String httpProtocol;
    private String statusCode;
    private String serverInfo;
    private String mimeType;
    private int contentSize;
    private String responseBody;

    // Constructor to initialize the HTTP response components
    public HttpResponseContext(String statusCode) {
        httpProtocol = "HTTP/1.1";
        this.statusCode = statusCode;
        serverInfo = "mtcg-server";
        mimeType = "application/json";
        contentSize = 0;
        responseBody = "";
    }

    // Getter and setter methods for the instance variables
    public String getHttpProtocol() {
        return httpProtocol;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getContentSize() {
        return contentSize;
    }

    public String getResponseBody() {
        return responseBody;
    }

    // Method to set the response body and update the content size
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        contentSize = responseBody.length();
    }
}

