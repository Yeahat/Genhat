package world;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import things.Thing;
import things.ThingGridCell;
import utils.display.DisplayText;
import entities.Agent;
import entities.Hero;
import entities.Placeholder;
import static world.Terrain.terrainType.*;
import static world.World.TimeOfDay.*;
import static world.World.ControlState.*;

public class World
{
	Grid<Terrain> terrainGrid;
	Grid<ThingGridCell> thingGrid;
	Grid<Agent> agentGrid;
	Grid<Float> lightModGrid;
	ArrayList<Agent> agents;
	ArrayList<Thing> things;
	ArrayList<Thing> lightSources;
	ArrayList<Thing> antiLightSources;
	private int width;
	private int depth;
	private int height;
	
	//TODO: refactor this stuff to a new Game State class
	Hero player;
	public enum TimeOfDay
	{
		Sunrise, Morning, Midday, Afternoon, Sunset, Night;
	}
	private TimeOfDay tod;
	
	//rendering constants
	private final int pixelSize = 2;
	private final int textureSize = 16;
	private final int textureSheetSize = 256;
	private final int quadVertexMax = pixelSize*textureSize;
	private final float tConv = ((float)textureSize)/((float)textureSheetSize);
	private final float tConvQuarterAdjustment = tConv/2.0f;
	
	//Textures
	public Texture textTexture;
	public ArrayList<Texture> hTerrainTextures = new ArrayList<Texture>();
	public ArrayList<Texture> vTerrainTextures = new ArrayList<Texture>();
	
	float[] displayCenter = new float[2];
	private boolean cameraLockV = false;
	private boolean cameraLockH = false;
	
	//Text box
	private boolean textBoxActive;
	private DisplayText textDisplay;
	
	//control state
	public enum ControlState
	{
		walking, talking;
	}
	private ControlState cs;
	
	/**
	 * Constructor, initializes display center to the center of the world
	 * 
	 * @param xSize world length
	 * @param ySize world width
	 * @param zSize world height
	 */
	public World(int xSize, int ySize, int zSize)
	{		
		this.init(xSize, ySize, zSize);
		
		displayCenter[0] = (float)xSize/2;
		displayCenter[1] = (float)ySize/2 + (float)zSize/2;
	}
	
	/**
	 * Constructor, sets initial display center
	 * 
	 * @param xSize world length
	 * @param ySize world width
	 * @param zSize world height
	 * @param center (x,y,z) center of the screen in grid coordinates
	 */
	public World(int xSize, int ySize, int zSize, int[] center)
	{
		this.init(xSize, ySize, zSize);
		
		displayCenter[0] = center[0];
		displayCenter[1] = center[1] + center[2];
	}
	
	/**
	 * Constructor, sets initial display center
	 * 
	 * @param xSize world length
	 * @param ySize world width
	 * @param zSize world height
	 * @param displayCenter (x,y) center of the screen in display coordinates
	 */
	public World(int xSize, int ySize, int zSize, float[] displayCenter)
	{
		this.init(xSize, ySize, zSize);
		
		this.displayCenter[0] = displayCenter[0];
		this.displayCenter[1] = displayCenter[1] + displayCenter[2];
	}
	
	private void init(int xSize, int ySize, int zSize)
	{
		terrainGrid = new Grid<Terrain>(xSize, ySize, zSize);
		thingGrid = new Grid<ThingGridCell>(xSize, ySize, zSize);
		agentGrid = new Grid<Agent>(xSize, ySize, zSize);
		lightModGrid = new Grid<Float>(xSize, ySize, zSize);
		
		width = xSize;
		depth = ySize;
		height = zSize;
		
		agents = new ArrayList<Agent>();
		things = new ArrayList<Thing>();
		lightSources = new ArrayList<Thing>();
		antiLightSources = new ArrayList<Thing>();
		
		textBoxActive = false;
		textDisplay = new DisplayText();
		
		setTod(Midday);
		setCs(walking);
	}
	
	public void loadTextures()
	{
		try {
			textTexture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/fonts/text.png"));
		} catch (IOException e) {e.printStackTrace();}
		
		Terrain.LoadHTextures(hTerrainTextures);
		Terrain.LoadVTextures(vTerrainTextures);
	}
	
	/**
	 * Run an update on all things active in the world
	 */
	public void updateThings()
	{
		for (int i = 0; i < things.size(); i ++)
		{
			things.get(i).update();
		}
	}
	
	/**
	 * Run an update on all agents active in the world
	 */
	public void updateAgents()
	{
		for (int i = 0; i < agents.size(); i ++)
		{
			agents.get(i).executeAction(this);
			agents.get(i).decideNextAction(this);
		}
	}
	
	/**
	 * Update the maximum world height to be rendered depending on whether the Hero Agent has a roof overhead
	 */
	private int updateHeightMax()
	{
		if (player == null)
		{
			return height;
		}
		
		int x = player.getPos().x;
		int y = player.getPos().y;
		int z = player.getPos().z;
		
		//roof check
		for (int k = z; k < height; k ++)
		{
			if (terrainGrid.get(x, y, z).getTerrainType() != air)
			{
				//adjustment for standing on stairs
				if (z - 1 >= 0 && this.hasThing(x, y, z - 1) && this.getThingsAt(x, y, z - 1).hasRamp())
					return z + player.getHeight() - 1;
				return z + player.getHeight();
			}
		}
		
		//occluding wall check
		for (int j = y - 1; j >= 0; j --)
		{
			int k = z + (y - j);
			if (k >= height)
				break;
			for (int i = 0; i < 10; i ++)
			{
				if (k + i >= height)
					break;
				if (terrainGrid.get(x, j, k + i).getTerrainType() != air)
				{
					//adjustment for standing on stairs
					if (z - 1 >= 0 && this.hasThing(x, y, z - 1) && this.getThingsAt(x, y, z - 1).hasRamp())
						return z + player.getHeight() - 1;
					return z + player.getHeight();
				}
			}
		}
		
		return height;
	}
	
	public void updateCameraScrollLock()
	{
		Hero player = getPlayer();
		Position pos = player.getPos();
		float screenPosX = pos.x + player.getOffsetX()*(1.0f/16.0f);
		float screenPosY = pos.y + pos.z + player.getOffsetY()*(1.0f/16.0f);
		if (!cameraLockV)
		{
			if (screenPosY <= (9.0f + (height - 1.0f)) && screenPosY >= depth - 9.0f)
			{
				setCameraLockV(true);
				displayCenter[1] = (int)((((9.0f + (height - 1.0f)) + (depth - 9.0f))/2.0f));
			}
			else if (screenPosY <= 9.0f + (height - 1.0f))
			{
				setCameraLockV(true);
				displayCenter[1] = 9.0f + (height - 1.0f);
			}
			else if (screenPosY >= depth - 9.0f)
			{
				setCameraLockV(true);
				displayCenter[1] = depth - 9.0f;
			}
		}
		else
		{
			if (!(screenPosY <= (9.0f + (height - 1.0f)) || screenPosY >= depth - 9.0f))
				setCameraLockV(false);
		}
		
		if (!cameraLockH)
		{
			if (screenPosX <= 12.0f && screenPosX >= width - 13.0f)
			{
				setCameraLockH(true);
				displayCenter[0] = (int)((12.0f + width - 13.0f)/2.0f);
			}
			else if (screenPosX <= 12.0f)
			{
				setCameraLockH(true);
				displayCenter[0] = 12.0f;
			}
			else if (screenPosX >= width - 13.0f)
			{
				setCameraLockH(true);
				displayCenter[0] = width - 13.0f;
			}
		}
		else
		{
			if (!(screenPosX <= 12.0f || screenPosX >= width - 13.0f))
				setCameraLockH(false);
		}
	}
	
	public void updateCamera()
	{
		Hero player = getPlayer();
		Position pos = player.getPos();
		float screenPosX = pos.x + player.getOffsetX()*(1.0f/16.0f);
		float screenPosY = pos.y + pos.z + player.getOffsetY()*(1.0f/16.0f);
		float[] newDisplayCenter = new float[2];
		if (isCameraLockH())
		{
			newDisplayCenter[0] = displayCenter[0];
		}
		else
		{
			newDisplayCenter[0] = screenPosX;
		}
		if (isCameraLockV())
		{
			newDisplayCenter[1] = displayCenter[1];
		}
		else
		{
			newDisplayCenter[1] = screenPosY;
		}
		setDisplayCenter(newDisplayCenter);
	}
	
	public boolean isShadowed(int x, int y, int z)
	{
		int shadowLength = 1;
		int shadowDirection = 1;
		boolean longShadows = false;
		switch (tod)
		{
		case Sunrise:
			longShadows = true;
			break;
		case Morning:
			shadowLength = 3;
			break;
		case Midday: 
			shadowDirection = 0;
			break;
		case Afternoon: 
			shadowLength = 3;
			shadowDirection = -1;
			break;
		case Sunset: 
			shadowDirection = -1;
			longShadows = true;
			break;
		case Night: 
			//no shadows
			return false;
		default: 
			shadowDirection = 0;
			break;
		}
		
		for (int k = 0; k + z < height; k ++)
		{
			if (k % shadowLength == 0)
				x += shadowDirection;

			if (x < 0 || x >= width || y < 0 || y >= depth)
				break;
			
			if (!terrainGrid.get(x, y, k + z).isTransparent())
				return true;
			
			if (longShadows && y + 1 < depth && !terrainGrid.get(x, y + 1, k + z).isTransparent())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Update specified portion of the grid for light modifications; grid parameters are assumed to be
	 * in bounds.
	 * 
	 * @param xMin minimum x coordinate
	 * @param xMax maximum x coordinate
	 * @param yMin minimum y coordinate
	 * @param yMax maximum y coordinate
	 * @param zMin minimum z coordinate
	 * @param zMax maximum z coordinate
	 */
	public void updateLightModGrid(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax)
	{
		for (int i = 0; i < width; i ++)
		{
			for (int j = 0; j < depth; j ++)
			{
				for (int k = 0; k < height; k ++)
				{
					lightModGrid.set(i, j, k, 0f);
				}
			}
		}
		
		for (int n = 0; n < lightSources.size(); n ++)
		{
			Position lightPos = lightSources.get(n).getPos();
			int i = lightPos.x;
			int j = lightPos.y;
			int k = lightPos.z;
			
			//special case: wall light sources, which should instead have the light source pushed up in the y direction
			if (this.getTerrainAt(i, j, k).isBlocking())
			{
				j -= 1;
			}
			
			if ((i >= xMin && i <= xMax) || (j >= yMin && j <= yMax) || (k >= zMin && k <= zMax))
			{
				//TODO: Update this to distinguish between point and directed lights
				//point light update
				float lightPower = lightSources.get(n).getLightPower();
				int lightDst = (int)(lightPower * 10);
				int iMin = Math.max(i - lightDst, 0);
				int jMin = Math.max(j - lightDst, 0);
				int kMin = Math.max(k - lightDst, 0);
				int iMax = Math.min(i + lightDst, width - 1);
				int jMax = Math.min(j + lightDst, depth - 1);
				int kMax = Math.min(k + lightDst, height - 1);
				for (int i2 = iMin; i2 <= iMax; i2 ++)
				{
					for (int j2 = jMin; j2 <= jMax; j2 ++)
					{
						for (int k2 = kMin; k2 <= kMax; k2 ++)
						{
							if (!checkLightBlockingLineOfSight(i, j, k, i2, j2, k2))
							{
								//increase light modification based on distance to light source
								float dst = (float)Math.sqrt(Math.pow(i - i2, 2) + Math.pow(j - j2, 2) + Math.pow(k - k2, 2));
								float updateVal = Math.max(lightPower - dst/10.0f, 0);
								if (updateVal > lightModGrid.get(i2, j2, k2))
									lightModGrid.set(i2, j2, k2, updateVal);
							}
						}
					}
				}
			}
		}
		
		for (int n = 0; n < antiLightSources.size(); n ++)
		{
			Position lightPos = antiLightSources.get(n).getPos();
			int i = lightPos.x;
			int j = lightPos.y;
			int k = lightPos.z;
			if ((i >= xMin && i <= xMax) || (j >= yMin && j <= yMax) || (k >= zMin && k <= zMax))
			{
				//TODO: Update this to distinguish between point and directed lights
				//point light update
				float lightPower = antiLightSources.get(n).getLightPower();
				int lightDst = (int)(lightPower * 10);
				int iMin = Math.max(i - lightDst, 0);
				int jMin = Math.max(j - lightDst, 0);
				int kMin = Math.max(k - lightDst, 0);
				int iMax = Math.min(i + lightDst, width - 1);
				int jMax = Math.min(j + lightDst, depth - 1);
				int kMax = Math.min(k + lightDst, height - 1);
				for (int i2 = iMin; i2 <= iMax; i2 ++)
				{
					for (int j2 = jMin; j2 <= jMax; j2 ++)
					{
						for (int k2 = kMin; k2 <= kMax; k2 ++)
						{
							if (!checkLightBlockingLineOfSight(i, j, k, i2, j2, k2))
							{
								//increase light modification based on distance to light source
								float dst = (float)Math.sqrt(Math.pow(i - i2, 2) + Math.pow(j - j2, 2) + Math.pow(k - k2, 2));
								float updateVal = Math.min(lightPower + dst/10.0f, 0);
								if (updateVal < lightModGrid.get(i2, j2, k2))
									lightModGrid.set(i2, j2, k2, updateVal);
							}
						}
					}
				}
			}
		}
	}
		
	/**
	 * Render terrain, things, and agents by layers
	 */
	public void renderWorld()
	{		
		int iMin, iMax, jMin, jMax, kMin, kMax;
		kMin = 0;
		kMax = Math.min(height - 1, updateHeightMax());
		iMin = Math.max(0, (int)(displayCenter[0] - 13));
		iMax = Math.min(width - 1, (int)(displayCenter[0] + 15));
		jMin = Math.max(0, (int)(displayCenter[1] - 10 - kMax));
		jMax = Math.min(depth - 1, (int)(displayCenter[1] + 11 + kMax));
		
		//adjustment for off-screen lights, this may need experimentation depending on max light distances
		int buffer = 4;
		int lightXMin = Math.max(0, iMin - buffer);
		int lightXMax = Math.min(width, iMax + buffer);
		int lightYMin = Math.max(0, jMin - buffer);
		int lightYMax = Math.min(depth, jMax + buffer);
		int lightZMin = Math.max(0, kMin - buffer);
		int lightZMax = Math.min(height, kMax + buffer);
		
		updateLightModGrid(lightXMin, lightXMax, lightYMin, lightYMax, lightZMin, lightZMax);
		//updateLightModGrid(iMin, iMax, jMin, jMax, lightZMin, lightZMax);
		
		for (int k = kMin; k <= kMax; k ++)
		{
			//***************************************************************************************************************
			//********* TERRAIN AND THING AND AGENT RENDERING ***************************************************************
			//***************************************************************************************************************
			for (int j = jMax; j >= jMin; j --)
			{
				for (int i = iMin; i <= iMax; i ++)
				{
					Terrain t = terrainGrid.get(i, j, k);					
					
					//Display vertical textures
					if (t.getTerrainType() != air)
					{
						//Determine position on screen
						int x = pixelSize*(textureSize*i - (int)(displayCenter[0]*textureSize)) + 400 - (quadVertexMax)/2;
						int y = (pixelSize*(textureSize*j - (int)(displayCenter[1]*textureSize)) + 300) + quadVertexMax*k - (quadVertexMax)/2;
						
						GL11.glPushMatrix();
						
							//Translate to screen position and bind appropriate texture
							GL11.glColor3f(1.0f, 1.0f, 1.0f);
							GL11.glEnable(GL11.GL_TEXTURE_2D);
							GL11.glTranslatef(x, y, 0);
							GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
					    	
							if (k < kMax || (k == kMax && t.isTransparent()))
							{
								t.bindVTexture(vTerrainTextures);
						    	//Determine which part of the texture to use based on how many neighbors are air
						    	int texX = t.getTexCol();
						    	int texY = t.getTexRow();
						    	
						    	boolean topEmpty, rightEmpty, leftEmpty, bottomOnGround;
						    	
						    	if (t.isUnblendedVertical())
						    	{
							    	topEmpty = k + 1 >= height || terrainGrid.get(i, j, k+1).getTerrainType() != t.getTerrainType();
					    			rightEmpty = i + 1 >= width || terrainGrid.get(i+1, j, k).getTerrainType() != t.getTerrainType();
					    			leftEmpty = i - 1 < 0 || terrainGrid.get(i-1, j, k).getTerrainType() != t.getTerrainType();
	
					    			bottomOnGround = false;
					    			if (k - 1 < 0 || j - 1 < 0)
					    				bottomOnGround = true;
					    			else if (terrainGrid.get(i, j, k-1).getTerrainType() != t.getTerrainType())
					    				bottomOnGround = true;
						    	}
						    	else
						    	{
							    	topEmpty = k + 1 >= height || terrainGrid.get(i, j, k+1).getTerrainType() == air || terrainGrid.get(i, j, k+1).isUnblendedVertical();
					    			rightEmpty = i + 1 >= width || terrainGrid.get(i+1, j, k).getTerrainType() == air || terrainGrid.get(i+1, j, k).isUnblendedVertical();
					    			leftEmpty = i - 1 < 0 || terrainGrid.get(i-1, j, k).getTerrainType() == air || terrainGrid.get(i-1, j, k).isUnblendedVertical();
	
					    			bottomOnGround = false;
					    			if (k - 1 < 0 || j - 1 < 0)
					    				bottomOnGround = false;
					    			else if (terrainGrid.get(i, j-1, k).getTerrainType() == air && terrainGrid.get(i, j-1, k-1).getTerrainType() != air)
					    				bottomOnGround = true;
						    	}
				    			
					    		if (topEmpty && bottomOnGround)
					    		{
					    			texY += 3;
					    		}
					    		else if (topEmpty)
					    		{
					    			//texY is unchanged, this case is required for the else case and organizational purposes
					    		}
					    		else if (bottomOnGround)
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
						    	
					    		if (k == kMax && t.isTransparent())
					    			renderCell(texX, texY, false, lightModGrid.get(i, j, k), .75f);
					    		else
					    			renderCell(texX, texY, false, lightModGrid.get(i, j, k));
							}
							else
							{
								//Commented out conditional also accounts for having a fullBlock thing below the piece of vertical terrain.
								//Uncomment this if there is ever a thing that is made to replace a wall.
								//if (terrainGrid[i][j][k-1].type != air || (this.hasThing(i, j, k-1) && this.getThingsAt(i, j, k-1).hasFullBlock()))
								if (terrainGrid.get(i, j, k-1).type != air)
								{
									renderCrossSection(i, j, k, t);
								}
							}
							
						GL11.glPopMatrix();
						//Edge overhang textures
						if ((j == 0 || (j - 1 >= 0 && terrainGrid.get(i, j-1, k).getTerrainType() == air)) 
								&& k != kMax && terrainGrid.get(i, j, k+1).getTerrainType() == air)
						{
							t = terrainGrid.get(i, j, k);
							//Determine position on screen
							x = pixelSize*(textureSize*i - (int)(displayCenter[0]*textureSize)) + 400 - (quadVertexMax)/2;
							y = (pixelSize*(textureSize*j - (int)(displayCenter[1]*textureSize)) + 300) + quadVertexMax*k - (quadVertexMax)/2;
							GL11.glPushMatrix();
							
							//Translate to screen position and bind appropriate texture
							GL11.glColor3f(1.0f, 1.0f, 1.0f);
							GL11.glEnable(GL11.GL_TEXTURE_2D);
							GL11.glTranslatef(x, y, 0);
							t.bindHTexture(hTerrainTextures);
							GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

							//Determine which part of the texture to use based on how many neighbors are air
					    	int texX = t.getTexColTop();
					    	int texY = t.getTexRowTop();
						
							boolean rightEmpty = i + 1 >= width || terrainGrid.get(i+1, j, k).getTerrainType() == air,
			    			leftEmpty = i - 1 < 0 || terrainGrid.get(i-1, j, k).getTerrainType() == air;
			    		
				    		texY += 4;
				    		
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
				    		
				    		renderCell(texX, texY, isShadowed(i, j, k+1), lightModGrid.get(i, j, k+1));
						
						GL11.glPopMatrix();
						}
					}
					//Display horizontal textures
					else if (k - 1 >= 0 && terrainGrid.get(i, j, k-1).getTerrainType() != air)
					{
						if (k - 1 >= 0)
						{							
							t = terrainGrid.get(i, j, k-1);
							//Determine position on screen
							int x = pixelSize*(textureSize*i - (int)(displayCenter[0]*textureSize)) + 400 - (quadVertexMax)/2;
							int y = (pixelSize*(textureSize*j - (int)(displayCenter[1]*textureSize)) + 300) + quadVertexMax*k - (quadVertexMax)/2;
							
							GL11.glPushMatrix();
								
								//Translate to screen position and bind appropriate texture
								GL11.glColor3f(1.0f, 1.0f, 1.0f);
								GL11.glEnable(GL11.GL_TEXTURE_2D);
								GL11.glTranslatef(x, y, 0);
								t.bindHTexture(hTerrainTextures);
								GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
						    	
						    	//Determine which part of the texture to use based on how many neighbors are air
						    	int texX = t.getTexColTop();
						    	int texY = t.getTexRowTop();
							
						    	boolean topEmpty, bottomEmpty, rightEmpty, leftEmpty;
						    	if (t.isUnblendedHorizontal())
						    	{
									topEmpty = j + 1 >= depth || terrainGrid.get(i, j+1, k-1).getTerrainTop() != t.getTerrainTop();
					    			bottomEmpty = j - 1 < 0 || terrainGrid.get(i, j-1, k-1).getTerrainTop() != t.getTerrainTop();
					    			rightEmpty = i + 1 >= width || terrainGrid.get(i+1, j, k-1).getTerrainTop() != t.getTerrainTop();
					    			leftEmpty = i - 1 < 0 || terrainGrid.get(i-1, j, k-1).getTerrainTop() != t.getTerrainTop();
						    	}
						    	else
						    	{
									topEmpty = j + 1 >= depth || terrainGrid.get(i, j+1, k-1).getTerrainType() == air;
					    			bottomEmpty = j - 1 < 0 || terrainGrid.get(i, j-1, k-1).getTerrainType() == air;
					    			rightEmpty = i + 1 >= width || terrainGrid.get(i+1, j, k-1).getTerrainType() == air;
					    			leftEmpty = i - 1 < 0 || terrainGrid.get(i-1, j, k-1).getTerrainType() == air;
						    	}
				    		
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
					    		
					    		renderCell(texX, texY, isShadowed(i, j, k), lightModGrid.get(i, j, k));
							
							GL11.glPopMatrix();
						}
					}
					//Display hanging bottom vertical textures
					if (k + 1 < kMax && terrainGrid.get(i, j, k+1).getTerrainType() != air && k - 1 >= 0
							&& ((!terrainGrid.get(i, j, k+1).isUnblendedVertical() && (terrainGrid.get(i, j, k).getTerrainType() == air || terrainGrid.get(i, j, k).isUnblendedVertical()))
									|| (terrainGrid.get(i, j, k+1).isUnblendedVertical() && (terrainGrid.get(i, j, k).getTerrainType() == air || terrainGrid.get(i, j, k).getTerrainType() != terrainGrid.get(i, j, k+1).getTerrainType()))))
					{
						t = terrainGrid.get(i, j, k+1);
						//Determine position on screen
						int x = pixelSize*(textureSize*i - (int)(displayCenter[0]*textureSize)) + 400 - (quadVertexMax)/2;
						int y = (pixelSize*(textureSize*j - (int)(displayCenter[1]*textureSize)) + 300) + quadVertexMax*k - (quadVertexMax)/2;
						
						GL11.glPushMatrix();
							GL11.glColor3f(1.0f, 1.0f, 1.0f);
							GL11.glEnable(GL11.GL_TEXTURE_2D);
							GL11.glTranslatef(x, y, 0);
							t.bindVTexture(vTerrainTextures);
							GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
							
							int texX = t.getTexCol();
					    	int texY = t.getTexRow();
					    	
					    	boolean rightEmpty, leftEmpty;
					    	
					    	if (t.isUnblendedVertical())
					    	{
					    		rightEmpty = i + 1 >= width || terrainGrid.get(i+1, j, k+1).getTerrainType() != t.getTerrainType();
				    			leftEmpty = i - 1 < 0 || terrainGrid.get(i-1, j, k+1).getTerrainType() != t.getTerrainType();
					    	}
					    	else
					    	{
						    	rightEmpty = i + 1 >= width || terrainGrid.get(i+1, j, k+1).getTerrainType() == air;
				    			leftEmpty = i - 1 < 0 || terrainGrid.get(i-1, j, k+1).getTerrainType() == air;
					    	}
							
				    		texY += 4;
							
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
					    	
				    		renderCell(texX, texY, false, lightModGrid.get(i, j, k+1));
							
						GL11.glPopMatrix();
					}
					
				}
				
				// Render Things
				for (int i = iMin; i <= iMax; i ++)
				{
					
					if (this.hasThing(i, j, k))
					{
						if (k < kMax)
						{
							int x = pixelSize*(textureSize*i - (int)(displayCenter[0]*textureSize)) + 400 - (quadVertexMax)/2;
							int y = (pixelSize*(textureSize*j - (int)(displayCenter[1]*textureSize)) + 300) + quadVertexMax*k - (quadVertexMax)/2;
							
							GL11.glPushMatrix();
								//don't shadow if the thing is in (i.e. on) a vertical wall
								if (terrainGrid.get(i, j, k).getTerrainType() != air)
									setLighting(false, lightModGrid.get(i, j, k));
								else
									setLighting(isShadowed(i, j, k), lightModGrid.get(i, j, k));
								GL11.glTranslatef(x, y, 0);
								thingGrid.get(i, j, k).renderThings(pixelSize, textureSize);
							GL11.glPopMatrix();
						}
						else
						{
							//NOTE: The commented out condition will render black boxes over a fullBlock thing even if vertical terrain is under it, 
							//not just if it's another fullBlock thing.  Test this to see which looks better.
							//if (terrainGrid[i][j][k-1].type != air || (this.hasThing(i, j, k-1) && this.getThingsAt(i, j, k-1).hasFullBlock()))
							if (this.getThingsAt(i, j, k).hasFullBlock() && ((this.hasThing(i, j, k-1) && this.getThingsAt(i, j, k-1).hasFullBlock()) || this.hasThing(i, j, k-2) && this.getThingsAt(i, j, k-2).hasTallBlock()))
							{
								int x = pixelSize*(textureSize*i - (int)(displayCenter[0]*textureSize)) + 400 - (quadVertexMax)/2;
								int y = (pixelSize*(textureSize*j - (int)(displayCenter[1]*textureSize)) + 300) + quadVertexMax*k - (quadVertexMax)/2;
								
								int adjustment = (this.getThingsAt(i, j, k).getBlockingWidth() - textureSize) / 2;
								
								GL11.glPushMatrix();
									GL11.glColor3f(0, 0, 0);
									GL11.glTranslatef(x - (pixelSize*adjustment), y, 0);
									GL11.glBegin(GL11.GL_QUADS);
										GL11.glVertex2f(0, 0);
										GL11.glVertex2f(quadVertexMax + (pixelSize*adjustment*2), 0);
										GL11.glVertex2f(quadVertexMax + (pixelSize*adjustment*2), quadVertexMax);
										GL11.glVertex2f(0, quadVertexMax);
									GL11.glEnd();
									GL11.glColor3f(1, 1, 1);
								GL11.glPopMatrix();
							}
						}
					}
				}
				if (k <= kMax)
				{
					//Render Agents
					for (int i = iMin; i <= iMax; i ++)
					{
						Agent agent = agentGrid.get(i, j, k);
						if (agent != null)
						{
							int x = pixelSize*(textureSize*i - (int)(displayCenter[0]*textureSize)) + 400 - (quadVertexMax)/2;
							int y = (pixelSize*(textureSize*j - (int)(displayCenter[1]*textureSize)) + 300) + quadVertexMax*k - (quadVertexMax)/2;
							
							GL11.glPushMatrix();
								if (agent.getClass() == Placeholder.class)
								{
									Position effectivePos = ((Placeholder)agent).getEffectivePos();
									setLighting(isShadowed(effectivePos.x, effectivePos.y, effectivePos.z), lightModGrid.get(effectivePos.x, effectivePos.y, effectivePos.z));
								}
								else
									setLighting(isShadowed(i, j, k), lightModGrid.get(i, j, k));
								GL11.glTranslatef(x, y, 0);
								agent.renderAgent(pixelSize, textureSize);
							GL11.glPopMatrix();
						}
					}
				}
			}
		}
	}

	private void renderCrossSection(int x, int y, int z, Terrain t)
	{
		//Determine which part of the texture to use based on how many neighbors are air
		Terrain toppedTerrain;
		int adjustment = 0;	//height adjustment needed for comparing unblended terrains
		if (terrainGrid.get(x, y, z-1).isTransparent())
		{
			toppedTerrain = t;
		}
		else
		{
			toppedTerrain = terrainGrid.get(x, y, z-1);
			adjustment = -1;
		}
		int texX = toppedTerrain.getTexCol();
		int texY = toppedTerrain.getTexRow();
		toppedTerrain.bindVTexture(vTerrainTextures);
    	
    	boolean topEmpty, bottomEmpty, rightEmpty, leftEmpty;
    	if (toppedTerrain.isUnblendedVertical())
    	{
			topEmpty = y + 1 >= depth
					|| terrainGrid.get(x, y+1, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x, y+1, z).isTransparent()
					|| terrainGrid.get(x, y+1, z-1).getTerrainType() == air;
			bottomEmpty = y - 1 < 0
					|| terrainGrid.get(x, y-1, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x, y-1, z).isTransparent()
					|| terrainGrid.get(x, y-1, z-1).getTerrainType() == air;
			rightEmpty = x + 1 >= width
					|| terrainGrid.get(x+1, y, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x+1, y, z).isTransparent()
					|| terrainGrid.get(x+1, y, z-1).getTerrainType() == air;
			leftEmpty = x - 1 < 0
					|| terrainGrid.get(x-1, y, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x-1, y, z).isTransparent()
					|| terrainGrid.get(x-1, y, z-1).getTerrainType() == air;
    	}
    	else
    	{
			topEmpty = y + 1 >= depth
					|| terrainGrid.get(x, y+1, z).getTerrainType() == air || terrainGrid.get(x, y+1, z).isTransparent()
					|| terrainGrid.get(x, y+1, z-1).getTerrainType() == air;
			bottomEmpty = y - 1 < 0
					|| terrainGrid.get(x, y-1, z).getTerrainType() == air || terrainGrid.get(x, y-1, z).isTransparent()
					|| terrainGrid.get(x, y-1, z-1).getTerrainType() == air;
			rightEmpty = x + 1 >= width
					|| terrainGrid.get(x+1, y, z).getTerrainType() == air || terrainGrid.get(x+1, y, z).isTransparent()
					|| terrainGrid.get(x+1, y, z-1).getTerrainType() == air;
			leftEmpty = x - 1 < 0
					|| terrainGrid.get(x-1, y, z).getTerrainType() == air || terrainGrid.get(x-1, y, z).isTransparent()
					|| terrainGrid.get(x-1, y, z-1).getTerrainType() == air;
    	}
	
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
    	
		//shift in x direction to cross section textures
		texX += 4;
		
		renderCell(texX, texY, false, lightModGrid.get(x, y, z));
		
		//corner rendering checks (four non-exclusive cases)
		if (!topEmpty && !rightEmpty)
		{
			//only render corner if the diagonal cell is empty
			boolean cornerEmpty;
			if (toppedTerrain.isUnblendedVertical())
	    	{
				cornerEmpty = x + 1 >= width || y + 1 >= depth
						|| terrainGrid.get(x+1, y+1, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x+1, y+1, z).isTransparent()
						|| terrainGrid.get(x+1, y+1, z-1).getTerrainType() == air;
	    	}
	    	else
	    	{
	    		cornerEmpty = x + 1 >= width || y + 1 >= depth
						|| terrainGrid.get(x+1, y+1, z).getTerrainType() == air || terrainGrid.get(x+1, y+1, z).isTransparent()
						|| terrainGrid.get(x+1, y+1, z-1).getTerrainType() == air;
	    	}
			
			if (cornerEmpty)
			{
				texX = toppedTerrain.getTexCol();
				texY = toppedTerrain.getTexRow();
				
				texX += 4; //shift to cross section textures
				texY += 4; //shift to corner textures
				
				GL11.glBegin(GL11.GL_QUADS);
				setLighting(false, lightModGrid.get(x, y, z));
					GL11.glTexCoord2f(texX * tConv + tConvQuarterAdjustment, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(quadVertexMax/2.0f, quadVertexMax/2.0f);
					GL11.glTexCoord2f(texX*tConv + tConv, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(quadVertexMax, quadVertexMax/2.0f);
					GL11.glTexCoord2f(texX*tConv + tConv, texY*tConv);
					GL11.glVertex2f(quadVertexMax, quadVertexMax);
					GL11.glTexCoord2f(texX*tConv + tConvQuarterAdjustment, texY*tConv);
					GL11.glVertex2f(quadVertexMax/2.0f, quadVertexMax);
				GL11.glEnd();
			}
		}
		if (!rightEmpty && !bottomEmpty)
		{
			//only render corner if the diagonal cell is empty
			boolean cornerEmpty;
			if (toppedTerrain.isUnblendedVertical())
	    	{
				cornerEmpty = x + 1 >= width || y - 1 < 0
						|| terrainGrid.get(x+1, y-1, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x+1, y-1, z).isTransparent()
						|| terrainGrid.get(x+1, y-1, z-1).getTerrainType() == air;
	    	}
	    	else
	    	{
	    		cornerEmpty = x + 1 >= width || y - 1 < 0
						|| terrainGrid.get(x+1, y-1, z).getTerrainType() == air || terrainGrid.get(x+1, y-1, z).isTransparent()
						|| terrainGrid.get(x+1, y-1, z-1).getTerrainType() == air;
	    	}
			
			if (cornerEmpty)
			{
				texX = toppedTerrain.getTexCol();
				texY = toppedTerrain.getTexRow();
				
				texX += 4; //shift to cross section textures
				texY += 4; //shift to corner textures
				
				GL11.glBegin(GL11.GL_QUADS);
				setLighting(false, lightModGrid.get(x, y, z));
					GL11.glTexCoord2f(texX * tConv + tConvQuarterAdjustment, texY*tConv + tConv);
					GL11.glVertex2f(quadVertexMax/2.0f, 0);
					GL11.glTexCoord2f(texX*tConv + tConv, texY*tConv + tConv);
					GL11.glVertex2f(quadVertexMax, 0);
					GL11.glTexCoord2f(texX*tConv + tConv, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(quadVertexMax, quadVertexMax/2.0f);
					GL11.glTexCoord2f(texX*tConv + tConvQuarterAdjustment, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(quadVertexMax/2.0f, quadVertexMax/2.0f);
				GL11.glEnd();
			}
		}
		if (!bottomEmpty && !leftEmpty)
		{
			//only render corner if the diagonal cell is empty
			boolean cornerEmpty;
			if (toppedTerrain.isUnblendedVertical())
	    	{
				cornerEmpty = x - 1 < 0 || y - 1 < 0
						|| terrainGrid.get(x-1, y-1, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x-1, y-1, z).isTransparent()
						|| terrainGrid.get(x-1, y-1, z-1).getTerrainType() == air;
	    	}
	    	else
	    	{
	    		cornerEmpty = x - 1 < 0 || y - 1 < 0
						|| terrainGrid.get(x-1, y-1, z).getTerrainType() == air || terrainGrid.get(x-1, y-1, z).isTransparent()
						|| terrainGrid.get(x-1, y-1, z-1).getTerrainType() == air;
	    	}
			
			if (cornerEmpty)
			{
				texX = toppedTerrain.getTexCol();
				texY = toppedTerrain.getTexRow();
				
				texX += 4; //shift to cross section textures
				texY += 4; //shift to corner textures
				
				GL11.glBegin(GL11.GL_QUADS);
				setLighting(false, lightModGrid.get(x, y, z));
					GL11.glTexCoord2f(texX * tConv, texY*tConv + tConv);
					GL11.glVertex2f(0, 0);
					GL11.glTexCoord2f(texX*tConv + tConvQuarterAdjustment, texY*tConv + tConv);
					GL11.glVertex2f(quadVertexMax/2.0f, 0);
					GL11.glTexCoord2f(texX*tConv + tConvQuarterAdjustment, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(quadVertexMax/2.0f, quadVertexMax/2.0f);
					GL11.glTexCoord2f(texX*tConv, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(0, quadVertexMax/2.0f);
				GL11.glEnd();
			}
		}
		if (!leftEmpty && !topEmpty)
		{
			//only render corner if the diagonal cell is empty
			boolean cornerEmpty;
			if (toppedTerrain.isUnblendedVertical())
	    	{
				cornerEmpty = x - 1 < 0 || y + 1 >= depth
						|| terrainGrid.get(x-1, y+1, z+adjustment).getTerrainType() != toppedTerrain.getTerrainType() || terrainGrid.get(x-1, y+1, z).isTransparent()
						|| terrainGrid.get(x-1, y+1, z-1).getTerrainType() == air;
	    	}
	    	else
	    	{
	    		cornerEmpty = x - 1 < 0 || y + 1 >= depth
						|| terrainGrid.get(x-1, y+1, z).getTerrainType() == air || terrainGrid.get(x-1, y+1, z).isTransparent()
						|| terrainGrid.get(x-1, y+1, z-1).getTerrainType() == air;
	    	}
			
			if (cornerEmpty)
			{
				texX = toppedTerrain.getTexCol();
				texY = toppedTerrain.getTexRow();
				
				texX += 4; //shift to cross section textures
				texY += 4; //shift to corner textures
				
				GL11.glBegin(GL11.GL_QUADS);
				setLighting(false, lightModGrid.get(x, y, z));
					GL11.glTexCoord2f(texX * tConv, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(0, quadVertexMax/2.0f);
					GL11.glTexCoord2f(texX*tConv + tConvQuarterAdjustment, texY*tConv + tConvQuarterAdjustment);
					GL11.glVertex2f(quadVertexMax/2.0f, quadVertexMax/2.0f);
					GL11.glTexCoord2f(texX*tConv + tConvQuarterAdjustment, texY*tConv);
					GL11.glVertex2f(quadVertexMax/2.0f, quadVertexMax);
					GL11.glTexCoord2f(texX*tConv, texY*tConv);
					GL11.glVertex2f(0, quadVertexMax);
				GL11.glEnd();
			}
		}
	}
	
	/**
	 * Render textboxes, menus, and such
	 */
	public void renderOverlay()
	{
		if (isTextBoxActive())
		{
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			textTexture.bind();
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			getTextDisplay().renderText();
		}
	}
	
	private void renderCell(float texX, float texY, boolean isShadowed, float lightMod)
	{
		renderCell(texX, texY, isShadowed, lightMod, 1.0f);
	}
	
	private void renderCell(float texX, float texY, boolean isShadowed, float lightMod, float transparency)
	{
		GL11.glBegin(GL11.GL_QUADS);
			setLighting(isShadowed, lightMod, transparency);
			GL11.glTexCoord2f(texX * tConv, texY*tConv + tConv);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(texX*tConv + tConv, texY*tConv + tConv);
			GL11.glVertex2f(quadVertexMax, 0);
			GL11.glTexCoord2f(texX*tConv + tConv, texY * tConv);
			GL11.glVertex2f(quadVertexMax, quadVertexMax);
			GL11.glTexCoord2f(texX*tConv, texY * tConv);
			GL11.glVertex2f(0, quadVertexMax);
		GL11.glEnd();
	}
	
	/**
	 * Overload without transparency
	 */
	private void setLighting(boolean shadowed, float lightMod)
	{
		setLighting(shadowed, lightMod, 1.0f);
	}
	
	/**
	 * Determine the lighting for rendering something depending on shadows,
	 * light sources, and time of day
	 * @param shadowed true if in shadow
	 * @param lightMod contributing amount from light sources
	 * @param transparency how transparent the rendering is
	 */
	private void setLighting(boolean shadowed, float lightMod, float transparency)
	{
		float r, g, b;
		switch (tod)
		{
		case Sunrise:
			if (shadowed)
			{
				r = .5f;
				g = .5f;
				b = .5f;
			}
			else
			{
				r = .9f;
				g = .8f;
				b = .8f;
			}
		break;
		case Morning:
			if (shadowed)
			{
				r = .7f;
				g = .7f;
				b = .7f;
			}
			else
			{
				r = 1f;
				g = 1f;
				b = 1f;
			}
		break;
		case Midday: 
			if (shadowed)
			{
				r = .8f;
				g = .8f;
				b = .8f;
			}
			else
			{
				r = 1f;
				g = 1f;
				b = 1f;
			}
		break;
		case Afternoon: 
			if (shadowed)
			{
				r = .7f;
				g = .7f;
				b = .7f;
			}
			else
			{
				r = 1f;
				g = 1f;
				b = 1f;
			}
		break;
		case Sunset: 
			if (shadowed)
			{
				r = .5f;
				g = .5f;
				b = .5f;
			}
			else
			{
				r = .9f;
				g = .75f;
				b = .85f;
			}
		break;
		case Night: 
			if (shadowed)
			{
				r = .3f;
				g = .3f;
				b = .5f;
			}
			else
			{
				r = .4f;
				g = .4f;
				b = .6f;
			}
		break;
		default: 
			if (shadowed)
			{
				r = .8f;
				g = .8f;
				b = .8f;
			}
			else
			{
				r = 1f;
				g = 1f;
				b = 1f;
			}
		}
		
		if (lightMod > 0)
		{
			r = Math.max(0, r + 1.2f * lightMod);
			g = Math.max(0, g + lightMod);
			b = Math.max(0, b + lightMod);
		}
		else if (lightMod < 0)
		{
			r = Math.min(1, r + .9f * lightMod);
			g = Math.min(1, g + lightMod);
			b = Math.min(1, b + .8f * lightMod);
		}
		
		GL11.glColor4f(r, g, b, transparency);
	}
	
	/**
	 * Add an agent to the agent list and the agent grid
	 * @param newAgent the agent to be added to the world
	 */
	public void addAgent(Agent newAgent)
	{
		agents.add(newAgent);
		
		Position pos = newAgent.getPos();
		agentGrid.set(pos.x, pos.y, pos.z, newAgent);
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
			Position pos = newAgents.get(i).getPos();
			agentGrid.set(pos.x, pos.y, pos.z, newAgents.get(i));
		}
	}
	
	public Agent getAgentAt(int x, int y, int z)
	{
		return agentGrid.get(x, y, z);
	}
	
	public void removeAgentAt(int x, int y, int z)
	{
		agents.remove(agentGrid.get(x, y, z));
		agentGrid.set(x, y, z, null);
	}
	
	public void addThing(Thing t, int x, int y, int z)
	{
		if (thingGrid.get(x, y, z) == null)
			thingGrid.set(x, y, z, new ThingGridCell());
		thingGrid.get(x, y, z).addThing(t);
		t.setPos(new Position(x, y, z));
		things.add(t);
		if (t.isLightSource())
			lightSources.add(t);
	}
	
	public void removeThingsAt(int x, int y, int z)
	{
		if (thingGrid.get(x, y, z) != null)
		{
			ArrayList<Thing> thingList = thingGrid.get(x, y, z).getThings();
			for (int i = 0; i < thingList.size(); i ++)
			{
				if (thingList.get(i).isLightSource())
					lightSources.remove(thingList.get(i));
				things.remove(thingList.get(i));
			}
		}
	}
	
	public void moveThing(Thing thing, int xChange, int yChange, int zChange)
	{
		Position pos = thing.getPos();
		int oldX = pos.x;
		int oldY = pos.y;
		int oldZ = pos.z;
		int newX = oldX + xChange;
		int newY = oldY + yChange;
		int newZ = oldZ + zChange;
		if (newX < 0 || newX >= width || newY < 0 || newY >= depth || newZ < 0 || newZ >= height)
		{
			System.out.println("Could not move agent, position out of bounds");
		}
		else
		{
			thing.setPos(new Position(newX, newY, newZ));
			thingGrid.get(oldX, oldY, oldZ).removeThing(thing);
			if (thingGrid.get(newX, newY, newZ) == null)
				thingGrid.set(newX, newY, newZ, new ThingGridCell());
			thingGrid.get(newX, newY, newZ).addThing(thing);
		}
	}
	
	public ThingGridCell getThingsAt(int x, int y, int z)
	{
		return thingGrid.get(x, y, z);
	}
	
	public ThingGridCell getThingsAt(Position pos)
	{
		return thingGrid.get(pos.x, pos.y, pos.z);
	}
	
	public Terrain getTerrainAt(Position pos)
	{
		return terrainGrid.get(pos.x, pos.y, pos.z);
	}
	
	public Terrain getTerrainAt(int x, int y, int z)
	{
		return terrainGrid.get(x, y, z);
	}
	
	public void moveAgent(Agent agent, int xChange, int yChange, int zChange)
	{
		Position pos = agent.getPos();
		int oldX = pos.x;
		int oldY = pos.y;
		int oldZ = pos.z;
		int newX = oldX + xChange;
		int newY = oldY + yChange;
		int newZ = oldZ + zChange;
		if (newX < 0 || newX >= width || newY < 0 || newY >= depth || newZ < 0 || newZ >= height)
		{
			System.out.println("Could not move agent, position out of bounds");
		}
		else
		{
			agent.setPos(new Position(newX, newY, newZ));
			agentGrid.set(oldX, oldY, oldZ, null);
			agentGrid.set(newX, newY, newZ, agent);
		}
	}
	
	/**
	 * Setter for the display center
	 * 
	 * @param center the new display center
	 */
	public void setDisplayCenter(float[] center)
	{
		displayCenter[0] = center[0];
		displayCenter[1] = center[1];
	}
	
	/**
	 * Setter for the display center
	 * 
	 * @param center the new display center
	 */
	public void setDisplayCenter(Position center)
	{
		displayCenter[0] = center.x;
		displayCenter[1] = center.y;
	}
	
	public void cycleTimeOfDay()
	{
		switch (tod)
		{
		case Sunrise: tod = Morning; break;
		case Morning: tod = Midday; break;
		case Midday: tod = Afternoon; break;
		case Afternoon: tod = Sunset; break;
		case Sunset: tod = Night; break;
		case Night: tod = Sunrise; break;
		}
	}
	
	/**
	 * Getter for the display x
	 * 
	 * @return x display pixel coordinate
	 */
	public float getDisplayX()
	{
		return displayCenter[0];
	}
	
	/**
	 * Getter for the display y
	 * 
	 * @return y display pixel coordinate
	 */
	public float getDisplayY()
	{
		return displayCenter[1];
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
		if (this.isInBounds(x, y, z))
			return agentGrid.get(x, y, z) != null;
		else
			return false;
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
		if (this.isInBounds(x, y, z))
			return thingGrid.get(x, y, z) != null && !thingGrid.get(x, y, z).isEmpty();
		else
			return false;
	}
	
	public boolean hasThing(Position pos)
	{
		if (this.isInBounds(pos.x, pos.y, pos.z))
			return thingGrid.get(pos.x, pos.y, pos.z) != null && !thingGrid.get(pos.x, pos.y, pos.z).isEmpty();
		else
			return false;
	}
	
	/**
	 * Determine whether an agent can move onto a grid cell based on other agents,
	 * things and whether they can be crossed, and terrain blocking
	 * 
	 * @param x grid location
	 * @param y grid location
	 * @param z grid location
	 * @return true if an agent cannot move to the grid space
	 */
	public boolean isBlocked(int x, int y, int z)
	{
		if (this.isOccupied(x, y, z)) //other agents
		{
			return true;
		}
		else if (terrainGrid.get(x, y, z).isBlocking())
		{
			return true;
		}
		else if (this.hasThing(x, y, z) && thingGrid.get(x, y, z).isBlocking()) //things
		{
			return true;
		}
		//NOTE: 19 is the minimum map size to do camera locked top/bottom edge blocking
		else if (depth > 19 && (y < height - 1 - z || y > depth - (z - 1))) //screen edge
		{
			return true;
		}
		return false;
	}
	
	public boolean isLightBlocking(int x, int y, int z)
	{
		if (isOccupied(x, y, z) && !agentGrid.get(x, y, z).isTransparent())
			return true;
		else if (hasThing(x, y, z) && !thingGrid.get(x, y, z).isTransparent())
			return true;
		else
			return !terrainGrid.get(x, y, z).isTransparent();
	}
	
	/**
	 * Determine whether a light source is blocked before reaching a test cell
	 * 
	 * @param x1 light source x
	 * @param y1 light source y
	 * @param z1 light source z
	 * @param x2 test cell x
	 * @param y2 test cell y
	 * @param z2 test cell z
	 * @return true if the cell is blocked, false otherwise
	 */
	public boolean checkLightBlockingLineOfSight(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		int dx = x2 - x1;
		int dy = y2 - y1;
		int dz = z2 - z1;
		
		//handle special cases of light-blocking test cells
		if (isLightBlocking(x2, y2, z2))
		{
			if (terrainGrid.get(x2, y2, z2).isBlocking())
			{
				if (dy < 0)
					return true;
				else if (dy == 0 && dz > 0)
					return true;
			}
		}
		
		//calculate steps for iterating through line of sight points in between the two points
		float step = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));		
		float xStep = (float)dx / step;
		float yStep = (float)dy / step;
		float zStep = (float)dz / step;
		
		//check spaces in between light source and test cell
		//System.out.println();
		//System.out.println("Light source: " + x1 + ", " + y1 + ", " + z1);
		for (int n = 1; n < step; n ++)
		{
			int i = (int)(x1 + n * xStep + .5);
			int j = (int)(y1 + n * yStep + .5);
			int k = (int)(z1 + n * zStep + .5);
			//System.out.println(i + ", " + j + ", " + k);
			
			if (isLightBlocking(i, j, k))
				return true;
		}
		//System.out.println("Target cell: " + x2 + ", " + y2 + ", " + z2);
		
		return false;
	}
	
	public boolean isCrossable(int x, int y, int z)
	{
		if (!this.isInBounds(x, y, z - 1))
		{
			if (this.hasThing(x, y, z))
				return this.thingGrid.get(x, y, z).isCrossable();
			return false;
		}
		
		
		if (this.terrainGrid.get(x, y, z-1).isBlocking())
			return true;
		else
		{
			if (this.hasThing(x, y, z))
			{
				return this.thingGrid.get(x, y, z).isCrossable();
			}
			return false;
		}
	}
	
	public boolean isLandable(int x, int y, int z)
	{
		//Add things that can't be landed on here
		if (this.hasThing(x, y, z) && this.thingGrid.get(x, y, z).hasRamp())
			return false;
		else
			return isCrossable(x, y, z);
	}
	
	/**
	 * Check whether a specified location is within the bounds of the world grid
	 * 
	 * @param x grid location
	 * @param y grid location
	 * @param z grid location
	 * @return true if the specified grid space is within bounds
	 */
	public boolean isInBounds(int x, int y, int z)
	{
		if (x >= 0 && x < width && y >= 0 && y < depth && z >= 0 && z < height)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Check whether a specified location is within the bounds of the world grid
	 * 
	 * @param Position 3D grid position
	 * @return true if the specified grid space is within bounds
	 */
	public boolean isInBounds(Position pos)
	{
		if (pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < depth && pos.z >= 0 && pos.z < height)
		{
			return true;
		}
		return false;
	}
	
	public void setTerrain(Grid<Terrain> t)
	{
		for (int i = 0; i < t.getWidth(); i ++)
		{
			for (int j = 0; j < t.getDepth(); j ++)
			{
				for (int k = 0; k < t.getHeight(); k ++)
				{
					//bounds checking
					if (i < width && j < depth && k < height)
					{
						terrainGrid.set(i, j, k, t.get(i, j, k));
					}
				}
			}
		}
	}
		
	/**
	 * Getter for the player-controlled agent
	 * @return the current player-controlled agent, null if there isn't one
	 */
	public Hero getPlayer()
	{
		return player;
	}
	
	/**
	 * Setter for the player-controlled agent
	 * @param agent the new player-controlled agent
	 */
	public void setPlayer(Hero hero)
	{
		player = hero;
	}

	public void setTod(TimeOfDay tod) {
		this.tod = tod;
	}

	public TimeOfDay getTod() {
		return tod;
	}

	public void setCameraLockV(boolean cameraLockV) {
		this.cameraLockV = cameraLockV;
	}

	public boolean isCameraLockV() {
		return cameraLockV;
	}

	public void setCameraLockH(boolean cameraLockH) {
		this.cameraLockH = cameraLockH;
	}

	public boolean isCameraLockH() {
		return cameraLockH;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public float getWidth() {
		return width;
	}

	public void setTextBoxActive(boolean textBoxActive) {
		this.textBoxActive = textBoxActive;
	}

	public boolean isTextBoxActive() {
		return textBoxActive;
	}

	public void setTextDisplay(DisplayText textDisplay) {
		this.textDisplay = textDisplay;
	}

	public DisplayText getTextDisplay() {
		return textDisplay;
	}

	public ControlState getCs() {
		return cs;
	}

	public void setCs(ControlState cs) {
		this.cs = cs;
	}

	public String save() 
	{
		String data = new String();
		data = width + "," + depth + "," + height + "\n";
		data += displayCenter[0] + "," + displayCenter[1] + "\n";
		
		return null;
	}

	public static World load(String data) {
		
		return null;
	}
}
