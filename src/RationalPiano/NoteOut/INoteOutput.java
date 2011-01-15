package RationalPiano.NoteOut;

/**
 * Manages note output for MIDI and OSC
 * Allows to turn on/off notes/voices on a midi channel / osc port and to de/activate sustain
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface INoteOutput {

	/**
	 * Turns on the given note via MIDI and/or OSC
	 * @param midiNoteNumber The MIDI note number to turn on
	 * @param velocity Initial note velocity; 0<=velocity<=1
	 */
	public abstract void noteOn(int midiNoteNumber, double velocity);

	/**
	 * Turns off the given note via MIDI and/or OSC
	 * @param midiNoteNumber The MIDI note number to turn off
	 */
	public abstract void noteOff(int midiNoteNumber);
	
	/**
	 * Sets the value of the sustain (damper pedal).
	 * @param sustain Value of the sustain parameter; 0<=sustain<=1
	 */
	public abstract void sustain(double sustain);

}