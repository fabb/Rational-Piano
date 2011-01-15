package RationalPiano.Graphic;

import java.util.ArrayList;
//import java.util.HashMap;

import processing.core.PApplet;

/**
 * Represents an array of several IGraphicVisualizationElement objects with iterated corresponding MIDI note numbers
 * 
 * @author Fabian Ehrentraud
 * @date 2010-10-20
 * @version 1.01
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class GraphicNoteLineArray implements IGraphicVisualizationElementArray {

	private PApplet papplet;
	
	private ArrayList<IGraphicVisualizationElement> lines = new ArrayList<IGraphicVisualizationElement>();
	private int lineCount;
	private int x_min;
	private int x_max;
	private int y_top;
	private int y_bottom;
	private int midi_notestart;
	private int roundOffset;

	/**
	 * Initializes the array of Note Lines
	 * @param papplet The processing applet to draw the note lines to
	 * @param lineCount Count of Note Lines to display
	 * @param midi_notestart The MIDI note number of the leftmost line, the other lines have incremental MIDI note numbers 
	 * @param x_min The left x coordinate relative to the PApplet window to start the drawing of the lines; x_min < x_max
	 * @param x_max The right x coordinate relative to the PApplet window to start the drawing of the lines; x_min < x_max
	 * @param y_top The top y coordinate relative to the PApplet window to start the drawing of the lines; y_top < y_bottom
	 * @param y_bottom The bottom y coordinate relative to the PApplet window to start the drawing of the lines; y_top < y_bottom
	 * @param lineBend Nonlnearly transforms the set line widths; 0<lineBend<=1, 1 means volume is translated linearly to the single lines, smaller values mean that smaller volumes get bigger.
	 * @param lineColorHueInactive Color hue of the lines that are not active. 0<=lineColorHueInactive<=255
	 * @param lineColorHueActive Color hue of the lines that are active. 0<=lineColorHueActive<=255
	 * @param lineColorSaturation Color saturation of the lines. 0<=lineColorSaturation<=255
	 * @param lineColorBrightness Color brightness of the lines. 0<=lineColorBrightness<=255
	 */
	public GraphicNoteLineArray(PApplet papplet, int lineCount, int midi_notestart, int x_min, int x_max, int y_top, int y_bottom, double lineBend, int lineColorHueInactive, int lineColorHueActive, int lineColorSaturation, int lineColorBrightness) {
		this.papplet = papplet;
		this.lineCount = lineCount;
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_top = y_top;
		this.y_bottom = y_bottom;
		this.midi_notestart = midi_notestart;
		
		roundOffset = ((x_max-x_min) - (x_max-x_min)/lineCount*lineCount) / 2; //every line has a width that's a whole number; if the width of the notelinearray is not divisible by the line count, they will get centered 
		int spacing = (x_max-x_min)/(lineCount);
		IGraphicVisualizationElement line;
		for(int i=0; i<lineCount; i++){
			line = new GraphicVerticalLineTriangle2(this.papplet, spacing, y_bottom-y_top, x_min + roundOffset + spacing*(i+1) - spacing/2 , y_top, lineBend, lineColorHueInactive, lineColorHueActive, lineColorSaturation, lineColorBrightness);
			lines.add(line);
		}
	}

	@Override
	public int getElementNote(int at_x, int at_y){
		if(at_x < x_min || at_x > x_max || at_y < y_top || at_y > y_bottom){
			return -1;
		}
		
		int spacing = (x_max-x_min)/(lineCount);
		int i;
		for(i=0; i<lineCount; i++){
			if(x_min + roundOffset + spacing*i > at_x){
				//found according line
				if(i == 0){
					//area left of the first line belongs to the first line too
					i++;
				}
				break;
			}
		}

		return midi_notestart + i-1;
	}

	@Override
	public int getLowestNote(){
		return midi_notestart;
	}

	@Override
	public void draw() {
		for(IGraphicVisualizationElement l : lines){
			l.draw();
		}

	}

	@Override
	public IGraphicVisualizationElement getElement(int midiNoteNumber) {
		if(midiNoteNumber - midi_notestart >= 0 && midiNoteNumber - midi_notestart < lines.size()){
			return lines.get(midiNoteNumber - midi_notestart);
		} else {
			return null;
		}
	}

	@Override
	public int getHighestNote() {
		return midi_notestart + lineCount - 1;
	}

}
