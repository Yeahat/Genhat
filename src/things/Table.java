package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import things.Thing.ConnectionContext;
import things.Thing.Orientation;
import world.GameState;
import world.Map;
import world.Position;
import entities.Agent;
import entities.Agent.Direction;
import static entities.Agent.Direction.*;

public class Table extends Thing {

	public Table()
	{
		loadTextures();
		
		texRow = 0;
		texCol = 0;
		blocking = true;
		setDir(Left);
	}
	
	public Table(Direction d)
	{
		loadTextures();
		
		texRow = 0;
		texCol = 0;
		blocking = true;
		setDir(d);
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
			switch (getDir())
			{
			case Left:
				texX += 0;
				break;
			case Down:
				texX += 1;
				break;
			case Right:
				texX += 2;
				break;
			case Up:
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
		data += "Table:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += dir.toString() + "\n";
		return data;
	}
	
	public static Table load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read direction
		Direction dir = Direction.valueOf(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Table table = new Table(dir);
		table.setPos(pos);
		
		return table;
	}
	
}
