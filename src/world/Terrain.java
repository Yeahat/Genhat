package world;

import static world.Terrain.terrainType.*;

public class Terrain {
	public enum terrainType
	{
		grass, dirt, water, air, cliff
	}
	
	boolean blocking;
	boolean vertical;
	terrainType type;
	int texRow;
	int texCol;
	
	public Terrain(terrainType t)
	{
		setTerrainType(t);
	}
	
	/**
	 * Getter for the terrain type
	 * @return the terrain type
	 */
	public terrainType getTerrainType()
	{
		return type;
	}
	
	/**
	 * Change the terrain type
	 * @param t the new terrain type
	 */
	public void setTerrainType(terrainType t)
	{
		type = t;
		updateAttributes();
	}
	
	public int getTexRow()
	{
		int height;
		if (vertical)
			height = 2;
		else
			height = 4;
		return texRow * height;
	}
	
	public int getTexCol()
	{
		int width;
		if (vertical)
			width = 2;
		else
			width = 4;
		return texCol * width;
	}
	
	/**
	 * Getter for a character representation of the terrain type
	 * @return a char representing the terrain
	 */
	public char getChar()
	{
		switch (type)
		{
		case grass:
			return ',';
		case dirt:
			return ':';
		case water:
			return '~';
		default:
			return ' ';
		}
	}
	
	/**
	 * Getter for blocking
	 * @return true if blocking
	 */
	public boolean isBlocking()
	{
		return blocking;
	}
	
	/**
	 * Getter for vertical option
	 * @return true if the terrain is vertical
	 */
	public boolean isVertical()
	{
		return vertical;
	}
	
	/**
	 * Updates the internal attributes of the terrain type.
	 * This must be called any time the terrain type is set.
	 */
	private void updateAttributes()
	{
		setBlocking();
		setVertical();
		setTexPos();
	}
	
	/**
	 * Determines whether or not a terrain type is blocking and updates accordingly
	 */
	private void setBlocking()
	{
		//add the names of any new non-blocking terrain types here
		if (type == grass || type == dirt)
			blocking = false;
		else
			blocking = true;
	}
	
	/**
	 * Determines whether or not a terrain type is vertical and updates accordingly
	 */
	private void setVertical()
	{
		//add the names of any new vertical terrain types here
		if (type == cliff)
			vertical = true;
		else
			vertical = false;
	}
	
	/**
	 * Setup the texture row and column based on the terrain type
	 */
	private void setTexPos()
	{
		switch (type)
		{
		//Horizontal textures
		case grass: 
			texRow = 0; texCol = 0; break;
		case dirt: 
			texRow = 0; texCol = 1; break;
		
		//Vertical textures
		case cliff:
			texRow = 0; texCol = 0; break;
			
		//Unset
		default:
			texRow = 0; texCol = 0; break;
		}
	}
}
