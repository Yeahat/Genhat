package actions;

import world.World;

public interface Action {
	/**
	 * Execute one step of the action
	 * 
	 * @param world the world which contains all information necessary for
	 * an action to be executed
	 */
	public void execute(World world);
}
