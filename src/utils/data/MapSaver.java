package utils.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Scanner;

import world.GameState;
import world.Map;

public class MapSaver
{
	public static void saveMap(Map world, GameState gameState)
	{
		String filePath = "maps/" + world.getMapName() + ".txt";
		try {
			PrintWriter out = new PrintWriter(filePath);
			ArrayList<String> data = world.save(gameState);
			for (int i = 0; i < data.size(); i ++)
			{
				out.print(data.get(i));
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Map loadMap(String mapName)
	{
		String filePath = "maps/" + mapName + ".txt";
		try {
			long start = System.nanoTime();
			String data = new Scanner(new File(filePath)).useDelimiter("\\A").next();
			long end = System.nanoTime();
			System.out.println("Reading finished in time: " + (end - start));
			return Map.load(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
