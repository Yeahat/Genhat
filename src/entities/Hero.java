package entities;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Step;
import actions.Turn;

import world.World;

import static entities.Agent.direction.*;

public class Hero extends Agent {
	int row;
	int column;
	
	//Actions
	Step step;
	Turn turn;
	
	/**
	 * Constructor
	 */
	public Hero()
	{
		super();
	}
	
	@Override
	protected void setActions()
	{
		super.setActions();
		step = new Step();
		turn = new Turn();
	}
	
	@Override
	public void initState()
	{
		super.initState();
		setDir(down);
		setSpeed(2);
		setFootstep(left);
		setHeight(2);
	}
	
	@Override
	public void decideNextAction(World world) 
	{
		if (currentAction.isFinished())
		{
			currentAction = idle;
			args.clear();
		}
	}

	@Override
	public void loadTextures() 
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/characters/char1.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public void renderAgent(int pixelSize, int terrainTextureSize) 
	{
		GL11.glPushMatrix();
			
			texture.bind();
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
			float tConvX = ((float)TEXTURE_SIZE_X)/((float)TEXTURE_SHEET_WIDTH);
			float tConvY = ((float)TEXTURE_SIZE_Y)/((float)TEXTURE_SHEET_HEIGHT);
			
			int texX = 0, texY = 0;
			switch (getDir())
			{
			case down:
				texY = 0;
				break;
			case right:
				texY = 1;
				break;
			case left:
				texY = 2;
				break;
			case up:
				texY = 3;				
				break;
			}
			
			//Special case footstep animations
			//Set footstep animation for walking up ramps down-facing ramps
			if (this.isRampAscending() && this.getDir() == up)
			{
				if ((Math.abs(offset[0]) <= 16 && Math.abs(offset[0]) > 7)
					|| (Math.abs(offset[1]) <= 16 && Math.abs(offset[1]) > 7))
				{
					if (getFootstep() == right)
						texX = 0;
					else
						texX = 2;
				}
				else if ((Math.abs(offset[0]) <= 32 && Math.abs(offset[0]) > 23)
				|| (Math.abs(offset[1]) <= 32 && Math.abs(offset[1]) > 23))
				{
					if (getFootstep() == right)
						texX = 2;
					else
						texX = 0;
				}
				else
				{
					texX = 1;
				}
			}
			//Set footstep animation for walking down down-facing ramps
			else if (this.isRampDescending() && this.getDir() == down)
			{
				if ((Math.abs(offset[0]) <= 7 && Math.abs(offset[0]) > 0)
					|| (Math.abs(offset[1]) <= 7 && Math.abs(offset[1]) > 0))
				{
					if (getFootstep() == right)
						texX = 2;
					else
						texX = 0;
				}
				else if ((Math.abs(offset[0]) <= 23 && Math.abs(offset[0]) > 16)
				|| (Math.abs(offset[1]) <= 23 && Math.abs(offset[1]) > 16))
				{
					if (getFootstep() == right)
						texX = 0;
					else
						texX = 2;
				}
				else
				{
					texX = 1;
				}
			}
			//Set footstep animation for regular stepping
			else
			{
				if (getDir() == left || getDir() == right)
				{
					if (Math.abs(offset[0]) <= 16 && Math.abs(offset[0]) > 7)
						{
							if (getFootstep() == right)
								texX = 2;
							else
								texX = 0;
						}
						else
						{
							texX = 1;
						}
				}
				else
				{
					if (Math.abs(offset[1]) <= 16 && Math.abs(offset[1]) > 7)
					{
						if (getFootstep() == right)
							texX = 2;
						else
							texX = 0;
					}
					else
					{
						texX = 1;
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
	
	/**
	 * Getter for the hero's step action, this is required to allow the keyboard polling to set
	 * the agent's action from outside of the scope of the class
	 * @return the agent's step action
	 */
	public Step getStepAction()
	{
		return step;
	}
	
	/**
	 * Getter for the hero's turn action, this is required to allow the keyboard polling to set
	 * the agent's action from outside of the scope of the class
	 * @return the agent's turn action
	 */
	public Turn getTurnAction()
	{
		return turn;
	}
	
	/**
	 * Getter for whether or not the hero is idle, this is required to allow the keyboard polling to determine
	 * whether it should change the agent's action from outside the scope of the class
	 * @return true if the agent's current action is idle
	 */
	public boolean isIdle()
	{
		if (currentAction == idle)
			return true;
		else
			return false;
	}
}
