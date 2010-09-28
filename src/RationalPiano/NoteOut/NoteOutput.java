package RationalPiano.NoteOut;

import processing.core.*;

/**
 * Manages note output for MIDI and OSC
 * Allows to turn on/off notes/voices on a midi channel / osc port
 * 
 * @author Fabian Ehrentraud
 * @date 2010-09-24
 * @version 1.01
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class NoteOutput {

	/**
	 * Possible modes of note message outputs: either only via MIDI, only via OSC or both
	 */
	public enum outputModes {MIDI_ONLY, OSC_ONLY, MIDI_AND_OSC};
	
	private boolean oscOn = false;
	private boolean midiOn = false;
	
	private SendMidi sendmidi = null;
	private SendOsc sendosc = null;
	
	/**
	 * Initializes the MIDI/OSC output
	 * @param papplet The processing applet to send the MIDI/OSC messages from
	 * @param outputMode allows to either activate midi output or osc output or both
	 * @param oscport UDP port to send the OSC note messages to
	 * @param midiDevice PART of the name of the MIDI device to send the note messages to
	 * @param midiChannel MIDI channel to send the note messages to; 0<=midiChannel<=15
	 */
	public NoteOutput(PApplet papplet, outputModes outputMode, int oscport, String midiDevice, int midiChannel) {
		if(outputMode == outputModes.OSC_ONLY || outputMode == outputModes.MIDI_AND_OSC ){
			oscOn = true;
			sendosc = new SendOsc(papplet, oscport);
		}
		if(outputMode == outputModes.MIDI_ONLY || outputMode == outputModes.MIDI_AND_OSC ){
			midiOn = true;
			sendmidi = new SendMidi(papplet, midiDevice, midiChannel);
		}
	}

	/**
	 * Turns on the given note via MIDI and/or OSC
	 * @param midiNoteNumber The MIDI note number to turn on
	 * @param velocity Initial note velocity; 0<=velocity<=1
	 */
	public void noteOn(int midiNoteNumber, double velocity){
		if(oscOn == true){
			sendosc.voiceOn(midiNoteNumber, (float)velocity);
		}
		if(midiOn == true){
			sendmidi.noteOn(midiNoteNumber, (int)(127*velocity));
		}
	}
	
	/**
	 * Turns off the given note via MIDI and/or OSC
	 * @param midiNoteNumber The MIDI note number to turn off
	 */
	public void noteOff(int midiNoteNumber){
		if(oscOn == true){
			sendosc.voiceOff(midiNoteNumber);
		}
		if(midiOn == true){
			sendmidi.noteOff(midiNoteNumber);
		}
	}
}
