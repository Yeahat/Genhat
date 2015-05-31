package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import things.Thing.ConnectionContext;
import world.Position;
import entities.Agent.Direction;
import static things.Thing.ConnectionContext.*;
import static entities.Agent.Direction.*;

public class Rope extends ClimbingSurface {

	private Rope(RopeBuilder builder)
	{
		super();
		
		this.horizontalConnection = builder.horizontalConnection;
		this.verticalConnection = builder.verticalConnection;
		this.dir = builder.dir;
		
		this.texRow = 4;
		this.texCol = 0;
		
		loadTextures();
	}
	
	@Override
	public void loadTextures()
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing3.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@SuppressWarnings("incomplete-switch")
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
			
			switch (getVerticalConnection())
			{
			case Middle:		texY += 1;	break;
			case Start:			texY += 2;	break;
			case Standalone:	texY += 3;	break;
			}
				
			switch (getHorizontalConnection())
			{
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
		data += "Rope:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += dir.toString() + "\n";
		data += horizontalConnection.toString() + "," + verticalConnection.toString() + "\n";
		return data;
	}
	
	public static Rope load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read direction and connections
		Direction dir = Direction.valueOf(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		ConnectionContext horizontalConnection = ConnectionContext.valueOf(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		ConnectionContext verticalConnection = ConnectionContext.valueOf(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Rope rope = new Rope.RopeBuilder().dir(dir).horizontalConnection(horizontalConnection).verticalConnection(verticalConnection).build();
		rope.setPos(pos);
		
		return rope;
	}
	
	public static class RopeBuilder
	{
		private ConnectionContext horizontalConnection = Standalone;
		private ConnectionContext verticalConnection = Standalone;
		private Direction dir = Up;
		
		public RopeBuilder horizontalConnection(ConnectionContext horizontalConnection)
		{
			this.horizontalConnection = horizontalConnection;
			return this;
		}
		
		public RopeBuilder verticalConnection(ConnectionContext verticalConnection)
		{
			this.verticalConnection = verticalConnection;
			return this;
		}
		
		public RopeBuilder dir(Direction dir)
		{
			this.dir = dir;
			return this;
		}
		
		public Rope build()
		{
			return new Rope(this);
		}
	}
}
