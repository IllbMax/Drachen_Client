package com.vsis.drachen.model.minigame.skirmish;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSkillSelector implements ISkillSelector {

	private Skill despair = new MeeleAttack("Despair", "meh.", 10, 15, 10, 0);
	private List<Skill> skills;
	Random rnd = new Random();

	public RandomSkillSelector(List<Skill> skills) {
		this.skills = skills;
	}

	@Override
	public Skill chooseSkill() {

		List<Skill> aktive = new ArrayList<Skill>(skills.size());
		for (Skill s : skills) {
			if (s.isReady())
				aktive.add(s);
		}
		if (aktive.size() == 0)
			return despair;
		else
			return aktive.get(rnd.nextInt(aktive.size()));
	}

}
