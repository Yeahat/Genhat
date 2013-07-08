package utils;

public class Distance {
	
	/**
	 * Calculate the Manhattan distance between two points projected onto a 2d (x,y) grid
	 * @param pos1 first position (must be at least length 2 where (pos1[0], pos1[1]) = (x, y)
	 * @param pos2 second position (must be at least length 2 where (pos2[0], pos2[1]) = (x, y)
	 * @return Manhattan distance between the points, -1 on error
	 */
	public static int distance2D(int[] pos1, int[] pos2)
	{
		if (pos1.length < 2 || pos2.length < 2)
		{
			System.out.println("Invalid positions for distance calculation");
			return -1;
		}
		
		return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
	}
}
