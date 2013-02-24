package world;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import things.Thing;
import entities.Agent;

import static world.Terrain.terrainType.*;

public class World {
	Terrain[][][] terrainGrid;
	Thing[][][] thingGrid;
	Agent[][][] agentGrid;
	ArrayList<Agent> agents;
	
	int PIXEL_SIZE = 4;
	
	//Textures
	private Texture hTerrainTexture;
	private Texture vTerrainTexture;
	
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
	
	public void loadTextures()
	{
		try {
			hTerrainTexture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/terrain/HTerrain.png"));
			vTerrainTexture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/terrain/VTerrain.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void renderWorld()
	{
		for (int k = 0; k < terrainGrid[0][0].length; k ++)
		{
			for (int j = 0; j < terrainGrid[0].length; j ++)
			{
				for (int i = 0; i < terrainGrid.length; i ++)
				{
					Terrain t = terrainGrid[i][j][k];
					
					if (t.getTerrainType() != air)
					{
						//Determine position on screen
						int x = PIXEL_SIZE*6 * (i - displayCenter[0]) + 400;
						int y = (PIXEL_SIZE*6 * (j - displayCenter[1]) + 300) - PIXEL_SIZE*6 * (displayCenter[2] - k);
						
						GL11.glPushMatrix();
							//Translate to screen position and bind appropriate texture
							GL11.glColor3f(1.0f, 1.0f, 1.0f);
							GL11.glEnable(GL11.GL_TEXTURE_2D);
							GL11.glTranslatef(x, y, 0);
							if (t.isVertical())
								vTerrainTexture.bind();
							else
								hTerrainTexture.bind();
							GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
					    	
					    	//Determine which part of the texture to use based on how many neighbors are air
					    	int texX = t.getTexCol();
					    	int texY = t.getTexRow();
					    	float tConv;
					    	if (t.isVertical())
					    	{
					    		if ((i - 1 < 0 || !terrainGrid[i-1][j][k].isVertical()) && 
					    				(i + 1 >= terrainGrid.length || !terrainGrid[i+1][j][k].isVertical()))
					    		{
					    			texY += 1;
					    		}
					    		else if (i - 1 < 0 || !terrainGrid[i-1][j][k].isVertical())
					    		{
					    			texX += 1;
					    			texY += 1;
					    		}
					    		else if (i + 1 >= terrainGrid.length || !terrainGrid[i+1][j][k].isVertical())
					    		{
					    			texX += 1;
					    		}
					    		
					    		tConv = 6.0f/128.0f;	//width and height of texture sheet
					    	}
					    	else
					    	{
					    		boolean topEmpty = j + 1 >= terrainGrid[0].length || terrainGrid[i][j+1][k].getTerrainType() == air,
					    			bottomEmpty = j - 1 < 0 || terrainGrid[i][j-1][k].getTerrainType() == air,
					    			rightEmpty = i + 1 >= terrainGrid.length || terrainGrid[i+1][j][k].getTerrainType() == air,
					    			leftEmpty = i - 1 < 0 || terrainGrid[i-1][j][k].getTerrainType() == air;
					    		
					    		if (topEmpty && bottomEmpty)
					    		{
					    			texY += 3;
					    		}
					    		else if (topEmpty)
					    		{
					    			//texY is unchanged, this case is required for the else case and organizational purposes
					    		}
					    		else if (bottomEmpty)
					    		{
					    			texY += 2;
					    		}
					    		else
					    		{
					    			texY += 1;
					    		}
					    		
					    		if (leftEmpty && rightEmpty)
					    		{
					    			texX += 3;
					    		}
					    		else if (leftEmpty)
					    		{
					    			//texX is unchanged, this case is required for the else case and organizational purposes
					    		}
					    		else if (rightEmpty)
					    		{
					    			texX += 2;
					    		}
					    		else
					    		{
					    			texX += 1;
					    		}
					    		
					    		tConv = 6.0f/256.0f;	//width and height of texture sheet
					    	}
					    	
					    	GL11.glBegin(GL11.GL_QUADS);
								GL11.glTexCoord2f(texX * tConv, texY*tConv + tConv);
								GL11.glVertex2f(0, 0);
								GL11.glTexCoord2f(texX*tConv + tConv, texY*tConv + tConv);
								GL11.glVertex2f(PIXEL_SIZE*6, 0);
								GL11.glTexCoord2f(texX*tConv + tConv, texY * tConv);
								GL11.glVertex2f(PIXEL_SIZE*6, PIXEL_SIZE*6);
								GL11.glTexCoord2f(texX*tConv, texY * tConv);
								GL11.glVertex2f(0, PIXEL_SIZE*6);
							GL11.glEnd();
							
						GL11.glPopMatrix();
					}
				}
			}
		}
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
	
	public void setTerrain(Terrain[][][] t)
	{
		for (int i = 0; i < t.length; i ++)
		{
			for (int j = 0; j < t[0].length; j ++)
			{
				for (int k = 0; k < t[0][0].length; k ++)
				{
					//bounds checking
					if (i < terrainGrid.length && j < terrainGrid[0].length && k < terrainGrid[0].length)
					{
						terrainGrid[i][j][k] = t[i][j][k];
					}
				}
			}
		}
	}
	
	public void rotateC()
	{
		//TODO: Rotate display center as well
		Terrain[][][] tempTerrain = new Terrain[terrainGrid.length][terrainGrid[0].length][terrainGrid[0][0].length];
		Thing[][][] tempThing = new Thing[thingGrid.length][thingGrid[0].length][thingGrid[0][0].length];
		Agent[][][] tempAgent = new Agent[agentGrid.length][agentGrid[0].length][agentGrid[0][0].length];
		for (int i = 0; i < terrainGrid.length; i ++)
		{
			for (int j = 0; j < terrainGrid.length; j ++)
			{
				for (int k = 0; k < terrainGrid.length; k ++)
				{
					tempTerrain[j][terrainGrid.length - 1 - i][k] = terrainGrid[i][j][k];
					tempThing[j][terrainGrid.length - 1 - i][k] = thingGrid[i][j][k];
					tempAgent[j][terrainGrid.length - 1 - i][k] = agentGrid[i][j][k];
				}
			}
		}
		
		terrainGrid = tempTerrain;
		thingGrid = tempThing;
		agentGrid = tempAgent;
	}
	
	public void rotateCC()
	{
		//TODO: Rotate display center as well
		Terrain[][][] tempTerrain = new Terrain[terrainGrid.length][terrainGrid[0].length][terrainGrid[0][0].length];
		Thing[][][] tempThing = new Thing[thingGrid.length][thingGrid[0].length][thingGrid[0][0].length];
		Agent[][][] tempAgent = new Agent[agentGrid.length][agentGrid[0].length][agentGrid[0][0].length];
		for (int i = 0; i < terrainGrid.length; i ++)
		{
			for (int j = 0; j < terrainGrid.length; j ++)
			{
				for (int k = 0; k < terrainGrid.length; k ++)
				{
					tempTerrain[terrainGrid[0].length - 1 - j][i][k] = terrainGrid[i][j][k];
					tempThing[thingGrid[0].length - 1 - j][i][k] = thingGrid[i][j][k];
					tempAgent[agentGrid[0].length - 1 - j][i][k] = agentGrid[i][j][k];
				}
			}
		}
		
		terrainGrid = tempTerrain;
		thingGrid = tempThing;
		agentGrid = tempAgent;
	}
}
