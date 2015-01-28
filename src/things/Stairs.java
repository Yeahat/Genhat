package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import entities.Agent.direction;
import static entities.Agent.direction.*;
import static things.Thing.connectionContext.*;
import static things.Stairs.stairsType.*;

public class Stairs extends Thing {
	public enum stairsType
	{
		outdoorWooden, indoorWooden;
	}
	private final stairsType type;
	private final connectionContext connection;
	private final StairsBottom associatedBottom;	//an extra thing rendered for graphical consistancy
	
	private Stairs(StairsBuilder builder)
	{
		this.type = builder.type;
		this.connection = builder.connection;
		this.dir = builder.dir;
		
		this.crossable = true;
		this.ramp = true;
		
		switch (this.type)
		{
		case outdoorWooden:
			this.texRow = 0;
			this.texCol = 0;
			break;
		case indoorWooden:
			this.texRow = 4;
			this.texCol = 0;
			break;
		}

		if ((this.dir == left && (this.connection == middle || this.connection == connectionContext.start))
				|| (this.dir == right && (this.connection == middle || this.connection == connectionContext.end)))
			associatedBottom = new StairsBottom(this.dir);
		else
			associatedBottom = null;
		
		loadTextures();
	}
	
	@Override
	public void loadTextures()
	{
		try {
			//stairs of all types currently use the same texture sheet, add a conditional if this changes in the future
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
		if (type == outdoorWooden)
		{
			switch (getDir())
			{
			case down:
				texX += 0;
				break;
			case right:
				texX += 1;
				break;
			case up:
				texX += 2;
				break;
			case left:
				texX += 3;				
				break;
			}
		}
		else if (type == indoorWooden)
		{
			if (dir == left)
				texY += 1;
			switch (connection)
			{
			case start:
				break;
			case middle:
				texX += 1;
				break;
			case end:
				texX += 2;
				break;
			case standalone:
				texX += 3;
				break;
			}
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
	public Stairs remove()
	{
		if (this.associatedBottom != null)
			this.associatedBottom.remove();
		return null;
	}
	
	public StairsBottom getAssociatedBottom() {
		return associatedBottom;
	}

	public static class StairsBuilder
	{
		private final stairsType type;
		private connectionContext connection = standalone;
		private direction dir = right;
		
		
		public StairsBuilder(stairsType type)
		{
			this.type = type;
		}
		
		public StairsBuilder connection(connectionContext connection)
		{
			this.connection = connection;
			return this;
		}
		
		public StairsBuilder dir(direction dir)
		{
			this.dir = dir;
			return this;
		}
		
		public Stairs build()
		{
			return new Stairs(this);
		}
	}
}
