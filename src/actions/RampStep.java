package actions;

import java.util.ArrayList;

import things.Thing;
import things.ThingGridCell;
import world.World;
import entities.Agent;
import entities.Hero;
import entities.Placeholder;
import entities.Agent.direction;

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
					if (canStepRamp(agent, world, up, true, false))
					{
						world.moveAgent(agent, 0, 1, 1);
						agent.setOffsetY(-32);
						int[] pos = agent.getPos();
						Placeholder h1 = new Placeholder(pos[0], pos[1] - 1, pos[2] - 1);
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
					int[] pos = agent.getPos();
					world.removeAgentAt(pos[0], pos[1] - 1, pos[2] - 1);
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
					int[] pos = agent.getPos();
					if (canStepRamp(agent, world, down, false, false))
					{
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
					int[] pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos[0], pos[1], pos[2] - 1) || !world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp())
					{
						if (canStepRamp(agent, world, left, true, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, -1, 0, 1);
							agent.setOffsetX(16);
							agent.setOffsetY(-16);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] + 1, p1[1], p1[2] - 1);
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
					else if (!world.hasThing(pos[0] - 1, pos[1], pos[2]) || !world.getThingsAt(pos[0] - 1, pos[1], pos[2]).hasRamp())
					{
						if (canStepRamp(agent, world, left, true, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, -1, 0, 0);
							agent.setOffsetX(16);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] + 1, p1[1], p1[2]);
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
					else if (world.hasThing(pos[0] - 1, pos[1], pos[2]) && world.getThingsAt(pos[0] - 1, pos[1], pos[2]).hasRamp())
					{
						if (canStepRamp(agent, world, left, true, false))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, -1, 0, 1);
							agent.setOffsetX(16);
							agent.setOffsetY(-24);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] + 1, p1[1], p1[2] - 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] + 1, pos[1], pos[2] - 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] + 1, pos[1], pos[2]);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] + 1, pos[1], pos[2] - 1);
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
					int[] pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos[0], pos[1], pos[2] - 1) || !world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp())
					{
						if (canStepRamp(agent, world, left, false, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, -1, 0, 0);
							agent.setOffsetX(16);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] + 1, p1[1], p1[2]);
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
					else if (!world.hasThing(pos[0] - 1, pos[1], pos[2] - 2) || !world.getThingsAt(pos[0] - 1, pos[1], pos[2] - 2).hasRamp())
					{
						if (canStepRamp(agent, world, left, false, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(8);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] + 1, p1[1], p1[2] + 1);
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
					else if (world.hasThing(pos[0] - 1, pos[1], pos[2] - 2) && world.getThingsAt(pos[0] - 1, pos[1], pos[2] - 2).hasRamp())
					{
						if (canStepRamp(agent, world, left, false, true))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, -1, 0, -1);
							agent.setOffsetX(16);
							agent.setOffsetY(8);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] + 1, p1[1], p1[2] + 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] + 1, pos[1], pos[2]);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] + 1, pos[1], pos[2] + 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] + 1, pos[1], pos[2] + 1);
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
					int[] pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos[0], pos[1], pos[2] - 1) || !world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp())
					{
						if (canStepRamp(agent, world, right, true, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, 1, 0, 1);
							agent.setOffsetX(-16);
							agent.setOffsetY(-16);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] - 1, p1[1], p1[2] - 1);
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
					else if (!world.hasThing(pos[0] + 1, pos[1], pos[2]) || !world.getThingsAt(pos[0] + 1, pos[1], pos[2]).hasRamp())
					{
						if (canStepRamp(agent, world, right, true, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, 1, 0, 0);
							agent.setOffsetX(-16);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] - 1, p1[1], p1[2]);
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
					else if (world.hasThing(pos[0] + 1, pos[1], pos[2]) && world.getThingsAt(pos[0] + 1, pos[1], pos[2]).hasRamp())
					{
						if (canStepRamp(agent, world, right, true, false))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, 1, 0, 1);
							agent.setOffsetX(-16);
							agent.setOffsetY(-24);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] - 1, p1[1], p1[2] - 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] - 1, pos[1], pos[2] - 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] - 1, pos[1], pos[2]);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] - 1, pos[1], pos[2] - 1);
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
					int[] pos = agent.getPos();
					
					//Case 1: stepping onto a ramp
					if (!world.hasThing(pos[0], pos[1], pos[2] - 1) || !world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp())
					{
						if (canStepRamp(agent, world, right, false, false))
						{
							steppingOn = true;
							steppingOff = false;
							steppingContinue = false;
							agent.setOnRamp(true);
							world.moveAgent(agent, 1, 0, 0);
							agent.setOffsetX(-16);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] - 1, p1[1], p1[2]);
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
					else if (!world.hasThing(pos[0] + 1, pos[1], pos[2] - 2) || !world.getThingsAt(pos[0] + 1, pos[1], pos[2] - 2).hasRamp())
					{
						if (canStepRamp(agent, world, right, false, false))
						{
							steppingOn = false;
							steppingOff = true;
							steppingContinue = false;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(8);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] - 1, p1[1], p1[2] + 1);
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
					else if (world.hasThing(pos[0] + 1, pos[1], pos[2] - 2) && world.getThingsAt(pos[0] + 1, pos[1], pos[2] - 2).hasRamp())
					{
						if (canStepRamp(agent, world, right, false, true))
						{
							steppingOn = false;
							steppingOff = false;
							steppingContinue = true;
							world.moveAgent(agent, 1, 0, -1);
							agent.setOffsetX(-16);
							agent.setOffsetY(8);
							int[] p1 = agent.getPos();
							Placeholder h1 = new Placeholder(p1[0] - 1, p1[1], p1[2] + 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] - 1, pos[1], pos[2]);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] - 1, pos[1], pos[2] + 1);
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
						int[] pos = agent.getPos();
						world.removeAgentAt(pos[0] - 1, pos[1], pos[2] + 1);
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
	 * @param specialCase true for the special case of descending from a ramp onto another ramp, the z needs extra adjustment
	 * @return true if the agent can step in the given direction, false otherwise
	 */
	private boolean canStepRamp(Agent agent, World world, direction dir, boolean ascending, boolean specialCase)
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
		{
			if (dir != left && dir != right)
				z += 1;
		}
		else
		{
			if (specialCase)
				z -= 2;
			else
				z -= 1;
		}

		int kMax = pos[2] + agent.getHeight();
		if (specialCase)
			kMax -= 1;
		for (int k = z; k < kMax; k ++)
		{
			//grid bounds check
			if (!world.isInBounds(x, y, k))
			{
				return false;
			}
			
			//collision check
			if (dir == left || dir == right)
			{
				if (world.hasThing(x, y, k) && (world.getThingsAt(x, y, k).hasRamp() && (world.getThingsAt(x, y, k).getRampDir() == left || world.getThingsAt(x, y, k).getRampDir() == right)))
				{
					if (world.getAgentAt(x, y, k) != null)
					{
						return false;
					}
				}
				else if (world.isBlocked(x, y, k))
				{
					return false;
				}
			}
			else if (world.isBlocked(x, y, k))
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
}
