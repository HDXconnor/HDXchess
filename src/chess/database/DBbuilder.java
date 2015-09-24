package chess.database;

import chess.core.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class DBbuilder {
    private static BoardSquareAccessor acc = new BoardSquareAccessor();

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
        PreparedStatement query = conn.prepareStatement("SELECT moves FROM Games");
        ResultSet moveSet = query.executeQuery();
        while (moveSet.next()) {
            String moves = moveSet.getString(1);
            moves = moves.split("\\{")[0];
            String[] moveList = moves.split("\\. ");
            Chessboard currentBoard = new Chessboard();
            for (int idx = 1; idx < moveList.length; idx++) {
                String[] turnMoveList = moveList[idx].split(" ");
                Move m1 = getMove(currentBoard, turnMoveList[0]);
                System.out.println(m1.toString());
                currentBoard = currentBoard.successor(m1);
                //save current board in db
                Move m2 = getMove(currentBoard, turnMoveList[1]);
                System.out.println(m2.toString());
                currentBoard = currentBoard.successor(m2);
                //save board
            }
        }
    }


    private static Move getMove(Chessboard currentBoard, String move) {
        if (move.contains("+") || move.contains("#")) {move = move.substring(0, move.length()-1);}
        System.out.println("\n\n\nMove: " + move);
        PieceColor c = currentBoard.getMoverColor();
        ChessPiece piece;
        if (move.contains("B")) {piece = ChessPiece.BISHOP;}
        else if (move.contains("N")) {piece = ChessPiece.KNIGHT;}
        else if (move.contains("R")) {piece = ChessPiece.ROOK;}
        else if (move.contains("K")) {piece = ChessPiece.KING;}
        else if (move.contains("Q")) {piece = ChessPiece.QUEEN;}
        else {piece = ChessPiece.PAWN;}
        System.out.println("piece: " + piece.toString());

        if (!move.contains("O-O")) {
            BoardSquare newSqr = getSqr(move);
            BoardSquare oldSqr = newSqr;
            BoardSquare tempSquare;
            ChessPiece tempPiece;
            /*if (piece.equals(ChessPiece.PAWN) && (!move.contains("x"))) {
                if (c.equals(PieceColor.BLACK)) {
                    tempSquare = getSqr(move, 1);
                    if (tempSquare != null && currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare;
                    }
                    tempSquare = getSqr(move, 2);
                    if (tempSquare != null && currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare;
                    }
                } else {
                    tempSquare = getSqr(move, -1);
                    if (tempSquare != null && currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare;
                    }
                    tempSquare = getSqr(move, -2);
                    if (tempSquare != null && currentBoard.at(tempSquare).equals(ChessPiece.PAWN)) {
                        oldSqr = tempSquare;
                    }
                }
            }*/
            //if (oldSqr.equals(newSqr)) {
                for (Move m : currentBoard.getLegalMovesTo(newSqr)) {
                    if (m.getPiece().equals(piece)) {
                        if (move.length() == 3 || move.length() == 2 || (move.length() == 4 && move.contains("x"))) {
                            return m;
                        }
                        else if (!piece.equals(ChessPiece.QUEEN) && !piece.equals(ChessPiece.KING)) {
                            System.out.println("here");
                            String id = move.substring(1, 2);
                            String label = m.getStart().toString();
                            if (label.contains(id)) {
                                return m;
                            }
                        }
                        else {System.out.println("here2");return m;}
                    }
                }
                for (Move m1 : currentBoard.getLegalMoves()) {
                    if (m1.getPiece().equals(piece)) {
                        if (move.length() == 3 || (move.length() == 4 && move.contains("x"))) {
                            return m1;
                        }
                        else if (!piece.equals(ChessPiece.QUEEN) && !piece.equals(ChessPiece.KING)) {
                            String id = move.substring(1, 2);
                            String label = m1.getStart().toString();
                            if (label.contains(id)) {
                                return m1;
                            }
                        }
                        else {return m1;}
                    }
                }
            //}
            System.out.println(c.toString());
            System.out.println(piece.toString());
            System.out.println(oldSqr.toString());
            System.out.println(newSqr.toString());
            return new Move(c, piece, oldSqr, newSqr);
        }
        else {
            System.out.println("iscastling");
            for (Move m : currentBoard.getLegalMoves()) {
                if (m.isCastlingMove()) {
                    if (m.getStop().file() == 'g') {
                        if (move.length() == 3) {return m;}
                    }
                    else if (move.length() == 5) {return m;}
                }
            }
        }
        return null;
    }

    private static boolean isPawn(String move) {
        String id = move.substring(move.length()-2, move.length()-1);
        return id.equals(id.toLowerCase());
    }

    private static BoardSquare getSqr(String loc) {
        String row = loc.substring(loc.length()-1, loc.length());
        String col = loc.substring(loc.length()-2, loc.length()-1);
        int rowInt = Integer.valueOf(row);
        col = col.toUpperCase();
        return acc.getBoardSquare(rowInt, col);
    }

    private static BoardSquare getSqr(String loc, int offset) {
        String row = loc.substring(loc.length()-1, loc.length());
        String col = loc.substring(loc.length()-2, loc.length()-1);
        int rowInt = Integer.valueOf(row);
        col = col.toUpperCase();
        return acc.getBoardSquare(rowInt + offset, col);
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