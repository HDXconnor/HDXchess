package chess.hash;

import java.lang.reflect.Array;
import java.util.Random;

// Reference:
//
// A cool and practical alternative to traditional hash tables, U. Erlingsson, M. Manasse, F. Mcsherry, 2006.
// In 7th Workshop on Distributed Data and Structures (WDAS'06)
// http://www.ru.is/faculty/ulfar/CuckooHash.pdf

abstract public class CuckooHash<K,V> {
	private Entry[] table;
	private XORShiftRandom random;
	private int M, numFuncs, numBuckets, size;
	private static final int cFail = 8;
	private int tableSectorSize;
	
	class Entry {
		K key;
		V value;
		int bucket;
		int funcIndex;
		
		Entry(K key, V value) {this.key = key; this.value = value; funcIndex = bucket = 0;}
		
		int hash() {return getHash(funcIndex, key) + bucket;}
		
		void bump() {
			bucket += 1;
			if (bucket == numBuckets) {
				bucket = 0;
				funcIndex = (funcIndex + 1) % numFuncs;
			}
		}
	}
	
	public CuckooHash(int sizeExp, int numHashFunctions, int numBuckets) {
		if (sizeExp > 30) {throw new IllegalArgumentException("Maximum size exponent is 30.");}
		numFuncs = numHashFunctions;
		this.numBuckets = numBuckets;
		size = 0;
		random = new XORShiftRandom(1);
		makeTable(sizeExp);
	}
	
	@SuppressWarnings("unchecked")
	void makeTable(int sizeExp) {
		M = sizeExp;
		tableSectorSize = capacity() / numFuncs;
		table = (Entry[])(Array.newInstance(Entry.class, capacity()));
	}
	
	abstract int evalKey(int func, K key);
	
	private int getHash(int func, K key) {
		return func * tableSectorSize + numBuckets * evalKey(func, key);
	}
	
	public long nextLong() {return random.nextLong();}
	
	protected Random getRandom() {return random;}
	
	public static int log2(int n) {
		int log = 0;
		while (n > 1) {
			n >>>= 1;
			log += 1;
		}
		return log;
	}
	
	public int size() {return size;}
	
	public int capacity() {return 1 << M;}
	
	public int log2Capacity() {return M;}
	
	public int maxHashExp() {return M - log2(getNumBuckets()*getNumHashFuncs());}
	
	public void put(K key, V value) {
		tryPut(new Entry(key, value));
	}
	
	boolean tryPut(Entry addMe) {
		int countdown = M * cFail;
		while (countdown > 0) {
			int spot = addMe.hash();
			if (table[spot] == null || table[spot].key.equals(addMe.key)) {
				table[spot] = addMe;
				size += 1;
				return true;
			} else {
				addMe = bump(spot, addMe);
				countdown -= 1;
			}
		}
		
		return false;
	}
	
	private Entry bump(int spot, Entry replacer) {
		Entry homeless = table[spot];
		table[spot] = replacer;
		homeless.bump();
		return homeless;
	}
	
	public boolean containsKey(K key) {
		return get(key) != null;
	}
	
	public V get(K key) {
		for (int i = 0; i < numFuncs; ++i) {
			int spotBase = getHash(i, key);
			for (int j = 0; j < numBuckets; ++j) {
				int spot = spotBase + j;
				if (table[spot] != null && table[spot].key.equals(key)) {
					return table[spot].value;
				}
			}
		}
		return null;
	}
	
	public int getNumBuckets() {return numBuckets;}
	public int getNumHashFuncs() {return numFuncs;}
}
