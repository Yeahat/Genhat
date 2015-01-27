package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import things.Stairs.StairsBuilder;
import things.Stairs.stairsType;
import things.Thing.connectionContext;
import world.World;
import entities.Agent;
import entities.Agent.direction;
import static entities.Agent.direction.*;
import static things.Thing.connectionContext.standalone;

public class Bar extends Thing {

	private final connectionContext connection;
	
	private Bar(BarBuilder builder)
	{
		this.dir = builder.dir;
		this.connection = builder.connection;
		
		texRow = 2;
		texCol = 0;
		blocking = true;
		
		loadTextures();
	}
	
	@Override
	public void loadTextures() {
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing1.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public void interact(Agent agent, World world){
		//get direction of interaction
		int x = 2*pos[0] - agent.getPos()[0];
		int y = 2*pos[1] - agent.getPos()[1];
		int z = pos[2];
		
		//pass on interaction if there is an agent one space along the interaction direction
		if (world.isOccupied(x, y, z))
		{
			world.getAgentAt(x, y, z).interact(agent, world);
		}
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
			if (dir == up || dir == down)
			{
				texY += 1;
			}
			switch (connection)
			{
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

	public static class BarBuilder
	{
		private connectionContext connection = standalone;
		private direction dir = left;
		
		public BarBuilder(){}
		
		public BarBuilder connection(connectionContext connection)
		{
			this.connection = connection;
			return this;
		}
		
		public BarBuilder dir(direction dir)
		{
			this.dir = dir;
			return this;
		}
		
		public Bar build()
		{
			return new Bar(this);
		}
	}
}
