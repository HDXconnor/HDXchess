package chess.ai;

import chess.core.Chessboard;

public interface BoardEval {
	public int eval(Chessboard board);
	public int maxValue();
}
