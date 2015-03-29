package utils.planners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;

import static entities.Agent.direction.*;

public class AStar {

	public enum MovementType
	{
		SimpleStepping, Stepping, Jumpping
	}
	
	private Node currentNode;
	private PriorityQueue<Node> frontier;
	private ArrayList<Position> explored;
	private ArrayList<direction> actions;
	private Position goal;
	private int loopCounter;
	private Agent agent;
	private World world;
	
	private MovementType movementType;
	private int loopsPerPlanningIteration;
	private int maxPlanningLoops;
	
	private boolean queryInProgress;
	private boolean solutionFound;
	
	private String path;
	
	public AStar(MovementType movementType)
	{
		this.frontier = new PriorityQueue<Node>();
		this.explored = new ArrayList<Position>();
		this.actions = new ArrayList<direction>();
		this.loopCounter = 0;
		
		this.movementType = movementType;
		loopsPerPlanningIteration = 30;
		maxPlanningLoops = 300;
		
		queryInProgress = false;
		solutionFound = false;
		
		path = "";
	}
	
	public void startPlanningQuery(Agent agent, World world, Position start, Position goal)
	{
		this.agent = agent;
		this.world = world;
		this.goal = goal;
		currentNode = new Node(start, Distance.distance2D(start, goal), "");
		frontier.clear();
		frontier.add(currentNode);
		explored.clear();
		actions.clear();
		actions.add(up);
		actions.add(down);
		actions.add(left);
		actions.add(right);
		loopCounter = 0;
		
		solutionFound = false;
		queryInProgress = true;
		
		path = "";
	}
	
	
	/**
	 * Execute an iteration of planning, will terminte on one of the following conditions:
	 *  - success : the plan can be extracted using the getPath() method
	 *  - reaching the specified number of loops per planning iteration : the state will be stored until plan() is called again
	 *  - failing to find a solution within the specified maximum number of planning loops 
	 */
	public void plan()
	{
		if (!queryInProgress)
		{
			System.out.println("No planning query has been initialized!");
			return;
		}
		
		int localLoopMax = loopCounter + loopsPerPlanningIteration;

		//search loop
		while (!frontier.isEmpty())
		{
			loopCounter ++;
			currentNode = frontier.poll();
			
			if (!explored.contains(currentNode.getPos())) //quick check for duplicates in frontier
			{
				//goal check
				if (currentNode.getPos().equals(goal))
				{
					path = currentNode.getPath();
					queryInProgress = false;
					solutionFound = true;
					return;
				}
				explored.add(currentNode.getPos());
				
				//find and add neighbors to be explored
				Collections.shuffle(actions); //randomize order of actions so that one equally useful direction will not be arbitrarily prioritized
				for (int i = 0; i < actions.size(); i ++)
				{
					direction dir = actions.get(i);
					if (PathPlannerUtils.canStep(agent, world, currentNode.getPos(), dir))
					{
						Position newPos = PathPlannerUtils.resultingPosition(world, currentNode.getPos(), dir, false, false);
						//add any unexplored nodes to the frontier
						if (!explored.contains(newPos))
						{
							String updatedPath = new String(currentNode.getPath());
							switch (dir)
							{
							case up:	updatedPath += "U";	break;
							case down:	updatedPath += "D";	break;
							case left:	updatedPath += "L";	break;
							case right:	updatedPath += "R";	break;
							}
							frontier.add(new Node(newPos, Distance.distance2D(newPos, goal), updatedPath));
						}
					}
				}
			}
			
			//global and local loop max check
			if (loopCounter >= maxPlanningLoops)
			{
				queryInProgress = false;
				solutionFound = false;
				return;
			}
			
			if (loopCounter >= localLoopMax)
			{
				return;
			}
		}
		
		queryInProgress = false;
		solutionFound = false;
	}
	
	public boolean isQueryInProgress()
	{
		return queryInProgress;
	}
	
	public boolean isSolutionFound()
	{
		return solutionFound;
	}
	
	public String getPath()
	{
		return path;
	}
}
