package actions;

import world.Position;
import world.World;
import entities.Agent;
import entities.Hero;
import entities.Placeholder;
import static entities.Agent.direction.*;

public class Jump implements Action {
	private boolean finished;
	private boolean jumpingUp;	//true if jump is ascending, false if jump is descending
	private boolean switched;	//flag for switching position on an descending up-facing jump to correctly occlude agent
	private float dstJumped;	//measures the distance jumped

	public Jump()
	{
		finished = false;
		dstJumped = 0;
		switched = false;
	}
	
	@Override
	public void execute(Agent agent, World world)
	{
		if (finished)
			return;
		
		//initialize jump
		if (!agent.isJumping())
		{
			if (canJump(agent, world))
			{
				dstJumped = 0;
				agent.setJumping(true);
				
				//set initial positions and offsets for all 8 cases
				if (jumpingUp)
				{
					Position pos = agent.getPos();
					switch (agent.getDir())
					{
					case up:
						world.moveAgent(agent, 0, 1, 1);
						agent.setOffsetY(-32);
						pos = agent.getPos();
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z - 1));
						world.addAgent(h1);
						break;
					case down:
						switched = false;
						Placeholder holder1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z + 1));
						world.addAgent(holder1);
						break;
					case left:
						world.moveAgent(agent, -1, 0, 1);
						agent.setOffsetX(16);
						agent.setOffsetY(-16);
						pos = agent.getPos();
						Placeholder h3 = new Placeholder(agent, new Position(pos.x + 1, pos.y, pos.z - 1));
						world.addAgent(h3);
						break;
					case right:
						world.moveAgent(agent, 1, 0, 1);
						agent.setOffsetX(-16);
						agent.setOffsetY(-16);
						pos = agent.getPos();
						Placeholder h4 = new Placeholder(agent, new Position(pos.x - 1, pos.y, pos.z - 1));
						world.addAgent(h4);
						break;
					}
				}
				else
				{
					Position pos = agent.getPos();
					switch (agent.getDir())
					{
					case up:
						switched = false;
						Placeholder holder4 = new Placeholder(agent, new Position(pos.x, pos.y + 1, pos.z - 1));
						world.addAgent(holder4);
						break;
					case down:
						Placeholder holder3 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z - 1));
						world.addAgent(holder3);
						break;
					case left:
						Placeholder holder1 = new Placeholder(agent, new Position(pos.x - 1, pos.y, pos.z - 1));
						world.addAgent(holder1);
						break;
					case right:
						Placeholder holder2 = new Placeholder(agent, new Position(pos.x + 1, pos.y, pos.z - 1));
						world.addAgent(holder2);
						break;
					}
				}
			}
			else
			{
				finished = true;
				return;
			}
		}
		
		//Execute jump (move through the animation for all 8 cases)
		if (jumpingUp)
		{
			switch (agent.getDir())
			{
			case up:
				float i3 = agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i3);
				agent.setOffsetY(dstJumped + lookupHeight() - 32);
				if (dstJumped >= 16)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x, pos.y - 1, pos.z - 1);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
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
				float i4 = -agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i4);
				
				if (dstJumped < 5)
					agent.setOffsetY(-dstJumped + lookupHeight());
				else
				{
					if (!switched)
					{
						Position pos = agent.getPos();
						Placeholder h = new Placeholder(agent, new Position(pos.x, pos.y, pos.z));
						world.removeAgentAt(pos.x, pos.y - 1, pos.z + 1);
						world.moveAgent(agent, 0, -1, 1);
						world.addAgent(h);
						switched = true;
					}
					agent.setOffsetY(-dstJumped + lookupHeight());
				}
					
				if (dstJumped >= 16)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x, pos.y + 1, pos.z - 1);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
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
				float i1 = -agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i1);
				agent.incrementXOffset(i1);
				agent.setOffsetY(-16 + lookupHeight());
				if (agent.getOffsetX() <= 0)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x + 1, pos.y, pos.z - 1);
					agent.setOffsetX(0);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
					finished = true;
				}
				//Camera for Hero
				if (agent.getClass().equals(Hero.class))
				{
					if (world.isCameraLockH() || world.isCameraLockV())
						world.updateCameraScrollLock();
					world.updateCamera();
				}
			break;
			case right:
				float i2 = agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i2);
				agent.incrementXOffset(i2);
				agent.setOffsetY(-16 + lookupHeight());
				if (agent.getOffsetX() >= 0)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x - 1, pos.y, pos.z - 1);
					agent.setOffsetX(0);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
					finished = true;
				}
				if (agent.getClass().equals(Hero.class))
				{
					if (world.isCameraLockH() || world.isCameraLockV())
						world.updateCameraScrollLock();
					world.updateCamera();
				}
			break;
			}
		}
		else
		{
			switch (agent.getDir())
			{
			case up:
				float i4 = agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i4);
				
				if (dstJumped < 12)
					agent.setOffsetY(dstJumped - (16 - lookupHeight()));
				else
				{
					if (!switched)
					{
						Position pos = agent.getPos();
						Placeholder h = new Placeholder(agent, new Position(pos.x, pos.y, pos.z));
						world.removeAgentAt(pos.x, pos.y + 1, pos.z - 1);
						world.moveAgent(agent, 0, 1, -1);
						world.addAgent(h);
						switched = true;
					}
					agent.setOffsetY(dstJumped - (16 - lookupHeight()));
				}
					
				if (dstJumped >= 16)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x, pos.y - 1, pos.z + 1);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
					finished = true;
				}
				
				if (agent.getClass().equals(Hero.class))
				{
					if (world.isCameraLockV())
						world.updateCameraScrollLock();
					world.updateCamera();
				}
			break;
			case down:
				//Add in placeholder, and setup the initial move above
				float i3 = -agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i3);
				agent.setOffsetY(-dstJumped - (16 - lookupHeight()));
				if (dstJumped >= 16)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x, pos.y - 1, pos.z - 1);
					world.moveAgent(agent, 0, -1, -1);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
					finished = true;
				}
				
				if (agent.getClass().equals(Hero.class))
				{
					if (world.isCameraLockV())
						world.updateCameraScrollLock();
					world.updateCamera();
				}
			break;
			case left:
				float i1 = -agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i1);
				agent.incrementXOffset(i1);
				agent.setOffsetY(lookupHeight() - 16);
				if (agent.getOffsetX() <= -16)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x - 1, pos.y, pos.z - 1);
					world.moveAgent(agent, -1, 0, -1);
					agent.setOffsetX(0);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
					finished = true;
				}
				
				if (agent.getClass().equals(Hero.class))
				{
					if (world.isCameraLockH() || world.isCameraLockV())
						world.updateCameraScrollLock();
					world.updateCamera();
				}
			break;
			case right:
				float i2 = agent.getSpeed() * 16.0f / 32.0f;
				dstJumped += Math.abs(i2);
				agent.incrementXOffset(i2);
				agent.setOffsetY(lookupHeight() - 16);
				if (agent.getOffsetX() >= 16)
				{
					Position pos = agent.getPos();
					world.removeAgentAt(pos.x + 1, pos.y, pos.z - 1);
					world.moveAgent(agent, 1, 0, -1);
					agent.setOffsetX(0);
					agent.setOffsetY(0);
					agent.setJumping(false);
					swapFootstep(agent);
					finished = true;
				}
				
				if (agent.getClass().equals(Hero.class))
				{
					if (world.isCameraLockH() || world.isCameraLockV())
						world.updateCameraScrollLock();
					world.updateCamera();
				}
			break;
			}
		}
	}

	@Override
	public boolean isFinished()
	{
		return finished;
	}

	private boolean canJump(Agent agent, World world)
	{
		Position pos = agent.getPos();
		int xFinish = pos.x;
		int yFinish = pos.y;
		switch (agent.getDir())
		{
		case up:	yFinish ++;	break;
		case down:	yFinish --;	break;
		case left:	xFinish --;	break;
		case right:	xFinish ++;	break;
		default:	return false;
		}
		
		//Check to see if the jump should be ascending
		if (world.isLandable(xFinish, yFinish, pos.z + 1))
		{
			jumpingUp = true;
			//Check if the area one space above the agent is blocked
			if (!world.isInBounds(pos.x, pos.y, pos.z + agent.getHeight()) || world.isBlocked(pos.x, pos.y, pos.z + agent.getHeight()))
			{
				return false;
			}
			else
			{
				//check the spaces the agent will occupy upon landing for blocking
				for (int z = 0; z < agent.getHeight(); z++)
				{
					if (!world.isInBounds(xFinish, yFinish, pos.z + 1 + z) || world.isBlocked(xFinish, yFinish, pos.z + 1 + z))
					{
						return false;
					}
				}
				
				return true;
			}
		}
		//Check to see if the jump should be descending
		else if (world.isLandable(xFinish, yFinish, pos.z - 1))
		{
			jumpingUp = false;
			
			//check the spaces the agent will occupy upon landing for blocking, as well
			//as one space above the agent's landing position that will be jumped through
			for (int z = 0; z <= agent.getHeight(); z++)
			{
				if (!world.isInBounds(xFinish, yFinish, pos.z - 1 + z) || world.isBlocked(xFinish, yFinish, pos.z - 1 + z))
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void swapFootstep(Agent agent)
	{
		if (agent.getStance() == left)
			agent.setFootstep(right);
		else
			agent.setFootstep(left);
	}
	
	private int lookupHeight()
	{
		int dst = (int)(dstJumped + .5);
		if (!jumpingUp)
			dst = 16 - dst;
		
		switch (dst)
		{
		case 0:		return 0;
		case 1: 	return 4;
		case 2: 	return 8;
		case 3: 	return 11;
		case 4: 	return 13;
		case 5: 	return 15;
		case 6: 	return 16;
		case 7: 	return 16;
		case 8:		return 17;
		case 9:		return 17;
		case 10:	return 18;
		case 11:	return 18;
		case 12:	return 18;
		case 13:	return 18;
		case 14:	return 17;
		case 15:	return 17;
		case 16:	return 16;
		default:	return 0;
		}
	}

	@Override
	public boolean requestInterrupt() {
		return false;
	}

	@Override
	public boolean isInterruptable() {
		return false;
	}
}
