package RationalPiano.VoiceManagement;

/**
 * Holds ADSR values and provides methods to convert a holdtime with initial velocity to current velocity.
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class FadeTracking {
	
	private float framerate;
	
	private int attack; //FIXME change attack, decay and release to delta value, not as tick-time. BUT then there is no if() for the time!
	private int decay;
	private double sustain;
	private int release;
	
	private boolean holdSustain;
	
	/**
	 * @param framerate Frame Rate of the target PApplet. Needed for scaling the attack, decay and release values which are given in seconds to frame counts.
	 * @param attack Attack time in seconds. Time the voice volume reaches the velocity after activating it.
	 * @param decay Decay time in seconds. Time the voice volume reaches the sustain level after the attack phase.
	 * @param sustain Fraction of the velocity value where a voice gets held at after attack + decay phase. 0<=sustain<=1.
	 * @param release Release Time in seconds. Time the voice volume fades to 0 after releasing it.
	 * @param holdSustain true if a voice should hold its sustain level after attack + decay phase; false = pluck mode: there is no decay phase at all, the release phase starts directly after the attack phase.
	 */
	public FadeTracking(float framerate, double attack, double decay, double sustain, double release, boolean holdSustain) {
		this.framerate = framerate;
		setAttack(attack);
		setDecay(decay);
		setSustain(sustain);
		setRelease(release);
		this.setHoldSustain(holdSustain);
	}
	
	/**
	 * Gets the current velocity (volume) of the given voice.
	 * @param voice A voice to calculate its current state for.
	 * @return The current velocity of the given voice. 0<=return<=1.
	 */
	public double getCurrentVelocity(OneVoice voice){
		//double initialVelocity, int holdtime, boolean released, int releasedtime
		
		double velo;
		
		if(!voice.isReleased()){
			//not yet released
			
			if(voice.getHoldtime() <= attack){
				//in attack phase, NOT attacking from 0 but from voice.getPreviousVolume()
				//processing.core.PApplet.println("A"); //debug
				velo = voice.getPreviousVolume() + (voice.getInitialVelocity() - voice.getPreviousVolume()) * (voice.getHoldtime() / (double)attack);
				//velo = voice.getInitialVelocity() * (voice.getHoldtime() / (double)attack); //attacking from 0
			}else{
				if(holdSustain == true){
					//hold after attack + decay
					if(voice.getHoldtime() <= attack + decay){
						//still decaying
						//processing.core.PApplet.println("D"); //debug
						velo = voice.getInitialVelocity() * (1 - (1 - sustain) * (voice.getHoldtime() - attack) / (double)decay);
					}else{
						//finished decaying, sustaining
						//processing.core.PApplet.println("S"); //debug
						velo = voice.getInitialVelocity() * sustain;
					}
				}else{
					//no decay phase, releasing right after attack
					//processing.core.PApplet.println("pR"); //debug
					velo = voice.getInitialVelocity() * (1 - ((voice.getHoldtime() - attack) / (double)release));
				}
			}
			
		}else{
			//is released
			
			if(voice.getReleasedtime() <= attack){
				//released in attack phase
				//processing.core.PApplet.println("ar"); //debug
				velo = (voice.getPreviousVolume() + (voice.getInitialVelocity() - voice.getPreviousVolume()) * (voice.getReleasedtime() / (double)attack)) * (1 - ((voice.getHoldtime() - voice.getReleasedtime()) / (double)release));
				//velo = (voice.getInitialVelocity() * (voice.getReleasedtime() / (double)attack)) * (1 - ((voice.getHoldtime() - voice.getReleasedtime()) / (double)release)); //attacking from 0
				//Mathematica: Manipulate[Plot[Piecewise[{{i*x/a, x <= rt}, {i*(rt/a)*(1 - ((x - rt)/r)),x > rt}}], {x, 0, rt + r}], {{rt, 20}, 0, 20}, {{a, 20}, 0, 20}, {{r, 20}, 0, 30}, {{i, 1}, 0, 1}]
			}else{
				if(holdSustain == true){
					//hold after attack + decay
					if(voice.getReleasedtime() <= attack + decay){
						//released in decay phase
						//processing.core.PApplet.println("d"); //debug
						velo = (voice.getInitialVelocity() * (1 - (1 - sustain) * (voice.getReleasedtime() - attack) / (double)decay)) * (1 - ((voice.getHoldtime() - voice.getReleasedtime()) / (double)release));
					}else{
						//released in sustain phase
						//processing.core.PApplet.println("r"); //debug
						velo = voice.getInitialVelocity() * sustain * (1 - ((voice.getHoldtime() - voice.getReleasedtime()) / (double)release));
					}
				}else{
					//no decay phase, releasing right after attack, releasing the voice doesn't make any difference
					//processing.core.PApplet.println("pr"); //debug
					velo = voice.getInitialVelocity() * (1 - ((voice.getHoldtime() - attack) / (double)release));
				}
			}
		}
		
		if(velo > 0){
			return velo; 
		} else {
			return 0;
		}
	}
	
	/**
	 * @return The attack value in seconds.
	 */
	public int getAttack() {
		return (int) (attack / framerate);
	}
	
	/**
	 * Sets the attack value.
	 * @param attack The attack value in seconds. Must be >=0.
	 */
	public void setAttack(double attack) {
		this.attack = (int) (attack * this.framerate);
	}
	
	/**
	 * @return The decay value in seconds.
	 */
	public int getDecay() {
		return (int) (decay / framerate);
	}
	
	/**
	 * Sets the decay value.
	 * @param decay The decay value in seconds. Must be >=0.
	 */
	public void setDecay(double decay) {
		this.decay = (int) (decay * this.framerate);
	}
	
	/**
	 * @return The sustain scale factor. Between 0 and 1.
	 */
	public double getSustain() {
		return sustain;
	}
	
	/**
	 * Sets the sustain scale factor.
	 * @param sustain The sustain scale factor. Must be between 0 and 1.
	 */
	public void setSustain(double sustain) {
		this.sustain = sustain;
	}
	
	/**
	 * @return The release value in seconds.
	 */
	public int getRelease() {
		return (int) (release / framerate);
	}
	
	/**
	 * Sets the release value.
	 * @param release The release value in seconds. Must be >= 0.
	 */
	public void setRelease(double release) {
		this.release = (int) (release * framerate);
	}

	/**
	 * Sets whether the voice should be held at the sustain value or not.
	 * @param holdSustain If true, a voice that is still held after the decay will stay at the initial velocity scaled by the sustain. If false, there is no decay phase and the voice will be faded out with the release time directly after the attack.
	 */
	public void setHoldSustain(boolean holdSustain) {
		this.holdSustain = holdSustain;
	}

	/**
	 * @return The boolean value wheter a voice is held at the sustain value after decay or not (release directly after attack).
	 */
	public boolean isHoldSustain() {
		return holdSustain;
	}
}
