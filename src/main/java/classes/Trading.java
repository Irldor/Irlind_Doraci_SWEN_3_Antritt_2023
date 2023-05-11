package classes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Trading {

    // Static member holds only one instance of the Trading class
    private static Trading single_instance;

    // Providing Global point of access
    public static Trading getInstance() {
        // Initialize the instance if it's null
        try {
            if (single_instance == null) {
                single_instance = new Trading();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return the singleton instance
        return single_instance;
    }


    public String DemonstrateMarketSpot() {
        // Declare variables
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        try {
            // Get connection instance
            conn = DB.getInstance().getConnection();

            // Prepare the statement
            ps = conn.prepareStatement("SELECT tradeid, cards.cardid, name,damage, owner, mindamage, type " +
                                           "FROM marketplace " +
                                           "JOIN cards " +
                                           "ON cards.cardID = marketplace.cardID;");

            // Execute the query
            rs = ps.executeQuery();

            // Fetch the results and add them to the array node
            while (rs.next()) {
                ObjectNode transaction = mapper.createObjectNode();
                // Add each column of the row as a field in the ObjectNode
                transaction.put("TradeID", rs.getString(1));
                transaction.put("CardID", rs.getString(2));
                transaction.put("Name", rs.getString(3));
                transaction.put("Damage", rs.getString(4));
                transaction.put("Owner", rs.getString(5));
                transaction.put("MinimumDamage", rs.getString(6));
                transaction.put("Type", rs.getString(7));

                // Add the populated ObjectNode to the ArrayNode
                arrayNode.add(transaction);
            }

            // Close the statement and result set
            rs.close();
            ps.close();

            // Close the connection
            conn.close();

            // Return the array node as a pretty-printed JSON string
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

        } catch (SQLException | JsonProcessingException e) {
            // Handle any exceptions and return null
            e.printStackTrace();
            return null;
        } finally {
            // Close the statement, result set, and connection if they're not null
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean TradeCardMarketplace(User user, String tradeID, String cardID, float minimumDamage, String type) {
        // Declare variables
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get connection instance
            conn = DB.getInstance().getConnection();

            // Check if the marketplace already contains the card
            if (checkMarketplaceFor(cardID)) {
                return false;
            }

            // Prepare the statement to check if the user owns the card
            ps = conn.prepareStatement("SELECT COUNT(cardid) " +
                    "FROM cards " +
                    "WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
            ps.setString(1, user.getUsername());
            ps.setString(2, cardID);

            // Execute the query
            rs = ps.executeQuery();

            // Check if the user owns the card
            if (!rs.next() || rs.getInt(1) != 1) {
                rs.close();
                ps.close();
                conn.close();
                return false;
            }

            // Close the result set and statement
            rs.close();
            ps.close();

            // Prepare the statement to insert the card into the marketplace
            ps = conn.prepareStatement("INSERT INTO marketplace(tradeid, cardid, mindamage, type) " +
                    "VALUES(?,?,?,?);");
            ps.setString(1, tradeID);
            ps.setString(2, cardID);
            ps.setFloat(3, minimumDamage);
            ps.setString(4, type);

            // Execute the update query
            int affectedRows = ps.executeUpdate();

            // Close the statement
            ps.close();

            // Check if the card was successfully inserted into the marketplace
            if (affectedRows != 1) {
                conn.close();
                return false;
            }

            // Close the connection
            conn.close();

            return true;

        } catch (SQLException e) {
            // Handle any exceptions and return false
            e.printStackTrace();
            return false;
        } finally {
            // Close the result set, statement, and connection if they're not null
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean Tradingdelete(User user, String id) {
        // Declare variables
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get connection instance
            conn = DB.getInstance().getConnection();

            // Prepare the statement to check if the user owns the card
            ps = conn.prepareStatement("SELECT cards.owner " +
                                            "FROM cards " +
                                            "JOIN marketplace " +
                                            "ON cards.cardID = marketplace.cardID " +
                                            "WHERE marketplace.tradeID = ?;");
            ps.setString(1, id);

            // Execute the query
            rs = ps.executeQuery();

            // Check if the user owns the card
            if (!rs.next() || !rs.getString(1).equals(user.getUsername())) {
                rs.close();
                ps.close();
                conn.close();
                return false;
            }

            // Close the result set and statement
            rs.close();
            ps.close();

            // Prepare the statement to remove the trade
            ps = conn.prepareStatement("DELETE FROM marketplace " +
                                           "WHERE tradeID = ?;");
            ps.setString(1, id);

            // Execute the update query
            int affectedRows = ps.executeUpdate();

            // Close the statement
            ps.close();

            // Check if the trade was successfully removed
            if (affectedRows != 1) {
                conn.close();
                return false;
            }

            // Close the connection
            conn.close();

            return true;

        } catch (SQLException e) {
            // Handle any exceptions and return false
            e.printStackTrace();
            return false;
        } finally {
            // Close the result set, statement, and connection if they're not null
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean checkMarketplaceFor(String cardID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getInstance().getConnection();

            ps = conn.prepareStatement("SELECT COUNT(cardid) FROM marketplace WHERE cardid = ?;");
            ps.setString(1, cardID);

            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    // Method to trade cards between users based on the tradeID and cardID
    public boolean Cards_Trading(User user, String tradeID, String cardID) {
        // Check if the user is null; if yes, return false
        if (user == null) {
            return false;
        }

        // Declare database-related variables
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get a connection instance from the database
            conn = DB.getInstance().getConnection();

            // Check if the card is already in the marketplace; if yes, return false
            if (checkMarketplaceFor(cardID)) {
                return false;
            }

            // Declare variables to store card data
            String cardName;
            float cardDamage;

            // Prepare a query to get the card details based on the user and cardID
            ps = conn.prepareStatement("SELECT name, damage " +
                                           "FROM cards " +
                                           "WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
            ps.setString(1, user.getUsername());
            ps.setString(2, cardID);

            // Execute the query and get the results
            rs = ps.executeQuery();

            // If the card is not found, close resources and return false
            if (!rs.next()) {
                rs.close();
                ps.close();
                conn.close();
                return false;
            }

            // Store the card data from the result set
            cardName = rs.getString(1);
            cardDamage = rs.getFloat(2);

            // Close the result set and prepared statement
            rs.close();
            ps.close();

            // Declare variables to store offered card data
            String offeredCardID;
            String offeredCardOwner;
            float minDamage;
            String type;

            // Prepare a query to get the offered card data based on the tradeID
            ps = conn.prepareStatement("SELECT marketplace.cardID, owner, minDamage, type " +
                                           "FROM marketplace " +
                                           "JOIN cards " +
                                           "ON marketplace.cardID = cards.cardID " +
                                           "WHERE tradeID = ?;");
            ps.setString(1, tradeID);

            // Execute the query and get the results
            rs = ps.executeQuery();

            // If the offered card is not found, close resources and return false
            if (!rs.next()) {
                rs.close();
                ps.close();
                conn.close();
                return false;
            }

            // Store the offered card data from the result set
            offeredCardID = rs.getString(1);
            offeredCardOwner = rs.getString(2);
            minDamage = rs.getFloat(3);
            type = rs.getString(4);

            // Close the result set and prepared statement
            rs.close();
            ps.close();

            // Get the instance of Handler_Card
            Handler_Card cardHandler = Handler_Card.getInstance();

            // Check the trade conditions based on the card type
            if (type.equalsIgnoreCase("monster")) {
                // If card type is a monster, but the determined category is spell, return false
                if (cardHandler.determineMonsterCategory(cardName) == MonsterCategory.Spell) {
                    return false;
                }
            } else {
                // If the determined category of the card doesn't match the specified type, return false
                if (cardHandler.determineMonsterCategory(cardName) != cardHandler.determineMonsterCategory(type)) {
                    return false;
                }
            }

            // If the damage value of the card is less than the minimum required damage, return false
            if (cardDamage < minDamage) {
                return false;
            }

            // If the user offering the card is the same as the user attempting to make the trade, return false
            if (offeredCardOwner.equalsIgnoreCase(user.getUsername())) {
                return false;
            }

            // Prepare and execute SQL statements to update the owner of the card
            try (PreparedStatement ps1 = conn.prepareStatement("UPDATE cards SET owner = ? WHERE cardID = ?")) {
                ps1.setString(1, offeredCardOwner);
                ps1.setString(2, cardID);
                ps1.executeUpdate();
            }

            // Prepare and execute SQL statements to update the owner of the offered card
            try (PreparedStatement ps2 = conn.prepareStatement("UPDATE cards SET owner = ? WHERE cardID = ?")) {
                ps2.setString(1, user.getUsername());
                ps2.setString(2, offeredCardID);
                ps2.executeUpdate();
            }

            // Prepare and execute SQL statements to remove the trade from the marketplace
            try (PreparedStatement ps3 = conn.prepareStatement("DELETE FROM marketplace WHERE tradeID = ?;")) {
                ps3.setString(1, tradeID);
                ps3.executeUpdate();
            }

            // Close the connection
            conn.close();


            // Return true as the trade has been successfully completed
            return true;

        } catch (SQLException e) {
            // Print the stack trace in case of any SQL exceptions
            e.printStackTrace();
        } finally {
            // Close resources in the finally block to ensure they are closed even if an exception occurs
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

// Return false if the trade could not be completed
        return false;
    }

}
