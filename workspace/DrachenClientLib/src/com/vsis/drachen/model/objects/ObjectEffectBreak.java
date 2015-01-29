package com.vsis.drachen.model.objects;

import com.vsis.drachen.model.User;

public class ObjectEffectBreak extends ObjectEffect {

	public ObjectEffectBreak() {

	}

	public ObjectEffectBreak(String name) {
		super(name);
	}

	@Override
	public boolean isConuming() {
		return true;
	}

	@Override
	public boolean isGenerating() {
		return false;
	}

	@Override
	public boolean perform(IItemExtension itemextension,
			ObjectEffectParameter param, User user, Item item) {
		// try to remove the item from location and user (if one works ->
		// success)
		return itemextension.removeItemFromUser(item)
				|| itemextension.removeItemFromLocation(item);

	}
}