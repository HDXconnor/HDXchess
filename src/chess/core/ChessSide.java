package chess.core;

import java.util.EnumMap;

class ChessSide {
	private EnumMap<ChessPiece,BitBoard> boards;
	private BoardSquare kingRookStart, kingRookCastle, queenRookStart, queenRookCastle;
	private boolean kingMoved, kingRookMoved, queenRookMoved;
	
	public ChessSide(ChessSide that) {
		this.boards = new EnumMap<ChessPiece,BitBoard>(ChessPiece.class);
		for (ChessPiece piece: that.boards.keySet()) {
			this.boards.put(piece, new BitBoard(that.boards.get(piece)));
		}
		this.kingMoved = that.kingMoved;
		this.kingRookMoved = that.kingRookMoved;
		this.queenRookMoved = that.queenRookMoved;
		this.kingRookStart = that.kingRookStart;
		this.kingRookCastle = that.kingRookCastle;
		this.queenRookStart = that.queenRookStart;
		this.queenRookCastle = that.queenRookCastle;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ChessSide) {
			ChessSide that = (ChessSide)other;
			if (this.kingMoved != that.kingMoved || this.kingRookMoved != that.kingRookMoved || this.queenRookMoved != that.queenRookMoved) {
				return false;
			}
			for (ChessPiece cp: boards.keySet()) {
				if (!boards.get(cp).equals(that.boards.get(cp))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public static ChessSide makeWhiteStart() {
		BitBoard pns = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n11111111\n00000000\n");
		BitBoard kts = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n01000010\n");
		BitBoard rks = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n10000001\n");
		BitBoard bps = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00100100\n");
		BitBoard qns = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00010000\n");
		BitBoard kng = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00001000\n");
		return new ChessSide(pns, kts, rks, bps, qns, kng, BoardSquare.H1, BoardSquare.A1);
	}
	
	public static ChessSide makeBlackStart() {
		BitBoard pns = new BitBoard("00000000\n11111111\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		BitBoard kts = new BitBoard("01000010\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		BitBoard rks = new BitBoard("10000001\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		BitBoard bps = new BitBoard("00100100\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		BitBoard qns = new BitBoard("00010000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		BitBoard kng = new BitBoard("00001000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		return new ChessSide(pns, kts, rks, bps, qns, kng, BoardSquare.H8, BoardSquare.A8);
	}
	
	private ChessSide(BitBoard pawns, BitBoard knights, BitBoard rooks, BitBoard bishops, BitBoard queens, BitBoard king, BoardSquare krStart, BoardSquare qrStart) {
		boards = new EnumMap<ChessPiece,BitBoard>(ChessPiece.class);
		boards.put(ChessPiece.PAWN, pawns);
		boards.put(ChessPiece.KNIGHT, knights);
		boards.put(ChessPiece.ROOK, rooks);
		boards.put(ChessPiece.BISHOP, bishops);
		boards.put(ChessPiece.QUEEN, queens);
		boards.put(ChessPiece.KING, king);
		
		kingMoved = kingRookMoved = queenRookMoved = false;
		kingRookStart = krStart;
		kingRookCastle = kingRookStart.successor(MoveDir.W).successor(MoveDir.W);
		queenRookStart = qrStart;
		queenRookCastle = queenRookStart.successor(MoveDir.E).successor(MoveDir.E).successor(MoveDir.E);
	}
	
	public ChessPiece at(BoardSquare s) {
		for (ChessPiece p: ChessPiece.values()) {
			if (p != ChessPiece.EMPTY && has(s, p)) {
				return p;
			}
		}
		return ChessPiece.EMPTY;
	}
	
	public boolean hasKing() {
		for (@SuppressWarnings("unused") BoardSquare s: boards.get(ChessPiece.KING)) {
			return true;
		}
		return false;
	}
	
	public BoardSquare getKingLocation() {
		for (BoardSquare s: boards.get(ChessPiece.KING)) {
			return s;
		}
		throw new IllegalStateException("No king present.");
	}
	
	public boolean isOccupied(BoardSquare s) {
		return at(s) != ChessPiece.EMPTY;
	}
	
	public boolean canCastleKingside() {
		return !kingMoved && !kingRookMoved;
	}
	
	public boolean canCastleQueenside() {
		return !kingMoved && !queenRookMoved;
	}
	
	public BitBoard differences(ChessSide other) {
		BitBoard result = new BitBoard();
		for (ChessPiece p: boards.keySet()) {
			result.addAll(this.boards.get(p).symmetricDifference(other.boards.get(p)));
		}
		return result;
	}
	
	private boolean has(BoardSquare s, ChessPiece p) {
		return boards.get(p).isSet(s);
	}
	
	public void move(Move m) {
		if (!at(m.getStart()).equals(m.getPiece())) {throw new IllegalArgumentException("Impossible move " + m);}
		teleport(m.getPiece(), m.getStart(), m.getStop());
		if (m.getPiece() == ChessPiece.KING) {
			BoardSquare kingTo = m.getStop();
			if (canCastleKingside() && kingTo.file() == 'g') {
				teleport(ChessPiece.ROOK, kingRookStart, kingRookCastle);
				kingRookMoved = true;
			}
			if (canCastleQueenside() && kingTo.file() == 'c') {
				teleport(ChessPiece.ROOK, queenRookStart, queenRookCastle);
				queenRookMoved = true;
			}
			kingMoved = true;
		} else if (m.getPiece() == ChessPiece.ROOK) {
			if (m.getStart() == kingRookStart) {
				kingRookMoved = true;
			} else if (m.getStart() == queenRookStart) {
				queenRookMoved = true;
			}
		}
	}
	
	private void teleport(ChessPiece p, BoardSquare start, BoardSquare end) {
		remove(p, start);
		add(p, end);
	}
	
	public void remove(ChessPiece p, BoardSquare s) {
		boards.get(p).clear(s);
	}
	
	public void add(ChessPiece p, BoardSquare s) {
		boards.get(p).set(s);
	}
	
	public BitBoard getAllPieces() {
		BitBoard result = new BitBoard();
		for (ChessPiece p: boards.keySet()) {
			result.addAll(boards.get(p));
		}
		return result;
	}
	
	public BitBoard getAllOf(ChessPiece type) {
		return new BitBoard(boards.get(type));
	}
}
