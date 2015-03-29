package actions;

import java.util.ArrayList;

import utils.planners.PathPlannerUtils;
import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;
import entities.Hero;
import entities.Placeholder;
import static entities.Agent.direction.*;

public class SimpleStep implements Action {

	private final direction dir;
	private boolean initialized;
	private boolean finished;
	
	public SimpleStep(direction dir)
	{
		this.dir = dir;
		initialized = false;
		finished = false;
	}
	
	@Override
	public void execute(Agent agent, World world)
	{
		if (finished)
			return;
		
		if (!initialized)
		{
			Placeholder h1;
			switch (dir)
			{
			case up:
				agent.setDir(up);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), up))
				{
					world.moveAgent(agent, 0, 1, 0);
					agent.setOffsetY(-16);
					Position pos = agent.getPos();
					//set placeholder to render instead of agent (fixes a graphical glitch caused by tile render order)
					agent.setRenderOnPlaceholder(true);
					h1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z));
					h1.setTransparent(true);
				}
				else
				{
					finished = true;
					return;
				}
			break;
			case down:
				agent.setDir(down);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), down))
				{
					world.moveAgent(agent, 0, -1, 0);
					agent.setOffsetY(16);
					Position pos = agent.getPos();
					h1 = new Placeholder(agent, new Position(pos.x, pos.y + 1, pos.z));
				}
				else
				{
					finished = true;
					return;
				}
			break;
			case left:
				agent.setDir(left);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), left))
				{
					world.moveAgent(agent, -1, 0, 0);
					agent.setOffsetX(16);
					Position pos = agent.getPos();
					h1 = new Placeholder(agent, new Position(pos.x + 1, pos.y, pos.z));
				}
				else
				{
					finished = true;
					return;
				}
			break;
			case right:
				agent.setDir(right);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), right))
				{
					world.moveAgent(agent, 1, 0, 0);
					agent.setOffsetX(-16);
					Position pos = agent.getPos();
					h1 = new Placeholder(agent, new Position(pos.x - 1, pos.y, pos.z));
				}
				else
				{
					finished = true;
					return;
				}
			break;
			default:
				h1 = null;	//unreachable unless dir was set to null, which would break everything regardless...
			}
			
			world.addAgent(h1);
			agent.setStepping(true);
			initialized = true;
		}
		
		switch (dir)
		{
		case up:
			agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() >= 0)
			{
				swapFootstep(agent);
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x, pos.y - 1, pos.z);
				agent.setOffsetY(0);
				agent.setRenderOnPlaceholder(false);
				agent.setStepping(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
		case down:
			agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() <= 0)
			{
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x, pos.y + 1, pos.z);
				swapFootstep(agent);
				agent.setOffsetY(0);
				agent.setStepping(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
		case left:
			agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() <= 0)
			{
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x + 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setStepping(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
		case right:
			agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() >= 0)
			{
				Position pos = agent.getPos();
				world.removeAgentAt(pos.x - 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setStepping(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
		}
	}

	@Override
	public boolean isFinished() 
	{
		return finished;
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
		return finished;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
}
