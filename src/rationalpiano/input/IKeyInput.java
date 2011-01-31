package rationalpiano.input;

/**
 * Provides callback methods for pressing and releasing keyboard keys.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IKeyInput {

	/**
	 * Hook for a pressed key on the keyboard.
	 * Only reacts to this key if its state was released before (to prevent the OS' setting of auto key repeat from taking negative effect).
	 * Creates a new voice that corresponds to that key (see toNote()). 
	 * @param key The keyboard key's corresponding ASCII character, dependent of the OS' input locale settings.
	 */
	public abstract void keyPressed(char key);

	/**
	 * Hook for a released key on the keyboard.
	 * Releases the voice that corresponds to that key (see toNote()).
	 * @param key The keyboard key's corresponding ASCII character, dependent of the OS' input locale settings.
	 */
	public abstract void keyReleased(char key);

}