package RationalPiano.Graphic;

/**
 * Provides methods to question all screen elements
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IGraphicControlsPassive {

	/**
	 * Gets the Midi note number corresponding to the graphic element at the x coordinate at_x and the y coordinate at_y.
	 * @param at_x X-Coordinate to look for an element
	 * @param at_y Y-Coordinate to look for an element
	 * @return Midi note number corresponding to the found element
	 */
	public abstract int getElementNote(int at_x, int at_y);

	/**
	 * @return The lowest Midi note number of the elements
	 */
	public abstract int getLowestNote();

	/**
	 * @return The highest Midi note number of the elements
	 */
	public abstract int getHighestNote();

}