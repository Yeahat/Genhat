package utils.planners;

import world.Position;

public class Distance {
	
	/**
	 * Calculate the Manhattan distance between two points projected onto a 2d (x,y) grid
	 * @param pos1 first position
	 * @param pos2 second position
	 * @return Manhattan distance between the points
	 */
	public static int distance2D(Position pos1, Position pos2)
	{
		return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y);
	}
	
	/**
	 * Calculate the Manhattan distance between two points in a 3D grid
	 * @param pos1 first position
	 * @param pos2 second position
	 * @return Manhattan distance between the points
	 */
	public static int distance3D(Position pos1, Position pos2)
	{
		return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y) + Math.abs(pos1.z - pos2.z);
	}
	
	/**
	 * Calculate the Euclidean distance between two points projected onto a 2d (x,y) grid
	 * @param pos1 first position
	 * @param pos2 second position
	 * @return Euclidean distance between the points
	 */
	public static double euclideanDistance2D(Position pos1, Position pos2)
	{
		return Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2));
	}
	
	/**
	 * Calculate the Euclidean distance between two points in a 3D grid
	 * @param pos1 first position
	 * @param pos2 second position
	 * @return Euclidean distance between the points
	 */
	public static double euclideanDistance3D(Position pos1, Position pos2)
	{
		return Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2) + Math.pow(pos1.z - pos2.z, 2));
	}
}
