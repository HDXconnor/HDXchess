package chess.core;

import java.util.*;

public class MoveMap {
	private Chessboard board;
	private PieceColor mover;
	private BitBoard pawnAdvances, pawnEast, pawnWest, attackMap, defenseMap;
	private EnumMap<BoardSquare,BitBoard> pieceMoves;
	private boolean isValidated;
	final static private ChessPiece[] promotions = new ChessPiece[]{ChessPiece.QUEEN, ChessPiece.ROOK, ChessPiece.BISHOP, ChessPiece.KNIGHT};
	private static MoveGenerator moveMaker = new MoveGenerator();
	
	public MoveMap(Chessboard board, PieceColor mover) {
		this.board = board;
		this.mover = mover;
		pieceMoves = new EnumMap<BoardSquare,BitBoard>(BoardSquare.class);
		attackMap = new BitBoard();
		defenseMap = new BitBoard();
		isValidated = false;
		
		BitBoard friendlyPiecesPresent = board.allPiecesFor(mover);
		BitBoard friendlyPiecesAbsent = friendlyPiecesPresent.negation();
		
		for (ChessPiece type: ChessPiece.values()) {
			if (type != ChessPiece.EMPTY) {
				if (type == ChessPiece.PAWN) {
					addPawns(friendlyPiecesAbsent, friendlyPiecesPresent);
				} else {
					BitBoard piecesAt = board.getAllOf(mover, type);
					for (BoardSquare start: piecesAt) {
						BitBoard movesFor = moveMaker.retrieveMovesFor(start, board.at(start), board.allPieces());
						defenseMap.addAll(movesFor.intersection(friendlyPiecesPresent));
						addMovesFor(start, movesFor.intersection(friendlyPiecesAbsent));
					}
				}
			}
		}
	}
	
	public BitBoard getAttackMap() {
		return new BitBoard(attackMap);
	}
	
	public BitBoard getSafeKingSquares() {
		MoveMap enemy = board.getOpponentMoveMap();
		BitBoard danger = enemy.getAttackMap();
		BitBoard allExceptKing = board.allPieces();
		allExceptKing.clear(board.kingAt(board.getMoverColor()));
		BitBoard enemiesNotAt = board.allPiecesFor(board.getOpponentColor()).negation();
		for (ChessPiece slider: ChessPiece.slidingPieces) {
			BitBoard cpAt = board.getAllOf(board.getOpponentColor(), slider);
			for (BoardSquare start: cpAt) {
				danger.addAll(moveMaker.retrieveMovesFor(start, slider, allExceptKing).intersection(enemiesNotAt));
			}
		}
		return danger.negation().intersection(enemy.getDefenseMap().negation());
	}
	
	public BitBoard getDefenseMap() {
		return new BitBoard(defenseMap);
	}
	
	public int getTotalPossibleMoves() {
		if (!isValidated) {validateVsCheck();}
		int total = pawnAdvances.numPieces() + pawnEast.numPieces() + pawnWest.numPieces();
		for (BoardSquare s: pieceMoves.keySet()) {
			total += pieceMoves.get(s).numPieces();
		}
		return total;
	}
	
	public boolean canAttack(BoardSquare sq) {
		return attackMap.isSet(sq);
	}

	public ArrayList<Move> makeMoveList() {
		if (!isValidated) {validateVsCheck();}
		ArrayList<Move> result = new ArrayList<Move>();
		addPawnCaptures(result, pawnWest, mover == PieceColor.WHITE ? MoveDir.SE : MoveDir.NE);
		addPawnCaptures(result, pawnEast, mover == PieceColor.WHITE ? MoveDir.SW : MoveDir.NW);
		addPieceMoves(result);
		addPawnAdvances(result);
		return result;
	}
	
	private void validateVsCheck() {
		MoveMap enemy = board.getOpponentMoveMap();
		BoardSquare king = board.kingAt(mover);
		BitBoard safe = getSafeKingSquares();
		
		pieceMoves.get(king).retainAll(safe);
		
		if (safe.isSet(king)) {
			purgePinMoves(enemy);
			addCastleMoves(king, safe);
		} else {
			purgeAllExcept(findAttackBlocks(enemy, king));			
		}

		isValidated = true;
	}
	
	BitBoard findAttackBlocks(MoveMap enemy, BoardSquare king) {
		BitBoard attackBlocks = new BitBoard();
		for (BoardSquare pieceAt: enemy.pieceMoves.keySet()) {
			if (enemy.getCapturesFor(pieceAt).isSet(king)) {
				attackBlocks.addAll(moveMaker.rayMaskBetween(pieceAt, king));
			}
		}
		BitBoard kingBoard = board.getAllOf(mover, ChessPiece.KING);
		attackBlocks.addAll(enemy.pawnEast.intersection(kingBoard).pawnCaptureWest(mover));
		attackBlocks.addAll(enemy.pawnWest.intersection(kingBoard).pawnCaptureEast(mover));
		return attackBlocks;
	}
	
	private void purgeAllExcept(BitBoard allowed) {
		for (BoardSquare pieceAt: pieceMoves.keySet()) {
			if (pieceAt != board.kingAt(mover)) {
				pieceMoves.put(pieceAt, pieceMoves.get(pieceAt).intersection(allowed));
			}
		}
		pawnAdvances.retainAll(allowed);
		pawnEast.retainAll(allowed);
		pawnWest.retainAll(allowed);
	}
	
	private void purgePinMoves(MoveMap enemy) {
		for (BoardSquare pieceAt: enemy.pieceMoves.keySet()) {
			for (BoardSquare pin: enemy.getPiecesPinnedBy(pieceAt)) {
				if (pieceMoves.containsKey(pin)) {
					pieceMoves.get(pin).retainAll(moveMaker.rayMaskBetween(pieceAt, pin));
				} else {
					pawnAdvances.retainAll(moveMaker.cancelledPawnMoves(mover, pin));
					pawnEast.retainAll(moveMaker.cancelledPawnEast(mover, pin, pieceAt));
					pawnWest.retainAll(moveMaker.cancelledPawnWest(mover, pin, pieceAt));
				}
			}
		}		
	}
	
	private void addCastleMoves(BoardSquare king, BitBoard safe) {
		if (board.potentialCastleKingside()) {tryAddingCastle(king, safe, MoveDir.E);}
		if (board.potentialCastleQueenside()) {tryAddingCastle(king, safe, MoveDir.W);}
	}

	private void tryAddingCastle(BoardSquare king, BitBoard safe, MoveDir dir) {
		if (king.hasSuccessor(dir)) {
			BoardSquare rookTarget = king.successor(dir);
			if (pieceMoves.get(king).isSet(rookTarget)) {
				BoardSquare rook = moveMaker.getCastleRook(mover, dir);
				if (pieceMoves.containsKey(rook) && pieceMoves.get(rook).isSet(rookTarget)) {
					BoardSquare kingTarget = rookTarget.successor(dir);
					if (safe.isSet(kingTarget) && board.at(kingTarget) == ChessPiece.EMPTY) {
						pieceMoves.get(king).set(kingTarget);
					}
				}
			}
		}
	}
	
	private void addMovesFor(BoardSquare start, BitBoard motionMap) {
		pieceMoves.put(start, motionMap);
		updateCaptures(motionMap);
	}
	
	private void addPawns(BitBoard friendlyPiecesAbsent, BitBoard friendlyPiecesPresent) {
		BitBoard piecesAt = board.getAllOf(mover, ChessPiece.PAWN);
		BitBoard openSquares = board.allPieces().negation();
		BitBoard advance1 = piecesAt.pawnAdvance(mover).intersection(openSquares);
		BitBoard advance2 = advance1.pawnAdvance(mover).intersection(openSquares).intersection(BitBoard.pawnAdvanceRankOnly(mover));
		pawnAdvances = advance1.union(advance2);
		
		pawnEast = piecesAt.pawnCaptureEast(mover);
		pawnWest = piecesAt.pawnCaptureWest(mover);
		BitBoard pawnCaptures = pawnEast.union(pawnWest);
		defenseMap.addAll(pawnCaptures.intersection(friendlyPiecesPresent));
		pawnEast = pawnEast.intersection(friendlyPiecesAbsent);
		pawnWest = pawnWest.intersection(friendlyPiecesAbsent);
		
		updateCaptures(pawnEast);
		updateCaptures(pawnWest);
		
		BitBoard opposingPiecesPresent = board.allPiecesFor(mover.other());
		addEnPassantCheck(board, opposingPiecesPresent);
		pawnEast.retainAll(opposingPiecesPresent);
		pawnWest.retainAll(opposingPiecesPresent);
	}
	
	private void addEnPassantCheck(Chessboard board, BitBoard opposingPiecesPresent) {
		if (board.hasLastMove()) {
			Move prev = board.getLastMove();
			if (prev.getPiece() == ChessPiece.PAWN && prev.getStart().rankDiff(prev.getStop()) == 2) {
				opposingPiecesPresent.set(prev.getStart().pawnAdvanceFrom(prev.getColor()));
			}
		}		
	}
	
	private void updateCaptures(BitBoard motionMap) {
		attackMap.addAll(motionMap);
	}
	
	private void addPawnAdvances(ArrayList<Move> result) {
		for (BoardSquare stop: pawnAdvances) {
			BoardSquare start = stop.pawnAdvanceFrom(board.getOpponentColor());
			if (stop.pawnJumpTarget(mover) && board.at(start) == ChessPiece.EMPTY) {
				start = start.pawnAdvanceFrom(board.getOpponentColor());
			} 
			
			if (stop.pawnEnd(mover)) {
				for (ChessPiece promo: promotions) {
					result.add(new Move(mover, ChessPiece.PAWN, start, stop, promo));
				}
			} else {
				result.add(new Move(mover, ChessPiece.PAWN, start, stop));
			}
		}
	}
	
	private void addPawnCaptures(ArrayList<Move> result, BitBoard src, MoveDir trackback) {
		for (BoardSquare stop: src) {
			BoardSquare start = stop.successor(trackback);
			if (stop.pawnEnd(mover)) {
				for (ChessPiece promo: promotions) {
					result.add(new Move(mover, ChessPiece.PAWN, start, stop, stop, promo));
				}
			} else if (board.at(stop) == ChessPiece.EMPTY) {
				result.add(new Move(mover, ChessPiece.PAWN, start, stop, stop.pawnAdvanceFrom(mover.other())));
			} else {
				result.add(new Move(mover, ChessPiece.PAWN, start, stop, stop));
			}
		}
	}

	private void addPieceMoves(ArrayList<Move> result) {
		ArrayList<Move> nonCaptures = new ArrayList<Move>();
		for (BoardSquare start: pieceMoves.keySet()) {
			for (BoardSquare stop: pieceMoves.get(start)) {
				if (board.at(stop) == ChessPiece.EMPTY) {
					nonCaptures.add(new Move(mover, board.at(start), start, stop));
				} else {
					result.add(new Move(mover, board.at(start), start, stop, stop));
				}
			}
		}		
		for (Move nc: nonCaptures) {result.add(nc);}
	}
	
	private BitBoard getCapturesFor(BoardSquare pieceAt) {
		return pieceMoves.get(pieceAt).intersection(board.allPiecesFor(mover.other()));
	}
	
	private BitBoard getSecondCapturesFor(BoardSquare pieceAt) {
		BitBoard firstCapturesFor = getCapturesFor(pieceAt);
		BitBoard firstPurged = board.allPieces().intersection(firstCapturesFor.negation());
		BitBoard secondMoves = moveMaker.retrieveMovesFor(pieceAt, board.at(pieceAt), firstPurged);
		return secondMoves.intersection(board.allPiecesFor(mover.other()));
	}

	private BitBoard getPiecesPinnedBy(BoardSquare pieceAt) {
		BitBoard pins = new BitBoard();
		if (board.at(pieceAt).castsShadow()) {
			BitBoard seconds = getSecondCapturesFor(pieceAt);
			BoardSquare enemyKing = board.kingAt(mover.other());
			if (seconds.isSet(enemyKing)) {
				for (BoardSquare pinnable: getCapturesFor(pieceAt)) {
					if (moveMaker.between(pieceAt, enemyKing, pinnable)) {
						pins.set(pinnable);
					}
				}
			} 
		}
		return pins;
	}
}
