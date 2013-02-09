package entities;

import java.util.ArrayList;

import world.World;
import actions.Action;

public abstract class Agent {
	//State information
	int x;
	int y;
	int z;
	ArrayList<Action> actions;
	Action currentAction;
	
	/**
	 * Constructor
	 */
	public Agent()
	{
		setActions();
	}
	
	/**
	 * Add the agent's actions to the action list, override this method to
	 * set the correct actions for each agent implementation
	 */
	private void setActions()
	{
		actions = new ArrayList<Action>();
	}
	
	/**
	 * Update the currentAction based on world information
	 * @param world the world
	 */
	public abstract void decideNextAction(World world);
	
	/**
	 * Execute the current action
	 * @param world the world information to be passed through to the action's execute call
	 */
	public void executeAction(World world)
	{
		if (currentAction == null)
			return;
		else
			currentAction.execute(world);
	}
}
