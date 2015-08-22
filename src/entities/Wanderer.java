package entities;

import static entities.Agent.Direction.Down;
import static entities.Agent.Direction.Left;
import static entities.Agent.Direction.Right;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Wander;
import world.Position;
import world.Map;

public class Wanderer extends Agent {
	int frequency;
	int distance;
	
	//Actions
	Wander wander;
	
	/**
	 * Constructor
	 */
	public Wanderer(Position homePos, int frequency, int distance)
	{
		super(false, false);
		
		this.frequency = frequency;
		this.distance = distance;
		this.setHomePos(homePos);
		
		setActions();		
		initState();
	}
	
	@Override
	protected void setActions()
	{
		super.setActions();
		wander = new Wander(frequency, distance);
	}
	
	@Override
	public void initState()
	{
		super.initState();
		setDir(Down);
		setSpeed(2);
		setStepping(false);
		setFootstep(Left);
		setHeight(2);
	}
	
	@Override
	public void decideNextAction(Map world) 
	{
		if (currentAction != wander)
			currentAction = wander;
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
			case Down:
				texY = 0;
				break;
			case Right:
				texY = 1;
				break;
			case Left:
				texY = 2;
				break;
			case Up:
				texY = 3;				
				break;
			}
			
			if ((Math.abs(offset[0]) <= 16 && Math.abs(offset[0]) > 7) 
				|| (Math.abs(offset[1]) <= 16) && Math.abs(offset[1]) > 7)
			{
				if (getFootstep() == Right)
					texX = 2;
				else
					texX = 0;
			}
			else
			{
				texX = 1;
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

	@Override
	public String save()
	{
		String data = new String("");
		data += "Wanderer:\n";
		data += this.saveCommon();
		data += frequency + "," + distance + "\n";
		return data;
	}
	
	public static Wanderer load(String data)
	{
		//read in common data
		CommonData commonData = Agent.loadCommon(data);
		data = commonData.remainingData;
		
		//read Wanderer-specific data
		int frequency = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		int distance = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		
		//create agent and set any relevant data
		Wanderer wanderer = new Wanderer(commonData.pos, frequency, distance);
		wanderer.setDir(commonData.dir);
		wanderer.setOnRamp(commonData.onRamp);
		
		return wanderer;
	}
}
