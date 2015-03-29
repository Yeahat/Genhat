package actions;

import java.util.ArrayList;

import utils.planners.PathPlannerUtils;
import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;
import static entities.Agent.direction.*;

public class Step implements Action {

	private final direction dir;
	boolean finished;
	SimpleStep simpleStep;
	RampStep rampStep;
	
	public Step(direction dir)
	{
		this.dir = dir;
		finished = false;
	}
	
	@Override
	public void execute(Agent agent, World world)
	{
		switch (dir)
		{
		case up:
			if (!agent.isOnRamp() && agent.getDir() != up)
				agent.setDir(up);
			//if any steps are already in progress, continue them,
			//otherwise determine new steps that should be started
			if (!continueStepping(agent, world))
			{
				//check for ramps
				Position pos = agent.getPos();
				if (world.hasThing(pos.z, pos.y, pos.z) && world.getThingsAt(pos.z, pos.y, pos.z).hasRamp()
						&& world.getThingsAt(pos.z, pos.y, pos.z).getRampDir() == down)
				{
					rampStep = new RampStep(dir, true);
					rampStep.execute(agent, world);
				}
				else
				{
					if (!agent.isOnRamp())
					{
						simpleStep = new SimpleStep(dir);
						simpleStep.execute(agent, world);
					}
				}
			}
		break;
		
		case down:
			if (!agent.isOnRamp() && agent.getDir() != down)
				agent.setDir(down);
			
			//if any steps are already in progress, continue them,
			//otherwise determine new steps that should be started
			if (!continueStepping(agent, world))
			{
				//check for ramps
				Position pos = agent.getPos();
				if (world.hasThing(pos.z, pos.y - 1, pos.z - 1) && world.getThingsAt(pos.z, pos.y - 1, pos.z - 1).hasRamp()
						&& world.getThingsAt(pos.z, pos.y - 1, pos.z - 1).getRampDir() == down)
				{
					rampStep = new RampStep(dir, false);
					rampStep.execute(agent, world);
				}
				else
				{
					if (!agent.isOnRamp())
					{
						simpleStep = new SimpleStep(dir);
						simpleStep.execute(agent, world);
					}
				}
			}
		break;
		
		default:	//left or right step
			if (agent.getDir() != dir)
				agent.setDir(dir);
			
			//if any steps are already in progress, continue them,
			//otherwise determine new steps that should be started
			if (!continueStepping(agent, world))
			{
				//check for ramps
				Position pos = agent.getPos();
				boolean[] rampStepCheck = PathPlannerUtils.checkForRampStep(world, pos, dir);
				//Ramp step
				if (rampStepCheck[0])
				{
					if (rampStepCheck[1])
						rampStep = new RampStep(dir, true);
					else
						rampStep = new RampStep(dir, false);
					rampStep.execute(agent, world);
				}
				//Normal step
				else
				{
					simpleStep = new SimpleStep(dir);
					simpleStep.execute(agent, world);
				}
			}
			
		break;
		}
		
		finished = (simpleStep == null || simpleStep.isFinished()) && (rampStep == null || rampStep.isFinished());
	}

	@Override
	public boolean isFinished() 
	{
		return finished;
	}
	
	private boolean continueStepping(Agent agent, World world)
	{
		if (agent.isRampAscending() || agent.isRampDescending())
		{
			rampStep.execute(agent, world);
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
