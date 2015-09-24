package chess.database;

import chess.core.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class DBbuilder {

    public static void main(String[] args) {
        DBbuilder db = new DBbuilder();
        Connection conn = openConnection();
        try {
//                createTable(conn);
//                populateGamesTable(conn);
            db.populateFensTable(conn);


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
            moves = moves.split("\\{")[0];
            String[] moveList = moves.split("\\. ");
            Chessboard currentBoard = new Chessboard();
            for (int idx = 1; idx < moveList.length; idx++) {
                String[] turnMoveList = moveList[idx].split(" ");
                Move m1 = getMove(currentBoard, turnMoveList[0]);
                currentBoard = currentBoard.successor(m1);
                //save current board in db
                Move m2 = getMove(currentBoard, turnMoveList[1]);
                currentBoard = currentBoard.successor(m2);
            }
            System.out.println(moveList.toString());
        }
    }

    private static Move getMove(Chessboard currentBoard, String move) {
        if (move.contains("+") || move.contains("#")) {move = move.substring(0, move.length()-1);}
        PieceColor c = currentBoard.getMoverColor();
        ChessPiece piece;
        if (isPawn(move)) {
            piece = ChessPiece.PAWN;
        }
        else if (move.contains("B")) {piece = ChessPiece.BISHOP;}
        else if (move.contains("N")) {piece = ChessPiece.KNIGHT;}
        else if (move.contains("R")) {piece = ChessPiece.ROOK;}
        else if (move.contains("K")) {piece = ChessPiece.KING;}
        else {piece = ChessPiece.QUEEN;}

        BoardSquare newSqr = getSqr(move);
        BoardSquare oldSqr = newSqr;
        BoardSquare tempSquare;
        ChessPiece tempPiece;
        if (piece.equals(ChessPiece.PAWN)) {
            if (c.equals(PieceColor.WHITE)) {
                System.out.println("yo");
                tempSquare = getSqr(move, 1);
                System.out.println(tempSquare);
                //tempPiece = currentBoard.at(tempSquare);
                if (currentBoard.at(tempSquare) != null) {
                    if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare;
                    }
                }
                tempSquare =getSqr(move, 2);
                if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                    oldSqr = tempSquare;
                }
            }
            else {
                tempSquare =getSqr(move, -1);
                if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                    oldSqr = tempSquare;
                }
                tempSquare =getSqr(move, -2);
                if (currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                    System.out.println("here");
                    oldSqr = tempSquare;
                }
            }
        }
        if (oldSqr != newSqr) {
            for (Move m : currentBoard.getLegalMovesTo(newSqr)) {
                if (move.length() == 3  || (move.length() == 4 && move.contains("x"))) {
                    if (m.getPiece().equals(piece)) {
                        oldSqr = m.getStart();
                    }
                } else {
                    String id = move.substring(1,2);
                    String label = m.getStart().toString();
                    if (label.contains(id)) {
                        oldSqr = m.getStart();
                    }
                }
            }
        }
        System.out.println(c.toString());
        System.out.println(piece.toString());
        System.out.println(oldSqr.toString());
        System.out.println(newSqr.toString());
        return new Move(c, piece, oldSqr, newSqr);
    }

    private static boolean isPawn(String move) {
        String id = move.substring(move.length()-2, move.length()-1);
        return id.equals(id.toLowerCase());
    }

    private static BoardSquare getSqr(String loc) {
        System.out.println(loc);
        String row = loc.substring(loc.length()-1, loc.length());
        String col = loc.substring(loc.length()-2, loc.length()-1);
        int rowInt = Integer.valueOf(row);
        col = col.toUpperCase();
        return BoardSquare.getBoardSquare(rowInt, col);
    }

    private static BoardSquare getSqr(String loc, int offset) {
        System.out.println(loc + offset);
        String row = loc.substring(loc.length()-1, loc.length());
        String col = loc.substring(loc.length()-2, loc.length()-1);
        int rowInt = Integer.valueOf(row);
        col = col.toUpperCase();
        return BoardSquare.getBoardSquare(rowInt + offset, col);
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