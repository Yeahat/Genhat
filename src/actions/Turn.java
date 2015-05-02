package actions;

import static entities.Agent.direction.down;
import static entities.Agent.direction.up;

import utils.planners.PathPlannerUtils;
import world.World;
import entities.Agent;
import entities.Agent.direction;

public class Turn implements Action {
	private final direction dir;
	private boolean finished;
	
	public Turn(direction dir)
	{
		this.dir = dir;
		this.finished = false;
	}
	
	@Override
	public void execute(Agent agent, World world) {
		
		if (agent.getDir() != dir && !PathPlannerUtils.isOnClimbingSurface(world, agent.getPos()))
		{
			if (dir == up || dir == down)
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
