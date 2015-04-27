package things;

public abstract class Ramp extends Thing {

	protected connectionContext horizontalConnection;
	protected connectionContext verticalConnection;
	
	public Ramp()
	{
		ramp = true;
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
