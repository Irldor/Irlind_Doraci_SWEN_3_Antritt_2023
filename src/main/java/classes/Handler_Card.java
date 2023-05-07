package classes;


import com.fasterxml.jackson.core.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.*;
import java.util.*;

public class Handler_Card {

    private static Handler_Card single_instance = null;

    public static Handler_Card getInstance()
    {
        if (single_instance == null) {
            single_instance = new Handler_Card();
        }
        return single_instance;
    }

    // This method creates a package of cards, ensuring that each card is available and
// not in any collection. It returns true if the package is created successfully and
// false otherwise.
    public boolean createCardPackage(List<Card> cards) {
        // Check if the input is valid and contains exactly 5 cards
        if (cards == null || cards.size() != 5) {
            return false;
        }

        try (Connection conn = DB.getInstance().getConnection()) {
            // Check if all cards are available and not in any collection
            if (!areCardsAvailable(cards, conn)) {
                return false;
            }

            // Insert the package with the given cards into the database
            if (!insertPackage(cards, conn)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // This method checks if all the cards in the given list are available and not in
// any collection. It returns true if all cards are available, and false otherwise.
    private boolean areCardsAvailable(List<Card> cards, Connection conn) throws SQLException {
        for (Card card : cards) {
            String cardId = card.getId();
            String query = "SELECT COUNT(cardid) FROM cards WHERE cardid = ? AND collection IS NULL;";

            // Execute the query to check if the card is available and not in any collection
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, cardId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) != 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // This method inserts a package with the given list of cards into the database.
// It returns true if the package is inserted successfully, and false otherwise.
    private boolean insertPackage(List<Card> cards, Connection conn) throws SQLException {
        String query = "INSERT INTO packages(cardid_1, cardid_2, cardid_3, cardid_4, cardid_5) VALUES(?,?,?,?,?);";

        // Execute the query to insert the package with the given cards into the database
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < 5; i++) {
                ps.setString(i + 1, cards.get(i).getId());
            }
            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        }
    }

    // This method retrieves all the cards owned by the specified user and returns
// them as a JSON string. If an error occurs, it returns null.
    public String showUserCards(User user) {
        try (Connection conn = DB.getInstance().getConnection()) {
            // Prepare the query to select all cards owned by the user
            String query = "SELECT cardid, name, damage FROM cards WHERE owner = ?;";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, user.getUsername());

                // Execute the query and convert the result to a JSON string
                String json = convertResultToJson(ps.executeQuery());
                return json;
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // This method retrieves the user's deck of cards and returns them as a JSON string.
// If an error occurs, it returns null.
    public String showUserDeck(User user) {
        try (Connection conn = DB.getInstance().getConnection()) {
            // Prepare the query to select the user's deck of cards
            String query = "SELECT cardid, name, damage FROM cards WHERE owner = ? AND collection = 'deck';";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, user.getUsername());

                // Execute the query and convert the result to a JSON string
                String json = convertResultToJson(ps.executeQuery());
                return json;
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertResultToJson(ResultSet rs) throws SQLException, JsonProcessingException {
        // Create a new list to store the cards as maps
        List<Map<String, String>> cards = new ArrayList<>();

        // Iterate through the result set and create a map for each card
        while (rs.next()) {
            Map<String, String> card = new HashMap<>();
            card.put("ID", rs.getString(1));
            card.put("Name", rs.getString(2));
            card.put("Damage", rs.getString(3));
            cards.add(card);
        }
        rs.close();

        // Use the Gson library to convert the list of maps to a JSON string
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(cards);
    }

    public boolean assignPackageToUser(User user) {
        // Establish a database connection
        try (Connection conn = DB.getInstance().getConnection()) {

            // Check if there are any packages available
            int packageID = findAvailablePackage(conn);

            // Return false if no package found
            if (packageID == -1) {
                return false;
            }

            // Attempt to purchase the package, return false if the user does not have enough coins
            if (!user.buyPackage()) {
                return false;
            }

            // Assign the cards in the package to the user
            assignCardsToUser(conn, user.getUsername(), packageID);

            // Remove the acquired package from the database
            deletePackage(conn, packageID);

        } catch (SQLException e) {
            // Log any SQL exceptions and return false
            e.printStackTrace();
            return false;
        }

        // Return true if the method executed successfully
        return true;
    }

    private int findAvailablePackage(Connection conn) throws SQLException {
        // Retrieve an available package from the database
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM packages LIMIT 1;");
             ResultSet rs = ps.executeQuery()) {

            // Return packageID if a package exists, otherwise return -1
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private void assignCardsToUser(Connection conn, String username, int packageID) throws SQLException {
        // Update the owner and collection of the cards in the package
        try (PreparedStatement ps = conn.prepareStatement("UPDATE cards\n" +
                "SET owner = ?, collection = 'stack'\n" +
                "WHERE cardID IN (\n" +
                "  SELECT cardid_1 FROM packages WHERE packageid = ?\n" +
                "  UNION\n" +
                "  SELECT cardid_2 FROM packages WHERE packageid = ?\n" +
                "  UNION\n" +
                "  SELECT cardid_3 FROM packages WHERE packageid = ?\n" +
                "  UNION\n" +
                "  SELECT cardid_4 FROM packages WHERE packageid = ?\n" +
                "  UNION\n" +
                "  SELECT cardid_5 FROM packages WHERE packageid = ?\n" +
                ");")) {
            ps.setString(1, username);
            ps.setInt(2, packageID);
            ps.setInt(3, packageID);
            ps.setInt(4, packageID);
            ps.setInt(5, packageID);
            ps.setInt(6, packageID);
            ps.executeUpdate();
        }
    }

    private void deletePackage(Connection conn, int packageID) throws SQLException {
        // Remove the specified package from the database
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM packages WHERE packageid = ?;")) {
            ps.setInt(1, packageID);
            ps.executeUpdate();
        }
    }

    public Element determineElementType(String element) {
        // Convert the input string to lowercase for case-insensitive comparisons
        String lowerCaseElement = element.toLowerCase();

        // Check if the element contains "water", "fire", or none and return the corresponding Element enum
        if (lowerCaseElement.contains("water")) {
            return Element.Water;
        } else if (lowerCaseElement.contains("fire")) {
            return Element.Fire;
        } else {
            return Element.Normal;
        }
    }

    public MonsterCategory determineMonsterCategory(String name) {
        // Convert the input string to lowercase for case-insensitive comparisons
        String lowerCaseName = name.toLowerCase();

        // Check if the name contains any of the specific monster categories and return the corresponding MonsterCategory enum
        if (lowerCaseName.contains("spell")) {
            return MonsterCategory.Spell;
        } else if (lowerCaseName.contains("dragon")) {
            return MonsterCategory.Dragon;
        } else if (lowerCaseName.contains("fireelf")) {
            return MonsterCategory.FireElf;
        } else if (lowerCaseName.contains("goblin")) {
            return MonsterCategory.Goblin;
        } else if (lowerCaseName.contains("knight")) {
            return MonsterCategory.Knight;
        } else if (lowerCaseName.contains("kraken")) {
            return MonsterCategory.Kraken;
        } else if (lowerCaseName.contains("ork")) {
            return MonsterCategory.Ork;
        } else if (lowerCaseName.contains("wizard")) {
            return MonsterCategory.Wizard;
        }

        // Return null if none of the specific monster categories match
        return null;
    }

    public CardDeck getUserDeck(User user) {
        CardDeck deck = null;

        // Establish a database connection
        try (Connection conn = DB.getInstance().getConnection()) {
            // Prepare a query to get cards that belong to the user and are in their deck
            String query = "SELECT cardid, name, damage FROM cards WHERE owner = ? AND collection = 'deck';";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, user.getUsername());
                ResultSet rs = ps.executeQuery();

                // Initialize a list to store the retrieved cards
                List<Card> cards = new ArrayList<>();

                // Loop through the result set and create Card objects, adding them to the list
                while (rs.next()) {
                    String name = rs.getString(2);
                    Card card = new Card(rs.getString(1), name, rs.getFloat(3),
                            determineMonsterCategory(name), determineElementType(name));
                    cards.add(card);
                }

                // Create a CardDeck object using the populated list of cards
                deck = new CardDeck(cards);
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return the CardDeck object or null if an exception occurred
        return deck;
    }

    public boolean registerCard(String id, String name, float damage) {
        // Check if the provided id and name are not empty
        if (!id.isEmpty() && !name.isEmpty()) {
            // Determine the card's element and monster category based on its name
            Element element = determineElementType(name);
            MonsterCategory cardType = determineMonsterCategory(name);

            // Check if the card has a valid element and monster category
            if (element != null && cardType != null) {
                // Establish a database connection
                try (Connection conn = DB.getInstance().getConnection()) {
                    // Prepare a query to insert a new card into the cards table
                    String query = "INSERT INTO cards(cardid, name, damage) VALUES(?,?,?);";
                    try (PreparedStatement ps = conn.prepareStatement(query)) {
                        ps.setString(1, id);
                        ps.setString(2, name);
                        ps.setFloat(3, damage);

                        // Execute the query and check the number of affected rows
                        int affectedRows = ps.executeUpdate();

                        // If no rows were affected, the card registration was unsuccessful
                        if (affectedRows == 0) {
                            return false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                // Card registration was successful
                return true;
            }
        }
        // Card registration was unsuccessful due to empty id or name, or invalid element or monster category
        return false;
    }


    public void deleteCard(String id) {
        // Establish a database connection
        try (Connection conn = DB.getInstance().getConnection()) {
            // Prepare a query to delete a card from the cards table where the card id matches the provided id
            // and the card is not part of any collection
            String query = "DELETE FROM cards WHERE cardid = ? AND collection IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, id);

                // Execute the query to delete the card
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createDeck(User user, List<String> ids) {
        // Check if the provided list of card IDs has the correct size for a deck
        if (ids.size() != 4) {
            return false;
        }

        // Check if the user owns the provided cards and if there are no duplicates
        if (!userOwnsCards(user, ids)) {
            return false;
        }

        // Set all cards owned by the user to the 'stack' collection
        updateCardsCollection(user, "stack");

        // Update the cards in the provided list to be part of the user's 'deck' collection
        updateCardsCollection(user, ids, "deck");

        // Deck creation was successful
        return true;
    }

    private boolean userOwnsCards(User user, List<String> ids) {
        try (Connection conn = DB.getInstance().getConnection()) {
            List<String> uniqueIds = new LinkedList<>();

            for (String cardID : ids) {
                if (uniqueIds.contains(cardID) || Trading.getInstance().marketplaceContains(cardID)) {
                    return false;
                }
                uniqueIds.add(cardID);

                String query = "SELECT COUNT(cardid) FROM cards WHERE cardid = ? AND owner = ?;";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, cardID);
                    ps.setString(2, user.getUsername());
                    ResultSet rs = ps.executeQuery();

                    if (!rs.next() || rs.getInt(1) != 1) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void updateCardsCollection(User user, String collection) {
        try (Connection conn = DB.getInstance().getConnection()) {
            String updateStackQuery = "UPDATE cards SET collection = ? WHERE owner = ?;";
            try (PreparedStatement ps = conn.prepareStatement(updateStackQuery)) {
                ps.setString(1, collection);
                ps.setString(2, user.getUsername());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCardsCollection(User user, List<String> ids, String collection) {
        try (Connection conn = DB.getInstance().getConnection()) {
            String updateDeckQuery = "UPDATE cards SET collection = ? WHERE owner = ? AND cardid = ?;";
            try (PreparedStatement ps = conn.prepareStatement(updateDeckQuery)) {
                for (String cardID : ids) {
                    ps.setString(1, collection);
                    ps.setString(2, user.getUsername());
                    ps.setString(3, cardID);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
