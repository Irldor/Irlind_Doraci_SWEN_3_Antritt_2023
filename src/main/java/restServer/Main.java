package restServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initialize server...");

        // Create a server socket to listen for incoming connections
        try (ServerSocket serverSocket = new ServerSocket(10001, 5)) {
            System.out.println("Awaiting client connections...");
            System.out.println();

            // Continuously accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Create a new thread to handle each client connection
                Thread clientThread = new Thread(() -> {
                    try (
                            // Establish input and output streams for the client socket
                            BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
                    ) {
                        // Read and process the client's request
                        HttpRequestContext clientRequest;
                        RequestParser parser = new RequestParser(inputReader);
                        clientRequest = parser.interpretHttpRequest();

                        // Output the received request
                        if (clientRequest != null) {
                            System.out.println("** Client - Begin **");
                            System.out.println("** Header: **");
                            System.out.println("    " + clientRequest.getMethod() + " " + clientRequest.getResource() + " " + clientRequest.getVersion());
                            for (Map.Entry<String, String> entry : clientRequest.getHeaders().entrySet()) {
                                System.out.println("    " + entry.getKey() + " " + entry.getValue());
                            }
                            System.out.println("** Body: **");
                            System.out.println(clientRequest.getBody());
                            System.out.println("-------------------------------------------");
                        }

                        // Generate and send the response to the client
                        ResponseGenerator responseGenerator = new ResponseGenerator(outputWriter);
                        responseGenerator.generateResponse(clientRequest);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        // Close the client socket
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Start the client thread
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}