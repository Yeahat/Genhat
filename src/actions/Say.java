package actions;

import utils.display.DisplayText;
import world.GameState;
import world.Position;
import world.Map;
import entities.Agent;
import static entities.Agent.Direction.*;

public class Say implements Action 
{
	private boolean talkingToHero;
	private boolean initialized;
	private boolean finished;
	private String name;
	private String text;
	private boolean turnFirst;
	private Agent interactee;
	
	Turn turn;
	
	public Say(String name, String text, Agent interactee)
	{
		this.name = name;
		this.text = text;
		this.turnFirst = false;
		this.interactee = interactee;
		
		talkingToHero = false;
		initialized = false;
		finished = false;
	}
	
	public Say(String name, String text, Agent interactee, boolean turnFirst)
	{
		this.name = name;
		this.text = text;
		this.turnFirst = turnFirst;
		this.interactee = interactee;

		talkingToHero = false;
		initialized = false;
		finished = false;
	}
	
	@Override
	public void execute(Agent agent, Map world, GameState gameState)
	{
		if (!initialized)
		{
			if (interactee == gameState.getPlayer())
				talkingToHero = true;
			
			//turn to face hero
			if (turnFirst)
			{
				Position interacteePos = interactee.getPos();
				Position agentPos = agent.getPos();
				int xDiff = agentPos.x - interacteePos.x;
				int yDiff = agentPos.y - interacteePos.y;
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
				
				
				turn.execute(agent, world, gameState);
			}
			
			//fill in the world text box
			DisplayText box = new DisplayText(name);
			box.setText(text);
			world.setTextDisplay(box);
			world.setTextBoxActive(true);
			initialized = true;
			finished = false;
		}
		else
		{
			if (!world.isTextBoxActive())
			{
				finished = true;
				initialized = false;
			}
		}
		
	}

	@Override
	public boolean isFinished()
	{
		return finished;
	}

	@Override
	public boolean requestInterrupt() {
		return !talkingToHero;
	}

	@Override
	public boolean isInterruptable() {
		return !talkingToHero;
	}

}
