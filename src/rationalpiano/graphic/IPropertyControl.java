package rationalpiano.graphic;

import rationalpiano.input.InputMidi;

/**
 * Displays mouse-tweakable controls to change parameters.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-30
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IPropertyControl extends IDrawable {

	/**
	 * @return true when the mouse is over one of the controls; false otherwise
	 */
	public abstract boolean isMouseOver();

	/**
	 * Sets the String-parameter control which controls the parameter for parameterName to the given alternatives.
	 * @param parameterName The name of the String-parameter.
	 * @param alternatives The possible alternatives for the given parameter.
	 */
	public abstract void setListItems(String parameterName, String[] alternatives);

	/**
	 * Adds the given object to the properties which can get changed.
	 * TODO make architecture more generic to be able to traverse parameter tweaks to all corresponding objects' methods
	 * @param inputMidi The InputMidi object which is used to change the open Midi Input device with the setMidiInputDevice() method.
	 */
	public abstract void addInputMidi(InputMidi inputMidi);

}