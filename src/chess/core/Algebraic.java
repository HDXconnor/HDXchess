package chess.core;

import java.util.ArrayList;
import java.util.List;

public class Algebraic {
	
	public static String encode(Move m, Chessboard board) {
		String result = m.getStop().toString();
		if (m.getPiece().equals(ChessPiece.PAWN)) {
			if (m.captures()) {
				result = m.getStart().file() + "x" + result;
			} 
			if (m.promotes()) {
				result += "=" + m.promotesTo().symbol();
			}
		} else if (m.isCastlingMove()) {
			if (m.getStop().file() == 'g') {
				result = "O-O";
			} else {
				result = "O-O-O";
			}
		} else {
			if (m.captures()) {
				result = "x" + result;
			}
			ArrayList<Move> candidates = new ArrayList<Move>();
			for (Move m2: board.getLegalMovesTo(m.getStop())) {
				if (m2.getPiece().equals(m.getPiece())) {
					candidates.add(m2);
				}
			}
			if (candidates.size() == 0) {
				throw new IllegalArgumentException("Impossible move " + m);
			} else if (candidates.size() == 2) {
				candidates.remove(m);
				Move other = candidates.get(0);
				if (m.getStart().file() == other.getStart().file()) {
					result = m.getStart().rankNum() + result;
				} else {
					result = m.getStart().file() + result;
				}
			} else if (candidates.size() > 2) {
				result = m.getStart() + result;
			}
			result = m.getPiece().symbol() + result;
		}
		
		Chessboard board2 = board.successor(m);
		if (board2.isCheckmate()) {
			result += "#";
		} else if (board2.moverInCheck()) {
			result += "+";
		}
		
		return result;
	}
	
	public static ArrayList<String> encodeAllMoves(Chessboard board) {
		List<Move> moves = board.getLegalMoves();
		ArrayList<String> result = new ArrayList<String>(moves.size());
		for (Move m: moves) {
			result.add(encode(m, board));
		}
		return result;
	}
	
	public static boolean isLegal(String move, Chessboard board) {
		try {
			decode(move, board);
			return true;
		} catch (IllegalMoveException iae) {
			return false;
		}
	}
	
	public static Move decode(String move, Chessboard board) throws IllegalMoveException {
		for (Move m: board.getLegalMoves()) {
			String encoded = encode(m, board);
			if (move.equals(encoded)) {
				return m;
			} else if ((encoded.endsWith("#") || encoded.endsWith("+")) && 
					move.equals(encoded.substring(0, encoded.length() - 1))) {
				return m;
			}
		}
		throw new IllegalMoveException(move + " is not a legal move");
	}
	
	public static Move decode(BoardSquare start, BoardSquare stop, Chessboard board) throws IllegalMoveException {
		for (Move m: board.getLegalMovesTo(stop)) {
			if (m.getStart().equals(start)) {
				return m;
			}
		}
		throw new IllegalMoveException(start + " to " + stop + " is not a legal move");
	}
	
	public static boolean moveExistsStartingAt(BoardSquare start, Chessboard board) {
		for (Move m: board.getLegalMoves()) {
			if (m.getStart().equals(start)) {
				return true;
			}
		}
		return false;
	}
	
	public static Chessboard from(String... moves) throws IllegalMoveException {
		Chessboard board = new Chessboard();
		for (String move: moves) {
			board = successor(board, move);
		}
		return board;
	}
	
	public static Chessboard successor(Chessboard board, String algebraicMove) throws IllegalMoveException {
		return board.successor(decode(algebraicMove, board));
	}
}
