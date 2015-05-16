package utils.planners;

import world.Position;
import world.Terrain.terrainType;
import world.World;
import entities.Agent;
import entities.Agent.Direction;
import static entities.Agent.Direction.*;
import static things.Thing.ConnectionContext.*;

public class PathPlannerUtils {	
	
	/**
	 * General movement classes, allowing movement as follows:
	 * 
	 * SimpleStepping: xy-planar movement only
	 * Stepping: xy-planar stepping, with added movement up and down ramps
	 * Climbing: all of Stepping, with added climbing on climbingSurfaces 
	 * Jumping: all movement, with added jumping up or down one space in the z direction
	 */
	public enum MovementClass
	{
		SimpleStepping, Stepping, Climbing, Jumping
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
	public static boolean canStep(Agent agent, World world, Position pos, Direction dir)
	{
		int x = pos.x;
		int y = pos.y;
		
		switch (dir)
		{
		case Up:
			y += 1;
		break;
		case Down:
			y -= 1;
		break;
		case Left:
			x -= 1;
		break;
		case Right:
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

	public static boolean isContinuingDescent(Agent agent, World world, Position pos, Direction dir)
	{
		Position posBelow = new Position(pos);
		posBelow.z --;
		
		Position forwardPos = new Position(pos);
		switch (dir)
		{
		case Up:	forwardPos.y += 1;	break;
		case Down:	forwardPos.y -= 1;	break;
		case Left:	forwardPos.x -= 1;	break;
		case Right:	forwardPos.x += 1;	break;
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
	public static boolean canStepHorizontalRamp(Agent agent, World world, Position pos, Direction dir, boolean ascending, boolean continuingDescent)
	{
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		
		switch (dir)
		{
		case Left:	x -= 1;	break;
		case Right:	x += 1;	break;
		default: return false;
		}
		
		if (!ascending)
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
			//NOTE: added k==z as test to prevent stacked ramps from allowing passage
			if (k == z && world.hasThing(x, y, k) && (world.getThingsAt(x, y, k).hasRamp() && (world.getThingsAt(x, y, k).getRampDir() == Left || world.getThingsAt(x, y, k).getRampDir() == Right)))
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
		//ground check
		if (!world.isCrossable(x, y, z))
		{
			return false;
		}
		
		return true;
	}
	
	public static boolean canStepVerticalRamp(Agent agent, World world, Position pos, Direction dir)
	{
		//ascending
		if (dir == Up)
		{
			int z = pos.z;
			int maxZ = pos.z + agent.getHeight();
			Position checkPos = new Position(pos);
			checkPos.y ++;

			//climbing up case
			if (hasRampWithDir(world, checkPos, Up))
			{
				for (int k = z + 1; k < maxZ; k ++)
				{
					if (!world.isInBounds(pos.x, pos.y, k) || world.isBlocked(pos.x, pos.y, k))
						return false;
				}
				return true;
			}
			
			//stepping off case
			else
			{
				return canStep(agent, world, pos, Up);
			}
		}
		
		//descending
		else if (dir == Down)
		{
			Position checkPos = new Position(pos);
			checkPos.z --;
			//stepping on case
			if (world.isInBounds(checkPos) && (world.getTerrainAt(checkPos).isBlocking()
					|| (world.hasThing(checkPos) && world.getThingsAt(checkPos).isCrossable())))
			{
				int z = pos.z;
				int maxZ = pos.z + agent.getHeight();
				for (int k = z; k < maxZ; k ++)
				{
					if (!world.isInBounds(pos.x, pos.y - 1, k) || world.isBlocked(pos.x, pos.y - 1, k))
						return false;
				}
				return true;
			}
			
			//climbing down case
			else
			{
				return (world.isInBounds(pos.x, pos.y, pos.z - 1) && !world.isBlocked(pos.x, pos.y, pos.z - 1));
			}
		}
		
		//sidestepping
		else
		{
			Position rampPos = new Position(pos);
			rampPos.y ++;
			rampPos.z --;
			
			//first check connection context of ramp at starting position for blocking railings
			if (world.getThingsAt(rampPos).getRampConnectionContext() == Standalone
					|| (dir == Left && world.getThingsAt(rampPos).getRampConnectionContext() == Start)
					|| (dir == Right && world.getThingsAt(rampPos).getRampConnectionContext() == End))
			{
				return false;
			}
			
			//next check that there is a ramp at the target position
			if (dir == Left)
				rampPos.x --;
			else
				rampPos.x ++;
			if (hasRampWithDir(world, rampPos, Up))
			{
				//check if the connection context for the target ramp has a blocking railing
				if (world.getThingsAt(rampPos).getRampConnectionContext() == Standalone
						|| (dir == Left && world.getThingsAt(rampPos).getRampConnectionContext() == End)
						|| (dir == Right && world.getThingsAt(rampPos).getRampConnectionContext() == Start))
				{
					return false;
				}
				
				//finally check to see if the space is blocked
				int z = pos.z;
				int maxZ = agent.getHeight();
				for (int k = z; k < maxZ; k ++)
				{
					if (!world.isInBounds(rampPos.x, pos.y, k) || world.isBlocked(rampPos.x, pos.y, k))
						return false;
				}
				return true;
			}
			else
				return false;
		}
	}
	
	public static boolean canClimb(Agent agent, World world, Position pos, Direction dir)
	{
		//ascending
		if (dir == Up)
		{
			int z = pos.z;
			int maxZ = pos.z + agent.getHeight();
			Position checkPos = new Position(pos);
			checkPos.y ++;

			//climbing up case
			if (hasClimbingSurfaceWithDir(world, checkPos, Up))
			{
				for (int k = z + 1; k < maxZ; k ++)
				{
					if (!world.isInBounds(pos.x, pos.y, k) || world.isBlocked(pos.x, pos.y, k))
						return false;
				}
				return true;
			}
			
			//top out case
			else
			{
				return canStep(agent, world, pos, Up);
			}
		}
		
		//descending
		else if (dir == Down)
		{
			Position checkPos = new Position(pos);
			checkPos.z --;
			//lower on edge case
			if (world.isInBounds(checkPos) && (world.getTerrainAt(checkPos).isBlocking()
					|| (world.hasThing(checkPos) && world.getThingsAt(checkPos).isCrossable())))
			{
				int z = pos.z;
				int maxZ = pos.z + agent.getHeight();
				for (int k = z; k < maxZ; k ++)
				{
					if (!world.isInBounds(pos.x, pos.y - 1, k) || world.isBlocked(pos.x, pos.y - 1, k))
						return false;
				}
				return true;
			}
			
			//climbing down case
			else
			{
				return (world.isInBounds(pos.x, pos.y, pos.z - 1) && !world.isBlocked(pos.x, pos.y, pos.z - 1));
			}
		}
		
		//sidestepping
		else
		{
			Position climbingSurfacePos = new Position(pos);
			climbingSurfacePos.y ++;
			climbingSurfacePos.z --;
			
			//first check connection context of climbing surface at starting position for uncrossable edges
			if (world.getThingsAt(climbingSurfacePos).getClimbingSurfaceConnectionContext() == Standalone
					|| (dir == Left && world.getThingsAt(climbingSurfacePos).getClimbingSurfaceConnectionContext() == Start)
					|| (dir == Right && world.getThingsAt(climbingSurfacePos).getClimbingSurfaceConnectionContext() == End))
			{
				return false;
			}
			
			//next check that there is a climbing surface at the target position
			if (dir == Left)
				climbingSurfacePos.x --;
			else
				climbingSurfacePos.x ++;
			if (hasClimbingSurfaceWithDir(world, climbingSurfacePos, Up))
			{
				//check if the connection context for the target climbing surface has an uncrossable edge
				if (world.getThingsAt(climbingSurfacePos).getClimbingSurfaceConnectionContext() == Standalone
						|| (dir == Left && world.getThingsAt(climbingSurfacePos).getClimbingSurfaceConnectionContext() == End)
						|| (dir == Right && world.getThingsAt(climbingSurfacePos).getClimbingSurfaceConnectionContext() == Start))
				{
					return false;
				}
				
				//finally check to see if the space is blocked
				int z = pos.z;
				int maxZ = agent.getHeight();
				for (int k = z; k < maxZ; k ++)
				{
					if (!world.isInBounds(climbingSurfacePos.x, pos.y, k) || world.isBlocked(climbingSurfacePos.x, pos.y, k))
						return false;
				}
				return true;
			}
			else
				return false;
		}
	}

	private static boolean hasRampWithDir(World world, Position pos, Direction dir)
	{
		return world.hasThing(pos) && world.getThingsAt(pos).hasRamp() && world.getThingsAt(pos).getRampDir() == dir;
	}
	
	private static boolean hasClimbingSurfaceWithDir(World world, Position pos, Direction dir)
	{
		return world.hasThing(pos) && world.getThingsAt(pos).hasClimbingSurface() && world.getThingsAt(pos).getClimbingSurfaceDir() == dir;
	}
	
	/**
	 * Determine if a step in a given direction should be treated as a horizontal ramp step or not, and if it is a horizontal ramp step, 
	 * determine if it is ascent or descent
	 * 
	 * @param world the world
	 * @param pos starting position of the agent
	 * @param dir the direction of the step
	 * @return {true if the agent should use a horizontal ramp step, true if the ramp step is ascending}
	 */
	public static boolean[] checkForHorizontalRampStep(World world, Position pos, Direction dir)
	{
		//on ramp case will always require a horizontal ramp step
		if (isOnRampHorizontal(world, pos))
		{
			boolean[] result = new boolean[2];
			result[0] = true;
			result[1] = (dir == world.getThingsAt(pos.x, pos.y, pos.z - 1).getRampDir());
			return result;
		}
		
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		
		int xt = x; //target pos x
		switch (dir)
		{
		case Left:	xt = x - 1;	break;
		case Right:	xt = x + 1;	break;
		default: boolean[] result = {false, false}; return result; //stepping on to a horizontal ramp can only happen with left/right steps
		}
		
		boolean isRampStep = false;
		boolean ascending = false;
		Direction oppositeDir = Right;
		if (dir == Right)
			oppositeDir = Left;
		
		//check for ramp in front (starting ascent)
		if (world.hasThing(xt, y, z) && world.getThingsAt(xt, y, z).hasRamp()
				&& world.getThingsAt(xt, y, z).getRampDir() == dir)
		{
			isRampStep = true;
			ascending = true;
		}
		//check for ramp in front and below (starting descent)
		else if (world.hasThing(xt, y, z - 1) && world.getThingsAt(xt, y, z - 1).hasRamp()
				&& world.getThingsAt(xt, y, z - 1).getRampDir() == oppositeDir)
		{
			isRampStep = true;
		}
		
		boolean result[] = {isRampStep, ascending};
		return result;
	}
	
	/**
	 * Determine if a step in a given direction should be treated as a ramp step or not
	 * @param world the world
	 * @param pos starting position of the agent
	 * @param dir the direction of the step
	 * @return true if the agent should use a vertical ramp step
	 */
	public static boolean checkForVerticalRampStep(World world, Position pos, Direction dir)
	{
		//on ramp case will always require a vertical ramp step
		if (isOnRampVertical(world, pos))
			return true;
	    
		switch (dir)
	    {
		case Up:
			//check for a vertical ramp in front
			Position frontPos = new Position(pos);
			frontPos.y ++;
			return world.isInBounds(frontPos) && world.hasThing(frontPos)
					&& world.getThingsAt(frontPos).hasRamp() && world.getThingsAt(frontPos).getRampDir() == Up;
		case Down:
			//check for a vertical ramp below and a completely open space in front
			Position belowPos = new Position(pos);
			belowPos.z --;
			return world.isInBounds(belowPos) && world.hasThing(belowPos)
					&& world.getThingsAt(belowPos).hasRamp() && world.getThingsAt(belowPos).getRampDir() == Up
					&& world.isInBounds(pos.x, pos.y - 1, pos.z - 1) && world.getTerrainAt(pos.x, pos.y - 1, pos.z - 1).getTerrainType() == terrainType.air
					&& (!world.hasThing(pos.x, pos.y - 1, pos.z) || !world.getThingsAt(pos.x, pos.y - 1, pos.z).isCrossable());
		default:
			return false; //left/right step will never be a vertical ramp step unless you're starting on a vertical ramp
	    }
	}
	
	/**
	 * Determine if a step in a given direction should be treated as climbing or not
	 * @param world the world
	 * @param pos starting position of the agent
	 * @param dir the direction of the step
	 * @return true if the agent should climb
	 */
	public static boolean checkForClimb(World world, Position pos, Direction dir)
	{
		//on ramp case will always require a vertical ramp step
		if (isOnClimbingSurface(world, pos))
			return true;
	    
		switch (dir)
	    {
		case Up:
			//check for a climbing surface in front
			Position frontPos = new Position(pos);
			frontPos.y ++;
			return world.isInBounds(frontPos) && world.hasThing(frontPos)
					&& world.getThingsAt(frontPos).hasClimbingSurface() && world.getThingsAt(frontPos).getClimbingSurfaceDir() == Up;
		case Down:
			//check for a climbing surface below and a completely open space in front
			Position belowPos = new Position(pos);
			belowPos.z --;
			return world.isInBounds(belowPos) && world.hasThing(belowPos)
					&& world.getThingsAt(belowPos).hasClimbingSurface() && world.getThingsAt(belowPos).getClimbingSurfaceDir() == Up
					&& world.isInBounds(pos.x, pos.y - 1, pos.z - 1) && world.getTerrainAt(pos.x, pos.y - 1, pos.z - 1).getTerrainType() == terrainType.air
					&& (!world.hasThing(pos.x, pos.y - 1, pos.z) || !world.getThingsAt(pos.x, pos.y - 1, pos.z).isCrossable());
		default:
			return false; //left/right step will never be climbing unless you're starting on a climbing surface
	    }
	}
	
	public static boolean isOnRampHorizontal(World world, Position pos)
	{
		return (world.hasThing(pos.x, pos.y, pos.z - 1) && world.getThingsAt(pos.x, pos.y, pos.z - 1).hasRamp()
		&& (world.getThingsAt(pos.x, pos.y, pos.z - 1).getRampDir() == Left || world.getThingsAt(pos.x, pos.y, pos.z - 1).getRampDir() == Right));
	}
	
	public static boolean isOnRampVertical(World world, Position pos)
	{
		return world.isInBounds(pos.x, pos.y, pos.z - 1) && world.getTerrainAt(pos.x, pos.y, pos.z - 1).getTerrainType() == terrainType.air
				&& (!world.hasThing(pos.x, pos.y, pos.z) || !world.getThingsAt(pos.x, pos.y, pos.z).isCrossable())
				&& world.isInBounds(pos.x, pos.y + 1, pos.z - 1) && world.hasThing(pos.x, pos.y + 1, pos.z - 1)
				&& world.getThingsAt(pos.x, pos.y + 1, pos.z - 1).hasRamp() && world.getThingsAt(pos.x, pos.y + 1, pos.z - 1).getRampDir() == Up;
	}
	
	public static boolean isOnClimbingSurface(World world, Position pos)
	{
		return world.isInBounds(pos.x, pos.y, pos.z - 1) && world.getTerrainAt(pos.x, pos.y, pos.z - 1).getTerrainType() == terrainType.air
				&& (!world.hasThing(pos.x, pos.y, pos.z) || !world.getThingsAt(pos.x, pos.y, pos.z).isCrossable())
				&& world.isInBounds(pos.x, pos.y + 1, pos.z - 1) && world.hasThing(pos.x, pos.y + 1, pos.z - 1)
				&& world.getThingsAt(pos.x, pos.y + 1, pos.z - 1).hasClimbingSurface() && world.getThingsAt(pos.x, pos.y + 1, pos.z - 1).getClimbingSurfaceDir() == Up;
	}

	/**
	 * Calculates the resulting position if an agent were to take a simple step from a given position in a given direction.
	 * Note that this assumes the step type is valid (this method does no collision detection, etc.).
	 * 
	 * @param world the world in which the step is taking place
	 * @param pos the position from which the step is initiated
	 * @param dir the direction to step
	 * @return the resulting Position at the step's conclusion
	 */
	public static Position simulateSimpleStep(World world, Position pos, Direction dir)
	{
		Position resultingPos = new Position(pos);
		switch (dir)
		{
		case Up:	resultingPos.y ++;	break;
		case Down:	resultingPos.y --;	break;
		case Left:	resultingPos.x --;	break;
		case Right:	resultingPos.x ++;	break;
		}
		
		return resultingPos;
	}
	
	/**
	 * Calculates the resulting position if an agent were to take a horizontal ramp step from a given position in a given direction.
	 * Note that this assumes the step type is valid (this method does no collision detection, etc.).
	 * 
	 * @param world the world in which the step is taking place
	 * @param pos the position from which the step is initiated
	 * @param dir the direction to step
	 * @param rampStep true if the step involves a ramp (can be determined with PathPlanners.checkForRampStep())
	 * @param ascending true if a ramp step is ascending, false if it's descending, unused if rampStep is false
	 * @return the resulting Position at the step's conclusion
	 */
	@SuppressWarnings("incomplete-switch")
	public static Position simulateHorizontalRampStep(World world, Position pos, Direction dir, boolean ascending)
	{
		Position resultingPos = new Position(pos);
		switch (dir)
		{
		case Left:	resultingPos.x --;	break;
		case Right:	resultingPos.x ++;	break;
		}
		
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
		
		return resultingPos;
	}
	
	/**
	 * Calculates the resulting position if an agent were to take a vertical ramp step from a given position in a given direction.
	 * Note that this assumes the step type is valid (this method does no collision detection, etc.).
	 * 
	 * @param world the world in which the step is taking place
	 * @param pos the position from which the step is initiated
	 * @param dir the direction to step
	 * @return the resulting Position at the step's conclusion
	 */
	public static Position simulateVerticalRampStep(World world, Position pos, Direction dir)
	{
		Position resultingPosition = new Position(pos);
		switch (dir)
		{
		case Up:
			//first case: climbing
			Position checkPosUp = new Position(pos.x, pos.y + 1, pos.z);
			if (hasRampWithDir(world, checkPosUp, Up))
			{
				resultingPosition.z ++;
			}
			//second case: stepping off top of ramp
			else
			{
				resultingPosition.y ++;
			}
		break;
		case Down:
			//first case: stepping on to ramp
			Position checkPosDown = new Position(pos.x, pos.y, pos.z - 1);
			if (hasRampWithDir(world, checkPosDown, Up))
			{
				resultingPosition.y --;
			}
			//second case: climbing down
			else
			{
				resultingPosition.z --;
			}
		break;
		case Left:
			resultingPosition.x --;
		break;
		case Right:
			resultingPosition.x ++;
		break;
		}
		
		return resultingPosition;
	}
	
	/**
	 * Calculates the resulting position if an agent were to climb from a given position in a given direction.
	 * Note that this assumes that climbing is valid (this method does no collision detection, etc.).
	 * 
	 * @param world the world in which the climb action is taking place
	 * @param pos the position from which the climb is initiated
	 * @param dir the direction to climb
	 * @return the resulting Position at the climb's conclusion
	 */
	public static Position simulateClimb(World world, Position pos, Direction dir)
	{
		Position resultingPosition = new Position(pos);
		switch (dir)
		{
		case Up:
			Position checkPosUp = new Position(pos.x, pos.y + 1, pos.z);
			//first case: standard climbing
			if (hasClimbingSurfaceWithDir(world, checkPosUp, Up))
			{
				resultingPosition.z ++;
			}
			//second case: topping out
			else
			{
				resultingPosition.y ++;
			}
		break;
		case Down:
			//first case: lowering onto the climb
			Position checkPosDown = new Position(pos.x, pos.y, pos.z - 1);
			if (hasClimbingSurfaceWithDir(world, checkPosDown, Up))
			{
				resultingPosition.y --;
			}
			//second case: standard climbing
			else
			{
				resultingPosition.z --;
			}
		break;
		case Left:
			resultingPosition.x --;
		break;
		case Right:
			resultingPosition.x ++;
		break;
		}
		
		return resultingPosition;
	}
}
