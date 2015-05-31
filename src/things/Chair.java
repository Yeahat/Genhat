package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import world.Map;
import world.Position;
import entities.Agent;
import entities.Agent.Direction;
import static entities.Agent.Direction.*;
import static things.Chair.ChairType.*;

public class Chair extends Thing {
	public enum ChairType
	{
		chairWooden, stoolWooden
	}
	private boolean pushedIn;
	private final ChairType chairType;
	
	private Chair(ChairBuilder builder)
	{
		this.chairType = builder.chairType;
		this.dir = builder.dir;
		this.pushedIn = builder.pushedIn;
		
		this.transparent = true;
		
		if (this.chairType == chairWooden)
		{
			this.texRow = 1;
			this.texCol = 0;
		}
		else if (this.chairType == stoolWooden)
		{
			this.texRow = 3;
			this.texCol = 0;
		}
		
		if (!this.pushedIn)
		{
			this.blocking = false;
		}
		
		loadTextures();
	}
	
	@Override
	public boolean interact(Agent agent, Map world)
	{
		if (!world.isOccupied(this.getPos().x, this.getPos().y, this.getPos().z))
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
		
		return true;
	}
	
	@Override
	public void loadTextures() {
		try {
			//chairs of all types currently use the same texture sheet, add a conditional if this changes in the future
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
			case Up:
				if (pushedIn)
					texX += 1;
				else
					texX += 0;
				break;
			case Down:
				if (pushedIn)
					texX += 3;
				else
					texX += 2;
				break;
			case Right:
				texY += 1;
				if (pushedIn)
					texX += 1;
				else
					texX += 0;
				break;
			case Left:
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

	@Override
	public String save()
	{
		String data = new String("");
		data += "Chair:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += dir.toString() + "\n";
		data += chairType.toString() + "," + pushedIn + "\n";
		return data;
	}
	
	public static Chair load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read pushedIn and chairType
		Direction dir = Direction.valueOf(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		ChairType chairType = ChairType.valueOf(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		boolean pushedIn = Boolean.parseBoolean(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Chair chair = new Chair.ChairBuilder(chairType).dir(dir).pushedIn(pushedIn).build();
		chair.setPos(pos);
		
		return chair;
	}
	
	public static class ChairBuilder
	{
		private final ChairType chairType;
		private Direction dir = Up;
		private boolean pushedIn = true;
		
		public ChairBuilder(ChairType chairType)
		{
			this.chairType = chairType;
		}
		
		public ChairBuilder dir(Direction dir)
		{
			this.dir = dir;
			return this;
		}
		
		public ChairBuilder pushedIn(boolean pushedIn)
		{
			this.pushedIn = pushedIn;
			return this;
		}
		
		public Chair build()
		{
			return new Chair(this);
		}
	}
}
