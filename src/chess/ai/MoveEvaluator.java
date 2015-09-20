package chess.ai;

import chess.core.*;

import java.util.ArrayList;
import java.util.EnumMap;


public class MoveEvaluator implements BoardEval {
    private int switch_piece_count = 6;
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
        for (BoardSquare s: board.allPieces()) {
            ChessPiece type = board.at(s);
            if (values.containsKey(type)) {
                if (board.colorAt(s).equals(board.getMoverColor())) {
                    total += values.get(type);
                    total += checkOpenings(board);
                    switch (switch_piece_count) {
                        case 1:
                            type.name().equals("PAWN");
                            pawnChecks();
                        case 2:
                            type.name().equals("ROOK");
                            rookChecks();
                        case 3:
                            type.name().equals("BISHOP");
                            bishopChecks();
                        case 4:
                            type.name().equals("KNIGHT");
                            knightChecks();
                        case 5:
                            type.name().equals("QUEEN");
                            queenChecks();
                        case 6:
                            type.name().equals("KING");
                            kingChecks();
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

    private int pawnChecks(){
        System.out.println("PAWN CHECK");
        return 0;
    }
    private int rookChecks(){
        return 0;
    }
    private int bishopChecks(){
        return 0;
    }
    private int knightChecks(){
        return 0;
    }
    private int queenChecks(){
        return 0;
    }
    private int kingChecks(){
        return 0;
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
}
                    /*if (type.name().equals("PAWN") && (s.toString().contains("5"))) {
                        total += -100;
                    }
                    else if (type.name().equals("KNIGHT") && (s.toString().startsWith("a") || s.toString().startsWith("h"))) {
                        total += 2;
                    }*/