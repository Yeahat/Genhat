package entities;

import static entities.Agent.direction.down;
import static entities.Agent.direction.left;

import java.io.IOException;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Jump;
import actions.Say;
import actions.Step;
import actions.Turn;
import world.World;
import static world.World.controlState.*;

public class Innkeeper extends Agent
{
	//texture info
	String textureSheet;
	
	//actions
	Say say;
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
			currentAction = say;
			args.clear();
			args.add("Innesley");
			args.add("Hello there, Baldorf!");
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
	}
	
	@Override
	public void decideNextAction(World world)
	{
		if (currentAction == say && currentAction.isFinished())
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
