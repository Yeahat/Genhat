package things;

public class LightSource extends Thing {

	public LightSource()
	{
		blocking = false;
		crossable = true;
		setLightSource(true);
	}
	
	@Override
	public void loadTextures() 
	{
		//no texture, do nothing
	}

	@Override
	public void renderThing(int pixelSize, int terrainTextureSize) 
	{
		//nothing to render
	}

}
