package main;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import world.Terrain;
import world.World;

import static world.Terrain.terrainType.*;

public class GameMain {
	
	World world;
	
	/**
	 * Main game loop, this is where everything happens
	 */
	public void gameLoop()
	{		
		//Initialization
		initGL();
		initWorld();
		loadTextures();
		
		//Main game loop
		while(!Display.isCloseRequested())	//exits when window is closed
		{
			//update everything
			
			//render everything
			renderGL();
			
			//get user input
			pollKeyboardInput();
			
			//update the screen
			Display.update();
			Display.sync(30);
		}
		
		//Exit
		Display.destroy();
	}
	
	/**
	 * Poll for input on the keyboard
	 */
	public void pollKeyboardInput()
	{
		//Check for key state changes
		while (Keyboard.next())
		{
			if (Keyboard.getEventKeyState())
			{
				if (Keyboard.getEventKey() == Keyboard.KEY_Q)
				{
					world.rotateCC();
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_E)
				{
					world.rotateC();
				}
			}
		}
		
		//Check for keys being held down
		if (Keyboard.isKeyDown(Keyboard.KEY_Z))
		{
			
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_X))
		{
			
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			world.IncrementDisplayY(1);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			world.IncrementDisplayY(-1);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			world.IncrementDisplayX(-1);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			world.IncrementDisplayX(1);
		}
	}
	
	/**
	 * Initialize the game window and all OpenGL-related setup
	 */
	public void initGL()
	{
		//Create game window
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		GL11.glClearColor(0.3f, 0.7f, 1.0f, 1.0f);
		
		//Allow transparent colors in textures
		GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	
    	//Setup 800 by 600 window
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 0, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		//Disable 3D effects
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	
	/**
	 * Initialize the world
	 */
	public void initWorld()
	{
		//genTestWorld0();
		genTestWorld1();
	}
	
	/**
	 * Load all textures
	 */
	public void loadTextures()
	{
		world.loadTextures();
	}
	
	/**
	 * Render any graphics (currently just the hero)
	 */
	public void renderGL()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL11.glPushMatrix();
			world.renderWorld();
		GL11.glPopMatrix();
	}
	
	/**
	 * Main class, simply constructs and runs the game
	 * 
	 * @param args the usual...
	 */
	public static void main(String[] args)
	{
		GameMain gameMain = new GameMain();
		gameMain.gameLoop();
	}
	
	
	/*#################################################################################
	 * Test Worlds
	 *#################################################################################*/
	private void genTestWorld0()
	{
		int xs = 10, ys = 10, zs = 10;
		Terrain[][][] t = new Terrain[xs][ys][zs];
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				for (int k = 0; k < zs; k ++)
				{
					t[i][j][k] = new Terrain(air);
				}
			}
		}
		
		t[xs/2][ys/2][zs/2] = new Terrain(grass);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
	}
	
	private void genTestWorld1()
	{
		int xs = 10, ys = 10, zs = 10;
		Terrain[][][] t = new Terrain[xs][ys][zs];
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				t[i][j][0] = new Terrain(rock);
				t[i][j][1] = new Terrain(grass);
			}
		}
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				for (int k = 2; k < zs; k ++)
				{
					t[i][j][k] = new Terrain(air);
				}
			}
		}
		
		t[2][2][1] = new Terrain(rock);
		t[3][2][1] = new Terrain(rock);
		t[4][2][1] = new Terrain(rock);
		t[2][3][1] = new Terrain(rock);
		t[3][3][1] = new Terrain(rock);
		t[4][3][1] = new Terrain(rock);
		t[3][4][1] = new Terrain(rock);
		t[4][4][1] = new Terrain(rock);
		t[3][4][2] = new Terrain(rock);
		t[4][4][2] = new Terrain(rock);
		
		t[2][2][2] = new Terrain(grass);
		t[3][2][2] = new Terrain(grass);
		t[4][2][2] = new Terrain(grass);
		t[2][3][2] = new Terrain(grass);
		t[3][3][2] = new Terrain(grass);
		t[4][3][2] = new Terrain(grass);
		
		t[3][4][3] = new Terrain(dirt);
		t[4][4][3] = new Terrain(dirt);
		
		t[7][3][1] = new Terrain(rock);
		t[7][3][2] = new Terrain(rock);
		
		t[7][3][3] = new Terrain(dirt, grass);
		
		t[6][8][1] = new Terrain(rock);
		t[6][8][2] = new Terrain(rock);
		t[7][8][1] = new Terrain(rock);
		t[7][8][2] = new Terrain(rock);
		t[8][8][1] = new Terrain(rock);
		t[8][8][2] = new Terrain(rock);
		t[7][8][3] = new Terrain(grass);
		t[8][8][3] = new Terrain(grass);
		t[6][8][3] = new Terrain(rock);
		t[6][8][4] = new Terrain(rock);
		//t[6][8][5] = new Terrain(grass);
		
		t[0][9][1] = new Terrain(dirt);
		t[0][8][1] = new Terrain(dirt);
		t[0][7][1] = new Terrain(dirt);
		t[1][9][1] = new Terrain(dirt);
		t[1][8][1] = new Terrain(dirt);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
	}
}