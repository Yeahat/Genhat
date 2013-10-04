package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;

public class StepCamera implements Action {

	private int dst = 0;
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args) {
		String arg1 = args.get(0);
		boolean steppingUpZ = false;
		boolean steppingDownZ = false;
		if (args.size() > 1)
		{
			if (args.get(1).equals("up"))
			{
				steppingUpZ = true;
				steppingDownZ = false;
			}
			else if (args.get(1).equals("down"))
			{
				steppingUpZ = false;
				steppingDownZ = true;
			}
		}
		else
		{
			steppingUpZ = false;
			steppingDownZ = false;
		}
		
		int vInc = 0;
		int hInc = 0;
		
		if (arg1.equals("up"))
		{
			vInc = (int)(agent.getSpeed() * 16.0f / 32.0f);
		}
		else if (arg1.equals("down"))
		{
			vInc = (int)(-agent.getSpeed() * 16.0f / 32.0f);
		}
		else if (arg1.equals("left"))
		{
			hInc = (int)(-agent.getSpeed() * 16.0f / 32.0f);
		}
		else if (arg1.equals("right"))
		{
			hInc = (int)(agent.getSpeed() * 16.0f / 32.0f);
		}
		else if (arg1.equals("none"))
		{
			//make no vertical or horizontal change
		}
		else
		{
			System.out.println("Invalid arguments to action StepCamera.");
			System.out.println("StepCamera must take 1 argument denoting direction, as either: {up, down, left, right}, " +
					"or {none} if there is no change in the x or y, and 1 argument denoting changes in z as either: {up, down}");
			return; //invalid arguments, do nothing
		}
		
		world.incrementDisplayX(hInc);
		world.incrementDisplayY(vInc);
		if (steppingUpZ)
			world.incrementDisplayZ((int)(agent.getSpeed() * 16.0f / 32.0f));
		else if (steppingDownZ)
			world.incrementDisplayZ((int)(-agent.getSpeed() * 16.0f / 32.0f));
	}

	@Override
	public boolean isFinished() {
			return true;
	}

}
