package main;

import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

//import loop.GameLoop;
//import loop.Timing;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import actions.Jump;
import actions.StepOrClimb;
import actions.Turn;
import entities.Agent;
import entities.Hero;
import entities.Innkeeper;
import entities.Wanderer;
import entities.Agent.Direction;
import things.Beam;
import things.Candle;
import things.Chair;
import things.Fireplace;
import things.FireplaceChimney;
import things.FireplaceSide;
import things.Firewood;
import things.Bar;
import things.Ladder;
import things.Rope;
import things.Stairs;
import things.Table;
import things.WallCandle;
import utils.display.DisplayText;
import world.Position;
import world.Terrain;
import world.World;
import static world.Terrain.terrainType.*;
import static entities.Agent.Direction.*;
import static world.World.TimeOfDay.*;
import static things.Chair.chairType.*;
import static things.Thing.ConnectionContext.*;
import static things.Thing.Orientation.*;
import static things.Stairs.stairsType.*;


public class GameMain {
	
	World world;
	
	int arrowKeyInputCount = 0;
	
	boolean devOn = false;
	TrueTypeFont font;
	//long lastFPS = Timing.getTime();
	int fps = 0;
	int currentFPS = 0;
	
	/**
	 * Main game loop, this is where everything happens
	 */
	public void gameLoop()
	{		
		//Initialization
		initGL();
		initWorld();
		loadTextures();
		
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, false);
			
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

			/*if (Timing.getTime() - lastFPS > 1000) {
				currentFPS = fps;
		        fps = 0; //reset the FPS counter
		        lastFPS += 1000; //add one second
		    }
		    fps++;*/
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
				
				//Tilde key (without shift) loads up the debug overlay 
				if (Keyboard.getEventKey() == Keyboard.KEY_GRAVE) 
				{
					devOn = !devOn;
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
							Direction d = player.getDir();
							Position pos = player.getPos();
							int x = pos.x;
							int y = pos.y;
							int z = pos.z;
							switch (d)
							{
							case Up: y++; break;
							case Down: y--; break;
							case Left: x--; break;
							case Right: x++; break;
							}
							if (world.getAgentAt(x, y, z) != null)
								world.getAgentAt(x, y, z).interact(player, world);
							else if (world.hasThing(x, y, z))
								world.getThingsAt(x, y, z).interact(player, world);
							else
							{
								if (player.isIdle())
								{
									player.setJumpAction(new Jump());
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
						if (arrowKeyInputCount < 3)
						{
							player.setTurnAction(new Turn(Down));
							player.setCurrentAction(player.getTurnAction());
						}
						else
						{
							player.setStepOrClimbAction(new StepOrClimb(Down));
							player.setCurrentAction(player.getStepOrClimbAction());
						}
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
						if (arrowKeyInputCount < 3)
						{
							player.setTurnAction(new Turn(Up));
							player.setCurrentAction(player.getTurnAction());
						}
						else
						{
							player.setStepOrClimbAction(new StepOrClimb(Up));
							player.setCurrentAction(player.getStepOrClimbAction());
						}
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
						if (arrowKeyInputCount < 3)
						{
							player.setTurnAction(new Turn(Right));
							player.setCurrentAction(player.getTurnAction());
						}
						else
						{
							player.setStepOrClimbAction(new StepOrClimb(Right));
							player.setCurrentAction(player.getStepOrClimbAction());
						}
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
						if (arrowKeyInputCount < 3)
						{
							player.setTurnAction(new Turn(Left));
							player.setCurrentAction(player.getTurnAction());
						}
						else
						{
							player.setStepOrClimbAction(new StepOrClimb(Left));
							player.setCurrentAction(player.getStepOrClimbAction());
						}
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
		
		
		if(devOn) {
			DisplayText dt = new DisplayText();
			world.textTexture.bind();
			String str = "FPS: " + currentFPS;
			System.out.println(str);
	        dt.overlayString(0, 0, str);
		}
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
		
		world.setTod(Sunrise);
		
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
		
		//inn test
		//bottom and top floor and roof
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
			for (int k = 4; k < 7; k ++)
			{
				t[i][37][k] = new Terrain(woodPlank, thatch);
				t[i][30][k] = new Terrain(plaster, thatch);
			}
		}
		for (int j = 30; j < 38; j ++)
		{
			for (int k = 1; k < 4; k ++)
			{
				t[25][j][k] = new Terrain(rock);
				t[44][j][k] = new Terrain(rock);
			}
			for (int k = 4; k < 6; k ++)
			{
				t[24][j][k] = new Terrain(plaster, thatch);
				t[45][j][k] = new Terrain(plaster, thatch);
				t[25][j][k] = new Terrain(plaster, thatch);
				t[44][j][k] = new Terrain(plaster, thatch);
			}
		}
		for (int i = 25; i < 45; i ++)
		{
			for (int j = 31; j < 37; j ++)
			{
				t[i][j][3] = new Terrain(woodSupport, woodPlank);
			}
		}
		for (int i = 25; i < 45; i ++)
		{
			for (int j = 31; j < 37; j ++)
			{
				t[i][j][6] = new Terrain(woodSupport, thatch);
			}
		}
		
		//doorway top
		t[34][30][3] = new Terrain(rock);
		t[35][30][3] = new Terrain(rock);
		
		//openings for stairs and chimney
		t[37][36][3] = new Terrain(air);
		t[41][36][3] = new Terrain(air);
		t[42][36][3] = new Terrain(air);
		
		//windows
		t[28][30][2] = new Terrain(windowProtruding, air);
		t[29][30][2] = new Terrain(windowProtruding, air);
		t[30][30][2] = new Terrain(windowProtruding, air);
		
		t[39][30][2] = new Terrain(windowProtruding, air);
		t[40][30][2] = new Terrain(windowProtruding, air);
		t[41][30][2] = new Terrain(windowProtruding, air);
		
		
		t[27][30][5] = new Terrain(window, air);
		t[27][37][5] = new Terrain(window, air);
		
		t[32][30][5] = new Terrain(window, air);
		t[32][37][5] = new Terrain(window, air);
		
		t[37][30][5] = new Terrain(window, air);
		//t[37][37][5] = new Terrain(window, air);
		
		t[42][30][5] = new Terrain(window, air);
		t[42][37][5] = new Terrain(window, air);
		
		//exterior wood beams
		Beam dbeam1 = new Beam.BeamBuilder().orientation(Diagonal).connection(Start).dir(Right).build();
		Beam dbeam2 = new Beam.BeamBuilder().orientation(Diagonal).connection(Middle).dir(Right).build();
		Beam dbeam3 = new Beam.BeamBuilder().orientation(Diagonal).connection(End).dir(Right).build();
		world.addThing(dbeam1, 28, 30, 4);
		world.addThing(dbeam2, 29, 30, 5);
		world.addThing(dbeam3, 30, 30, 6);
		Beam dbeam4 = new Beam.BeamBuilder().orientation(Diagonal).connection(Start).dir(Left).build();
		Beam dbeam5 = new Beam.BeamBuilder().orientation(Diagonal).connection(Middle).dir(Left).build();
		Beam dbeam6 = new Beam.BeamBuilder().orientation(Diagonal).connection(End).dir(Left).build();
		world.addThing(dbeam4, 39, 30, 6);
		world.addThing(dbeam5, 40, 30, 5);
		world.addThing(dbeam6, 41, 30, 4);
		Beam dbeam7 = new Beam.BeamBuilder().orientation(Diagonal).connection(Standalone).dir(Right).build();
		Beam dbeam8 = new Beam.BeamBuilder().orientation(Diagonal).connection(Standalone).dir(Left).build();
		world.addThing(dbeam7, 45, 30, 4);
		world.addThing(dbeam8, 24, 30, 4);
		Beam vbeam1 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Right).build();
		Beam vbeam2 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Right).build();
		Beam vbeam3 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Right).build();
		Beam vbeam4 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Right).build();
		Beam vbeam5 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Right).build();
		Beam vbeam6 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Right).build();
		Beam vbeam7 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Right).build();
		Beam vbeam8 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Right).build();
		Beam vbeam9 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Right).build();
		Beam vbeam10 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Right).build();
		Beam vbeam11 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Right).build();
		Beam vbeam12 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Right).build();
		Beam vbeam14 = new Beam.BeamBuilder().orientation(Vertical).connection(Standalone).dir(Right).build();
		world.addThing(vbeam1, 26, 30, 4);
		world.addThing(vbeam2, 26, 30, 5);
		world.addThing(vbeam3, 26, 30, 6);
		world.addThing(vbeam4, 31, 30, 4);
		world.addThing(vbeam5, 31, 30, 5);
		world.addThing(vbeam6, 31, 30, 6);
		world.addThing(vbeam7, 36, 30, 4);
		world.addThing(vbeam8, 36, 30, 5);
		world.addThing(vbeam9, 36, 30, 6);
		world.addThing(vbeam10, 41, 30, 4);
		world.addThing(vbeam11, 41, 30, 5);
		world.addThing(vbeam12, 41, 30, 6);
		world.addThing(vbeam14, 45, 30, 4);
		Beam vbeam15 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Left).build();
		Beam vbeam16 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Left).build();
		Beam vbeam17 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Left).build();
		Beam vbeam18 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Left).build();
		Beam vbeam19 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Left).build();
		Beam vbeam20 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Left).build();
		Beam vbeam21 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Left).build();
		Beam vbeam22 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Left).build();
		Beam vbeam23 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Left).build();
		Beam vbeam24 = new Beam.BeamBuilder().orientation(Vertical).connection(Start).dir(Left).build();
		Beam vbeam25 = new Beam.BeamBuilder().orientation(Vertical).connection(Middle).dir(Left).build();
		Beam vbeam26 = new Beam.BeamBuilder().orientation(Vertical).connection(End).dir(Left).build();
		Beam vbeam27 = new Beam.BeamBuilder().orientation(Vertical).connection(Standalone).dir(Left).build();
		world.addThing(vbeam27, 24, 30, 4);
		world.addThing(vbeam15, 28, 30, 4);
		world.addThing(vbeam16, 28, 30, 5);
		world.addThing(vbeam17, 28, 30, 6);
		world.addThing(vbeam18, 33, 30, 4);
		world.addThing(vbeam19, 33, 30, 5);
		world.addThing(vbeam20, 33, 30, 6);
		world.addThing(vbeam21, 38, 30, 4);
		world.addThing(vbeam22, 38, 30, 5);
		world.addThing(vbeam23, 38, 30, 6);
		world.addThing(vbeam24, 43, 30, 4);
		world.addThing(vbeam25, 43, 30, 5);
		world.addThing(vbeam26, 43, 30, 6);
		Beam hbeam1 = new Beam.BeamBuilder().orientation(Horizontal).connection(Start).dir(Right).build();
		Beam hbeam2 = new Beam.BeamBuilder().orientation(Horizontal).connection(Start).dir(Left).build();
		world.addThing(hbeam1, 24, 30, 3);
		world.addThing(hbeam2, 24, 30, 4);
		Beam hbeam3 = new Beam.BeamBuilder().orientation(Horizontal).connection(End).dir(Right).build();
		Beam hbeam4 = new Beam.BeamBuilder().orientation(Horizontal).connection(End).dir(Left).build();
		world.addThing(hbeam3, 45, 30, 3);
		world.addThing(hbeam4, 45, 30, 4);
		for (int i = 25; i <= 44; i ++)
		{
			Beam botBeam = new Beam.BeamBuilder().orientation(Horizontal).connection(Middle).dir(Right).build();
			Beam topBeam = new Beam.BeamBuilder().orientation(Horizontal).connection(Middle).dir(Left).build();
			world.addThing(botBeam, i, 30, 3);
			world.addThing(topBeam, i, 30, 4);
		}
		
		
		//house objects
		Bar b1 = new Bar.BarBuilder().connection(Start).build();
		world.addThing(b1, 27, 35, 1);
		for (int i = 0; i < 3; i ++)
		{
			Bar bar = new Bar.BarBuilder().connection(Middle).build();
			world.addThing(bar, 28+i, 35, 1);
		}
		Bar b2 = new Bar.BarBuilder().connection(End).build();
		world.addThing(b2, 31, 35, 1);
		Bar b3 = new Bar.BarBuilder().connection(Start).build();
		world.addThing(b3, 33, 35, 1);
		Bar b4 = new Bar.BarBuilder().connection(End).build();
		world.addThing(b4, 34, 35, 1);
		Bar b5 = new Bar.BarBuilder().dir(Up).connection(End).build();
		world.addThing(b5, 27, 36, 1);
		Bar b6 = new Bar.BarBuilder().dir(Up).connection(End).build();
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
		Chair c1 = new Chair.ChairBuilder(chairWooden).dir(Left).build();
		Chair c2 = new Chair.ChairBuilder(chairWooden).dir(Right).build();
		Chair c3 = new Chair.ChairBuilder(chairWooden).dir(Left).build();
		Chair c4 = new Chair.ChairBuilder(chairWooden).dir(Right).build();
		Chair c5 = new Chair.ChairBuilder(chairWooden).dir(Up).build();
		Chair c6 = new Chair.ChairBuilder(chairWooden).dir(Down).build();
		world.addThing(c1, 39, 33, 1);
		world.addThing(c2, 37, 33, 1);
		world.addThing(c3, 43, 33, 1);
		world.addThing(c4, 41, 33, 1);
		world.addThing(c5, 42, 32, 1);
		world.addThing(c6, 42, 34, 1);
		
		Stairs s1 = new Stairs.StairsBuilder(indoorWooden).dir(Right).horizontalConnection(Start).build();
		Stairs s2 = new Stairs.StairsBuilder(indoorWooden).dir(Right).horizontalConnection(Middle).build();
		Stairs s3 = new Stairs.StairsBuilder(indoorWooden).dir(Right).horizontalConnection(End).build();
		world.addThing(s1, 40, 36, 1);
		world.addThing(s2, 41, 36, 2);
		world.addThing(s3, 42, 36, 3);
		world.addThing(s2.getAssociatedBottom(), 41, 36, 1);
		world.addThing(s3.getAssociatedBottom(), 42, 36, 2);
		
		WallCandle candle1 = new WallCandle();
		WallCandle candle2 = new WallCandle();
		WallCandle candle3 = new WallCandle();
		world.addThing(candle1, 38, 37, 2);
		world.addThing(candle2, 33, 37, 2);
		world.addThing(candle3, 28, 37, 2);
		WallCandle wcandle4 = new WallCandle();
		world.addThing(wcandle4, 33, 37, 5);
		
		Candle candle4 = new Candle();
		Candle candle5 = new Candle();
		Candle candle6 = new Candle();
		world.addThing(candle4, 38, 33, 1);
		world.addThing(candle5, 27, 35, 1);
		world.addThing(candle6, 34, 35, 1);
		
		Fireplace fp = new Fireplace(true, 3);
		FireplaceSide fpside1 = new FireplaceSide(Left);
		FireplaceSide fpside2 = new FireplaceSide(Right);
		FireplaceChimney chimney1 = new FireplaceChimney(Down);
		FireplaceChimney chimney2 = new FireplaceChimney(Down);
		FireplaceChimney chimney3 = new FireplaceChimney(Down);
		FireplaceChimney chimney4 = new FireplaceChimney(Down);
		FireplaceChimney chimneytop = new FireplaceChimney(Up);
		world.addThing(fp, 37, 36, 1);
		world.addThing(fpside1, 36, 36, 1);
		world.addThing(fpside2, 38, 36, 1);
		world.addThing(chimney1, 37, 36, 3);
		world.addThing(chimney2, 37, 36, 4);
		world.addThing(chimney3, 37, 36, 5);
		world.addThing(chimney4, 37, 36, 6);
		world.addThing(chimneytop, 37, 36, 7);
		t[37][36][6] = new Terrain(air);
		
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
		
		//vertical stairs test
		for (int i = 4; i < 10; i ++)
		{
			for (int j = 18; j < 20; j ++)
			{
				for (int k = 1; k < 4; k ++)
				{
					t[i][j][k] = new Terrain(dirt, grass);
				}
			}
		}
		for (int k = 1; k < 4; k ++)
		{
			Stairs tempVStairs1;
			Stairs tempVStairs2;
			Stairs tempVStairs3;
			
			if (k == 1)
			{
				tempVStairs1 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(Start).verticalConnection(Start).build();
				tempVStairs2 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(End).verticalConnection(Start).build();
				tempVStairs3 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(Standalone).verticalConnection(Start).build();
			}
			else if (k == 3)
			{
				tempVStairs1 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(Start).verticalConnection(End).build();
				tempVStairs2 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(End).verticalConnection(End).build();
				tempVStairs3 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(Standalone).verticalConnection(End).build();
			}
			else
			{
				tempVStairs1 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(Start).verticalConnection(Middle).build();
				tempVStairs2 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(End).verticalConnection(Middle).build();
				tempVStairs3 = new Stairs.StairsBuilder(indoorWooden).dir(Up).horizontalConnection(Standalone).verticalConnection(Middle).build();
			}
				
			world.addThing(tempVStairs1, 5, 18, k);
			world.addThing(tempVStairs2, 6, 18, k);
			world.addThing(tempVStairs3, 7, 18, k);
		}
		
		//rope and ladder test
		for (int k = 1; k < 4; k ++)
		{
			Rope tempVStairs1;
			Rope tempVStairs2;
			Rope tempVStairs3;
			
			if (k == 1)
			{
				tempVStairs1 = new Rope.RopeBuilder().dir(Up).horizontalConnection(Start).verticalConnection(Start).build();
				tempVStairs2 = new Rope.RopeBuilder().dir(Up).horizontalConnection(End).verticalConnection(Start).build();
				tempVStairs3 = new Rope.RopeBuilder().dir(Up).horizontalConnection(Standalone).verticalConnection(Start).build();
			}
			else if (k == 3)
			{
				tempVStairs1 = new Rope.RopeBuilder().dir(Up).horizontalConnection(Start).verticalConnection(End).build();
				tempVStairs2 = new Rope.RopeBuilder().dir(Up).horizontalConnection(End).verticalConnection(End).build();
				tempVStairs3 = new Rope.RopeBuilder().dir(Up).horizontalConnection(Standalone).verticalConnection(End).build();
			}
			else
			{
				tempVStairs1 = new Rope.RopeBuilder().dir(Up).horizontalConnection(Start).verticalConnection(Middle).build();
				tempVStairs2 = new Rope.RopeBuilder().dir(Up).horizontalConnection(End).verticalConnection(Middle).build();
				tempVStairs3 = new Rope.RopeBuilder().dir(Up).horizontalConnection(Standalone).verticalConnection(Middle).build();
			}
				
			world.addThing(tempVStairs1, 15, 13, k);
			world.addThing(tempVStairs2, 16, 13, k);
			world.addThing(tempVStairs3, 17, 13, k);
		}
		
		Ladder ladder1 = new Ladder(Start);
		Ladder ladder2 = new Ladder(Middle);
		Ladder ladder3 = new Ladder(End);
		
		world.addThing(ladder1, 14, 13, 1);
		world.addThing(ladder2, 14, 13, 2);
		world.addThing(ladder3, 14, 13, 3);
		
		world.setTerrain(t);
		
		//agents
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		hero.setPos(new Position(25, 25, 1));
		agents.add(hero);
		
		Innkeeper innkeeper = new Innkeeper("char1", 0, 1);
		innkeeper.setPos(new Position(31, 36, 1));
		agents.add(innkeeper);
		
		world.setTod(Sunrise);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
}
