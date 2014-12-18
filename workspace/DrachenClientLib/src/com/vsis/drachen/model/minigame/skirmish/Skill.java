package com.vsis.drachen.model.minigame.skirmish;

public abstract class Skill {

	/**
	 * Name of the Skill
	 */
	private String name;
	/**
	 * Description of the Skill
	 */
	private String description;

	public Skill(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * determines if the skill is hidden from the user so that it can only be
	 * activated by sensor events
	 * 
	 * @return true if hidden
	 */
	public abstract boolean isHidden();

	/**
	 * determines if the skill can be used now
	 * 
	 * @return true if it can be used
	 */
	public abstract boolean isReady();

	/**
	 * Calculates the damage that this skill will do
	 * 
	 * @return number of hp the target will loose
	 */
	public abstract Damage calculateDamage();

	/**
	 * speed of skill usage a the higher the speed, the earlier the skill will
	 * be performed
	 * 
	 * @return speed of skill
	 */
	public abstract int getSpeed();

}
