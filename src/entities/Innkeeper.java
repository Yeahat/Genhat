package entities;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Converse;
import actions.FollowPath;
import actions.Say;
import actions.Turn;
import actions.WalkToPoint;
import world.Position;
import world.World;

import static entities.Agent.Direction.*;
import static world.World.ControlState.*;
import static utils.planners.PathPlannerUtils.MovementClass.*;

public class Innkeeper extends Agent
{
	//testing
	String path = "RRRLLLLLRR";
	Position testPos1 = new Position(31, 36, 1);
	//Position testPos2 = new Position(29, 29, 1);
	Position testPos2 = new Position(37, 35, 4);
	//Position testPos2 = new Position(7, 19, 4);
	//Position testPos2 = new Position(14, 15, 4);
	
	//state
	int conversationNumber;
	
	//texture info
	String textureSheet;
	
	//actions
	Say say;
	Converse converse;
	Turn turn;
	FollowPath followPath;
	WalkToPoint walkToPoint;
	
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
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<String> texts = new ArrayList<String>();
				ArrayList<Integer> waitTimes = new ArrayList<Integer>();
				names.add("Woman");
				names.add("Baldorf");
				names.add("Woman");
				names.add("Baldorf");
				names.add("Innesley");
				texts.add("I haven't seen you around here before.");
				texts.add("The name's Baldorf.");
				texts.add("Innesly.  Nice to meet you, Baldorf!");
				texts.add("Likewise.  So, got any food or rooms?");
				texts.add("Not exactly... We don't have a kitchen yet and we're kinda going for an “open-air” feeling here..."
						+ "Okay so actually the inn's not finished yet, but we're getting there!");
				waitTimes.add(0);
				waitTimes.add(10);
				waitTimes.add(10);
				waitTimes.add(10);
				waitTimes.add(10);
				converse = new Converse(names, texts, waitTimes, agent);
				currentAction = converse;
				
				conversationNumber ++;
			}
			else if (conversationNumber == 1)
			{
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<String> texts = new ArrayList<String>();
				ArrayList<Integer> waitTimes = new ArrayList<Integer>();
				names.add("Baldorf");
				names.add("Innesley");
				names.add("Baldorf");
				names.add("Innesley");
				texts.add("What's new?");
				texts.add("I can walk wherever I want!");
				texts.add("Great! But why do you keep going back and forth between two specific spots?");
				texts.add("Because... that's where I want to go!");
				waitTimes.add(0);
				waitTimes.add(10);
				waitTimes.add(10);
				waitTimes.add(30);
				converse = new Converse(names, texts, waitTimes, agent);
				currentAction = converse;

				conversationNumber ++;
			}
			else
			{
				String name = "Innesley";
				String text = "I have big plans for a top floor!  I'd also like to be able to walk up stairs, and maybe even adjust my walking on-the-fly to"
						+ "avoid other moving people... Like you!  I also have an embarrassing habit of blocking people into tight spaces and not moving...";
				say = new Say(name, text, agent);
				currentAction = say;
			}
		}
		else
		{
			Position agentPos = agent.getPos();
			int xDiff = pos.x - agentPos.x;
			int yDiff = pos.y - agentPos.y;
			if (Math.abs(xDiff) > Math.abs(yDiff))
			{
				if (xDiff < 0)
					turn = new Turn(Right);
				else
					turn = new Turn(Left);
			}
			else
			{
				if (yDiff < 0)
					turn = new Turn(Up);
				else
					turn = new Turn(Down);
			}
			currentAction = turn;
		}
	}
	
	@Override
	protected void setActions()
	{
		super.setActions();
	}
	
	@Override
	public void initState()
	{
		super.initState();
		setDir(Down);
		setSpeed(2);
		setFootstep(Left);
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
					if (heldActionStack.isEmpty())
						setInterrupted(false);
				}
				else
				{
					currentAction = idle;
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
				beginInteraction(waitingInteractee, world);
				setInterruptRequested(false);
			}
		}
		else if (isInterrupted())
		{
			if (currentAction.isFinished())
			{
				currentAction = heldActionStack.pop();
				if (heldActionStack.isEmpty())
					setInterrupted(false);
			}
		}
		else
		{
			if (currentAction == idle)
			{
				if (pos.equals(testPos1))
				{
					walkToPoint = new WalkToPoint(testPos2, Stepping);
				}
				else
				{
					walkToPoint = new WalkToPoint(testPos1, Stepping);
				}
				
				currentAction = walkToPoint;
			}
			else if (currentAction.isFinished())
			{
				currentAction = idle;
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
