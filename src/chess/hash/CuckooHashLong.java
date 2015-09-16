package chess.hash;

import java.util.ArrayList;

abstract public class CuckooHashLong<K extends LongHashable,V> extends CuckooHash<K,V> {

	private HashFunc[] funcs;
	private ArrayList<Entry> entries;
	
	public CuckooHashLong(int sizeExp, int numHashFunctions, int numBuckets) {
		super(sizeExp, numHashFunctions, numBuckets);
		entries = new ArrayList<Entry>(capacity());
		funcs = new HashFunc[getNumHashFuncs()];
		makeFuncs();
	}
	
	abstract public HashFunc makeFunc();
	
	private void makeFuncs() {
		for (int i = 0; i < getNumHashFuncs(); ++i) {
			funcs[i] = makeFunc();
		}		
	}
	
	int evalKey(int func, K key) {
		return funcs[func].hash(key.longHashCode());
	}
	
	public void put(K key, V value) {
		Entry addMe = new Entry(key, value);
		entries.add(addMe);
		if (!tryPut(addMe)) {
			int sizeExp = this.log2Capacity();
			while (!tableRebuilt(sizeExp)) {
				sizeExp += 1;
			}
		}
	}
	
	boolean tableRebuilt(int sizeExp) {
		makeTable(sizeExp);
		makeFuncs();
		for (Entry kv: entries) {
			if (!tryPut(kv)) {
				return false;
			}
		}
		return true;
	}
}
