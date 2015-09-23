package chess.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBAccessor {

    private Connection conn;

    public DBAccessor() {
        conn = openConnection();
    }

    public boolean checkMatch(String fen) {
        try {
        PreparedStatement query = conn.prepareStatement(
                        "SELECT * FROM GameFens WHERE GameFens MATCH 'B:" + fen + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static Connection openConnection() {
        try {
            Class.forName("org.sqlite.JDBC"); // This loads the driver
        } catch (ClassNotFoundException e1) {
            System.err.println("SqliteJDBC library not found. "
                    + "Perhaps you need to set the build path?");
            System.exit(-1);
        }

        try {
            //URL below specifies filename for storing the DB
            return DriverManager.getConnection("jdbc:sqlite:chessGames.db");
        } catch (SQLException e) {
            System.err.println("Could not connect to DBMS.");
            System.exit(-1);
        }
        return null; // we'll never get here, but the compiler insists
    }
}
