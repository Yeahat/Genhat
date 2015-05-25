package actions;


import utils.planners.PathPlannerUtils;
import world.Map;
import entities.Agent;
import entities.Agent.Direction;
import static entities.Agent.Direction.*;

public class StepOrClimb implements Action {

	private final Direction dir;
	boolean finished;
	SimpleStep simpleStep;
	Action rampStep;
	Climb climb;
	
	public StepOrClimb(Direction dir)
	{
		this.dir = dir;
		finished = false;
	}
	
	@Override
	public void execute(Agent agent, Map world)
	{
		//begin a new step if one is not already in progress, otherwise continue executing the step in progress
		if (!continueStepping(agent, world))
		{
			//set direction if necessary
			if (agent.getDir() != dir && (dir == Left || dir == Right || !PathPlannerUtils.isOnRampHorizontal(world, agent.getPos())) 
					&& !PathPlannerUtils.isOnClimbingSurface(world, agent.getPos()))
			{
				agent.setDir(dir);
			}
			//check for horizontal ramp steps
			boolean horizontalRampCheck[] = PathPlannerUtils.checkForHorizontalRampStep(world, agent.getPos(), dir);
			if (horizontalRampCheck[0])
			{
				rampStep = new HorizontalRampStep(dir, horizontalRampCheck[1]);
				rampStep.execute(agent, world);
			}
			//check for vertical ramp steps
			else if (PathPlannerUtils.checkForVerticalRampStep(world, agent.getPos(), dir))
			{
				rampStep = new VerticalRampStep(dir);
				rampStep.execute(agent, world);
			}
			//check for climbing
			else if (PathPlannerUtils.checkForClimb(world, agent.getPos(), dir))
			{
				climb = new Climb(dir);
				climb.execute(agent, world);
			}
			//all special cases checked for, use a simple step instead
			else
			{
				simpleStep = new SimpleStep(dir);
				simpleStep.execute(agent, world);
			}
		}
		
		finished = (simpleStep == null || simpleStep.isFinished()) 
				&& (rampStep == null || rampStep.isFinished())
				&& (climb == null || climb.isFinished());
	}

	@Override
	public boolean isFinished() 
	{
		return finished;
	}
	
	private boolean continueStepping(Agent agent, Map world)
	{
		if (agent.isRampAscending() || agent.isRampDescending())
		{
			rampStep.execute(agent, world);
			return true;
		}
		
		if (agent.isClimbing())
		{
			climb.execute(agent, world);
			return true;
		}
		
		if (agent.isStepping())
		{
			simpleStep.execute(agent, world);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean requestInterrupt() {
		return finished;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
}
