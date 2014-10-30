package com.vsis.drachen.model.world;

public interface IShape {

	public abstract boolean Contains(Point q);

	public abstract Point getCenter();

	public abstract double getArea();

}