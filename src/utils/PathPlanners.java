package utils;

import static entities.Agent.direction.left;
import static entities.Agent.direction.right;
import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;

public class PathPlanners {
	/**
	 * Determine whether it is possible to step to the next location, incorporating bounds checking,
	 * collision checking with things and objects, and ensuring that the next location either has
	 * solid ground below it or a crossable thing on it
	 * 
	 * @param agent the agent taking the action
	 * @param world the world
	 * @param dir the direction of the step
	 * @return true if the agent can step in the given direction, false otherwise
	 */
	public static boolean canStep(Agent agent, World world, Position pos, direction dir)
	{
		int x = pos.x;
		int y = pos.y;
		
		switch (dir)
		{
		case up:
			y += 1;
		break;
		case down:
			y -= 1;
		break;
		case left:
			x -= 1;
		break;
		case right:
			x += 1;
		break;
		}
		
		for (int z = pos.z; z < pos.z + agent.getHeight(); z ++)
		{
			//grid bounds check
			if (!world.isInBounds(x, y, z))
			{
				return false;
			}
			//collision check
			if (world.isBlocked(x, y, z))
			{
				return false;
			}
		}
		//ground check
		if (!world.isCrossable(x, y, pos.z))
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Determine whether it is possible to step to the next location, where that location is at the top
	 * of a ramp, incorporating bounds checking,
	 * collision checking with things and objects, and ensuring that the next location either has
	 * solid ground below it or a crossable thing on it
	 * 
	 * @param agent the agent taking the action
	 * @param world the world
	 * @param pos starting position of agent
	 * @param dir the direction of the step
	 * @param ascending true if ascending a ramp, false otherwise
	 * @param continuingDescent true for the special case of descending from a ramp onto another ramp, the z needs extra adjustment
	 * @return true if the agent can step in the given direction, false otherwise
	 */
	public static boolean canStepRamp(Agent agent, World world, Position pos, direction dir, boolean ascending, boolean continuingDescent)
	{
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		
		switch (dir)
		{
		case up:	y += 1;	break;
		case down:	y -= 1;	break;
		case left:	x -= 1;	break;
		case right:	x += 1;	break;
		}
		
		if (ascending)
		{
			if (dir != left && dir != right)
				z += 1;
		}
		else
		{
			if (continuingDescent)
				z -= 2;
			else
				z -= 1;
		}

		int kMax = pos.z + agent.getHeight();
		if (continuingDescent)
			kMax -= 1;
		for (int k = z; k < kMax; k ++)
		{
			//grid bounds check
			if (!world.isInBounds(x, y, k))
			{
				return false;
			}
			
			//collision check
			if (dir == left || dir == right)
			{
				if (world.hasThing(x, y, k) && (world.getThingsAt(x, y, k).hasRamp() && (world.getThingsAt(x, y, k).getRampDir() == left || world.getThingsAt(x, y, k).getRampDir() == right)))
				{
					if (world.getAgentAt(x, y, k) != null)
					{
						return false;
					}
				}
				else if (world.isBlocked(x, y, k))
				{
					return false;
				}
			}
			else if (world.isBlocked(x, y, k))
			{
				return false;
			}
		}
		//ground check
		if (!world.isCrossable(x, y, z))
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Determine if a step in a given direction should be treated as a ramp step or a normal step, and if it is a ramp step, determine if it is ascent or descent
	 * @param agent the agent taking the action
	 * @param world the world
	 * @param pos starting position of the agent
	 * @param dir the direction of the step
	 * @return {true if the agent should use a ramp step instead of a regular step, true if the ramp step is ascending}
	 */
	public static boolean[] checkForRampStep(Agent agent, World world, Position pos, direction dir)
	{
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		int xt = x; //target pos x
		switch (dir)
		{
		case left:	xt = x - 1;	break;
		case right:	xt = x + 1;	break;
		default: boolean[] result = {false, false}; return result; //ramp steps are only for left/right directions, not up/down
		}
		
		boolean isRampStep = false;
		boolean ascending = false;
		
		if ((world.hasThing(xt, y, z) && world.getThingsAt(xt, y, z).hasRamp()
				&& world.getThingsAt(xt, y, z).getRampDir() == dir)
				|| (world.hasThing(x, y, z - 1) && world.getThingsAt(x, y, z - 1).hasRamp()
						&& world.getThingsAt(x, y, z - 1).getRampDir() == dir))
		{
			isRampStep = true;
			ascending = true;
		}
		else if ((world.hasThing(xt, y, z - 1) && world.getThingsAt(xt, y, z - 1).hasRamp()
				&& world.getThingsAt(xt, y, z - 1).getRampDir() == right)
				|| (world.hasThing(x, y, z - 1) && world.getThingsAt(x, y, z - 1).hasRamp()
						&& world.getThingsAt(x, y, z - 1).getRampDir() == right))
		{
			isRampStep = true;
		}
		
		boolean result[] = {isRampStep, ascending};
		return result;
	}

}
