package utils.planners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import utils.planners.PathPlannerUtils.MovementClass;
import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;
import static utils.planners.PathPlannerUtils.MovementClass.*;
import static entities.Agent.direction.*;

public class AStar {
	
	private Node currentNode;
	private PriorityQueue<Node> frontier;
	private ArrayList<Position> explored;
	private ArrayList<direction> actions;
	private Position goal;
	private int loopCounter;
	private Agent agent;
	private World world;
	
	private MovementClass movementType;
	private int loopsPerPlanningIteration;
	private int maxPlanningLoops;
	
	private boolean queryInProgress;
	private boolean solutionFound;
	
	private String path;
	
	public AStar(MovementClass movementType)
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
		if (movementType == SimpleStepping)
			currentNode = new Node(start, Distance.distance2D(start, goal), "");
		else
			currentNode = new Node(start, Distance.distance3D(start, goal), "");
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
					//check for a valid new position
					direction dir = actions.get(i);
					Position newPos = null;
					if (movementType == SimpleStepping)
					{
						if (PathPlannerUtils.canStep(agent, world, currentNode.getPos(), dir))
							newPos = PathPlannerUtils.resultingPosition(world, currentNode.getPos(), dir, false, false);
					}
					else if (movementType == Stepping)
					{
						
						Position checkPos = new Position(currentNode.getPos());
						checkPos.z --;
						if ((dir == up || dir == down) && world.hasThing(checkPos) && world.getThingsAt(checkPos).hasRamp())
						{
							continue;
						}
						
						//TODO: check if onHorizontalRamp, then check if onVerticalRamp, then do this
						boolean rampStepCheck[] = PathPlannerUtils.checkForHorizontalRampStep(world, currentNode.getPos(), dir);
						if (rampStepCheck[0]) //Ramp Step
						{
							boolean continuingDescent = !rampStepCheck[1] && PathPlannerUtils.isContinuingDescent(agent, world, currentNode.getPos(), dir);
							if (PathPlannerUtils.canStepHorizontalRamp(agent, world, currentNode.getPos(), dir, rampStepCheck[1], continuingDescent))
								newPos = PathPlannerUtils.resultingPosition(world, currentNode.getPos(), dir, true, rampStepCheck[1]);
						}
						else //Regular Step
						{
							if (PathPlannerUtils.canStep(agent, world, currentNode.getPos(), dir))
								newPos = PathPlannerUtils.resultingPosition(world, currentNode.getPos(), dir, false, false);
						}
					}
					
					//add new position if one was found
					if (newPos != null)
					{
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
							if (movementType == SimpleStepping)
								frontier.add(new Node(newPos, Distance.distance2D(newPos, goal), updatedPath));
							else
								frontier.add(new Node(newPos, Distance.distance3D(newPos, goal), updatedPath));
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
