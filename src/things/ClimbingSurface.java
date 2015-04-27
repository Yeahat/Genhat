package things;

public abstract class ClimbingSurface extends Thing {

	protected connectionContext horizontalConnection;
	protected connectionContext verticalConnection;
	
	public ClimbingSurface()
	{
		climbingSurface = true;
	}
	
	public connectionContext getHorizontalConnection()
	{
		return horizontalConnection;
	}
	
	public connectionContext getVerticalConnection()
	{
		return verticalConnection;
	}
}
