package world;

import static world.Terrain.terrainType.*;

public class Terrain {
	public enum terrainType
	{
		grass, dirt, water
	}
	
	boolean blocking;
	terrainType type;
	
	public Terrain(terrainType t)
	{
		type = t;
		setBlocking();
	}
	
	/**
	 * Change the terrain type
	 * @param t the new terrain type
	 */
	public void setTerrainType(terrainType t)
	{
		type = t;
		setBlocking();
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
	 * Determines whether or not a terrain type is blocking based on its name and updates
	 * accordingly
	 */
	private void setBlocking()
	{
		//add the names of any new non-blocking terrain types here
		if (type == grass || type == dirt)
			blocking = true;
		else
			blocking = false;
	}
	
}
