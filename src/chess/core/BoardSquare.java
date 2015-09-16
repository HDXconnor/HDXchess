package chess.core;

public enum BoardSquare {
	A8, B8, C8, D8, E8, F8, G8, H8,
	A7, B7, C7, D7, E7, F7, G7, H7,
	A6, B6, C6, D6, E6, F6, G6, H6,
	A5, B5, C5, D5, E5, F5, G5, H5,
	A4, B4, C4, D4, E4, F4, G4, H4,
	A3, B3, C3, D3, E3, F3, G3, H3,
	A2, B2, C2, D2, E2, F2, G2, H2,
	A1, B1, C1, D1, E1, F1, G1, H1;
	
	private String label;
	private long mask;
	
	private BoardSquare() {
		this.label = name().toLowerCase();
		this.mask = 1L << this.ordinal();
	}
	
	public long getMask() {return mask;}
	
	@Override
	public String toString() {return label;}
	
	public int fileNum() {
		return label.charAt(0) - 'a' + 1;
	}
	
	public char file() {
		return label.charAt(0);
	}
	
	public int rankNum() {
		return label.charAt(1) - '0';
	}
	
	public int fileDiff(BoardSquare other) {
		return Math.abs(this.fileNum() - other.fileNum());
	}
	
	public int rankDiff(BoardSquare other) {
		return Math.abs(this.rankNum() - other.rankNum());
	}
	
	public boolean hasKnightMoveTo(BoardSquare other) {
		return this.fileDiff(other) == 2 && this.rankDiff(other) == 1
				|| this.fileDiff(other) == 1 && this.rankDiff(other) == 2;
	}
	
	public boolean hasRookMoveTo(BoardSquare other) {
		return this != other && (this.fileNum() == other.fileNum() || this.rankNum() == other.rankNum());
	}
	
	public boolean hasBishopMoveTo(BoardSquare other) {
		return this != other && fileDiff(other) == rankDiff(other);
	}
	
	public boolean hasKingMoveTo(BoardSquare other) {
		return this != other && fileDiff(other) <= 1 && rankDiff(other) <= 1;
	}
	
	public boolean onEdge() {
		return rankNum() == 1 || rankNum() == 8 || fileNum() == 1 || fileNum() == 8;
	}
	
	public boolean pawnImpossible() {
		int rank = rankNum();
		return rank == 1 || rank == 8;
	}
	
	public boolean pawnStart(PieceColor color) {
		return color == PieceColor.WHITE && rankNum() == 2 || 
				color == PieceColor.BLACK && rankNum() == 7;
	}
	
	public boolean pawnJumpTarget(PieceColor color) {
		return color == PieceColor.WHITE && rankNum() == 4 ||
				color == PieceColor.BLACK && rankNum() == 5;
	}
	
	public boolean pawnEnd(PieceColor color) {
		return color == PieceColor.WHITE && rankNum() == 8 ||
				color == PieceColor.BLACK && rankNum() == 1;
	}
	
	public BoardSquare pawnAdvanceFrom(PieceColor color) {
		return successor(color == PieceColor.WHITE ? MoveDir.N : MoveDir.S);
	}
	
	public boolean hasPawnEast() {
		return !pawnImpossible() && file() != 'h';
	}
	
	public boolean hasPawnWest() {
		return !pawnImpossible() && file() != 'a';
	}
	
	public BoardSquare pawnCaptureEast(PieceColor color) {
		return successor(color == PieceColor.WHITE ? MoveDir.NE : MoveDir.SE);
	}
	
	public BoardSquare pawnCaptureWest(PieceColor color) {
		return successor(color == PieceColor.WHITE ? MoveDir.NW : MoveDir.SW);
	}
	
	public BoardSquare successor(MoveDir dir) {
		int newFile = fileNum() + dir.xDelta();
		int newRank = rankNum() + dir.yDelta();
		return fileRank2Square(newFile, newRank);
	}
	
	public boolean hasSuccessor(MoveDir dir) {
		int newFile = fileNum() + dir.xDelta();
		int newRank = rankNum() + dir.yDelta();
		return newRank >= 1 && newRank <= 8 && newFile >= 1 && newFile <= 8;
	}
	
	public static BoardSquare fileRank2Square(int file, int rank) {
		return BoardSquare.valueOf(fileToStr(file) + rankToStr(rank));
	}
	
	private static String fileToStr(int file) {
		return Character.toString((char)('A' + file - 1));
	}
	
	private static String rankToStr(int rank) {
		return Character.toString((char)('0' + rank));
	}
}
