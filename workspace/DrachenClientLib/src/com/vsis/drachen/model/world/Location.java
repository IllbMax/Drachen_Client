package com.vsis.drachen.model.world;

import java.util.HashSet;
import java.util.Set;

import com.vsis.drachen.model.IdObject;

public class Location extends IdObject implements ILocation {

	private String name;
	private String imageKey;

	private String scannerKey;
	private Location parentLocation = null;

	private Set<Location> childLocations = new HashSet<Location>(0);

	// private Set<QuestPrototype> quests = new HashSet<QuestPrototype>(0);

	private Polygon shape;

	public Location() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#getParentLocation()
	 */
	@Override
	public Location getParentLocation() {
		return parentLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#getChildLocations()
	 */
	@Override
	public Set<Location> getChildLocations() {
		return childLocations;
	}

	// public Set<QuestPrototype> getQuestPrototypes(){
	// return quests;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#getShape()
	 */
	@Override
	public Polygon getShape() {
		return shape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vsis.drachen.model.world.ILocation#setShape(com.vsis.drachen.model
	 * .world.Polygon)
	 */
	@Override
	public void setShape(Polygon newShape) {
		shape = newShape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#setName(java.lang.String)
	 */
	@Override
	public void setName(String newName) {
		name = newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#getImageKey()
	 */
	@Override
	public String getImageKey() {
		return imageKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#setImageKey(java.lang.String)
	 */
	@Override
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vsis.drachen.model.world.ILocation#setParentLocation(com.vsis.drachen
	 * .model.world.Location)
	 */
	@Override
	public void setParentLocation(Location newParentLocation) {
		if (parentLocation != null) {
			parentLocation.childLocations.remove(this);
		}
		parentLocation = newParentLocation;
		if (parentLocation != null) {
			parentLocation.childLocations.add(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vsis.drachen.model.world.ILocation#isInside(com.vsis.drachen.model
	 * .world.Point)
	 */
	@Override
	public boolean isInside(Point p) {
		return shape != null && shape.Contains(p);
	}

	public Location findSublocation(String searchName) {
		if (this.name.equals(searchName)) {
			return this;
		} else {
			for (Location child : childLocations) {
				Location tmpLoc = child.findSublocation(searchName);
				if (tmpLoc != null) {
					return tmpLoc;
				}
			}
		}
		//
		return null;
	}

	/**
	 * search this and childlocations for the Location with the right name
	 * 
	 * @param p
	 * @return null if no location contains the point
	 */
	@Override
	public Location findSublocation(Point p) {
		if (isInside(p)) {
			for (Location loc : childLocations) {
				Location l = loc.findSublocation(p);
				if (l != null)
					return l;
			}
			return this;
		}
		return null;
	}

	// public void removeQuestPrototype(QuestPrototype quest){
	// quests.remove(quest);
	// }
	// public void addQuestPrototype(QuestPrototype quest){
	// quests.add(quest);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vsis.drachen.model.world.ILocation#updateReferences()
	 */
	@Override
	public void updateReferences() {
		for (Location l : childLocations) {
			l.parentLocation = this;
			l.updateReferences();
		}
	}

	public String getScannerKey() {
		return scannerKey;
	}

	public void setScannerKey(String scannerKey) {
		this.scannerKey = scannerKey;
	}

	/**
	 * defines if location is inside a building (so it has no gps
	 * coordinate-shape)
	 * 
	 * @return true if it isn't reachable by GPS
	 */
	public boolean isInsideLocation() {
		return shape == null;
	}
}
