package com.iot.locallization_ibeacon.navigation;

import java.util.ArrayList;

public class Node{
	final static int DIJKSTRA_WEIGHT = 1;
	final static int HEURISTIC_WEIGHT = 1;
	private double x, y;
	private int id, indexFrom, floorNo;
	private double  heuristicDist,dijkstraDist,  AStarValue;
	private ArrayList<Node> neighbors;
	private boolean isVisited = false;
	
	
	public int getFloorNo() {
		return floorNo;
	}

	public void setFloorNo(int floorNo) {
		this.floorNo = floorNo;
	}

	public int getIndexFrom() {
		return indexFrom;
	}

	public void setIndexFrom(int indexFrom) {
		this.indexFrom = indexFrom;
	}

	public double getDijkstraDist() {
		return dijkstraDist;
	}

	public void setDijkstraDist(double value) {
		this.dijkstraDist = value;
	}
		

	public double getHeuristicDist() {
		return heuristicDist;
	}

	public void setHeuristicDist(double heuristicDist) {
		this.heuristicDist = heuristicDist;
	}

	public double getAStarValue() {
		return AStarValue;
	}

	public void setAStarValue() {
		AStarValue = heuristicDist * HEURISTIC_WEIGHT + dijkstraDist * DIJKSTRA_WEIGHT;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public Node(int i, int j)
	{
		this.x=i;
		this.y=j;
		this.dijkstraDist= 0;
		this.heuristicDist = 0;
		this.isVisited = false;
		neighbors = new ArrayList<Node>();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public void setNeighbor(Node node)
	{
		neighbors.add(node);
	}
	
	public ArrayList<Node> getNeighbors()
	{
		return neighbors;
	}
	

}
