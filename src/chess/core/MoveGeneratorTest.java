package chess.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.*;

public class MoveGeneratorTest {
	public List<BoardSquare> toList(BoardSquare... sqs) {
		return Collections.unmodifiableList(Arrays.asList(sqs));
	}
	
	@Test
	public void testMarch() {
		BitBoard occupied = new BitBoard("00000000\n00010000\n00010000\n00010000\n00000000\n01000110\n00000000\n00000000\n");
		BitBoard moves = MoveGenerator.legalMarchFor(BoardSquare.D3, MoveGenerator.rookDirs, occupied);
		assertEquals(new BitBoard("00000000\n00000000\n00000000\n00010000\n00010000\n01101100\n00010000\n00010000\n"), moves);
	}
	
	@Test
	public void testEnumeration() {
		BitBoard testBoard = new BitBoard("11100000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		ArrayList<BitBoard> combos = MoveGenerator.allCombosOf(testBoard);
		//for (int i = 0; i < combos.size(); ++i) {System.out.println(combos.get(i));}
		assertEquals(new BitBoard("11100000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(0));
		assertEquals(new BitBoard("11000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(1));
		assertEquals(new BitBoard("10100000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(2));
		assertEquals(new BitBoard("10000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(3));
		assertEquals(new BitBoard("01100000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(4));
		assertEquals(new BitBoard("01000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(5));
		assertEquals(new BitBoard("00100000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(6));
		assertEquals(new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n"), combos.get(7));
	}
}
