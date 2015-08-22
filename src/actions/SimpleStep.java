package actions;

import utils.planners.PathPlannerUtils;
import world.Position;
import world.Map;
import entities.Agent;
import entities.Agent.Direction;
import entities.Hero;
import entities.Placeholder;
import static entities.Agent.Direction.*;

public class SimpleStep implements Action {

	private final Direction dir;
	private boolean initialized;
	private boolean finished;
	
	public SimpleStep(Direction dir)
	{
		this.dir = dir;
		initialized = false;
		finished = false;
	}
	
	@Override
	public void execute(Agent agent, Map world)
	{
		if (finished)
			return;
		
		if (!initialized)
		{
			Placeholder h1;
			switch (dir)
			{
			case Up:
				agent.setDir(Up);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), Up))
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
			case Down:
				agent.setDir(Down);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), Down))
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
			case Left:
				agent.setDir(Left);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), Left))
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
			case Right:
				agent.setDir(Right);
				if(PathPlannerUtils.canStep(agent, world, agent.getPos(), Right))
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
		case Up:
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
		case Down:
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
		case Left:
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
		case Right:
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
		if (agent.getFootstep() == Right)
		{	
			agent.setFootstep(Left);
		}
		else
		{
			agent.setFootstep(Right);
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
	
	/* Actions are not currently saved, keeping this here in case they ever are...
	@Override
	public String save()
	{
		String data = new String("SimpleStep:\n");
		data += dir.toString() + "," + initialized + "," + finished + "\n";
		data += "~SimpleStep\n";
		return data;
	}
	*/
	
	public static SimpleStep load(String data)
	{
		if (data.equals("null\n"))
			return null;

		SimpleStep simpleStep = new SimpleStep(Direction.valueOf(data.substring(0, data.indexOf(','))));
		data = data.substring(data.indexOf(',') + 1);
		simpleStep.initialized = Boolean.parseBoolean(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		simpleStep.finished = Boolean.parseBoolean(data.substring(0, data.indexOf('\n')));
		
		return simpleStep;
	}
}
