package things;

import java.util.ArrayList;

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
	 * Add a new thing to the thing cell, always gets added on top (i.e. the back of the list)
	 * @param t the thing to be added
	 */
	public void addThing(Thing t)
	{
		thingList.add(t);
	}
	
	/**
	 * Remove a thing from the thing cell
	 */
	public void removeThing(Thing t)
	{
		thingList.remove(t);
	}
}
