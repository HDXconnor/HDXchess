package chess.ai;

import chess.core.Chessboard;
import chess.core.Move;
import chess.core.PieceColor;

public class AlphaBeta extends Searcher {
    public PieceColor color;

    @Override
    public MoveScore findBestMove(Chessboard board, BoardEval eval, int depth) {
        Node node = new Node();
        color = board.getMoverColor();
        setup(board, eval, depth);
        MoveScore result = evalMoves(board, eval, depth, node);
        tearDown();
        return result;
    }

    private MoveScore evalMoves(Chessboard board, BoardEval eval, int depth, Node node) {
        MoveScore best = null;
        for (Move m: board.getLegalMoves()) {
            Node cur = new Node(node.alpha, node.beta);
            Chessboard next = generate(board, m);

            MoveScore result = new MoveScore(-evalBoard(next, eval, depth - 1, cur), m);

            if (best == null || result.getScore() > best.getScore()) {
                best = result;
            }
            if (next.getMoverColor().equals(color)) {
                if (result.getScore() > cur.alpha) {cur.alpha = result.getScore();}
            }
            else if (result.getScore() < node.beta) {cur.beta = result.getScore();}
            if (cur.alpha >= cur.beta) {return best;}
        }
        //if alpha>beta return best
        return best;
    }

    private int evalBoard(Chessboard board, BoardEval eval, int depth, Node node) {
        if (!board.hasKing(board.getMoverColor()) || board.isCheckmate()) {
            return -eval.maxValue();
        } else if (board.isStalemate()) {
            return 0;
        } else if (depth == 0) {
            return evaluate(board, eval);
        } else {
            return evalMoves(board, eval, depth, node).getScore();
        }
    }
}

/*
Initial values for Alpha and Beta are taken from the parent node (-1, 1 for the starting node).
For each legal move m:

Generate a successor node (passing down current Alpha-Beta values)
If the current player is the protagonist, and successor's value is greater than Alpha, set Alpha to successor.
If the current player is the adversary, and successor's value is less than Beta, set Beta to successor.
If Alpha ≥ Beta, exit the loop.
*/
