package chess.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class MoveDirTest {

	@Test
	public void testBetween() {
		assertEquals(MoveDir.N, MoveDir.between(BoardSquare.A1, BoardSquare.A3));
		assertEquals(MoveDir.S, MoveDir.between(BoardSquare.A3, BoardSquare.A1));
		assertEquals(MoveDir.E, MoveDir.between(BoardSquare.A1, BoardSquare.C1));
		assertEquals(MoveDir.W, MoveDir.between(BoardSquare.C1, BoardSquare.A1));
		assertEquals(MoveDir.NE, MoveDir.between(BoardSquare.A1, BoardSquare.C3));
		assertEquals(MoveDir.NW, MoveDir.between(BoardSquare.C1, BoardSquare.A3));
		assertEquals(MoveDir.SE, MoveDir.between(BoardSquare.A3, BoardSquare.C1));
		assertEquals(MoveDir.SW, MoveDir.between(BoardSquare.C3, BoardSquare.A1));
	}

}
