import classes.DB;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class Test_DB {

    /**
     * This test case checks if the DB singleton instance is created correctly and if getConnection() returns a valid connection.
     */
    @Test
    public void testDBSingletonAndConnection() {
        // Get the singleton instance of the DB class
        DB dbInstance = DB.getInstance();
        assertNotNull(dbInstance, "DB instance should not be null");

        // Get the connection from the DB instance
        Connection connection = dbInstance.getConnection();
        assertNotNull(connection, "Connection should not be null");

        // Verify if the connection is valid
        try {
            assertTrue(connection.isValid(5), "Connection should be valid");
        } catch (SQLException e) {
            fail("Unexpected SQLException during isValid() check");
        }
    }
}
