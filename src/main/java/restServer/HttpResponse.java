package restServer;

// Store request context
// A significantly modified version of the ResponseContext class with added comments
public class HttpResponse {
    private String httpProtocol;
    private String statusCode;
    private String serverInfo;
    private String mimeType;
    private int contentSize;
    private String responseBody;

    // Constructor initializing with a given status code
    public HttpResponse(String statusCode) {
        this.httpProtocol = "HTTP/1.1";
        this.statusCode = statusCode;
        this.serverInfo = "mtcg-server";
        this.mimeType = "application/json";
        this.responseBody = "";
        this.contentSize = 0;
    }

    // Getter methods for the instance variables
    public String getHttpProtocol() {
        return httpProtocol;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getContentSize() {
        return contentSize;
    }

    public String getResponseBody() {
        return responseBody;
    }

    // Method to set the status code
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    // Method to set the MIME type
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    // Method to set the response body and automatically update the content size
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        this.contentSize = responseBody.length();
    }
}


