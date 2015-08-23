package actions;

import utils.planners.PathPlannerUtils;
import world.GameState;
import world.Position;
import world.Map;
import entities.Agent;
import entities.Agent.Direction;
import entities.Hero;
import entities.Placeholder;
import static entities.Agent.Direction.*;

public class VerticalRampStep implements Action {

	private final Direction dir;
	private boolean initialized;
	private boolean finished;
	private boolean specialCase; //tracks special cases (step off for ascending, step on for descending)

	public VerticalRampStep(Direction dir)
	{
		this.dir = dir;
		this.initialized = false;
		this.finished = false;
	}
	
	@Override
	public void execute(Agent agent, Map world, GameState gameState)
	{		
		if (finished)
			return;
		
		//initialize ramp step
		if (!initialized)
		{
			Position pos;
			switch (dir)
			{
			case Up:
				//Ascending a vertical ramp
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, agent.getPos(), Up))
				{
					Position checkPos = new Position(agent.getPos());
					checkPos.y ++;
					//Regular case: moving upwards
					if (world.hasThing(checkPos) && world.getThingsAt(checkPos).hasRamp() && world.getThingsAt(checkPos).getRampDir() == Up)
					{
						world.moveAgent(agent, 0, 0, 1);
						agent.setOffsetY(-16);
						pos = agent.getPos();
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y, pos.z - 1));
						world.addAgent(h1);
						agent.setRampAscending(true);
						specialCase = false;
					}
					//Special case: stepping off of ramp
					else
					{
						world.moveAgent(agent, 0, 1, 0);
						agent.setOffsetY(-16);
						pos = agent.getPos();
						//set placeholder to render instead of agent (fixes a graphical glitch caused by tile render order)
						agent.setRenderOnPlaceholder(true);
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z));
						h1.setTransparent(true);
						world.addAgent(h1);
						agent.setRampAscending(true);
						specialCase = true;
					}
				}
				else
				{
					finished = true;
					return;
				}
			break;
				
			case Down:
				//Descending a vertical ramp
				pos = agent.getPos();
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, pos, Down))
				{
					//Special case: stepping on
					if (world.getTerrainAt(pos.x, pos.y, pos.z - 1).isBlocking() || (world.hasThing(pos) && world.getThingsAt(pos).isCrossable()))
					{
						world.moveAgent(agent, 0, -1, 0);
						agent.setOffsetY(16);
						pos = agent.getPos();
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y + 1, pos.z));
						world.addAgent(h1);
						agent.setRampDescending(true);
						specialCase = true;
					}
					//Regular case: moving downwards
					else
					{
						world.moveAgent(agent, 0, 0, -1);
						agent.setOffsetY(16);
						pos = agent.getPos();
						//set placeholder to render instead of agent (fixes a graphical glitch caused by tile render order)
						agent.setRenderOnPlaceholder(true);
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y, pos.z + 1));
						h1.setTransparent(true);
						world.addAgent(h1);
						agent.setRampDescending(true);
						specialCase = false;
					}
				}
				else
				{
					finished = true;
					return;
				}
			break;
				
			case Left:
				pos = agent.getPos();
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, pos, Left))
				{
					world.moveAgent(agent, -1, 0, 0);
					agent.setOffsetX(16);
					pos = agent.getPos();
					Placeholder h1 = new Placeholder(agent, new Position(pos.x + 1, pos.y, pos.z));
					world.addAgent(h1);
					agent.setRampAscending(true);
				}
				else
				{
					finished = true;
					return;
				}
			break;
				
			case Right:
				pos = agent.getPos();
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, pos, Right))
				{
					world.moveAgent(agent, 1, 0, 0);
					agent.setOffsetX(-16);
					pos = agent.getPos();
					Placeholder h1 = new Placeholder(agent, new Position(pos.x - 1, pos.y, pos.z));
					world.addAgent(h1);
					agent.setRampAscending(true);
				}
				else
				{
					finished = true;
					return;
				}
			break;
			}
			
			initialized = true;
		}
		
		Position pos;
		//Execute step (many cases...)
		switch (dir)
		{
		case Up:
			agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() >= 0)
			{
				swapFootstep(agent);
				pos = agent.getPos();
				if (specialCase)
				{
					world.removeAgentAt(pos.x, pos.y - 1, pos.z);
					agent.setRenderOnPlaceholder(false);
				}
				else
					world.removeAgentAt(pos.x, pos.y, pos.z - 1);
				agent.setOffsetY(0);
				agent.setRampAscending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock(gameState);
				world.updateCamera(gameState);
			}
		break;
		case Down:
			agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() <= 0)
			{
				pos = agent.getPos();
				if (specialCase)
					world.removeAgentAt(pos.x, pos.y + 1, pos.z);
				else
				{
					world.removeAgentAt(pos.x, pos.y, pos.z + 1);
					agent.setRenderOnPlaceholder(false);
				}
				swapFootstep(agent);
				agent.setOffsetY(0);
				agent.setRampDescending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock(gameState);
				world.updateCamera(gameState);
			}
		break;
			
		case Left:
			agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() <= 0)
			{
				pos = agent.getPos();
				world.removeAgentAt(pos.x + 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setRampAscending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock(gameState);
				world.updateCamera(gameState);
			}
		break;
		case Right:
			agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() >= 0)
			{
				pos = agent.getPos();
				world.removeAgentAt(pos.x - 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setRampAscending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock(gameState);
				world.updateCamera(gameState);
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
		String data = new String("VerticalRampStep:\n");
		data += dir.toString() + "," + initialized + "," + finished + "," + specialCase + "\n";
		data += "~VerticalRampStep\n";
		return data;
	}
	*/
	
	public static VerticalRampStep load(String data)
	{
		if (data.equals("null\n"))
			return null;

		VerticalRampStep verticalRampStep = new VerticalRampStep(Direction.valueOf(data.substring(0, data.indexOf(','))));
		data = data.substring(data.indexOf(',') + 1);
		verticalRampStep.initialized = Boolean.parseBoolean(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		verticalRampStep.finished = Boolean.parseBoolean(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		verticalRampStep.specialCase = Boolean.parseBoolean(data.substring(0, data.indexOf('\n')));
		
		return verticalRampStep;
	}
}
