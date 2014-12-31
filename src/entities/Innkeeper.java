package entities;

import static entities.Agent.direction.down;
import static entities.Agent.direction.left;

import java.io.IOException;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Converse;
import actions.Jump;
import actions.Say;
import actions.Step;
import actions.Turn;
import world.World;
import static world.World.controlState.*;

public class Innkeeper extends Agent
{
	//state
	int conversationNumber;
	
	//texture info
	String textureSheet;
	
	//actions
	Say say;
	Converse converse;
	Turn turn;
	
	/**
	 * Constructor
	 */
	public Innkeeper()
	{
		super(true, true, false);
		textureSheet = "char1";
		setTexRow(0);
		setTexCol(1);
		
		loadTextures();
	}
	
	/**
	 * Constructor with choice of texture
	 * @param charSheet character sheet to use
	 * @param row texture row
	 * @param col texture column
	 */
	public Innkeeper(String charSheet, int row, int col)
	{
		super(true, true, false);
		textureSheet = charSheet;
		setTexRow(row);
		setTexCol(col);
		
		loadTextures();
	}
	
	@Override
	public void interact(Agent agent, World world)
	{
		if (agent == world.getPlayer())
		{
			world.setCs(talking);
			
			if (conversationNumber == 0)
			{
				currentAction = converse;
				args.clear();
				args.add("Woman");
				args.add("I haven't seen you around here before.");
				args.add("10");
				args.add("Baldorf");
				args.add("The name's Baldorf.");
				args.add("10");
				args.add("Woman");
				args.add("Innesly.  Nice to meet you, Baldorf!");
				args.add("10");
				args.add("Baldorf");
				args.add("Likewise.  So, got any food or rooms?");
				args.add("10");
				args.add("Innesley");
				args.add("Not exactly... We don't have a kitchen yet and we're kinda going for an “open-air” feeling here..."
						+ "Okay so actually the inn's not finished yet, but we're getting there!");
				
				conversationNumber ++;
			}
			else if (conversationNumber == 1)
			{
				currentAction = converse;
				args.clear();
				args.add("Baldorf");
				args.add("What's new?");
				args.add("10");
				args.add("Innesley");
				args.add("Nothing much.  The inn's not finished, so I can't offer you a room.  All we can do is "
						+ "have a conversation, but that's still something!");
				args.add("30");
				args.add("Baldorf");
				args.add("S'pose so...\n\n\n\n\n\nI'm still tired and hungry though!  Conditions are bad for us test characters...");
				args.add("10");
				args.add("Innesley");
				args.add("We don't even have a concept of time yet, so how can you be hungry or tired?");
				
				conversationNumber ++;
			}
			else
			{
				currentAction = say;
				args.clear();
				args.add("Innesley");
				args.add("I have big plans for a top floor!  Or maybe I'll learn to walk around on my own first!  There's so much to do.");
			}
		}
		else
		{
			currentAction = turn;
			args.clear();
			int[] agentPos = agent.getPos();
			int xDiff = pos[0] - agentPos[0];
			int yDiff = pos[1] - agentPos[1];
			if (Math.abs(xDiff) > Math.abs(yDiff))
			{
				if (xDiff < 0)
					args.add("right");
				else
					args.add("left");
			}
			else
			{
				if (yDiff < 0)
					args.add("up");
				else
					args.add("down");
			}
		}
	}
	
	@Override
	protected void setActions()
	{
		super.setActions();
		converse = new Converse();
		say = new Say();
		turn = new Turn();
	}
	
	@Override
	public void initState()
	{
		super.initState();
		setDir(down);
		setSpeed(2);
		setFootstep(left);
		setHeight(2);
		conversationNumber = 0;
	}
	
	@Override
	public void decideNextAction(World world)
	{
		if ((currentAction == say || currentAction == converse) && currentAction.isFinished())
		{
			world.setCs(walking);
			currentAction = idle;
			args.clear();
		}
	}

	@Override
	public void loadTextures()
	{
		String texturePath = "graphics/characters/" + textureSheet + ".png";
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(texturePath));
		} catch (IOException e) {e.printStackTrace();}
	}

}
