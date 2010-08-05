package RationalPiano.VoiceManagement;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import RationalPiano.ConsonanceCalculation.Consonance;
import RationalPiano.Graphic.GraphicControls;
import RationalPiano.NoteOut.NoteOutput;

import processing.core.PApplet;

/**
 * Manages all active voices and provides functions to add/remove voices and to calculate the consonances of all keys in range which also sets the line widths
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class Voices {
	
	private PApplet papplet;
	private GraphicControls graphiccontrols;
	private NoteOutput noteoutput;
	
	private FadeTracking fade;
	private Consonance consonance;

	private HashMap<Integer,Double> voicesValues = new HashMap<Integer,Double>();
	
	private ConcurrentHashMap<Integer, OneVoice> activeVoices = new ConcurrentHashMap<Integer, OneVoice>();
	
	private static final Logger logger = Logger.getLogger(Voices.class.getName());
	
	/**
	 * Initializing this class with standard ADSR values
	 * @param papplet The processing applet belonging to this voice management object.
	 * @param graphiccontrols The GraphicControls object to ask for line positions and to manipulate lines.
	 * @param noteoutput The NoteOutput object to send note on/off messages to.
	 * @param framerate Frame Rate of the target PApplet. Needed for scaling the attack, decay and release values which are given in seconds to frame counts.
	 * @param holdSustain true if a voice should hold its sustain level after attack + decay phase; false = pluck mode: there is no decay phase at all, the release phase starts directly after the attack phase. 
	 * @param maxfrac The maximum dissonance value for rational numbers (numerator * denominator) to take account for. 157 is a good value. High values will cause longer initialization times!
	 * @param bellWidth The width of the bell shaped curve with which each fraction's point get's "fuzzified". bellWidth==1 means it's inflection point is at +- 1/Sqrt(e) semitones. bellWidth>0
	 */
	public Voices(PApplet papplet, GraphicControls graphiccontrols, NoteOutput noteoutput, float framerate, Boolean holdSustain, int maxfrac, double bellWidth) {
		this(papplet, graphiccontrols, noteoutput, framerate, 0.15, 0.5, 0.65, 1, true, maxfrac, bellWidth); //standard values for attack, decay, stustain, release and use holdSustain
	}
	
	/**
	 * Initializing this class with the given parameters
	 * @param papplet The processing applet belonging to this voice management object.
	 * @param graphiccontrols The GraphicControls object to ask for line positions and to manipulate lines.
	 * @param noteoutput The NoteOutput object to send note on/off messages to.
	 * @param framerate Frame Rate of the target PApplet. Needed for scaling the attack, decay and release values which are given in seconds to frame counts.
	 * @param attack Attack time in seconds. Time the voice volume reaches the velocity after activating it.
	 * @param decay Decay time in seconds. Time the voice volume reaches the sustain level after the attack phase.
	 * @param sustain Fraction of the velocity value where a voice gets held at after attack + decay phase. 0<=sustain<=1.
	 * @param release Release Time in seconds. Time the voice volume fades to 0 after releasing it.
	 * @param holdSustain true if a voice should hold its sustain level after attack + decay phase; false = pluck mode: there is no decay phase at all, the release phase starts directly after the attack phase.
	 * @param maxfrac The maximum dissonance value for rational numbers (numerator * denominator) to take account for. 157 is a good value. High values will cause longer initialization times!
	 * @param bellWidth The width of the bell shaped curve with which each fraction's point get's "fuzzified". bellWidth==1 means it's inflection point is at +- 1/Sqrt(e) semitones. bellWidth>0
	 */
	public Voices(PApplet papplet, GraphicControls graphiccontrols, NoteOutput noteoutput, float framerate, double attack, double decay, double sustain, double release, boolean holdSustain, int maxfrac, double bellWidth) {
		logger.info("Setting up voice management");
		this.papplet = papplet;
		this.graphiccontrols = graphiccontrols;
		this.noteoutput = noteoutput;
		
		fade = new FadeTracking(framerate, attack, decay, sustain, release, holdSustain);
		
		consonance = new Consonance(graphiccontrols.getFirstLineNote(), graphiccontrols.getLineCount(), maxfrac, bellWidth);
	}
	
	/**
	 * Adds a new voice with the given MIDI note number
	 * Can be concurrently called with voiceTick() or releaseVoice().
	 * @param midiNoteNumber MIDI note number to turn on
	 * @param velocity velocity to turn the given note on with; 0<=velocity<=1
	 * @return true if the given MIDI note was turned off before and now is turned on, false otherwise
	 */
	public boolean newVoice(int midiNoteNumber, double velocity){
		double previousVolume = 0;
		
		if(activeVoices.keySet().contains(midiNoteNumber)){
			if(activeVoices.get(midiNoteNumber).isReleased() == false){
				return false; //nothing to do, already active, only one voice per note
			} else {
				previousVolume = fade.getCurrentVelocity(activeVoices.get(midiNoteNumber));
				activeVoices.remove(midiNoteNumber);
			}
		}
		
		noteoutput.noteOn(midiNoteNumber, 1);
		
		graphiccontrols.setLineActive(midiNoteNumber, true);
		
		activeVoices.put(midiNoteNumber, new OneVoice(midiNoteNumber, velocity, previousVolume));
		
		return true;
	}
	
	/**
	 * Releases the given voice. This will start the voice's release phase.
	 * Can be concurrently called with voiceTick() or newVoice().
	 * @param midiNoteNumber MIDI note number to turn off
	 * @return true if the given MIDI note was turned on before and thus was turned off, false otherwise
	 */
	public boolean releaseVoice(int midiNoteNumber){
		//the following two lines are not in the if branch because it would not release the voice when the voice was already faded out and after that the voice is released (only in holdSustain mode)
		noteoutput.noteOff(midiNoteNumber);
		graphiccontrols.setLineActive(midiNoteNumber, false);
		
		if(activeVoices.keySet().contains(midiNoteNumber) && activeVoices.get(midiNoteNumber).isReleased() == false){
			activeVoices.get(midiNoteNumber).release();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Can be concurrently called with newVoice() or releaseVoice().
	 * Increments the hold time for each voice which sets its internal value to a new one according to ADSR.
	 * Also calculates the consonance anew.
	 */
	public void voiceTick(){
		OneVoice voice;
		double velo;
		
		//calculate current voice velocities
		
		for(Integer key : activeVoices.keySet()){
			voice = activeVoices.get(key);
			voice.incrementHoldtime();
			velo = fade.getCurrentVelocity(voice);
			if(velo <= 0){
				activeVoices.remove(key);
				
				if(voicesValues.containsKey(key)){
					voicesValues.remove(key);
				}
				
				//graphiccontrols.setLineWidth(key, 0); //would set the line width to 0

				//graphiccontrols.setLineActive(key, false); //would visually release voice if adsr approaches 0

			}else{
				voicesValues.put(key, velo);
				
				//graphiccontrols.setLineWidth(key, (int)(255*fade.getCurrentVelocity(voice))); //would set the line to the width according to its current adsr value
			}
		}

		
		//calculate consonances
		
		HashMap<Integer, Double> voiceConsonances = consonance.calculate(voicesValues);

		
		//set line widths according to found consonances
		
		for(Integer key : voiceConsonances.keySet()){
			graphiccontrols.setLineWidth(key, voiceConsonances.get(key));
		}
	}
}
