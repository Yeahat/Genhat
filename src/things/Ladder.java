package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import things.Thing.ConnectionContext;
import things.Thing.Orientation;
import world.Position;
import entities.Agent.Direction;
import static things.Thing.ConnectionContext.*;
import static entities.Agent.Direction.*;

public class Ladder extends ClimbingSurface {

	public Ladder()
	{
		super();
		
		this.horizontalConnection = Standalone;
		this.verticalConnection = Standalone;
		
		this.dir = Up;
		this.texRow = 7;
		this.texCol = 1;
		
		loadTextures();
	}
	
	public Ladder(ConnectionContext verticalConnection)
	{
		super();
		
		this.dir = Up;
		horizontalConnection = Standalone;
		this.verticalConnection = verticalConnection;
		
		this.texRow = 7;
		this.texCol = 1;
		
		loadTextures();
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
			switch (getVerticalConnection())
			{
			case Start:
				texX += 0;
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
		data += "Ladder:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += verticalConnection.toString() + "\n";
		return data;
	}
	
	public static Ladder load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read verticalConnection
		ConnectionContext verticalConnection = ConnectionContext.valueOf(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Ladder ladder = new Ladder(verticalConnection);
		ladder.setPos(pos);
		
		return ladder;
	}
}
