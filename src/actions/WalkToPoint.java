package actions;

import utils.planners.AStar;
import utils.planners.PathPlannerUtils.MovementClass;
import world.Position;
import world.World;
import entities.Agent;
import static utils.planners.PathPlannerUtils.MovementClass.*;

public class WalkToPoint implements Action {

	private boolean initialized;
	private boolean planning;
	private boolean finished;
	private final Position goal;
	private final MovementClass movementClass;
	private AStar planner;
	
	private FollowPath followPath;
	
	public WalkToPoint(Position goal)
	{
		this.goal = goal;
		this.movementClass = Stepping;
		planning = false;
		finished = false;
		initialized = false;
		planner = new AStar(Stepping);
	}
	
	public WalkToPoint(Position goal, MovementClass movementClass)
	{
		this.goal = goal;
		this.movementClass = movementClass;
		planning = false;
		finished = false;
		initialized = false;
		planner = new AStar(this.movementClass);
	}
	
	@Override
	public void execute(Agent agent, World world)
	{
		if (finished)
			return;
		
		if (!initialized)
		{
			planner.startPlanningQuery(agent, world, agent.getPos(), goal);
			planning = true;
			initialized = true;
		}
		
		if (planning)
		{
			planner.plan();
			
			//check if planner finished
			if (!planner.isQueryInProgress())
			{
				if (planner.isSolutionFound())
				{
					followPath = new FollowPath(planner.getPath(), this.movementClass, 0);
					planning = false;
				}
				else
				{
					planning = false;
					finished = true;
				}
			}
		}
		else
		{
			followPath.execute(agent, world);
			if (followPath.isFinished())
			{
				finished = true;
			}
		}
	}

	@Override
	public boolean isFinished()
	{
		return finished;
	}

	@Override
	public boolean requestInterrupt()
	{
		if (followPath == null)
			return true;
		
		return followPath.requestInterrupt();
	}

	@Override
	public boolean isInterruptable()
	{
		return true;
	}
}
