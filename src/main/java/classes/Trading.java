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

    private static Trading single_instance = null;

    public static Trading getInstance()
    {
        if (single_instance == null) {
            single_instance = new Trading();
        }
        return single_instance;
    }

    public String showMarketplace() {
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
            ps = conn.prepareStatement("SELECT tradeid, cards.cardid, name,damage, owner, mindamage, type FROM marketplace JOIN cards ON cards.cardID = marketplace.cardID;");

            // Execute the query
            rs = ps.executeQuery();

            // Fetch the results and add them to the array node
            while (rs.next()) {
                ObjectNode deal = mapper.createObjectNode();
                deal.put("TradeID", rs.getString(1));
                deal.put("CardID", rs.getString(2));
                deal.put("Name", rs.getString(3));
                deal.put("Damage", rs.getString(4));
                deal.put("Owner", rs.getString(5));
                deal.put("MinimumDamage", rs.getString(6));
                deal.put("Type", rs.getString(7));
                arrayNode.add(deal);
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

    public boolean removeTrade(User user, String id) {
        // Declare variables
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get connection instance
            conn = DB.getInstance().getConnection();

            // Prepare the statement to check if the user owns the card
            ps = conn.prepareStatement("SELECT cards.owner FROM cards JOIN marketplace ON cards.cardID = marketplace.cardID WHERE marketplace.tradeID = ?;");
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
            ps = conn.prepareStatement("DELETE FROM marketplace WHERE tradeID = ?;");
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

    public boolean card2market(User user, String tradeID, String cardID, float minimumDamage, String type) {
        // Declare variables
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get connection instance
            conn = DB.getInstance().getConnection();

            // Check if the marketplace already contains the card
            if (marketplaceContains(cardID)) {
                return false;
            }

            // Prepare the statement to check if the user owns the card
            ps = conn.prepareStatement("SELECT COUNT(cardid) FROM cards WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
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
            ps = conn.prepareStatement("INSERT INTO marketplace(tradeid, cardid, mindamage, type) VALUES(?,?,?,?);");
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

    // Method to trade cards between users based on the tradeID and cardID
    public boolean tradeCards(User user, String tradeID, String cardID) {
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
            if (marketplaceContains(cardID)) {
                return false;
            }

            // Declare variables to store card data
            String cardName;
            float cardDamage;

            // Prepare a query to get the card details based on the user and cardID
            ps = conn.prepareStatement("SELECT name, damage FROM cards WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
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
            ps = conn.prepareStatement("SELECT marketplace.cardID, owner, minDamage, type FROM marketplace JOIN cards ON marketplace.cardID = cards.cardID WHERE tradeID = ?;");
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

            // Get an instance of the Handler_Card class
            Handler_Card manager = Handler_Card.getInstance();

            // Validate the trade conditions based on the card types and damage values
            if (type.equalsIgnoreCase("monster")) {
                if (manager.determineMonsterCategory(cardName) == MonsterCategory.Spell) {
                    return false;
                }
            } else if (manager.determineMonsterCategory(cardName) != manager.determineMonsterCategory(type)) {
                return false;
            }

            // Check if the cardDamage is less than the minimum required damage for the trade; if yes, return false
            if (cardDamage < minDamage) {
                return false;
            }

            // Check if the offered card owner is the same as the user attempting the trade; if yes, return false
            if (offeredCardOwner.equalsIgnoreCase(user.getUsername())) {
                return false;
            }

            // Update the owner of the user's card to the offered card owner
            ps = conn.prepareStatement("UPDATE cards SET owner = ? WHERE cardID = ?");
            ps.setString(1, offeredCardOwner);
            ps.setString(2, cardID);
            ps.executeUpdate();
            ps.close();

            // Update the owner of the offered card to the user
            ps = conn.prepareStatement("UPDATE cards SET owner = ? WHERE cardID = ?");
            ps.setString(1, user.getUsername());
            ps.setString(2, offeredCardID);
            ps.executeUpdate();
            ps.close();

            // Remove the trade from the marketplace
            ps = conn.prepareStatement("DELETE FROM marketplace WHERE tradeID = ?;");
            ps.setString(1, tradeID);
            ps.executeUpdate();
            ps.close();

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



        public boolean marketplaceContains(String cardID) {
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

}
