package RationalPiano.VoiceManagement;

/**
 * Represents one active voice with one key and holds the holdtime and whether this voice was already released.
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class OneVoice {

	private int key;
	private double initialVelocity;
	private double previousVolume;
	private int holdtime = 0;
	private int releasedtime = 0;

	private boolean released = false;

	/**
	 * @param key Midi note number this voice is associated with
	 * @param initialVelocity The velocity the voice has been struck with in the beginning. Between 0 and 1.
	 */
	public OneVoice(int key, double initialVelocity) {
		this(key, initialVelocity, 0);
	}

	/**
	 * @param key Midi note number this voice is associated with
	 * @param initialVelocity The velocity the voice has been struck with in the beginning. Between 0 and 1.
	 * @param previousVolume The volume the same key had when this voice overwrote it. Between 0 and 1.
	 */
	public OneVoice(int key, double initialVelocity, double previousVolume) {
		this.key = key;
		this.initialVelocity = initialVelocity;
		this.previousVolume = previousVolume;
	}
	
	/**
	 * @return The volume the same key had when this voice overwrote it.
	 */
	public double getPreviousVolume() {
		return previousVolume;
	}

	/**
	 * @return The midi note this voice is associated with.
	 */
	public int getKey() {
		return key;
	}
	
	/**
	 * @return The velocity the voice has struck with in the beginning. Between 0 and 1.
	 */
	public double getInitialVelocity() {
		return initialVelocity;
	}
	
	/**
	 * Adds one to the holdtime.
	 */
	public void incrementHoldtime(){
		holdtime++;
	}
	
	/**
	 * @return The count incrementHoldtime() has been called. 
	 */
	public int getHoldtime() {
		return holdtime;
	}
	
	/**
	 * @return The count incrementHoldtime() has been called since release() has been called.
	 */
	public int getReleasedtime() {
		return releasedtime;
	}
	
	/**
	 * Sets this voice as released. Only possible once.
	 */
	public void release(){
		if(released == false){
			released = true;
			releasedtime = holdtime;
		}
	}
	
	/**
	 * @return If this voice is already released.
	 */
	public boolean isReleased() {
		return released;
	}
}

