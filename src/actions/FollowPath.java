package actions;

import world.World;
import entities.Agent;
import entities.Agent.direction;
import utils.planners.PathPlannerUtils.MovementClass;
import static entities.Agent.direction.*;

public class FollowPath implements Action 
{	
	private String path;
	private final int waitTime;
	private final MovementClass movementClass;
	private boolean executingStep;
	private char currentStep;

	Action step;
	Wait wait;
	
	public FollowPath(String path, MovementClass movementClass, int waitTime)
	{
		this.path = path;
		this.waitTime = waitTime;
		this.movementClass = movementClass;
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
			case 'U':	setStep(up);	break;
			case 'D':	setStep(down);	break;
			case 'L':	setStep(left);	break;
			case 'R':	setStep(right);	break;
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

	private void setStep(direction dir)
	{
		if (movementClass == MovementClass.SimpleStepping)
			step = new SimpleStep(dir);
		else
			step = new Step(dir);
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
