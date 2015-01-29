package com.vsis.drachen.model.objects;

import com.vsis.drachen.model.IdObject;
import com.vsis.drachen.model.User;

/**
 * Command pattern for user-item-interaction
 */
public abstract class ObjectEffect extends IdObject {

	public ObjectEffect() {

	}

	public ObjectEffect(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public abstract boolean isConuming();

	public abstract boolean isGenerating();

	/**
	 * performs the effect on the item
	 * 
	 * @param extension
	 *            parameterObject extension to the items
	 * @param param
	 *            parameter for performing the effect
	 * @param user
	 *            user performing the effect
	 * @param item
	 *            item on which the effect should be performed
	 * @return true if the perform is successful
	 */
	public abstract boolean perform(IItemExtension extension,
			ObjectEffectParameter param, User user, Item item);
}