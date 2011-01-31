package rationalpiano.consonance;

import java.util.HashMap;

/**
 * Calculates the consonances for a range of notes (on the 12TET scale) given a set of active notes with different volumes.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IConsonance {

	/**
	 * Gets the dissonances for all notes in range [notestart, notestart+notecount-1] for the given set of sounding notes with their according volumes.
	 * @param voicesValues A map with the sounding notes; the Integer key value denotes the midi note number and the Double value denotes the according volume; all notes not in the map are assumed to have volume 0.
	 * @return A map with consonance values between 0 and 1 for all notes in range [notestart, notestart+notecount-1].
	 */
	public abstract HashMap<Integer, Double> calculate(
			HashMap<Integer, Double> voicesValues);

}