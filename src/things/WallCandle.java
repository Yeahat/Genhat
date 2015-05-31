package things;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import world.Position;

public class WallCandle extends Thing {

	float baseLightPower;
	float updateFrequency;
	int minRate;
	int updateCounter = 0;
	int animationFrame;
	boolean lit;
	Random rand;
	
	public WallCandle()
	{
		loadTextures();
		
		texRow = 6;
		texCol = 0;
		blocking = false;
		crossable = false;
		setLightSource(true);

		lit = true;
		baseLightPower = .6f;
		updateFrequency = 5;
		minRate = 3;
		setLightPower(baseLightPower);
		animationFrame = 0;
		
		rand = new Random();
	}
	
	public WallCandle(boolean isLit)
	{
		loadTextures();
		
		texRow = 6;
		texCol = 0;
		blocking = false;
		crossable = false;
		
		if (isLit)
		{
			lit = true;
			setLightSource(true);
		}
		else
		{
			lit = false;
			setLightSource(false);
		}
		baseLightPower = .6f;
		updateFrequency = 5;
		minRate = 3;
		setLightPower(baseLightPower);
		animationFrame = 0;
		
		rand = new Random();
	}
	
	@Override
	public void loadTextures() 
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing2.png"));
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
			
			if (lit)
			{
				if (animationFrame < 5)
					texX += 1;
				else if (animationFrame < 10)
					texX += 2;
				else if (animationFrame < 15)
					texX += 3;
				else if (animationFrame < 20)
					texX += 2;
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

	@Override
	public void update()
	{
		if (lit)
		{
			updateCounter ++;
			if (updateCounter >= minRate)
			{
				if (rand.nextFloat() < 1.0f/updateFrequency)
				{
					float change = (rand.nextFloat() - .5f) * .025f;
					setLightPower(baseLightPower + change);
					updateCounter = 0;
				}
			}
		
			animationFrame ++;
			if (animationFrame >= 20)
			{
				animationFrame = 0;
			}
		}
	}
	
	@Override
	public String save()
	{
		String data = new String("");
		data += "WallCandle:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += lit + "," + updateCounter + "," + animationFrame + "\n";
		return data;
	}
	
	public static WallCandle load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read lit and flicker state data
		boolean lit = Boolean.parseBoolean(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		int updateCounter = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		int animationFrame = Integer.parseInt(data.substring(0, data.indexOf('\n')));

		//create thing and set any relevant data
		WallCandle wallCandle = new WallCandle(lit);
		wallCandle.setPos(pos);
		wallCandle.updateCounter = updateCounter;
		wallCandle.animationFrame = animationFrame;
		
		return wallCandle;
	}
}
