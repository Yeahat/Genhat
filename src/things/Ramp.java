package things;

public abstract class Ramp extends Thing {

	protected ConnectionContext horizontalConnection;
	protected ConnectionContext verticalConnection;
	
	public Ramp()
	{
		ramp = true;
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
