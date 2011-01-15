package RationalPiano.Graphic;

import processing.core.*;

/**
 * Represents a vertical line with a changeable width.
 * The width is drawn with a color transistion.
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class GraphicVerticalLineDefault implements IGraphicVisualizationElement {
	
	private PApplet papplet;
	private int halfwidth;
	private int height;
	private int x; //center of the x value
	private int y; //top of the y value
	private int volume = 0; //min is 0 and max is 255
	private boolean active = false;
	
	private int hueInactive;
	private int hueActive;
	private int saturation;
	private int brightness;
	
	private double bend;

	/**
	 * Generates a new line
	 * @param papplet The processing applet to draw the note line to
	 * @param width Width of the line in pixels
	 * @param height Height of the line in pixels
	 * @param x_center X coordinate of the center of the line
	 * @param y_top Y coordinate of the top of the line
	 * @param bend 0<bend<=1, 1 means volume is translated linearly, smaller values mean that smaller volumes get bigger.
	 * @param lineColorHueInactive Color hue of the lines that are not active. 0<=lineColorHueInactive<=255
	 * @param lineColorHueActive Color hue of the lines that are active. 0<=lineColorHueActive<=255
	 * @param lineColorSaturation Color saturation of the lines. 0<=lineColorSaturation<=255
	 * @param lineColorBrightness Color brightness of the lines. 0<=lineColorBrightness<=255
	 */
	public GraphicVerticalLineDefault(PApplet papplet, int width, int height, int x_center, int y_top, double bend, int lineColorHueInactive, int lineColorHueActive, int lineColorSaturation, int lineColorBrightness) {
		this.papplet = papplet;
		this.halfwidth = (int)Math.ceil(width/2);
		this.height = height;
		this.x = x_center;
		this.y = y_top;
		this.bend = bend;
		this.hueInactive = lineColorHueInactive;
		this.hueActive = lineColorHueActive;
		this.saturation = lineColorSaturation;
		this.brightness = lineColorBrightness;
	}

	@Override
	public void setVolume(double volume){
		if(volume>=0 && volume <=1){
			int v;
			v = (int)(255 * Math.pow(volume, bend));
			//v = (int)(255 * ((Math.log(volume)/Math.log(2)) / 8 + 1));
			this.volume = (v >= 0) ? v : 0;
		}
	}
	
	@Override
	public void setActive(boolean active){
		this.active = active;
	}

	@Override
	public void draw() {
		papplet.colorMode(PConstants.HSB); //be warned: this will change all color uses in the whole PApplet unless a colorMode() call precedes those
		
		//debug
		/*
		papplet.fill(0,255,255);
		papplet.textAlign(PConstants.CENTER, PConstants.TOP);
		papplet.text(String.valueOf(volume), x, y+height+10);
		*/
		
		for (int i = 0; i < halfwidth; i++) {
			setStrokeColor(i);
			papplet.line(x-i, y, x-i, y+height);
			papplet.line(x+i, y, x+i, y+height);
		}
	}
	
	/**
	 * Sets the PApplets stroke color in dependence of the pixel distance from the center of the line
	 * @param distance The absolute distance in x-direction from the center of the line; distance >= 0  
	 */
	private void setStrokeColor(int distance){
		//TODO nicer color display
		
		int alpha = 255;
	
		int minval = 64;
		
		alpha = volume - (255 - minval) * distance/halfwidth + minval;
		
		if(active){
			papplet.stroke(hueActive,saturation,brightness, alpha);
		}else{
			papplet.stroke(hueInactive,saturation,brightness, alpha);
		}
	}
}
