package entities;

import java.io.IOException;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Step;

import world.World;

public class Hero extends Agent {
	int row;
	int column;
	
	//Actions
	Step step;
	
	public Hero()
	{
		super();
	}
	
	@Override
	protected void setActions()
	{
		super.setActions();
		step = new Step();
	}
	
	@Override
	public void decideNextAction(World world) 
	{
		//Do nothing, this is a player-controlled character so actions are set by key presses
	}

	@Override
	public void loadTextures() 
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/characters/char1.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@Override
	public void renderAgent() 
	{
		
	}

}
