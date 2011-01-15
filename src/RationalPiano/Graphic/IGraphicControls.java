package RationalPiano.Graphic;

/**
 * Manages all screen elements, i.e. the background, the vertical lines (and in the future text and the parameter controls)
 * Provides methods to draw and access those objects
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IGraphicControls extends IDrawable {

	/**
	 * @return The GraphicVisualizationElementArray containing the note visualization elements. 
	 */
	public abstract IGraphicVisualizationElementArray getGraphicVisualizationElementArray();

}