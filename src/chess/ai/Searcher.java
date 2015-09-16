package chess.ai;

import chess.core.Chessboard;
import chess.core.Move;

abstract public class Searcher {
	private int movesApplied, evalCalls;
	private long start, duration;

	abstract public MoveScore findBestMove(Chessboard board, BoardEval eval, int depth);
	
	int evaluate(Chessboard board, BoardEval eval) {
		evalCalls += 1;
		return eval.eval(board);
	}
	
	Chessboard generate(Chessboard parent, Move m) {
		movesApplied += 1;
		return parent.successor(m);
	}
	
	void setup(Chessboard board, BoardEval eval, int depth) {
		movesApplied = 0;
		evalCalls = 0;
		start = System.currentTimeMillis();		
	}
	
	void tearDown() {
		duration = System.currentTimeMillis() - start;		
	}

	public int getBoardsGenerated() {
		return movesApplied;
	}

	public int getBoardsEvaluated() {
		return evalCalls;
	}

	public long getDuration() {
		return duration;
	}
}
