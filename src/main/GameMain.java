package main;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import entities.Agent;
import entities.Hero;
import entities.Innkeeper;
import entities.Wanderer;
import entities.Agent.direction;
import things.Candle;
import things.Chair;
import things.Fireplace;
import things.FireplaceChimney;
import things.FireplaceSide;
import things.Firewood;
import things.Bar;
import things.Stairs;
import things.Table;
import things.WallCandle;
import world.Position;
import world.Terrain;
import world.World;
import static world.Terrain.terrainType.*;
import static entities.Agent.direction.*;
import static world.World.timeOfDay.*;
import static things.Chair.chairType.*;
import static things.Thing.connectionContext.*;
import static things.Stairs.stairsType.*;


public class GameMain {
	
	World world;
	
	int arrowKeyInputCount = 0;
	
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
			world.updateThings();
			world.updateAgents();
			world.updateCameraScrollLock();
			world.updateCamera();
			
			//render everything
			renderGL();
			
			//get user input
			pollKeyboardInput();
			
			//update the screen
			Display.update();
			Display.sync(32);			
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
		//(Pressed Action Keys)
		while (Keyboard.next())
		{
			if (Keyboard.getEventKeyState())
			{
				//Debugging stuff
				if (Keyboard.getEventKey() == Keyboard.KEY_T)
				{
					world.cycleTimeOfDay();
				}
				
				switch (world.getCs())
				{
				case walking:
					//Pressed Action Keys
					if (Keyboard.getEventKey() == Keyboard.KEY_Z)
					{
						Hero player = world.getPlayer();
						if (player != null)
						{
							direction d = player.getDir();
							Position pos = player.getPos();
							int x = pos.x;
							int y = pos.y;
							int z = pos.z;
							switch (d)
							{
							case up: y++; break;
							case down: y--; break;
							case left: x--; break;
							case right: x++; break;
							}
							if (world.getAgentAt(x, y, z) != null)
								world.getAgentAt(x, y, z).interact(player, world);
							else if (world.hasThing(x, y, z))
								world.getThingsAt(x, y, z).interact(player, world);
							else
							{
								if (player.isIdle())
								{
									ArrayList<String> newArgs = new ArrayList<String>();
									player.setArgs(newArgs);
									player.setCurrentAction(player.getJumpAction());
								}
							}
						}
					}
				break;
				case talking:
					if (Keyboard.getEventKey() == Keyboard.KEY_Z)
					{
						if (world.isTextBoxActive())
						{
							if (world.getTextDisplay().sendInput(Keyboard.KEY_Z))
							{
								world.setTextBoxActive(false);
							}
						}
					}
				break;
				}
			}
		}
		
		//Held Action Keys
		switch (world.getCs())
		{
		case walking:
			if (Keyboard.isKeyDown(Keyboard.KEY_X))
			{
				Hero player = world.getPlayer();
				if (player != null && !player.isJumping())
				{
					if (player.getSpeed() != 4)
						player.setSpeed(4);
				}
			}
			else
			{
				Hero player = world.getPlayer();
				if (player != null && !player.isJumping())
				{
					if (player.getSpeed() != 2)
						player.setSpeed(2);
				}
			}
			
			//Arrow Keys
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			{
				Hero player = world.getPlayer();
				if (player != null)
				{
					arrowKeyInputCount ++;
					if (player.isIdle())
					{
						ArrayList<String> newArgs = new ArrayList<String>();
						newArgs.add("down");
						player.setArgs(newArgs);
						if (arrowKeyInputCount < 3)
							player.setCurrentAction(player.getTurnAction());
						else
							player.setCurrentAction(player.getStepAction());
					}
				}
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			{
				Hero player = world.getPlayer();
				if (player != null)
				{
					arrowKeyInputCount ++;
					if (player.isIdle())
					{
						ArrayList<String> newArgs = new ArrayList<String>();
						newArgs.add("up");
						player.setArgs(newArgs);
						if (arrowKeyInputCount < 3)
							player.setCurrentAction(player.getTurnAction());
						else
							player.setCurrentAction(player.getStepAction());
					}
				}
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				Hero player = world.getPlayer();
				if (player != null)
				{
					arrowKeyInputCount ++;
					if (player.isIdle())
					{
						ArrayList<String> newArgs = new ArrayList<String>();
						newArgs.add("right");
						player.setArgs(newArgs);
						if (arrowKeyInputCount < 3)
							player.setCurrentAction(player.getTurnAction());
						else
							player.setCurrentAction(player.getStepAction());
					}
				}
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			{
				Hero player = world.getPlayer();
				if (player != null)
				{
					arrowKeyInputCount ++;
					if (player.isIdle())
					{
						ArrayList<String> newArgs = new ArrayList<String>();
						newArgs.add("left");
						player.setArgs(newArgs);
						if (arrowKeyInputCount < 3)
							player.setCurrentAction(player.getTurnAction());
						else
							player.setCurrentAction(player.getStepAction());
					}
				}
			}
			else
			{
				arrowKeyInputCount = 0;
			}
		break;
		case talking:
			if (Keyboard.isKeyDown(Keyboard.KEY_X))
			{
				if (world.isTextBoxActive())
				{
					if (world.getTextDisplay().getSpeed() != 8)
						world.getTextDisplay().setSpeed(8);
				}
			}
			else
			{
				if (world.isTextBoxActive())
				{
					if (world.getTextDisplay().getSpeed() != 1)
						world.getTextDisplay().setSpeed(1);
				}
			}
		break;
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
		//genTestWorldHeroTest();
		//genTestWorldStairs();
		//genTestWorldJump();
		genLargeWorld();
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
		GL11.glPushMatrix();
			world.renderOverlay();
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
		t[6][8][5] = new Terrain(grass);
		
		t[0][9][1] = new Terrain(dirt);
		t[0][8][1] = new Terrain(dirt);
		t[0][7][1] = new Terrain(dirt);
		t[0][6][1] = new Terrain(dirt);
		t[1][9][1] = new Terrain(dirt);
		t[1][8][1] = new Terrain(air);
		t[1][7][1] = new Terrain(air);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
	}
	
	private void genTestWorldHeroTest()
	{
		genTestWorld1();
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		hero.setPos(new Position(1, 1, 2));
		agents.add(hero);
		
		Wanderer wanderer = new Wanderer(new Position(8, 6, 2), 32, 2);
		wanderer.setPos(new Position(8, 6, 2));
		agents.add(wanderer);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
	
	private void genTestWorldJump()
	{
		int xs = 6, ys = 6, zs = 8;
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
		
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				t[i][j][0] = new Terrain(grass);
			}
		}
		
		t[2][2][1] = new Terrain(grass);
		t[2][3][1] = new Terrain(grass);
		t[3][2][1] = new Terrain(grass);
		t[3][3][1] = new Terrain(grass);
		t[2][2][2] = new Terrain(grass);
		t[2][3][2] = new Terrain(grass);
		t[1][2][1] = new Terrain(grass);
		t[0][2][1] = new Terrain(grass);
		t[0][3][1] = new Terrain(grass);
		t[0][3][2] = new Terrain(grass);
		t[1][3][1] = new Terrain(grass);
		t[1][3][2] = new Terrain(grass);
		t[0][4][1] = new Terrain(grass);
		t[0][4][2] = new Terrain(grass);
		t[1][4][1] = new Terrain(grass);
		t[1][4][2] = new Terrain(grass);
		t[5][5][1] = new Terrain(grass);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
		
		world.setTod(sunrise);
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		hero.setPos(new Position(4, 1, 1));
		agents.add(hero);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
	
	private void genLargeWorld()
	{
		int xs = 50, ys = 50, zs = 10;
		
		world = new World(xs, ys, zs);
		
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
		
		Random rand = new Random();
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				if (rand.nextInt(2) == 0)
					t[i][j][0] = new Terrain(grass);
				else
					t[i][j][0] = new Terrain(dirt);
			}
		}
		
		//jumping/lighting/interact test
		for (int i = 12; i < 15; i ++)
		{
			for (int j = 25; j < 31; j ++)
			{
				t[i][j][1] = new Terrain(dirt, grass);
			}
			t[i][27][2] = new Terrain(dirt, grass);
		}
		Candle jumpCandle = new Candle();
		world.addThing(jumpCandle, 14, 27, 3);
		Chair jumpStool = new Chair.ChairBuilder(stoolWooden).build();
		Chair jumpStool2 = new Chair.ChairBuilder(stoolWooden).build();
		Chair jumpStool3 = new Chair.ChairBuilder(stoolWooden).build();
		world.addThing(jumpStool, 13, 26, 2);
		world.addThing(jumpStool2, 14, 25, 2);
		world.addThing(jumpStool3, 13, 29, 2);
		
		//house test
		//floor
		for (int i = 25; i < 45; i ++)
		{
			for (int j = 30; j < 38; j ++)
			{
				t[i][j][0] = new Terrain(dirt, woodPlank);
			}
		}
		//walls
		for (int i = 25; i < 45; i ++)
		{
			for (int k = 1; k < 4; k ++)
			{
				t[i][37][k] = new Terrain(rock);
				if (i < 34 || i > 35)
					t[i][30][k] = new Terrain(rock);
			}
		}
		for (int j = 30; j < 38; j ++)
		{
			for (int k = 1; k < 4; k ++)
			{
				t[25][j][k] = new Terrain(rock);
				t[44][j][k] = new Terrain(rock);
			}
		}
		
		//windows
		t[28][30][2] = new Terrain(glass, air);
		t[29][30][2] = new Terrain(glass, air);
		t[30][30][2] = new Terrain(glass, air);
		
		//house objects
		Bar b1 = new Bar.BarBuilder().connection(start).build();
		world.addThing(b1, 27, 35, 1);
		for (int i = 0; i < 3; i ++)
		{
			Bar bar = new Bar.BarBuilder().connection(middle).build();
			world.addThing(bar, 28+i, 35, 1);
		}
		Bar b2 = new Bar.BarBuilder().connection(end).build();
		world.addThing(b2, 31, 35, 1);
		Bar b3 = new Bar.BarBuilder().connection(start).build();
		world.addThing(b3, 33, 35, 1);
		Bar b4 = new Bar.BarBuilder().connection(end).build();
		world.addThing(b4, 34, 35, 1);
		Bar b5 = new Bar.BarBuilder().dir(up).connection(end).build();
		world.addThing(b5, 27, 36, 1);
		Bar b6 = new Bar.BarBuilder().dir(up).connection(end).build();
		world.addThing(b6, 34, 36, 1);
		
		Chair stool1 = new Chair.ChairBuilder(stoolWooden).build();
		Chair stool2 = new Chair.ChairBuilder(stoolWooden).build();
		Chair stool3 = new Chair.ChairBuilder(stoolWooden).build();
		world.addThing(stool1, 28, 34, 1);
		world.addThing(stool2, 29, 34, 1);
		world.addThing(stool3, 30, 34, 1);
		
		Table t1 = new Table();
		Table t2 = new Table();
		world.addThing(t1, 38, 33, 1);
		world.addThing(t2, 42, 33, 1);
		Chair c1 = new Chair.ChairBuilder(chairWooden).dir(left).build();
		Chair c2 = new Chair.ChairBuilder(chairWooden).dir(right).build();
		Chair c3 = new Chair.ChairBuilder(chairWooden).dir(left).build();
		Chair c4 = new Chair.ChairBuilder(chairWooden).dir(right).build();
		Chair c5 = new Chair.ChairBuilder(chairWooden).dir(up).build();
		Chair c6 = new Chair.ChairBuilder(chairWooden).dir(down).build();
		world.addThing(c1, 39, 33, 1);
		world.addThing(c2, 37, 33, 1);
		world.addThing(c3, 43, 33, 1);
		world.addThing(c4, 41, 33, 1);
		world.addThing(c5, 42, 32, 1);
		world.addThing(c6, 42, 34, 1);
		
		Stairs s1 = new Stairs.StairsBuilder(indoorWooden).dir(right).connection(start).build();
		Stairs s2 = new Stairs.StairsBuilder(indoorWooden).dir(right).connection(middle).build();
		Stairs s3 = new Stairs.StairsBuilder(indoorWooden).dir(right).connection(end).build();
		world.addThing(s1, 40, 36, 1);
		world.addThing(s2, 41, 36, 2);
		world.addThing(s3, 42, 36, 3);
		world.addThing(s2.getAssociatedBottom(), 41, 36, 1);
		world.addThing(s3.getAssociatedBottom(), 42, 36, 2);
		
		WallCandle candle1 = new WallCandle();
		WallCandle candle2 = new WallCandle();
		WallCandle candle3 = new WallCandle();
		world.addThing(candle1, 38, 36, 2);
		world.addThing(candle2, 33, 36, 2);
		world.addThing(candle3, 28, 36, 2);
		
		Candle candle4 = new Candle();
		Candle candle5 = new Candle();
		Candle candle6 = new Candle();
		world.addThing(candle4, 38, 33, 1);
		world.addThing(candle5, 27, 35, 1);
		world.addThing(candle6, 34, 35, 1);
		
		Fireplace fp = new Fireplace(true, 3);
		FireplaceSide fpside1 = new FireplaceSide(left);
		FireplaceSide fpside2 = new FireplaceSide(right);
		FireplaceChimney chimney1 = new FireplaceChimney(down);
		FireplaceChimney chimney2 = new FireplaceChimney(down);
		FireplaceChimney chimney3 = new FireplaceChimney(down);
		FireplaceChimney chimneytop = new FireplaceChimney(up);
		world.addThing(fp, 37, 36, 1);
		world.addThing(fpside1, 36, 36, 1);
		world.addThing(fpside2, 38, 36, 1);
		world.addThing(chimney1, 37, 36, 3);
		world.addThing(chimney2, 37, 36, 4);
		world.addThing(chimney3, 37, 36, 5);
		world.addThing(chimneytop, 37, 36, 6);
		
		Firewood fw1 = new Firewood();
		Firewood fw2 = new Firewood();
		Firewood fw3 = new Firewood();
		world.addThing(fw1, 41, 36, 1);
		world.addThing(fw2, 42, 36, 1);
		world.addThing(fw3, 43, 36, 1);
		
		//shadow tests
		for (int i = 1; i < 8; i ++)
		{
			t[22][28][i] = new Terrain(dirt, grass);
			t[18][25][i] = new Terrain(dirt, grass);
		}
		t[19][25][7] = new Terrain(dirt, grass);
		t[19][24][7] = new Terrain(dirt, grass);
		t[17][26][7] = new Terrain(dirt, grass);
		t[17][25][7] = new Terrain(dirt, grass);
		
		//vertical transparency test
		for (int i = 1; i < 4; i ++)
		{
			for (int j = 13; j <= 18; j ++)
			{
				t[j][13][i] = new Terrain(dirt, grass);
				t[j][14][i] = new Terrain(dirt, grass);
				t[j][15][i] = new Terrain(dirt, grass);
			}
		}
		
		world.setTerrain(t);
		
		//agents
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		hero.setPos(new Position(25, 25, 1));
		agents.add(hero);
		
		Innkeeper innkeeper = new Innkeeper("char1", 0, 1);
		innkeeper.setPos(new Position(31, 36, 1));
		agents.add(innkeeper);
		
		world.setTod(sunrise);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
}
