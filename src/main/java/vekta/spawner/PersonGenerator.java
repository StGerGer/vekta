package vekta.spawner;

import processing.core.PVector;
import vekta.Faction;
import vekta.RenderLevel;
import vekta.Resources;
import vekta.economy.ProductivityModifier;
import vekta.object.planet.TerrestrialPlanet;
import vekta.person.Person;
import vekta.spawner.world.AsteroidSpawner;
import vekta.terrain.HabitableTerrain;
import vekta.terrain.settlement.OutpostSettlement;
import vekta.terrain.settlement.Settlement;

import java.util.ArrayList;
import java.util.List;

import static vekta.Vekta.*;

public class PersonGenerator {
	public static Person createPerson() {
		return createPerson(randomHome());
	}

	public static Person createPerson(Settlement home) {
		Person person = register(new Person(randomPersonName(), home.getFaction()));
		person.setHome(home);
		if(v.random(1) < .5F) {
			person.setTitle(randomPersonTitle(person));
		}
		return person;
	}
	
	public static String randomPersonName() {
		return Resources.generateString("person");
	}

	public static String randomBossName() {
		return Resources.generateString("boss");
	}

	public static String randomPersonTitle(Person person) {
		float r = v.random(1);
		if(r > .6 && person.hasHome()) {
			return "of " + person.findHomeObject().getName();
		}
		if(r > .4) {
			return "of " + person.getFaction().getName();
		}
		else {
			return Resources.generateString("person_title");
		}
	}

	public static Settlement randomHome(Faction faction) {
		List<Settlement> homes = new ArrayList<>();
		// Choose from settlements affecting faction's economy
		for(ProductivityModifier mod : faction.getEconomy().getModifiers()) {
			if(mod instanceof Settlement) {
				homes.add((Settlement)mod);
			}
		}
		return !homes.isEmpty() ? v.random(homes) : randomHome();
	}

	public static Settlement randomHome() {
		TerrestrialPlanet planet = getWorld().findRandomObject(TerrestrialPlanet.class);
		if(planet != null) {
			// Find a suitable existing settlement
			List<Settlement> settlements = planet.getLandingSite().getTerrain().getSettlements();
			if(!settlements.isEmpty()) {
				return v.random(settlements);
			}
		}

		// If no candidate was found, create an asteroid with a new settlement
		PVector pos = WorldGenerator.randomSpawnPosition(RenderLevel.PLANET, new PVector());
		Settlement settlement = new OutpostSettlement(FactionGenerator.randomFaction());
		AsteroidSpawner.createAsteroid(pos, new HabitableTerrain(settlement));
		return settlement;
	}
}
