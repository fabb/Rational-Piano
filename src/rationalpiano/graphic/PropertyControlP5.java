package rationalpiano.graphic;

import java.io.IOException;

import rationalpiano.input.InputMidi;
import rationalpiano.persistence.ConfigurationData;
import processing.core.PApplet;
import controlP5.*;

/**
 * Displays mouse-tweakable controls to change parameters.
 * Allows tweaking of Midi Input device.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-30
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class PropertyControlP5 implements IPropertyControl, ControlListener {

	private PApplet papplet;
	private ControlP5 cP5;

	private DropdownList midiInputList;
	private InputMidi midiInput;
	private ConfigurationData config;

	/**
	 * Creates all new parameter controls on screen and registers the control event callback handler with the PApplet instance.
	 * @param papplet The processing applet to register the control event callback method.
	 * @param config The ConfigurationData object which allows tweaking and saving of prperties.
	 */
	public PropertyControlP5(PApplet papplet, ConfigurationData config) {
		this.papplet = papplet;
		this.config = config;
		
		cP5 = new controlP5.ControlP5(papplet);
		
		setup();
		
		cP5.addListener(this);
	}

	
	/**
	 * Configures the parameter controls.
	 * At the moment just sets up a drop down list for Midi Input.
	 */
	private void setup() {
		int x_left = 0;
		int y_top = 22; //OSX menu bar is most commonly 22 pixels in height and even shows up in Processing's full screen mode 
		int select_height = 15;
		int list_height = 300;
		int width = 200;
		int item_height = 20;
		
		int backgroundColor = papplet.color(190);
		int colorBackground = papplet.color(60);
		int colorActive = papplet.color(255,128);
		
		String label = "MIDI Input Device";
		
		midiInputList = cP5.addDropdownList("MIDI Input", x_left, y_top+select_height, width, list_height);
		
		midiInputList.setBackgroundColor(backgroundColor);
		midiInputList.setColorBackground(colorBackground);
		midiInputList.setColorActive(colorActive);

		//midiInputList.setHeight(210); //set the height of a pulldown menu, should always be a multiple of itemHeight
		midiInputList.setItemHeight(item_height);
		midiInputList.setBarHeight(select_height);
		
		midiInputList.captionLabel().set(label);
		midiInputList.captionLabel().style().marginTop = 3;
		midiInputList.captionLabel().style().marginLeft = 3;
		
		midiInputList.valueLabel().style().marginTop = 3;

		/*
		String[] items = {"a","b","c"};
		int id = 0;
		for(String s : items){
			midiInputList.addItem(s, id++);
		}
		*/
	}

	/**
	 * Callback method which gets called from the PApplet instance when one of the parameter controls is tweaked
	 * @param receivedEvent The received event.
	 */
	@Override
	public void controlEvent(ControlEvent receivedEvent) {
		if(receivedEvent.isGroup()){
			// check if the Event was triggered from a ControlGroup
			//System.out.println("Group\n" + receivedEvent.group().value() + " from " + receivedEvent.group());
			//System.out.println(receivedEvent.group().captionLabel());
			String selectedMidiInputDevice = receivedEvent.group().captionLabel().toString();
			midiInput.setMidiInputDevice(selectedMidiInputDevice);
			config.midiInputDevice = selectedMidiInputDevice;
			try {
				config.saveData();
			} catch (IOException e) {
				//ignore
				e.printStackTrace();
			}
		}else if (receivedEvent.isController()){
			//System.out.println("Controller\n" + receivedEvent.controller().value() + " from " + receivedEvent.controller());
		}
	}

	@Override
	public void draw() {
		//System.out.println(cP5.window(papplet).isMouseOver());
		cP5.draw();
	}

	@Override
	public boolean isMouseOver() {
		//FIXME there is a documented bug in the library which makes it not work for groups yet, when the mouse is hovering the dropdown box (not the elements), this still returns false
		return cP5.window(papplet).isMouseOver();
	}


	@Override
	public void setListItems(String parameterName, String[] alternatives) {
		if(parameterName == "midiInput"){
			//remove previously contained items
			midiInputList.clear();
			
			int id = 0;
			for(String s : alternatives){
				midiInputList.addItem(s, id++);
			}
		}
		//TODO add and handle more parameters
	}

	@Override
	public void addInputMidi(InputMidi inputMidi) {
		midiInput = inputMidi;
	}
}
