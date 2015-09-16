package chess.hash;

import java.util.Random;

// Reference: http://www.jstatsoft.org/v08/i14/

/*
@article{Marsaglia:2003:JSSOBK:v08i14,
  author =	"George Marsaglia",
  title =	"Xorshift RNGs",
  journal =	"Journal of Statistical Software",
  volume =	"8",
  number =	"14",
  pages =	"1--6",
  day =  	"4",
  month =	"7",
  year = 	"2003",
  CODEN =	"JSSOBK",
  ISSN = 	"1548-7660",
  bibdate =	"2003-07-04",
  URL =  	"http://www.jstatsoft.org/v08/i14",
  accepted =	"2003-07-04",
  acknowledgement = "",
  keywords =	"",
  submitted =	"2003-05-06",
}

The idea (and part of the implementation) for subclassing comes from:
http://www.javamex.com/tutorials/random_numbers/java_util_random_subclassing.shtml
 */

@SuppressWarnings("serial")
public class XORShiftRandom extends Random {
	private long seed;
	
	public XORShiftRandom() {
		this(System.nanoTime());
	}
	
	public XORShiftRandom(long seed) {
		this.seed = seed;
	}
	
	@Override
	protected int next(int nBits) {
		return (int)(nextLong() & ((1L << nBits) - 1));
	}

	@Override
	public long nextLong() {
		seed ^= (seed << 21);
		seed ^= (seed >>> 35);
		seed ^= (seed << 4);
		return seed;
	}
}
