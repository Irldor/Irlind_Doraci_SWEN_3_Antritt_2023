package restServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import classes.Battle;
import classes.Trading;
import classes.Handler_User;
import classes.Card;
import classes.DB;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import classes.Handler_Card;
import classes.User;
import java.sql.Connection;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Handle request and send response
public class ResponseGenerator {

    BufferedWriter writer;

    public ResponseGenerator(BufferedWriter writer) {
        this.writer = writer;
    }

    public void generateResponse(HttpRequest request) {
        // Initialize the response object with a default "400 Bad Request" status
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Check if the request and its headers are not null
        if (request != null && request.getHeaders() != null) {
            // Split the request resource by slashes
            String[] parts = request.getResource().split("/");
            User user;

            // Check if the length of the parts array is 2 or 3
            if (parts.length == 2 || parts.length == 3) {
                // Assign the first part after the initial slash to the variable "resource"
                String resource = parts[1];

                if ("delete".equals(resource)) {
                    // If the resource is "delete", call the deleteAll method
                    response = clearAll(request);
                } else if ("users".equals(resource)) {
                    // If the resource is "users", call the users method
                    response = users(request);
                } else if ("sessions".equals(resource)) {
                    // If the resource is "sessions", call the sessions method
                    response = session(request);
                } else if ("packages".equals(resource)) {
                    // If the resource is "packages", call the packages method
                    response = packages(request);
                } else if ("transactions".equals(resource) && parts.length == 3 && "packages".equals(parts[2])) {
                    // If the resource is "transactions" and the third part is "packages", call the transactionsPackages method
                    user = authorize(request);
                    response = (user != null) ? PackageExchange(user, request) : createUnauthorizedResponse("Access denied");
                } else if ("cards".equals(resource)) {
                    // If the resource is "cards", call the showCards method
                    user = authorize(request);
                    response = (user != null) ? CardDispplay(user, request) : createUnauthorizedResponse("Access denied");
                } else if ("deck".equals(resource)) {
                    // If the resource is "deck", call the requestDeck method
                    user = authorize(request);
                    response = (user != null) ? DeckCall(user, request) : createUnauthorizedResponse("Access denied");
                } else if ("stats".equals(resource)) {
                    // If the resource is "stats", call the stats method
                    user = authorize(request);
                    response = (user != null) ? Information(user, request) : createUnauthorizedResponse("Access denied");
                } else if ("score".equals(resource)) {
                    // If the resource is "score", call the scoreboard method
                    user = authorize(request);
                    response = (user != null) ? Resultsdisplay(request) : createUnauthorizedResponse("Access denied");
                } else if ("tradings".equals(resource)) {
                    // If the resource is "tradings", call the trade method
                    user = authorize(request);
                    response = (user != null) ? Cardstrade(request, user) : createUnauthorizedResponse("Access denied");
                } else if ("battles".equals(resource)) {
                    // If the resource is "battles", call the battle method
                    user = authorize(request);
                    response = (user != null) ? battle(request, user) : createUnauthorizedResponse("Access denied");
                }
            }
        }

        // Send the response using the writer
        sendResponse(response, writer);
    }

    private static HttpResponse createUnauthorizedResponse(String message) {
        // Create a new HttpResponse object with a "401 Unauthorized" status
        HttpResponse response = new HttpResponse("401 Unauthorized");
        // Set the response body with the provided message
        response.setResponseBody(message);
        // Return the created response object
        return response;
    }

    private static void sendResponse(HttpResponse response, BufferedWriter writer) {
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

    private HttpResponse clearAll(HttpRequest request) {
        // Start with a default response of "400 Bad Request"
        HttpResponse response = new HttpResponse("400 Bad Request");
        // Obtain an instance of the Handler_User class
        Handler_User handlerUser = Handler_User.getInstance();

        // Check if the request method is DELETE
        if (request.getMethod().equals("DELETE")) {
            try (Connection conn = DB.getInstance().getConnection()) {
                // Initialize an array of table names to be deleted
                String[] tables = {"packages", "marketplace", "cards", "users"};

                // Iterate over the table names
                for (String table : tables) {
                    // Prepare a statement to delete all records from the current table
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + ";")) {
                        // Execute the update
                        ps.executeUpdate();
                    }
                }

                // Set the response status to "200 OK" and the response body to "Successfully deleted"
                response.setStatusCode("200 OK");
                response.setResponseBody("Successfully deleted");
            } catch (SQLException e) {
                // Print the stack trace for the SQLException
                e.printStackTrace();

                // Set the response status to "409 Conflict" and the response body to "Error while deleting"
                response.setStatusCode("409 Conflict");
                response.setResponseBody("Error while deleting");
            }
        }

        // Return the response
        return response;
    }


    private HttpResponse users(HttpRequest request) {
        // Initialize default response as "400 Bad Request"
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Get instance of user handler
        Handler_User handlerUser = Handler_User.getInstance();

        // Extract the request method
        String requestMethod = request.getMethod();

        // Split the resource path into parts
        String[] parts = request.getResource().split("/");

        // Authorize user and get the user object
        User user = authorize(request);

        // Create an ObjectMapper for JSON processing
        ObjectMapper mapper = new ObjectMapper();

        try {
            // If it's a GET request and user is authorized and path length is 3 and username matches the one in the path
            if ("GET".equals(requestMethod) && user != null && parts.length == 3 && user.getUsername().equals(parts[2])) {
                // Get the user information
                String userInfo = user.info();

                // Set the status code and response body based on whether user info exists
                response.setStatusCode(userInfo != null ? "200 OK" : "404 Not Found");
                response.setResponseBody(userInfo != null ? userInfo : "User not found");
            }
            // If it's a POST request
            else if ("POST".equals(requestMethod)) {
                // Parse the request body to a JSON node
                JsonNode jsonNode = mapper.readTree(request.getBody());

                // If the JSON node has "Username" and "Password" fields
                if (jsonNode.has("Username") && jsonNode.has("Password")) {
                    // Attempt to sign up user and set the status code and response body based on the result
                    boolean isSignedUp = handlerUser.signUpUser(jsonNode.get("Username").asText(), jsonNode.get("Password").asText());
                    response.setStatusCode(isSignedUp ? "201 Created" : "409 Conflict");
                    response.setResponseBody(isSignedUp ? "User created" : "Username already exists");
                }
            } else if ("PUT".equals(requestMethod)) {
                // Authorize user and check if the resource is valid
                user = authorize(request);
                if (user != null) {
                    String[] editUser = request.getResource().split("/");
                    if (editUser.length == 3) {
                        // Check if the requested user is the authorized user
                        if (user.getUsername().equals(editUser[2])) {
                            // Update user information
                            try {
                                JsonNode jsonNode = mapper.readTree(request.getBody());
                                if (jsonNode.has("Name") && jsonNode.has("Bio") && jsonNode.has("Image")) {
                                    if (user.updateInfo(jsonNode.get("Name").asText(), jsonNode.get("Bio").asText(), jsonNode.get("Image").asText())) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the response
        return response;
    }


    private HttpResponse session(HttpRequest request) {
        Handler_User manager = Handler_User.getInstance();
        HttpResponse response = new HttpResponse("400 Bad Request");
        ObjectMapper mapper = new ObjectMapper();

        String requestMethod = request.getMethod();
        if ("POST".equals(requestMethod)) {
            try {
                JsonNode jsonNode = mapper.readTree(request.getBody());
                if (jsonNode.has("Username") && jsonNode.has("Password")) {
                    String username = jsonNode.get("Username").asText();
                    String password = jsonNode.get("Password").asText();
                    if (manager.UserSignIn(username, password)) {
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
                    if (manager.SignOutUser(username, password)) {
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

    private HttpResponse packages(HttpRequest request) {
        // Default response is 400 (Bad Request)
        HttpResponse response = new HttpResponse("400 Bad Request");

        // We only accept POST requests in this handler
        if (!request.getMethod().equals("POST")) {
            return response;
        }

        // Check if the user has authorization
        // Get the authorization header from the request
        String authHeader = request.getHeaders().get("authorization:");
        // If the authorization header is null or the role verification fails, return a 403 (Forbidden) response
        if (authHeader == null || !Handler_User.getInstance().roleVerification(authHeader)) {
            response.setStatusCode("403 Forbidden");
            response.setResponseBody("Access forbidden");
            return response;
        }

        // Parse cards from the request body
        // Parse the request body into a list of cards
        List<Card> cards = parseCardsFromRequestBody(request.getBody());
        // If parsing fails or the number of cards is not 5, return the default response
        if (cards == null || cards.size() != 5) {
            return response;
        }

        // Register each card and create a package
        // If registration or package creation fails, delete the registered cards
        if (!registerCardsAndCreatePackage(cards)) {
            deleteRegisteredCards(cards);
        } else {
            // If registration and package creation are successful, return a 201 (Created) response
            response.setStatusCode("201 Created");
            response.setResponseBody("Package created.");
        }

        return response;
    }

    private List<Card> parseCardsFromRequestBody(String body) {
        // Create a new ObjectMapper for parsing the JSON body
        ObjectMapper mapper = new ObjectMapper();
        // Configure the mapper to accept case insensitive properties
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        try {
            // Try to parse the body into a list of cards and return it
            return mapper.readValue(body, new TypeReference<List<Card>>() {
            });
        } catch (IOException e) {
            // If parsing fails, print the stack trace and return null
            e.printStackTrace();
            return null;
        }
    }

    private boolean registerCardsAndCreatePackage(List<Card> cards) {
        // Get the card handler instance
        Handler_Card manager = Handler_Card.getInstance();
        // Try to register each card
        for (Card card : cards) {
            if (!manager.logCard(card.getId(), card.getName(), card.getDamage())) {
                // If registration fails, return false
                return false;
            }
        }
        // Try to create a package with the registered cards and return the result
        return manager.createCardPackage(cards);
    }

    private void deleteRegisteredCards(List<Card> cards) {
        // Get the card handler instance
        Handler_Card manager = Handler_Card.getInstance();
        // Delete each card
        for (Card card : cards) {
            manager.removeCard(card.getId());
        }
    }


    private HttpResponse PackageExchange(User user, HttpRequest request) {
        // Get an instance of the card handler
        Handler_Card manager = Handler_Card.getInstance();

        // Initialize default bad request response
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Check if the request method is POST
        if ("POST".equals(request.getMethod())) {
            // Try to assign a card package to the user
            boolean packageAssigned = manager.assignPackageToUser(user);

            // If package assignment is successful, set response status and body
            if (packageAssigned) {
                response.setStatusCode("200 OK");
                response.setResponseBody("Package is successfully acquired by the user.");
            } else {
                // If package assignment fails, set error status and body
                response.setStatusCode("409 Conflict");
                response.setResponseBody("Error acquiring package.");
            }
        }

        // Return the response object
        return response;
    }


    private HttpResponse CardDispplay(User user, HttpRequest request) {
        // Initialize a response with a default "400 Bad Request" status
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Check if the request method is "GET"
        if (isGetMethod(request)) {
            // Process the GET request and update the response
            response = processGetRequest(user, response);
        }

        // Return the response
        return response;
    }

    private boolean isGetMethod(HttpRequest request) {
        return "GET".equals(request.getMethod());
    }

    private HttpResponse processGetRequest(User user, HttpResponse response) {
        // Get the JSON response string of the user's cards
        String jsonResponse = getJsonResponse(user);

        // Update the response status and body based on the JSON response
        response = updateResponse(response, jsonResponse);

        return response;
    }

    private String getJsonResponse(User user) {
        return Handler_Card.getInstance().showUserCards(user);
    }

    private HttpResponse updateResponse(HttpResponse response, String jsonResponse) {
        // If the JSON response is not null, set the status to "200 OK" and the body to the JSON response
        if (jsonResponse != null) {
            response.setStatusCode("200 OK");
            response.setResponseBody(jsonResponse);
        } else {
            // If the JSON response is null, set the status to "404 Error" and the body to "No cards available."
            response.setStatusCode("404 Error");
            response.setResponseBody("No cards available.");
        }

        return response;
    }


    private HttpResponse DeckCall(User user, HttpRequest request) {
        // Initialize a response with a default "400 Bad Request" status
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Get an instance of Handler_Card
        Handler_Card manager = Handler_Card.getInstance();

        // Determine the request method
        String requestMethod = request.getMethod();

        // If the request method is "GET", process the GET request
        if ("GET".equals(requestMethod)) {
            response = processGetRequest(user, response, manager);
        }
        // If the request method is "PUT", process the PUT request
        else if ("PUT".equals(requestMethod)) {
            response = processPutRequest(user, request, response, manager);
        }

        // Return the response
        return response;
    }

    private HttpResponse processGetRequest(User user, HttpResponse response, Handler_Card manager) {
        // Get the JSON representation of the user's deck
        String jsonDeck = manager.showDeck(user);

        // If the JSON deck is not null, set the response status to "200 OK" and the body to the JSON deck
        if (jsonDeck != null) {
            response.setStatusCode("200 OK");
            response.setResponseBody(jsonDeck);
        } else {
            // If the JSON deck is null, set the response status to "404 Not Found" and the body to "Deck not found."
            response.setStatusCode("404 Not Found");
            response.setResponseBody("Deck not found.");
        }

        return response;
    }

    private HttpResponse processPutRequest(User user, HttpRequest request, HttpResponse response, Handler_Card manager) {
        // Create an ObjectMapper to convert JSON to a List<String>
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert the request body to a list of card IDs
            List<String> cardIds = mapper.readValue(request.getBody(), new TypeReference<List<String>>() {
            });

            // If the size of the card IDs list is 4, attempt to generate a deck
            if (cardIds.size() == 4) {
                if (manager.generateDeck(user, cardIds)) {
                    // If the deck is successfully generated, set the response status to "201 Created" and the body to "Deck created."
                    response.setStatusCode("201 Created");
                    response.setResponseBody("Deck created.");
                } else {
                    // If the deck is not successfully generated, set the response status to "409 Conflict" and the body to "Error while creating deck."
                    response.setStatusCode("409 Conflict");
                    response.setResponseBody("Error while creating deck.");
                }
            } else {
                // If the size of the card IDs list is not 4, set the response status to "400 Bad Request" and the body to "Invalid number of cards. The deck must have exactly 4 cards."
                response.setStatusCode("400 Bad Request");
                response.setResponseBody("Invalid number of cards. The deck must have exactly 4 cards.");
            }
        } catch (IOException e) {
            // If an IOException occurs, print the stack trace and set the response status to "400 Bad Request" and the body to "Invalid request body."
            e.printStackTrace();
            response.setStatusCode("400 Bad Request");
            response.setResponseBody("Invalid request body.");
        }

        return response;
    }


    private HttpResponse Information(User user, HttpRequest request) {
        // Create a new object mapper to convert Java objects to JSON
        ObjectMapper mapper = new ObjectMapper();

        // Initialize the HttpResponse object with a default status code of "400 Bad Request"
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Check if the HTTP method is "GET"
        if (request.getMethod().equalsIgnoreCase("GET")) {
            // Try to get the user stats and convert them to JSON format
            try {
                String userStatsJson = mapper.writeValueAsString(user.stats());

                // If successful, set the response status code to "200 OK" and the response body to the user stats in JSON format
                response.setStatusCode("200 OK");
                response.setResponseBody(userStatsJson);
            } catch (JsonProcessingException e) {
                // Handle any exceptions that occur when processing the JSON
                e.printStackTrace();

                // Set the response status code to "500 Internal Server Error" and the response body to an error message
                response.setStatusCode("500 Internal Server Error");
                response.setResponseBody("Error retrieving user stats.");
            }
        }

        // Return the response
        return response;
    }


    private HttpResponse Resultsdisplay(HttpRequest request) {
        // Get the singleton instance of the Battle class
        Battle manager = Battle.getInstance();

        // Initialize the HttpResponse object with a default status code of "400 Bad Request"
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Check if the HTTP method is "GET"
        if (request.getMethod().equalsIgnoreCase("GET")) {
            // Try to fetch the scoreboard and convert it to JSON format
            try {
                // Create a new object mapper to convert Java objects to JSON
                ObjectMapper mapper = new ObjectMapper();

                // Get the scoreboard from the manager and convert it to JSON format
                String scoreboardJson = mapper.writeValueAsString(manager.fetchScoreboard());

                // If successful, set the response status code to "200 OK" and the response body to the scoreboard in JSON format
                response.setStatusCode("200 OK");
                response.setResponseBody(scoreboardJson);
            } catch (JsonProcessingException e) {
                // Handle any exceptions that occur when processing the JSON
                e.printStackTrace();

                // Set the response status code to "500 Internal Server Error" and the response body to an error message
                response.setStatusCode("500 Internal Server Error");
                response.setResponseBody("Error retrieving scoreboard.");
            }
        }

        // Return the response
        return response;
    }

    /**
     * Handles the HTTP requests related to battles.
     *
     * @param request The HTTP request context.
     * @param user    The user making the request.
     * @return The HTTP response context.
     */
    private HttpResponse battle(HttpRequest request, User user) {
        // Initialize a new HTTP response with a default status of "400 Bad Request"
        HttpResponse response = new HttpResponse("400 Bad Request");

        // Check if the HTTP request method is POST
        if ("POST".equals(request.getMethod())) {
            // Get an instance of the Battle manager
            Battle manager = Battle.getInstance();

            // Register the user for a battle and retrieve the result
            String battleResult = manager.registerAndBattleUser(user);

            // If a battle result exists...
            if (battleResult != null) {
                // Set the HTTP response status to "200 OK" and return the battle result
                response.setStatusCode("200 OK");
                response.setResponseBody(battleResult);
            } else {
                // Otherwise, set the HTTP response status to "404 Not Found" and return an error message
                response.setStatusCode("404 Not Found");
                response.setResponseBody("Could not find an opponent for the battle.");
            }
        }

        // Return the HTTP response
        return response;
    }


    private User authorize(HttpRequest request) {
        // Check for the presence of "authorization" key in the headers of the HTTP request
        if (!request.getHeaders().containsKey("authorization:")) {
            // If the key is absent, return null indicating unsuccessful authorization
            return null;
        }

        // Extract the "authorization" value from the headers
        String authorizationValue = request.getHeaders().get("authorization:");

        // Instantiate the user handler
        Handler_User userHandler = Handler_User.getInstance();

        // Call the authorize method of the user handler with the extracted authorization value
        // and return the resulting User object (could be null if authorization fails)
        return userHandler.authorize(authorizationValue);
    }


    private HttpResponse Cardstrade(HttpRequest request, User user) {
        Trading manager = Trading.getInstance();
        HttpResponse response = new HttpResponse("400 Bad Request");
        String[] parts;

        switch (request.getMethod()) {
            case "GET":
                response.setResponseBody(manager.DemonstrateMarketSpot());
                response.setStatusCode("200 OK");
                break;

            case "POST":
                parts = request.getResource().split("/");

                if (parts.length == 3) {
                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        JsonNode jsonNode = mapper.readTree(request.getBody());

                        if (jsonNode.has("Card2Trade")) {
                            if (manager.Cards_Trading(user, parts[2], jsonNode.get("Card2Trade").asText())) {
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
                            if (manager.TradeCardMarketplace(user, jsonNode.get("Id").asText(), jsonNode.get("CardToTrade").asText(), (float) jsonNode.get("MinimumDamage").asDouble(), jsonNode.get("Type").asText())) {
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
                    if (manager.Tradingdelete(user, parts[2])) {
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

}

