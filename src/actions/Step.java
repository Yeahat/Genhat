package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;

import static entities.Agent.direction.*;

public class Step implements Action {

	boolean finishedStep = true;
	SimpleStep simpleStep = new SimpleStep();
	RampStep rampStep = new RampStep();
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		//invalid arguments, do nothing
		if (args.size() < 1)
		{
			System.out.println("Invalid arguments to action Step.");
			System.out.println("Step must take 1 argument denoting direction, as either: {up, down, left, right}");
			return;
		}
			
		String arg1 = args.get(0);
		if (arg1.equals("up"))
		{
			if (!agent.isOnRamp())
				agent.setDir(up);
			//if any steps are already in progress, continue them,
			//otherwise determine new steps that should be started
			if (!continueStepping(agent, world, args))
			{
				finishedStep = false;
				//check for ramps
				int[] pos = agent.getPos();
				if (world.hasThing(pos[0], pos[1], pos[2]) && world.getThingsAt(pos[0], pos[1], pos[2]).hasRamp()
						&& world.getThingsAt(pos[0], pos[1], pos[2]).getRampDir() == down)
				{
					ArrayList<String> tempArgs = new ArrayList<String>();
					tempArgs.addAll(args);
					tempArgs.add("ascending");
					rampStep.execute(agent, world, tempArgs);
				}
				else
				{
					if (!agent.isOnRamp())
						simpleStep.execute(agent, world, args);
				}
			}
			
			finishedStep = simpleStep.isFinished() && rampStep.isFinished();
		}
		
		
		else if (arg1.equals("down"))
		{
			if (!agent.isOnRamp())
				agent.setDir(down);
			
			//if any steps are already in progress, continue them,
			//otherwise determine new steps that should be started
			if (!continueStepping(agent, world, args))
			{
				finishedStep = false;
				//check for ramps
				int[] pos = agent.getPos();
				if (world.hasThing(pos[0], pos[1] - 1, pos[2] - 1) && world.getThingsAt(pos[0], pos[1] - 1, pos[2] - 1).hasRamp()
						&& world.getThingsAt(pos[0], pos[1] - 1, pos[2] - 1).getRampDir() == down)
				{
					ArrayList<String> tempArgs = new ArrayList<String>();
					tempArgs.addAll(args);
					tempArgs.add("descending");
					rampStep.execute(agent, world, tempArgs);
				}
				else
				{
					if (!agent.isOnRamp())
					{
						simpleStep.execute(agent, world, args);
					}
				}
			}
			
			finishedStep = simpleStep.isFinished() && rampStep.isFinished();
		}
		
		
		else if (arg1.equals("left"))
		{
			agent.setDir(left);
			
			//if any steps are already in progress, continue them,
			//otherwise determine new steps that should be started
			if (!continueStepping(agent, world, args))
			{
				finishedStep = false;
				//check for ramps
				int[] pos = agent.getPos();
				//Ascend ramp
				if ((world.hasThing(pos[0] - 1, pos[1], pos[2]) && world.getThingsAt(pos[0] - 1, pos[1], pos[2]).hasRamp()
						&& world.getThingsAt(pos[0] - 1, pos[1], pos[2]).getRampDir() == left)
						|| (world.hasThing(pos[0], pos[1], pos[2] - 1) && world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp()
								&& world.getThingsAt(pos[0], pos[1], pos[2] - 1).getRampDir() == left))
				{
					ArrayList<String> tempArgs = new ArrayList<String>();
					tempArgs.addAll(args);
					tempArgs.add("ascending");
					rampStep.execute(agent, world, tempArgs);
				}
				//Descend ramp
				else if ((world.hasThing(pos[0] - 1, pos[1], pos[2] - 1) && world.getThingsAt(pos[0] - 1, pos[1], pos[2] - 1).hasRamp()
						&& world.getThingsAt(pos[0] - 1, pos[1], pos[2] - 1).getRampDir() == right)
						|| (world.hasThing(pos[0], pos[1], pos[2] - 1) && world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp()
								&& world.getThingsAt(pos[0], pos[1], pos[2] - 1).getRampDir() == right))
				{
					ArrayList<String> tempArgs = new ArrayList<String>();
					tempArgs.addAll(args);
					tempArgs.add("descending");
					rampStep.execute(agent, world, tempArgs);
				}
				//Normal step
				else
				{
					simpleStep.execute(agent, world, args);
				}
			}
			
			finishedStep = simpleStep.isFinished() && rampStep.isFinished();
		}
		
		
		else if (arg1.equals("right"))
		{
			agent.setDir(right);
			
			//if any steps are already in progress, continue them,
			//otherwise determine new steps that should be started
			if (!continueStepping(agent, world, args))
			{
				finishedStep = false;
				//check for ramps
				int[] pos = agent.getPos();
				//Ascend ramp
				if ((world.hasThing(pos[0] + 1, pos[1], pos[2]) && world.getThingsAt(pos[0] + 1, pos[1], pos[2]).hasRamp()
						&& world.getThingsAt(pos[0] + 1, pos[1], pos[2]).getRampDir() == right)
						|| (world.hasThing(pos[0], pos[1], pos[2] - 1) && world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp()
								&& world.getThingsAt(pos[0], pos[1], pos[2] - 1).getRampDir() == right))
				{
					ArrayList<String> tempArgs = new ArrayList<String>();
					tempArgs.addAll(args);
					tempArgs.add("ascending");
					rampStep.execute(agent, world, tempArgs);
				}
				//Descend ramp
				else if ((world.hasThing(pos[0] + 1, pos[1], pos[2] - 1) && world.getThingsAt(pos[0] + 1, pos[1], pos[2] - 1).hasRamp()
						&& world.getThingsAt(pos[0] + 1, pos[1], pos[2] - 1).getRampDir() == left)
						|| (world.hasThing(pos[0], pos[1], pos[2] - 1) && world.getThingsAt(pos[0], pos[1], pos[2] - 1).hasRamp()
								&& world.getThingsAt(pos[0], pos[1], pos[2] - 1).getRampDir() == left))
				{
					ArrayList<String> tempArgs = new ArrayList<String>();
					tempArgs.addAll(args);
					tempArgs.add("descending");
					rampStep.execute(agent, world, tempArgs);
				}
				//Normal step
				else
				{
					simpleStep.execute(agent, world, args);
				}
			}
			
			finishedStep = simpleStep.isFinished() && rampStep.isFinished();
		}
		else
		{
			System.out.println("Invalid arguments to action Step.");
			System.out.println("Step must take 1 argument denoting direction, as either: {up, down, left, right}");
			return; //invalid arguments, do nothing
		}
	}

	@Override
	public boolean isFinished() 
	{
		return finishedStep;
	}
	
	private boolean continueStepping(Agent agent, World world, ArrayList<String> args)
	{
		if (agent.isRampAscending())
		{
			ArrayList<String> tempArgs = new ArrayList<String>();
			tempArgs.addAll(args);
			tempArgs.add("ascending");
			rampStep.execute(agent, world, tempArgs);
			return true;
		}
		else if (agent.isRampDescending())
		{
			ArrayList<String> tempArgs = new ArrayList<String>();
			tempArgs.addAll(args);
			tempArgs.add("descending");
			rampStep.execute(agent, world, tempArgs);
			return true;
		}
		else if (agent.isStepping())
		{
			simpleStep.execute(agent, world, args);
			return true;
		}
		else
		{
			return false;
		}
	}
}
