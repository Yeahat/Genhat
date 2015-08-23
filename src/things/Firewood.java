package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import world.GameState;
import world.Map;
import world.Position;
import entities.Agent;
import entities.Agent.Direction;

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
	public boolean interact(Agent agent, Map world, GameState gameState)
	{
		if (quantity > 0)
			setQuantity(quantity - 1);
		return true;
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
	
	@Override
	public String save()
	{
		String data = new String("");
		data += "Firewood:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += quantity + "\n";
		return data;
	}
	
	public static Firewood load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read quantity
		int quantity = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Firewood firewood = new Firewood(quantity);
		firewood.setPos(pos);
		
		return firewood;
	}
}
