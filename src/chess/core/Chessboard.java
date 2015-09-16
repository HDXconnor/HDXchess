package chess.core;

import java.util.*;

public class Chessboard {
	// Essential representation
	private EnumMap<PieceColor,ChessSide> sides;
	private PieceColor turn;
	
	// Move generation
	private Move lastMove = null;
	private int numMoves = 0;
	private Chessboard parent;
	
	public Chessboard() {
		turn = PieceColor.WHITE;
		
		this.sides = new EnumMap<PieceColor,ChessSide>(PieceColor.class);
		sides.put(PieceColor.WHITE, ChessSide.makeWhiteStart());
		sides.put(PieceColor.BLACK, ChessSide.makeBlackStart());
		
		parent = null;
	}
	
	public boolean isStartingBoard() {
		return numMoves == 0;
	}
	
	public Chessboard successor(Move m) {
		Chessboard succ = new Chessboard(this);
		succ.move(m);
		succ.lastMove = m;
		succ.numMoves = this.numMoves + 1;
		return succ;		
	}
	
	public boolean hasLastMove() {
		return getNumMoves() > 0;
	}
	
	public Move getLastMove() {
		return lastMove;
	}
	
	public int getNumMoves() {
		return numMoves;
	}
	
	public PieceColor getMoverColor() {return turn;}
	public PieceColor getOpponentColor() {return turn.other();}
	
	public boolean onePerSquare() {
		return getMover().getAllPieces().intersection(getNonMover().getAllPieces()).equals(new BitBoard());
	}
	
	public BitBoard allPiecesFor(PieceColor color) {
		return new BitBoard(sides.get(color).getAllPieces());
	}
	
	public BitBoard allPieces()	{
		return allPiecesFor(PieceColor.WHITE).union(allPiecesFor(PieceColor.BLACK));
	}
	
	public boolean hasKing(PieceColor color) {
		return sides.get(color).hasKing();
	}
	
	public BoardSquare kingAt(PieceColor color) {
		return sides.get(color).getKingLocation();
	}
	
	public boolean isOccupied(BoardSquare s) {
		return getNonMover().isOccupied(s) || getMover().isOccupied(s);
	}
	
	public boolean isValidMoverTarget(BoardSquare s) {
		return at(s) != ChessPiece.EMPTY && colorAt(s).equals(this.getMoverColor().other());
	}
	
	public boolean potentialCastleKingside() {
		return getMover().canCastleKingside();
	}
	
	public boolean potentialCastleQueenside() {
		return getMover().canCastleQueenside();
	}
	
	public List<Move> getLegalMoves() {
		return Collections.unmodifiableList(getMoveMap().makeMoveList());
	}
	
	public MoveMap getMoveMap() {
		return new MoveMap(this, turn);
	}
	
	public MoveMap getOpponentMoveMap() {
		return new MoveMap(this, turn.other());
	}
	
	public ArrayList<Move> getLegalMovesTo(BoardSquare sq) {
		ArrayList<Move> result = new ArrayList<Move>();
		for (Move m: getLegalMoves()) {
			if (m.getStop().equals(sq)) {
				result.add(m);
			}
		}
		return result;
	}
	
	public ChessPiece at(BoardSquare s) {
		ChessPiece p = getWhite().at(s);
		if (p == ChessPiece.EMPTY) {
			p = getBlack().at(s);
		}
		return p;
	}
	
	public PieceColor colorAt(BoardSquare s) {
		ChessPiece p = getWhite().at(s);
		if (p == ChessPiece.EMPTY) {
			if (getBlack().at(s) == ChessPiece.EMPTY) {throw new IllegalStateException("No color for empty piece at " + s);}
			return PieceColor.BLACK;
		} else {
			return PieceColor.WHITE;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(turn);
		sb.append('\n');
		int nextRow = 8;
		for (BoardSquare s: BoardSquare.values()) {
			char sym = at(s).symbol();
			if (at(s) != ChessPiece.EMPTY && colorAt(s) == PieceColor.BLACK) {
				sym = Character.toLowerCase(sym);
			}
			sb.append(sym);
			nextRow -= 1;
			if (nextRow == 0) {
				sb.append('\n');
				nextRow = 8;
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Chessboard) {
			Chessboard that = (Chessboard)other;
			return this.getMover().equals(that.getMover()) && this.getNonMover().equals(that.getNonMover());
		} else {
			return false;
		}
	}
	
	public boolean moverInCheck() {
		return getOpponentMoveMap().canAttack(kingAt(turn));
	}
	
	public boolean opponentInCheck() {
		return getMoveMap().canAttack(kingAt(turn.other()));
	}
	
	public boolean isCheckmate() {
		return moverInCheck() && !gameInProgress();
	}
	
	public boolean isStalemate() {
		return !moverInCheck() && !gameInProgress();
	}
	
	public boolean gameInProgress() {
		return getMoveMap().getTotalPossibleMoves() > 0;
	}
	
	public Chessboard getParent() {return parent;}
	
	public BitBoard getAllOf(PieceColor color, ChessPiece type) {
		return sides.get(color).getAllOf(type);
	}
	
	public BitBoard differences(Chessboard that) {
		BitBoard blacks = sides.get(PieceColor.BLACK).differences(that.sides.get(PieceColor.BLACK));
		BitBoard whites = sides.get(PieceColor.WHITE).differences(that.sides.get(PieceColor.WHITE));
		return blacks.union(whites);
	}

	private Chessboard(Chessboard that) {
		this.turn = that.turn;
		
		this.sides = new EnumMap<PieceColor,ChessSide>(PieceColor.class);
		this.sides.put(PieceColor.WHITE, new ChessSide(that.sides.get(PieceColor.WHITE)));
		this.sides.put(PieceColor.BLACK, new ChessSide(that.sides.get(PieceColor.BLACK)));
		
		this.parent = that;
	}
	
	private ChessSide getMover() {
		return sides.get(turn);
	}
	
	private ChessSide getNonMover() {
		return sides.get(turn.other());
	}
	
	private void move(Move m) {
		if (turn != m.getColor()) {throw new IllegalArgumentException(m + " out of turn.");}
		ChessSide mover = getMover();
		ChessSide other = getNonMover();
		mover.move(m);
		if (m.captures()) {
			ChessPiece target = other.at(m.getCapture());
			other.remove(target, m.getCapture());
		} 
		if (m.promotes()) {
			mover.remove(ChessPiece.PAWN, m.getStop());
			mover.add(m.promotesTo(), m.getStop());
		}
		turn = turn.other();
	}
	
	private ChessSide getWhite() {return sides.get(PieceColor.WHITE);}
	private ChessSide getBlack() {return sides.get(PieceColor.BLACK);}
}
