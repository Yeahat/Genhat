package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import entities.Agent.direction;

import static entities.Agent.direction.*;

public class StairsIndoor extends Thing {
	direction renderDir;
	
	public StairsIndoor()
	{
		loadTextures();
		
		texRow = 4;
		texCol = 0;
		setDir(right);
		renderDir = right;
		blocking = true;
		crossable = true;
		ramp = true;
	}
	
	public StairsIndoor(direction d)
	{
		loadTextures();
		
		if (d == left)
			texRow = 5;
		else
			texRow = 4;
		texCol = 0;
		setDir(d);
		renderDir = d;
		crossable = true;
		ramp = true;
		blocking = true;
	}
	
	public StairsIndoor(direction d, direction d2)
	{
		loadTextures();
		
		if (d == left)
			texRow = 5;
		else
			texRow = 4;
		texCol = 0;
		setDir(d);
		renderDir = d2;
		crossable = true;
		ramp = true;
		blocking = true;
	}
	
	@Override
	public void loadTextures()
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing1.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public void renderThing(int pixelSize, int terrainTextureSize)
	{
		GL11.glPushMatrix();
		
		texture.bind();
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	
		float tConvX = ((float)TEXTURE_SIZE_X)/((float)TEXTURE_SHEET_WIDTH);
		float tConvY = ((float)TEXTURE_SIZE_Y)/((float)TEXTURE_SHEET_HEIGHT);
		
		int texX = texCol * 4;
		int texY = texRow;
		switch (renderDir)
		{
		case right:
			if (getDir() == left)
				texX += 0;
			else
				texX += 3;
			break;
		case down:
			texX += 1;
			break;
		case up:
			texX += 2;
			break;
		case left:
			if (getDir() == left)
				texX += 3;
			else
				texX += 0;				
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
