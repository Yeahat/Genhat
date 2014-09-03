package things;

import java.util.Random;

public class LightSource extends Thing {

	float baseLightPower;
	float updateFrequency;
	int minRate;
	int updateCounter = 0;
	Random rand;
	
	public LightSource()
	{
		blocking = false;
		crossable = true;
		setLightSource(true);
		
		baseLightPower = .6f;
		updateFrequency = 5;
		minRate = 3;
		setLightPower(baseLightPower);
		
		rand = new Random();
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

	@Override
	public void update()
	{
		updateCounter ++;
		if (updateCounter >= minRate)
		{
			if (rand.nextFloat() < 1.0f/updateFrequency)
			{
				float change = (rand.nextFloat() - .5f) * .025f;
				setLightPower(baseLightPower + change);
				updateCounter = 0;
			}
		}
	}
	
}
