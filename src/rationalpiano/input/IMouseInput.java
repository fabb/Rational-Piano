package rationalpiano.input;

/**
 * Provides callback methods for pressing and releasing mouse buttons.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface IMouseInput {

	/**
	 * Hook to be called when a mouse button has been pressed.
	 * Creates a new voice that corresponds with the Line at the press coordinates.
	 * @param mouseX The X-coordinate position in Pixels on the PApplet window where the mouse button has been pressed at. Leftmost pixel = 0.
	 * @param mouseY The Y-coordinate position in Pixels on the PApplet window where the mouse button has been pressed at. Topmost pixel = 0.	 
	 * @param mouseButton An integer representing the mouse button. 37 == left mouse button, 39 == right mouse button, 3 == middle mouse button
	 */
	public abstract void mousePressed(int mouseX, int mouseY, int mouseButton);

	/**
	 * Hook to be called when a mouse button has been released.
	 * Releases the last via mouse click created voice.
	 * @param mouseX The X-coordinate position in Pixels on the PApplet window where the mouse button has been released at. Leftmost pixel = 0.
	 * @param mouseY The Y-coordinate position in Pixels on the PApplet window where the mouse button has been released at. Topmost pixel = 0.
	 * @param mouseButton An integer representing the mouse button. 37 == left mouse button, 39 == right mouse button, 3 == middle mouse button
	 */
	public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

}