package entities;

import static entities.Agent.Direction.Left;
import static entities.Agent.Direction.Right;

import org.lwjgl.opengl.GL11;

import world.GameState;
import world.Position;
import world.Map;

public class Placeholder extends Agent {

	Agent agent; //the agent for which the placeholder is acting
	
	public Placeholder(Position pos)
	{
		this.pos = pos;
		this.agent = null;
	}
	
	public Placeholder(Agent agent, Position pos)
	{
		this.pos = pos;
		this.agent = agent;
		this.setAssociated(true);
		
		if (agent.isRenderOnPlaceholder() && !agent.isTransparent())
			this.setTransparent(false);
		else
			this.setTransparent(true);
	}
	
	@Override
	public void decideNextAction(Map world, GameState gameState) 
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
			if (agent.pos.y > this.pos.y || agent.pos.z > this.pos.z)
			{
				this.offset[1] += 16;
			}
			
			if (agent.pos.z < this.pos.z)
			{
				this.offset[1] -= 16;
			}
			//render the agent linked to this placeholder
			GL11.glPushMatrix();
			
				agent.texture.bind();
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
				float tConvX = ((float)agent.TEXTURE_SIZE_X)/((float)agent.TEXTURE_SHEET_WIDTH);
				float tConvY = ((float)agent.TEXTURE_SIZE_Y)/((float)agent.TEXTURE_SHEET_HEIGHT);
				
				int texX = agent.getTexCol() * 3;
				int texY = agent.getTexRow();
				if (agent.isClimbing())
				{
					texY += 4;
				}
				else
				{
					switch (agent.getDir())
					{
					case Down:	texY += 0;	break;
					case Right:	texY += 1;	break;
					case Left:	texY += 2;	break;
					case Up:	texY += 3;	break;
					}
				}
				
				//Set footstep animation for jumping
				if (agent.isJumping())
				{
					if (agent.getStance() == Right)
						texX += 2;
					else
						texX += 0;
				}
				//Set footstep animation for climbing
				else if (this.isClimbing())
				{
					if (Math.abs(offset[0]) <= 16 && Math.abs(offset[0]) > 7)
					{
						if (getFootstep() == Right)
							texX += 2;
						else
							texX += 0;
					}
					else if (Math.abs(offset[1]) <= 16 && Math.abs(offset[1]) > 7)
					{
						if (getFootstep() == Right)
							texX += 2;
						else
							texX += 0;
					}
					else
					{
						texX += 1;
					}
				}
				//Set footstep animation for regular stepping
				else
				{
					if (agent.getDir() == Left || agent.getDir() == Right)
					{
						if (Math.abs(agent.offset[0]) <= 16 && Math.abs(agent.offset[0]) > 7)
						{
							if (agent.getFootstep() == Right)
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
						if (Math.abs(agent.offset[1]) <= 16 && Math.abs(agent.offset[1]) > 7)
						{
							if (agent.getFootstep() == Right)
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
	
	@Override
	public String save()
	{
		String data = new String("");
		data += "Placeholder:\n";
		data += this.saveCommon();
		return data;
	}
	
	public static Placeholder load(String data)
	{
		//read in common data
		CommonData commonData = Agent.loadCommon(data);
		
		//create agent and set any relevant data
		Placeholder placeholder = new Placeholder(commonData.pos);
		
		return placeholder;
	}
}
