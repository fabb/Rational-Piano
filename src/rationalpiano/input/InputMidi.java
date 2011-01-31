package rationalpiano.input;

import java.util.logging.Logger;

import processing.core.PApplet;
import rationalpiano.noteout.INoteOutput;
import rationalpiano.voicemanagement.IVoices;

import rwmidi.Controller;
import rwmidi.Note;
import rwmidi.ProgramChange;
import rwmidi.RWMidi;
import rwmidi.MidiInput;
import rwmidi.SysexMessage;

/**
 * Receives Midi Notes and CC Messages for Sustain.
 * Translates presses to newVoice() and noteOn() and releases to releaseVoice() and noteOff().
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-30
 * @version 1.1
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class InputMidi {
	
	private MidiInput midiinput;
	private PApplet papplet;
	private IVoices voices;
	private INoteOutput noteoutput;
	
	private String chosenMidiInput;
	
	private static final Logger logger = Logger.getLogger(InputMidi.class.getName());

	/**
	 * Initializes the MIDI Input with the given MIDI device.
	 * @param papplet The processing applet.
	 * @param voices A Voices object for creating new voices and removing old ones.
	 * @param noteoutput The NoteOutput object to send note on/off messages to.
	 * @param midiInputDevice The partial and case-insensitive name of the MIDI Input device which should get opened.
	 */
	public InputMidi(PApplet papplet, IVoices voices, INoteOutput noteoutput, String midiInputDevice) {
		this.papplet = papplet;
		this.voices = voices;
		this.noteoutput = noteoutput;
		
		setMidiInputDevice(midiInputDevice);
	}
	
	/**
	 * @return The String names of all Midi Input Devices
	 */
	public String[] getMidiInputDevices(){
		return RWMidi.getInputDeviceNames();
	}
	
	/**
	 * Sets the Midi Input Device to the given one.
	 * This method gets automatically called upon construction, but can also be used to change the device later on.
	 * @param midiInputDevice The partial and case-insensitive name of the MIDI Input device which should get opened.
	 */
	public void setMidiInputDevice(String midiInputDevice) {
		try{
			//close previously opened device
			midiinput.closeMidi();
		}catch(NullPointerException e){
		}
		
		String devices[] = RWMidi.getInputDeviceNames();
		
		logger.config("Available MIDI Input Devices:");
		for(String device : devices){
			logger.config("    " + device);
		}
		logger.config("Searching for MIDI Device containing string '" + midiInputDevice + "'");
		
		int i;
		for(i=0; i<devices.length; i++){
			if(devices[i].toLowerCase().contains(midiInputDevice.toLowerCase())){
				logger.config("MIDI Device chosen: '" + devices[i] + "'");
				chosenMidiInput = devices[i];
				break;
			}
		}
		if(i==devices.length){
			i--;
			logger.severe("MIDI Device not found, choosing last device: '" + devices[i] + "'");
			chosenMidiInput = devices[i];
		}

		midiinput = RWMidi.getInputDevices()[i].createInput(); //FIXME exception can happen here but the library catches it (as it is a processing library) => modify library to throw the exeption
		
		midiinput.plug(this);
	}

	/**
	 * Handles incoming Note On messages.
	 * Translates these calls to according method calls in the IVoices and INoteOutput objects.
	 * @param note The received Note On message.
	 */
	public void noteOnReceived(Note note){
		if(note.getVelocity() == 0){
			noteOffReceived(note);
			return;
		}
		else{
			noteoutput.noteOn(note.getPitch(), (double)note.getVelocity()/127);
			voices.newVoice(note.getPitch(),(double)note.getVelocity()/127);
			//logger.config("Note On: Pitch = " + note.getPitch() + ", Velocity = " + note.getVelocity());
		}
	}
	
	/**
	 * Handles incoming Note Off messages.
	 * Translates these calls to according method calls in the IVoices and INoteOutput objects.
	 * @param note The received Note Off message.
	 */
	public void noteOffReceived(Note note){
		noteoutput.noteOff(note.getPitch());
		voices.releaseVoice(note.getPitch());
		//logger.config("Note Off: Pitch = " + note.getPitch());
	}
	
	/**
	 * Handles incoming CC messages.
	 * Only pays attention to Sustain (damper) messages which are CC64 and forwards them to the IVoices and INoteOutput objects.
	 * @param controller The received CC message.
	 */
	public void controllerChangeReceived(Controller controller){
		if(controller.getCC() == 64){
			noteoutput.sustain((double)controller.getValue() / 127);
			if(controller.getValue() >= 64){
				voices.setSustain(true);
			}else{
				voices.setSustain(false);
			}
		}
	}
	
	/**
	 * Handles incoming Program Change messages, in this case ignore them.
	 * @param programchange The received Program Change message.
	 */
	public void programChangeReceived(ProgramChange programchange){
		//do nothing
	}
	
	/**
	 * Handles incoming Sysex messages, in this case ignore them.
	 * @param sysexmessage The received Sysex message.
	 */
	public void sysexReceived(SysexMessage sysexmessage){
		//do nothing
	}

}
