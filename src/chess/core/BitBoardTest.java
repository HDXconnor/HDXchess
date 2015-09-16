package chess.core;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class BitBoardTest {

	@Test
	@Ignore
	public void test() {
		testBitString("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n11111111\n00000000\n", 8);
		testBitString("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00100100\n", 2);
		testBitString("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00010000\n", 1);
		testBitString("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00001000\n", 1);
		testBitString("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n01000010\n", 2);
		testBitString("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n10000001\n", 2);
	}
	
	public void testBitString(String bs, int numSet) {
		BitBoard bb = new BitBoard(bs);
		int total = 0;
		for (@SuppressWarnings("unused") BoardSquare s: bb) {
			total += 1;
		}
		assertEquals(numSet, total);
		assertEquals(numSet, bb.numPieces());
		assertEquals(bs, bb.toString());
	}

	@Test
	public void testExpectedBoards() {
		assertEquals(new BitBoard("00000000\n01111110\n01111110\n01111110\n01111110\n01111110\n01111110\n00000000\n"), BitBoard.INTERIOR_SQUARES);
		assertEquals(new BitBoard("01111111\n01111111\n01111111\n01111111\n01111111\n01111111\n01111111\n01111111\n"), BitBoard.NO_FILE_A);
		assertEquals(new BitBoard("11111110\n11111110\n11111110\n11111110\n11111110\n11111110\n11111110\n11111110\n"), BitBoard.NO_FILE_H);
		assertEquals(new BitBoard("00000000\n00000000\n00000000\n00000000\n11111111\n00000000\n00000000\n00000000\n"), BitBoard.pawnAdvanceRankOnly(PieceColor.WHITE));
		assertEquals(new BitBoard("00000000\n00000000\n00000000\n11111111\n00000000\n00000000\n00000000\n00000000\n"), BitBoard.pawnAdvanceRankOnly(PieceColor.BLACK));
	}
	
	@Test
	public void testIteration1() {
		System.out.println("testIteration1");
		BitBoard bb = new BitBoard().negation();
		int i = 0;
		for (BoardSquare s: bb) {
			assertEquals(BoardSquare.values()[i], s);
			i += 1;
		}
	}
	
	@Test
	public void testIteration2() {
		System.out.println("testIteration2");
		BitBoard bb = new BitBoard("11100000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		BoardSquare[] results = new BoardSquare[]{BoardSquare.A8, BoardSquare.B8, BoardSquare.C8};
		for (BoardSquare result: results) {
			assertTrue(bb.isSet(result));
		}
		
		int i = 0;
		for (BoardSquare s: bb) {
			assertEquals(results[i], s);
			i += 1;
		}
	}
	
	@Test
	public void testClear() {
		BitBoard bb = new BitBoard().negation();
		for (BoardSquare s: BoardSquare.values()) {
			bb.clear(s);
			assertTrue(!bb.isSet(s));
		}
	}
	
	@Test
	public void testSet() {
		BitBoard bb = new BitBoard();
		for (BoardSquare s: BoardSquare.values()) {
			bb.set(s);
			assertTrue(bb.isSet(s));
		}
	}
	
	public static String toFullBits(long x) {
		String result = Long.toBinaryString(x);
		int zerosNeeded = 64 - result.length();
		for (int i = 0; i < zerosNeeded; ++i) {
			result = "0" + result;
		}
		return result;
	}
	
	@Test
	public void testPawnAdvance() {
		BitBoard startW = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n11111111\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n11111111\n00000000\n00000000\n"), startW.pawnAdvance(PieceColor.WHITE));
		
		BitBoard midW = new BitBoard("00000010\n00001000\n00000000\n00000000\n01000000\n00000000\n10110101\n00000000\n");
		assertEquals(new BitBoard("00001000\n00000000\n00000000\n01000000\n00000000\n10110101\n00000000\n00000000\n"), midW.pawnAdvance(PieceColor.WHITE));
	
		BitBoard startB = new BitBoard("00000000\n11111111\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n11111111\n00000000\n00000000\n00000000\n00000000\n00000000\n"), startB.pawnAdvance(PieceColor.BLACK));
	
		BitBoard midB = new BitBoard("00000000\n00001000\n00000000\n00010000\n01000000\n10100101\n00000010\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n00001000\n00000000\n00010000\n01000000\n10100101\n00000010\n"), midB.pawnAdvance(PieceColor.BLACK));
	}
	
	@Test
	public void testCaptureEast() {
		BitBoard startW = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n11111111\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n01111111\n00000000\n00000000\n"), startW.pawnCaptureEast(PieceColor.WHITE));
		
		BitBoard midW = new BitBoard("00000010\n00001000\n00000000\n00000000\n01000000\n00000000\n10110101\n00000000\n");
		assertEquals(new BitBoard("00000100\n00000000\n00000000\n00100000\n00000000\n01011010\n00000000\n00000000\n"), midW.pawnCaptureEast(PieceColor.WHITE));
	
		BitBoard startB = new BitBoard("00000000\n11111111\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n01111111\n00000000\n00000000\n00000000\n00000000\n00000000\n"), startB.pawnCaptureEast(PieceColor.BLACK));
	
		BitBoard midB = new BitBoard("00000000\n00001000\n00000000\n00010000\n01000000\n10100101\n00000010\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n00000100\n00000000\n00001000\n00100000\n01010010\n00000001\n"), midB.pawnCaptureEast(PieceColor.BLACK));
	}
	
	@Test
	public void testCaptureWest() {
		BitBoard startW = new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n11111111\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n00000000\n00000000\n00000000\n11111110\n00000000\n00000000\n"), startW.pawnCaptureWest(PieceColor.WHITE));
		
		BitBoard midW = new BitBoard("00000010\n00001000\n00000000\n00000000\n01000000\n00000000\n10110101\n00000000\n");
		assertEquals(new BitBoard("00010000\n00000000\n00000000\n10000000\n00000000\n01101010\n00000000\n00000000\n"), midW.pawnCaptureWest(PieceColor.WHITE));
	
		BitBoard startB = new BitBoard("00000000\n11111111\n00000000\n00000000\n00000000\n00000000\n00000000\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n11111110\n00000000\n00000000\n00000000\n00000000\n00000000\n"), startB.pawnCaptureWest(PieceColor.BLACK));
	
		BitBoard midB = new BitBoard("00000000\n00001000\n00000000\n00010000\n01000000\n10100101\n00000010\n00000000\n");
		assertEquals(new BitBoard("00000000\n00000000\n00010000\n00000000\n00100000\n10000000\n01001010\n00000100\n"), midB.pawnCaptureWest(PieceColor.BLACK));
		
	}

	@Test
	public void rookTest() {
		BitBoard rook = BitBoard.makeRookMoves(BoardSquare.D4);
		assertEquals("00010000\n00010000\n00010000\n00010000\n11101111\n00010000\n00010000\n00010000\n", rook.toString());
	}

	@Test
	public void bishopTest() {
		BitBoard bishop = BitBoard.makeBishopMoves(BoardSquare.D4);
		assertEquals("00000001\n10000010\n01000100\n00101000\n00000000\n00101000\n01000100\n10000010\n", bishop.toString());
	}
	
	@Test
	public void queenTest() {
		BitBoard queen = BitBoard.makeQueenMoves(BoardSquare.D4);
		assertEquals("00010001\n10010010\n01010100\n00111000\n11101111\n00111000\n01010100\n10010010\n", queen.toString());
	}
	
	@Test
	public void kingTest() {
		BitBoard king = BitBoard.makeKingMoves(BoardSquare.D4);
		assertEquals("00000000\n00000000\n00000000\n00111000\n00101000\n00111000\n00000000\n00000000\n", king.toString());
	}
	
	@Test
	public void knightTest() {
		BitBoard knight = BitBoard.makeKnightMoves(BoardSquare.D4);
		assertEquals("00000000\n00000000\n00101000\n01000100\n00000000\n01000100\n00101000\n00000000\n", knight.toString());
	}
	
	@Test
	public void unionIntersectionTest() {
		BitBoard rook = BitBoard.makeRookMoves(BoardSquare.D4);
		BitBoard bishop = BitBoard.makeBishopMoves(BoardSquare.D4);
		BitBoard queen = BitBoard.makeQueenMoves(BoardSquare.D4);
		assertEquals(queen, rook.union(bishop));
		assertEquals(rook, queen.intersection(rook));
		assertEquals(bishop, queen.intersection(bishop));
	}
	
	@Test
	public void stringTest() {
		BitBoard b = BitBoard.makeRookMoves(BoardSquare.D4);
		assertEquals(b, new BitBoard(b.toString()));
	}
}
