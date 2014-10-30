package com.vsis.drachen;

import java.util.List;

import com.vsis.drachen.model.User;
import com.vsis.drachen.model.quest.Quest;
import com.vsis.drachen.model.quest.QuestPrototype;
import com.vsis.drachen.model.quest.QuestTarget;
import com.vsis.drachen.model.world.Location;

public class DrachenClient {

	public static void main(String[] args) {

		// to enable all the coockies
		BlubClient blub = new BlubClient();

		try {
			blub.initConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		User user = blub.Login("9lindt", "test");

		List<QuestPrototype> questPrototypes = blub.QuestsForLocation(1);

		Location location = blub.LocationForId(3);

		System.out.println("Moin moin " + user.getDisplayName());
		System.out.println("Derzeit hast du " + user.getQuests().size()
				+ " Quests:");
		for (Quest quest : user.getQuests()) {
			System.out.println(String.format("[Quest %d] %s: %s",
					quest.getId(), quest.getName(), quest.getDescription()));
			for (QuestTarget questTarget : quest.getQuestTargets()) {
				System.out.println(String.format("\t[Target %d] %s",
						questTarget.getId(), questTarget.getName()));
			}
		}

		System.out.println("Available Quests for LocationId " + 1 + ":");
		for (QuestPrototype questPrototype : questPrototypes) {
			System.out.println(String.format("[Quest %d] %s: %s",
					questPrototype.getId(), questPrototype.getName(),
					questPrototype.getDescription()));
		}

		System.out.println("Locations:");
		printLocation(location, "");

	}

	private static void printLocation(Location location, String indent) {
		System.out.println(String.format("%s[Location %d] %s: %s", indent,
				location.getId(), location.getName(), location.getShape()));
		for (Location loc : location.getChildLocations()) {
			printLocation(loc, indent + "\t");
		}
	}

}
