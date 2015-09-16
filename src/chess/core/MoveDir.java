package chess.core;

public enum MoveDir {
	NONE {public int xDelta() {return  0;} public int yDelta() {return  0;}},
	N    {public int xDelta() {return  0;} public int yDelta() {return  1;}}, 
	NW   {public int xDelta() {return -1;} public int yDelta() {return  1;}}, 
	W    {public int xDelta() {return -1;} public int yDelta() {return  0;}}, 
	SW   {public int xDelta() {return -1;} public int yDelta() {return -1;}}, 
	S    {public int xDelta() {return  0;} public int yDelta() {return -1;}}, 
	SE   {public int xDelta() {return  1;} public int yDelta() {return -1;}}, 
	E    {public int xDelta() {return  1;} public int yDelta() {return  0;}}, 
	NE   {public int xDelta() {return  1;} public int yDelta() {return  1;}};
	
	abstract public int xDelta();
	abstract public int yDelta();
	
	public static MoveDir between(BoardSquare start, BoardSquare end) {
		int fileDiff = end.fileNum() - start.fileNum();
		int rankDiff = end.rankNum() - start.rankNum();
		
		if (fileDiff == 0) {
			if (rankDiff > 0) {
				return N;
			} else if (rankDiff < 0) {
				return S;
			}
		}
		
		if (rankDiff == 0) {
			if (fileDiff > 0) {
				return E;
			} else if (fileDiff < 0) {
				return W;
			}
		}
		
		if (fileDiff == rankDiff) {
			if (fileDiff > 0) {
				return NE;
			} else if (fileDiff < 0) {
				return SW;
			}
		}
		
		if (fileDiff == -rankDiff) {
			if (fileDiff > 0) {
				return SE;
			} else if (fileDiff < 0) {
				return NW;
			}
		}
		
		return NONE;
	}
}
