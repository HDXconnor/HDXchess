package chess.core;

import java.util.*;

import chess.hash.CuckooHashLong;
import chess.hash.CuckooUMAS;

class MoveGenerator {
	private EnumMap<ChessPiece,EnumMap<BoardSquare,BitBoard>> moves;
	private EnumMap<ChessPiece,EnumMap<BoardSquare,BitBoard>> masks;
	private EnumMap<ChessPiece,EnumMap<BoardSquare,CuckooHashLong<BitBoard,BitBoard>>> slides;
	private EnumMap<ChessPiece,MoveDir[]> pieceDirs;
	private EnumMap<BoardSquare,EnumMap<BoardSquare,EnumSet<BoardSquare>>> between;
	private EnumMap<BoardSquare,EnumMap<BoardSquare,BitBoard>> rayMasksBetween;
	private EnumMap<PieceColor,EnumMap<BoardSquare,BitBoard>> cancelledPawnMoves;
	private EnumMap<PieceColor,EnumMap<BoardSquare,BitBoard>> cancelledPawnEast;
	private EnumMap<PieceColor,EnumMap<BoardSquare,BitBoard>> cancelledPawnWest;
	private EnumMap<PieceColor,EnumMap<MoveDir,BoardSquare>> castleRookStarts;
	
	final static MoveDir[] rookDirs = new MoveDir[]{MoveDir.N, MoveDir.E, MoveDir.S, MoveDir.W};
	final static MoveDir[] bishopDirs = new MoveDir[]{MoveDir.NW, MoveDir.NE, MoveDir.SE, MoveDir.SW};
	
	public MoveGenerator() {
		makePieceDirs();
		makeMovesMasks();
		makeSlides();
		makeBetweens();
		makeCancelledPawns();
		makeCastleRookStarts();
	}
	
	public BitBoard retrieveMovesFor(BoardSquare src, ChessPiece type, BitBoard allPieces) {
		if (type == ChessPiece.ROOK) {
			return new BitBoard(retrieveSlidingMovesFor(ChessPiece.ROOK, src, allPieces));
		} else if (type == ChessPiece.BISHOP) {
			return new BitBoard(retrieveSlidingMovesFor(ChessPiece.BISHOP, src, allPieces));
		} else if (type == ChessPiece.QUEEN) {
			BitBoard fromRook = retrieveSlidingMovesFor(ChessPiece.ROOK, src, allPieces);
			BitBoard fromBishop = retrieveSlidingMovesFor(ChessPiece.BISHOP, src, allPieces);
			return fromRook.union(fromBishop);
		} else {
			return new BitBoard(moves.get(type).get(src));
		}
	}
	
	public boolean between(BoardSquare start, BoardSquare end, BoardSquare candidate) {
		return between.get(start).get(end).contains(candidate);
	}
	
	public BitBoard rayMaskBetween(BoardSquare start, BoardSquare end) {
		return new BitBoard(rayMasksBetween.get(start).get(end));
	}
	
	public BitBoard cancelledPawnMoves(PieceColor color, BoardSquare pawnAt) {
		return new BitBoard(cancelledPawnMoves.get(color).get(pawnAt));
	}
	
	public BitBoard cancelledPawnWest(PieceColor color, BoardSquare pawnAt, BoardSquare pinner) {
		BitBoard result = new BitBoard(cancelledPawnWest.get(color).get(pawnAt));
		result.set(pinner);
		return result;
	}
	
	public BitBoard cancelledPawnEast(PieceColor color, BoardSquare pawnAt, BoardSquare pinner) {
		BitBoard result = new BitBoard(cancelledPawnEast.get(color).get(pawnAt));
		result.set(pinner);
		return result;
	}
	
	public BoardSquare getCastleRook(PieceColor color, MoveDir dir) {
		return castleRookStarts.get(color).get(dir);
	}
	
	private void makePieceDirs() {
		pieceDirs = new EnumMap<ChessPiece,MoveDir[]>(ChessPiece.class);
		pieceDirs.put(ChessPiece.BISHOP, bishopDirs);
		pieceDirs.put(ChessPiece.ROOK, rookDirs);
	}
	
	private void makeMovesMasks() {
		moves = new EnumMap<ChessPiece,EnumMap<BoardSquare,BitBoard>>(ChessPiece.class);
		moves.put(ChessPiece.KNIGHT, new EnumMap<BoardSquare,BitBoard>(BoardSquare.class));
		moves.put(ChessPiece.KING, new EnumMap<BoardSquare,BitBoard>(BoardSquare.class));

		masks = new EnumMap<ChessPiece,EnumMap<BoardSquare,BitBoard>>(ChessPiece.class);
		masks.put(ChessPiece.ROOK, new EnumMap<BoardSquare,BitBoard>(BoardSquare.class));
		masks.put(ChessPiece.BISHOP, new EnumMap<BoardSquare,BitBoard>(BoardSquare.class));
		
		for (BoardSquare bs: BoardSquare.values()) {
			moves.get(ChessPiece.KNIGHT).put(bs, BitBoard.makeKnightMoves(bs));
			moves.get(ChessPiece.KING).put(bs, BitBoard.makeKingMoves(bs));
			
			BitBoard rook = BitBoard.makeRookMoves(bs);
			if (bs.onEdge()) {
				masks.get(ChessPiece.ROOK).put(bs, rook);
			} else {
				masks.get(ChessPiece.ROOK).put(bs, rook.intersection(BitBoard.INTERIOR_SQUARES));
			}

			BitBoard bishop = BitBoard.makeBishopMoves(bs);
			masks.get(ChessPiece.BISHOP).put(bs, bishop.intersection(BitBoard.INTERIOR_SQUARES));
		}		
	}
	
	private void makeSlides() {
		slides = new EnumMap<ChessPiece,EnumMap<BoardSquare,CuckooHashLong<BitBoard,BitBoard>>>(ChessPiece.class);
		slides.put(ChessPiece.ROOK, makeSlides(ChessPiece.ROOK));
		slides.put(ChessPiece.BISHOP, makeSlides(ChessPiece.BISHOP));		
	}
	
	private void makeBetweens() {
		rayMasksBetween = new EnumMap<BoardSquare,EnumMap<BoardSquare,BitBoard>>(BoardSquare.class);
		between = new EnumMap<BoardSquare,EnumMap<BoardSquare,EnumSet<BoardSquare>>>(BoardSquare.class);
		for (BoardSquare start: BoardSquare.values()) {
			EnumMap<BoardSquare,EnumSet<BoardSquare>> inner = new EnumMap<BoardSquare,EnumSet<BoardSquare>>(BoardSquare.class);
			EnumMap<BoardSquare,BitBoard> rays = new EnumMap<BoardSquare,BitBoard>(BoardSquare.class);
			for (BoardSquare end: BoardSquare.values()) {
				MoveDir vector = MoveDir.between(start, end);
				EnumSet<BoardSquare> found = EnumSet.noneOf(BoardSquare.class);
				BitBoard rayMask = new BitBoard();
				rayMask.set(start);
				if (vector != MoveDir.NONE) {
					for (BoardSquare candidate: BoardSquare.values()) {
						if (MoveDir.between(start, candidate) == vector && MoveDir.between(candidate, end) == vector) {
							found.add(candidate);
						}
					}
					
					BoardSquare rayAt = start;
					while (rayAt != end) {
						rayAt = rayAt.successor(vector);
						rayMask.set(rayAt);
					}
				}
				rays.put(end, rayMask);
				inner.put(end, found);
			}
			between.put(start, inner);
			rayMasksBetween.put(start, rays);
		}
	}
	
	private void makeCancelledPawns() {
		cancelledPawnMoves = new EnumMap<PieceColor,EnumMap<BoardSquare,BitBoard>>(PieceColor.class);
		cancelledPawnEast = new EnumMap<PieceColor,EnumMap<BoardSquare,BitBoard>>(PieceColor.class);
		cancelledPawnWest = new EnumMap<PieceColor,EnumMap<BoardSquare,BitBoard>>(PieceColor.class);
		for (PieceColor color: PieceColor.values()) {
			EnumMap<BoardSquare,BitBoard> colorCancelled = new EnumMap<BoardSquare,BitBoard>(BoardSquare.class);
			EnumMap<BoardSquare,BitBoard> colorEast = new EnumMap<BoardSquare,BitBoard>(BoardSquare.class);
			EnumMap<BoardSquare,BitBoard> colorWest = new EnumMap<BoardSquare,BitBoard>(BoardSquare.class);
			for (BoardSquare s: BoardSquare.values()) {
				BitBoard cancelled = new BitBoard().negation();
				BitBoard cancelEast = new BitBoard().negation();
				BitBoard cancelWest = new BitBoard().negation();
				if (!s.pawnImpossible()) {
					BoardSquare advance = s.pawnAdvanceFrom(color);
					cancelled.clear(advance);
					if (s.pawnStart(color)) {
						cancelled.clear(advance.pawnAdvanceFrom(color));
					}
					if (s.hasPawnEast()) {cancelEast.clear(s.pawnCaptureEast(color));}
					if (s.hasPawnWest()) {cancelWest.clear(s.pawnCaptureWest(color));}
				}
				colorCancelled.put(s, cancelled);
				colorEast.put(s, cancelEast);
				colorWest.put(s, cancelWest);
			}
			cancelledPawnMoves.put(color, colorCancelled);
			cancelledPawnEast.put(color, colorEast);
			cancelledPawnWest.put(color, colorWest);
		}
	}
	
	private EnumMap<BoardSquare,CuckooHashLong<BitBoard,BitBoard>> makeSlides(ChessPiece type) {
		EnumMap<BoardSquare,BitBoard> maskMap = masks.get(type);
		EnumMap<BoardSquare,CuckooHashLong<BitBoard,BitBoard>> result = new EnumMap<BoardSquare,CuckooHashLong<BitBoard,BitBoard>>(BoardSquare.class);
		for (BoardSquare s: BoardSquare.values()) {
			ArrayList<BitBoard> combos = allCombosOf(maskMap.get(s));
			int sizeExp = CuckooHashLong.log2(combos.size());
			CuckooHashLong<BitBoard,BitBoard> hashed = new CuckooUMAS<BitBoard,BitBoard>(sizeExp, 2, 4);
			for (BitBoard pattern: combos) {
				hashed.put(pattern, legalMarchFor(s, pieceDirs.get(type), pattern));
			}
			result.put(s, hashed);
		}
		return result;
	}
	
	static BitBoard legalMarchFor(BoardSquare start, MoveDir[] dirs, BitBoard pattern) {
		BitBoard legal = new BitBoard();
		for (MoveDir dir: dirs) {
			march(start, dir, pattern, legal);
		}
		return legal;
	}
	
	static void march(BoardSquare start, MoveDir dir, BitBoard occupancy, BitBoard result) {
		BoardSquare where = start;
		while (where.hasSuccessor(dir)) {
			where = where.successor(dir);
			result.set(where);
			if (occupancy.isSet(where)) {
				return;
			}
		}
	}
	
	static ArrayList<BitBoard> allCombosOf(BitBoard mask) {
		if (mask.numPieces() > 16) {
			System.out.println("mask: " + mask);
			throw new IllegalArgumentException(mask.numPieces() + "; too big!");
		}
		ArrayList<BitBoard> combos = new ArrayList<BitBoard>();
		ArrayList<BoardSquare> candidates = new ArrayList<BoardSquare>();
		for (BoardSquare c: mask) {candidates.add(c);}
		allCombosOf(mask, candidates, 0, combos);
		return combos;
	}
	
	private static void allCombosOf(BitBoard mask, ArrayList<BoardSquare> candidates, int c, ArrayList<BitBoard> combos) {
		if (c < candidates.size()) {
			BitBoard cleared = new BitBoard(mask);
			cleared.clear(candidates.get(c));
			allCombosOf(mask, candidates, c + 1, combos);
			allCombosOf(cleared, candidates, c + 1, combos);
		} else {
			combos.add(mask);
		}
	}
	
	private BitBoard retrieveSlidingMovesFor(ChessPiece type, BoardSquare src, BitBoard allPieces) {
		BitBoard key = allPieces.intersection(masks.get(type).get(src));
		return slides.get(type).get(src).get(key);
	}
	
	private void makeCastleRookStarts() {
		castleRookStarts = new EnumMap<PieceColor,EnumMap<MoveDir,BoardSquare>>(PieceColor.class);
		castleRookStarts.put(PieceColor.WHITE, new EnumMap<MoveDir,BoardSquare>(MoveDir.class));
		castleRookStarts.put(PieceColor.BLACK, new EnumMap<MoveDir,BoardSquare>(MoveDir.class));
		
		castleRookStarts.get(PieceColor.WHITE).put(MoveDir.E, BoardSquare.H1);
		castleRookStarts.get(PieceColor.WHITE).put(MoveDir.W, BoardSquare.A1);
		castleRookStarts.get(PieceColor.BLACK).put(MoveDir.E, BoardSquare.H8);
		castleRookStarts.get(PieceColor.BLACK).put(MoveDir.W, BoardSquare.A8);
	}
}
