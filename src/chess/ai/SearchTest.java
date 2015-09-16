package chess.ai;

import static org.junit.Assert.*;

import org.junit.*;

import chess.core.Algebraic;
import chess.core.Chessboard;
import chess.core.IllegalMoveException;
import chess.core.Move;


public class SearchTest {

	@Test
	public void test1() throws IllegalMoveException {
		testBasicMate(new Minimax());
	}

	public void testBasicMate(Searcher s) throws IllegalMoveException {
		BoardEval eval = new BasicMaterial();
		
		Chessboard board1 = Algebraic.from("f4", "e5");
		Move best = s.findBestMove(board1, eval, 2).getMove();
		Chessboard board1a = board1.successor(best);
		Move reply = s.findBestMove(board1a, eval, 2).getMove();
		Chessboard board1b = board1a.successor(reply);
		System.out.println("best: " + Algebraic.encode(best, board1) + " reply: " + Algebraic.encode(reply, board1a));
		assertTrue(!board1b.isCheckmate());
		
		Chessboard board2 = Algebraic.successor(board1, "g4");
		assertEquals(Algebraic.decode("Qh4#", board2), s.findBestMove(board2, eval, 2).getMove());
	}
}
