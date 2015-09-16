package chess.ai;


public class Node {
    public float alpha;
    public float beta;

    public Node() {
        alpha = -1;
        beta = 1;
    }

    public Node(float a, float b) {
        alpha = a;
        beta = b;
    }
}
