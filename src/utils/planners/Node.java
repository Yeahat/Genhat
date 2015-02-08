package utils.planners;

import world.Position;

public class Node implements Comparable<Object>{
	private final Position pos;
	private final String path;
	private final int h;
	
	public Node(Position pos, int distance, String path)
	{
		this.pos = pos;
		this.path = path;
		this.h = this.path.length() + distance;
	}

	@Override
	public int compareTo(Object arg0) {
		Integer i1 = new Integer(this.h);
		Integer i2 = new Integer(((Node)arg0).getH());
		return i1.compareTo(i2);
	}
	
	public Position getPos() {
		return pos;
	}

	public String getPath() {
		return path;
	}

	public int getH() {
		return h;
	}
}
