package chess.core;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

import org.junit.*;


public class ChessboardTest {
	
	Chessboard[] boards;
	BitBoard[] diffs;
	
	@Before
	public void setup() throws IllegalMoveException {
		boards = new Chessboard[29];
		String[] moves = new String[]{"e4", "c5", "Nf3", "d6", "d4", "cxd4", "c4", "dxc3", "Bb5+",
				"Bd7", "e5", "Bxb5", "Qd3", "f5", "O-O", "Qd7", "a4", "Nc6", "axb5", "cxb2", "Nc3",
				"bxa1=Q", "Qxf5", "a6", "Qh5+", "g6", "h3", "O-O-O"};
		diffs = new BitBoard[]{
				new BitBoard(BoardSquare.E2, BoardSquare.E4), 
				new BitBoard(BoardSquare.C7, BoardSquare.C5),
				new BitBoard(BoardSquare.G1, BoardSquare.F3),
				new BitBoard(BoardSquare.D7, BoardSquare.D6),
				new BitBoard(BoardSquare.D2, BoardSquare.D4),
				new BitBoard(BoardSquare.C5, BoardSquare.D4),
				new BitBoard(BoardSquare.C2, BoardSquare.C4),
				new BitBoard(BoardSquare.D4, BoardSquare.C3, BoardSquare.C4),
				new BitBoard(BoardSquare.F1, BoardSquare.B5), 
				new BitBoard(BoardSquare.C8, BoardSquare.D7),
				new BitBoard(BoardSquare.E4, BoardSquare.E5),
				new BitBoard(BoardSquare.D7, BoardSquare.B5),
				new BitBoard(BoardSquare.D1, BoardSquare.D3),
				new BitBoard(BoardSquare.F7, BoardSquare.F5),
				new BitBoard(BoardSquare.E1, BoardSquare.G1, BoardSquare.H1, BoardSquare.F1), 
				new BitBoard(BoardSquare.D8, BoardSquare.D7),
				new BitBoard(BoardSquare.A2, BoardSquare.A4),
				new BitBoard(BoardSquare.B8, BoardSquare.C6),
				new BitBoard(BoardSquare.A4, BoardSquare.B5),
				new BitBoard(BoardSquare.C3, BoardSquare.B2),
				new BitBoard(BoardSquare.B1, BoardSquare.C3),
				new BitBoard(BoardSquare.B2, BoardSquare.A1),
				new BitBoard(BoardSquare.D3, BoardSquare.F5),
				new BitBoard(BoardSquare.A7, BoardSquare.A6),
				new BitBoard(BoardSquare.F5, BoardSquare.H5),
				new BitBoard(BoardSquare.G7, BoardSquare.G6),
				new BitBoard(BoardSquare.H2, BoardSquare.H3),
				new BitBoard(BoardSquare.E8, BoardSquare.C8, BoardSquare.A8, BoardSquare.D8)};
		
		boards[0] = new Chessboard();
		for (int i = 1; i < boards.length; ++i) {
			boards[i] = Algebraic.successor(boards[i-1], moves[i-1]);
		}
	}

	@Test
	public void init() {
		Chessboard b = new Chessboard();
		assertEquals("WHITE\nrnbqkbnr\npppppppp\n--------\n--------\n--------\n--------\nPPPPPPPP\nRNBQKBNR\n", b.toString());
	}

	@Test
	public void game1() {
		Chessboard b = new Chessboard();
		assertTrue(b.onePerSquare());
		b = b.successor(new Move("WHITE_PAWN_e2_e4"));
		assertTrue(b.onePerSquare());
		b = b.successor(new Move("BLACK_PAWN_d7_d5"));
		assertTrue(b.onePerSquare());
		b = b.successor(new Move("WHITE_PAWN_e4_d5xd5"));
		assertTrue(b.onePerSquare());
		assertEquals("BLACK\nrnbqkbnr\nppp-pppp\n--------\n---P----\n--------\n--------\nPPPP-PPP\nRNBQKBNR\n", b.toString());
	}
	
	public Set<Move> makeGoal(Chessboard board, String... moves) throws IllegalMoveException {
		Set<Move> goal = new TreeSet<Move>();
		for (String move: moves) {
			goal.add(Algebraic.decode(move, board));
		}
		return goal;
	}
	
	public void addToGoal(Chessboard board, Set<Move> moves, String... adds) throws IllegalMoveException {
		moves.addAll(makeGoal(board, adds));
	}
	
	public void removeFromGoal(Chessboard board, Set<Move> moves, String... dels) throws IllegalMoveException {
		moves.removeAll(makeGoal(board, dels));
	}
	
	public void printMoves(Set<Move> moves) {
		for (Move m: moves) {
			System.out.print(m + " ");
		}
		System.out.println();
	}
	
	public Set<Move> diffs(Set<Move> one, Set<Move> two) {
		Set<Move> copy = new TreeSet<Move>(one);
		copy.removeAll(two);
		return copy;
	}
	
	public void printAllDiffs(Set<Move> one, Set<Move> two, String oneLabel, String twoLabel) {
		System.out.println("In " + oneLabel + " but not " + twoLabel);
		printMoves(diffs(one, two));
		System.out.println("In " + twoLabel + " but not " + oneLabel);
		printMoves(diffs(two, one));
	}
	
	public void lastMoveWas(String expected, int whichBoard) {
		assertEquals(expected, Algebraic.encode(boards[whichBoard].getLastMove(), boards[whichBoard-1]));
		assertEquals(diffs[whichBoard-1], boards[whichBoard].differences(boards[whichBoard-1]));
	}
	
	@Test
	public void testStart() throws IllegalMoveException {
		moveCheck(boards[0], 
				makeGoal(boards[0], "a3", "a4", "b3", "b4", "c3", "c4", "d3", "d4", "e3", 
						"e4", "f3", "f4", "g3", "g4", "h3", "h4", "Nh3", "Nf3", "Na3", "Nc3"),
				"WHITE\nrnbqkbnr\npppppppp\n--------\n--------\n--------\n--------\nPPPPPPPP\nRNBQKBNR\n");
	}

	@Test
	public void testMove1() throws IllegalMoveException {
		moveCheck(boards[1],
				makeGoal(boards[1], "a6", "a5", "b6", "b5", "c6", "c5", "d6", "d5", "e6", "e5", "f6",	
						"f5", "g6", "g5", "h6", "h5", "h6", "f6", "a6", "c6", "Na6", "Nc6", "Nf6", "Nh6"),
				"BLACK\nrnbqkbnr\npppppppp\n--------\n--------\n----P---\n--------\nPPPP-PPP\nRNBQKBNR\n");
		lastMoveWas("e4", 1);
	}

	@Test
	public void testMove2() throws IllegalMoveException {
		moveCheck(boards[2], 
				makeGoal(boards[2], "Ke2", "Qe2", "Qf3", "Qg4", "Qh5", "Ba6", "Bb5", "Bc4", "Bd3", 
						"Be2", "Na3", "Nc3", "Ne2", "Nf3", "Nh3", "a3", "a4", "b3", "b4", "c3", "c4", 
						"d3", "d4", "e5", "f3", "f4", "g3", "g4", "h3", "h4"),
				"WHITE\nrnbqkbnr\npp-ppppp\n--------\n--p-----\n----P---\n--------\nPPPP-PPP\nRNBQKBNR\n");
		lastMoveWas("c5", 2);
	}

	@Test
	public void testMove3() throws IllegalMoveException {
		moveCheck(boards[3],
				makeGoal(boards[3], "Qa5", "Qb6", "Qc7", "Na6", "Nc6", "Nf6", "Nh6", "a5", "a6", "b5", 
						"b6", "c4", "d5", "d6", "e5", "e6", "f5", "f6", "g5", "g6", "h5", "h6"),
				"BLACK\nrnbqkbnr\npp-ppppp\n--------\n--p-----\n----P---\n-----N--\nPPPP-PPP\nRNBQKB-R\n");
		lastMoveWas("Nf3", 3);
	}
	
	@Test
	public void testMove4() throws IllegalMoveException {
		moveCheck(boards[4],
				makeGoal(boards[4], "Ke2", "Qe2", "Rg1", "Ba6", "Bb5+", "Bc4", "Bd3", "Be2", "Na3", "Nc3", 
						"Nd4", "Ne5", "Ng1", "Ng5", "Nh4", "a3", "a4", "b3", "b4", "c3", "c4", "d3", "d4", "e5", 
						"g3", "g4", "h3", "h4"),
				"WHITE\nrnbqkbnr\npp--pppp\n---p----\n--p-----\n----P---\n-----N--\nPPPP-PPP\nRNBQKB-R\n");
		lastMoveWas("d6", 4);
	}
	
	@Test
	public void testMove5() throws IllegalMoveException {
		moveCheck(boards[5],
				makeGoal(boards[5], "Kd7", "Qa5+", "Qb6", "Qc7", "Qd7", "Bd7", "Be6", "Bf5", "Bg4", "Bh3", "Na6", 
						"Nc6", "Nd7", "Nf6", "Nh6", "a5", "a6", "b5", "b6", "c4", "cxd4", "d5", "e5", "e6", "f5", 
						"f6", "g5", "g6", "h5", "h6"),
				"BLACK\nrnbqkbnr\npp--pppp\n---p----\n--p-----\n---PP---\n-----N--\nPPP--PPP\nRNBQKB-R\n");
		lastMoveWas("d4", 5);
	}
	
	@Test
	public void testMove6() throws IllegalMoveException {
		moveCheck(boards[6],
				makeGoal(boards[6], "Kd2", "Ke2", "Qd2", "Qd3", "Qxd4", "Qe2", "Rg1", "Bd2", "Be3", "Bf4", "Bg5", 
						"Bh6", "Ba6", "Bb5+", "Bc4", "Bd3", "Be2", "Na3", "Nc3", "Nbd2", "Nfd2", "Nxd4", "Ne5", 
						"Ng1", "Ng5", "Nh4", "a3", "a4", "b3", "b4", "c3", "c4", "e5", "g3", "g4", "h3", "h4"),
				"WHITE\nrnbqkbnr\npp--pppp\n---p----\n--------\n---pP---\n-----N--\nPPP--PPP\nRNBQKB-R\n");
		lastMoveWas("cxd4", 6);
	}
	
	@Test
	public void testMove7() throws IllegalMoveException {
		moveCheck(boards[7], 
				makeGoal(boards[7], "Kd7", "Qa5+", "Qb6", "Qc7", "Qd7", "Bd7", "Be6", "Bf5", "Bg4", "Bh3", "Na6", 
						"Nc6", "Nd7", "Nf6", "Nh6", "a5", "a6", "b5", "b6", "dxc3", "d3", "d5", "e5", "e6", "f5", 
						"f6", "g5", "g6", "h5", "h6"),
				"BLACK\nrnbqkbnr\npp--pppp\n---p----\n--------\n--PpP---\n-----N--\nPP---PPP\nRNBQKB-R\n");
		lastMoveWas("c4", 7);
	}
	
	@Test
	public void testMove8() throws IllegalMoveException {
		moveCheck(boards[8],
				makeGoal(boards[8], "Ke2", "Qa4+", "Qb3", "Qc2", "Qd2", "Qd3", "Qd4", "Qd5", "Qxd6", "Qe2", "Rg1", 
						"Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Ba6", "Bb5+", "Bc4", "Bd3", "Be2", "Na3", "Nxc3", 
						"Nbd2", "Nfd2", "Nd4", "Ne5", "Ng1", "Ng5", "Nh4", "a3", "a4", "b3", "b4", "bxc3", "e5", 
						"g3", "g4", "h3", "h4"),
				"WHITE\nrnbqkbnr\npp--pppp\n---p----\n--------\n----P---\n--p--N--\nPP---PPP\nRNBQKB-R\n");
		lastMoveWas("dxc3", 8);
	}
	
	@Test
	public void testMove9() throws IllegalMoveException {
		moveCheck(boards[9],
				makeGoal(boards[9], "Qd7", "Bd7", "Nc6", "Nd7"), 
				"BLACK\nrnbqkbnr\npp--pppp\n---p----\n-B------\n----P---\n--p--N--\nPP---PPP\nRNBQK--R\n");
		lastMoveWas("Bb5+", 9);
	}
	
	@Test
	public void testMove10() throws IllegalMoveException {
		moveCheck(boards[10],
				makeGoal(boards[10], "Ke2", "Kf1", "O-O", "Qa4", "Qb3", "Qc2", "Qd2", "Qd3", "Qd4", "Qd5", 
						"Qxd6", "Qe2", "Rf1", "Rg1", "Ba4", "Ba6", "Bc4", "Bc6", "Bd3", "Bxd7+", "Be2", 
						"Bf1", "Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Na3", "Nxc3", "Nbd2", "Nfd2", "Nd4", 
						"Ne5", "Ng1", "Ng5", "Nh4", "a3", "a4", "b3", "b4", "bxc3", "e5", "g3", "g4", 
						"h3", "h4"),
				"WHITE\nrn-qkbnr\npp-bpppp\n---p----\n-B------\n----P---\n--p--N--\nPP---PPP\nRNBQK--R\n");
		lastMoveWas("Bd7", 10);
	}
	
	@Test
	public void testMove11() throws IllegalMoveException {
		moveCheck(boards[11],
				makeGoal(boards[11], "Qa5", "Qb6", "Qc7", "Qc8", "Bxb5", "Bc6", "Na6", "Nc6", "Nf6", 
						"Nh6", "a5", "a6", "b6", "cxb2", "c2", "d5", "dxe5", "e6", "f5", "f6", "g5", 
						"g6", "h5", "h6"),
				"BLACK\nrn-qkbnr\npp-bpppp\n---p----\n-B--P---\n--------\n--p--N--\nPP---PPP\nRNBQK--R\n");
		lastMoveWas("e5", 11);
	}
	
	@Test
	public void testMove12() throws IllegalMoveException {
		moveCheck(boards[12], 
				makeGoal(boards[12], "Qa4", "Qb3", "Qc2", "Qd2", "Qd3", "Qd4", "Qd5", "Qxd6", "Qe2", 
						"Rf1", "Rg1", "Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Na3", "Nxc3", "Nbd2", "Nfd2", 
						"Nd4", "Ng1", "Ng5", "Nh4", "a3", "a4", "b3", "b4", "bxc3", "exd6", "e6", "g3", 
						"g4", "h3", "h4"), 
				"WHITE\nrn-qkbnr\npp--pppp\n---p----\n-b--P---\n--------\n--p--N--\nPP---PPP\nRNBQK--R\n");
		lastMoveWas("Bxb5", 12);
	}
	
	@Test
	public void testMove13() throws IllegalMoveException {
		moveCheck(boards[13],
				makeGoal(boards[13], "Kd7", "Qa5", "Qb6", "Qc7", "Qc8", "Qd7", "Ba4", "Ba6", "Bc4", "Bc6", 
						"Bxd3", "Bd7", "Na6", "Nc6", "Nd7", "Nf6", "Nh6", "a5", "a6", "b6", "cxb2", "c2", 
						"d5", "dxe5", "e6", "f5", "f6", "g5", "g6", "h5", "h6"), 
				"BLACK\nrn-qkbnr\npp--pppp\n---p----\n-b--P---\n--------\n--pQ-N--\nPP---PPP\nRNB-K--R\n");
		lastMoveWas("Qd3", 13);
	}
	
	@Test
	public void testMove14() throws IllegalMoveException {
		moveCheck(boards[14],
				makeGoal(boards[14], "Kd1", "Ke2", "Kf1", "O-O", "Qxb5+", "Qc2", "Qxc3", "Qc4", "Qd1", 
						"Qd2", "Qd4", "Qd5", "Qxd6", "Qe2", "Qe3", "Qe4", "Qf1", "Qxf5", "Rf1", "Rg1", 
						"Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Na3", "Nxc3", "Nbd2", "Nfd2", "Nd4", "Ng1", 
						"Ng5", "Nh4", "a3", "a4", "b3", "b4", "bxc3", "exd6", "e6", "exf6", "g3", "g4", 
						"h3", "h4"), 
				"WHITE\nrn-qkbnr\npp--p-pp\n---p----\n-b--Pp--\n--------\n--pQ-N--\nPP---PPP\nRNB-K--R\n");
		lastMoveWas("f5", 14);
	}
	
	@Test
	public void testMove15() throws IllegalMoveException {
		moveCheck(boards[15], 
				makeGoal(boards[15], "Kd7", "Kf7", "Qa5", "Qb6", "Qc7", "Qc8", "Qd7", "Ba4", "Ba6", 
						"Bc4", "Bc6", "Bxd3", "Bd7", "Na6", "Nc6", "Nd7", "Nf6", "Nh6", "a5", "a6", 
						"b6", "cxb2", "c2", "d5", "dxe5", "e6", "f4", "g5", "g6", "h5", "h6"), 
				"BLACK\nrn-qkbnr\npp--p-pp\n---p----\n-b--Pp--\n--------\n--pQ-N--\nPP---PPP\nRNB--RK-\n");
		lastMoveWas("O-O", 15);
	}
	
	@Test
	public void testMove16() throws IllegalMoveException {
		moveCheck(boards[16], 
				makeGoal(boards[16], "Kh1", "Qxb5", "Qc2", "Qxc3", "Qc4", "Qd1", "Qd2", "Qd4", "Qd5",
						"Qxd6", "Qe2", "Qe3", "Qe4", "Qxf5", "Rd1", "Re1", "Bd2", "Be3", "Bf4", "Bg5", 
						"Bh6", "Na3", "Nxc3", "Nbd2", "Nfd2", "Nd4", "Ne1", "Ng5", "Nh4", "a3", "a4", 
						"b3", "b4", "bxc3", "exd6", "e6", "g3", "g4", "h3", "h4"), 
				"WHITE\nrn--kbnr\npp-qp-pp\n---p----\n-b--Pp--\n--------\n--pQ-N--\nPP---PPP\nRNB--RK-\n");
		lastMoveWas("Qd7", 16);
	}
	
	@Test
	public void testMove17() throws IllegalMoveException {
		moveCheck(boards[17],
				makeGoal(boards[17], "Kd8", "Kf7", "Qc6", "Qc7", "Qc8", "Qd8", "Qe6", "Bxa4", "Ba6", 
						"Bc4", "Bc6", "Bxd3", "Na6", "Nc6", "Nf6", "Nh6", "a5", "a6", "b6", "cxb2", 
						"c2", "d5", "dxe5", "e6", "f4", "g5", "g6", "h5", "h6"), 
				"BLACK\nrn--kbnr\npp-qp-pp\n---p----\n-b--Pp--\nP-------\n--pQ-N--\n-P---PPP\nRNB--RK-\n");
		lastMoveWas("a4", 17);
	}
	
	@Test
	public void testMove18() throws IllegalMoveException {
		moveCheck(boards[18], 
				makeGoal(boards[18], "Kh1", "Qxb5", "Qc2", "Qxc3", "Qc4", "Qd1", "Qd2", "Qd4", "Qd5", 
						"Qxd6", "Qe2", "Qe3", "Qe4", "Qxf5", "Ra2", "Ra3", "Rd1", "Re1", "Bd2", "Be3", 
						"Bf4", "Bg5", "Bh6", "Na3", "Nxc3", "Nbd2", "Nfd2", "Nd4", "Ne1", "Ng5", "Nh4", 
						"a5", "axb5", "b3", "b4", "bxc3", "exd6", "e6", "g3", "g4", "h3", "h4"), 
				"WHITE\nr---kbnr\npp-qp-pp\n--np----\n-b--Pp--\nP-------\n--pQ-N--\n-P---PPP\nRNB--RK-\n");
		lastMoveWas("Nc6", 18);
	}
	
	@Test
	public void testMove19() throws IllegalMoveException {
		moveCheck(boards[19], 
				makeGoal(boards[19], "O-O-O", "Kd8", "Kf7", "Qc7", "Qc8", "Qd8", "Qe6", "Rb8", "Rc8", 
						"Rd8", "Na5", "Nb4", "Nb8", "Nd4", "Nd8", "Nxe5", "Nf6", "Nh6", "a5", "a6", 
						"b6", "cxb2", "c2", "d5", "dxe5", "e6", "f4", "g5", "g6", "h5", "h6"), 
				"BLACK\nr---kbnr\npp-qp-pp\n--np----\n-P--Pp--\n--------\n--pQ-N--\n-P---PPP\nRNB--RK-\n");
		lastMoveWas("axb5", 19);
	}
	
	@Test
	public void testMove20() throws IllegalMoveException {
		moveCheck(boards[20], 
				makeGoal(boards[20], "Kh1", "Qa3", "Qb3", "Qc2", "Qc3", "Qc4", "Qd1", "Qd2", "Qd4", 
						"Qd5", "Qxd6", "Qe2", "Qe3", "Qe4", "Qxf5", "Ra2", "Ra3", "Ra4", "Ra5", "Ra6", 
						"Rxa7", "Rd1", "Re1", "Bxb2", "Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Na3", "Nc3", 
						"Nbd2", "Nfd2", "Nd4", "Ne1", "Ng5", "Nh4", "b6", "bxc6", "exd6", "e6", "g3", 
						"g4", "h3", "h4"), 
				"WHITE\nr---kbnr\npp-qp-pp\n--np----\n-P--Pp--\n--------\n---Q-N--\n-p---PPP\nRNB--RK-\n");
		lastMoveWas("cxb2", 20);
	}
	
	@Test
	public void testMove21() throws IllegalMoveException {
		moveCheck(boards[21],
				makeGoal(boards[21], "O-O-O", "Kd8", "Kf7", "Qc7", "Qc8", "Qd8", "Qe6", "Rb8", "Rc8", 
						"Rd8", "Na5", "Nb4", "Nb8", "Nd4", "Nd8", "Nxe5", "Nf6", "Nh6", "a5", "a6", 
						"bxa1=Q", "b1=Q", "bxc1=Q", "b6", "d5", "dxe5", "e6", "f4", "g5", "g6", "h5", 
						"h6"), 
				"BLACK\nr---kbnr\npp-qp-pp\n--np----\n-P--Pp--\n--------\n--NQ-N--\n-p---PPP\nR-B--RK-\n");
		lastMoveWas("Nc3", 21);
	}
	
	@Test
	public void testMove22() throws IllegalMoveException {
		moveCheck(boards[22],
				makeGoal(boards[22], "Kh1", "Qb1", "Qc2", "Qc4", "Qd1", "Qd2", "Qd4", "Qd5", "Qxd6", 
						"Qe2", "Qe3", "Qe4", "Qxf5", "Rd1", "Re1", "Ba3", "Bb2", "Bd2", "Be3", "Bf4", 
						"Bg5", "Bh6", "Na2", "Na4", "Nb1", "Nd1", "Nd5", "Ne2", "Ne4", "Nd2", "Nd4", 
						"Ne1", "Ng5", "Nh4", "b6", "bxc6", "exd6", "e6", "g3", "g4", "h3", "h4"), 
				"WHITE\nr---kbnr\npp-qp-pp\n--np----\n-P--Pp--\n--------\n--NQ-N--\n-----PPP\nq-B--RK-\n");
		lastMoveWas("bxa1=Q", 22);
	}
	
	@Test
	public void testMove23() throws IllegalMoveException {
		moveCheck(boards[23],
				makeGoal(boards[23], "O-O-O", "Kd8", "Qa2", "Qa3", "Qa4", "Qa5", "Qa6", "Qb1", "Qb2", 
						"Qxc1", "Qxc3", "Qc7", "Qc8", "Qd8", "Qe6", "Qxf5", "Rb8", "Rc8", "Rd8", "Na5", 
						"Nb4", "Nb8", "Nd4", "Nd8", "Nxe5", "Nf6", "Nh6", "a5", "a6", "b6", "d5", 
						"dxe5", "e6", "g5", "g6", "h5", "h6"), 
				"BLACK\nr---kbnr\npp-qp-pp\n--np----\n-P--PQ--\n--------\n--N--N--\n-----PPP\nq-B--RK-\n");
		lastMoveWas("Qxf5", 23);
	}
	
	@Test
	public void testMove24() throws IllegalMoveException {
		moveCheck(boards[24],
				makeGoal(boards[24], "Kh1", "Qb1", "Qc2", "Qd3", "Qxd7+", "Qe4", "Qe6", "Qf4", "Qf6", 
						"Qf7+", "Qxf8+", "Qg4", "Qg5", "Qg6+", "Qh3", "Qh5+", "Qxh7", "Rd1", "Re1", 
						"Ba3", "Bb2", "Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Na2", "Na4", "Nb1", "Nd1", 
						"Nd5", "Ne2", "Ne4", "Nd2", "Nd4", "Ne1", "Ng5", "Nh4", "bxa6", "b6", "bxc6", 
						"exd6", "e6", "g3", "g4", "h3", "h4"), 
				"WHITE\nr---kbnr\n-p-qp-pp\np-np----\n-P--PQ--\n--------\n--N--N--\n-----PPP\nq-B--RK-\n");
		lastMoveWas("a6", 24);
	}
	
	@Test
	public void testMove25() throws IllegalMoveException {
		moveCheck(boards[25],
				makeGoal(boards[25],"Kd8", "g6"),
				"BLACK\nr---kbnr\n-p-qp-pp\np-np----\n-P--P--Q\n--------\n--N--N--\n-----PPP\nq-B--RK-\n");
		lastMoveWas("Qh5+", 25);
	}
	
	@Test
	public void testMove26() throws IllegalMoveException {
		moveCheck(boards[26],
				makeGoal(boards[26], "Kh1", "Qf5", "Qg4", "Qg5", "Qxg6+", "Qh3", "Qh4", "Qh6", "Qxh7", 
						"Rd1", "Re1", "Ba3", "Bb2", "Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Na2", "Na4", 
						"Nb1", "Nd1", "Nd5", "Ne2", "Ne4", "Nd2", "Nd4", "Ne1", "Ng5", "Nh4", "bxa6", 
						"b6", "bxc6", "exd6", "e6", "g3", "g4", "h3", "h4"), 
				"WHITE\nr---kbnr\n-p-qp--p\np-np--p-\n-P--P--Q\n--------\n--N--N--\n-----PPP\nq-B--RK-\n");
		lastMoveWas("g6", 26);
	}
	
	@Test
	public void testMove27() throws IllegalMoveException {
		moveCheck(boards[27],
				makeGoal(boards[27], "O-O-O", "Kd8", "Kf7", "Qa2", "Qa3", "Qa4", "Qa5", "Qb1", "Qb2", 
						"Qxc1", "Qxc3", "Qc7", "Qc8", "Qd8", "Qe6", "Qf5", "Qg4", "Qxh3", "Ra7", 
						"Rb8", "Rc8", "Rd8", "Bg7", "Bh6", "Na5", "Na7", "Nb4", "Nb8", "Nd4", "Nd8", 
						"Nxe5", "Nf6", "Nh6", "a5", "axb5", "b6", "d5", "dxe5", "e6", "gxh5", "h6"), 
				"BLACK\nr---kbnr\n-p-qp--p\np-np--p-\n-P--P--Q\n--------\n--N--N-P\n-----PP-\nq-B--RK-\n");
		lastMoveWas("h3", 27);
	}
	
	@Test
	public void testMove28() throws IllegalMoveException {
		moveCheck(boards[28], 
				makeGoal(boards[28], "Kh1", "Kh2", "Qf5", "Qg4", "Qg5", "Qxg6", "Qh4", "Qh6", "Qxh7", 
						"Rd1", "Re1", "Ba3", "Bb2", "Bd2", "Be3", "Bf4", "Bg5", "Bh6", "Na2", "Na4", 
						"Nb1", "Nd1", "Nd5", "Ne2", "Ne4", "Nd2", "Nd4", "Ne1", "Ng5", "Nh2", "Nh4", 
						"bxa6", "b6", "bxc6", "exd6", "e6", "g3", "g4", "h4"), 
				"WHITE\n--kr-bnr\n-p-qp--p\np-np--p-\n-P--P--Q\n--------\n--N--N-P\n-----PP-\nq-B--RK-\n");
		lastMoveWas("O-O-O", 28);
	}
	
	@Test
	public void foolsMate() throws IllegalMoveException {
		Chessboard board = Algebraic.from("f4", "e5", "g4", "Qh4#");
		assertTrue(board.isCheckmate());
		assertEquals("WHITE\nrnb-kbnr\npppp-ppp\n--------\n----p---\n-----PPq\n--------\nPPPPP--P\nRNBQKBNR\n", board.toString());
		
		Chessboard board2 = Algebraic.from("f4", "e5", "g4", "Qh4");
		assertEquals(board2, board);
	}
	
	@Test
	public void game2() throws IllegalMoveException {
		Chessboard board = Algebraic.from("e4", "g6", "d4", "e6", "Nf3", "Bb4+", "c3", "Ba5");
		System.out.println(board);
		assertEquals("WHITE\nrnbqk-nr\npppp-p-p\n----p-p-\nb-------\n---PP---\n--P--N--\nPP---PPP\nRNBQKB-R\n", board.toString());
		assertTrue(board.getLegalMoves().contains(new Move("WHITE_PAWN_b2_b3")));
		assertTrue(board.getLegalMoves().contains(new Move("WHITE_PAWN_b2_b4")));
	}
	
	@Test
	public void game3() throws IllegalMoveException {
		Chessboard board = Algebraic.from("e4", "d6", "d4", "Qd7", "Nc3", "Qe6", "Nf3", "Bd7", "b3", "Qg4", "h3", "Qg6", "Bc4", "e6", "O-O", "Be7");
		System.out.println(board);
	}
	
	@Test
	public void game4() throws IllegalMoveException {
		Chessboard board = Algebraic.from("e4", "Nc6", "d4", "Nb4", "Nf3", "Nxc2+");
		assertTrue(board.getLegalMoves().contains(new Move("WHITE_QUEEN_d1_c2xc2")));
		System.out.println(board);
		System.out.println(Algebraic.encodeAllMoves(board));
		board = Algebraic.successor(board, "Qxc2");
	}
	
	@Test
	public void scholarsMate() throws IllegalMoveException {
		Chessboard board = Algebraic.from("e3", "e5", "Qh5", "Nf6", "Bc4", "Ne4", "Qxf7#");
		assertTrue(board.isCheckmate());
	}
	
	@Test
	public void protectedByPawn() throws IllegalMoveException {
		Chessboard board = Algebraic.from("e4", "e5", "d4", "exd4", "Qxd4", "Qh4", "e5", 
				"Nc6", "e6", "b6", "Nc3", "Bb7", "Qxd7#");
		assertTrue(board.isCheckmate());
	}
	
	@Test
	public void movedIntoCheck() throws IllegalMoveException {
		Chessboard board = Algebraic.from("Na3", "d6", "h3", "Bd7", "Nb1", "Bb5", "d3", "h5",
				"f3", "g5", "Kd2", "g4", "c4", "Bh6+", "e3", "gxf3", "cxb5", "fxg2", "Bxg2", "Qc8",
				"Qxh5", "Kd7", "Qxf7", "Bxe3+", "Kxe3", "Rxh3+", "Rxh3", "Nf6", "Ke2", "c6",
				"bxc6+", "bxc6", "Bg5", "Nh7", "Rxh7", "Na6", "Qxe7#");
		assertTrue(board.isCheckmate());
	}
	
	public void moveCheck(Chessboard board, Set<Move> goal, String boardGoal) {
		System.out.println(sourceString(board.toString()));
		System.out.println(board);
		Set<Move> moves = new TreeSet<Move>(board.getLegalMoves());
		if (!moves.containsAll(goal)) {
			System.out.print("Goal, not move:");
			printMoves(diffs(goal, moves));
		}
		if (!goal.containsAll(moves)) {
			System.out.print("Move, not goal:");
			printMoves(diffs(moves, goal));
		}
		assertTrue(moves.containsAll(goal));
		assertTrue(goal.containsAll(moves));
		assertEquals(boardGoal, board.toString());
	}
	
	public void printMovesAlgebraic(Chessboard board, Set<Move> moves) {
		for (Move m: moves) {
			System.out.print("\"" + Algebraic.encode(m, board) + "\", ");
		}
		System.out.println();
	}
	
	public static String sourceString(String stuff) {
		return '"' + stuff.replace("\n", "\\n") + '"';
	}
}
