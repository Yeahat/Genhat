package world;

import static world.Terrain.terrainType.*;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Terrain {
	public enum terrainType
	{
		grass, dirt, air, rock, thatch, glass, window, windowProtruding, woodPlank, woodPlankWeathered, woodSupport, plaster
	}
	
	boolean blocking;
	boolean transparent;
	boolean unblendedVertical;
	boolean unblendedHorizontal;
	terrainType type;
	terrainType top;
	int texRow;
	int texCol;
	int texRowTop;
	int texColTop;
	int vTexIndex;
	int hTexIndex;
	
	public Terrain(terrainType t)
	{
		setTerrainType(t);
	}
	
	public Terrain(terrainType aType, terrainType aTop)
	{
		setTerrainType(aType, aTop);
	}
	
	/**
	 * Getter for the terrain type
	 * @return the terrain type
	 */
	public terrainType getTerrainType()
	{
		return type;
	}
	
	/**
	 * Getter for the terrain type top
	 * @return the terrain type top
	 */
	public terrainType getTerrainTop()
	{
		return top;
	}
	
	/**
	 * Change the terrain type
	 * @param t the new terrain type
	 */
	public void setTerrainType(terrainType t)
	{
		type = t;
		top = t;
		loadTextures();
		updateAttributes();
	}
	
	/**
	 * Change the terrain type
	 * @param t the new terrain type
	 */
	public void setTerrainType(terrainType aType, terrainType aTop)
	{
		type = aType;
		top = aTop;
		loadTextures();
		updateAttributes();
	}
	
	public void bindVTexture(ArrayList<Texture> textures)
	{
		textures.get(vTexIndex).bind();
	}
	
	public void bindHTexture(ArrayList<Texture> textures)
	{
		textures.get(hTexIndex).bind();
	}
	
	private void loadTextures()
	{
		if (type == grass || type == dirt || type == rock || type == plaster || type == woodPlank || type == woodPlankWeathered)
			vTexIndex = 0;
		else if (type == woodSupport)
			vTexIndex = 1;
		else if (type == air || type == glass || type == windowProtruding || type == window)
			vTexIndex = 2;
		
		if (top == grass || top == dirt || top == rock || top == thatch || top == woodPlank || top == air)
			hTexIndex = 0;
	}
	
	public int getTexRow()
	{
		return texRow * 5;
	}
	
	public int getTexCol()
	{
		if (transparent)
			return texCol * 4;
		else
			return texCol * 8;
	}
	
	public int getTexRowTop()
	{
		return texRowTop * 5;
	}
	
	public int getTexColTop()
	{
		return texColTop * 4;
	}
	
	/**
	 * Getter for blocking
	 * @return true if blocking
	 */
	public boolean isBlocking()
	{
		return blocking;
	}
	
	/**
	 * Getter for transparent
	 * @return true if transparent
	 */
	public boolean isTransparent()
	{
		return transparent;
	}
	
	/**
	 * Getter for unblendedVertical
	 * @return true if unblendedVertical
	 */
	public boolean isUnblendedVertical()
	{
		return unblendedVertical;
	}
	
	/**
	 * Getter for unblendedHorizontal
	 * @return true if unblendedHorizontal
	 */
	public boolean isUnblendedHorizontal()
	{
		return unblendedHorizontal;
	}
	
	/**
	 * Updates the internal attributes of the terrain type.
	 * This must be called any time the terrain type is set.
	 */
	private void updateAttributes()
	{
		setBlocking();
		setTransparent();
		setUnblended();
		setTexPos();
	}
	
	/**
	 * Determines whether or not a terrain type is blocking and updates accordingly
	 */
	private void setBlocking()
	{
		//add the names of any new non-blocking terrain types here
		if (type == air)
			blocking = false;
		else
			blocking = true;
	}
	
	/**
	 * Determines whether or not a terrain type is transparent and updates accordingly
	 */
	private void setTransparent()
	{
		//add the names of any new transparent terrain types here
		if (type == air || type == glass || type == window || type == windowProtruding)
			transparent = true;
		else
			transparent = false;
	}
	
	/**
	 * Determines whether or not a terrain type should be blended with surrounding terrain types and updates accordingly
	 */
	private void setUnblended()
	{
		//add the names of any new unblended vertical terrain types here
		if (type == glass || type == window || type == windowProtruding || type == woodSupport)
			unblendedVertical = true;
		else
			unblendedVertical = false;
		
		//add the names of any new unblended horizontal terrain types here
		if (top == glass || top == woodPlank)
			unblendedHorizontal = true;
		else
			unblendedHorizontal = false;
	}
	
	/**
	 * Setup the texture row and column based on the terrain type
	 */
	private void setTexPos()
	{
		//Vertical textures
		switch (type)
		{
		case grass: 
			texRow = 0; texCol = 0; break;
		case dirt: 
			texRow = 0; texCol = 1; break;
		case rock:
			texRow = 1; texCol = 0; break;
		case plaster:
			texRow = 1; texCol = 1; break;
		case woodPlank:
			texRow = 2; texCol = 0; break;
		case woodPlankWeathered:
			texRow = 2; texCol = 1; break;
			
		case woodSupport:
			texRow = 0; texCol = 0; break;
			
		case glass:
			texRow = 0; texCol = 0; break;
		case windowProtruding: 
			texRow = 0; texCol = 1; break;
		case window: 
			texRow = 0; texCol = 2; break;

		//Unset
		default:
			texRow = 2; texCol = 3; break;
		}
		
		//Horizontal textures
		switch (top)
		{
		case grass:
			texRowTop = 0; texColTop = 0; break;
		case dirt: 
			texRowTop = 0; texColTop = 1; break;
		case rock:
			texRowTop = 0; texColTop = 2; break;
		case thatch:
			texRowTop = 0; texColTop = 3; break;
		case woodPlank:
			texRowTop = 1; texColTop = 0; break;
		
		//Unset (blank)
		default:
			texRowTop = 2; texColTop = 3; break;
		}
	}
	
	public static void LoadVTextures(ArrayList<Texture> textures)
	{
		Texture v0 = null;
		Texture v1 = null;
		Texture v2 = null;
		
		try {
			v0 = (TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/terrain/VTerrain1.png")));
		} catch (IOException e) {e.printStackTrace();}
		try {
			v1 = (TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/terrain/VTerrain2.png")));
		} catch (IOException e) {e.printStackTrace();}
		try {
			v2 = (TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/terrain/VTransparentTerrain1.png")));
		} catch (IOException e) {e.printStackTrace();}
		
		textures.add(v0);
		textures.add(v1);
		textures.add(v2);
	}
	
	public static void LoadHTextures(ArrayList<Texture> textures)
	{
		Texture h0 = null;
		
		try {
			h0 = (TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/terrain/HTerrain.png")));
		} catch (IOException e) {e.printStackTrace();}
		
		textures.add(h0);
	}
}
