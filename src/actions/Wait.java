package actions;

import world.GameState;
import world.Map;
import entities.Agent;

public class Wait implements Action
{
	private int frames;
	
	public Wait(int frames)
	{
		this.frames = frames;
	}
	
	@Override
	public void execute(Agent agent, Map world, GameState gameState)
	{
		if (frames > 0)
			frames --;
	}

	@Override
	public boolean isFinished()
	{
		return (frames == 0);
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
		String data = new String("Wait:\n");
		data += frames + "\n";
		data += "~Wait\n";
		return data;
	}
	*/
	
	public static Wait load(String data)
	{
		if (data.equals("null\n"))
			return null;

		return new Wait(Integer.parseInt(data.substring(0, data.indexOf('\n'))));
	}
}
