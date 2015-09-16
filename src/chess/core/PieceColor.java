package chess.core;

public enum PieceColor {
	BLACK {public PieceColor other() {return WHITE;}}, 
	WHITE {public PieceColor other() {return BLACK;}};
	
	abstract public PieceColor other();
}
