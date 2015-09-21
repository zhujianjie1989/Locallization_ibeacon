package com.iot.locallization_ibeacon.navigation;

public class Path {
	private double length;
	private long processTime;
	private String pathDetails;
	
	public Path()
	{
		this.length = 0;
		this.processTime= 0;
		this.pathDetails = "";
	}

	public double getLength() {
		return length;
	}

	public void setLength(double lenght) {
		this.length = lenght;
	}

	public long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}

	public String getPathDetails() {
		return pathDetails;
	}

	public void addPathDetails(String pathDetails) {
		this.pathDetails = this.pathDetails + pathDetails;
	}
	
	public void printPath()
	{
		System.out.println(this.pathDetails);
	}
	
	
	
	
}
