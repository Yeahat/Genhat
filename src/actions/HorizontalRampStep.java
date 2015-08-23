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
import static actions.HorizontalRampStep.SteppingState.*;

public class HorizontalRampStep implements Action {

	private final Direction dir;
	private final boolean ascending;
	private boolean initialized;
	private boolean finished;
	private SteppingState steppingState;

	public enum SteppingState
	{
		On, Off, Cont
	}
	
	public HorizontalRampStep(Direction dir, boolean ascending)
	{
		this.dir = dir;
		this.ascending = ascending;
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
			switch (dir)
			{
			case Up:
				finished = true;
				return;
				
			case Down:
				finished = true;
				return;
				
			case Left:
				//Ascending a right-facing ramp
				if (ascending)
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Left, true, false))
						{
							steppingState = On;
							agent.setOnRamp(true);
							world.moveAgent(agent, -1, 0, 1);
							agent.setOffsetX(16);
							agent.setOffsetY(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							agent.setRampAscending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x - 1, pos.y, pos.z) || !world.getThingsAt(pos.x - 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Left, true, false))
						{
							steppingState = Off;
							world.moveAgent(agent, -1, 0, 0);
							agent.setOffsetX(16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z));
							world.addAgent(h1);
							agent.setRampAscending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x - 1, pos.y, pos.z) && world.getThingsAt(pos.x - 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Left, true, false))
						{
							steppingState = Cont;
							world.moveAgent(agent, -1, 0, 1);
							agent.setOffsetX(16);
							agent.setOffsetY(-20);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							agent.setRampAscending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
				}
				
				//Descending a left-facing ramp
				else
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Left, false, false))
						{
							steppingState = On;
							agent.setOnRamp(true);
							world.moveAgent(agent, -1, 0, 0);
							agent.setOffsetX(16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z));
							world.addAgent(h1);
							agent.setRampDescending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x - 1, pos.y, pos.z - 2) || !world.getThingsAt(pos.x - 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Left, false, false))
						{
							steppingState = Off;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(12);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							agent.setRampDescending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x - 1, pos.y, pos.z - 2) && world.getThingsAt(pos.x - 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Left, false, true))
						{
							steppingState = Cont;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(12);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							agent.setRampDescending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
				}
			break;
				
			case Right:
				//Ascending a left-facing ramp
				if (ascending)
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Right, true, false))
						{
							steppingState = On;
							agent.setOnRamp(true);
							world.moveAgent(agent, 1, 0, 1);
							agent.setOffsetX(-16);
							agent.setOffsetY(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							agent.setRampAscending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x + 1, pos.y, pos.z) || !world.getThingsAt(pos.x + 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Right, true, false))
						{
							steppingState = Off;
							world.moveAgent(agent, 1, 0, 0);
							agent.setOffsetX(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z));
							world.addAgent(h1);
							agent.setRampAscending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x + 1, pos.y, pos.z) && world.getThingsAt(pos.x + 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Right, true, false))
						{
							steppingState = Cont;
							world.moveAgent(agent, 1, 0, 1);
							agent.setOffsetX(-16);
							agent.setOffsetY(-20);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							agent.setRampAscending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
				}
				
				//Descending a right-facing ramp
				else
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Right, false, false))
						{
							steppingState = On;
							agent.setOnRamp(true);
							world.moveAgent(agent, 1, 0, 0);
							agent.setOffsetX(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z));
							world.addAgent(h1);
							agent.setRampDescending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x + 1, pos.y, pos.z - 2) || !world.getThingsAt(pos.x + 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Right, false, false))
						{
							steppingState = Off;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(12);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							agent.setRampDescending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x + 1, pos.y, pos.z - 2) && world.getThingsAt(pos.x + 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlannerUtils.canStepHorizontalRamp(agent, world, agent.getPos(), Right, false, true))
						{
							steppingState = Cont;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(12);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							agent.setRampDescending(true);
						}
						else
						{
							finished = true;
							return;
						}
					}
				}
			break;
			}
			
			initialized = true;
		}
		
		//Execute step (many cases...)
		if (ascending)
		{
			switch (steppingState)
			{
			case On:
				switch (dir)
				{
				case Up:
					
				break;
				case Down:
					
				break;
				case Left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() <= 11)
					{
						agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() >= -4)
							agent.setOffsetY(-4);
					}
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z - 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-4);
						agent.setRampAscending(false);
						finished = true;
					}
				break;
				case Right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() >= -11)
					{
						agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() >= -4)
							agent.setOffsetY(-4);
					}
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z - 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-4);
						agent.setRampAscending(false);
						finished = true;
					}
				break;
				}
			break;
			case Off:
				switch (dir)
				{
				case Up:
					
				break;
				case Down:
					
				break;
				case Left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() >= 0)
						agent.setOffsetY(0);
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampAscending(false);
						agent.setOnRamp(false);
						finished = true;
					}
				break;
				case Right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() >= 0)
						agent.setOffsetY(0);
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampAscending(false);
						agent.setOnRamp(false);
						finished = true;
					}
				break;
				}
			break;
			case Cont:
				switch (dir)
				{
				case Up:
					
				break;
				case Down:
					
				break;
				case Left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() >= -4)
						agent.setOffsetY(-4);
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z - 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampAscending(false);
						finished = true;
					}
				break;
				case Right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() >= -4)
						agent.setOffsetY(-4);
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z - 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampAscending(false);
						finished = true;
					}
				break;
				}
			break;
			}
		}
		else
		{
			switch (steppingState)
			{
			case On:
				switch (dir)
				{
				case Up:
					
				break;
				case Down:
					
				break;
				case Left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() <= 3)
					{
						agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() <= -4)
							agent.setOffsetY(-4);
					}
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-4);
						agent.setRampDescending(false);
						finished = true;
					}
				break;
				case Right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() >= -3)
					{
						agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() <= -4)
							agent.setOffsetY(-4);
					}
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-4);
						agent.setRampDescending(false);
						finished = true;
					}
				break;
				}
			break;
			case Off:
				switch (dir)
				{
				case Up:
					
				break;
				case Down:
					
				break;
				case Left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() <= 0)
						agent.setOffsetY(0);
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z + 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampDescending(false);
						agent.setOnRamp(false);
						finished = true;
					}
				break;
				case Right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() <= 0)
						agent.setOffsetY(0);
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z + 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampDescending(false);
						agent.setOnRamp(false);
						finished = true;
					}
				break;
				}
			break;
			case Cont:
				switch (dir)
				{
				case Up:
					
				break;
				case Down:
					
				break;
				case Left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() <= -4)
						agent.setOffsetY(-4);
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z + 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampDescending(false);
						finished = true;
					}
				break;
				case Right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() <= -4)
						agent.setOffsetY(-4);
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z + 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setRampDescending(false);
						finished = true;
					}
				break;
				}
			break;
			}
		}
		
		//Camera for Hero
		if (agent.getClass().equals(Hero.class))
		{
			if (world.isCameraLockH() || world.isCameraLockV())
				world.updateCameraScrollLock(gameState);
			world.updateCamera(gameState);
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
		String data = new String("HorizontalRampStep:\n");
		data += dir.toString() + "," + ascending + "," + initialized + "," + finished + "," + steppingState.toString() + "\n";
		data += "~HorizontalRampStep\n";
		return data;
	}
	*/
	
	public static HorizontalRampStep load(String data)
	{
		if (data.equals("null\n"))
			return null;

		Direction dir = Direction.valueOf(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		Boolean ascending = Boolean.parseBoolean(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		HorizontalRampStep horizontalRampStep = new HorizontalRampStep(dir, ascending);
		
		horizontalRampStep.initialized = Boolean.parseBoolean(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		horizontalRampStep.finished = Boolean.parseBoolean(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		horizontalRampStep.steppingState = SteppingState.valueOf(data.substring(0, data.indexOf('\n')));
		
		return horizontalRampStep;
	}
}
