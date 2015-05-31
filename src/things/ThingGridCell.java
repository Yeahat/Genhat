package things;

import java.util.ArrayList;

import things.Thing.ConnectionContext;
import world.Map;
import entities.Agent;
import entities.Agent.Direction;

public class ThingGridCell {
	private ArrayList<Thing> thingList;
	
	public ThingGridCell()
	{
		thingList = new ArrayList<Thing>();
	}
	
	public boolean isEmpty()
	{
		if (thingList.size() == 0)
			return true;
		return false;
	}
	
	public boolean isBlocking()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isBlocking())
				return true;
		}
		return false;
	}
	
	public boolean isCrossable()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isCrossable())
				return true;
		}
		return false;
	}
	
	public boolean hasRamp()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isRamp())
				return true;
		}
		return false;
	}
	
	public boolean hasClimbingSurface()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isClimbingSurface())
				return true;
		}
		return false;
	}
	
	public boolean hasFullBlock()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).fullBlock)
				return true;
		}
		return false;
	}
	
	public boolean hasTallBlock()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).tallBlock)
				return true;
		}
		return false;
	}
	
	public int getBlockingWidth()
	{
		int maxWidth = 0;
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).fullBlock)
			{
				if (thingList.get(i).blockingWidth > maxWidth)
					maxWidth = thingList.get(i).blockingWidth;
			}
		}
		return maxWidth;
	}
	
	/**
	 * Get the direction of a ramp (assumes there is a ramp in this cell)
	 * @return the direction of the first ramp in this cell
	 */
	public Direction getRampDir()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isRamp())
				return thingList.get(i).getDir();
		}
		return Direction.Left;	//this will return if there is no ramp, in which case the function shouldn't have been called anyway
	}
	
	/**
	 * Get the direction of a climbingSurface (assumes there is a ramp in this cell)
	 * @return the direction of the first ramp in this cell
	 */
	public Direction getClimbingSurfaceDir()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isClimbingSurface())
				return thingList.get(i).getDir();
		}
		return Direction.Up;	//this will return if there is no ramp, in which case the function shouldn't have been called anyway
	}
	
	/**
	 * Get the (horizontal) connection context of a ramp (assumes there is a ramp in this cell)
	 * @return the connection context of the first ramp in this cell
	 */
	public ConnectionContext getRampConnectionContext()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isRamp())
				return ((Ramp)(thingList.get(i))).getHorizontalConnection();
		}
		return ConnectionContext.Standalone;	//this will return if there is no ramp, in which case the function shouldn't have been called anyway
	}
	
	/**
	 * Get the (horizontal) connection context of a climbing surface (assumes there is a climbing surface in this cell)
	 * @return the connection context of the first climbing surface in this cell
	 */
	public ConnectionContext getClimbingSurfaceConnectionContext()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isClimbingSurface())
				return ((ClimbingSurface)(thingList.get(i))).getHorizontalConnection();
		}
		return ConnectionContext.Standalone;	//this will return if there is no ramp, in which case the function shouldn't have been called anyway
	}
	
	public boolean isTransparent()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (!thingList.get(i).isTransparent())
			{
				return false;
			}
		}
		return true;
	}
	
	public void renderThings(int pixelSize, int terrainTextureSize)
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			thingList.get(i).renderThing(pixelSize, terrainTextureSize);
		}
	}
	
	/**
	 * Interact with the things in this cell, currently interacts only with the thing on top.
	 */
	public void interact(Agent agent, Map world)
	{
		for (int i = thingList.size() - 1; i >= 0; i --)
		{
			if (thingList.get(i).interact(agent, world))
				break;
		}
	}
	
	/**
	 * Add a new thing to the thing cell, always gets added on top (i.e. the back of the list)
	 * @param t the thing to be added
	 */
	public void addThing(Thing t)
	{
		thingList.add(t);
	}
	
	/**
	 * Remove a thing from the thing cell
	 * @param t the thing to be removed
	 */
	public void removeThing(Thing t)
	{
		thingList.remove(t);
	}
	
	/**
	 * Getter for the thing list
	 * @return the list of things in this cell
	 */
	public ArrayList<Thing> getThings()
	{
		return thingList;
	}
}
