package RationalPiano.NoteOut;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.gombology.OscXXL.PortOut;
import net.gombology.synOscP5.SYN;
import net.gombology.synOscP5.SynMessage;
import processing.core.PApplet;

/**
 * Manages note output for OSC
 * Allows to turn on/off voices on an osc port following the rules of the SYNoscopy namespace
 * 
 * @author Fabian Ehrentraud
 * @date 2010-12-27
 * @version 1.1
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class SendOsc {

	private PApplet papplet;
	private PortOut out;
	
	private static final Logger logger = Logger.getLogger(SendOsc.class.getName());
	
	//first value is midi note number, second is osc voice number
	private ConcurrentHashMap<Integer, Integer> activeVoices = new ConcurrentHashMap<Integer, Integer>();

	/**
	 * Initializes OSC output with the given UDP port.
	 * @param papplet The processing applet to send the OSC messages from.
	 * @param port The UDP port to send the OSC messages from.
	 */
	public SendOsc(PApplet papplet, int port) {
		logger.info("Setting up OSC output on port " + port);
		this.papplet = papplet;
		out = PortOut.getInstance("localhost", port, papplet);
	}
	
	/**
	 * Close socket when quitting.
	 * FIXME won't get called - but actually it isn't needed either
	 */
	@Override
	public void finalize(){
		logger.info("Closing OSC port");
		out.close();
	}

	/**
	 * Turns on the voice with the given MIDI note number and velocity.
	 * @param midiNoteNumber The MIDI note number to turn on.
	 * @param velocity Velocity of the new voice; 0<=velocity<=1
	 */
	public void voiceOn(int midiNoteNumber, float velocity) {
		//Bundle bundle = new Bundle();
		SynMessage msg;
		
		int voicenumber = useFreeVoice(midiNoteNumber);
		
		msg = new SYN().synth(1).voice(voicenumber).midi(midiNoteNumber).cutOff(false).velocity(velocity);
		//bundle.add(msg);
		//out.send(bundle, papplet);
		out.send(msg, papplet);
	}
	
	/**
	 * Associates the given midi note number with the an unused synoscopy voice number.
	 * @param midiNoteNumber This will be associated with a free synoscopy voice number.
	 * @return The synoscopy voice number the midi note got associated with.
	 */
	private int useFreeVoice(int midiNoteNumber) {
		if(activeVoices.contains(midiNoteNumber)){
			//if the midi note number is already in the list, reuse the voice number - should not happen with properly working Voices class
			int v = activeVoices.get(midiNoteNumber);
			activeVoices.put(midiNoteNumber, v);
			return v;
		}
		for(int v=0; v < activeVoices.size()+1; v++){
			if(activeVoices.containsKey(v)){
				activeVoices.put(midiNoteNumber, v);
				return v;
			}
		}
		return 0; //should be unreachable
	}

	/**
	 * Removes a midi note from the list of the corresponding active voices.
	 * @param midiNoteNumber This will be searched in the list of the corresponding active voices and removed if it exists.
	 */
	private void freeVoice(int midiNoteNumber) {
		if(activeVoices.contains(midiNoteNumber)){
			activeVoices.remove(midiNoteNumber);
		}
	}	

	/**
	 * Turns off the voice with the given MIDI note number.
	 * @param midiNoteNumber The MIDI note number to turn off.
	 */
	public void voiceOff(int midiNoteNumber) {
		SynMessage msg;
		freeVoice(midiNoteNumber);
		msg = new SYN().synth(1).voice(midiNoteNumber).off();
		out.send(msg, papplet);
	}

	/**
	 * Sends a sustain message with the given sustain value.
	 * @param sustain Sustain value to send; 0<=sustain<=1
	 */
	public void sustain(double sustain) {
		SynMessage msg;
		msg = new SYN().synth(1).sustain(sustain); //TODO check if the argument has the right data range [0,1]
		out.send(msg, papplet);
	}

}

