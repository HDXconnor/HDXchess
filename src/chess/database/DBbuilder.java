package chess.database;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DBbuilder {

        public static void main(String[] args) {
            Connection conn = openConnection();
            try {
//                createTable(conn);
//                populateGamesTable(conn);

                PreparedStatement query = conn.prepareStatement(
                        "SELECT * FROM Games");
                processQuery(conn, query);

            } catch(SQLException e) {
                e.printStackTrace();
            }
//              catch (IOException e) {
//                e.printStackTrace();
            //}
            finally {
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
                //URL below specifies filename for storing the DB
                return DriverManager.getConnection("jdbc:sqlite:chessGames.db");
            } catch (SQLException e) {
                System.err.println("Could not connect to DBMS.");
                System.exit(-1);
            }
            return null; // we'll never get here, but the compiler insists
        }

        private static void createTable(Connection conn) throws SQLException {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE Games(gameNum INTEGER, result TEXT, moves TEXT)");
            stmt.executeUpdate("CREATE TABLE GameFens(gameNum INTEGER, boardFen TEXT)");
            //stmt.executeUpdate("CREATE TABLE GameInfo(gameNum INTEGER, whiteRat INTEGER, blackRat INTEGER)");
            System.out.println("Tables Created!");
        }

        private static void populateGamesTable(Connection conn) throws SQLException, IOException {
                FileInputStream fstream = null;
                Scanner scanner = null;
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Games (gameNum, result, moves) VALUES (?, ?, ?)");
                try {
                    fstream = new FileInputStream("ficsDB.pgn");
                    scanner = new Scanner(fstream, "UTF-8");
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();

                        // get Game Numbers
                        if(line.startsWith("[FICSGames")){
                            //System.out.println("gameNumber: " + line.split("\"")[1]);
                            insert.setInt(1, Integer.parseInt(line.split("\"")[1]));
                        }
                        //get results of games
                        else if(line.startsWith("[Result")) {
                            //System.out.println(line);
                            insert.setString(2, line.split("\"")[1]);
                        }
                        // get moves from games
                        else if(line.startsWith("1. ")){
                            //System.out.println("MOVES: " + line.split("\\{")[0]);
                            insert.setString(3, line);
                        }
                    }            int updated = insert.executeUpdate();
                    if (updated == 0){
                        System.err.println("Failed to insert something");
                    }
                    System.out.println("Population Successful");

                    if (scanner.ioException() != null) {
                        throw scanner.ioException();
                    }
                } finally {
                    if (fstream != null) {
                        fstream.close();
                    }
                    if (scanner != null) {
                        scanner.close();
                    }

                }

        }

        private static void processQuery(Connection conn, PreparedStatement query)
                throws SQLException {
            ResultSet rows = query.executeQuery();
            boolean found = false;

            while (rows.next()) {
                found = true;
                String firstName = rows.getString(1); // Again, it counts from 1!
                String lastName = rows.getString(2);
                String id = rows.getString(3);
                System.out.println(firstName + " " + lastName + " " + id);
            }
            if (!found) {
                System.out.println("No such child found.");
            }
            rows.close();
        }
    }