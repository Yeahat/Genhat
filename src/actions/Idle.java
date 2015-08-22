package actions;

import entities.Agent;

import world.Map;

public class Idle implements Action {

	/**
	 * Do nothing, this is the default idle action
	 */
	@Override
	public void execute(Agent agent, Map world) 
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
	
	/* Actions are not currently saved, keeping this here in case they ever are...
	@Override
	public String save()
	{
		return "Idle:\n~Idle\n";
	}
	*/
	
	public Idle load(String data)
	{
		if (data.equals("null\n"))
			return null;
		
		return new Idle();
	}
}
