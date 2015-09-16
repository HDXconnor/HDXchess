package chess.core;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class MoveTest {
	
	private Move m1, m2, m3;
	
	@Before 
	public void setup() {
		m1 = new Move(PieceColor.WHITE, ChessPiece.PAWN, BoardSquare.E2, BoardSquare.E4);
		m2 = new Move(PieceColor.WHITE, ChessPiece.PAWN, BoardSquare.E2, BoardSquare.E4);		
		m3 = new Move(PieceColor.WHITE, ChessPiece.PAWN, BoardSquare.E2, BoardSquare.E3);		
	}

	@Test
	public void stringTest() {
		assertEquals("WHITE_PAWN_e2_e4", m1.toString());
		assertEquals(m1, new Move(m1.toString()));
	}

	@Test
	public void eqTest() {
		assertEquals(m1, m2);
		assertFalse(m1.equals(m3));
		assertFalse(m2.equals(m3));
	}
	
	@Test
	public void hashSetTest() {
		setTest(new HashSet<Move>());
	}
	
	@Test
	public void treeSetTest() {
		setTest(new TreeSet<Move>());
	}
	
	public void setTest(Set<Move> s) {
		s.add(m1);
		s.add(m2);
		s.add(m3);
		assertTrue(s.contains(m1));
		assertTrue(s.contains(m2));
		assertTrue(s.contains(m3));
		assertEquals(2, s.size());
	}
}
