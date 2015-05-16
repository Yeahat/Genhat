package things;

public abstract class ClimbingSurface extends Thing {

	protected ConnectionContext horizontalConnection;
	protected ConnectionContext verticalConnection;
	
	public ClimbingSurface()
	{
		climbingSurface = true;
	}
	
	public ConnectionContext getHorizontalConnection()
	{
		return horizontalConnection;
	}
	
	public ConnectionContext getVerticalConnection()
	{
		return verticalConnection;
	}
}
