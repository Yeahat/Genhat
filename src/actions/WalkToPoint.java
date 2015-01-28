package actions;

import java.util.ArrayList;

import utils.planners.PathPlanners;
import world.Position;
import world.World;
import entities.Agent;

public class WalkToPoint implements Action {

	private final int plannerID;
	private boolean planned = false;
	private boolean initialized = false;
	private String path = "";
	private Position goal;
	private int plannerWait = 0;
	
	private FollowPath followPath;
	ArrayList<String> followPathArgs = new ArrayList<String>();
	
	public WalkToPoint()
	{
		plannerID = 0;
		followPath = new FollowPath();
	}
	
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		if (!planned)
		{
			if (!initialized)
			{
				if (args.size() < 1)
				{
					System.out.println("Invalid arguments to action WalkToPoint.");
					System.out.println("WalkToPoint must take 1 argument denoting the goal point, as a string from a position in the form: (x, y, z),");
					System.out.println("and an optional second argument denoting a wait period as an integer (defaults to 0 if unspecified).");
					return;
				}
				goal = Position.posFromString(args.get(0)); //read goal from args
				//quick goal check in case it's already the current location
				if (goal.equals(agent.getPos()))
				{
					return;
				}
				initialized = true;
			}
			
			if (plannerWait == 0)
			{
				switch (plannerID)
				{
				default:
				path = PathPlanners.aStarSimpleStep(agent, world, agent.getPos(), goal);
				break;
				}
				followPathArgs.clear();
				followPathArgs.add(path);
				if (args.size() > 1)
					followPathArgs.add(args.get(1));
				if (path == "")
				{
					plannerWait = 30;
				}
				planned = true;
			}
			else
				plannerWait --;	
		}
		followPath.execute(agent, world, followPathArgs);
		if (followPath.isFinished())
		{
			planned = false;
			initialized = false;
		}
	}

	@Override
	public boolean isFinished()
	{
		return followPath.isFinished();
	}

	@Override
	public boolean requestInterrupt()
	{
		return followPath.requestInterrupt();
	}

	@Override
	public boolean isInterruptable()
	{
		return true;
	}
}
