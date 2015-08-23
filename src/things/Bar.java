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
import static entities.Agent.Direction.*;
import static things.Thing.ConnectionContext.Standalone;

public class Bar extends Thing {

	private final ConnectionContext connection;
	
	private Bar(BarBuilder builder)
	{
		this.dir = builder.dir;
		this.connection = builder.connection;
		
		texRow = 5;
		texCol = 1;
		blocking = true;
		
		loadTextures();
	}
	
	@Override
	public void loadTextures() {
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing2.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public boolean interact(Agent agent, Map world, GameState gameState){
		//get direction of interaction
		int x = 2*pos.x - agent.getPos().x;
		int y = 2*pos.y - agent.getPos().y;
		int z = pos.z;
		
		//pass on interaction if there is an agent one space along the interaction direction
		if (world.isOccupied(x, y, z))
		{
			world.getAgentAt(x, y, z).interact(agent, world, gameState);
		}
		
		return true;
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
			if (dir == Up || dir == Down)
			{
				texY += 1;
			}
			switch (connection)
			{
			case Start:
				break;
			case Middle:
				texX += 1;
				break;
			case End:
				texX += 2;
				break;
			case Standalone:
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

	@Override
	public String save()
	{
		String data = new String("");
		data += "Bar:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += dir.toString() + "\n";
		data += connection.toString() + "\n";
		return data;
	}
	
	public static Bar load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read direction and connection
		Direction dir = Direction.valueOf(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		ConnectionContext connection = ConnectionContext.valueOf(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Bar bar = new Bar.BarBuilder().dir(dir).connection(connection).build();
		bar.setPos(pos);
		
		return bar;
	}
	
	public static class BarBuilder
	{
		private ConnectionContext connection = Standalone;
		private Direction dir = Left;
		
		public BarBuilder(){}
		
		public BarBuilder connection(ConnectionContext connection)
		{
			this.connection = connection;
			return this;
		}
		
		public BarBuilder dir(Direction dir)
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
