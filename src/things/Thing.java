package things;

import org.newdawn.slick.opengl.Texture;

import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;

public abstract class Thing {
	public enum connectionContext
	{
		start, middle, end, standalone
	}
	
	//State
	boolean blocking = true;
	boolean crossable = false;
	boolean ramp = false;
	boolean transparent = false; //true if thing should not block light sources
	protected boolean lightSource = false;
	protected boolean antiLightSource = false;
	protected float lightPower = 0.6f; //strength of light emission if the Thing is a light source, light absorption if the thing is an anti light source
	protected Position pos = new Position(); //Position (x, y, z)
	protected float[] realOffset = new float[3]; //sub-grid offset from the current position (x, y, z) measured in pixels (default is 16 pixels per grid space)
	protected float[] pixelOffset = new float[2]; //Pixel offset from current position (x, y, z), truncated to the nearest pixel when rendering
	protected direction dir;	//direction the thing is facing
	protected float terminalVelocity = 8.0f; //maximum change in offset allowed
	protected float[] velocity = new float[3]; //frame-by-frame changes in (x, y, z) position of the thing
	
	//Texture
	Texture texture;
	int texRow;
	int texCol;
	protected int TEXTURE_SIZE_X = 24;	//Sprite sheet character width, this can be overridden
	protected int TEXTURE_SIZE_Y = 32;	//Sprite sheet character height, this can be overridden
	protected int TEXTURE_SHEET_WIDTH = 256; //Sprite sheet height, this can be overridden (must be a multiple of 2)
	protected int TEXTURE_SHEET_HEIGHT = 256; //Sprite sheet width, this can be overridden (must be a multiple of 2)
	
	/**
	 * Load any required textures for displaying
	 */
	public abstract void loadTextures();
	
	/**
	 * Render the Thing with OpenGL
	 * @param pixelSize the ratio of real screen pixels to in game pixels
	 * @param terrainTextureSize the pixel dimensions of a terrain tile
	 */
	public abstract void renderThing(int pixelSize, int terrainTextureSize);
	
	/**
	 * Time update for a thing, empty by default but can be overridden.
	 */
	public void update(){}
	
	/**
	 * Action taken when the thing is interacted with; empty by default but can be overridden.
	 * @param agent the agent interacting with the thing
	 * @param world the world in which the thing exists
	 */
	public void interact(Agent agent, World world){}
	
	/**
	 * Method to call when removing a thing from the world, allows a thing's removal to first make any necessary changes
	 * to surrounding things that it affects.
	 * @return whatever's left when the thing is removed (often null or a destroyed version of the thing)
	 */
	public Thing remove()
	{
		return null;
	}
	
	/**
	 * Define at what point gravity affects the thing.  This defaults to determining if a blocking thing, agent, or
	 * terrain is below the thing by one space in the z direction.  This can be overridden.
	 * 
	 * @param world the world in which the thing exists
	 * @return true if the object should not be at rest with regards to gravity
	 */
	private boolean gravityCheck(World world)
	{
		//check if there's room to fall
		if (!blockedBelow(world))
			return true;
		
		//case where object is currently falling (based on offset), but won't fall down a z-level in the grid
		if (realOffset[2] > 0)
			return true;
		
		return false;
	}
	
	/**
	 * Define how gravity affects the thing.  This is defaulted to have objects fall with nothing
	 * below them, but it can be overridden.  This method also assumes an offset is used to render objects
	 * within different grid locations, with the default offset being 16 pixels per grid cell.
	 * 
	 * @param world the world in which the thing exists
	 */
	public void applyGravity(World world)
	{
		//check to see if gravity should be acting on an object
		if (gravityCheck(world))
			velocity[2] = Math.max(velocity[2] - 1.0f, -terminalVelocity);
		else
			velocity[2] = 0;
	}
	
	/**
	 * Determine if a thing is blocked by the z-axis space under it
	 * @param world the world in which the thing exists
	 * @return true if blocked
	 */
	private boolean blockedBelow(World world)
	{
		if (pos.z - 1 < 0)
			return true;
		if ((world.getTerrainAt(pos.x, pos.y, pos.z - 1) != null && world.getTerrainAt(pos.x, pos.y, pos.z - 1).isBlocking())
				|| (world.getThingsAt(pos.x, pos.y, pos.z - 1) != null && world.getThingsAt(pos.x, pos.y, pos.z - 1).isBlocking())
				|| world.getAgentAt(pos.x, pos.y, pos.z - 1) != null)
			return true;
		return false;
	}
	
	/**
	 * Determine if a thing is blocked by the z-axis space above it
	 * @param world the world in which the thing exists
	 * @return true if blocked
	 */
	private boolean blockedAbove(World world)
	{
		if (!world.isInBounds(pos.x, pos.y, pos.z + 1)
				|| (world.getTerrainAt(pos.x, pos.y, pos.z + 1) != null && world.getTerrainAt(pos.x, pos.y, pos.z + 1).isBlocking())
				|| (world.getThingsAt(pos.x, pos.y, pos.z + 1) != null && world.getThingsAt(pos.x, pos.y, pos.z + 1).isBlocking())
				|| world.getAgentAt(pos.x, pos.y, pos.z + 1) != null)
			return true;
		return false;
	}

	/**
	 * Determine if a thing is blocked in a specified direction within its x-y plane
	 * @param world the world in which the thing exists
	 * @param d the direction to check for blocking
	 * @return true if blocked
	 */
	private boolean blockedInDirection(World world, direction d)
	{
		int x = pos.x, y = pos.y, z = pos.z;
		
		switch (d)
		{
		case left: x -= 1; break;
		case right: x += 1; break;
		case up: y += 1; break;
		case down: y -= 1; break;
		}
		
		if (!world.isInBounds(x, y, z) || world.isBlocked(x, y, z))
			return true;
		return false;
	}
	
	/**
	 * Add velocity to offset and update the thing's position accordingly.  This method assumes
	 * a thing cannot occupy the same space as a blocking terrain, a blocking thing, or an agent.
	 * 
	 * @param world the world in which the thing exists
	 */
	public void propagateOffset(World world)
	{
		realOffset[0] += velocity[0];
		realOffset[1] += velocity[1];
		realOffset[2] += velocity[2];
		
		
		
		//Hitting ceilings or moving up
		while (realOffset[2] > 8)
		{
			if (blockedAbove(world))
			{
				realOffset[2] = 8;
				velocity[2] = 0;
			}
			else
			{
				realOffset[2] -= 16;
				world.moveThing(this, 0, 0, 1);
			}
		}
		
		//Hitting floors or dropping down
		while (realOffset[2] < -8)
		{
			if (blockedBelow(world))
			{
				realOffset[2] = 0;
				velocity[2] = 0;
			}
			else
			{
				realOffset[2] += 16;
				world.moveThing(this, 0, 0, -1);
			}
		}
	}
	
	//***********************************************************
	//**************** Getters and Setters **********************
	//***********************************************************
	
	/**
	 * Getter for blocking
	 * @return true if blocking
	 */
	public boolean isBlocking()
	{
		return blocking;
	}
	
	/**
	 * Getter for crossable (i.e. an agent can walk over it even if there is no ground below)
	 * @return true if crossable
	 */
	public boolean isCrossable()
	{
		return crossable;
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
	 * Getter for whether or not something is a ramp (i.e. an agent walking on it will change
	 * the (x,y) position and the z position)
	 * @return true if it is a ramp
	 */
	public boolean isRamp()
	{
		return ramp;
	}

	public void setPos(Position pos) {
		this.pos = new Position(pos);
	}

	public Position getPos() {
		return pos;
	}

	public void setDir(direction dir) {
		this.dir = dir;
	}

	public direction getDir() {
		return dir;
	}

	public void setLightSource(boolean lightSource) {
		this.lightSource = lightSource;
	}

	public boolean isLightSource() {
		return lightSource;
	}

	public void setLightPower(float lightPower) {
		this.lightPower = lightPower;
	}

	public float getLightPower() {
		return lightPower;
	}
}
