package metricspaces.util;

import java.util.Random;

/**
 * Centralises an instance of the random number generator so that one instance can be use
 * and it can be seeded for testing purposes.
 * 
 * @author stewart
 *
 */
public class RandomHelper {
	private static Random random;
	
	private static Random getRandom() {
		if (random == null)
			random = new Random(1);
		
		return random;
	}
	
	/**
	 * Gets a random number between start and end inclusive.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getNextInt(int start, int end) {
		return getRandom().nextInt(end - start + 1) + start;
	}
}
