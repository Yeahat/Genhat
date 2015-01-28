package utils;

import world.Position;

public class Distance {
	
	/**
	 * Calculate the Manhattan distance between two points projected onto a 2d (x,y) grid
	 * @param pos1 first position
	 * @param pos2 second position
	 * @return Manhattan distance between the points, -1 on error
	 */
	public static int distance2D(Position pos1, Position pos2)
	{
		return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y);
	}
}
