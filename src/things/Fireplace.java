package things;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import world.GameState;
import world.Map;
import world.Position;
import entities.Agent;

public class Fireplace extends Thing {

	float baseLightPower;
	float updateFrequency;
	int minRate;
	int updateCounter = 0;
	int animationFrame;
	int woodLevel;	//amount of wood in the fireplace from 1 (least) to 3 (most)
	int burnoutTimer;	//minimum time to pass before the fire burns down to the next lower wood level
	int burnoutCounter = 0;	//frame counter for a burnout
	float burnoutChance;	//chance [0,1] that the fire will burn down to the next lower wood level after the minimum time is reached
	boolean lit;
	Random rand;
	
	public Fireplace()
	{
		loadTextures();
		
		texRow = 0;
		texCol = 1;
		blocking = true;
		crossable = false;
		fullBlock = true;
		tallBlock = true;
		blockingWidth = 22;
		setLightSource(true);

		lit = true;
		setWoodLevel(3);
		updateFrequency = 5;
		minRate = 3;
		setLightPower(baseLightPower);
		animationFrame = 0;
		burnoutTimer = 900; //15 seconds, for quick testing purposes	//TODO: make this reasonable for gameplay
		burnoutChance = .01f; //this chance is actually somewhat high, but is set this way for testing purposes
		rand = new Random();
	}
	
	public Fireplace(boolean isLit)
	{
		loadTextures();
		
		texRow = 0;
		texCol = 1;
		blocking = true;
		crossable = false;
		fullBlock = true;
		tallBlock = true;
		blockingWidth = 22;
		
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
		setWoodLevel(3);
		updateFrequency = 5;
		minRate = 3;
		setLightPower(baseLightPower);
		animationFrame = 0;
		burnoutTimer = 900; //15 seconds, for quick testing purposes	//TODO: make this reasonable for gameplay
		burnoutChance = .01f; //this chance is actually somewhat high, but is set this way for testing purposes
		
		rand = new Random();
	}
	
	public Fireplace(boolean isLit, int woodLevel)
	{
		loadTextures();
		
		texRow = 0;
		texCol = 1;
		blocking = true;
		crossable = false;
		fullBlock = true;
		tallBlock = true;
		blockingWidth = 22;
		
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
		if (woodLevel < 1)
			woodLevel = 1;
		else if (woodLevel > 3)
			woodLevel = 3;
		setWoodLevel(woodLevel);
		updateFrequency = 5;
		minRate = 3;
		setLightPower(baseLightPower);
		animationFrame = 0;
		burnoutTimer = 900; //15 seconds, for quick testing purposes	//TODO: make this reasonable for gameplay
		burnoutChance = .01f; //this chance is actually somewhat high, but is set this way for testing purposes
		
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
				else
					texX += 3;
			}
			switch(woodLevel)
			{
				case 1: texY += 2; break;
				case 2: texY += 1; break;
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
	public boolean interact(Agent agent, Map world, GameState gameState)
	{
		if (!lit)
			lit = true;
		setWoodLevel(3);
		
		return true;
	}
	
	//TODO: Have the piece check to make sure it has a left and right piece and adjust rendering accordingly, make the same changes for the left and right side
	@Override
	public void update()
	{
		if (lit)
		{
			if (burnoutCounter > burnoutTimer)
			{
				if (rand.nextFloat() <= burnoutChance)
				{
					if (woodLevel == 1)
						lit = false;
					else
						setWoodLevel(woodLevel - 1);
					burnoutCounter = 0;
				}
			}
			else
				burnoutCounter ++;
			
			updateCounter ++;
			if (updateCounter >= minRate)
			{
				if (rand.nextFloat() < 1.0f/updateFrequency)
				{
					float change = (rand.nextFloat() - .5f) * .05f;
					setLightPower(baseLightPower + change);
					updateCounter = 0;
				}
			}
		
			animationFrame ++;
			if (animationFrame >= 15)
			{
				animationFrame = 0;
			}
		}
	}
	
	/**
	 * Set the amount of wood in the fireplace
	 * @param level wood level from 1 (lowest) to 3 (highest)
	 */
	public void setWoodLevel(int level)
	{
		if (level < 2)
		{
			woodLevel = 1;
			baseLightPower = .3f;
		}
		else if (level == 2)
		{
			woodLevel = 2;
			baseLightPower = .5f;
		}
		else
		{
			woodLevel = 3;
			baseLightPower = .7f;
		}
	}
	
	@Override
	public String save()
	{
		String data = new String("");
		data += "Fireplace:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += lit + "," + woodLevel + "," + updateCounter + "," + animationFrame + "," + burnoutCounter + "\n";
		return data;
	}
	
	public static Fireplace load(String data)
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
		int woodLevel = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		int updateCounter = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		int animationFrame = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		int burnoutCounter = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Fireplace fireplace = new Fireplace(lit, woodLevel);
		fireplace.setPos(pos);
		fireplace.updateCounter = updateCounter;
		fireplace.animationFrame = animationFrame;
		fireplace.burnoutCounter = burnoutCounter;
		
		return fireplace;
	}
}
