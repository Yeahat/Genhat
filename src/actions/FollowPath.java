package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;

import static entities.Agent.direction.*;

public class FollowPath implements Action 
{	
	String path;
	int frequency;
	boolean looping;
	boolean executingStep;
	char currentStep;

	Step step;
	ArrayList<String> stepArgs;
	
	public FollowPath()
	{
		frequency = 0;
		step = new Step();
		path = "";
		looping = false;
		executingStep = false;
		currentStep = '-';
		stepArgs = new ArrayList<String>();
	}
	
	public FollowPath(boolean isLooping, int freq)
	{
		frequency = freq;
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
			
			//handle invalid strings in the path
			if (currentStep != 'u' && currentStep != 'd' && currentStep != 'l' && currentStep != 'r')
			{
				currentStep = '-';
				return;
			}
			else
			{
				stepArgs.clear();
				switch (currentStep)
				{
				case 'u':	stepArgs.add("up");		break;
				case 'd':	stepArgs.add("down");	break;
				case 'l':	stepArgs.add("left");	break;
				case 'r':	stepArgs.add("right");	break;
				}
				executingStep = false;
			}
		}
		
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
	
	public void setFrequency(int freq)
	{
		frequency = freq;
	}
	
	public void setPath(String newPath)
	{
		path = newPath.toLowerCase();
	}
}
