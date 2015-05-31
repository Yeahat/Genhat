package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import world.Position;
import entities.Agent.Direction;
import static entities.Agent.Direction.*;
import static things.Thing.ConnectionContext.*;
import static things.Thing.Orientation.*;

public class Beam extends Thing {

	private final ConnectionContext connection;
	private final Orientation orientation;
	
	private Beam(BeamBuilder builder)
	{
		/* Note: direction is used to specify a type, which has different meaning depending on orientation:
		 * 
		 * horizontal:
		 *   up - beam aligned to top of cell
		 *   down - beam aligned to bottom of cell
		 *   left - top half of beam for overlapping cells
		 *   right - bottom half of beam for overlapping cells
		 *   
		 * vertical:
		 *   left - beam aligned to left of cell
		 *   right - beam aligned to right of cell
		 *   down - right half of beam for overlapping cells
		 *   up - left half of beam for overlapping cells
		 *   
		 * diagonal:
		 *   left - single beam, top-left to bottom-right (main) diagonal
		 *   right - single beam, bottom-left to top-right diagonal
		 *   up - crossing beam, main diagonal on top
		 *   down - crossing beam, main diagonal on bottom
		 */
		
		this.dir = builder.dir;
		this.orientation = builder.orientation;
		this.connection = builder.connection;
		
		this.transparent = true;
		
		if (this.orientation == Vertical)
		{
			this.blocking = false;
		}
		
		switch (orientation)
		{
		case Horizontal:
			texCol = 1;
			switch (dir)
			{
			case Up: texRow = 0; break;
			case Down: texRow = 1; break;
			case Left: texRow = 2; break;
			case Right: texRow = 3; break;
			}
			break;
		case Vertical:
			texCol = 1;
			switch (dir)
			{
			case Left: texRow = 4; break;
			case Right: texRow = 5; break;
			case Down: texRow = 6; break;
			case Up: texRow = 7; break;
			}
			break;
		case Diagonal:
			texCol = 0;
			switch (dir)
			{
			case Left: texRow = 0; break;
			case Right: texRow = 1; break;
			case Up: texRow = 2; break;
			case Down: texRow = 3; break;
			}
			break;
		}
		
		loadTextures();
	}
	
	@Override
	public void loadTextures() {
		String sheet;
		if (orientation == Diagonal)
			sheet = "graphics/objects/thing3.png";
		else
			sheet = "graphics/objects/thing1.png";
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(sheet));
		} catch (IOException e) {e.printStackTrace();}
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
			
			switch (connection)
			{
			case Start: break;
			case Middle: texX += 1; break;
			case End: texX += 2; break;
			case Standalone: texX += 3; break;
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
		data += "Beam:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += dir.toString() + "\n";
		data += connection.toString() + "," + orientation.toString() + "\n";
		return data;
	}
	
	public static Beam load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read direction, connection, and orientation
		Direction dir = Direction.valueOf(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		ConnectionContext connection = ConnectionContext.valueOf(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		Orientation orientation = Orientation.valueOf(data.substring(0, data.indexOf('\n')));
		
		//create thing and set any relevant data
		Beam beam = new Beam.BeamBuilder().dir(dir).connection(connection).orientation(orientation).build();
		beam.setPos(pos);
		
		return beam;
	}
	
	public static class BeamBuilder
	{
		private ConnectionContext connection = Standalone;
		private Orientation orientation = Horizontal;
		private Direction dir = Down;
		
		public BeamBuilder(){}
		
		public BeamBuilder connection(ConnectionContext connection)
		{
			this.connection = connection;
			return this;
		}
		
		public BeamBuilder orientation(Orientation orientation)
		{
			this.orientation = orientation;
			return this;
		}
		
		public BeamBuilder dir(Direction dir)
		{
			this.dir = dir;
			return this;
		}
		
		public Beam build()
		{
			return new Beam(this);
		}
	}
}
