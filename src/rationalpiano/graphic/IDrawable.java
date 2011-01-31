package rationalpiano.graphic;

/**
 * An element that can be drawn on screen.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)

 */
public interface IDrawable {

	/**
	 * Draws this visual element or, if it is a container, all contained visual elements and the background.
	 * May be called with the framerate.
	 * @warning May change papplet's color mode (eg to HSB).
	 */
	public abstract void draw();

}