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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return x == other.x && y == other.y && z == other.z;
	}
	
	@Override
	public String toString()
	{
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
	
	public static Position posFromString(String str)
	{
		String[] split = str.split("[\\( ,\\)]+"); //TODO: Not sure why but this includes an empty string as the first element, so the x, y, and z are offset by 1
		return new Position(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
	}
}
