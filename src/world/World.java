package world;

import java.util.ArrayList;

import things.Thing;
import entities.Agent;

public class World {
	Terrain[][][] terrainGrid;
	Thing[][][] thingGrid;
	Agent[][][] agentGrid;
	ArrayList<Agent> agents;
	
	int[] displayCenter = new int[3];
	
	/**
	 * Constructor, initializes display center to the center of the world
	 * 
	 * @param xSize world length
	 * @param ySize world width
	 * @param zSize world height
	 */
	public World(int xSize, int ySize, int zSize)
	{
		terrainGrid = new Terrain[xSize][ySize][zSize];
		thingGrid = new Thing[xSize][ySize][zSize];
		agentGrid = new Agent[xSize][ySize][zSize];
		
		displayCenter[0] = (int)xSize/2;
		displayCenter[1] = (int)ySize/2;
		displayCenter[2] = (int)zSize/2;
	}
	
	/**
	 * Constructor, sets initial display center
	 * 
	 * @param xSize world length
	 * @param ySize world width
	 * @param zSize world height
	 * @param center (x,y,z) center of the screen
	 */
	public World(int xSize, int ySize, int zSize, int[] center)
	{
		terrainGrid = new Terrain[xSize][ySize][zSize];
		thingGrid = new Thing[xSize][ySize][zSize];
		agentGrid = new Agent[xSize][ySize][zSize];
		
		displayCenter[0] = center[0];
		displayCenter[1] = center[1];
		displayCenter[2] = center[2];
	}
	
	public String displayWorld()
	{
		String str = "";
		
		return str;
	}
	
	/**
	 * Add agents to the agent list and the agent grid
	 * @param newAgents agents to be added to the world
	 */
	public void addAgents(ArrayList<Agent> newAgents)
	{
		agents.addAll(newAgents);
		for (int i = 0; i < newAgents.size(); i ++)
		{
			int[] pos = newAgents.get(i).getPos();
			agentGrid[pos[0]][pos[1]][pos[2]] = newAgents.get(i);
		}
	}
	
	/**
	 * Setter for the display center
	 * 
	 * @param center the new display center
	 */
	public void setDisplayCenter(int[] center)
	{
		displayCenter[0] = center[0];
		displayCenter[1] = center[1];
		displayCenter[2] = center[2];
	}
	
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
