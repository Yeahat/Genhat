package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import world.World;

import entities.Agent;

public class Firewood extends Thing {

	int quantity;
	int quantityUnit;	//amount of quantity to be added/removed to warrant a change in appearance (max quantity is quantityUnit * 3)
	
	public Firewood()
	{
		loadTextures();
		
		texRow = 7;
		texCol = 0;		
		quantityUnit = 5;
		setQuantity(quantityUnit*3);
	}
	
	public Firewood(int q)
	{
		loadTextures();
		
		texRow = 7;
		texCol = 0;
		quantityUnit = 5;
		setQuantity(q);
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
			int displayFrame = (int)(Math.ceil((double)quantity / (double)quantityUnit));
			texX += (3 - displayFrame);
			
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

	@Override
	public void interact(Agent agent, World world)
	{
		if (quantity > 0)
			setQuantity(quantity - 1);
	}
	
	public void setQuantity(int q)
	{
		if (q > quantityUnit * 3)
		{
			System.out.println("Amount given is greater than the set quantity this thing can have, value reset to the maximum quantity");
			q = quantityUnit * 3;
		}
		quantity = q;
		if (q <= 0)
			blocking = false;
		else
			blocking = true;
	}
}
