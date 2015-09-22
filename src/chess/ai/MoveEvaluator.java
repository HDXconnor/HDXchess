package chess.ai;

import chess.core.*;

import java.util.ArrayList;
import java.util.EnumMap;


public class MoveEvaluator implements BoardEval {
	final static int MAX_VALUE = 1000;
	private EnumMap<ChessPiece,Integer> values = new EnumMap<ChessPiece,Integer>(ChessPiece.class);
    private boolean openingOver = false;

    private ArrayList<Chessboard> openingMoves1 = new ArrayList<Chessboard>();
    private ArrayList<Chessboard> openingMoves2 = new ArrayList<Chessboard>();

    private int openingValue = -5;
	
	public MoveEvaluator() {
        //basic values
		values.put(ChessPiece.BISHOP, 3);
		values.put(ChessPiece.KNIGHT, 3);
		values.put(ChessPiece.PAWN, 1);
		values.put(ChessPiece.QUEEN, 9);
		values.put(ChessPiece.ROOK, 5);
		values.put(ChessPiece.KING, MAX_VALUE);
	}

    @Override
    public int eval(Chessboard board) {
        int total = 0;
        //if (isSolvedState(board)) {return MAX_VALUE;}
        for (BoardSquare s: board.allPieces()) {
            ChessPiece type = board.at(s);
            if (values.containsKey(type)) {
                if (board.colorAt(s).equals(board.getMoverColor())) {
                    total += values.get(type);
                    total += checkOpenings(board);
                    switch (type.symbol()) {
                        case 'P':
                            type.name().equals("PAWN");
                            total += pawnChecks(board, s);
                            break;
                        case 'R':
                            type.name().equals("ROOK");
                            total += rookChecks(board, s);
                            break;
                        case 'B':
                            type.name().equals("BISHOP");
                            total += bishopChecks(board, s);
                            break;
                        case 'N':
                            type.name().equals("KNIGHT");
                            total += knightChecks(board, s);
                            break;
                        case 'Q':
                            type.name().equals("QUEEN");
                            total += queenChecks(board, s);
                            break;
                        case 'K':
                            type.name().equals("KING");
                            total += kingChecks(board, s);
                    }
                } else {
                    total -= values.get(type);
                }
            }
        }
        //System.out.println(total);
        return total;
    }

    private boolean isWhite(Chessboard board){
        return board.getMoverColor().equals("WHITE");
    }

    private int checkOpenings(Chessboard board) {
        if (openingOver) {return 0;}
        if (inPosition(board, openingMoves1) || inPosition(board, openingMoves2)) {
            return openingValue;
        }
        openingOver = true;
        return 0;
    }

    private int pawnChecks(Chessboard board, BoardSquare sq){
        int tot = 0;
        if (pawnIsDoubled(board, sq)) {
            tot += 2;
        }
        return tot;
    }
    private int rookChecks(Chessboard board, BoardSquare sq) {
        int tot = 0;
        if (rookInOpenFile(board, sq)) {
            tot += 2;
        }
        return tot;
    }
    private int bishopChecks(Chessboard board, BoardSquare sq){
        int tot = 0;
        return tot;
    }
    private int knightChecks(Chessboard board, BoardSquare sq){
        int tot = 0;
        return tot;
    }
    private int queenChecks(Chessboard board, BoardSquare sq){
        int tot = 0;
        return tot;
    }
    private int kingChecks(Chessboard board, BoardSquare sq){
        int tot = 0;
        return tot;
    }

    @Override
	public int maxValue() {
		return MAX_VALUE;
	}
	
	public boolean hasValue(ChessPiece piece) {
		return values.containsKey(piece);
	}
	
	public int valueOf(ChessPiece piece) {
		return values.get(piece);
	}

    private boolean inPosition(Chessboard board, ArrayList<Chessboard> positions) {
        PieceColor color = board.getMoverColor().other();
        BitBoard boardPieces = board.allPiecesFor(color);

        for (Chessboard compPos: positions) {
            BitBoard compPieces = compPos.allPiecesFor(color);
            if (boardPieces.equals(compPieces)) {return true;}
        }
        return false;
    }
    /*private boolean isSolvedState(Chessboard board) {
        //if pieces < numMinPieces
        board.toFEN();
        return false; //TODO
    }*/

    private boolean rookInOpenFile(Chessboard board, BoardSquare sq) {
        if (getColumnPieces(board, sq).size() == 1) {return true;}
        return false;
    }

    private boolean pawnIsDoubled(Chessboard board, BoardSquare sq) {
        int row = sq.getRow();
        ArrayList<BoardSquare> locs = getColumnLocations(board, sq);
        for (BoardSquare s: locs) {
            if (board.at(s).equals(ChessPiece.PAWN) && rightColor(board, s, sq)) {
                if ((s.getRow() + 1 == row) || (s.getRow() - 1 == row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean rightColor(Chessboard board, BoardSquare s1, BoardSquare s2) {
        return board.colorAt(s1).equals(board.colorAt(s2));
    }
    private ArrayList<ChessPiece> getColumnPieces(Chessboard board, BoardSquare sq) {
        ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();
        String columnName = sq.toString().substring(0,1);
        for (BoardSquare s: board.allPieces()) {
            if (s.toString().contains(columnName)) {
                pieces.add(board.at(s));
            }
        }
        return pieces;
    }

    private ArrayList<BoardSquare> getColumnLocations(Chessboard board, BoardSquare sq) {
        ArrayList<BoardSquare> locs = new ArrayList<BoardSquare>();
        String columnName = sq.toString().substring(0,1);
        for (BoardSquare s: board.allPieces()) {
            if (s.toString().contains(columnName)) {
                locs.add(s);
            }
        }
        return locs;
    }
}