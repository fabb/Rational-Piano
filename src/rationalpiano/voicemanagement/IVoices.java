package rationalpiano.voicemanagement;

/**
 * Manages all active voices and provides functions to add/remove voices and to calculate the consonances of all keys in range which also sets the visual element strengthness
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IVoices extends ITickable {

	/**
	 * Adds a new voice with the given MIDI note number
	 * Can be concurrently called with tick() or releaseVoice().
	 * @param midiNoteNumber MIDI note number to turn on
	 * @param velocity velocity to turn the given note on with; 0<=velocity<=1
	 * @return true if the given MIDI note was turned off before and now is turned on, false otherwise
	 */
	public abstract boolean newVoice(int midiNoteNumber, double velocity);

	/**
	 * Releases the given voice. This will start the voice's release phase.
	 * Can be concurrently called with tick() or newVoice().
	 * @param midiNoteNumber MIDI note number to turn off
	 * @return true if the given MIDI note was turned on before and thus was turned off, false otherwise
	 */
	public abstract boolean releaseVoice(int midiNoteNumber);
	
	/**
	 * Sets the sustain value to the given one.
	 * Activated sustain will not release held voices, even if releaseVoices() is called for them.
	 * This is active until sustain is turned off again.
	 * @param sustain True when sustain should be activated, False otherwise.
	 */
	public abstract void setSustain(boolean sustain);

}