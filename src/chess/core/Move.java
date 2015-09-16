package chess.core;

public class Move implements Comparable<Move> {
	private PieceColor color;
	private ChessPiece piece, promoteTo;
	private BoardSquare start, stop, capture;
	private boolean hasCapture, canCapture, willPromote;
	private String str;
	private int hash;
	
	public Move(String s) {
		String[] parts = s.split("_");
		this.color = PieceColor.valueOf(parts[0]);
		this.piece = ChessPiece.valueOf(parts[1]);
		this.start = BoardSquare.valueOf(parts[2].toUpperCase());
		this.stop = BoardSquare.valueOf(parts[3].substring(0, 2).toUpperCase());
		
		this.hasCapture = parts[3].contains("x");
		if (this.hasCapture) {
			int where = parts[3].indexOf('x') + 1;
			this.capture = BoardSquare.valueOf(parts[3].substring(where, where + 2).toUpperCase());
		}
		
		this.willPromote = parts[3].contains("=");
		if (this.willPromote) {
			int where = parts[3].indexOf('=') + 1;
			this.promoteTo = ChessPiece.valueOf(parts[3].substring(where).toUpperCase());
			promotionCheck(promoteTo);
		}
		
		this.canCapture = (hasCapture || piece != ChessPiece.PAWN);
		strHash();
	}
	
	public Move(PieceColor color, ChessPiece piece, BoardSquare start, BoardSquare stop) {
		this.color = color;
		this.piece = piece;
		this.start = start;
		this.stop = stop;
		this.hasCapture = false;
		strHash();
	}
	
	public Move(PieceColor color, ChessPiece piece, BoardSquare start, BoardSquare stop, BoardSquare capture) {
		this(color, piece, start, stop);
		addCapture(capture);
		strHash();
	}
	
	public Move(PieceColor color, ChessPiece piece, BoardSquare start, BoardSquare stop, ChessPiece promotion) {
		this(color, piece, start, stop);
		addPromotion(promotion);
		strHash();
	}
	
	public Move(PieceColor color, ChessPiece piece, BoardSquare start, BoardSquare stop, BoardSquare capture, ChessPiece promotion) {
		this(color, piece, start, stop);
		addCapture(capture);
		addPromotion(promotion);
		strHash();
	}
	
	private void addCapture(BoardSquare capture) {
		this.hasCapture = true;
		this.capture = capture;
	}
	
	private void promotionCheck(ChessPiece promotion) {
		if (piece != ChessPiece.PAWN) {throw new IllegalArgumentException("Only pawns promote!");}
		if (!stop.pawnEnd(color)) {throw new IllegalArgumentException("Pawns only promote at the end!");}
	}
	
	private void addPromotion(ChessPiece promotion) {
		promotionCheck(promotion);
		this.willPromote = true;
		this.promoteTo = promotion;
	}
	 
	private void strHash() {
		this.str = color.toString() + "_" + piece.toString() + "_"  + start.toString() + "_"  + stop.toString();
		if (hasCapture) {this.str += "x" + capture.toString();}
		if (willPromote) {this.str += "=" + promoteTo.toString();}
		this.hash = this.str.hashCode();		
	}
	
	public ChessPiece getPiece() {return piece;}
	public PieceColor getColor() {return color;}
	public BoardSquare getStart() {return start;}
	public BoardSquare getStop() {return stop;}
	public boolean captures() {return hasCapture;}
	public BoardSquare getCapture() {return capture;}
	public boolean canCauseCheck() {return canCapture;}
	public boolean promotes() {return willPromote;}
	public ChessPiece promotesTo() {return promoteTo;}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Move) {
			Move that = (Move)other;
			return this.color == that.color && this.piece == that.piece && this.start == that.start && this.stop == that.stop;
		} else {
			return false;
		}
 	}
	
	public boolean isCastlingMove() {
		return piece.equals(ChessPiece.KING) && start.fileDiff(stop) == 2;
	}
	
	public String toString() {
		return str;
	}
	
	public int hashCode() {
		return hash;
	}

	@Override
	public int compareTo(Move m) {
		if (this.color == m.color) {
			if (this.piece == m.piece) {
				if (this.start == m.start) {
					if (this.stop == m.stop) {
						return 0;
					} else {
						return this.stop.compareTo(m.stop);
					}
				} else {
					return this.start.compareTo(m.start);
				}
			} else {
				return this.piece.compareTo(m.piece);
			}
		} else {
			return this.color.compareTo(m.color);
		}
	}
}
