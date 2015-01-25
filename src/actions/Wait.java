package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;

public class Wait implements Action
{
	int frames = 0;
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		//initialize a new wait
		if (frames == 0)
		{
			if (args.size() < 1)
			{
				System.out.println("Invalid arguments to action Wait.");
				System.out.println("Wait must take one argument for how many frames to wait, as an integer.");
				return;
			}
			frames = Integer.parseInt(args.get(0));
			return;
		}
		
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
