package chess.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoardSquareTest {
	
	@Test
	public void fileNums() {
		assertEquals(1, BoardSquare.A1.fileNum());
		assertEquals(2, BoardSquare.B1.fileNum());
		assertEquals(3, BoardSquare.C1.fileNum());
		assertEquals(4, BoardSquare.D1.fileNum());
		assertEquals(5, BoardSquare.E1.fileNum());
		assertEquals(6, BoardSquare.F1.fileNum());
		assertEquals(7, BoardSquare.G1.fileNum());
		assertEquals(8, BoardSquare.H1.fileNum());
	}
	
	@Test
	public void pawned() {
		assertEquals(BoardSquare.E3, BoardSquare.E2.pawnAdvanceFrom(PieceColor.WHITE));
		assertEquals(BoardSquare.E4, BoardSquare.E3.pawnAdvanceFrom(PieceColor.WHITE));
		assertEquals(BoardSquare.E5, BoardSquare.E4.pawnAdvanceFrom(PieceColor.WHITE));
		assertEquals(BoardSquare.E6, BoardSquare.E5.pawnAdvanceFrom(PieceColor.WHITE));
		assertEquals(BoardSquare.E7, BoardSquare.E6.pawnAdvanceFrom(PieceColor.WHITE));
		assertEquals(BoardSquare.E8, BoardSquare.E7.pawnAdvanceFrom(PieceColor.WHITE));
		
		assertEquals(BoardSquare.E6, BoardSquare.E7.pawnAdvanceFrom(PieceColor.BLACK));
		assertEquals(BoardSquare.E5, BoardSquare.E6.pawnAdvanceFrom(PieceColor.BLACK));
		assertEquals(BoardSquare.E4, BoardSquare.E5.pawnAdvanceFrom(PieceColor.BLACK));
		assertEquals(BoardSquare.E3, BoardSquare.E4.pawnAdvanceFrom(PieceColor.BLACK));
		assertEquals(BoardSquare.E2, BoardSquare.E3.pawnAdvanceFrom(PieceColor.BLACK));
		assertEquals(BoardSquare.E1, BoardSquare.E2.pawnAdvanceFrom(PieceColor.BLACK));
	}

	@Test
	public void a1NoGo() {
		assertFalse(BoardSquare.A1.hasSuccessor(MoveDir.SE));
		assertFalse(BoardSquare.A1.hasSuccessor(MoveDir.S));
		assertFalse(BoardSquare.A1.hasSuccessor(MoveDir.NW));
		assertFalse(BoardSquare.A1.hasSuccessor(MoveDir.W));
		assertFalse(BoardSquare.A1.hasSuccessor(MoveDir.SW));
	}
	
	@Test
	public void a1GoN() {
		assertTrue(BoardSquare.A1.hasSuccessor(MoveDir.N));
		assertEquals(BoardSquare.A2, BoardSquare.A1.successor(MoveDir.N));
		assertEquals(BoardSquare.A3, BoardSquare.A2.successor(MoveDir.N));
		assertEquals(BoardSquare.A4, BoardSquare.A3.successor(MoveDir.N));
		assertEquals(BoardSquare.A5, BoardSquare.A4.successor(MoveDir.N));
		assertEquals(BoardSquare.A6, BoardSquare.A5.successor(MoveDir.N));
		assertEquals(BoardSquare.A7, BoardSquare.A6.successor(MoveDir.N));
		assertEquals(BoardSquare.A8, BoardSquare.A7.successor(MoveDir.N));
		assertFalse(BoardSquare.A8.hasSuccessor(MoveDir.N));
	}
	
	@Test 
	public void a1GoE() {
		assertTrue(BoardSquare.A1.hasSuccessor(MoveDir.E));
		assertEquals(BoardSquare.B1, BoardSquare.A1.successor(MoveDir.E));
		assertEquals(BoardSquare.C1, BoardSquare.B1.successor(MoveDir.E));
		assertEquals(BoardSquare.D1, BoardSquare.C1.successor(MoveDir.E));
		assertEquals(BoardSquare.E1, BoardSquare.D1.successor(MoveDir.E));
		assertEquals(BoardSquare.F1, BoardSquare.E1.successor(MoveDir.E));
		assertEquals(BoardSquare.G1, BoardSquare.F1.successor(MoveDir.E));
		assertEquals(BoardSquare.H1, BoardSquare.G1.successor(MoveDir.E));
		assertFalse(BoardSquare.H1.hasSuccessor(MoveDir.E));
	}

}
