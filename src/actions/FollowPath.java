package actions;

import world.World;
import entities.Agent;

import static entities.Agent.direction.*;

public class FollowPath implements Action 
{	
	private String path;
	private final int waitTime;
	private boolean executingStep;
	private char currentStep;

	Step step;
	Wait wait;
	
	public FollowPath(String path, int waitTime)
	{
		this.path = path;
		this.waitTime = waitTime;
		
		executingStep = false;
		currentStep = '-';
	}
	
	@Override
	public void execute(Agent agent, World world) 
	{		
		if (currentStep == '-' && path.length() == 0)
			return;
		
		if (currentStep == '-')
		{
			currentStep = path.charAt(0);
			if (path.length() == 1)
				path = "";
			else
				path = path.substring(1);
			
			switch (currentStep)
			{
			case 'U':	step = new Step(up);	break;
			case 'D':	step = new Step(down);	break;
			case 'L':	step = new Step(left);	break;
			case 'R':	step = new Step(right);	break;
			}
			executingStep = false;

			wait = new Wait(waitTime);
		}
		
		if (!wait.isFinished())
		{
			wait.execute(agent, world);
		}
		else
		{
			step.execute(agent, world);
			if (step.isFinished() && !executingStep)
			{
				//step is blocked by something, try again on the next cycle
			}
			else if (!executingStep)
			{
				executingStep = true;
			}
			else if (executingStep && step.isFinished())
			{
				executingStep = false;
				currentStep = '-';
			}
		}
	}

	@Override
	public boolean isFinished() {
		return currentStep == '-' && path.length() == 0;
	}

	@Override
	public boolean isInterruptable()
	{
		return true;
	}
	
	@Override
	public boolean requestInterrupt()
	{
		return !executingStep;
	}
}
