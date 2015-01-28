package entities;

import static entities.Agent.direction.down;
import static entities.Agent.direction.left;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Converse;
import actions.FollowPath;
import actions.Say;
import actions.Turn;
import world.Position;
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
	FollowPath followPath;
	
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
		if (currentAction.isInterruptable())
		{
			if (agent == world.getPlayer())
			{
				setInteractingWithHero(true);
				world.setCs(talking);
			}
			
			if (currentAction.requestInterrupt())
			{
				setInterrupted(true);
				//queue current action and commence with interaction
				heldActionStack.push(currentAction);
				heldActionArgsStack.push(new ArrayList<String>(args));
				beginInteraction(agent, world);
			}
			else
			{
				setInterruptRequested(true);
				waitingInteractee = agent;
			}
		}
		else
		{
			return;	//the agent cannot be interacted with due to their current action
		}
	}
	
	private void beginInteraction(Agent agent, World world)
	{
		if (agent == world.getPlayer())
		{
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
			Position agentPos = agent.getPos();
			int xDiff = pos.x - agentPos.x;
			int yDiff = pos.y - agentPos.y;
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
		followPath = new FollowPath(true, 0);
		followPath.setPath("lllrrrrrll");
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
		if (isInteractingWithHero())
		{
			if (isInterruptRequested())
			{
				if (currentAction.requestInterrupt())
				{
					setInterrupted(true);
					//queue current action and commence with interaction
					heldActionStack.push(currentAction);
					heldActionArgsStack.push(new ArrayList<String>(args));
					beginInteraction(waitingInteractee, world);
					setInterruptRequested(false);
				}
			}
			else if ((currentAction == say || currentAction == converse) && currentAction.isFinished())
			{
				world.setCs(walking);
				setInteractingWithHero(false);
				if (isInterrupted())
				{
					currentAction = heldActionStack.pop();
					args = heldActionArgsStack.pop();
					if (heldActionStack.isEmpty())
						setInterrupted(false);
				}
				else
				{
					currentAction = idle;
					args.clear();
				}
			}
		}
		else if (isInterruptRequested())
		{
			if (currentAction.requestInterrupt())
			{
				setInterrupted(true);
				//queue current action and commence with interaction
				heldActionStack.push(currentAction);
				heldActionArgsStack.push(new ArrayList<String>(args));
				beginInteraction(waitingInteractee, world);
				setInterruptRequested(false);
			}
		}
		else if (isInterrupted())
		{
			if (currentAction.isFinished())
			{
				currentAction = heldActionStack.pop();
				args = heldActionArgsStack.pop();
				if (heldActionStack.isEmpty())
					setInterrupted(false);
			}
		}
		else
		{
			if (currentAction == idle)
			{
				currentAction = followPath;
			}
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
