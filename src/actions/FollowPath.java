package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;

public class FollowPath implements Action 
{	
	String path;
	boolean frequencySpecified = false;
	boolean waitFinished = true;
	boolean looping;
	boolean executingStep;
	char currentStep;

	Step step;
	Wait wait = new Wait();
	ArrayList<String> stepArgs;
	ArrayList<String> waitArgs = new ArrayList<String>();
	
	public FollowPath()
	{
		step = new Step();
		path = "";
		looping = false;
		executingStep = false;
		currentStep = '-';
		stepArgs = new ArrayList<String>();
	}
	
	public FollowPath(boolean isLooping)
	{
		step = new Step();
		path = "";
		looping = isLooping;
		executingStep = false;
		currentStep = '-';
		stepArgs = new ArrayList<String>();
	}
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args) 
	{
		//invalid arguments, do nothing
		if (args.size() < 1)
		{
			System.out.println("Invalid arguments to action FollowPath.");
			System.out.println("FollowPath must take 1 argument denoting path to follow, as a string composed of characters from [U,D,L,R],");
			System.out.println("and an optional second argument denoting a wait period as an integer (defaults to 0 if unspecified).");
			return;
		}
		if (path.length() == 0 && currentStep == '-') //read path from args
		{
			path = args.get(0);
			//read frequency if it's specified
			if (args.size() > 1)
			{
				waitArgs.clear();
				waitArgs.add(args.get(1));
				frequencySpecified = true;
			}
			else
				frequencySpecified = false;
		}
		
		if (currentStep == '-' && path.length() == 0)
			return;
		
		if (currentStep == '-')
		{
			if (looping)
			{
				currentStep = path.charAt(0);
				path += path.charAt(0);
				path = path.substring(1);
			}
			else
			{
				currentStep = path.charAt(0);
				if (path.length() == 1)
					path = "";
				else
					path = path.substring(1);
			}
			
			stepArgs.clear();
			switch (currentStep)
			{
			case 'U':	stepArgs.add("up");		break;
			case 'D':	stepArgs.add("down");	break;
			case 'L':	stepArgs.add("left");	break;
			case 'R':	stepArgs.add("right");	break;
			}
			executingStep = false;
			if (frequencySpecified)
				waitFinished = false;
		}
		
		if (!waitFinished)
		{
			wait.execute(agent, world, waitArgs);
			if (wait.isFinished())
				waitFinished = true;
		}
		else
		{
			step.execute(agent, world, stepArgs);
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
		if (currentStep == '-' && path.length() == 0)
			return true;
		else
			return false;
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
	
	public void setLooping(boolean isLooping)
	{
		looping = isLooping;
	}
}
