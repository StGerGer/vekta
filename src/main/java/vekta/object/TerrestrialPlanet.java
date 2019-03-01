package vekta.object;

import processing.core.PVector;
import vekta.LandingSite;

/**
 * Terrestrial (landable) planet
 */
public class TerrestrialPlanet extends Planet {
	private final LandingSite site;

	public TerrestrialPlanet(float mass, float density, PVector position, PVector velocity, int color) {
		super(mass, density, position, velocity, color);

		site = new LandingSite(this);
	}

	public LandingSite getLandingSite() {
		return site;
	}

	@Override
	public void onCollide(SpaceObject s) {
		// Check if landing
		if(s instanceof Spaceship) { // TODO: create `Lander` interface for event handling
			Spaceship ship = (Spaceship)s;
			if(ship.isLanding()) {
				if(site.land(ship)) {
					return;
				}
			}
		}
		// Oof
		super.onCollide(s);
	}

	@Override public void onDestroy(SpaceObject s) {
		super.onDestroy(s);

		// If something landed on this planet, destroy it as well
		SpaceObject landed = site.getLanded();
		if(landed != null) {
			landed.onDestroy(s);
		}
	}
}