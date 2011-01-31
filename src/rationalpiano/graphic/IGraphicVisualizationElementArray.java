package rationalpiano.graphic;

/**
 * Represents an array of several IGraphicVisualizationElement objects with iterated corresponding MIDI note numbers.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IGraphicVisualizationElementArray extends IDrawable {

	/**
	 * @return The lowest Midi note number of all elements
	 */
	public abstract int getLowestNote();

	/**
	 * @return The highest Midi note number of all elements
	 */
	public abstract int getHighestNote();
	
	/**
	 * @param midiNoteNumber The MIDI note number to return the corresponding element for
	 * @return The IGraphicVisualizationElement corresponding to the specified MIDI note number
	 */
	public abstract IGraphicVisualizationElement getElement(int midiNoteNumber);

	/**
	 * Gets the MIDI note number of the element at the specified coordinate
	 * @param at_x X coordinate
	 * @param at_y Y coordinate
	 * @return MIDI note number of the element at the specified coordinate; -1 in case that the coordinates are out of the area of this visualization array
	 */
	public abstract int getElementNote(int at_x, int at_y);
}
