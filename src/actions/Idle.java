package actions;

import entities.Agent;

import world.World;

public class Idle implements Action {

	/**
	 * Do nothing, this is the default idle action
	 */
	@Override
	public void execute(Agent agent, World world) 
	{
		return;
	}
	
	/**
	 * The action is always finished
	 * @return true always
	 */
	@Override
	public boolean isFinished()
	{
		return true;
	}

	@Override
	public boolean requestInterrupt() {
		return true;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
}
