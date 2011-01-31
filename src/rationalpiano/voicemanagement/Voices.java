package rationalpiano.voicemanagement;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;

import rationalpiano.consonance.Consonance;
import rationalpiano.consonance.IConsonance;
import rationalpiano.graphic.IGraphicControls;
import processing.core.PApplet;

/**
 * Manages all active voices and provides functions to add/remove voices and to calculate the consonances of all keys in range which also sets the visual element strengthness
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-28
 * @version 1.11
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class Voices implements IVoices {
	
	private PApplet papplet;
	private IGraphicControls graphiccontrols;
	
	private FadeTracking fade;
	private IConsonance consonance;

	private HashMap<Integer,Double> voicesValues = new HashMap<Integer,Double>();
	
	private ConcurrentHashMap<Integer, OneVoice> activeVoices = new ConcurrentHashMap<Integer, OneVoice>();
	private ConcurrentSkipListSet<Integer> scheduledRemoveVoices = new ConcurrentSkipListSet<Integer>();  
	private boolean sustain;
	
	private static final Logger logger = Logger.getLogger(Voices.class.getName());
	
	/**
	 * Initializing this class with standard ADSR values
	 * @param papplet The processing applet belonging to this voice management object.
	 * @param graphiccontrols The GraphicControls object to ask for line positions and to manipulate lines.
	 * @param framerate Frame Rate of the target PApplet. Needed for scaling the attack, decay and release values which are given in seconds to frame counts.
	 * @param holdSustain true if a voice should hold its sustain level after attack + decay phase; false = pluck mode: there is no decay phase at all, the release phase starts directly after the attack phase. 
	 * @param maxfrac The maximum dissonance value for rational numbers (numerator * denominator) to take account for. 157 is a good value. High values will cause longer initialization times!
	 * @param bellWidth The width of the bell shaped curve with which each fraction's point get's "fuzzified". bellWidth==1 means it's inflection point is at +- 1/Sqrt(e) semitones. bellWidth>0
	 */
	public Voices(PApplet papplet, IGraphicControls graphiccontrols, float framerate, Boolean holdSustain, int maxfrac, double bellWidth) {
		this(papplet, graphiccontrols, framerate, 0.15, 0.5, 0.65, 1, true, maxfrac, bellWidth); //standard values for attack, decay, stustain, release and use holdSustain
	}
	
	/**
	 * Initializing this class with the given parameters
	 * @param papplet The processing applet belonging to this voice management object.
	 * @param graphiccontrols The GraphicControls object to ask for line positions and to manipulate lines.
	 * @param framerate Frame Rate of the target PApplet. Needed for scaling the attack, decay and release values which are given in seconds to frame counts.
	 * @param attack Attack time in seconds. Time the voice volume reaches the velocity after activating it.
	 * @param decay Decay time in seconds. Time the voice volume reaches the sustain level after the attack phase.
	 * @param sustain Fraction of the velocity value where a voice gets held at after attack + decay phase. 0<=sustain<=1.
	 * @param release Release Time in seconds. Time the voice volume fades to 0 after releasing it.
	 * @param holdSustain true if a voice should hold its sustain level after attack + decay phase; false = pluck mode: there is no decay phase at all, the release phase starts directly after the attack phase.
	 * @param maxfrac The maximum dissonance value for rational numbers (numerator * denominator) to take account for. 157 is a good value. High values will cause longer initialization times!
	 * @param bellWidth The width of the bell shaped curve with which each fraction's point get's "fuzzified". bellWidth==1 means it's inflection point is at +- 1/Sqrt(e) semitones. bellWidth>0
	 */
	public Voices(PApplet papplet, IGraphicControls graphiccontrols, float framerate, double attack, double decay, double sustain, double release, boolean holdSustain, int maxfrac, double bellWidth) {
		logger.info("Setting up voice management");
		this.papplet = papplet;
		this.graphiccontrols = graphiccontrols;
		
		fade = new FadeTracking(framerate, attack, decay, sustain, release, holdSustain);
		
		consonance = new Consonance(graphiccontrols.getGraphicVisualizationElementArray().getLowestNote(), graphiccontrols.getGraphicVisualizationElementArray().getHighestNote() - graphiccontrols.getGraphicVisualizationElementArray().getLowestNote() + 1, maxfrac, bellWidth);
	}
	
	@Override
	public boolean newVoice(int midiNoteNumber, double velocity){
		try{
			scheduledRemoveVoices.remove(midiNoteNumber); //only interesting when sustain=true
			graphiccontrols.getGraphicVisualizationElementArray().getElement(midiNoteNumber).setActive(true);

			if(activeVoices.keySet().contains(midiNoteNumber)){
				double previousVolume = fade.getCurrentVelocity(activeVoices.get(midiNoteNumber));
				if(velocity < previousVolume){
					//don't do a "downward attack", but rather keep the current level 
					velocity = previousVolume;
				}
				activeVoices.get(midiNoteNumber).retrigger(velocity,previousVolume);
			}else{
				activeVoices.put(midiNoteNumber, new OneVoice(midiNoteNumber, velocity));
			}
			return true;
		}catch(NullPointerException e){
			return false;
		}
	}
	
	@Override
	public boolean releaseVoice(int midiNoteNumber){
		//the following line is not in the if branch because it would not release the voice when the voice was already faded out and after that the voice is released (only in holdSustain mode)
		try{
			graphiccontrols.getGraphicVisualizationElementArray().getElement(midiNoteNumber).setActive(false);

			if(activeVoices.keySet().contains(midiNoteNumber) && activeVoices.get(midiNoteNumber).isReleased() == false){
				if(sustain == true){
					scheduledRemoveVoices.add(midiNoteNumber);
				}else{
					activeVoices.get(midiNoteNumber).release();
				}
				
				return true;
			}
			
			return false;
		}catch(NullPointerException e){
			return false;
		}
	}
	
	/**
	 * Can be concurrently called with newVoice() or releaseVoice().
	 * Increments the hold time for each voice which sets its internal value to a new one according to ADSR.
	 * Also calculates the consonance anew.
	 */
	@Override
	public void tick(){
		OneVoice voice;
		double velo;
		
		if(sustain == false){
			for(Integer midiNoteNumber : scheduledRemoveVoices){
				try{
					activeVoices.get(midiNoteNumber).release();
				}catch(NullPointerException e){
				}
				scheduledRemoveVoices.remove(midiNoteNumber);
			}
		}
		
		//calculate current voice velocities
		
		for(Integer key : activeVoices.keySet()){
			try{
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
			}catch(NullPointerException e){
			}
		}

		
		//calculate consonances
		
		HashMap<Integer, Double> voiceConsonances = consonance.calculate(voicesValues);

		
		//set line widths according to found consonances
		
		for(Integer key : voiceConsonances.keySet()){
			try{
				graphiccontrols.getGraphicVisualizationElementArray().getElement(key).setVolume(voiceConsonances.get(key));
			}catch(NullPointerException e){
			}
		}
	}

	@Override
	public void setSustain(boolean sustain) {
		this.sustain = sustain;
	}
}
