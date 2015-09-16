package chess.ai;

import chess.core.Move;

public class MoveScore {
	private Move move;
	private int score;
	private boolean cutoff;
	private double distance;

	public MoveScore(int score, Move m) {
		this.move = m;
		this.score = score;
		this.cutoff = false;
	}
	
	public void setCutoff(boolean cutoff) {this.cutoff = cutoff;}
	public void setDistance(double distance) {this.distance = distance;}
	
	public int getScore() {return score;}
	public Move getMove() {return move;}
	public boolean isCutoff() {return cutoff;}
	public double getDistance() {return distance;}
}
