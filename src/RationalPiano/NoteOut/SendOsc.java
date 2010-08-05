package RationalPiano.NoteOut;

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
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class SendOsc {

	private PApplet papplet;
	private PortOut out;
	
	private static final Logger logger = Logger.getLogger(SendOsc.class.getName());

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
		//TODO better voice management - warning: when reusing a voice that is still in its release phase, that cuts the old voice. that's why here's a voice per note number used yet
		SynMessage msg;
		msg = new SYN().synth(1).voice(midiNoteNumber).midi(midiNoteNumber).velocity(velocity);
		//bundle.add(msg);
		//out.send(bundle, papplet);
		out.send(msg, papplet);
	}
	
	/**
	 * Turns off the voice with the given MIDI note number.
	 * @param midiNoteNumber The MIDI note number to turn off.
	 */
	public void voiceOff(int midiNoteNumber) {
		SynMessage msg;
		msg = new SYN().synth(1).voice(midiNoteNumber).off();
		out.send(msg, papplet);
	}
}

