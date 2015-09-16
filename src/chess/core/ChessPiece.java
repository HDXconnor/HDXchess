package chess.core;

public enum ChessPiece {
	PAWN {
		public char symbol() {return 'P';}
		public boolean castsShadow() {return false;}
	}, QUEEN {
		public char symbol() {return 'Q';}
		public boolean castsShadow() {return true;}
	}, ROOK {
		public char symbol() {return 'R';}
		public boolean castsShadow() {return true;}
	}, BISHOP {
		public char symbol() {return 'B';}
		public boolean castsShadow() {return true;}
	}, KNIGHT {
		public char symbol() {return 'N';}
		public boolean castsShadow() {return false;}
	}, KING {
		public char symbol() {return 'K';}
		public boolean castsShadow() {return false;}
	}, EMPTY {
		public char symbol() {return '-';}
		public boolean castsShadow() {return false;}
	};
	abstract public boolean castsShadow();
	abstract public char symbol();
	
	public final static ChessPiece[] slidingPieces = new ChessPiece[]{QUEEN, ROOK, BISHOP};
}