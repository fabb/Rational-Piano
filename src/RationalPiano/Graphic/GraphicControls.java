package RationalPiano.Graphic;

import java.util.logging.Logger;

import RationalPiano.Persistence.ConfigurationData;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Manages all screen elements, i.e. the background, the vertical lines (and in the future text and the parameter controls)
 * Provides methods to draw, change and question those objects
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-29
 * @version 1.021
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class GraphicControls implements IGraphicControls {
	
	private PApplet papplet;
	//private PFont font;
	
	private int backgroundColorHue;
	private int backgroundColorSaturation;
	private int backgroundColorBrightness;

	private IGraphicVisualizationElementArray graphiclines;
	private IPropertyControl graphicparametercontrols;
	
	private static final Logger logger = Logger.getLogger(GraphicControls.class.getName());

	/**
	 * Initialize the drawing window and generate the controls
	 * @param papplet The processing applet to draw the elements to
	 * @param config The ConfigurationData object which allows tweaking and saving by the contained propery control object
	 * @param fullscreen Whether this PApplet runs in fullscreen mode. If true, width and height will get ignored and set to the screen size.
	 * @param vertical_scaling Use this fraction of the full height for the visualization display; Only applicable if fullscreen==true; 0<=vertical_scaling<=1
	 * @param width width of the window in pixels
	 * @param height height of the window in pixels
	 * @param framerate framerate of the window in frames per second
	 * @param notecount Count of Note Control Elements (Vertical Lines) to display, must be bigger than 0
	 * @param notestart Midi note number of the first note, the following notes get incremented numbers
	 * @param lineBend Nonlnearly transforms the set line widths; 0<lineBend<=1, 1 means volume is translated linearly to the single lines, smaller values mean that smaller volumes get bigger. 
	 * @param backgroundColorHue Hue of the background color of the applet. 0<=backgroundColorHue<=255
	 * @param backgroundColorSaturation Saturation of the background color of the applet. 0<=backgroundColorSaturation<=255
	 * @param backgroundColorBrightness Brightness of the background color of the applet. 0<=backgroundColorBrightness<=255
	 * @param lineColorHueInactive Color hue of the lines that are not active. 0<=lineColorHueInactive<=255
	 * @param lineColorHueActive Color hue of the lines that are active. 0<=lineColorHueActive<=255
	 * @param lineColorSaturation Color saturation of the lines. 0<=lineColorSaturation<=255
	 * @param lineColorBrightness Color brightness of the lines. 0<=lineColorBrightness<=255
	 */
	public GraphicControls(PApplet papplet, ConfigurationData config, Boolean fullscreen, double vertical_scaling, int width, int height, float framerate, int notecount, int notestart, double lineBend, int backgroundColorHue, int backgroundColorSaturation, int backgroundColorBrightness, int lineColorHueInactive, int lineColorHueActive, int lineColorSaturation, int lineColorBrightness) {
		this.papplet = papplet;
		setup(fullscreen, config, vertical_scaling, width, height, framerate, notecount, notestart, lineBend, backgroundColorHue, backgroundColorSaturation, backgroundColorBrightness, lineColorHueInactive, lineColorHueActive, lineColorSaturation, lineColorBrightness);
	}
	
	/**
	 * Initialize the drawing window and generate the controls
	 * @param fullscreen Whether this PApplet runs in fullscreen mode. If true, width and height will get ignored and set to the screen size. 
	 * @param config The ConfigurationData object which allows tweaking and saving by the contained propery control object
	 * @param vertical_scaling If only a smaller height should be used for the visualization display; Only applicable if fullscreen==true
	 * @param width width of the window in pixels
	 * @param height height of the window in pixels
	 * @param framerate framerate of the window in frames per second
	 * @param notecount Count of Note Control Elements (Vertical Lines) to display, must be bigger than 0
	 * @param notestart Midi note number of the first note, the following notes get incremented numbers
	 * @param lineBend Nonlnearly transforms the set line widths; 0<lineBend<=1, 1 means volume is translated linearly to the single lines, smaller values mean that smaller volumes get bigger.
	 * @param backgroundColorHue Hue of the background color of the applet. 0<=backgroundColorHue<=255
	 * @param backgroundColorSaturation Saturation of the background color of the applet. 0<=backgroundColorSaturation<=255
	 * @param backgroundColorBrightness Brightness of the background color of the applet. 0<=backgroundColorBrightness<=255
	 * @param lineColorHueInactive Color hue of the lines that are not active. 0<=lineColorHueInactive<=255
	 * @param lineColorHueActive Color hue of the lines that are active. 0<=lineColorHueActive<=255
	 * @param lineColorSaturation Color saturation of the lines. 0<=lineColorSaturation<=255
	 * @param lineColorBrightness Color brightness of the lines. 0<=lineColorBrightness<=255
	 */
	private void setup(Boolean fullscreen, ConfigurationData config, double vertical_scaling, int width, int height, float framerate, int notecount, int notestart, double lineBend, int backgroundColorHue, int backgroundColorSaturation, int backgroundColorBrightness, int lineColorHueInactive, int lineColorHueActive, int lineColorSaturation, int lineColorBrightness){
		if(fullscreen){
			width = papplet.screenWidth;
			height = papplet.screenHeight;
		}else{
			if(width > papplet.screenWidth - 10){ //TODO find way to set a more reasonable maximum width depending on task bar and window border
				width = papplet.screenWidth - 10;
				logger.warning("Configured applet width was too high, cropped to " + width);
			}
			if(height > papplet.screenHeight - 100){ //TODO find way to set a more reasonable maximum height depending on title bar, task bar and window border
				height = papplet.screenHeight - 100;
				logger.warning("Configured applet height was too high, cropped to " + height);
			}
		}
		
		papplet.size(width, height, PConstants.P2D);
		logger.info("Setting up graphical elements"); //after papplet.size() as otherwise RationalPiano.Run.RationalPiano.setup() would get called again.
		papplet.frameRate(framerate);
		
		this.backgroundColorHue = backgroundColorHue;
		this.backgroundColorSaturation = backgroundColorSaturation;
		this.backgroundColorBrightness = backgroundColorBrightness;
		

		//font = papplet.createFont("SanSerif", 12); //FIXME this takes ages as the system's font folder is being scanned
		
		int linearray_y_top;
		
		if(fullscreen){
			linearray_y_top = (int)((1-vertical_scaling)*papplet.height);
		}else{
			linearray_y_top = 0;
		}
		
		int linearray_y_bottom = papplet.height;
		
		graphiclines = new GraphicNoteLineArray(papplet, notecount, notestart, 0, papplet.width, linearray_y_top, linearray_y_bottom, lineBend, lineColorHueInactive, lineColorHueActive, lineColorSaturation, lineColorBrightness);
		
		graphicparametercontrols = new PropertyControlP5(papplet,config);
	}

	@Override
	public void draw() {
		papplet.colorMode(PConstants.HSB); //be warned: this will change all color uses in the whole PApplet unless a colorMode() call precedes those
		
		
		papplet.background(backgroundColorHue,backgroundColorSaturation,backgroundColorBrightness);
		
		//TODO help text / menu
		/*
		papplet.stroke(255);
		
		papplet.fill(255);
		
		papplet.textFont(font);
		papplet.textAlign(PConstants.LEFT, PConstants.TOP);
		papplet.text("press keys 'a', 's', 'd' 'f' etc. to activate the according notes", 5, 20);
		*/
		
		graphiclines.draw();
		
		graphicparametercontrols.draw();
	}

	@Override
	public IGraphicVisualizationElementArray getGraphicVisualizationElementArray() {
		return graphiclines;
	}

	@Override
	public boolean isMouseOverGraphicVisualizationElementArray() {
		//TODO this is actually not true when the GraphicVisualizationElementArray does not cover the whole screen, but as InputDevs catches this case already, this is good enough and working
		return !(graphicparametercontrols.isMouseOver());
	}

	@Override
	public IPropertyControl getParameterControl() {
		return graphicparametercontrols;
	}
}
