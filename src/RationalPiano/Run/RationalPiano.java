package RationalPiano.Run;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import RationalPiano.Graphic.GraphicControls;
import RationalPiano.Graphic.IDrawable;
import RationalPiano.Graphic.IGraphicControls;
import RationalPiano.Input.InputDevs;
import RationalPiano.Input.IKeyInput;
import RationalPiano.Input.IMouseInput;
import RationalPiano.Logging.RationalLogger;
import RationalPiano.NoteOut.NoteOutput;
import RationalPiano.NoteOut.INoteOutput;
import RationalPiano.Persistence.ConfigurationData;
import RationalPiano.VoiceManagement.Voices;
import RationalPiano.VoiceManagement.IVoices;
import RationalPiano.VoiceManagement.ITickable;

/**
 * The main class for the RationalPiano application.
 * Do not run as a Java Applet as there is some configuration done in main().
 * Serves all needed functions of PApplet and forwards such method calls to generated objects which are responsible for dedicated functions like input, note output, graphic controls and voice management.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-30
 * @version 1.06
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class RationalPiano extends PApplet {
	
	private static final long serialVersionUID = 1L;
	
	private INoteOutput noteoutput;
	private IGraphicControls graphiccontrols;
	private IDrawable graphicdraw;
	private InputDevs input;
	private IKeyInput keyInput;
	private IMouseInput mouseInput;
	private IVoices voices;
	private ITickable voicestick;
	
	private static ConfigurationData config;
	private static final String saveFileName = "RationalPianoSettings.cfg";
	
	private static final Logger logger = Logger.getLogger(RationalPiano.class.getName());
	private static Level loglevel = Level.CONFIG;
	private static boolean logToFile = false;
	private static String logFileName = "RationalPianoLogfile.txt";
	
	/**
	 * @param args Passed arguments.
	 */
	public static void main(String[] args) {
		/*
		System.out.println(args.length + " Arguments");
		for(String s : args)
			System.out.println(" " + s);
		*/
		/** /
		if(args.length>0){
			try{
				loglevel = Level.parse(args[0].toUpperCase());
			}catch(IllegalArgumentException e){
				System.err.println("Error: Couldn't match first argument '" + args[0] + "' to a Logging Level. Using '" + loglevel.getName() + "' instead.");
			}
		}
		/**/
		
		try{
			RationalLogger.initiate(loglevel, logToFile, logFileName);
		} catch (IOException ex){
			System.err.println("Error: Couldn't initialize Logger");
		}
		
		//loading the settings must take place BEFORE starting in fullscreen mode when using --present
		config = new ConfigurationData(saveFileName);

		if(config.fullscreen){
			PApplet.main(new String[] { "--present", "RationalPiano.Run.RationalPiano" });
		}else{
			PApplet.main(new String[] { "RationalPiano.Run.RationalPiano" });
		}
	}

	/**
	 * Called the first time the applet starts.
	 */
	@Override
	public void setup() {
		//take care which statements you put before size() or else the code in setup() gets executed twice
		graphiccontrols = new GraphicControls(this, config, config.fullscreen, config.vertical_scaling, config.width, config.height, config.framerate, config.notecount, config.notestart, config.lineBend, config.backgroundColorHue, config.backgroundColorSaturation, config.backgroundColorBrightness, config.lineColorHueInactive, config.lineColorHueActive, config.lineColorSaturation, config.lineColorBrightness);
		graphicdraw = graphiccontrols;

		noteoutput = new NoteOutput(this, config.outputMode, config.oscport, config.midiOutputDevice, config.midiChannel);

		voices = new Voices(this, graphiccontrols, config.framerate, config.attack, config.decay, config.sustain, config.release, config.holdSustain, config.maxfrac, config.bellWidth);
		voicestick = voices;
		
		input = new InputDevs(this, voices, noteoutput, graphiccontrols, config.tuioPort, config.midiInputDevice);
		keyInput = input;
		mouseInput = input;
		
		//TODO make configuration traversal more elaborate
		graphiccontrols.getParameterControl().addInputMidi(input.getInputMidi());
		graphiccontrols.getParameterControl().setListItems("midiInput", input.getInputMidi().getMidiInputDevices());
		
		logger.info("Ready");
	}

	/**
	 * Periodically called with interval of the framerate.
	 */
	@Override
	public void draw() {
		voicestick.tick();
		graphicdraw.draw();
	}

	/**
	 * Called when any mouse button is pressed.
	 */
	@Override
	public void mousePressed() {
		mouseInput.mousePressed(mouseX,mouseY,mouseButton);
	}
	
	/**
	 * Called when any mouse button is released.
	 */
	@Override
	public void mouseReleased() {
		mouseInput.mouseReleased(mouseX,mouseY,mouseButton);
	}
	
	/**
	 * Called when any keyboard key is pressed.
	 * When a key is held down this will get called periodically with the OS's setting for automatic key repeat.
	 * But in this case keyReleased() won't get called inbetween. 
	 */
	@Override
	public void keyPressed() {
		keyInput.keyPressed(key);
	}
	
	/**
	 * Called when any keyboard key is released.
	 */
	@Override
	public void keyReleased() {
		keyInput.keyReleased(key);
	}

	/**
	 * Called when the applet is quit.
	 */
	@Override
	public void stop() {
		logger.info("Exiting");
		
		super.stop();
	}
	
}
