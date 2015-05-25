package actions;

import world.Map;
import entities.Agent;

public class Wait implements Action
{
	private int frames;
	
	public Wait(int frames)
	{
		this.frames = frames;
	}
	
	@Override
	public void execute(Agent agent, Map world)
	{
		if (frames > 0)
			frames --;
	}

	@Override
	public boolean isFinished()
	{
		return (frames == 0);
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
