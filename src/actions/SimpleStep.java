package actions;

import java.util.ArrayList;

import utils.planners.PathPlanners;
import world.Position;
import world.World;
import entities.Agent;
import entities.Hero;
import entities.Placeholder;
import static entities.Agent.direction.*;

public class SimpleStep implements Action {

	boolean finishedStep = true;
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		//invalid arguments, do nothing
		if (args.size() < 1)
		{
			System.out.println("Invalid arguments to action SimpleStep.");
			System.out.println("SimpleStep must take 1 argument denoting direction, as either: {up, down, left, right}");
			return;
		}
			
		String arg1 = args.get(0);
		
		if (arg1.equals("up"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(up);
				
				if(PathPlanners.canStep(agent, world, agent.getPos(), up))
				{
					world.moveAgent(agent, 0, 1, 0);
					agent.setOffsetY(-16);
					Position pos = agent.getPos();
					//set placeholder to render instead of agent (fixes a graphical glitch caused by tile render order)
					agent.setRenderOnPlaceholder(true);
					Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z));
					h1.setTransparent(true);
					world.addAgent(h1);
					finishedStep = false;
					agent.setStepping(true);
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
				swapFootstep(agent);
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x, pos.y - 1, pos.z);
				agent.setOffsetY(0);
				agent.setRenderOnPlaceholder(false);
				agent.setStepping(false);
				finishedStep = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		}
		else if (arg1.equals("down"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(down);

				if(PathPlanners.canStep(agent, world, agent.getPos(), down))
				{
					world.moveAgent(agent, 0, -1, 0);
					agent.setOffsetY(16);
					Position pos = agent.getPos();
					Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y + 1, pos.z));
					world.addAgent(h1);
					finishedStep = false;
					agent.setStepping(true);
				}
				else
				{
					finishedStep = true;
					return;
				}
			}

			agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() <= 0)
			{
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x, pos.y + 1, pos.z);
				swapFootstep(agent);
				agent.setOffsetY(0);
				agent.setStepping(false);
				finishedStep = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		}
		else if (arg1.equals("left"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(left);

				if(PathPlanners.canStep(agent, world, agent.getPos(), left))
				{
					world.moveAgent(agent, -1, 0, 0);
					agent.setOffsetX(16);
					Position pos = agent.getPos();
					Placeholder h1 = new Placeholder(agent, new Position(pos.x + 1, pos.y, pos.z));
					world.addAgent(h1);
					finishedStep = false;
					agent.setStepping(true);
				}
				else
				{
					finishedStep = true;
					return;
				}
			}
			
			agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() <= 0)
			{
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x + 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setStepping(false);
				finishedStep = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		}
		else if (arg1.equals("right"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(right);

				if(PathPlanners.canStep(agent, world, agent.getPos(), right))
				{
					world.moveAgent(agent, 1, 0, 0);
					agent.setOffsetX(-16);
					Position pos = agent.getPos();
					Placeholder h1 = new Placeholder(agent, new Position(pos.x - 1, pos.y, pos.z));
					world.addAgent(h1);
					finishedStep = false;
					agent.setStepping(true);
				}
				else
				{
					finishedStep = true;
					return;
				}
			}
			
			agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() >= 0)
			{
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x - 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setStepping(false);
				finishedStep = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock();
				world.updateCamera();
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
	
	
		
	private void swapFootstep(Agent agent)
	{
		if (agent.getFootstep() == right)
		{	
			agent.setFootstep(left);
		}
		else
		{
			agent.setFootstep(right);
		}
	}

	@Override
	public boolean requestInterrupt() {
		return finishedStep;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
}
