package chess.database;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class DBbuilder {

        public static void main(String[] args) {
            Connection conn = openConnection();
            try {
                //createTable(conn);
                //populateTable(conn);

                PreparedStatement query = conn.prepareStatement(
                        "SELECT firstName, lastName, studentID FROM Students WHERE firstName LIKE ?");
                Scanner in = new Scanner(System.in);
                while(true) {
                    System.out.print("student name? ");
                    query.setString(1, in.nextLine()); // Note JDBC counts from 1!
                    processQuery(conn, query);
                }
            } catch(SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(conn != null) conn.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
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
                // Note that the URL below specifies the filename for storing the DB
                return DriverManager.getConnection("jdbc:sqlite:choir.db");
            } catch (SQLException e) {
                System.err.println("Could not connect to DBMS.");
                System.exit(-1);
            }
            return null; // we'll never get here, but the compiler insists
        }

        private static void createTable(Connection conn) throws SQLException {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE Games(gameNum INTEGER, result TEXT, moves INTEGER)");
            stmt.executeUpdate("CREATE TABLE GameFens(gameNum INTEGER, boardFen TEXT)");
           System.out.println("Created tables!");
        }

        private static void populateTable(Connection conn) throws SQLException, IOException {
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Games (gameNum, result, moves) VALUES (?, ?, ?)");
            PGNparser parser = new PGNparser();
            parser.parsePGN("ficsDB.pgn");

//            for (String child : myStudents) {
//                String[] tokens = child.split(" ");
//                insert.setString(1, tokens[0]); // note JDBC counts from 1!
//                insert.setString(2, tokens[1]);
//                insert.setInt(3, Integer.parseInt(tokens[2]));
//                int updated = insert.executeUpdate();
//                if (updated == 0) {
//                    System.err.println("Failed to insert " + tokens[0]);
//                }
//            }
        }

        private static void processQuery(Connection conn, PreparedStatement query)
                throws SQLException {
            ResultSet rows = query.executeQuery();
            boolean found = false;
            while (rows.next()) {
                found = true;
                String firstName = rows.getString(1); // Again, it counts from 1!
                String lastName = rows.getString(2);
                int id = rows.getInt(3);
                System.out.println(firstName + " " + lastName + " " + id);
            }
            if (!found) {
                System.out.println("No such child found.");
            }
            rows.close();
        }
    }