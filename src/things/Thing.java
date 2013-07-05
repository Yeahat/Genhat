package things;

public abstract class Thing {
	boolean blocking;
	boolean crossable;
	
	/**
	 * Getter for blocking
	 * @return true if blocking
	 */
	public boolean isBlocking()
	{
		return blocking;
	}
	
	/**
	 * Getter for crossable (i.e. an agent can walk over it even if there is no ground below)
	 * @return true if crossable
	 */
	public boolean isCrossable()
	{
		return crossable;
	}
}
