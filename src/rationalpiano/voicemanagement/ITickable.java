package rationalpiano.voicemanagement;

/**
 * Provides a function wich allows to do (periodic) recalculations
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-15
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public interface ITickable {

	/**
	 * Advances one frame causing recalculation.
	 */
	public abstract void tick();

}