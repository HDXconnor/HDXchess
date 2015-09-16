package chess.core;

@SuppressWarnings("serial")
public class IllegalMoveException extends Exception {
	public IllegalMoveException(String msg) {
		super(msg);
	}
}
