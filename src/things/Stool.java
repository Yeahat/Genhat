package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import entities.Agent.direction;
import static entities.Agent.direction.*;

public class Stool extends Thing {

	boolean pushedIn;
	
	public Stool()
	{
		loadTextures();
		
		texRow = 2;
		texCol = 0;
		blocking = false;
		setDir(up);
		
		pushedIn = true;
		crossable = false;
	}
	
	public Stool(direction d)
	{
		loadTextures();
		
		texRow = 2;
		texCol = 0;
		blocking = false;
		setDir(d);
		
		pushedIn = true;
		crossable = false;
	}
	
	/**
	 * Overloaded constructor
	 * @param d direction
	 * @param p pushed in flag
	 */
	public Stool(direction d, boolean p)
	{
		loadTextures();
		
		texRow = 2;
		texCol = 0;
		blocking = false;
		setDir(d);
		
		pushedIn = p;
		if (pushedIn)
			crossable = false;
		else
			crossable = true;
	}

	@Override
	public void interact()
	{
		if (pushedIn)
		{
			pushedIn = false;
			crossable = true;
		}
		else
		{
			pushedIn = true;
			crossable = false;
		}
	}
	
	@Override
	public void loadTextures() {
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing2.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
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
