package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Handler_User {

    // Singleton instance of the class
    private static Handler_User single_instance = null;


    // Method to get the singleton instance
    public static Handler_User getInstance() {
        // First check to avoid entering the synchronized block unnecessarily
        if (single_instance == null) {
            // Synchronizing on Handler_User.class to make sure only one thread can instantiate the object
            synchronized (Handler_User.class) {
                // Second check in case another thread entered the synchronized block before this one
                if (single_instance == null) {
                    // If the instance is still null, instantiate it
                    single_instance = new Handler_User();
                }
            }
        }
        // Return the instance
        return single_instance;
    }


    public User authorize(String token) {
        // Initialize the user object as null
        User user = null;

        try (Connection conn = DB.getInstance().getConnection()) {
            // Prepare the SQL query to get user data based on the provided token
            String query = "SELECT username, name, bio, image, coins, games, wins, elo " +
                    "FROM users WHERE token = ? AND islogged = TRUE;";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                // Set the token parameter for the prepared statement
                ps.setString(1, token);

                // Execute the query and get the results
                ResultSet rs = ps.executeQuery();

                // If a result is found, create a User object and populate it with the retrieved data
                if (rs.next()) {
                    user = createUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return the user object if found, otherwise return null
        return user;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        // Extract data from the ResultSet and create a User object
        String username = rs.getString(1);
        String name = rs.getString(2);
        String bio = rs.getString(3);
        String image = rs.getString(4);
        int coins = rs.getInt(5);
        int games = rs.getInt(6);
        int wins = rs.getInt(7);
        int elo = rs.getInt(8);

        // Return the populated User object
        return new User(username, name, bio, image, coins, games, wins, elo);
    }

// this method tells us if the user is administrator
    public boolean roleVerification(String token) {
        // Initialize the admin flag as false
        boolean admin = false;

        try (Connection conn = DB.getInstance().getConnection()) {
            // Prepare the SQL query to check if the user is an admin based on the provided token
            String query = "SELECT COUNT(username) FROM users " +
                    "WHERE token = ? AND admin = TRUE AND islogged = TRUE;";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                // Set the token parameter for the prepared statement
                ps.setString(1, token);

                // Execute the query and get the results
                ResultSet rs = ps.executeQuery();

                // If a result is found and the count is 1, set the admin flag as true
                if (rs.next() && rs.getInt(1) == 1) {
                    admin = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return the admin flag
        return admin;
    }

    public boolean signUpUser(String username, String password) {
        System.out.println("User Sign Up started");
        String token = "Basic " + username + "-mtcgToken";

        try (Connection conn = DB.getInstance().getConnection()) {
            // Check if the user already exists
            if (userExists(conn, username)) {
                System.out.println("User is already registered!");
                return false;
            }

            // If the username is "admin", set the admin flag to true
            boolean admin = username.equals("admin");

            // Register the user
            return insertUser(conn, username, password, token, admin);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Helper method to check if a user exists
    private boolean userExists(Connection conn, String username) throws SQLException {
        String query = "SELECT COUNT(username) FROM users WHERE username = ?;";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            return rs.next() && rs.getInt(1) > 0;
        }
    }

    //Helper method to insert a new user into the database
    private boolean insertUser(Connection conn, String username, String password, String token, boolean admin) throws SQLException {
        String query = admin ? "INSERT INTO users(username, password, token, admin) VALUES(?,?,?,TRUE);"
                : "INSERT INTO users(username, password, token) VALUES(?,?,?);";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, token);
            int affectedRows = ps.executeUpdate();

            return affectedRows != 0;
        }
    }

    //Method to log in a user with a username and password
    public boolean UserSignIn(String username, String password) {
        String query = "UPDATE users SET islogged = TRUE WHERE username = ? AND password = ?;";

        // SQL query that sets 'islogged' to TRUE for the specified user
        try (Connection conn = DB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            int affectedRows = ps.executeUpdate();

            // If one row was affected, the login was successful
            if (affectedRows == 1) {
                System.out.println("User logged in " + username);
                return true;
            }
        } catch (SQLException e) {
            // If an SQL exception occurred, print the stack trace and return false
            e.printStackTrace();
            return false;
        }

        return false;
    }

    //Method to log out a user with a username and password
    public boolean SignOutUser(String username, String password) {
        String query = "UPDATE users SET islogged = FALSE WHERE username = ? AND password = ?;";

        try (Connection conn = DB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }



}
