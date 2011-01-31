package rationalpiano.test;

import org.junit.Test;

import rationalpiano.consonance.Consonance;

/**
 * A simple Test of the Consonance class
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class ConsonanceTest {

	@Test
	public void testGetNoteDiss() {
		System.out.println("starting...");

		int notestart = 62;
		int notecount = 13;
		int maxfrac = 157;
		double bellWidth = 0.35;
		
		Consonance conso = new Consonance(notestart,notecount,maxfrac,bellWidth);

		//fail("Not yet implemented");
	}

}
