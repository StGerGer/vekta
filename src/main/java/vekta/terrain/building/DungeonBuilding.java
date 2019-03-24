package vekta.terrain.building;

import vekta.dungeon.Dungeon;
import vekta.menu.Menu;
import vekta.menu.option.DungeonRoomOption;
import vekta.terrain.LandingSite;
import vekta.terrain.settlement.SettlementPart;

public class DungeonBuilding implements SettlementPart {
	private final Dungeon dungeon;

	public DungeonBuilding(Dungeon dungeon) {
		this.dungeon = dungeon;
	}

	public Dungeon getDungeon() {
		return dungeon;
	}

	@Override
	public String getName() {
		return getDungeon().getName();
	}

	@Override
	public String getGenericName() {
		return "Dungeon";
	}

	@Override
	public BuildingType getType() {
		return BuildingType.EXTERNAL;
	}

	@Override
	public void setup(LandingSite site) {
	}

	@Override
	public void setupMenu(Menu menu) {
		getDungeon().getStartRoom().setVisited(true); // Don't show special outline
		menu.add(new DungeonRoomOption(getDungeon().getName(), getDungeon().getStartRoom()));
	}
}