package chess.core;

import java.util.HashMap;

public class BoardSquareAccessor {
    private static HashMap<String, BoardSquare> boardSquares;
    private static HashMap<Integer, String> columns;

    public BoardSquareAccessor() {
        setupBoardSquares();
    }

    public static BoardSquare getBoardSquare(int row, int column) {
        String col = columns.get(column);
        String sqr = col.concat(String.valueOf(row));
        return boardSquares.get(sqr);
    }

    public static BoardSquare getBoardSquare(int row, String column) {
        String sqr = column.concat(String.valueOf(row));
        System.out.println("Sqr: " + sqr);
        return boardSquares.get(sqr);
    }

    private static void setupBoardSquares() {
        boardSquares = new HashMap<String, BoardSquare>();
        columns = new HashMap<Integer, String>();
        for (BoardSquare sq : BoardSquare.class.getEnumConstants()) {
            boardSquares.put(sq.toString().toUpperCase(), sq);
        }
        columns.put(1, "A");
        columns.put(2, "B");
        columns.put(3, "C");
        columns.put(4, "D");
        columns.put(5, "E");
        columns.put(6, "F");
        columns.put(7, "G");
        columns.put(8, "H");
    }
}
