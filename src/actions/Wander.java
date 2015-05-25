package actions;

import java.util.Random;

import utils.planners.Distance;
import world.Position;
import world.Map;
import entities.Agent;
import entities.Agent.Direction;
import static entities.Agent.Direction.*;

public class Wander implements Action {

	private final int frequency;	//approximate number of frames between each wandering step
	private final int distance; //number of spaces from the home position to wander around in
	private final float turnChance;
	
	Step step;
	Turn turn;
	
	public Wander(int frequency, int distance)
	{
		this.frequency = frequency;
		this.distance = distance;
		this.turnChance = .25f;
	}
	
	public Wander(int frequency, int distance, float turnChance)
	{
		this.frequency = frequency;
		this.distance = distance;
		this.turnChance = turnChance;
	}
	
	@Override
	public void execute(Agent agent, Map world)
	{
		Random rand = new Random();
		if (step == null || step.isFinished())
		{
			if (rand.nextFloat() < 1.0f/frequency)
			{
				if (rand.nextFloat() < turnChance)
				{
					turn = new Turn(setTurnDirection());
					turn.execute(agent, world);
				}
				else
				{
					if (setStepDirection(agent))
					{
						step.execute(agent, world);
					}
				}
			}
		}
		else
		{
			step.execute(agent, world);
		}
	}

	@Override
	public boolean isFinished()
	{
		if (step == null)
			return true;
		
		return step.isFinished();
	}

	private Direction setTurnDirection()
	{
		Random rand = new Random();
		int n = rand.nextInt(4);
		switch (n)
		{
		case 0:		return Up;
		case 1:		return Down;
		case 2:		return Left;
		default:	return Right;
		}
	}
	
	private boolean setStepDirection(Agent agent)
	{
		Position pos1 = agent.getHomePos();
		Position pos2;
		int loopCount = 0;
		boolean success = true;
		Direction dir = Left;
		
		//loop until a direction is found that allows the agent to wander within the distance limit,
		//or stop if a suitable direction is not found within a number of attempts
		do
		{
			if (loopCount > 32)
			{
				success = false;
				break;
			}
			
			pos2 = new Position(pos1);
			dir = setTurnDirection();
			switch (dir)
			{
			case Up:	pos2.y += 1;	break;
			case Down:	pos2.y -= 1;	break;
			case Right:	pos2.x += 1;	break;
			default:	pos2.x -= 1;	break;
			}
			
			loopCount ++;
			
		} while (Distance.distance2D(pos1, pos2) > distance);
		
		if (success)
			step = new Step(dir);
		
		return success;
	}

	@Override
	public boolean requestInterrupt() {
		if (step == null)
			return true;
		
		return step.isFinished();
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
	
}
