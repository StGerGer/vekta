package vekta.terrain.settlement;

import vekta.Faction;
import vekta.Resources;
import vekta.economy.Economy;
import vekta.economy.NoiseModifier;
import vekta.spawner.PersonGenerator;
import vekta.spawner.WorldGenerator;
import vekta.terrain.building.*;

import java.util.List;

import static vekta.Vekta.v;

public class TownSettlement extends Settlement {

	public TownSettlement(Faction faction) {
		super(faction, "town");

		add(new District(this, "Marketplace", BuildingType.MARKET));
		if(v.chance(.75F)) {
			add(new District(this, Resources.generateString("town_social"), BuildingType.RESIDENTIAL));
		}

		add(new CapitalBuilding(this));

		add(new ForumBuilding(this, (int)(v.random(5, 10))));

		List<MarketBuilding> buildings = WorldGenerator.randomMarkets(2, .1F);
		if(!buildings.isEmpty()) {
			int ct = 0;
			for(MarketBuilding building : buildings) {
				add(building);
				// Limit number of marketplaces
				if(++ct == 3) {
					break;
				}
			}
		}
		else {
			// Occasionally have a specialized market
			add(WorldGenerator.createMarket(2));
		}

		if(v.chance(.2F)) {
			add(new RefineryBuilding());
		}
	}

	@Override
	public String getGenericName() {
		return "Town";
	}

	@Override
	public void onSetup() {
		getTerrain().addFeature("Rural");

		// Add extra people
		int personCt = (int)v.random(2) + 1;
		for(int i = 0; i < personCt; i++) {
			PersonGenerator.createPerson(this);
		}
	}

	@Override
	public void setupEconomy(Economy economy) {
		economy.setValue(v.random(2, 5));
		economy.addModifier(new NoiseModifier(1));
	}
}
