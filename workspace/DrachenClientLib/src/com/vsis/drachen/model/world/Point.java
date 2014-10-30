package com.vsis.drachen.model.world;

import com.vsis.drachen.model.IdObject;

public class Point extends IdObject {

	private double x;

	private double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setXY(double x, double y) {
		this.y = y;
		this.x = x;
	}

}
