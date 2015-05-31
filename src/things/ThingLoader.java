package things;

public class ThingLoader {
	public static Thing loadThing(String data)
	{
		String thingType = data.substring(0, data.indexOf(':'));
		data = data.substring(data.indexOf('\n') + 1);
		switch (thingType)
		{
		case "Bar":					return Bar.load(data);
		case "Beam": 				return Beam.load(data);
		case "Candle":				return Candle.load(data);
		case "Chair":				return Chair.load(data);
		case "Fireplace":			return Fireplace.load(data);
		case "FireplaceChimney":	return FireplaceChimney.load(data);
		case "FireplaceSide":		return FireplaceSide.load(data);
		case "Firewood":			return Firewood.load(data);
		case "Ladder":				return Ladder.load(data);
		case "LightSource":			return LightSource.load(data);
		case "Rope":				return Rope.load(data);
		case "Stairs":				return Stairs.load(data);
		case "StairsBottom":		return StairsBottom.load(data);
		case "Table":				return Table.load(data);
		case "WallCandle":			return WallCandle.load(data);
		default:					return null;	
		}
	}
}
