package entities;

import static entities.Agent.direction.down;
import static entities.Agent.direction.left;
import static entities.Agent.direction.right;
import static entities.Agent.direction.up;

import org.lwjgl.opengl.GL11;

import world.Position;
import world.World;

public class Placeholder extends Agent {

	Agent agent; //the agent for which the placeholder is acting
	
	public Placeholder(Agent agent, Position pos)
	{
		this.pos = pos;
		
		this.agent = agent;
		
		if (agent.isRenderOnPlaceholder() && !agent.isTransparent())
			this.setTransparent(false);
		else
			this.setTransparent(true);
	}
	
	@Override
	public void decideNextAction(World world) 
	{
		this.setCurrentAction(idle);
	}

	@Override
	public void loadTextures()
	{
		//load no texture, this is a placeholder to simply occupy a space while another agent is making
		//a special case move to that space
	}

	@Override
	public void renderAgent(int pixelSize, int terrainTextureSize)
	{
		if (agent.isRenderOnPlaceholder())
		{
			//make any necessary adjustments to offsets to account for difference between hero and placeholder
			this.offset[0] = agent.offset[0];
			this.offset[1] = agent.offset[1];
			if (agent.pos.y > this.pos.y)
			{
				this.offset[1] += 16;
			}
			
			//render the agent linked to this placeholder
			GL11.glPushMatrix();
			
				agent.texture.bind();
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
				float tConvX = ((float)agent.TEXTURE_SIZE_X)/((float)agent.TEXTURE_SHEET_WIDTH);
				float tConvY = ((float)agent.TEXTURE_SIZE_Y)/((float)agent.TEXTURE_SHEET_HEIGHT);
				
				int texX = agent.getTexCol() * 3;
				int texY = agent.getTexRow();
				switch (agent.getDir())
				{
				case down:
					texY += 0;
					break;
				case right:
					texY += 1;
					break;
				case left:
					texY += 2;
					break;
				case up:
					texY += 3;				
					break;
				}
				
				//Special case footstep animations
				//Set footstep animation for walking up ramps down-facing ramps
				if (this.isRampAscending() && this.getDir() == up)
				{
					if ((Math.abs(this.offset[0]) <= 16 && Math.abs(this.offset[0]) > 7)
						|| (Math.abs(this.offset[1]) <= 16 && Math.abs(this.offset[1]) > 7))
					{
						if (agent.getFootstep() == right)
							texX += 0;
						else
							texX += 2;
					}
					else if ((Math.abs(this.offset[0]) <= 32 && Math.abs(this.offset[0]) > 23)
					|| (Math.abs(this.offset[1]) <= 32 && Math.abs(this.offset[1]) > 23))
					{
						if (agent.getFootstep() == right)
							texX += 2;
						else
							texX += 0;
					}
					else
					{
						texX += 1;
					}
				}
				//Set footstep animation for jumping
				else if (agent.isJumping())
				{
					if (agent.getStance() == right)
						texX += 2;
					else
						texX += 0;
				}
				//Set footstep animation for walking down down-facing ramps
				else if (agent.isRampDescending() && agent.getDir() == down)
				{
					if ((Math.abs(this.offset[0]) <= 7 && Math.abs(this.offset[0]) > 0)
						|| (Math.abs(this.offset[1]) <= 7 && Math.abs(this.offset[1]) > 0))
					{
						if (agent.getFootstep() == right)
							texX += 2;
						else
							texX += 0;
					}
					else if ((Math.abs(this.offset[0]) <= 23 && Math.abs(this.offset[0]) > 16)
					|| (Math.abs(this.offset[1]) <= 23 && Math.abs(this.offset[1]) > 16))
					{
						if (agent.getFootstep() == right)
							texX += 0;
						else
							texX += 2;
					}
					else
					{
						texX += 1;
					}
				}
				//Set footstep animation for regular stepping
				else
				{
					if (agent.getDir() == left || agent.getDir() == right)
					{
						if (Math.abs(this.offset[0]) <= 16 && Math.abs(this.offset[0]) > 7)
							{
								if (agent.getFootstep() == right)
									texX += 2;
								else
									texX += 0;
							}
							else
							{
								texX += 1;
							}
					}
					else if (agent.getDir() == down)
					{
						if (Math.abs(this.offset[1]) <= 16 && Math.abs(this.offset[1]) > 7)
						{
							if (agent.getFootstep() == right)
								texX += 2;
							else
								texX += 0;
						}
						else
						{
							texX += 1;
						}
					}
					else
					{
						if (Math.abs(this.offset[1]) <= 7 && Math.abs(this.offset[1]) > 0)
						{
							if (agent.getFootstep() == right)
								texX += 2;
							else
								texX += 0;
						}
						else
						{
							texX += 1;
						}
					}
				}
				
				int xMin = pixelSize * ((terrainTextureSize - agent.TEXTURE_SIZE_X) / 2 + (int)(this.offset[0]));
				int xMax = xMin + pixelSize * (agent.TEXTURE_SIZE_X);
				int yMin = pixelSize * ((int)(this.offset[1]));
				int yMax = yMin + pixelSize * (agent.TEXTURE_SIZE_Y);
				
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
		else
		{
			//otherwise render nothing, this is a placeholder to simply occupy a space while another agent is making
			//a special case move to that space
			
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		
		if (obj.getClass() != this.getClass())
			return false;
		
		Position pos2 = ((Placeholder)(obj)).getPos();
		return (pos2.x == pos.x && pos2.x == pos.x && pos2.x == pos.x);
	}
	
	public Position getEffectivePos()
	{
		return agent.pos;
	}
}
