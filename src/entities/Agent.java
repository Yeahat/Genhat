package entities;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import world.World;
import actions.Action;
import actions.Idle;

public abstract class Agent {
	//Enumerations
	//Note: use "import static entities.Agent.(enum name).*;" to use these directly in different files
	public enum direction
	{
		left, right, up, down
	}
	
	//State information
	//Note: All state information for any agent implementation should go here, not in the specific
	//agent class.  Much of it may be unused by specific agent implementations, but this will allow
	//for any action to be attributed to any agent.
	int[] pos = new int[3]; //Position (x, y, z)
	float[] offset = new float[2]; //Pixel offset from current position (x, y, z), truncated to the nearest pixel when rendering
	Action currentAction;
	ArrayList<String> args;	//extra arguments for executing actions
	private direction dir;	//direction the agent is facing
	private int speed;	//speed that the agent is walking at, must be a power of 2 (measured in pixels per second)
	boolean stepping;	//true if the agent is currently taking a step
	private direction footstep; //whether the next step is the left or right foot
	private int height;	//Agent height in tiles
	private int[] homePos;
	
	//Actions: List all actions of this agent here
	Idle idle;
	
	//Textures
	protected Texture texture;
	protected int TEXTURE_SIZE_X = 24;	//Sprite sheet character width, this can be overridden
	protected int TEXTURE_SIZE_Y = 32;	//Sprite sheet character height, this can be overridden
	protected int TEXTURE_SHEET_WIDTH = 256; //Sprite sheet height, this can be overridden (must be a multiple of 2)
	protected int TEXTURE_SHEET_HEIGHT = 256; //Sprite sheet width, this can be overridden (must be a multiple of 2)
	
	/**
	 * Constructor
	 */
	public Agent()
	{
		setActions();		
		loadTextures();
		initState();
	}
	
	/**
	 * Constructor that may or may not initialize state
	 * @param initState true if state should be initialized, false otherwise
	 */
	public Agent(boolean initState)
	{
		setActions();
		loadTextures();
		if (initState)
			initState();
	}
	
	/**
	 * Constructor that may or may not initialize state and set actions
	 * @param initState true if state should be initialized, false otherwise
	 * @param setActions true if actions should be set, false otherwise
	 */
	public Agent(boolean initState, boolean setActions)
	{
		if (setActions)
			setActions();
		loadTextures();
		if (initState)
			initState();
	}
	
	/**
	 * Add the agent's actions to the action list, override this method to
	 * set the correct actions for each agent implementation
	 */
	protected void setActions()
	{
		//instantiate actions here
		idle = new Idle();
		
		//set initial action
		currentAction = idle;
	}
	
	/**
	 * Add arguments to be used as action parameters
	 * @param newArgs the new arguments to use when calling actions
	 */
	public void setArgs(ArrayList<String> newArgs)
	{
		args = newArgs;
	}
	
	/**
	 * Update the currentAction based on world information
	 * @param world the world
	 */
	public abstract void decideNextAction(World world);
	
	/**
	 * Execute the current action
	 * @param world the world information to be passed through to the action's execute call
	 */
	public void executeAction(World world)
	{
		currentAction.execute(this, world, args);
	}
	
	/**
	 * Incrementer for the x offset value
	 * @param value the value by which to increment x
	 */
	public void incrementXOffset(float value)
	{
		offset[0] += value;
	}
	
	/**
	 * Incrementer for the y offset value
	 * @param value the value by which to increment y
	 */
	public void incrementYOffset(float value)
	{
		offset[1] += value;
	}
	
	/**
	 * Load any required textures for displaying
	 */
	public abstract void loadTextures();
	
	/**
	 * Initialize any state variables to be used
	 */
	public void initState()
	{
		args = new ArrayList<String>();
		offset[0] = 0;
		offset[1] = 0;
	}
	
	/**
	 * Render the agent using OpenGL
	 * @param pixelSize how many screen pixels make up a "game" pixel
	 * @param terrainTextureSize size of the terrain texture (i.e. dimensions of the world grid)
	 */
	public abstract void renderAgent(int pixelSize, int terrainTextureSize);

	
	//***********************************************************
	//**************** Getters and Setters **********************
	//***********************************************************
	
	/**
	 * Setter for the current action
	 * @param action the new action to be set as current
	 */
	public void setCurrentAction(Action action)
	{
		currentAction = action;
	}
	
	public void setStepping(boolean stepping) {
		this.stepping = stepping;
	}

	public boolean isStepping() {
		return stepping;
	}
	
	/**
	 * Setter for position
	 * @param newPos new values for position (x, y, z), must be of size 3 or it will do nothing
	 */
	public void setPos(int[] newPos)
	{
		if (newPos.length < 3)
			return;
		pos[0] = newPos[0];
		pos[1] = newPos[1];
		pos[2] = newPos[2];
	}
	
	/**
	 * Getter for position
	 */
	public int[] getPos()
	{
		return pos;
	}
	
	/**
	 * Setter for offset
	 * @param newOffset new values for offset (x, y), must be of size 2 or it will do nothing
	 */
	public void setOffset(float[] newOffset)
	{
		if (newOffset.length < 2)
			return;
		offset[0] = newOffset[0];
		offset[1] = newOffset[1];
	}
	
	public void setOffsetX(float newOffset)
	{
		offset[0] = newOffset;
	}
	
	public void setOffsetY(float newOffset)
	{
		offset[1] = newOffset;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return speed;
	}
	
	public float getOffsetX() {
		return offset[0];
	}
	
	public float getOffsetY() {
		return offset[1];
	}
	
	public Action getCurrentAction() {
		return currentAction;
	}

	public void setDir(direction dir) {
		this.dir = dir;
	}

	public direction getDir() {
		return dir;
	}

	public void setFootstep(direction footstep) {
		this.footstep = footstep;
	}

	public direction getFootstep() {
		return footstep;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setHomePos(int[] homePos) {
		this.homePos = homePos;
	}

	public int[] getHomePos() {
		return homePos;
	}
}
