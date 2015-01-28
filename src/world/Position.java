package world;

public class Position
{
	public int x = 0;
	public int y = 0;
	public int z = 0;
	
	public Position(){}
	
	public Position(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//copy constructor
	public Position(Position pos)
	{
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}
}
