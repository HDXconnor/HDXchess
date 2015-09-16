package chess.hash;

public class CuckooUMAS<K extends LongHashable,V> extends CuckooHashLong<K, V> {
	
	public CuckooUMAS(int sizeExp, int numHashFunctions, int numBuckets) {
		super(sizeExp, numHashFunctions, numBuckets);
	}

	@Override
	public HashFunc makeFunc() {
		long a = nextLong() >>> 2;
		if (a % 2 == 0) {a += 1;}
		long b = nextLong() & Integer.MAX_VALUE;
		return new UniversalMultAddShift(a, b, maxHashExp());
	}

}
