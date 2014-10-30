package com.vsis.drachen.model.world;

import java.util.HashSet;
import java.util.Set;

import com.vsis.drachen.model.IdObject;

public class Location extends IdObject {

	private String name;

	private Location parentLocation = null;

	private Set<Location> childLocations = new HashSet<Location>(0);

	// private Set<QuestPrototype> quests = new HashSet<QuestPrototype>(0);

	private Polygon shape;

	public Location() {

	}

	public String getName() {
		return name;
	}

	public Location getParentLocation() {
		return parentLocation;
	}

	public Set<Location> getChildLocations() {
		return childLocations;
	}

	// public Set<QuestPrototype> getQuestPrototypes(){
	// return quests;
	// }

	public Polygon getShape() {
		return shape;
	}

	public void setShape(Polygon newShape) {
		shape = newShape;
	}

	public void setName(String newName) {
		name = newName;
	}

	public void setParentLocation(Location newParentLocation) {
		if (parentLocation != null) {
			parentLocation.childLocations.remove(this);
		}
		parentLocation = newParentLocation;
		if (parentLocation != null) {
			parentLocation.childLocations.add(this);
		}
	}

	public boolean isInside(Point p) {
		return shape.Contains(p);
	}

	/**
	 * search this and childlocations for the lowest Location fitting the Point
	 * 
	 * @param p
	 * @return null if no location contains the point
	 */
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

	public void updateReferences() {
		for (Location l : childLocations) {
			l.parentLocation = this;
			l.updateReferences();
		}
	}

}
