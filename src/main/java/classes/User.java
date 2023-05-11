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
        // Initialize a new HashMap to store user information
        Map<String,String> userInfo = new HashMap<>();

        // Put user's name, bio, image, and coins into the map
        userInfo.put("Name:", this.name);
        userInfo.put("Bio:", this.bio);
        userInfo.put("Image:", this.image);
        userInfo.put("Coins:", String.valueOf(this.coins));

        // Use ObjectMapper to convert the map to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(userInfo);
        } catch (JsonProcessingException e) {
            // In case of error, print the stack trace
            e.printStackTrace();
        }

        // Return null if the conversion failed
        return null;
    }
    /**
     * Deducts the cost of a package from the user's coins and updates the database.
     *
     * @return true if the user has enough coins and the purchase is successful, false otherwise
     */
    public boolean purchasePackage() {
        // If the user doesn't have enough coins, we terminate the operation and return false
        if (this.coins < 5) {
            return false;
        }

        try {
            // We get a connection object from the DB singleton
            try (Connection conn = DB.getInstance().getConnection()) {
                // We calculate the new amount of coins after the purchase
                int updatedCoins = this.coins - 5;

                // Execute the SQL statement and store the result
                boolean result = executeUpdate(conn, updatedCoins);

                // If everything went smoothly, we return true
                return result;
            }

        } catch (SQLException e) {
            // If something went wrong with the database operation, we print the stack trace for debugging
            e.printStackTrace();
        }

        // If we reach this point, something went wrong, so we return false
        return false;
    }

    private boolean executeUpdate(Connection conn, int updatedCoins) throws SQLException {
        // Preparing our SQL statement
        String sqlQuery = "UPDATE users SET coins = ? WHERE username = ?;";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)) {
            // We set the new amount of coins and username in our prepared statement
            preparedStatement.setInt(1, updatedCoins);
            preparedStatement.setString(2, this.username);

            // We execute the update operation and return whether it was successful or not
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 1;
        }
    }


    public String stats() {
        // Create a new HashMap to hold the stats
        Map<String, Integer> statMap = new HashMap<>();

        // Add the number of wins and games to the map
        statMap.put("Wins:", this.wins);
        statMap.put("Games:", this.games);

        // Initialize an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Attempt to convert the map to a JSON string and return it
            return objectMapper.writeValueAsString(statMap);
        } catch (JsonProcessingException e) {
            // If an error occurs, print the stack trace
            e.printStackTrace();
        }

        // If the conversion failed, return null
        return null;
    }

    public boolean win(){
        this.incrementWins().incrementGames().changeElo(3);
        return Statstore();
    }

    public boolean lose(){
        this.incrementGames().changeElo(-5);
        return Statstore();
    }

    public boolean draw(){
        this.incrementGames();
        return Statstore();
    }

    private User incrementWins() {
        this.wins++;
        return this;
    }

    private User incrementGames() {
        this.games++;
        return this;
    }

    private User changeElo(int amount) {
        this.elo += amount;
        return this;
    }


    /**
     * Stores the updated user statistics (wins, games, and elo) in the database.
     *
     * @return true if the statistics are successfully saved, false otherwise
     */
    public boolean Statstore() {
        try {
            // Obtain a connection to the database
            try (Connection conn = DB.getInstance().getConnection()) {
                // Execute the SQL statement
                executeStatUpdate(conn);
                // Return true to indicate a successful update
                return true;
            }
        } catch (SQLException e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }

        // Return false if an exception occurs or the update fails
        return false;
    }

    private void executeStatUpdate(Connection conn) throws SQLException {
        // Prepare an SQL statement to update the user's stats
        try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET wins = ?, games = ?, elo = ? WHERE username = ?;")) {
            // Set the updated values for the prepared statement
            ps.setInt(1, wins);
            ps.setInt(2, games);
            ps.setInt(3, elo);
            ps.setString(4, username);

            // Execute the SQL statement
            ps.executeUpdate();
        }
    }


    /**
     * Updates the user's name, bio, and image in the database.
     *
     * @param name  the new name for the user
     * @param bio   the new bio for the user
     * @param image the new image for the user
     * @return true if the user information is updated successfully, false otherwise
     */
    public boolean updateInfo(String name, String bio, String image) {
        try {
            // Obtain a connection to the database
            try (Connection conn = DB.getInstance().getConnection()) {
                // Execute the SQL statement and store the number of affected rows
                int affectedRows = executeInfoUpdate(conn, name, bio, image);

                // If the update affected one row, the update was successful
                return affectedRows == 1;
            }
        } catch (SQLException e) {
            // Print the stack trace for debugging purposes
            e.printStackTrace();
        }

        // Return false if an exception occurs or the update fails
        return false;
    }

    private int executeInfoUpdate(Connection conn, String name, String bio, String image) throws SQLException {
        // Prepare an SQL statement to update the user's info
        try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?;")) {
            // Set the updated values for the prepared statement
            ps.setString(1, name);
            ps.setString(2, bio);
            ps.setString(3, image);
            ps.setString(4, username);

            // Execute the SQL statement and return the number of affected rows
            return ps.executeUpdate();
        }
    }

}
