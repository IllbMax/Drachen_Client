package com.vsis.drachen.model.objects;

import com.vsis.drachen.model.User;

public class ObjectEffectCreate extends ObjectEffect {

	private boolean giveToUser;

	public ObjectEffectCreate() {

	}

	public ObjectEffectCreate(String name) {
		super(name);
	}

	@Override
	public boolean isConuming() {
		return false;
	}

	@Override
	public boolean isGenerating() {
		return true;
	}

	@Override
	public boolean perform(IItemExtension itemextension,
			ObjectEffectParameter param, User user, Item item) {

		for (Item newitem : param.getCreatedItems()) {

			if (isGiveToUser() || itemextension.userOwnsItem(item))
				itemextension.addItemToUser(newitem);
			else
				itemextension.addItemToLocation(item.getLocationId(), newitem);
		}

		return true;
	}

	public boolean isGiveToUser() {
		return giveToUser;
	}

	public void setGiveToUser(boolean giveToUser) {
		this.giveToUser = giveToUser;
	}
}