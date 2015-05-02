package entities;

import java.util.Stack;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import static entities.Agent.direction.*;
import world.Position;
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
	Position pos = new Position(); //Position (x, y, z)
	protected float[] offset = new float[2]; //Pixel offset from current position (x, y, z), truncated to the nearest pixel when rendering
	Action currentAction;
	Stack<Action> heldActionStack = new Stack<Action>(); //action that is put on hold until an interaction is completed
	private boolean interruptRequested = false;
	private boolean interrupted = false;
	private boolean interactingWithHero = false;
	Agent waitingInteractee; //the character who initiated an interaction and interrupt request
	private int texCol = 0; //texture row
	private int texRow = 0; //texture column
	private direction dir = down;	//direction the agent is facing
	private int speed = 2;	//speed that the agent is walking at, must be a power of 2 (measured in pixels per second)
	boolean stepping = false;	//true if the agent is currently taking a step
	private boolean jumping = false;	//true if the agent is currently jumping
	private boolean rampAscending = false;	//true if the agent is currently taking a step up a ramp
	private boolean rampDescending = false;	//true if the agent is currently taking a step down a ramp
	private boolean climbing = false; //true if the agent is currently climbing on a climbingSurface
	private boolean onRamp = false;	//true if the agent is currently standing on top of a ramp
	private boolean transparent = false; //true if the agent should not block light sources
	private boolean renderOnPlaceholder = false; //true if the agent should render on its placeholder's position while moving (for steps in +y direction)
	private direction footstep = left; //whether the next step is the left or right foot
	private direction stance = right;	//which foot to put first when jumping (left = regular footed, right = goofy footed)
	private int height = 2;	//Agent height in tiles
	private Position homePos;
	
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
	 * Constructor that may or may not initialize state, load textures, and set actions
	 * @param initState true if state should be initialized, false otherwise
	 * @param setActions true if actions should be set, false otherwise
	 */
	public Agent(boolean initState, boolean setActions, boolean loadTextures)
	{
		if (setActions)
			setActions();
		if (loadTextures)
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
		currentAction.execute(this, world);
	}
	
	/**
	 * What to do when the agent is interacted with by another agent; empty by default but can be overriden.
	 * @param agent the agent doing the interacting
	 * @param world the world in which the interacting agents exist
	 */
	public void interact(Agent agent, World world){}
	
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
		offset[0] = 0;
		offset[1] = 0;
	}
	
	/**
	 * Render the agent using OpenGL, this is currently implemented with a default
	 * rendering that assumes the texture has a left, middle, and right step.  This method
	 * can be overridden if more specific or different rendering is required.
	 * @param pixelSize how many screen pixels make up a "game" pixel
	 * @param terrainTextureSize size of the terrain texture (i.e. dimensions of the world grid)
	 */
	public void renderAgent(int pixelSize, int terrainTextureSize)
	{
		if (!isRenderOnPlaceholder())
		{
			GL11.glPushMatrix();
			
				texture.bind();
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
				float tConvX = ((float)TEXTURE_SIZE_X)/((float)TEXTURE_SHEET_WIDTH);
				float tConvY = ((float)TEXTURE_SIZE_Y)/((float)TEXTURE_SHEET_HEIGHT);
				
				int texX = getTexCol() * 3;
				int texY = getTexRow();
				if (isClimbing())
				{
					texY += 3;
				}
				else
				{
					switch (getDir())
					{
					case down:	texY += 0;	break;
					case right:	texY += 1;	break;
					case left:	texY += 2;	break;
					case up:	texY += 3;	break;
					}
				}
				
				//Set footstep animation for jumping
				if (this.isJumping())
				{
					if (getStance() == right)
						texX += 2;
					else
						texX += 0;
				}
				//Set footstep animation for regular stepping
				else
				{
					if (getDir() == left || getDir() == right)
					{
						if (Math.abs(offset[0]) <= 16 && Math.abs(offset[0]) > 7)
						{
							if (getFootstep() == right)
								texX += 2;
							else
								texX += 0;
						}
						else
						{
							texX += 1;
						}
					}
					else
					{
						if (Math.abs(offset[1]) <= 16 && Math.abs(offset[1]) > 7)
						{
							if (getFootstep() == right)
								texX += 2;
							else
								texX += 0;
						}
						else
						{
							texX += 1;
						}
					}
				}
				
				int xMin = pixelSize * ((terrainTextureSize - TEXTURE_SIZE_X) / 2 + (int)(offset[0]));
				int xMax = xMin + pixelSize * (TEXTURE_SIZE_X);
				int yMin = pixelSize * ((int)(offset[1]));
				int yMax = yMin + pixelSize * (TEXTURE_SIZE_Y);
				
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(texX * tConvX, texY*tConvY + tConvY);
					GL11.glVertex2f(xMin, yMin);
					GL11.glTexCoord2f(texX*tConvX + tConvX, texY*tConvY + tConvY);
					GL11.glVertex2f(xMax, yMin);
					GL11.glTexCoord2f(texX*tConvX + tConvX, texY * tConvY);
					GL11.glVertex2f(xMax, yMax);
					GL11.glTexCoord2f(texX*tConvX, texY * tConvY);
					GL11.glVertex2f(xMin, yMax);
				GL11.glEnd();
				
			GL11.glPopMatrix();
		}
	}

	
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
	public void setPos(Position pos)
	{
		this.pos = pos;
	}
	
	/**
	 * Getter for position
	 */
	public Position getPos()
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

	public void setRampAscending(boolean rampStepping) {
		this.rampAscending = rampStepping;
	}

	public boolean isRampAscending() {
		return rampAscending;
	}

	public void setRampDescending(boolean rampDescending) {
		this.rampDescending = rampDescending;
	}

	public boolean isRampDescending() {
		return rampDescending;
	}

	public void setOnRamp(boolean onRamp) {
		this.onRamp = onRamp;
	}

	public boolean isOnRamp() {
		return onRamp;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setStance(direction stance) {
		this.stance = stance;
	}

	public direction getStance() {
		return stance;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public int getTexRow() {
		return texRow;
	}

	public void setTexRow(int texRow) {
		this.texRow = texRow;
	}

	public int getTexCol() {
		return texCol;
	}

	public void setTexCol(int texCol) {
		this.texCol = texCol;
	}

	public boolean isRenderOnPlaceholder() {
		return renderOnPlaceholder;
	}

	public void setRenderOnPlaceholder(boolean renderOnPlaceholder) {
		this.renderOnPlaceholder = renderOnPlaceholder;
	}

	public boolean isInterruptRequested() {
		return interruptRequested;
	}

	public void setInterruptRequested(boolean interruptRequested) {
		this.interruptRequested = interruptRequested;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public boolean isInteractingWithHero() {
		return interactingWithHero;
	}

	public void setInteractingWithHero(boolean interactingWithHero) {
		this.interactingWithHero = interactingWithHero;
	}

	public Position getHomePos() {
		return homePos;
	}

	public void setHomePos(Position homePos) {
		this.homePos = homePos;
	}

	public boolean isClimbing() {
		return climbing;
	}

	public void setClimbing(boolean climbing) {
		this.climbing = climbing;
	}
}
