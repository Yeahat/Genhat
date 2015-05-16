package actions;

import static entities.Agent.Direction.Down;
import static entities.Agent.Direction.Up;

import utils.planners.PathPlannerUtils;
import world.World;
import entities.Agent;
import entities.Agent.Direction;

public class Turn implements Action {
	private final Direction dir;
	private boolean finished;
	
	public Turn(Direction dir)
	{
		this.dir = dir;
		this.finished = false;
	}
	
	@Override
	public void execute(Agent agent, World world) {
		
		if (agent.getDir() != dir && !PathPlannerUtils.isOnClimbingSurface(world, agent.getPos()))
		{
			if (dir == Up || dir == Down)
			{
				if (!PathPlannerUtils.isOnRampHorizontal(world, agent.getPos()))
					agent.setDir(dir);
			}
			else
				agent.setDir(dir);
		}
		finished = true;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
	
	@Override
	public boolean requestInterrupt() {
		return true;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
}
