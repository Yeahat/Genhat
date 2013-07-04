package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;
import entities.Agent.direction;

import static entities.Agent.direction.*;

public class Step implements Action {

	boolean finishedStep = false;
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		direction stepDir;
		
		//invalid arguments, do nothing
		if (args.size() < 1)
		{
			System.out.println("Invalid arguments to action Step.");
			System.out.println("Step must take 1 argument denoting direction, as either: {up, down, left, right}");
			return;
		}
			
		String arg1 = args.get(0);
		if (arg1.equals("up"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(up);
				//TODO: Check if the step is valid (out of map bounds, location occupied by another Agent, an unpassable Thing, or non-air Terrain)
				
				world.moveAgent(agent, 0, 1, 0);
				agent.setOffsetY(-16);
				finishedStep = false;
				agent.setStepping(true);
			}
			
			agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() >= 0)
			{
				agent.setOffsetY(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else if (arg1.equals("down"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(down);
				//TODO: Check if the step is valid (out of map bounds, location occupied by another Agent, an unpassable Thing, or non-air Terrain)
				
				world.moveAgent(agent, 0, -1, 0);
				agent.setOffsetY(16);
				finishedStep = false;
				agent.setStepping(true);
			}

			agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() <= 0)
			{
				agent.setOffsetY(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else if (arg1.equals("left"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(left);
				//TODO: Check if the step is valid (out of map bounds, location occupied by another Agent, an unpassable Thing, or non-air Terrain)
				
				world.moveAgent(agent, -1, 0, 0);
				agent.setOffsetX(16);
				finishedStep = false;
				agent.setStepping(true);
			}
			
			agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() <= 0)
			{
				agent.setOffsetX(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else if (arg1.equals("right"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(right);
				//TODO: Check if the step is valid (out of map bounds, location occupied by another Agent, an unpassable Thing, or non-air Terrain)
				
				world.moveAgent(agent, 1, 0, 0);
				agent.setOffsetX(-16);
				finishedStep = false;
				agent.setStepping(true);
			}
			
			agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() >= 0)
			{
				agent.setOffsetX(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else
		{
			System.out.println("Invalid arguments to action Step.");
			System.out.println("Step must take 1 argument denoting direction, as either: {up, down, left, right}");
			return; //invalid arguments, do nothing
		}
	}

	@Override
	public boolean isFinished() 
	{
		return finishedStep;
	}

}
