package things;

import java.util.Random;

import world.Position;

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
	
	@Override
	public String save()
	{
		String data = new String("");
		data += "LightSource:\n";
		data += pos.x + "," + pos.y + "," + pos.z + "\n";
		data += updateCounter + "\n";
		return data;
	}
	
	public static LightSource load(String data)
	{
		//read in position
		Position pos = new Position();
		pos.x = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.y = Integer.parseInt(data.substring(0, data.indexOf(',')));
		data = data.substring(data.indexOf(',') + 1);
		pos.z = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);
		
		//read lit and flicker state data
		int updateCounter = Integer.parseInt(data.substring(0, data.indexOf('\n')));
		data = data.substring(data.indexOf('\n') + 1);

		//create thing and set any relevant data
		LightSource lightSource = new LightSource();
		lightSource.setPos(pos);
		lightSource.updateCounter = updateCounter;
		
		return lightSource;
	}
}
