package actions;

import static entities.Agent.Direction.Down;
import static entities.Agent.Direction.Up;
import utils.planners.PathPlannerUtils;
import world.GameState;
import world.Map;
import entities.Agent;
import entities.Agent.Direction;

public class Turn implements Action {
	private final Direction dir;
	private boolean finished;
	
	public Turn(Direction dir)
	{
		this.dir = dir;
		this.finished = false;
	}
	
	@Override
	public void execute(Agent agent, Map world, GameState gameState) {
		
		if (agent.getDir() != dir && !PathPlannerUtils.isOnClimbingSurface(world, agent.getPos()))
		{
			if (dir == Up || dir == Down)
			{
				if (!PathPlannerUtils.isOnRampHorizontal(world, agent.getPos()))
					agent.setDir(dir);
			}
			else
				agent.setDir(dir);
		}
		finished = true;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
	
	@Override
	public boolean requestInterrupt() {
		return true;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
	
	/* Actions are not currently saved, keeping this here in case they ever are...
	@Override
	public String save()
	{
		String data = new String("Turn:\n");
		data += dir.toString() + "," + finished + "\n";
		data += "~Turn\n";
		return data;
	}
	*/
	
	public static Turn load(String data)
	{
		if (data.equals("null\n"))
			return null;

		Turn turn = new Turn(Direction.valueOf(data.substring(0, data.indexOf(','))));
		data = data.substring(data.indexOf(',') + 1);
		turn.finished = Boolean.parseBoolean(data.substring(0, data.indexOf('\n')));
		
		return turn;
	}
}
