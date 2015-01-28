package actions;

import java.util.ArrayList;
import java.util.Random;

import utils.planners.Distance;
import world.Position;
import world.World;
import entities.Agent;

public class Wander implements Action {

	int frequency;	//approximate number of frames between each wandering step
	int distance; //number of spaces from the home position to wander around in
	float turnChance;
	
	ArrayList<String> stepArgs;
	
	Step step;
	Turn turn;
	
	public Wander(int freq, int dst)
	{
		frequency = freq;
		distance = dst;
		turnChance = .25f;
		stepArgs = new ArrayList<String>();
		step = new Step();
		turn = new Turn();
	}
	
	public Wander(int freq, int dst, float tc)
	{
		frequency = freq;
		distance = dst;
		turnChance = tc;
		stepArgs = new ArrayList<String>();
		step = new Step();
		turn = new Turn();
	}
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		Random rand = new Random();
		if (step.isFinished())
		{
			if (rand.nextFloat() < 1.0f/frequency)
			{
				if (rand.nextFloat() < turnChance)
				{
					setTurnDirection();
					turn.execute(agent, world, stepArgs);
				}
				else
				{
					if (setStepDirection(agent))
					{
						step.execute(agent, world, stepArgs);
					}
				}
			}
		}
		else
		{
			step.execute(agent, world, stepArgs);
		}
	}

	@Override
	public boolean isFinished()
	{
		return step.isFinished();
	}

	private void setTurnDirection()
	{
		Random rand = new Random();
		int n = rand.nextInt(4);
		String dir;
		switch (n)
		{
		case 0:		dir = "up";		break;
		case 1:		dir = "down";	break;
		case 2:		dir = "left";	break;
		default:	dir = "right";	break;
		}
		stepArgs.clear();
		stepArgs.add(dir);
	}
	
	private boolean setStepDirection(Agent agent)
	{
		Position pos1 = agent.getHomePos();
		Position pos2 = new Position(pos1);
		int loopCount = 0;
		boolean success = true;
		
		//loop until a direction is found that allows the agent to wander within the distance limit,
		//or stop if a suitable direction is not found within a number of attempts
		do
		{
			setTurnDirection();
			if (stepArgs.get(0).equals("up"))
				pos2.y += 1;
			else if (stepArgs.get(0).equals("down"))
				pos2.y -= 1;
			else if (stepArgs.get(0).equals("right"))
				pos2.x += 1;
			else
				pos2.x -= 1;
			
			loopCount ++;
			if (loopCount > 32)
			{
				success = false;
				break;
			}
			
		} while (Distance.distance2D(pos1, pos2) > distance);
		
		return success;
	}

	@Override
	public boolean requestInterrupt() {
		return step.isFinished();
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
	
}
