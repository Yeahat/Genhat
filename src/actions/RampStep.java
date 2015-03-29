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
import static actions.RampStep.SteppingState.*;

public class RampStep implements Action {

	private final direction dir;
	private final boolean ascending;
	private boolean initialized;
	private boolean finished;
	private SteppingState steppingState;

	public enum SteppingState
	{
		On, Off, Cont
	}
	
	public RampStep(direction dir, boolean ascending)
	{
		this.dir = dir;
		this.ascending = ascending;
		this.initialized = false;
		this.finished = false;
	}
	
	@Override
	public void execute(Agent agent, World world)
	{		
		if (finished)
			return;
		
		//initialize ramp step
		if (!initialized)
		{
			switch (dir)
			{
			case up:
				//Ascending a down-facing ramp
				if (ascending)
				{
					if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), up, true, false))
					{
						world.moveAgent(agent, 0, 1, 1);
						agent.setOffsetY(-32);
						Position pos = agent.getPos();
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z - 1));
						world.addAgent(h1);
						agent.setRampAscending(true);
					}
					else
					{
						finished = true;
						return;
					}
				}
			break;
				
			case down:
				if (!ascending)
				{
					//Descending a down-facing ramp
					Position pos = agent.getPos();
					if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), down, false, false))
					{
						Placeholder holder = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z - 1));
						world.addAgent(holder);
						finished = false;
						agent.setRampDescending(true);
					}
					else
					{
						finished = true;
						return;
					}
				}
			break;
				
			case left:
				//Ascending a right-facing ramp
				if (ascending)
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), left, true, false))
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), left, true, false))
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), left, true, false))
						{
							steppingState = Cont;
							world.moveAgent(agent, -1, 0, 1);
							agent.setOffsetX(16);
							agent.setOffsetY(-24);
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), left, false, false))
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), left, false, false))
						{
							steppingState = Off;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(8);
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), left, false, true))
						{
							steppingState = Cont;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(8);
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
				
			case right:
				//Ascending a left-facing ramp
				if (ascending)
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), right, true, false))
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), right, true, false))
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), right, true, false))
						{
							steppingState = Cont;
							world.moveAgent(agent, 1, 0, 1);
							agent.setOffsetX(-16);
							agent.setOffsetY(-24);
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), right, false, false))
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), right, false, false))
						{
							steppingState = Off;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(8);
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
						if (PathPlannerUtils.canStepRamp(agent, world, agent.getPos(), right, false, true))
						{
							steppingState = Cont;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(8);
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
				case up:
					
				break;
				case down:
					
				break;
				case left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() <= 8)
					{
						agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() >= -7)
							agent.setOffsetY(-7);
					}
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z - 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-7);
						agent.setRampAscending(false);
						finished = true;
					}
				break;
				case right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() >= -8)
					{
						agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() >= -7)
							agent.setOffsetY(-7);
					}
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z - 1);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-7);
						agent.setRampAscending(false);
						finished = true;
					}
				break;
				}
			break;
			case Off:
				switch (dir)
				{
				case up:
					
				break;
				case down:
					
				break;
				case left:
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
				case right:
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
				case up:
					
				break;
				case down:
					
				break;
				case left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() >= -8)
						agent.setOffsetY(-8);
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
				case right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() >= -8)
						agent.setOffsetY(-8);
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
				case up:
					
				break;
				case down:
					
				break;
				case left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() <= 8)
					{
						agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() <= -9)
							agent.setOffsetY(-9);
					}
					if (agent.getOffsetX() <= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x + 1, pos.y, pos.z);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-9);
						agent.setRampDescending(false);
						finished = true;
					}
				break;
				case right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetX() >= -8)
					{
						agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
						if (agent.getOffsetY() <= -9)
							agent.setOffsetY(-9);
					}
					if (agent.getOffsetX() >= 0)
					{
						Position pos = agent.getPos();
						world.removeAgentAt(pos.x - 1, pos.y, pos.z);
						swapFootstep(agent);
						agent.setOffsetX(0);
						agent.setOffsetY(-9);
						agent.setRampDescending(false);
						finished = true;
					}
				break;
				}
			break;
			case Off:
				switch (dir)
				{
				case up:
					
				break;
				case down:
					
				break;
				case left:
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
				case right:
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
				case up:
					
				break;
				case down:
					
				break;
				case left:
					agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() <= -8)
						agent.setOffsetY(-8);
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
				case right:
					agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
					agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
					if (agent.getOffsetY() <= -8)
						agent.setOffsetY(-8);
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
				world.updateCameraScrollLock();
			world.updateCamera();
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
