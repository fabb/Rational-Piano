package RationalPiano.Input;

import java.util.concurrent.ConcurrentHashMap;

import processing.core.PApplet;
import RationalPiano.Graphic.IGraphicControlsPassive;
import RationalPiano.NoteOut.INoteOutput;
import RationalPiano.VoiceManagement.IVoices;

/**
 * Manages Input from Mouse, Keyboard, MIDI and Multitouch XXX.
 * Translates presses to newVoice() and noteOn() and releases to releaseVoice() and noteOff().
 * 
 * @author Fabian Ehrentraud
 * @date 2010-10-10
 * @version 1.01
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class InputDevs implements IKeyInput, IMouseInput {
	
	private PApplet papplet;
	private IVoices voices;
	private INoteOutput noteoutput;
	private IGraphicControlsPassive graphiccontrols;
	private TuioInput tuioinput;
	private InputMidi midiinput;
	
	private int lastMouseNote;
	
	private ConcurrentHashMap<Integer, Boolean> activeKeys = new ConcurrentHashMap<Integer, Boolean>();

	/**
	 * Initializes the input management and generates a TUIO UDP receiver
	 * @param papplet The processing applet to get the size of for scaling TUIO input
	 * @param voices A Voices object for creating new voices and removing old ones
	 * @param noteoutput The NoteOutput object to send note on/off messages to.
	 * @param graphiccontrols A IGraphicControlsPassive object needed for getting the corresponding note at a given coordinate
	 * @param tuioPort The local UDP port the TUIO Listener should listen at
	 * @param midiInputDevice Partial case-insensitive name of the wanted Midi Input Device
	 */
	public InputDevs(PApplet papplet, IVoices voices, INoteOutput noteoutput, IGraphicControlsPassive graphiccontrols, int tuioPort, String midiInputDevice) {
		this.papplet = papplet;
		this.voices = voices;
		this.noteoutput = noteoutput;
		this.graphiccontrols = graphiccontrols;
		this.tuioinput = new TuioInput(papplet, voices, noteoutput, graphiccontrols, tuioPort);
		this.midiinput = new InputMidi(papplet, voices, noteoutput, midiInputDevice);
	}

	/* (non-Javadoc)
	 * @see RationalPiano.Input.IMouseInput#mousePressed(int, int, int)
	 */
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		//PApplet.println(mouseButton); //debug
		if(lastMouseNote != -1){
			return; //clicked another mouse button while one was still held down
		}
		
		lastMouseNote = graphiccontrols.getElementNote(mouseX,mouseY);
		//lines.getLine(this.mouseX).volume((int)(Math.random()*255));
		
		if(lastMouseNote != -1){
			noteoutput.noteOn(lastMouseNote, 1);
			voices.newVoice(lastMouseNote, 1);
		}
	}

	/* (non-Javadoc)
	 * @see RationalPiano.Input.IMouseInput#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		//note_last = graphiccontrols.getLineNote(mouseX,mouseY); //not necessary as the last key is released
		
		if(lastMouseNote != -1){
			noteoutput.noteOff(lastMouseNote);
			voices.releaseVoice(lastMouseNote);
		}
		
		lastMouseNote = -1;
	}

	/* (non-Javadoc)
	 * @see RationalPiano.Input.IKeyInput#keyPressed(char)
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
			noteoutput.noteOn(note, 1);
			voices.newVoice(note, 1);
		}
	}

	/* (non-Javadoc)
	 * @see RationalPiano.Input.IKeyInput#keyReleased(char)
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
			noteoutput.noteOff(note);
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
			return graphiccontrols.getLowestNote() + 0;
		case 'x': 
			return graphiccontrols.getLowestNote() + 1;
		case 'c': 
			return graphiccontrols.getLowestNote() + 2;
		case 'v': 
			return graphiccontrols.getLowestNote() + 3;
		case 'b': 
			return graphiccontrols.getLowestNote() + 4;
		case 'n': 
			return graphiccontrols.getLowestNote() + 5;
		case 'm': 
			return graphiccontrols.getLowestNote() + 6;
		case ',': 
			return graphiccontrols.getLowestNote() + 7;
		case '.': 
			return graphiccontrols.getLowestNote() + 8;
		case '-': 
			return graphiccontrols.getLowestNote() + 9;
		case 'a': 
			return graphiccontrols.getLowestNote() + 10;
		case 's': 
			return graphiccontrols.getLowestNote() + 11;
		case 'd': 
			return graphiccontrols.getLowestNote() + 12;
		case 'f': 
			return graphiccontrols.getLowestNote() + 13;
		case 'g': 
			return graphiccontrols.getLowestNote() + 14;
		case 'h': 
			return graphiccontrols.getLowestNote() + 15;
		case 'j': 
			return graphiccontrols.getLowestNote() + 16;
		case 'k': 
			return graphiccontrols.getLowestNote() + 17;
		case 'l': 
			return graphiccontrols.getLowestNote() + 18;
		case 'ö': 
			return graphiccontrols.getLowestNote() + 19;
		case 'ä': 
			return graphiccontrols.getLowestNote() + 20;
		case 'q': 
			return graphiccontrols.getLowestNote() + 21;
		case 'w': 
			return graphiccontrols.getLowestNote() + 22;
		case 'e': 
			return graphiccontrols.getLowestNote() + 23;
		case 'r': 
			return graphiccontrols.getLowestNote() + 24;
		case 't': 
			return graphiccontrols.getLowestNote() + 25;
		case 'z': 
			return graphiccontrols.getLowestNote() + 26;
		case 'u': 
			return graphiccontrols.getLowestNote() + 27;
		case 'i': 
			return graphiccontrols.getLowestNote() + 28;
		case 'o': 
			return graphiccontrols.getLowestNote() + 29;
		case 'p': 
			return graphiccontrols.getLowestNote() + 30;
		case 'ü': 
			return graphiccontrols.getLowestNote() + 31;
		case '+': 
			return graphiccontrols.getLowestNote() + 32;
		case '#': 
			return graphiccontrols.getLowestNote() + 33;
		default:
			return -1;
		}
	}
}
