package entities;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import world.World;
import actions.Action;
import actions.Idle;

public abstract class Agent {
	//State information
	int[] pos = new int[3]; //Position (x, y, z)
	int[] offset = new int[2]; //Pixel offset from current position (x, y, z)
	Action currentAction;
	ArrayList<String> args;
	
	//Actions: List all actions of this agent here
	Idle idle;
	
	//Textures
	protected Texture texture;
	
	/**
	 * Constructor
	 */
	public Agent()
	{
		setActions();
		args = new ArrayList<String>();
		offset[0] = 0;
		offset[1] = 0;
		
		loadTextures();
	}
	
	/**
	 * Add the agent's actions to the action list, override this method to
	 * set the correct actions for each agent implementation
	 */
	protected void setActions()
	{
		//instantiate actions here
		idle = new Idle();
		
		//set initial action
		currentAction = idle;
	}
	
	/**
	 * Setter for the current action
	 * @param action the new action to be set as current
	 */
	public void setCurrentAction(Action action)
	{
		currentAction = action;
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
		currentAction.execute(this, world, args);
	}
	
	/**
	 * Setter for offset
	 * @param newOffset new values for offset (x, y), must be of size 2 or it will do nothing
	 */
	public void setOffset(int[] newOffset)
	{
		if (newOffset.length < 2)
			return;
		offset[0] = newOffset[0];
		offset[1] = newOffset[1];
	}
	
	/**
	 * Incrementer for the x offset value
	 * @param value the value by which to increment x
	 */
	public void incrementXOffset(int value)
	{
		offset[0] += value;
	}
	
	/**
	 * Incrementer for the y offset value
	 * @param value the value by which to increment y
	 */
	public void incrementYOffset(int value)
	{
		offset[1] += value;
	}
	
	/**
	 * Setter for position
	 * @param newPos new values for position (x, y, z), must be of size 3 or it will do nothing
	 */
	public void setPos(int[] newPos)
	{
		if (newPos.length < 3)
			return;
		pos[0] = newPos[0];
		pos[1] = newPos[1];
		pos[2] = newPos[2];
	}
	
	/**
	 * Getter for position
	 */
	public int[] getPos()
	{
		return pos;
	}
	
	/**
	 * Load any required textures for displaying
	 */
	public abstract void loadTextures();
	
	/**
	 * Render the agent using OpenGL
	 */
	public abstract void renderAgent();
}
