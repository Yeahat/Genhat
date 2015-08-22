package entities;

public class AgentLoader {
	public static Agent loadAgent(String data)
	{
		String agentType = data.substring(0, data.indexOf(':'));
		data = data.substring(data.indexOf('\n') + 1);
		switch (agentType)
		{
		case "Hero":				return Hero.load(data);
		case "Innkeeper": 			return Innkeeper.load(data);
		case "Placeholder":			return Placeholder.load(data);
		case "Wanderer":			return Wanderer.load(data);
		default:					return null;	
		}
	}
}
