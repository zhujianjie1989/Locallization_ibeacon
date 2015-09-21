package com.iot.locallization_ibeacon.navigation;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class App
{
	final static String FILE_NAME_FLOOR_1 = "C:\\Users\\user\\Desktop\\floor1.txt";
	final static String FILE_NAME_FLOOR_2 = "C:\\Users\\user\\Desktop\\floor2.txt";
	private ArrayList<Node> nodes, nodes2, emptyNodes, emptyNodes2, visitedNodes, staircaseNodes, staircaseNodes2;
	private ArrayList<Node> backupEmptyNodes, backupEmptyNodes2;
	private ArrayList<Path> bestPaths;
	private ArrayList<ArrayList<Node>> allNodes, allEmptyNodes, allStaircaseNodes;
	private Node node;
	private Node startNode, endNode;
	private int startFloor, endFloor;
	FileInputStream fstream = null;
	private double minLength = 0;
	
	public static void main(String[] args)
	{		
		new App().initializeNodes();
	}
	
	public void initializeNodes()
	{
		int counter=0;
		
		
		nodes = new ArrayList<Node>();
		nodes2 = new ArrayList<Node>();
		emptyNodes = new ArrayList<Node>();
		emptyNodes2 = new ArrayList<Node>();
		backupEmptyNodes = new ArrayList<Node>();
		backupEmptyNodes2 = new ArrayList<Node>();
		
		visitedNodes = new ArrayList<Node>();
		staircaseNodes = new ArrayList<Node>();
		staircaseNodes2 = new ArrayList<Node>();
		bestPaths = new ArrayList<Path>();
		allNodes = new ArrayList<ArrayList<Node>>();
		allEmptyNodes = new ArrayList<ArrayList<Node>>();
		allStaircaseNodes = new ArrayList<ArrayList<Node>>();
		
		//initialize fundamental node layout
		for(int i=0; i<7; i++)
		{
			for(int j=0; j<7; j++)
			{
				
				node = new Node(i, j);
				node.setId(counter);
				node.setFloorNo(0);
				nodes.add(node);		
				counter++;
				System.out.println(node.getX() + " "+ node.getY());
			}
		}
		allNodes.add(nodes);//add one floor nodes 
		
		counter = 0;
		for(int i=0; i<7; i++)
		{
			for(int j=0; j<7; j++)
			{
				
				node = new Node(i, j);
				node.setId(counter);
				node.setFloorNo(1);
				nodes2.add(node);
				counter++;
				System.out.println(node.getX() + " "+ node.getY());
			}
		}
		
		allNodes.add(nodes2);//add another flood nodes
		
		setUserInput();
		
		System.out.println("startNode id: "+startNode.getId());
		System.out.println("endNode id: "+endNode.getId());
		
		//set up the connections and staircase nodes
		setAllNeighbors("C:\\Users\\user\\Desktop\\floor" + startFloor + ".txt", startFloor, emptyNodes, staircaseNodes);
		setAllNeighbors("C:\\Users\\user\\Desktop\\floor" + endFloor + ".txt", endFloor, emptyNodes2, staircaseNodes2);
		if(allStaircaseNodes.get(startFloor).get(0).equals(allStaircaseNodes.get(endFloor).get(0)))
		{
			System.out.println("They are the same!");
		}
		
		//backup initial state of empty nodes
		backupEmptyNodes = emptyNodes;
		backupEmptyNodes2 = emptyNodes2;
		
		//start finding the path
		findBestPath(startFloor, endFloor);
		
	}
	
	public void setUserInput()
	{
		//input the starting and ending nodes
		Scanner in = new Scanner(System.in);
		//get the starting node
		System.out.println("Input the start node floor No: ");
		startFloor = Integer.valueOf(in.nextLine());
		
		System.out.println("Input the start node index: ");
		int startIndex = Integer.valueOf(in.nextLine());
		startNode = allNodes.get(startFloor).get(startIndex);
		
		//get the ending node
		System.out.println("Input the end node floor No: ");
		endFloor = Integer.valueOf(in.nextLine());
		
		System.out.println("Input the end node index: ");
		int endIndex = Integer.valueOf(in.nextLine());
		endNode = allNodes.get(endFloor).get(endIndex);
	}
	
	public void setAllNeighbors(String fileName, int floorNo, ArrayList<Node> emptyNodes, ArrayList<Node> staircaseNodes)
	{
		try {
			fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			staircaseNodes.clear();
			while((strLine = br.readLine()) != null)
			{
				String[] numbers = strLine.split(" ");
				if(numbers.length == 1)//if the number is indicating the staircase index
				{
					int staircaseNodeIndex = Integer.valueOf(numbers[0]);
					staircaseNodes.add(allNodes.get(floorNo).get(staircaseNodeIndex));
				}
				else
				{
					int firstNum = Integer.valueOf(numbers[0]);
					int secondNum = Integer.valueOf(numbers[1]);
					setNeighbors(firstNum, secondNum, floorNo);
					if(!emptyNodes.contains(allNodes.get(floorNo).get(firstNum)))
					{
						emptyNodes.add(allNodes.get(floorNo).get(firstNum));
					}
					if(!emptyNodes.contains(allNodes.get(floorNo).get(secondNum)))
					{
						emptyNodes.add(allNodes.get(floorNo).get(secondNum));
					}
					
					System.out.println("Neighbors: "+firstNum +", " + secondNum);					
				}				
			}
			allEmptyNodes.add(emptyNodes);// add one floor nodes to the empty nodes
			allStaircaseNodes.add(staircaseNodes);
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setNeighbors(int i, int j, int floorNo)
	{
		allNodes.get(floorNo).get(i).setNeighbor(allNodes.get(floorNo).get(j));
		allNodes.get(floorNo).get(j).setNeighbor(allNodes.get(floorNo).get(i));
	}
	
	public void findBestPath(int startFloor, int endFloor)
	{
		
		
		//start calculating the paths
		for(int i=0; i<allStaircaseNodes.get(startFloor).size(); i++)
		{
			//Start the algorithm
			Path path1 = startAlgorithm(startNode, allStaircaseNodes.get(startFloor).get(i), startFloor);
			Path path2 = startAlgorithm(allStaircaseNodes.get(endFloor).get(i), endNode, endFloor);
			
			//compute the total length
			double totalPathLength = path1.getLength() + path2.getLength();
			long totalProcessTime = path1.getProcessTime() + path2.getProcessTime();
			System.out.println("Total path length: "+totalPathLength + ", total process time: "+totalProcessTime);
			System.out.println("-----------------------------------------------");
			
			
			//find the best path based on path length
			if(i==0)
			{
				minLength = totalPathLength;
				bestPaths.add(path1);
				bestPaths.add(path2);
			}
			else
			{
				if(totalPathLength < minLength)
				{
					minLength = totalPathLength;
					bestPaths.clear();
					bestPaths.add(path1);
					bestPaths.add(path2);
				}
				
			}
		}
		long totalProcessTime = bestPaths.get(0).getProcessTime() + bestPaths.get(1).getProcessTime();
		//print best path
		System.out.println("Best path: ");
		bestPaths.get(0).printPath();
		bestPaths.get(1).printPath();
		System.out.println("Total path lenght: "+minLength + ", total process time: "+totalProcessTime);
	}
	
	public Path startAlgorithm(Node startNode, Node endNode, int floorNo)
	{
		
		//starting point
		long startTime = System.currentTimeMillis();
		Path path = new Path();
		
		//clear all records
		//nodes = clearVisited(nodes);
		//emptyNodes = initializeEmptyNodes(emptyNodes);
		clearVisitedNodes(floorNo);
		visitedNodes.clear();
		if(floorNo == 0)
		{
			initializeEmptyNodes();
		}
		
		//start node is visited when the algorithm starts
		startNode.setVisited(true);
		allEmptyNodes.get(floorNo).remove(startNode);
		visitedNodes.add(startNode);
		
		int searchingIndex = 0;
		Node searchingNode = visitedNodes.get(searchingIndex);
		
		//algorithm stops when the endNode is visited
		while(!endNode.isVisited())
		{
			for(int i=0; i<searchingNode.getNeighbors().size(); i++)
			{
				Node currentNode = searchingNode.getNeighbors().get(i);
				
				if(!currentNode.isVisited())
				{
					//if the neighboring node is not visited
					//compute heuristic distance
					double heuristicDist = heuristicDistance(currentNode, endNode);
					currentNode.setHeuristicDist(heuristicDist);
					
					//compute dijkstra'distance
					currentNode.setDijkstraDist(
							searchingNode.getDijkstraDist() + 1);
					
					//compute AStar distance
					currentNode.setAStarValue();
					
					currentNode.setVisited(true);
					currentNode.setIndexFrom(searchingNode.getId());
					allEmptyNodes.get(floorNo).remove(currentNode);
					visitedNodes.add(currentNode);

				}
				else
				{
					if(searchingNode.getAStarValue() + 1 < currentNode.getAStarValue())
					{
						currentNode.setDijkstraDist(searchingNode.getDijkstraDist() + 1);
						currentNode.setAStarValue();
						currentNode.setIndexFrom(searchingNode.getId());
					}
				}
			}
			searchingIndex++;
			searchingNode = visitedNodes.get(searchingIndex);			
		}
		long endTime = System.currentTimeMillis();
		long processTime = endTime - startTime;

		//print the search result
		Node nodeFrom = endNode;
		while(!nodeFrom.equals(startNode))
		{
			path.addPathDetails(nodeFrom.getId() + "->");
			nodeFrom = allNodes.get(floorNo).get(nodeFrom.getIndexFrom());			
		}
		path.addPathDetails(nodeFrom.getId() + "------>");
		path.setLength(endNode.getDijkstraDist());
		path.setProcessTime(processTime);
		path.addPathDetails("Path distance: " + endNode.getDijkstraDist() + ", Process time: "+processTime);
		path.printPath();
		return path;
	}
	
	private void clearVisitedNodes(int floorNo) 
	{
		for(int i=0; i<allNodes.get(floorNo).size(); i++)
		{
			allNodes.get(floorNo).get(i).setVisited(false);
			allNodes.get(floorNo).get(i).setDijkstraDist(0);
			allNodes.get(floorNo).get(i).setHeuristicDist(0);
			allNodes.get(floorNo).get(i).setAStarValue();
		}
	}
	
	public void initializeEmptyNodes()
	{
		allEmptyNodes.clear();
		allEmptyNodes.add(backupEmptyNodes);
		allEmptyNodes.add(backupEmptyNodes2);
	}

	

	public double heuristicDistance(Node currentNode, Node endNode)
	{
		double dist = Math.sqrt(Math.pow(currentNode.getX() - endNode.getX(),2) + Math.pow(currentNode.getY() - endNode.getY(),2));
		return dist;
		
	}
	
	
}