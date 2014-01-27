package world;

import static world.Terrain.terrainType.*;

public class Terrain {
	public enum terrainType
	{
		grass, dirt, air, rock, thatch, glass, woodFloor
	}
	
	boolean blocking;
	boolean transparent;
	boolean unblendedVertical;
	boolean unblendedHorizontal;
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
	 * Getter for the terrain type top
	 * @return the terrain type top
	 */
	public terrainType getTerrainTop()
	{
		return top;
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
	 * Getter for unblendedVertical
	 * @return true if unblendedVertical
	 */
	public boolean isUnblendedVertical()
	{
		return unblendedVertical;
	}
	
	/**
	 * Getter for unblendedHorizontal
	 * @return true if unblendedHorizontal
	 */
	public boolean isUnblendedHorizontal()
	{
		return unblendedHorizontal;
	}
	
	/**
	 * Updates the internal attributes of the terrain type.
	 * This must be called any time the terrain type is set.
	 */
	private void updateAttributes()
	{
		setBlocking();
		setTransparent();
		setUnblended();
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
		if (type == air || type == glass)
			transparent = true;
		else
			transparent = false;
	}
	
	/**
	 * Determines whether or not a terrain type should be blended with surrounding terrain types and updates accordingly
	 */
	private void setUnblended()
	{
		//add the names of any new unblended vertical terrain types here
		if (type == glass)
			unblendedVertical = true;
		else
			unblendedVertical = false;
		
		//add the names of any new unblended horizontal terrain types here
		if (type == glass)
			unblendedHorizontal = true;
		else
			unblendedHorizontal = false;
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
		case glass:
			texRow = 0; texCol = 3; break;
			
		//Unset (blank)
		default:
			texRow = 2; texCol = 3; break;
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
		case thatch:
			texRowTop = 0; texColTop = 3; break;
		case woodFloor:
			texRowTop = 1; texColTop = 0; break;
		
		//Unset (blank)
		default:
			texRowTop = 2; texColTop = 3; break;
		}
	}
}
