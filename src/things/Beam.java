package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import entities.Agent.direction;
import static entities.Agent.direction.*;
import static things.Thing.connectionContext.*;
import static things.Thing.Orientation.*;

public class Beam extends Thing {

	private final connectionContext connection;
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
		 *   left: single beam, top-left to bottom-right (main) diagonal
		 *   right: single beam, bottom-left to top-right diagonal
		 *   up: crossing beam, main diagonal on top
		 *   down: crossing beam, main diagonal on bottom
		 */
		
		this.dir = builder.dir;
		this.orientation = builder.orientation;
		this.connection = builder.connection;
		
		this.transparent = true;
		
		if (this.orientation == vertical)
		{
			this.blocking = false;
		}
		
		switch (orientation)
		{
		case horizontal:
			texCol = 1;
			switch (dir)
			{
			case up: texRow = 0; break;
			case down: texRow = 1; break;
			case left: texRow = 2; break;
			case right: texRow = 3; break;
			}
			break;
		case vertical:
			texCol = 1;
			switch (dir)
			{
			case left: texRow = 4; break;
			case right: texRow = 5; break;
			case down: texRow = 6; break;
			case up: texRow = 7; break;
			}
			break;
		case diagonal:
			texCol = 0;
			texRow = 1;
			break;
		}
		
		loadTextures();
	}
	
	@Override
	public void loadTextures() {
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing1.png"));
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
			
			if (orientation == diagonal)
			{
				switch (dir)
				{
				case left: break;
				case right: texX += 1; break;
				case up: texX += 2; break;
				case down: texX += 3; break;
				}
			}
			else
			{
				switch (connection)
				{
				case start: break;
				case middle: texX += 1; break;
				case end: texX += 2; break;
				case standalone: texX += 3; break;
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

	public static class BeamBuilder
	{
		private connectionContext connection = standalone;
		private Orientation orientation = horizontal;
		private direction dir = down;
		
		public BeamBuilder(){}
		
		public BeamBuilder connection(connectionContext connection)
		{
			this.connection = connection;
			return this;
		}
		
		public BeamBuilder orientation(Orientation orientation)
		{
			this.orientation = orientation;
			return this;
		}
		
		public BeamBuilder dir(direction dir)
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
