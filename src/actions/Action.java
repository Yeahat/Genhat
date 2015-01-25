package actions;

import java.util.ArrayList;

import entities.Agent;

import world.World;

public interface Action {	
	/**
	 * Execute one step of the action
	 * 
	 * @param world the world which contains all information necessary for
	 * an action to be executed
	 */
	public void execute(Agent agent, World world, ArrayList<String> args);
	
	public boolean isFinished();
	
	public boolean requestInterrupt();
	
	public boolean isInterruptable();
}