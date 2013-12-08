package world;

import static world.Terrain.terrainType.*;

public class Terrain {
	public enum terrainType
	{
		grass, dirt, air, rock
	}
	
	boolean blocking;
	boolean transparent;
	terrainType type;
	terrainType top;
	int texRow;
	int texCol;
	int texRowTop;
	int texColTop;
	
	public Terrain(terrainType t)
	{
		setTerrainType(t);
	}
	
	public Terrain(terrainType aType, terrainType aTop)
	{
		setTerrainType(aType, aTop);
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
		top = t;
		updateAttributes();
	}
	
	/**
	 * Change the terrain type
	 * @param t the new terrain type
	 */
	public void setTerrainType(terrainType aType, terrainType aTop)
	{
		type = aType;
		top = aTop;
		updateAttributes();
	}
	
	public int getTexRow()
	{
		return texRow * 5;
	}
	
	public int getTexCol()
	{
		return texCol * 4;
	}
	
	public int getTexRowTop()
	{
		return texRowTop * 5;
	}
	
	public int getTexColTop()
	{
		return texColTop * 4;
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
	 * Getter for transparent
	 * @return true if transparent
	 */
	public boolean isTransparent()
	{
		return transparent;
	}
	
	/**
	 * Updates the internal attributes of the terrain type.
	 * This must be called any time the terrain type is set.
	 */
	private void updateAttributes()
	{
		setBlocking();
		setTransparent();
		setTexPos();
	}
	
	/**
	 * Determines whether or not a terrain type is blocking and updates accordingly
	 */
	private void setBlocking()
	{
		//add the names of any new non-blocking terrain types here
		if (type == air)
			blocking = false;
		else
			blocking = true;
	}
	
	/**
	 * Determines whether or not a terrain type is transparent and updates accordingly
	 */
	private void setTransparent()
	{
		//add the names of any new transparent terrain types here
		if (type == air)
			transparent = true;
		else
			transparent = false;
	}
	
	/**
	 * Setup the texture row and column based on the terrain type
	 */
	private void setTexPos()
	{
		//Vertical textures
		switch (type)
		{
		case grass: 
			texRow = 0; texCol = 0; break;
		case dirt: 
			texRow = 0; texCol = 1; break;
		case rock:
			texRow = 0; texCol = 2; break;
			
		//Unset
		default:
			texRow = 0; texCol = 0; break;
		}
		
		//Horizontal textures
		switch (top)
		{
		case grass:
			texRowTop = 0; texColTop = 0; break;
		case dirt: 
			texRowTop = 0; texColTop = 1; break;
		case rock:
			texRowTop = 0; texColTop = 2; break;
		}
	}
}
