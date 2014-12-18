package com.vsis.drachen.model.minigame.skirmish;

public class Damage {

	public static enum DamageType {
		None, physical
	}

	private int value;
	private DamageType dmgType;

	public Damage(int value, DamageType type) {
		this.value = value;
		this.dmgType = type;
	}

	public int getValue() {
		return value;
	}

	public DamageType getDmgType() {
		return dmgType;
	}

}
