package world;

public class Grid<E>
{
	private int width;
	private int depth;
	private int height;
	
	private Object[] grid;
	
	/**
	 * Constructor for a new 3-dimensional grid
	 * @param width grid width (x)
	 * @param depth grid depth (y)
	 * @param height grid height (z)
	 */
	public Grid(int width, int depth, int height)
	{
		this.setWidth(width);
		this.setDepth(depth);
		this.setHeight(height);
		grid = new Object[this.getWidth()*this.getDepth()*this.getHeight()];
	}
	
	/**
	 * Access an element of the grid
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return the element at position (x, y, z)
	 */
	@SuppressWarnings("unchecked")
	public E get(int x, int y, int z)
	{
		return (E)(grid[x*getDepth()*getHeight() + y*getHeight() + z]);
	}
	
	/**
	 * Set an element in the grid
	 * @param x x grid coordinate
	 * @param y y grid coordinate
	 * @param z z grid coordinate
	 * @param element the element to set at position (x, y, z)
	 */
	public void set(int x, int y, int z, E element)
	{
		grid[x*getDepth()*getHeight() + y*getHeight() + z] = element;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
