package com.vsis.drachen.model.objects;

import java.util.List;

/**
 * Indicates that the effect generates new items
 */
public interface IGeneratingObjectsEffect {

	/**
	 * returns a list of Prototypes which should be generated by a effect
	 * 
	 * @return list of ItemPrototyps
	 */
	public List<ItemPrototype> generatedItems();
}