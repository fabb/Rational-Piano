package RationalPiano.Graphic;

import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Manages all screen elements, i.e. the background, the vertical lines (and in the future text and the parameter controls)
 * Provides methods to draw, change and question those objects
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class GraphicControls {
	
	private PApplet papplet;
	//private PFont font;
	
	private int backgroundColorHue;
	private int backgroundColorSaturation;
	private int backgroundColorBrightness;

	private GraphicNoteLineArray graphiclines;
	//private GraphicParameterControl graphicparametercontrols;
	
	private static final Logger logger = Logger.getLogger(GraphicControls.class.getName());

	/**
	 * Initialize the drawing window and generate the controls
	 * @param papplet The processing applet to draw the elements to
	 * @param fullscreen Whether this PApplet runs in fullscreen mode. If true, width and height will get ignored and set to the screen size.
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
	public GraphicControls(PApplet papplet, Boolean fullscreen, int width, int height, float framerate, int notecount, int notestart, double lineBend, int backgroundColorHue, int backgroundColorSaturation, int backgroundColorBrightness, int lineColorHueInactive, int lineColorHueActive, int lineColorSaturation, int lineColorBrightness) {
		this.papplet = papplet;
		setup(fullscreen, width, height, framerate, notecount, notestart, lineBend, backgroundColorHue, backgroundColorSaturation, backgroundColorBrightness, lineColorHueInactive, lineColorHueActive, lineColorSaturation, lineColorBrightness);
	}
	
	/**
	 * Initialize the drawing window and generate the controls
	 * @param fullscreen Whether this PApplet runs in fullscreen mode. If true, width and height will get ignored and set to the screen size. 
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
	private void setup(Boolean fullscreen, int width, int height, float framerate, int notecount, int notestart, double lineBend, int backgroundColorHue, int backgroundColorSaturation, int backgroundColorBrightness, int lineColorHueInactive, int lineColorHueActive, int lineColorSaturation, int lineColorBrightness){
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
		
		graphiclines = new GraphicNoteLineArray(papplet, notecount, notestart, 0, papplet.width, papplet.height/10, papplet.height-papplet.height/10, lineBend, lineColorHueInactive, lineColorHueActive, lineColorSaturation, lineColorBrightness);
		
		//TODO generate parameter controls for live-tweaking parameters
	}

	/**
	 * Draws all contained visual elements and the background
	 * @warning Changes papplet's color mode to HSB.
	 */
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
		
		//TODO draw parameter controls
	}

	/**
	 * Sets the visual strenghtness of the selected line. If there is no line corresponding to key, nothing happens.
	 * @param key Chooses the Line which correspondends to this midi note number, 0<=key<=255
	 * @param width Visual strenghtness to set the selected line to, 0<=width<=1
	 */
	public void setLineWidth(int key, double width) {
		GraphicVerticalLine line = graphiclines.getLine(key);
		if(line != null){
			line.setVolume(width);
		}
	}
	
	/**
	 * Gets the Midi note number corresponding to the line at the x coordinate at_x
	 * @param at_x X-Coordinate to look for a line
	 * @param at_y Y-Coordinate to look for a line
	 * @return Midi note number corresponding to the found line
	 */
	public int getLineNote(int at_x, int at_y){
		return graphiclines.getLineNote(at_x, at_y);
	}
	
	/**
	 * @return The midi note number of the first (leftmost) line
	 */
	public int getFirstLineNote(){
		return graphiclines.getFirstLineNote();
	}
	
	/**
	 * @return The count of lines that are drawn on the screen.
	 */
	public int getLineCount(){
		return graphiclines.getLineCount();
	}

	/**
	 * Sets the specified line active or inactive. This allows to highlight the line according to whether its corresponding note is still held or released.
	 * @param key The midi note which corresponds to a specific line.
	 * @param active True to activate the line, false to deactivate it.
	 */
	public void setLineActive(int key, boolean active) {
		GraphicVerticalLine line = graphiclines.getLine(key);
		
		if(line != null){
			line.setActive(active);	
		}
	}

}
