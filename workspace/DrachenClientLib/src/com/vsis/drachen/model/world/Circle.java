package com.vsis.drachen.model.world;

public class Circle implements IShape {

	private Point center;
	private double radius;
	double r_square;

	public Circle(Point center, double radius) {
		setCenter(center);
		setRadius(radius);
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	@Override
	public boolean Contains(Point q) {
		double dx = q.getX() - center.getX();
		double dy = q.getY() - center.getY();

		return (dx * dx + dy * dy) <= r_square;
	}

	@Override
	public Point getCenter() {
		return center;
	}

	@Override
	public double getArea() {
		return Math.PI * r_square;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		r_square = radius * radius;
	}

}
