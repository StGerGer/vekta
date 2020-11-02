package vekta.situation;

import vekta.object.planet.TerrestrialPlanet;
import vekta.player.Player;
import vekta.world.AttributeZoomController;
import vekta.world.RenderLevel;

import static vekta.Vekta.getDistanceUnit;
import static vekta.Vekta.getWorld;

public class NearPlanetSituation implements Situation {

	private static final float NEAR_PLANET_TRESHOLD = 1E10F;	// How close a player can be before they are considered "near" a planet

	@Override
	public boolean isHappening(Player player) {
		for(TerrestrialPlanet planet : getWorld().findObjects(TerrestrialPlanet.class)) {
			if(player.getShip().relativePosition(planet).mag() < NEAR_PLANET_TRESHOLD) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void start(Player player) {
		getWorld().addZoomController(new AttributeZoomController(getClass(), getDistanceUnit(RenderLevel.PLANET)));
	}
}
