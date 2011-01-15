package RationalPiano.Graphic;

/**
 * Interface for a graphic visualization element which can be changed in its appearance. 
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IGraphicVisualizationElement extends IDrawable {

	/**
	 * Sets the graphic strengthness to draw this element with.
	 * @param volume 0<=volume<=1
	 */
	public abstract void setVolume(double volume);

	/**
	 * Sets this element either active or inactive which changes its appearance.
	 * @param active True to set this element active or False to set it inactive.
	 */
	public abstract void setActive(boolean active);

	/**
	 * Draws the element with a shape and color gradient according to its volume.
	 * @warning Changes papplet's color mode to HSB.
	 */
	public abstract void draw();

}