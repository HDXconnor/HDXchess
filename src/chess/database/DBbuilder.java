package chess.database;

import chess.core.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class DBbuilder {

    public static void main(String[] args) {
        Connection conn = openConnection();
        try {
//                createTable(conn);
//                populateGamesTable(conn);
            populateFensTable(conn);


        } catch(SQLException e) {
            e.printStackTrace();
        }
//              catch (IOException e) {
//                e.printStackTrace();
        //}
        catch (IOException e) {
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

    private static void populateFensTable(Connection conn) throws SQLException, IOException {
        PreparedStatement query = conn.prepareStatement("SELECT moves FROM Games");//TODO gets all from move column
        ResultSet moveSet = query.executeQuery();
        while (moveSet.next()) {
            String moves = moveSet.getString(1);
            moves = moves.split("{")[0];
            String[] moveList = moves.split("\. ");
            Chessboard currentBoard = new Chessboard();
            for (int idx = 1; idx < moveList.length; idx++) {
                String[] turnMoveList = moveList[idx].split(" ");
                Move m = getMove(currentBoard, turnMoveList[0]);
                currentBoard = currentBoard.successor(m);
            }
            System.out.println(moveList.toString());
        }
    }

    private static Move getMove(Chessboard currentBoard, String move) {
        PieceColor c = currentBoard.getMoverColor();
        ChessPiece piece;
        if (move.matches("\\p{javaLowerCase}}+")) {
            piece = ChessPiece.PAWN;
        }
        else if (move.contains("B")) {piece = ChessPiece.BISHOP;}
        else if (move.contains("N")) {piece = ChessPiece.KNIGHT;}
        else if (move.contains("R")) {piece = ChessPiece.ROOK;}
        else if (move.contains("K")) {piece = ChessPiece.KING;}
        else {piece = ChessPiece.QUEEN;}

        BoardSquare newSqr = BoardSquare.getBoardSquare(Integer.valueOf(move.substring(move.length()-1, move.length())), move.substring(move.length()-2, move.length()-1));
        BoardSquare oldSqr;
        BoardSquare tempSquare
        for (Move m : currentBoard.getLegalMovesTo(newSqr)) {
            if (piece.equals(ChessPiece.PAWN)) {
                if (c.equals(PieceColor.BLACK)) {
                    tempSquare = BoardSquare.getBoardSquare(Integer.valueOf(move.substring(1, 0))+1, move.substring(0, 1));
                    if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare; break;
                    }
                    tempSquare = BoardSquare.getBoardSquare(Integer.valueOf(move.substring(1, 0))+2, move.substring(0, 1));
                    if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare; break;
                    }
                }
                else {
                    tempSquare = BoardSquare.getBoardSquare(Integer.valueOf(move.substring(1, 0))-1, move.substring(0, 1));
                    if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare; break;
                    }
                    tempSquare = BoardSquare.getBoardSquare(Integer.valueOf(move.substring(1, 0))-2, move.substring(0, 1));
                    if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare; break;
                    }
                }
            }
            //else if (piece.equals())
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