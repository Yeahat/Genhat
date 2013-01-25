package entity;

public abstract class Agent {
	//State information
	int x;
	int y;
	
	public abstract void decideNextAction();
	
	public abstract void executeAction();
}
