package chess.ai;

import chess.core.Chessboard;
import chess.core.Move;
import chess.core.PieceColor;

public class AlphaBeta extends Searcher {
    public PieceColor playerColor;

    @Override
    public MoveScore findBestMove(Chessboard board, BoardEval eval, int depth) {
        int parentAlpha = -1;
        int parentBeta = 1;
        playerColor = board.getMoverColor();
        setup(board, eval, depth);
        MoveScore result = evalMoves(board, eval, depth, parentAlpha, parentBeta);
        tearDown();
        return result;
    }

    private MoveScore evalMoves(Chessboard board, BoardEval eval, int depth, int alpha, int beta) {
        MoveScore best = null;
        int a = alpha;
        int b = beta;

        //for each legal move m:
        for (Move m: board.getLegalMoves()) {
            //System.out.println("\n" + m.toString());
            //Generate a successor node (passing down current Alpha-Beta values)
            Chessboard next = generate(board, m);
            MoveScore result = new MoveScore(-evalBoard(next, eval, depth - 1, -b, -a), m);

            if (best == null || result.getScore() > best.getScore()) {
                best = result;
            }
            if (result.getScore() > a) {
                a = result.getScore();
            }
            //If Alpha ≥ Beta, exit the loop.
            if (a >= b) {
                return best;
            }
        }
        System.out.println("Best: " + best.getMove());
        return best;
    }

    private int evalBoard(Chessboard board, BoardEval eval, int depth, int alpha, int beta) {
        if (!board.hasKing(board.getMoverColor()) || board.isCheckmate()) {
            return -eval.maxValue();
        } else if (board.isStalemate()) {
            return 0;
        } else if (depth == 0) {
            return evaluate(board, eval);
        } else {
            return evalMoves(board, eval, depth, alpha, beta).getScore();
        }
    }
}