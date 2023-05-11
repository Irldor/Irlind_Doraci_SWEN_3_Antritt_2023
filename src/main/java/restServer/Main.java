package restServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing server...");

        // Create a server socket and start listening for client connections
        try (ServerSocket serverSocket = new ServerSocket(10001)) {
            System.out.println("Server is waiting for client connections...");

            // Continuously accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Handle each client connection in a new thread
                handleClientConnection(clientSocket);
            }
        } catch (IOException e) {
            // Log any exceptions that occur while creating the server socket or accepting connections
            e.printStackTrace();
        }
    }

    private static void handleClientConnection(Socket clientSocket) {
        // Start a new thread to handle the client connection
        new Thread(() -> {
            try {
                // Process the client's request
                processClientRequest(clientSocket);
            } catch (IOException e) {
                // Log any exceptions that occur while processing the request
                e.printStackTrace();
            } finally {
                // Ensure the client socket is closed, even if an exception occurs
                closeClientSocket(clientSocket);
            }
        }).start();
    }

    private static void processClientRequest(Socket clientSocket) throws IOException {
        // Create input and output streams for communicating with the client
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        // Parse the client's HTTP request
        HttpRequest clientRequest = new RequestParser(inputReader).interpretHttpRequest();

        // Log the client's request for debugging purposes
        logClientRequest(clientRequest);

        // Generate a response to the client's request and send it
        new ResponseGenerator(outputWriter).generateResponse(clientRequest);
    }

    private static void logClientRequest(HttpRequest clientRequest) {
        if (clientRequest != null) {
            // Log the details of the client's request
            System.out.println("** Client - Begin **");
            System.out.println("** Header: **");
            System.out.println("    " + clientRequest.getMethod() + " " + clientRequest.getResource() + " " + clientRequest.getVersion());

            for (Map.Entry<String, String> entry : clientRequest.getHeaders().entrySet()) {
                System.out.println("    " + entry.getKey() + " " + entry.getValue());
            }
            System.out.println("** Body: **");
            System.out.println(clientRequest.getBody());
            System.out.println("/////////////////////////////////////////////////////////////");
        }
    }

    private static void closeClientSocket(Socket clientSocket) {
        try {
            // Close the client socket
            clientSocket.close();
        } catch (IOException e) {
            // Log any exceptions that occur while closing the socket
            e.printStackTrace();
        }
    }

}