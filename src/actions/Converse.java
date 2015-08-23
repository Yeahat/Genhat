package actions;

import java.util.ArrayList;

import world.GameState;
import world.Map;
import entities.Agent;

public class Converse implements Action
{	
	private final ArrayList<String> names;
	private final ArrayList<String> texts;
	private final ArrayList<Integer> waitTimes;
	private final Agent interactee;
	private boolean talkingToHero;
	private boolean initialized;
	private Say say;
	private Wait wait;
	
	public Converse(ArrayList<String> names, ArrayList<String> texts, ArrayList<Integer> waitTimes, Agent interactee)
	{
		this.names = names;
		this.texts = texts;
		this.waitTimes = waitTimes;
		this.interactee = interactee;
		
		talkingToHero = false;
		initialized = false;
	}
	
	@Override
	public void execute(Agent agent, Map world, GameState gameState)
	{
		if (texts.isEmpty() && say.isFinished())
			return;
		
		//start a new conversation
		if (!initialized)
		{
			if (interactee == gameState.getPlayer())
				talkingToHero = true;
			
			wait = new Wait(waitTimes.get(0));
			waitTimes.remove(0);
			
			initialized = true;
		}
		
		//run say, if say is finished, advance to the next text box
		if (wait.isFinished())
		{
			if (names.size() > waitTimes.size())
			{
				//prepare a new Say action
				say = new Say(names.get(0), texts.get(0), interactee, true);
				names.remove(0);
				texts.remove(0);
			}
			say.execute(agent, world, gameState);
			if (say.isFinished())
			{
				//set up the next wait time, if there are any left
				if (waitTimes.size() != 0)
				{
					wait = new Wait(waitTimes.get(0));
					waitTimes.remove(0);
				}
			}
		}
		else
		{
			wait.execute(agent, world, gameState);
		}
	}

	@Override
	public boolean isFinished()
	{
		return texts.isEmpty() && say.isFinished();
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
