package rationalpiano.noteout;

/**
 * Manages note output for MIDI and OSC
 * Allows to turn on/off notes/voices on a midi channel / osc port and to de/activate sustain
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-28
 * @version 1.01
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface INoteOutput {

	/**
	 * Possible modes of note message outputs: either only via MIDI, only via OSC or both
	 */
	public enum outputModes {MIDI_ONLY, OSC_ONLY, MIDI_AND_OSC, NO_OUTPUT}

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