package main;

import java.util.UUID;

import civilization_and_minds.group.agents.SocietyGroupAgent;
import civilization_and_minds.group.purpose.SocietyPurpose;
import civilization_and_minds.group.purpose.IGroupPurpose.GroupType;
import civilization_and_minds.group.types.SettlementGroup;
import civilization_and_minds.social.concepts.profile.Profile;
import civilization_and_minds.social.concepts.profile.ProfileType;
import civilization_and_minds.social.concepts.relation.RelationType;
import processing.core.PApplet;
import sim.GameMapTile;
import sim.GameUniverse;

public class Main {

	public static void main(String[] args) {
		GameUniverse universe = new GameUniverse(UUID.randomUUID(), 1, 1);
		universe.initTileMap();
		universe.setCurrentTile(new GameMapTile(universe.getTile(0, 0), 800, 500, universe));
		UUID groupid = UUID.randomUUID();
		SettlementGroup sgr = new SettlementGroup(
				new SocietyGroupAgent(new SocietyPurpose("mainciv", GroupType.SETTLEMENT), groupid),
				new Profile(groupid, ProfileType.GROUP).setOptionalName("mainciv"), universe.getTile(0, 0));
		sgr.getKnowledge().learnNewRelationship(sgr.getSelfProfile(), RelationType.FOUND_AT, universe.getTile(0, 0));
		universe.addNewGroup(sgr);
		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(universe, 15f));
	}

}
