package com.vsis.drachen.model.minigame.skirmish;

import java.util.Random;

import com.vsis.drachen.model.minigame.skirmish.Damage.DamageType;

/**
 * simple meele attack with min/max damage and chance of critical hits
 * 
 */
public class MeeleAttack extends Skill {

	int minDamage, maxDamage;
	/**
	 * procent \in [0,100];
	 */
	int critChance;
	Random rnd;
	int speed;
	boolean isHidden;

	public MeeleAttack(String name, String description, String actionText,
			int minDmg, int maxDmg, int speed, int critChance) {
		this(name, description, actionText, minDmg, maxDmg, speed, critChance,
				false);
	}

	public MeeleAttack(String name, String description, String actionText,
			int minDmg, int maxDmg, int speed, int critChance, boolean isHidden) {
		super(name, description, actionText);
		minDamage = minDmg;
		maxDamage = maxDmg;
		this.critChance = critChance;
		this.speed = speed;
		this.isHidden = isHidden;
		rnd = new Random(System.nanoTime() ^ hashCode());
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public Damage calculateDamage() {
		int dmg = minDamage;
		dmg += rnd.nextInt(maxDamage - minDamage + 1);

		if (rnd.nextInt(100) < critChance)
			dmg += dmg / 2;

		return new Damage(dmg, DamageType.physical);
	}

	@Override
	public int getSpeed() {
		return speed;
	}

	@Override
	public boolean isHidden() {
		return isHidden;
	}
}
