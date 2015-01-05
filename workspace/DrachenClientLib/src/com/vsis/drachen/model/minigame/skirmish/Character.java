package com.vsis.drachen.model.minigame.skirmish;

import java.util.List;

import com.vsis.drachen.model.minigame.skirmish.Damage.DamageType;

public class Character {

	private String name;
	/**
	 * name of avatar image
	 */
	private String avatar;
	/**
	 * health/hit points
	 */
	private int hp;
	private int maxHp;
	/**
	 * list of skills that the Character can use
	 */
	private List<Skill> skills;

	private ISkillSelector skillSelector;

	public Character(String name, int maxHp, List<Skill> skills,
			ISkillSelector skillSelector, String avatar) {
		this.name = name;
		this.avatar = avatar;
		this.maxHp = maxHp;
		this.hp = maxHp;
		this.skills = skills;
		this.skillSelector = skillSelector;
	}

	public int getHP() {
		return hp;
	}

	public int getMaxHP() {
		return maxHp;
	}

	public String getName() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public ISkillSelector getSkillSelector() {
		return skillSelector;
	}

	public void receiveDamage(Damage dmg) {
		if (dmg.getDmgType() != DamageType.None)
			hp -= dmg.getValue();
		if (hp < 0)
			hp = 0;
		if (hp > maxHp)
			hp = maxHp;
	}

}
