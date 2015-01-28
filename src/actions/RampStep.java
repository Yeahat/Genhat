package actions;

import java.util.ArrayList;

import utils.PathPlanners;
import world.Position;
import world.World;
import entities.Agent;
import entities.Hero;
import entities.Placeholder;
import static entities.Agent.direction.*;

public class RampStep implements Action {

	boolean finishedStep = true;
	boolean steppingOn = false;
	boolean steppingOff = false;
	boolean steppingContinue = false;
	boolean ascending = true;
	
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
				ascending = false;
			else
				ascending = true;
		}
		
		if (arg1.equals("up"))
		{
			//Ascending a down-facing ramp
			if (ascending)
			{
				if (!agent.isRampAscending())
				{
					if (PathPlanners.canStepRamp(agent, world, agent.getPos(), up, true, false))
					{
						world.moveAgent(agent, 0, 1, 1);
						agent.setOffsetY(-32);
						Position pos = agent.getPos();
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z - 1));
						world.addAgent(h1);
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
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x, pos.y - 1, pos.z - 1);
					agent.setOffsetY(0);
					agent.setRampAscending(false);
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
		}
		
		
		else if (arg1.equals("down"))
		{
			if (!ascending)
			{
				if (!agent.isRampDescending())
				{					
					//Descending a down-facing ramp
					Position pos = agent.getPos();
					if (PathPlanners.canStepRamp(agent, world, agent.getPos(), down, false, false))
					{
						Placeholder holder = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z - 1));
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
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x, pos.y - 1, pos.z - 1);
					world.moveAgent(agent, 0, -1, -1);
					agent.setRampDescending(false);
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
		}
		
		
		else if (arg1.equals("left"))
		{
			//Ascending a right-facing ramp
			if (ascending)
			{
				if (!agent.isRampAscending())
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), left, true, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, -1, 0, 1);
							agent.setOffsetX(16);
							agent.setOffsetY(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampAscending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x - 1, pos.y, pos.z) || !world.getThingsAt(pos.x - 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), left, true, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, -1, 0, 0);
							agent.setOffsetX(16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampAscending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x - 1, pos.y, pos.z) && world.getThingsAt(pos.x - 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), left, true, false))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, -1, 0, 1);
							agent.setOffsetX(16);
							agent.setOffsetY(-24);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampAscending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
				}
				
				//Case 1: stepping onto a ramp
				if (steppingOn)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 2: stepping off of a ramp
				if (steppingOff)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 3: stepping off of a ramp onto another ramp
				if (steppingContinue)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
			}
			
			//Descending a left-facing ramp
			else
			{
				if (!agent.isRampDescending())
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), left, false, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, -1, 0, 0);
							agent.setOffsetX(16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampDescending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x - 1, pos.y, pos.z - 2) || !world.getThingsAt(pos.x - 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), left, false, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(8);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampDescending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x - 1, pos.y, pos.z - 2) && world.getThingsAt(pos.x - 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), left, false, true))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(8);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x + 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampDescending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
				}
				
				//Case 1: stepping onto a ramp
				if (steppingOn)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 2: stepping off of a ramp
				if (steppingOff)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 3: stepping off of a ramp onto another ramp
				if (steppingContinue)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
			}
		}
		
		
		else if (arg1.equals("right"))
		{
			//Ascending a left-facing ramp
			if (ascending)
			{
				if (!agent.isRampAscending())
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), right, true, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, 1, 0, 1);
							agent.setOffsetX(-16);
							agent.setOffsetY(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampAscending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x + 1, pos.y, pos.z) || !world.getThingsAt(pos.x + 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), right, true, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, 1, 0, 0);
							agent.setOffsetX(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampAscending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x + 1, pos.y, pos.z) && world.getThingsAt(pos.x + 1, pos.y, pos.z).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), right, true, false))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, 1, 0, 1);
							agent.setOffsetX(-16);
							agent.setOffsetY(-24);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z - 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampAscending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
				}
				
				//Case 1: stepping onto a ramp
				if (steppingOn)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 2: stepping off of a ramp
				if (steppingOff)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 3: stepping off of a ramp onto another ramp
				if (steppingContinue)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
			}
			
			//Descending a right-facing ramp
			else
			{
				if (!agent.isRampDescending())
				{
					Position pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos.x, pos.y, pos.z - 1) || !world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), right, false, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, 1, 0, 0);
							agent.setOffsetX(-16);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampDescending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 2: stepping off of a ramp
					else if (!world.hasThing(pos.x + 1, pos.y, pos.z - 2) || !world.getThingsAt(pos.x + 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), right, false, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(8);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampDescending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
					//Case 3: stepping off of a ramp onto another ramp
					else if (world.hasThing(pos.x + 1, pos.y, pos.z - 2) && world.getThingsAt(pos.x + 1, pos.y, pos.z - 2).hasRamp())
					{
						if (PathPlanners.canStepRamp(agent, world, agent.getPos(), right, false, true))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(8);
							Position p1 = agent.getPos();
							Placeholder h1 = new Placeholder(agent, new Position(p1.x - 1, p1.y, p1.z + 1));
							world.addAgent(h1);
							finishedStep = false;
							agent.setRampDescending(true);
						}
						else
						{
							finishedStep = true;
							return;
						}
					}
				}
				
				//Case 1: stepping onto a ramp
				if (steppingOn)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 2: stepping off of a ramp
				if (steppingOff)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
				//Case 3: stepping off of a ramp onto another ramp
				if (steppingContinue)
				{
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
						finishedStep = true;
					}
					
					//Camera for Hero
					if (agent.getClass().equals(Hero.class))
					{
						if (world.isCameraLockH() || world.isCameraLockV())
							world.updateCameraScrollLock();
						world.updateCamera();
					}
				}
			}
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
