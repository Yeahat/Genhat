package tools;

public class MapEditor {
	private MapEditorLauncher mapEditorLauncher;
	
	public MapEditor()
	{
		mapEditorLauncher = new MapEditorLauncher(this);
		mapEditorLauncher.setVisible(true);
	}
	
	protected void loadMap(String mapName)
	{
		System.out.println("Loading " + mapName);
	}
	
	protected void createMap(String mapName, int x, int y, int z)
	{
		System.out.println("Creating " + mapName);
	}
	
	public static void main(String[] args)
	{
		MapEditor mapEditor = new MapEditor();
	}
}
