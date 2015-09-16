package chess.core;

import static org.junit.Assert.*;

import org.junit.*;

public class ChessSideTest {

	ChessSide white;
	
	@Before
	public void setup() {
		white = ChessSide.makeWhiteStart();
	}
	
	@Test
	public void moveTest() {
		white.move(new Move("WHITE_PAWN_e2_e4"));
		assertEquals(ChessPiece.PAWN, white.at(BoardSquare.E4));
		assertEquals(ChessPiece.EMPTY, white.at(BoardSquare.E2));
	}
	
	public void kingCastleClear() {
		white.move(new Move("WHITE_PAWN_e2_e4"));
		white.move(new Move("WHITE_KNIGHT_g1_f3"));
		white.move(new Move("WHITE_BISHOP_f1_e2"));
		assertTrue(white.canCastleKingside());		
	}

	@Test
	public void kingCastleTest1() {
		kingCastleClear();
		white.move(new Move("WHITE_KING_e1_g1"));
		assertFalse(white.canCastleKingside());
		assertFalse(white.canCastleQueenside());
		assertEquals(ChessPiece.KING, white.at(BoardSquare.G1));
		assertEquals(ChessPiece.ROOK, white.at(BoardSquare.F1));
	}

	@Test
	public void kingCastleTest2() {
		kingCastleClear();
		white.move(new Move("WHITE_KING_e1_f1"));
		assertFalse(white.canCastleKingside());
		assertFalse(white.canCastleQueenside());
		white.move(new Move("WHITE_KING_f1_e1"));
		assertFalse(white.canCastleKingside());
		assertFalse(white.canCastleQueenside());
	}

	@Test
	public void kingCastleTest3() {
		kingCastleClear();
		white.move(new Move("WHITE_ROOK_h1_g1"));
		assertFalse(white.canCastleKingside());
		assertTrue(white.canCastleQueenside());
	}
	
	public void queenCastleClear() {
		white.move(new Move("WHITE_PAWN_e2_e4"));
		white.move(new Move("WHITE_PAWN_d2_d4"));
		white.move(new Move("WHITE_KNIGHT_b1_c3"));
		white.move(new Move("WHITE_BISHOP_c1_e3"));
		white.move(new Move("WHITE_QUEEN_d1_d2"));
		assertTrue(white.canCastleQueenside());		
	}
	
	@Test
	public void queenCastleTest1() {
		queenCastleClear();
		white.move(new Move("WHITE_KING_e1_c1"));
		assertFalse(white.canCastleKingside());
		assertFalse(white.canCastleQueenside());
		assertEquals(ChessPiece.KING, white.at(BoardSquare.C1));
		assertEquals(ChessPiece.ROOK, white.at(BoardSquare.D1));
	}
	
	@Test
	public void queenCastleTest2() {
		queenCastleClear();
		white.move(new Move("WHITE_ROOK_a1_d1"));
		assertTrue(white.canCastleKingside());
		assertFalse(white.canCastleQueenside());
	}
}
