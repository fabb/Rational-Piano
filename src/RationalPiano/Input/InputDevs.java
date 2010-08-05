package RationalPiano.Input;

import java.util.concurrent.ConcurrentHashMap;

import processing.core.PApplet;
import RationalPiano.Graphic.GraphicControls;
import RationalPiano.VoiceManagement.Voices;

/**
 * Manages Input from Mouse, Keyboard and Multitouch.
 * Translates presses to newVoice() and releases to releaseVoice().
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class InputDevs {
	
	private PApplet papplet;
	private Voices voices;
	private GraphicControls graphiccontrols;
	private TuioInput tuioinput;
	
	private int lastMouseNote;
	
	private ConcurrentHashMap<Integer, Boolean> activeKeys = new ConcurrentHashMap<Integer, Boolean>();

	/**
	 * Initializes the input management and generates a TUIO UDP receiver
	 * @param papplet The processing applet to get the size of for scaling TUIO input
	 * @param voices A Voices object for creating new voices and removing old ones
	 * @param graphiccontrols A GraphicControls object needed for getting the corresponding note at a given coordinate
	 * @param tuioPort The local UDP port the TUIO Listener should listen at
	 */
	public InputDevs(PApplet papplet, Voices voices, GraphicControls graphiccontrols, int tuioPort) {
		this.papplet = papplet;
		this.voices = voices;
		this.graphiccontrols = graphiccontrols;
		this.tuioinput = new TuioInput(papplet, voices, graphiccontrols, tuioPort);
	}

	/**
	 * Hook to be called when a mouse button has been pressed.
	 * Creates a new voice that corresponds with the Line at the press coordinates.
	 * @param mouseX The X-coordinate position in Pixels on the PApplet window where the mouse button has been pressed at. Leftmost pixel = 0.
	 * @param mouseY The Y-coordinate position in Pixels on the PApplet window where the mouse button has been pressed at. Topmost pixel = 0.	 
	 * @param mouseButton An integer representing the mouse button. 37 == left mouse button, 39 == right mouse button, 3 == middle mouse button
	 */
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		//PApplet.println(mouseButton); //debug
		if(lastMouseNote != -1){
			return; //clicked another mouse button while one was still held down
		}
		
		lastMouseNote = graphiccontrols.getLineNote(mouseX,mouseY);
		//lines.getLine(this.mouseX).volume((int)(Math.random()*255));
		
		if(lastMouseNote != -1){
			voices.newVoice(lastMouseNote, 1);
		}
	}

	/**
	 * Hook to be called when a mouse button has been released.
	 * Releases the last via mouse click created voice.
	 * @param mouseX The X-coordinate position in Pixels on the PApplet window where the mouse button has been released at. Leftmost pixel = 0.
	 * @param mouseY The Y-coordinate position in Pixels on the PApplet window where the mouse button has been released at. Topmost pixel = 0.
	 * @param mouseButton An integer representing the mouse button. 37 == left mouse button, 39 == right mouse button, 3 == middle mouse button
	 */
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		//note_last = graphiccontrols.getLineNote(mouseX,mouseY); //not necessary as the last key is released
		
		if(lastMouseNote != -1){
			voices.releaseVoice(lastMouseNote);
		}
		
		lastMouseNote = -1;
	}

	/**
	 * Hook for a pressed key on the keyboard.
	 * Only reacts to this key if its state was released before (to prevent the OS' setting of auto key repeat from taking negative effect).
	 * Creates a new voice that corresponds to that key (see toNote()). 
	 * @param key The keyboard key's corresponding ASCII character, dependent of the OS' input locale settings.
	 */
	public void keyPressed(char key) {
		if(activeKeys.containsKey((int)key)){
			//PApplet.println("already in"); //for debug
			//no need to add another note as this is just window's pressed-key-repeat functionality
			return;
		}else{
			//PApplet.println("now it's in"); //for debug
			activeKeys.put((int)key, true);
			//PApplet.println(activeKeys.size()); //for debug
		}
		
		int note = toNote(key);
		
		if(note != -1){
			voices.newVoice(note, 1);
		}
	}

	/**
	 * Hook for a released key on the keyboard.
	 * Releases the voice that corresponds to that key (see toNote()).
	 * @param key The keyboard key's corresponding ASCII character, dependent of the OS' input locale settings.
	 */
	public void keyReleased(char key) {
		if(activeKeys.containsKey((int)key)){
			//PApplet.println("removed"); //for debug
			activeKeys.remove((int)key);
		}else{
			//PApplet.println("blind release"); //for debug
			//blind release, shouldn't happen
			return;
		}
		
		int note = toNote(key);
		
		if(note != -1){
			voices.releaseVoice(note);
		}
	}
	
	/**
	 * Converts a keyboard key to the corrensponding midi note. The middle keyboard row (asdf...) is mapped to notes, 'y' corresponds to the leftmost note.
	 * @warning This uses the GERMAN keyboard layout.
	 * @param key The keyboard key of a german layout.
	 * @return The corrensponding midi note.
	 */
	private int toNote(char key){
		switch(key){
		case 'y': 
			return graphiccontrols.getFirstLineNote() + 0;
		case 'x': 
			return graphiccontrols.getFirstLineNote() + 1;
		case 'c': 
			return graphiccontrols.getFirstLineNote() + 2;
		case 'v': 
			return graphiccontrols.getFirstLineNote() + 3;
		case 'b': 
			return graphiccontrols.getFirstLineNote() + 4;
		case 'n': 
			return graphiccontrols.getFirstLineNote() + 5;
		case 'm': 
			return graphiccontrols.getFirstLineNote() + 6;
		case ',': 
			return graphiccontrols.getFirstLineNote() + 7;
		case '.': 
			return graphiccontrols.getFirstLineNote() + 8;
		case '-': 
			return graphiccontrols.getFirstLineNote() + 9;
		case 'a': 
			return graphiccontrols.getFirstLineNote() + 10;
		case 's': 
			return graphiccontrols.getFirstLineNote() + 11;
		case 'd': 
			return graphiccontrols.getFirstLineNote() + 12;
		case 'f': 
			return graphiccontrols.getFirstLineNote() + 13;
		case 'g': 
			return graphiccontrols.getFirstLineNote() + 14;
		case 'h': 
			return graphiccontrols.getFirstLineNote() + 15;
		case 'j': 
			return graphiccontrols.getFirstLineNote() + 16;
		case 'k': 
			return graphiccontrols.getFirstLineNote() + 17;
		case 'l': 
			return graphiccontrols.getFirstLineNote() + 18;
		case 'ö': 
			return graphiccontrols.getFirstLineNote() + 19;
		case 'ä': 
			return graphiccontrols.getFirstLineNote() + 20;
		case 'q': 
			return graphiccontrols.getFirstLineNote() + 21;
		case 'w': 
			return graphiccontrols.getFirstLineNote() + 22;
		case 'e': 
			return graphiccontrols.getFirstLineNote() + 23;
		case 'r': 
			return graphiccontrols.getFirstLineNote() + 24;
		case 't': 
			return graphiccontrols.getFirstLineNote() + 25;
		case 'z': 
			return graphiccontrols.getFirstLineNote() + 26;
		case 'u': 
			return graphiccontrols.getFirstLineNote() + 27;
		case 'i': 
			return graphiccontrols.getFirstLineNote() + 28;
		case 'o': 
			return graphiccontrols.getFirstLineNote() + 29;
		case 'p': 
			return graphiccontrols.getFirstLineNote() + 30;
		case 'ü': 
			return graphiccontrols.getFirstLineNote() + 31;
		case '+': 
			return graphiccontrols.getFirstLineNote() + 32;
		case '#': 
			return graphiccontrols.getFirstLineNote() + 33;
		default:
			return -1;
		}
	}
}
