package RationalPiano.Graphic;

/**
 * Manages all screen elements, i.e. the background, the vertical lines (and in the future text and the parameter controls)
 * Provides methods to draw, change and question those objects
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IGraphicControls extends IGraphicControlsPassive, IDrawable {

	/**
	 * Sets the visual strenghtness of the selected element. If there is no element corresponding to key, nothing happens.
	 * @param key Chooses the element(s) which correspondends to this midi note number, 0<=key<=255
	 * @param width Visual strenghtness to set the selected element to, 0<=width<=1
	 */
	public abstract void setElementWidth(int key, double width);

	/**
	 * Sets the specified element active or inactive. This allows to highlight the element according to whether its corresponding note is still held or released.
	 * @param key The midi note which corresponds to a specific element.
	 * @param active True to activate the element, false to deactivate it.
	 */
	public abstract void setElementActive(int key, boolean active);

}