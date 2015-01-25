package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;

public class Converse implements Action
{	
	ArrayList<String> sayArgs = new ArrayList<String>();
	ArrayList<String> waitTimes = new ArrayList<String>();
	Say say = new Say();
	Wait wait = new Wait();
	boolean talkingToHero = false;
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{
		//start a new conversation
		if (sayArgs.isEmpty())
		{
			if (args.size() < 2 || (args.size() - 2) % 3 != 0)
			{
				System.out.println("Invalid arguments to action Converse.");
				System.out.println("Converse must take one or more triples of argments in the form {{name, text, waitTime}, {name, text, waitTime}, ...}");
				System.out.println("Note that the final wait time does not need to be included, as it will not be used.");
				return;
			}
			
			if (agent == world.getPlayer())
				talkingToHero = true;
			else
				talkingToHero = false;
			
			for (int i = 0; i < args.size(); i += 3)
			{
				sayArgs.add(args.get(i));
				sayArgs.add(args.get(i + 1));
				if (i + 3 < args.size())
					waitTimes.add(args.get(i + 2));
			}
		}
		
		//run say, if say is finished, advance to the next text box
		if (wait.isFinished())
		{
			say.execute(agent, world, sayArgs);
			if (say.isFinished())
			{
				//pop off the front two arguments (the already displayed name/text pair)
				sayArgs.remove(0);
				sayArgs.remove(0);
				
				//check if we're done
				if (sayArgs.size() == 0)
				{
					waitTimes.clear();
					return;
				}
				
				wait.execute(agent, world, waitTimes);
			}
		}
		else
		{
			wait.execute(agent, world, waitTimes);
			if (wait.isFinished())
			{
				//pop off the front wait time
				waitTimes.remove(0);
			}
		}
	}

	@Override
	public boolean isFinished()
	{
		return sayArgs.isEmpty();
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
