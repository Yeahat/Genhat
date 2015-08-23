package world;

import java.util.ArrayList;

import things.Table;
import world.Map.TimeOfDay;
import entities.AgentLoader;
import entities.Hero;
import entities.Agent.Direction;
import static world.Map.TimeOfDay.*;

public class GameState
{
	private Hero player;
	private TimeOfDay timeOfDay;
	private String currentMap;
	private int timeCounter;
	private final int DAY_INTERVAL = 9600;	//the number of frames before the time of day changes
	private final int NIGHT_INTERVAL = DAY_INTERVAL*2;
	private final int SUNRISE_INTERVAL = DAY_INTERVAL/2;
	private int timeCheck;
	
	public GameState()
	{
		this.timeCounter = 0;
		this.timeOfDay = Midday;
		this.timeCheck = DAY_INTERVAL;
	}
	
	/**
	 * Update game information, e.g. the passage of time
	 */
	public void update()
	{
		timeCounter ++;
		
		if (timeCounter > timeCheck)
			cycleTimeOfDay();
	}
	
	/**
	 * Cycle the time of day forward and set the new timeCheck as needed
	 */
	public void cycleTimeOfDay()
	{
		//advance the time of day and set the new timeCheck
		switch (timeOfDay)
		{
		case Morning:
			timeOfDay = Midday;
		break;
		case Midday:
			timeOfDay = Afternoon;
		break;
		case Afternoon:
			timeOfDay = Sunset;
			timeCheck = SUNRISE_INTERVAL;
		break;
		case Sunset:
			timeOfDay = Night;
			timeCheck = NIGHT_INTERVAL;
		break;
		case Night:
			timeOfDay = Sunrise;
			timeCheck = SUNRISE_INTERVAL;
		break;
		case Sunrise:
			timeOfDay = Morning;
			timeCheck = DAY_INTERVAL;
		break;
		}
		timeCounter = 0;
	}
	
	public ArrayList<String> save()
	{
		ArrayList<String> data = new ArrayList<String>();
		
		data.add(currentMap + "," + timeOfDay.toString() + "," + timeCounter + "\n");
		data.add(player.save());
		
		return data;
	}
	
	public static GameState load(String data)
	{
		GameState gameState = new GameState();
		
		//read in map and time info
		gameState.currentMap = data.substring(0, data.indexOf(','));
		data = data.substring(data.indexOf(',') + 1);
		gameState.setTimeOfDay(TimeOfDay.valueOf(data.substring(0, data.indexOf(','))));
		data = data.substring(data.indexOf(',') + 1);
		gameState.setTimeCounter(Integer.parseInt(data.substring(0, data.indexOf('\n'))));
		data = data.substring(data.indexOf('\n') + 1);
		gameState.setPlayer((Hero)AgentLoader.loadAgent(data));
		
		return gameState;
	}
	
	public Hero getPlayer() {
		return player;
	}
	public void setPlayer(Hero player) {
		this.player = player;
	}
	public TimeOfDay getTimeOfDay() {
		return timeOfDay;
	}
	public void setTimeOfDay(TimeOfDay timeOfDay) {
		this.timeOfDay = timeOfDay;
		switch (timeOfDay)
		{
		case Morning:
			timeCheck = DAY_INTERVAL;
		break;
		case Midday:
			timeCheck = DAY_INTERVAL;
		break;
		case Afternoon:
			timeCheck = DAY_INTERVAL;
		break;
		case Sunset:
			timeCheck = SUNRISE_INTERVAL;
		break;
		case Night:
			timeCheck = NIGHT_INTERVAL;
		break;
		case Sunrise:
			timeCheck = SUNRISE_INTERVAL;
		break;
		}
		timeCounter = 0;
	}

	public int getTimeCounter() {
		return timeCounter;
	}

	public void setTimeCounter(int timeCounter) {
		this.timeCounter = timeCounter;
	}

	public String getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(String currentMap) {
		this.currentMap = currentMap;
	}
}
