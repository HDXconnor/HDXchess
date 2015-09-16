package chess.hash;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.*;

public class CuckooHashTest {
	
	class LongKey implements LongHashable {
		long x;
		
		LongKey(long x) {this.x = x;}

		@Override
		public long longHashCode() {return x;}
	}
	
	Random random;
	
	@Before
	public void setup() {
		random = new Random(10);
	}
	
	@Test
	public void test1() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(8, 2, 2);
		basicTest(map, 0.5, 256);
	}

	@Test
	public void test2a() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(8, 2, 2);
		basicTest(map, 0.8, 256);
	}
	
	@Test
	public void test2b() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(8, 2, 2);
		basicTest(map, 0.9, 512);
	}
	
	@Test
	public void test3() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(8, 4, 2);
		basicTest(map, 0.8, 256);
	}

	@Test
	public void test4() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(8, 4, 1);
		basicTest(map, 0.8, 256);
	}

	@Test
	public void test5a() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(8, 4, 1);
		basicTest(map, 0.95, 256);
	}

	@Test
	public void test5b() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(8, 4, 1);
		basicTest(map, 0.98, 512);
	}
	
	@Test
	public void test6() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(10, 4, 4);
		basicTest(map, 0.8, 1024);
	}

	@Test
	public void test7a() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(12, 4, 4);
		basicTest(map, 0.8, 4096);
	}

	@Test
	public void test7b() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(12, 2, 1);
		basicTest(map, 0.8, 8192);
	}

	@Test
	public void test7c() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(12, 2, 2);
		basicTest(map, 0.8, 4096);
	}

	@Test
	public void test8() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(14, 2, 2);
		basicTest(map, 0.8, 16384);
	}

	@Test
	public void test9a() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(15, 4, 4);
		basicTest(map, 0.8, 32768);
	}
	
	@Test
	public void test9b() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(15, 4, 4);
		basicTest(map, 5.5, 262144);
	}
	
	@Test
	public void test10() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(20, 4, 4);
		basicTest(map, 0.8, 1048576);
	}

	@Test
	@Ignore
	public void test11() {
		CuckooHashLong<LongKey,String> map = new CuckooUMAS<LongKey,String>(22, 4, 4);
		basicTest(map, 0.8, 4194304);
	}

	public void basicTest(CuckooHashLong<LongKey,String> map, double load, int expectedCapacity) {
		System.out.println("Capacity: " + map.capacity() + " HashFuncs: " + map.getNumHashFuncs() + " Buckets: " + map.getNumBuckets() + " Load: " + load);
		long time = System.currentTimeMillis();
		ArrayList<LongKey> keys = new ArrayList<LongKey>();
		for (int i = 0; i < map.capacity() * load; ++i) {
			keys.add(new LongKey(random.nextLong()));
		}
		
		for (int i = 0; i < keys.size(); ++i) {
			LongKey key = keys.get(i);
			String v = Long.toString(key.x);
			map.put(key, v);
			assertTrue(map.containsKey(key));
			assertEquals(v, map.get(key));
		}
		
		for (int i = 0; i < keys.size(); ++i) {
			LongKey key = keys.get(i);
			String v = Long.toString(key.x);
			assertEquals(v, map.get(key));
		}
		
		assertEquals(expectedCapacity, map.capacity());
		long duration = System.currentTimeMillis() - time;
		System.out.println(duration + " ms");
	}

	@Test
	public void logTest() {
		assertEquals(1, CuckooHashLong.log2(2));
		assertEquals(2, CuckooHashLong.log2(4));
		assertEquals(6, CuckooHashLong.log2(64));
	}
}
