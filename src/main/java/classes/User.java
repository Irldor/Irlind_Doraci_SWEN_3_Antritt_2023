package classes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class User {
    private String username, name, bio, image;
    private int coins, games, wins, elo;

    public String getUsername(){
        return username;
    }

    public String getName(){
        return name;
    }

    public String info(){
        try {
            Map<String,String> map = new HashMap<>();

            map.put("Name:",name);
            map.put("Bio:",bio);
            map.put("Image:",image);
            map.put("Coins:",String.valueOf(coins));

            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String stats(){
        try {
            Map<String,Integer> map = new HashMap<>();

            map.put("Wins:",wins);
            map.put("Games:",games);

            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deducts the cost of a package from the user's coins and updates the database.
     *
     * @return true if the user has enough coins and the purchase is successful, false otherwise
     */
    public boolean buyPackage() {
        try {
            // Check if the user has enough coins to buy the package
            if (coins < 5) {
                return false;
            }

            // Obtain a connection to the database
            Connection conn = DB.getInstance().getConnection();

            // Prepare an SQL statement to update the user's coins after the purchase
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET coins = ? WHERE username = ?;");

            // Set the updated coins value for the prepared statement
            ps.setInt(1, coins - 5);
            ps.setString(2, username);

            // Execute the SQL statement
            ps.executeUpdate();

            // Close the statement and connection to release resources
            ps.close();
            conn.close();

            // Return true to indicate a successful purchase
            return true;
        } catch (SQLException e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }

        // Return false if an exception occurs or the purchase fails
        return false;
    }

    public boolean win(){
        wins++;
        games++;
        elo+=3;
        return saveStats();
    }

    public boolean lose(){
        games++;
        elo-=5;
        return saveStats();
    }

    public boolean draw(){
        games++;
        return saveStats();
    }

    /**
     * Stores the updated user statistics (wins, games, and elo) in the database.
     *
     * @return true if the statistics are successfully saved, false otherwise
     */
    public boolean saveStats() {
        try {
            // Obtain a connection to the database
            Connection conn = DB.getInstance().getConnection();

            // Prepare an SQL statement to update the user's stats
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET wins = ?, games = ?, elo = ? WHERE username = ?;");

            // Set the updated values for the prepared statement
            ps.setInt(1, wins);
            ps.setInt(2, games);
            ps.setInt(3, elo);
            ps.setString(4, username);

            // Execute the SQL statement
            ps.executeUpdate();

            // Close the statement and connection to release resources
            ps.close();
            conn.close();

            // Return true to indicate a successful update
            return true;
        } catch (SQLException e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }

        // Return false if an exception occurs or the update fails
        return false;
    }

    /**
     * Updates the user's name, bio, and image in the database.
     *
     * @param name  the new name for the user
     * @param bio   the new bio for the user
     * @param image the new image for the user
     * @return true if the user information is updated successfully, false otherwise
     */
    public boolean setUserInfo(String name, String bio, String image) {
        try {
            // Obtain a connection to the database
            Connection conn = DB.getInstance().getConnection();

            // Prepare an SQL statement to update the user's info
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?;");

            // Set the updated values for the prepared statement
            ps.setString(1, name);
            ps.setString(2, bio);
            ps.setString(3, image);
            ps.setString(4, username);

            // Execute the SQL statement and store the number of affected rows
            int affectedRows = ps.executeUpdate();

            // Close the statement and connection to release resources
            ps.close();
            conn.close();

            // If the update affected one row, the update was successful
            if (affectedRows == 1) {
                return true;
            }
        } catch (SQLException e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }

        // Return false if an exception occurs or the update fails
        return false;
    }
}
