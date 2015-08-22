package entities;

import entities.Agent.Direction;
import world.Position;

public class CommonData
{
	Position pos;
	Direction dir;
	boolean onRamp;
	String remainingData;	//any data left to load, generally handled by a subclass of Agent
	
	public CommonData()
	{
		pos = new Position();
	}
}
