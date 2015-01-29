package com.vsis.drachen.model.world;

import java.util.Set;

public interface ILocation {

	public abstract String getName();

	public abstract Location getParentLocation();

	public abstract Set<Location> getChildLocations();

	public abstract Polygon getShape();

	public abstract void setShape(Polygon newShape);

	public abstract void setName(String newName);

	public abstract String getImageKey();

	public abstract void setImageKey(String imageKey);

	public abstract void setParentLocation(Location newParentLocation);

	public abstract boolean isInside(Point p);

	/**
	 * search this and childlocations for the lowest Location fitting the Point
	 * 
	 * @param p
	 * @return null if no location contains the point
	 */
	public abstract Location findSublocation(Point p);

	public abstract void updateReferences();

}