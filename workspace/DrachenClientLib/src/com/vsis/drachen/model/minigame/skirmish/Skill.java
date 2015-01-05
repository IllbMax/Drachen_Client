package com.vsis.drachen.model.minigame.skirmish;

public abstract class Skill {

	public static final String DEFAULT_ACTION_TEXT = "%1$s uses %3$s";

	/**
	 * Name of the Skill
	 */
	private String name;
	/**
	 * Description of the Skill
	 */
	private String description;
	/**
	 * Text displayed at the screen if this skill is used
	 * 
	 * Formatting text: %1$s name of user, %2$s name of target, %3$s name of
	 * skill
	 */
	private String actionText;

	public Skill(String name, String description, String actionText) {
		this.name = name;
		this.description = description;
		this.actionText = actionText;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getActionText() {
		return actionText;
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
