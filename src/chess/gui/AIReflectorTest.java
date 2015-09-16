package chess.gui;

import static org.junit.Assert.*;

import org.junit.Test;

import chess.ai.Searcher;


public class AIReflectorTest {

	@Test
	public void test() {
		AIReflector<Searcher> searchers = new AIReflector<Searcher>(Searcher.class, "edu.hendrix.chess.ai");
		assertEquals("Available: AlphaBeta Minimax OrderedAlphaBeta OrderedCachingAlphaBeta", searchers.toString());
	}

}
