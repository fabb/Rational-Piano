package rationalpiano.input;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import processing.core.PApplet;
import rationalpiano.graphic.IGraphicControls;
import rationalpiano.noteout.INoteOutput;
import rationalpiano.voicemanagement.IVoices;
import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

/**
 * Manages TUIO Multitouch input and de/activates the according voices and notes
 * 
 * @author Fabian Ehrentraud
 * @date 2010-10-10
 * @version 1.02
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class TuioInput implements TuioListener {
	
	private PApplet papplet;
	
	//TuioProcessing tuioClient; //won't allow the methods here in this class, TuioListener Interface is being used instead
	private TuioClient tuioClient;
	private int tuioPort;

	private IVoices voices;
	private INoteOutput noteoutput;
	private IGraphicControls graphiccontrols;
	
	//key is sessionid and object is midi note number
	private ConcurrentHashMap<Long, Integer> activeKeys = new ConcurrentHashMap<Long, Integer>();
	
	private static final Logger logger = Logger.getLogger(TuioInput.class.getName());
	
	
	/**
	 * Initializes the TUIO receiver.
	 * @param papplet The processing applet to get the size of for scaling TUIO input
	 * @param voices The Voices object to add/remove voices to/from.
	 * @param noteoutput The NoteOutput object to send note on/off messages to.
	 * @param graphiccontrols The IGraphicControlsPassive object to ask for line positions.
	 * @param tuioPort The local UDP port the TUIO Listener should listen at
	 */
	public TuioInput(PApplet papplet, IVoices voices, INoteOutput noteoutput, IGraphicControls graphiccontrols, int tuioPort) {
		logger.info("Setting up TUIO input");
		this.papplet = papplet;
		this.tuioPort = tuioPort;
		this.voices = voices;
		this.noteoutput = noteoutput;
		this.graphiccontrols = graphiccontrols;

		tuioClient = new TuioClient(tuioPort);
		tuioClient.addTuioListener(this);
		tuioClient.connect();
	}
	
	/**
	 * Close socket when quitting.
	 * FIXME won't get called - but actually it isn't needed either
	 */
	@Override
	public void finalize(){
		if(tuioClient.isConnected()){
			logger.info("Closing TUIO port");
			tuioClient.disconnect();
		}
	}
	
	//The following callback methods are called whenever a TUIO event occurs

	/**
	 * Called when an object is added to the scene.
	 * This is being ignored in this application.
	 * @param tobj Object that has been added.
	 */
	@Override
	public void addTuioObject(TuioObject tobj) {
		//PApplet.println("add object " + tobj.getSymbolID() + " (" + tobj.getSessionID() + ") " + tobj.getX() + " " + tobj.getY() + " " + tobj.getAngle());
	}

	/**
	 * Called when an object is removed from the scene.
	 * This is being ignored in this application.
	 * @param tobj Object that has been removed.
	 */
	@Override
	public void removeTuioObject(TuioObject tobj) {
		//PApplet.println("remove object " + tobj.getSymbolID() + " (" + tobj.getSessionID() + ")");
	}

	/**
	 * Called when an object is moved.
	 * This is being ignored in this application.
	 * @param tobj Object that has been moved.
	 */
	@Override
	public void updateTuioObject(TuioObject tobj) {
		//PApplet.println("update object " + tobj.getSymbolID() + " (" + tobj.getSessionID() + ") " + tobj.getX() + " " + tobj.getY() + " " + tobj.getAngle() + " " + tobj.getMotionSpeed() + " " + tobj.getRotationSpeed() + " " + tobj.getMotionAccel() + " " + tobj.getRotationAccel());
	}

	/**
	 * Called when a cursor is added to the scene.
	 * This adds a new voice corresponding to the line at the pressed point.
	 * @param tcur Cursor that has been added.
	 */
	@Override
	public void addTuioCursor(TuioCursor tcur) {
		//PApplet.println("add cursor " + tcur.getCursorID() + " (" + tcur.getSessionID() + ") " + tcur.getX() + " " + tcur.getY());
		
		int note = graphiccontrols.getGraphicVisualizationElementArray().getElementNote((int)(tcur.getX() * papplet.screenWidth - (papplet.getBounds().getX() + papplet.frame.getBounds().getX())), (int)(tcur.getY() * papplet.screenHeight - (papplet.getBounds().getY() + papplet.frame.getBounds().getY())));
		
		if(note != -1){
			noteoutput.noteOn(note, 1);
			if(voices.newVoice(note, 1) == true){
				activeKeys.put(tcur.getSessionID(), note);
			}
		}

	}

	/**
	 * Called when a cursor is moved.
	 * This is being ignored in this application.
	 * @param tcur Cursor that has been moved
	 */
	@Override
	public void updateTuioCursor(TuioCursor tcur) {
		//PApplet.println("update cursor " + tcur.getCursorID() + " (" + tcur.getSessionID() + ") " + tcur.getX() + " " + tcur.getY() + " " + tcur.getMotionSpeed() + " " + tcur.getMotionAccel());
	}

	/**
	 * Called when a cursor is removed from the scene.
	 * This releases the according voice.
	 * @param tcur Cursor that has been removed.
	 */
	@Override
	public void removeTuioCursor(TuioCursor tcur) {
		//PApplet.println("remove cursor " + tcur.getCursorID() + " (" + tcur.getSessionID() + ")");
		
		if(activeKeys.containsKey(tcur.getSessionID())){
			int note = activeKeys.get(tcur.getSessionID());
			
			noteoutput.noteOff(note);
			voices.releaseVoice(note);
			activeKeys.remove(tcur.getSessionID());
		}
	}

	/**
	 * Called after each message bundle representing the end of an image frame.
	 * This is being ignored in this application.
	 * @param bundleTime Time that the whole bundle has been sent at.
	 */
	@Override
	public void refresh(TuioTime bundleTime) {
		//papplet.redraw();
	}

}
