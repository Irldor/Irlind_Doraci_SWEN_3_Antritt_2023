package restServer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import classes.Handler_Card;
import classes.Battle;
import classes.Trading;
import classes.Handler_User;
import classes.Card;
import classes.DB;
import classes.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Handle request and send response
public class ResponseGenerator {

    BufferedWriter writer;

    public ResponseGenerator(BufferedWriter writer){
        this.writer = writer;
    }

    public void generateResponse(HttpRequestContext request) {
        // Initialize the response object with a default "400 Bad Request" status
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");

        // Check if the request and its headers are not null
        if (request != null && request.getHeaders() != null) {
            // Split the request resource by slashes
            String[] parts = request.getResource().split("/");
            User user;

            // Check if the length of the parts array is 2 or 3
            if (parts.length == 2 || parts.length == 3) {
                // Assign the first part after the initial slash to the variable "resource"
                String resource = parts[1];

                // Use a switch statement to handle different resources
                switch (resource) {
                    case "delete":
                        // If the resource is "delete", call the deleteAll method
                        response = deleteAll(request);
                        break;
                    case "users":
                        // If the resource is "users", call the users method
                        response = users(request);
                        break;
                    case "sessions":
                        // If the resource is "sessions", call the sessions method
                        response = sessions(request);
                        break;
                    case "packages":
                        // If the resource is "packages", call the packages method
                        response = packages(request);
                        break;
                    case "transactions":
                        // If the resource is "transactions" and the third part is "packages", call the transactionsPackages method
                        if (parts.length == 3 && "packages".equals(parts[2])) {
                            user = authorize(request);
                            response = (user != null) ? transactionsPackages(user, request)
                                    : createUnauthorizedResponse("Access denied");
                        }
                        break;
                    case "cards":
                        // If the resource is "cards", call the showCards method
                        user = authorize(request);
                        response = (user != null) ? showCards(user, request)
                                : createUnauthorizedResponse("Access denied");
                        break;
                    case "deck":
                        // If the resource is "deck", call the requestDeck method
                        user = authorize(request);
                        response = (user != null) ? requestDeck(user, request)
                                : createUnauthorizedResponse("Access denied");
                        break;
                    case "stats":
                        // If the resource is "stats", call the stats method
                        user = authorize(request);
                        response = (user != null) ? stats(user, request)
                                : createUnauthorizedResponse("Access denied");
                        break;
                    case "score":
                        // If the resource is "score", call the scoreboard method
                        user = authorize(request);
                        response = (user != null) ? scoreboard(request)
                                : createUnauthorizedResponse("Access denied");
                        break;
                    case "tradings":
                        // If the resource is "tradings", call the trade method
                        user = authorize(request);
                        response = (user != null) ? trade(request, user)
                                : createUnauthorizedResponse("Access denied");
                        break;
                    case "battles":
                        // If the resource is "battles", call the battle method
                        user = authorize(request);
                        response = (user != null) ? battle(request, user)
                                : createUnauthorizedResponse("Access denied");
                        break;
                }
            }
        }

        // Send the response using the writer
        sendResponse(response, writer);
    }

    private static HttpResponseContext createUnauthorizedResponse(String message) {
        // Create a new HttpResponseContext object with a "401 Unauthorized" status
        HttpResponseContext response = new HttpResponseContext("401 Unauthorized");
        // Set the response body with the provided message
        response.setResponseBody(message);
        // Return the created response object
        return response;
    }
    private static void sendResponse(HttpResponseContext response, BufferedWriter writer) {
        // Try to send the response using the provided BufferedWriter
        try {
            // Create a StringBuilder to build the response string
            StringBuilder sb = new StringBuilder();
            // Add the HTTP protocol and status code
            sb.append(response.getHttpProtocol()).append(" ").append(response.getStatusCode()).append("\r\n");
            // Add the server info
            sb.append("Server: ").append(response.getServerInfo()).append("\r\n");
            // Add the content type and content length
            sb.append("Content-Type: ").append(response.getMimeType()).append("\r\n");
            sb.append("Content-Length: ").append(response.getContentSize()).append("\r\n\r\n");
            // Add the response body
            sb.append(response.getResponseBody());

            // Write the response string to the writer and flush it
            writer.write(sb.toString());
            writer.flush();

        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        }
    }

    private HttpResponseContext deleteAll(HttpRequestContext request) {
        // Initialize the response object with a default "400 Bad Request" status
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");
        // Get an instance of the Handler_User class
        Handler_User handlerUser = Handler_User.getInstance();

        // Check if the request method is DELETE
        if (request.getMethod().equals("DELETE")) {
            try {
                // Establish a connection to the database
                Connection conn = DB.getInstance().getConnection();

                // Define an array of table names to be deleted
                String[] tables = {"packages", "marketplace", "cards", "users"};
                // Iterate through the table names
                for (String table : tables) {
                    // Create a PreparedStatement to delete all records from the current table
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + ";");
                    // Execute the update
                    ps.executeUpdate();
                    // Close the PreparedStatement
                    ps.close();
                }

                // Close the database connection
                conn.close();
                // Set the response status code to "200 OK" and the response body to "Successfully deleted"
                response.setStatusCode("200 OK");
                response.setResponseBody("Successfully deleted");
            } catch (SQLException e) {
                // Handle SQLException
                e.printStackTrace();
                // Set the response status code to "409 Conflict" and the response body to "Error while deleting"
                response.setStatusCode("409 Conflict");
                response.setResponseBody("Error while deleting");
            }
        }

        // Return the response object
        return response;
    }


    private HttpResponseContext users(HttpRequestContext request) {
        // Initialize default bad request response
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");

        // Get instance of user handler
        Handler_User handlerUser = Handler_User.getInstance();

        // Check if the request method is valid
        String requestMethod = request.getMethod();
        if ("GET".equals(requestMethod)) {
            // Authorize user and check if the resource is valid
            User user = authorize(request);
            if (user != null) {
                String[] parts = request.getResource().split("/");
                if (parts.length == 3) {
                    // Check if the requested user is the authorized user
                    if (user.getUsername().equals(parts[2])) {
                        // Get user information
                        String userInfo = user.info();
                        if (userInfo != null) {
                            // Set response status and body
                            response.setStatusCode("200 OK");
                            response.setResponseBody(userInfo);
                        } else {
                            response.setStatusCode("404 Not Found");
                            response.setResponseBody("User not found");
                        }
                    } else {
                        response.setStatusCode("401 Unauthorized");
                        response.setResponseBody("Access denied");
                    }
                }
            } else {
                response.setStatusCode("401 Unauthorized");
                response.setResponseBody("Access denied");
            }
        } else if ("POST".equals(requestMethod)) {
            // Register new user
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode jsonNode = mapper.readTree(request.getBody());
                if (jsonNode.has("Username") && jsonNode.has("Password")) {
                    if (handlerUser.registerUser(jsonNode.get("Username").asText(), jsonNode.get("Password").asText())) {
                        response.setStatusCode("201 Created");
                        response.setResponseBody("User created");
                    } else {
                        response.setStatusCode("409 Conflict");
                        response.setResponseBody("Username already exists");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("PUT".equals(requestMethod)) {
            // Authorize user and check if the resource is valid
            User user = authorize(request);
            if (user != null) {
                String[] editUser = request.getResource().split("/");
                if (editUser.length == 3) {
                    // Check if the requested user is the authorized user
                    if (user.getUsername().equals(editUser[2])) {
                        // Update user information
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonNode jsonNode = mapper.readTree(request.getBody());
                            if (jsonNode.has("Name") && jsonNode.has("Bio") && jsonNode.has("Image")) {
                                if (user.setUserInfo(jsonNode.get("Name").asText(), jsonNode.get("Bio").asText(), jsonNode.get("Image").asText())) {
                                    response.setStatusCode("200 OK");
                                    response.setResponseBody("User info successfully updated.");
                                } else {
                                    response.setStatusCode("404 Not Found");
                                    response.setResponseBody("User not found.");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        response.setStatusCode("401 Unauthorized");
                        response.setResponseBody("Access denied");
                    }
                }
            } else {
                response.setStatusCode("401 Unauthorized");
                response.setResponseBody("Access denied");
            }
        }

        return response;
    }

    private HttpResponseContext sessions(HttpRequestContext request) {
        Handler_User manager = Handler_User.getInstance();
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");
        ObjectMapper mapper = new ObjectMapper();

        String requestMethod = request.getMethod();
        if ("POST".equals(requestMethod)) {
            try {
                JsonNode jsonNode = mapper.readTree(request.getBody());
                if (jsonNode.has("Username") && jsonNode.has("Password")) {
                    String username = jsonNode.get("Username").asText();
                    String password = jsonNode.get("Password").asText();
                    if (manager.loginUser(username, password)) {
                        response.setStatusCode("200 OK");
                        response.setResponseBody("User successfully logged in.");
                    } else {
                        response.setStatusCode("401 Unauthorized");
                        response.setResponseBody("Invalid username or password.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("DELETE".equals(requestMethod)) {
            try {
                JsonNode jsonNode = mapper.readTree(request.getBody());
                if (jsonNode.has("Username") && jsonNode.has("Password")) {
                    String username = jsonNode.get("Username").asText();
                    String password = jsonNode.get("Password").asText();
                    if (manager.logoutUser(username, password)) {
                        response.setStatusCode("200 OK");
                        response.setResponseBody("User successfully logged out.");
                    } else {
                        response.setStatusCode("401 Unauthorized");
                        response.setResponseBody("Invalid username or password.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    private HttpResponseContext packages(HttpRequestContext request) {
        // Get the card handler instance
        Handler_Card manager = Handler_Card.getInstance();

        // Create a default bad request response
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");

        // Check if the request method is POST
        if (request.getMethod().equals("POST")) {
            // Get the user handler instance
            Handler_User handlerUser = Handler_User.getInstance();

            // Check if the user is authorized to create packages
            if (request.getHeaders().containsKey("authorization:") && !handlerUser.isAdmin(request.getHeaders().get("authorization:"))) {
                response.setStatusCode("403 Forbidden");
                response.setResponseBody("Access forbidden");
                return response;
            }

            // Read the cards from the request body
            ObjectMapper mapper = new ObjectMapper();
            try {
                // Ignore case sensitivity for property names
                mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                List<Card> cards = mapper.readValue(request.getBody(), new TypeReference<List<Card>>() {});

                // Check if the number of cards is 5
                if (cards.size() == 5) {
                    List<Card> createdCards = new ArrayList<>();
                    boolean creationFailed = false;

                    // Register each card
                    for (Card card : cards) {
                        if (!manager.registerCard(card.getId(), card.getName(), card.getDamage())) {
                            // If registration fails, mark creation as failed
                            creationFailed = true;
                            break;
                        }
                        createdCards.add(card);
                    }

                    // If all cards are registered, create a package
                    if (!creationFailed && manager.createCardPackage(cards)) {
                        response.setStatusCode("201 Created");
                        response.setResponseBody("Package created.");
                    } else {
                        // If creation fails, delete the cards that were registered
                        for (Card card : createdCards) {
                            manager.deleteCard(card.getId());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    private HttpResponseContext transactionsPackages(User user, HttpRequestContext request) {
        Handler_Card manager = Handler_Card.getInstance();
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");

        if ("POST".equals(request.getMethod())) {
            if (manager.assignPackageToUser(user)) {
                response.setStatusCode("200 OK");
                response.setResponseBody("Package successfully acquired by the user.");
            } else {
                response.setStatusCode("409 Conflict");
                response.setResponseBody("Error acquiring package.");
            }
        }

        return response;
    }

    private HttpResponseContext showCards(User user, HttpRequestContext request) {
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");

        if ("GET".equals(request.getMethod())) {
            String jsonResponse = Handler_Card.getInstance().showUserCards(user);
            if (jsonResponse != null) {
                response.setStatusCode("200 OK");
                response.setResponseBody(jsonResponse);
            } else {
                response.setStatusCode("404 Error");
                response.setResponseBody("No cards available.");
            }
        }

        return response;
    }


    private HttpResponseContext requestDeck(User user, HttpRequestContext request) {
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");
        Handler_Card manager = Handler_Card.getInstance();

        String requestMethod = request.getMethod();
        if ("GET".equals(requestMethod)) {
            String jsonDeck = manager.showUserDeck(user);
            if (jsonDeck != null) {
                response.setStatusCode("200 OK");
                response.setResponseBody(jsonDeck);
            } else {
                response.setStatusCode("404 Not Found");
                response.setResponseBody("Deck not found.");
            }
        } else if ("PUT".equals(requestMethod)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<String> cardIds = mapper.readValue(request.getBody(), new TypeReference<List<String>>() {});
                if (cardIds.size() == 4) {
                    if (manager.createDeck(user, cardIds)) {
                        response.setStatusCode("201 Created");
                        response.setResponseBody("Deck created.");
                    } else {
                        response.setStatusCode("409 Conflict");
                        response.setResponseBody("Error while creating deck.");
                    }
                } else {
                    response.setStatusCode("400 Bad Request");
                    response.setResponseBody("Invalid number of cards. The deck must have exactly 4 cards.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatusCode("400 Bad Request");
                response.setResponseBody("Invalid request body.");
            }
        }

        return response;
    }



    private HttpResponseContext stats(User user, HttpRequestContext request) {
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");

        if ("GET".equals(request.getMethod())) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                response.setStatusCode("200 OK");
                response.setResponseBody(mapper.writeValueAsString(user.stats()));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatusCode("500 Internal Server Error");
                response.setResponseBody("Error retrieving user stats.");
            }
        }
        return response;
    }


    private HttpResponseContext scoreboard(HttpRequestContext request) {
        Battle manager = Battle.getInstance();
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");
        if ("GET".equals(request.getMethod())) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                response.setResponseBody(mapper.writeValueAsString(manager.fetchScoreboard()));
                response.setStatusCode("200 OK");
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatusCode("500 Internal Server Error");
                response.setResponseBody("Error retrieving scoreboard.");
            }
        }
        return response;
    }


    private HttpResponseContext trade(HttpRequestContext request, User user) {
        Trading manager = Trading.getInstance();
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");
        String[] parts;

        switch (request.getMethod()) {
            case "GET":
                response.setResponseBody(manager.showMarketplace());
                response.setStatusCode("200 OK");
                break;

            case "POST":
                parts = request.getResource().split("/");

                if (parts.length == 3) {
                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        JsonNode jsonNode = mapper.readTree(request.getBody());

                        if (jsonNode.has("Card2Trade")) {
                            if (manager.tradeCards(user, parts[2], jsonNode.get("Card2Trade").asText())) {
                                response.setStatusCode("200 OK");
                                response.setResponseBody("Cards traded successfully.");
                            } else {
                                response.setStatusCode("400 Bad Request");
                                response.setResponseBody("Error while trading cards.");
                            }
                        } else {
                            response.setStatusCode("400 Bad Request");
                            response.setResponseBody("Missing parameter 'Card2Trade'.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        response.setStatusCode("400 Bad Request");
                        response.setResponseBody("Error while processing the request.");
                    }
                } else {
                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        JsonNode jsonNode = mapper.readTree(request.getBody());

                        if (jsonNode.has("Id") && jsonNode.has("CardToTrade") && jsonNode.has("Type") && jsonNode.has("MinimumDamage")) {
                            if (manager.card2market(user, jsonNode.get("Id").asText(), jsonNode.get("CardToTrade").asText(), (float) jsonNode.get("MinimumDamage").asDouble(), jsonNode.get("Type").asText())) {
                                response.setStatusCode("201 Created");
                                response.setResponseBody("Cards put up for trade successfully.");
                            } else {
                                response.setStatusCode("400 Bad Request");
                                response.setResponseBody("Error while putting up cards for trade.");
                            }
                        } else {
                            response.setStatusCode("400 Bad Request");
                            response.setResponseBody("Missing one or more parameters.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        response.setStatusCode("400 Bad Request");
                        response.setResponseBody("Error while processing the request.");
                    }
                }

                break;

            case "DELETE":
                parts = request.getResource().split("/");

                if (parts.length == 3) {
                    if (manager.removeTrade(user, parts[2])) {
                        response.setStatusCode("200 OK");
                        response.setResponseBody("Trade proposal revoked.");
                    } else {
                        response.setStatusCode("400 Bad Request");
                        response.setResponseBody("Error while revoking trade proposal.");
                    }
                } else {
                    response.setStatusCode("400 Bad Request");
                    response.setResponseBody("Invalid request.");
                }

                break;

            default:
                response.setStatusCode("400 Bad Request");
                response.setResponseBody("Invalid request method.");
                break;
        }

        return response;
    }


    private HttpResponseContext battle(HttpRequestContext request, User user) {
        HttpResponseContext response = new HttpResponseContext("400 Bad Request");

        if ("POST".equals(request.getMethod())) {
            Battle manager = Battle.getInstance();
            String battleResult = manager.registerAndBattleUser(user);

            if (battleResult != null) {
                response.setStatusCode("200 OK");
                response.setResponseBody(battleResult);
            } else {
                response.setStatusCode("404 Not Found");
                response.setResponseBody("Could not find an opponent for the battle.");
            }
        }

        return response;
    }



    private User authorize(HttpRequestContext request){
        User user = null;

        if (request.getHeaders().containsKey("authorization:")){
            Handler_User manager = Handler_User.getInstance();
            user = manager.authorizeUser(request.getHeaders().get("authorization:"));
        }

        return user;
    }
}
