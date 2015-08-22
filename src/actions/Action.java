package actions;

import entities.Agent;
import world.Map;

public interface Action {	
	/**
	 * Execute one step of the action
	 * 
	 * @param agent the agent executing the action
	 * @param world the world which contains all information necessary for
	 * an action to be executed
	 */
	public void execute(Agent agent, Map world);
	
	public boolean isFinished();
	
	public boolean requestInterrupt();
	
	public boolean isInterruptable();
	
	/* Actions are not currently saved, keeping this here in case they ever are...
	public String save();
	*/
}