package utils.planners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;
import static entities.Agent.direction.*;

public class PathPlannerUtils {	
	
	public enum MovementType
	{
		SimpleStepping, Stepping, Jumping
	}
	
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

	public static boolean isContinuingDescent(Agent agent, World world, Position pos, direction dir)
	{
		Position posBelow = new Position(pos);
		posBelow.z --;
		
		Position forwardPos = new Position(pos);
		switch (dir)
		{
		case up:	forwardPos.y += 1;	break;
		case down:	forwardPos.y -= 1;	break;
		case left:	forwardPos.x -= 1;	break;
		case right:	forwardPos.x += 1;	break;
		}
		forwardPos.z -= 2;
		
		return world.hasThing(posBelow) && world.hasThing(forwardPos)
				&& world.getThingsAt(posBelow).hasRamp() && world.getThingsAt(forwardPos).hasRamp();
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

		int kMax = z + agent.getHeight();
		//if (continuingDescent)
		//	kMax -= 1;
		
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
				//NOTE: added k==z as test to prevent stacked ramps from allowing passage
				if (k == z && world.hasThing(x, y, k) && (world.getThingsAt(x, y, k).hasRamp() && (world.getThingsAt(x, y, k).getRampDir() == left || world.getThingsAt(x, y, k).getRampDir() == right)))
				{
					if (world.getAgentAt(x, y, k) != null)
					{
						return false;
					}
					//TODO: check any non-ramp things and make sure they're not blocking or are crossable?
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
	 * @param world the world
	 * @param pos starting position of the agent
	 * @param dir the direction of the step
	 * @return {true if the agent should use a ramp step instead of a regular step, true if the ramp step is ascending}
	 */
	public static boolean[] checkForRampStep(World world, Position pos, direction dir)
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

	/**
	 * Calculates the resulting position if an agent were to take a step from a given position in a given direction.
	 * @param world the world in which the step is taking place
	 * @param pos the position from which the step is initiated
	 * @param dir the direction to step
	 * @param rampStep true if the step involves a ramp (can be determined with PathPlanners.checkForRampStep())
	 * @param ascending true if a ramp step is ascending, false if it's descending, unused if rampStep is false
	 * @return the resulting Position at the step's conclusion
	 */
	public static Position resultingPosition(World world, Position pos, direction dir, boolean rampStep, boolean ascending)
	{
		Position resultingPos = new Position(pos);
		switch (dir)
		{
		case up:	resultingPos.y ++;	break;
		case down:	resultingPos.y --;	break;
		case left:	resultingPos.x --;	break;
		case right:	resultingPos.x ++;	break;
		}
		
		if (rampStep)
		{
			if (ascending)
			{
				//Two cases: the final step that will leave the stairs does not change the height, everything else does.
				if (world.hasThing(resultingPos) && world.getThingsAt(resultingPos).hasRamp())
					resultingPos.z ++;
			}
			else
			{
				//Two cases: the first step that will get on a ramp does not change the height, everything else does.
				if (world.hasThing(pos.x, pos.y, pos.z - 1) && world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp())
					resultingPos.z --;
			}
		}
		
		return resultingPos;
	}
}
