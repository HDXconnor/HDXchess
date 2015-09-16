package chess.hash;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UniversalMultAddShiftTest {
	UniversalMultAddShift func;
	long[] numList = new long[]{-4972683369271453960L, 4755622236989466036L, 1092083446069765248L, 
			4503168388465453601L, -3342383633144681140L, 6836568506072080118L, -2651111998922877327L,
			-5257604778215879520L, 5298954437391079618L, -1544970817896810324L, 
			152667988033140578L, 8128050330563547256L};
	
	@Before
	public void setup() {
		func = new UniversalMultAddShift(0x100000001L, 1, 32);
	}

	@Test
	public void test() {
		assertEquals(2, func.hash(2));
	}

	@Test
	public void nums() {
		for (long num: numList) {
			System.out.println(num + " " + func.hash(num));
		}
	}
}
