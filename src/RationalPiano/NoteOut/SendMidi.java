package RationalPiano.NoteOut;

import java.util.logging.Logger;

import processing.core.PApplet;
import rwmidi.MidiOutput;
import rwmidi.RWMidi;

/**
 * Manages note output for MIDI
 * Allows to turn on/off notes on a midi channel
 * 
 * @author Fabian Ehrentraud
 * @date 2010-09-23
 * @version 1.01
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class SendMidi {
	
	private PApplet papplet;
	private MidiOutput output;
	private int channel;
	
	private static final Logger logger = Logger.getLogger(SendMidi.class.getName());
	
	/**
	 * Opens the specified MIDI device.
	 * @param papplet The processing applet to send the MIDI messages from.
	 * @param midiDevice PART of the name of the MIDI device to send the note messages to.
	 * @param midiChannel MIDI channel to send the note messages to; 0<=midiChannel<=15
	 */
	public SendMidi(PApplet papplet, String midiDevice, int midiChannel) {
		logger.info("Setting up MIDI output on device '" + midiDevice + "' on channel " + midiChannel);
		
		this.papplet = papplet;
		
		this.channel = midiChannel;
		
		String devices[] = RWMidi.getOutputDeviceNames();
		
		logger.config("Available MIDI Output Devices:");
		for(String device : devices){
			logger.config("    " + device);
		}
		logger.config("Searching for MIDI Device containing string '" + midiDevice + "'");
		
		int i;
		for(i=0; i<devices.length; i++){
			if(devices[i].toLowerCase().contains(midiDevice.toLowerCase())){
				logger.config("MIDI Device chosen: '" + devices[i] + "'");
				break;
			}
		}
		if(i==devices.length){
			i--;
			logger.severe("MIDI Device not found, choosing last device: '" + devices[i] + "'");
		}

		output = RWMidi.getOutputDevices()[i].createOutput(); //FIXME exception can happen here but the library catches it (as it is a processing library) => modify library to throw the exeption
	}
	
	/**
	 * Close MIDI device when quitting.
	 * FIXME won't get called - but actually it isn't needed either
	 */
	public void finalize(){
		logger.info("Closing MIDI device");
		output.closeMidi();
	}

	/**
	 * Turns on the voice with the given MIDI note number and velocity.
	 * @param midiNoteNumber The MIDI note number to turn on.
	 * @param velocity Velocity of the new voice; 0<=velocity<=127
	 */
	public void noteOn(int midiNoteNumber, int velocity){
		if(velocity<=0 || velocity>127){
			return;
		}
		if(midiNoteNumber<=0 || midiNoteNumber>127){
			return;
		}
		
		output.sendNoteOn(channel, midiNoteNumber, velocity);
	}

	/**
	 * Turns off the voice with the given MIDI note number.
	 * @param midiNoteNumber The MIDI note number to turn off.
	 */
	public void noteOff(int midiNoteNumber){
		if(midiNoteNumber>=0 && midiNoteNumber<=127){
			output.sendNoteOff(channel, midiNoteNumber, 0);
		}
	}
}
