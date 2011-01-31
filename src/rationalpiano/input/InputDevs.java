package rationalpiano.input;

import java.util.concurrent.ConcurrentHashMap;

import processing.core.PApplet;
import rationalpiano.graphic.IGraphicControls;
import rationalpiano.noteout.INoteOutput;
import rationalpiano.voicemanagement.IVoices;

/**
 * Manages Input from Mouse, Keyboard, MIDI and Multitouch.
 * Translates presses to newVoice() and noteOn() and releases to releaseVoice() and noteOff().
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-30
 * @version 1.1
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class InputDevs implements IKeyInput, IMouseInput {
	
	private PApplet papplet;
	private IVoices voices;
	private INoteOutput noteoutput;
	private IGraphicControls graphiccontrols;
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
	public InputDevs(PApplet papplet, IVoices voices, INoteOutput noteoutput, IGraphicControls graphiccontrols, int tuioPort, String midiInputDevice) {
		this.papplet = papplet;
		this.voices = voices;
		this.noteoutput = noteoutput;
		this.graphiccontrols = graphiccontrols;
		this.tuioinput = new TuioInput(papplet, voices, noteoutput, graphiccontrols, tuioPort);
		this.midiinput = new InputMidi(papplet, voices, noteoutput, midiInputDevice);
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		//PApplet.println(mouseButton); //debug
		
		if(graphiccontrols.isMouseOverGraphicVisualizationElementArray()){
			if(lastMouseNote != -1){
				return; //clicked another mouse button while one was still held down
			}
			
			lastMouseNote = graphiccontrols.getGraphicVisualizationElementArray().getElementNote(mouseX,mouseY);
			//lines.getLine(this.mouseX).volume((int)(Math.random()*255));
			
			if(lastMouseNote != -1){
				noteoutput.noteOn(lastMouseNote, 1);
				voices.newVoice(lastMouseNote, 1);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		if(graphiccontrols.isMouseOverGraphicVisualizationElementArray()){
			//note_last = graphiccontrols.getLineNote(mouseX,mouseY); //not necessary as the last key is released
			
			if(lastMouseNote != -1){
				noteoutput.noteOff(lastMouseNote);
				voices.releaseVoice(lastMouseNote);
			}
			
			lastMouseNote = -1;
		}
	}

	@Override
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

	@Override
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
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 0;
		case 'x': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 1;
		case 'c': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 2;
		case 'v': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 3;
		case 'b': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 4;
		case 'n': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 5;
		case 'm': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 6;
		case ',': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 7;
		case '.': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 8;
		case '-': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 9;
		case 'a': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 10;
		case 's': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 11;
		case 'd': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 12;
		case 'f': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 13;
		case 'g': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 14;
		case 'h': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 15;
		case 'j': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 16;
		case 'k': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 17;
		case 'l': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 18;
		case 'ö': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 19;
		case 'ä': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 20;
		case 'q': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 21;
		case 'w': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 22;
		case 'e': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 23;
		case 'r': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 24;
		case 't': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 25;
		case 'z': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 26;
		case 'u': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 27;
		case 'i': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 28;
		case 'o': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 29;
		case 'p': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 30;
		case 'ü': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 31;
		case '+': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 32;
		case '#': 
			return graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 33;
		default:
			return -1;
		}
	}

	/**
	 * @return The InputMidi object which handles Midi Input.
	 */
	public InputMidi getInputMidi() {
		return midiinput;
	}
}
