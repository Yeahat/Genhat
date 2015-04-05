package things;

import java.util.ArrayList;

import world.World;
import entities.Agent;
import entities.Agent.direction;

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
	 * Get the direction of a ramp (assuming there is a ramp in this cell)
	 * @return the direction of the ramp in this cell
	 */
	public direction getRampDir()
	{
		for (int i = 0; i < thingList.size(); i ++)
		{
			if (thingList.get(i).isRamp())
				return thingList.get(i).getDir();
		}
		return direction.left;	//this will return if there is no ramp, in which case the function shouldn't have been called anyway
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
	public void interact(Agent agent, World world)
	{
		thingList.get(thingList.size() - 1).interact(agent, world);
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
