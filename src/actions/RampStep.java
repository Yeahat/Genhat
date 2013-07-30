package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;
import entities.Placeholder;
import entities.Agent.direction;

import static entities.Agent.direction.*;

public class RampStep implements Action {

	boolean finishedStep = true;
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{		
		//invalid arguments, do nothing
		if (args.size() < 2)
		{
			System.out.println("Invalid arguments to action RampStep.");
			System.out.println("RampStep must take 2 arguments denoting step direction, as either: {up, down, left, right},");
			System.out.println("and whether the agent is ascending or descending the ramp, as either {ascending, descending}");
			return;
		}
			
		//read arguments
		String arg1 = args.get(0);
		String arg2 = args.get(1);
		
		if (!agent.isRampAscending() && !agent.isRampDescending())
		{
			if (arg2.equals("descending"))
				agent.setPrepareRampAscend(false);
			else
				agent.setPrepareRampAscend(true);
		}
		
		if (arg1.equals("up"))
		{
			//Ascending a down-facing ramp
			if (agent.isPrepareRampAscend())
			{
				if (!agent.isRampAscending())
				{
					if (canStepRamp(agent, world, up, true))
					{
						world.moveAgent(agent, 0, 1, 1);
						agent.setOffsetY(-32);
						finishedStep = false;
						agent.setRampAscending(true);
					}
					else
					{
						finishedStep = true;
						return;
					}
				}
				
				agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
				if (agent.getOffsetY() >= 0)
				{
					agent.setOffsetY(0);
					agent.setRampAscending(false);
					finishedStep = true;
				}
			}
		}
		
		
		else if (arg1.equals("down"))
		{
			if (!agent.isPrepareRampAscend())
			{
				if (!agent.isRampDescending())
				{					
					//Descending a down-facing ramp
					int[] pos = agent.getPos();
					if (canStepRamp(agent, world, down, false))
					{
						//world.moveAgent(agent, 0, -1, -1);
						Placeholder holder = new Placeholder(pos[0], pos[1] - 1, pos[2] - 1);
						world.addAgent(holder);
						finishedStep = false;
						agent.setRampDescending(true);
					}
					else
					{
						finishedStep = true;
						return;
					}
				}
				
				agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
				if (agent.getOffsetY() <= -32)
				{
					agent.setOffsetY(0);
					int[] pos = agent.getPos();
					world.removeAgentAt(pos[0], pos[1] - 1, pos[2] - 1);
					world.moveAgent(agent, 0, -1, -1);
					agent.setRampDescending(false);
					finishedStep = true;
				}
			}
		}
		
		
		else if (arg1.equals("left"))
		{
			
		}
		
		
		else if (arg1.equals("right"))
		{
			
		}
		else
		{
			System.out.println("Invalid arguments to action RampStep.");
			System.out.println("RampStep must take 2 arguments denoting step direction, as either: {up, down, left, right},");
			System.out.println("and whether the agent is ascending or descending the ramp, as either {ascending, descending}");
			return; //invalid arguments, do nothing
		}
	}

	@Override
	public boolean isFinished() 
	{
		return finishedStep;
	}
	
	/**
	 * Determine whether it is possible to step to the next location, where that location is at the top
	 * of a ramp, incorporating bounds checking,
	 * collision checking with things and objects, and ensuring that the next location either has
	 * solid ground below it or a crossable thing on it
	 * 
	 * @param agent the agent taking the action
	 * @param world the world
	 * @param dir the direction of the ramp
	 * @param ascending true if ascending a ramp, false otherwise
	 * @return true if the agent can step in the given direction, false otherwise
	 */
	private boolean canStepRamp(Agent agent, World world, direction dir, boolean ascending)
	{
		int[] pos = agent.getPos();
		int x = pos[0];
		int y = pos[1];
		int z = pos[2];
		
		switch (dir)
		{
		case up:	y += 1;	break;
		case down:	y -= 1;	break;
		case left:	x -= 1;	break;
		case right:	x += 1;	break;
		}
		if (ascending)
			z += 1;
		else
			z -= 1;
		
		for (int k = z; k < pos[2] + agent.getHeight(); k ++)
		{
			//grid bounds check
			if (!world.isInBounds(x, y, k))
			{
				return false;
			}
			//collision check
			if (world.isBlocked(x, y, k))
			{
				return false;
			}
		}
		//ground check
		if (!world.isCrossable(x, y, z))
		{
			return false;
		}
		
		return true;
	}
}
