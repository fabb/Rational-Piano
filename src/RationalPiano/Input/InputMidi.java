package RationalPiano.Input;

import java.util.logging.Logger;

import processing.core.PApplet;
import RationalPiano.Graphic.GraphicControls;
import RationalPiano.NoteOut.SendMidi;
import RationalPiano.VoiceManagement.Voices;

import rwmidi.Controller;
import rwmidi.Note;
import rwmidi.ProgramChange;
import rwmidi.RWMidi;
import rwmidi.MidiInput;
import rwmidi.SysexMessage;

/**
 * XXX
 * 
 * @author Fabian Ehrentraud
 * @date 2010-09-24
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class InputMidi {
	
	private MidiInput midiinput;
	private PApplet papplet;
	private Voices voices;
	
	private static final Logger logger = Logger.getLogger(SendMidi.class.getName());

	/**
	 * XXX
	 * @param papplet
	 * @param voices
	 * @param midiDevice
	 */
	public InputMidi(PApplet papplet, Voices voices, String midiDevice) {
		// TODO Auto-generated constructor stub
		this.papplet = papplet;
		this.voices = voices;
		
		String devices[] = RWMidi.getInputDeviceNames();
		
		logger.config("Available MIDI Input Devices:");
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

		midiinput = RWMidi.getInputDevices()[i].createInput(); //FIXME exception can happen here but the library catches it (as it is a processing library) => modify library to throw the exeption
		
		midiinput.plug(this);
	}
	
	/**
	 * XXX
	 * @param note
	 */
	public void noteOnReceived(Note note){
		if(note.getVelocity() == 0){
			noteOffReceived(note);
			return;
		}
		else{
			voices.newVoice(note.getPitch(),(double)note.getVelocity()/127);
			//logger.config("Note On: Pitch = " + note.getPitch() + ", Velocity = " + note.getVelocity());
		}
	}
	
	/**
	 * XXX
	 * @param note
	 */
	public void noteOffReceived(Note note){
		voices.releaseVoice(note.getPitch());
		//logger.config("Note Off: Pitch = " + note.getPitch());
	}
	
	/**
	 * XXX
	 * @param controller
	 */
	public void controllerChangeReceived(Controller controller){
		//do nothing
	}
	
	/**
	 * XXX
	 * @param programchange
	 */
	public void programChangeReceived(ProgramChange programchange){
		//do nothing
	}
	
	public void sysexReceived(SysexMessage sysexmessage){
		//do nothing
	}

}
