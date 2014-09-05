package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import world.World;

import entities.Agent;
import entities.Agent.direction;
import static entities.Agent.direction.*;

public class Chair extends Thing {

	boolean pushedIn;
	
	public Chair()
	{
		loadTextures();
		
		texRow = 1;
		texCol = 0;
		transparent = true;
		setDir(left);
		
		pushedIn = true;
		blocking = true;
		
	}
	
	public Chair(direction d)
	{
		loadTextures();
		
		texRow = 1;
		texCol = 0;
		transparent = true;
		setDir(d);
		
		pushedIn = true;
		blocking = true;
	}
	
	/**
	 * Overloaded constructor
	 * @param d direction
	 * @param p pushed in flag
	 */
	public Chair(direction d, boolean p)
	{
		loadTextures();
		
		texRow = 1;
		texCol = 0;
		transparent = true;
		setDir(d);
		
		pushedIn = p;
		if (pushedIn)
			blocking = true;
		else
			blocking = false;
	}

	@Override
	public void interact(Agent agent, World world)
	{
		if (!world.isOccupied(this.getPos()[0], this.getPos()[1], this.getPos()[2]))
		{
			if (pushedIn)
			{
				pushedIn = false;
				blocking = false;
			}
			else
			{
				pushedIn = true;
				blocking = true;
			}
		}
	}
	
	@Override
	public void loadTextures() {
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing2.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	public void renderThing(int pixelSize, int terrainTextureSize) {
		GL11.glPushMatrix();
		
			texture.bind();
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
			float tConvX = ((float)TEXTURE_SIZE_X)/((float)TEXTURE_SHEET_WIDTH);
			float tConvY = ((float)TEXTURE_SIZE_Y)/((float)TEXTURE_SHEET_HEIGHT);
			
			int texX = texCol * 4;
			int texY = texRow;
			switch (getDir())
			{
			case up:
				if (pushedIn)
					texX += 1;
				else
					texX += 0;
				break;
			case down:
				if (pushedIn)
					texX += 3;
				else
					texX += 2;
				break;
			case right:
				texY += 1;
				if (pushedIn)
					texX += 1;
				else
					texX += 0;
				break;
			case left:
				texY += 1;
				if (pushedIn)
					texX += 3;
				else
					texX += 2;
				break;
			}
			
			int xMin = pixelSize * ((terrainTextureSize - TEXTURE_SIZE_X) / 2);
			int xMax = xMin + pixelSize * (TEXTURE_SIZE_X);
			int yMin = 0;
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
