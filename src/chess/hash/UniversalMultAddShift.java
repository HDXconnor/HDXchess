package chess.hash;

// Obtained from the Universal Hashing wikipedia page.
//
// Original reference (paywalled):
// Woelfel, Philipp (1999). "Efficient Strongly Universal and Optimally Universal Hashing". Mathematical Foundations of Computer Science 1999. LNCS 1672. pp. 262-272. doi:10.1007/3-540-48340-3_24. Retrieved 17 May 2011. 
//
// Reference in German:
// Woelfel, Philipp (2003) Uber die Komplexitat der Multiplikation in eingeschrankten Branchingprogrammmodellen (Ph.D.). Universitat Dortmund. Retrieved 18 September 2012.
//
// Turns out it is NOT a good hash function for Cuckoo, although it works well enough for my purposes:
//
// On risks of using cuckoo hashing with simple universal hash classes (2009)
// by Martin Dietzfelbinger , Ulf Schellbach
// In Proc. 20th ACM/SIAM Symposium on Discrete Algorithms (SODA)
//

public class UniversalMultAddShift implements HashFunc {
	private long a, b;
	private int M;
	final static long B_START = -9223372036854775808L;
	
	public UniversalMultAddShift(long a, long b, int M) {
		if (a % 2 != 1 || a <= 0) {throw new IllegalArgumentException("a must be odd and positive");}
		if (b <= 0 || b >= (B_START >>> M)) {throw new IllegalArgumentException("b must be positive and < 2^(64-M)");}
		if (M > 32 || M < 1) {throw new IllegalArgumentException("Requirement: 0 < M <= 32");}
		this.a = a;
		this.b = b;
		this.M = M;
	}
	
	public int hash(long x) {
		return (int)((a * x + b) >>> (64 - M));
	}
	
	public String toString() {
		return "UniversalHashFunc:M="+M+",a="+a+",b="+b;
	}
}
