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

public class GameSaver
{
	public static void saveMap(Map world, GameState gameState, String saveName)
	{
		String filePath = "data/" + saveName + "/" + world.getMapName() + ".txt";
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
	
	public static Map loadMap(String mapName, String saveName)
	{
		String filePath = "data/" + saveName + "/" + mapName + ".txt";
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
	
	public static void saveGameState(GameState gameState, String saveName)
	{
		String filePath = "data/" + saveName + "/gameState.txt";
		try {
			PrintWriter out = new PrintWriter(filePath);
			ArrayList<String> data = gameState.save();
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
	
	public static GameState loadGameState(String saveName)
	{
		String filePath = "data/" + saveName + "/gameState.txt";
		try {
			String data = new Scanner(new File(filePath)).useDelimiter("\\A").next();
			return GameState.load(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
