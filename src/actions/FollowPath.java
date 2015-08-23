package actions;

import world.GameState;
import world.Map;
import entities.Agent;
import entities.Agent.Direction;
import utils.planners.PathPlannerUtils.MovementClass;
import static entities.Agent.Direction.*;

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
	public void execute(Agent agent, Map world, GameState gameState) 
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
			case 'U':	setStep(Up);	break;
			case 'D':	setStep(Down);	break;
			case 'L':	setStep(Left);	break;
			case 'R':	setStep(Right);	break;
			}
			executingStep = false;

			wait = new Wait(waitTime);
		}
		
		if (!wait.isFinished())
		{
			wait.execute(agent, world, gameState);
		}
		else
		{
			step.execute(agent, world, gameState);
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

	private void setStep(Direction dir)
	{
		if (movementClass == MovementClass.SimpleStepping)
			step = new SimpleStep(dir);
		else if (movementClass == MovementClass.Stepping)
			step = new Step(dir);
		else
			step = new StepOrClimb(dir);
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
