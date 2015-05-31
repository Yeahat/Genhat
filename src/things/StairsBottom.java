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

public class StairsBottom extends Thing {

	public StairsBottom()
	{
		loadTextures();
		
		texRow = 7;
		texCol = 0;
		setDir(Left);
		blocking = false;
		crossable = true;
		ramp = false;
		setAssociated(true);
	}
	
	public StairsBottom(Direction d)
	{
		loadTextures();
		
		texRow = 7;
		texCol = 0;
		setDir(d);
		blocking = false;
		crossable = true;
		ramp = false;
		setAssociated(true);
	}
	
	@Override
	public void loadTextures()
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing1.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public boolean interact(Agent agent, Map world)
	{
		return false;
	}
	
	@Override
	public String save()
	{
		String data = new String("");
		data += "StairsBottom:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += dir.toString() + "\n";
		return data;
	}
	
	public static StairsBottom load(String data)
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
		StairsBottom stairsBottom = new StairsBottom(dir);
		stairsBottom.setPos(pos);
		
		return stairsBottom;
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
		
		if (getDir() == Left)
			texX += 1;
		
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
}
