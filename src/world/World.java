package world;

import java.util.ArrayList;

import things.Thing;
import entities.Agent;

public class World {
	Terrain[][][] terrainGrid;
	Thing[][][] thingGrid;
	Agent[][][] agentGrid;
	ArrayList<Agent> agents;
	
	/**
	 * Determine if there is an agent at a certain location
	 * 
	 * @param x grid location
	 * @param y grid location
	 * @param z grid location
	 * @return true if the space is occupied
	 */
	public boolean isOccupied(int x, int y, int z)
	{
		return agentGrid[x][y][x] != null;
	}
	
	/**
	 * Determine if there is a thing at a certain location
	 * 
	 * @param x grid location
	 * @param y grid location
	 * @param z grid location
	 * @return true if the space has a thing in it
	 */
	public boolean hasThing(int x, int y, int z)
	{
		return thingGrid[x][y][z] != null;
	}
	
	/**
	 * Determine whether an agent can move onto a grid cell based on other agents,
	 * things and whether they can be crossed, and terrain blocking
	 * 
	 * @param x grid location
	 * @param y grid location
	 * @param z grid location
	 * @return true if an agent cannot move to the grid space space
	 */
	public boolean isBlocked(int x, int y, int z)
	{
		if (this.isOccupied(x,y,z))
			return true;
		else if (this.hasThing(x,y,z))
			return this.thingGrid[x][y][z].isBlocking();
		else
			return terrainGrid[x][y][z].isBlocking();
	}
	
}
