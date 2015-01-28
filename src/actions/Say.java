package actions;

import java.util.ArrayList;

import utils.DisplayText;
import world.Position;
import world.World;
import entities.Agent;

public class Say implements Action 
{
	boolean started = false;
	boolean finished = false;
	boolean talkingToHero = false;
	
	Turn turn = new Turn();
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		if (!started)
		{
			if (args.size() < 2)
			{
				System.out.println("Invalid arguments to action Say.");
				System.out.println("Say must take 2 required argument denoting the speaker's name and the text, and an optional argment for whether the speaker should face the hero first as either: {true, false}");
				return;
			}
			
			if (agent == world.getPlayer())
				talkingToHero = true;
			else
				talkingToHero = false;
			
			String name = args.get(0);
			String text = args.get(1);
			boolean turnFirst = true;
			if (args.size() > 2)
			{
				if (args.get(2).equals("false"))
					turnFirst = false;
				else
					turnFirst = true;
			}
			
			//turn to face hero
			if (turnFirst)
			{
				ArrayList<String> turnArgs = new ArrayList<String>();
				Position heroPos = world.getPlayer().getPos();
				Position agentPos = agent.getPos();
				int xDiff = agentPos.x - heroPos.x;
				int yDiff = agentPos.y - heroPos.y;
				if (Math.abs(xDiff) > Math.abs(yDiff))
				{
					if (xDiff < 0)
						turnArgs.add("right");
					else
						turnArgs.add("left");
				}
				else
				{
					if (yDiff < 0)
						turnArgs.add("up");
					else
						turnArgs.add("down");
				}
				
				turn.execute(agent, world, turnArgs);
			}
			
			//fill in the world text box
			DisplayText box = new DisplayText(name);
			box.setText(text);
			world.setTextDisplay(box);
			world.setTextBoxActive(true);
			started = true;
			finished = false;
		}
		else
		{
			if (!world.isTextBoxActive())
			{
				finished = true;
				started = false;
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
