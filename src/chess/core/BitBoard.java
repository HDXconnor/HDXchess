package chess.core;

//Essentially a reimplementation of EnumSet, augmented by direct access
//to the underlying representation to help with chess calculation. 

import java.util.Iterator;

import chess.core.BitBoard;
import chess.core.BoardSquare;
import chess.hash.LongHashable;


public class BitBoard implements Iterable<BoardSquare>, LongHashable {
	private long bits;

	public final static BitBoard INTERIOR_SQUARES = makeInteriorSquares();
	public final static BitBoard NO_FILE_A = makeWithoutFileA();
	public final static BitBoard NO_FILE_H = makeWithoutFileH();
	
	public BitBoard() {
		this(0);
	}
	
	private BitBoard(long bits) {
		this.bits = bits;
	}
	
	public BitBoard(BitBoard that) {
		this(that.bits);
	}
	
	public BitBoard(BoardSquare... boardSquares) {
		this();
		for (BoardSquare s: boardSquares) {
			set(s);
		}
	}
	
	public BitBoard(String rep) {
		this();
		int displayIndex = 0;
		for (int i = 0; i < rep.length(); ++i) {
			if (rep.charAt(i) != '\n') {
				if (rep.charAt(i) == '1') {
					set(BoardSquare.values()[displayIndex]);
				}
				displayIndex += 1;
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int nextRow = 8;
		for (BoardSquare bs: BoardSquare.values()) {
			sb.append(isSet(bs) ? '1' : '0');
			nextRow -= 1;
			if (nextRow == 0) {
				sb.append('\n');
				nextRow = 8;
			}
		}
		return sb.toString();
	}
	
	public int numPieces() {return Long.bitCount(bits);}
	 
	@Override
	public boolean equals(Object other) {
		if (other instanceof BitBoard) {
			BitBoard that = (BitBoard)other;
			return this.bits == that.bits;
		} else {
			return false;
		}
	}
	
	public boolean isEmpty() {return bits == 0;}
	
	public boolean isSet(BoardSquare s) {
		return (bits & s.getMask()) != 0;
	}
	
	public void set(BoardSquare bs) {
		bits |= bs.getMask();
	}
	
	public void clear(BoardSquare bs) {
		bits &= ~bs.getMask();
	}
	
	public void addAll(BitBoard that) {
		this.bits |= that.bits;
	}
	
	public void retainAll(BitBoard that) {
		this.bits &= that.bits;
	}
	
	public void removeIntersection(BitBoard that) {
		this.bits ^= that.bits;
	}
	
	public BitBoard intersection(BitBoard that) {
		BitBoard result = new BitBoard(this);
		result.retainAll(that);
		return result;
	}
	
	public BitBoard union(BitBoard that) {
		BitBoard result = new BitBoard(this);
		result.addAll(that);
		return result;
	}
	
	public BitBoard symmetricDifference(BitBoard that) {
		BitBoard result = new BitBoard(this);
		result.removeIntersection(that);
		return result;
	}
	
	public BitBoard negation() {
		BitBoard result = new BitBoard();
		result.bits = ~this.bits;
		return result;
	}
	
	@Override
	public long longHashCode() {
		return bits;
	}
	
	@Override
	public int hashCode() {
		return (int)(bits) ^ (int)(bits >>> 32);
	}
	
	@Override
	public Iterator<BoardSquare> iterator() {
		return new BBIter();
	}
	
	public BitBoard pawnAdvance(PieceColor mover) {
		BitBoard result = new BitBoard(this);
		if (mover.equals(PieceColor.WHITE)) {
			result.bits >>>= 8;
		} else {
			result.bits <<= 8;
		}
		return result;
	}
	
	public BitBoard pawnCaptureEast(PieceColor mover) {
		BitBoard result = new BitBoard(this);
		if (mover.equals(PieceColor.WHITE)) {
			result.bits >>>= 7;
		} else {
			result.bits <<= 9;
		}
		return result.intersection(NO_FILE_A);
	}
	
	public BitBoard pawnCaptureWest(PieceColor mover) {
		BitBoard result = new BitBoard(this);
		if (mover.equals(PieceColor.WHITE)) {
			result.bits >>>= 9;
		} else {
			result.bits <<= 7;
		}
		return result.intersection(NO_FILE_H);
	}
	
	// Specific useful boards
	
	public static BitBoard makeRookMoves(BoardSquare rook) {
		BitBoard result = new BitBoard();
		for (BoardSquare bs: BoardSquare.values()) {
			if (rook.hasRookMoveTo(bs)) {
				result.set(bs);
			}
		}
		return result;
	}

	public static BitBoard makeBishopMoves(BoardSquare bish) {
		BitBoard result = new BitBoard();
		for (BoardSquare bs: BoardSquare.values()) {
			if (bish.hasBishopMoveTo(bs)) {
				result.set(bs);
			}
		}
		return result;
	}

	public static BitBoard makeQueenMoves(BoardSquare queen) {
		BitBoard result = makeRookMoves(queen);
		return result.union(makeBishopMoves(queen));
	}
	
	public static BitBoard makeKingMoves(BoardSquare king) {
		BitBoard result = new BitBoard();
		for (BoardSquare bs: BoardSquare.values()) {
			if (king.hasKingMoveTo(bs)) {
				result.set(bs);
			}
		}
		return result;
	}
	
	public static BitBoard makeKnightMoves(BoardSquare kn) {
		BitBoard result = new BitBoard();
		for (BoardSquare bs: BoardSquare.values()) {
			if (kn.hasKnightMoveTo(bs)) {
				result.set(bs);
			}
		}
		return result;
	}
	
	private static BitBoard makeInteriorSquares() {
		BitBoard result = new BitBoard();
		for (BoardSquare bs: BoardSquare.values()) {
			if (!bs.onEdge()) {
				result.set(bs);
			}
		}
		return result;
	}
	
	private static BitBoard makeWithoutFileA() {
		BitBoard result = new BitBoard();
		for (BoardSquare s: BoardSquare.values()) {
			if (s.file() != 'a') {
				result.set(s);
			}
		}
		return result;
	}
	
	private static BitBoard makeWithoutFileH() {
		BitBoard result = new BitBoard();
		for (BoardSquare s: BoardSquare.values()) {
			if (s.file() != 'h') {
				result.set(s);
			}
		}
		return result;
	}
	
	public static BitBoard pawnAdvanceRankOnly(PieceColor color) {
		return color == PieceColor.WHITE ? new BitBoard(0x000000FF00000000L) : new BitBoard(0x00000000FF000000L);
	}
	
	private class BBIter implements Iterator<BoardSquare> {
		long unseen;
		long prev;
		
		BBIter() {
			unseen = bits;
			prev = 0;
		}

		@Override
		public boolean hasNext() {
			return unseen != 0;
		}

		@Override
		public BoardSquare next() {
			prev = unseen & -unseen;
			unseen -= prev;
			return BoardSquare.values()[Long.numberOfTrailingZeros(prev)];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
}
