package com.vsis.drachen.model.objects;

import com.vsis.drachen.model.User;

public class ObjectEffectTakeDrop extends ObjectEffect {

	public ObjectEffectTakeDrop() {

	}

	public ObjectEffectTakeDrop(String name) {
		super(name);
	}

	@Override
	public boolean isConuming() {
		return false;
	}

	@Override
	public boolean isGenerating() {
		return false;
	}

	@Override
	public boolean perform(IItemExtension itemextension,
			ObjectEffectParameter param, User user, Item item) {

		if (itemextension.userOwnsItem(item)) {
			itemextension.removeItemFromUser(item);
			itemextension.addItemToLocation(user.getLocation().getId(), item);
		} else // if(item.getLocationId() > 0)
		{
			itemextension.removeItemFromLocation(item);
			itemextension.addItemToUser(item);

		}
		return true;
	}
}