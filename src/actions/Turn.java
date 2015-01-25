package actions;

import static entities.Agent.direction.down;
import static entities.Agent.direction.left;
import static entities.Agent.direction.right;
import static entities.Agent.direction.up;

import java.util.ArrayList;

import world.World;
import entities.Agent;

public class Turn implements Action {

	@Override
	public void execute(Agent agent, World world, ArrayList<String> args) {
		//invalid arguments, do nothing
		if (args.size() < 1)
		{
			System.out.println("Invalid arguments to action Turn.");
			System.out.println("Turn must take 1 argument denoting direction, as either: {up, down, left, right}");
			return;
		}
			
		String arg1 = args.get(0);
		if (arg1.equals("up"))
		{
			if (!agent.isOnRamp())
				agent.setDir(up);
		}
		else if (arg1.equals("down"))
		{
			if (!agent.isOnRamp())
				agent.setDir(down);
		}
		else if (arg1.equals("left"))
		{
			agent.setDir(left);
		}
		else if (arg1.equals("right"))
		{
			agent.setDir(right);
		}
		else
		{
			System.out.println("Invalid arguments to action Turn.");
			System.out.println("Turn must take 1 argument denoting direction, as either: {up, down, left, right}");
			return; //invalid arguments, do nothing
		}
	}

	@Override
	public boolean isFinished() {
		return true;	//one frame action, so always return true for finished
	}
	
	@Override
	public boolean requestInterrupt() {
		return true;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
}
