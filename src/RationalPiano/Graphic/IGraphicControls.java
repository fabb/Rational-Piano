package RationalPiano.Graphic;

/**
 * Manages all screen elements, i.e. the background, the vertical lines (and in the future text and the parameter controls)
 * Provides methods to draw and access those objects
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-30
 * @version 1.1
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IGraphicControls extends IDrawable {

	/**
	 * @return The GraphicVisualizationElementArray containing the note visualization elements. 
	 */
	public abstract IGraphicVisualizationElementArray getGraphicVisualizationElementArray();
	
	/**
	 * @return true if the mouse is above the GraphicVisualizationElementArray, false if it is not (e.g. above a screen control, even if that is on top of the GraphicVisualizationElementArray)
	 */
	public abstract boolean isMouseOverGraphicVisualizationElementArray();

	/**
	 * @return The IPropertyControl element which allows tweaking of controls on screen.
	 */
	public abstract IPropertyControl getParameterControl();

}