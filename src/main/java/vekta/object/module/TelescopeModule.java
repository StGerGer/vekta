package vekta.object.module;

import processing.core.PVector;
import vekta.menu.Menu;
import vekta.menu.handle.SurveyMenuHandle;
import vekta.menu.option.BackOption;
import vekta.object.Targeter;
import vekta.object.TerrestrialPlanet;
import vekta.overlay.singleplayer.Notification;
import vekta.terrain.LandingSite;

import static vekta.Vekta.*;

public class TelescopeModule extends ShipModule {
	private static final float RANGE_SCALE = 1000;

	private final float resolution;

	public TelescopeModule(float resolution) {
		this.resolution = resolution;
	}

	public float getResolution() {
		return resolution;
	}

	@Override
	public String getName() {
		return "Survey Telescope";
	}

	@Override
	public ModuleType getType() {
		return ModuleType.TELESCOPE;
	}

	@Override
	public boolean isBetter(Module other) {
		return other instanceof TelescopeModule && getResolution() > ((TelescopeModule)other).getResolution();
	}

	@Override
	public Module getVariant() {
		return new TelescopeModule(chooseInclusive(1, 10));
	}

	@Override
	public void onKeyPress(char key) {
		if(key == 'r') {
			Targeter t = (Targeter)getShip().getModule(ModuleType.TARGET_COMPUTER);
			if(t != null && t.getTarget() instanceof TerrestrialPlanet) {
				TerrestrialPlanet planet = (TerrestrialPlanet)t.getTarget();
				float dist = PVector.dist(getShip().getPosition(), planet.getPosition());
				float maxDist = getResolution() * RANGE_SCALE;
				if(dist <= maxDist) {
					LandingSite site = ((TerrestrialPlanet)t.getTarget()).getLandingSite();
					Menu menu = new Menu(new SurveyMenuHandle(new BackOption(getWorld()), site));
					menu.addDefault();
					setContext(menu);
				}
				else {
					addObject(new Notification("Out of range! (" + round(maxDist / dist * 100) + "% resolution)"));
				}
			}
		}
	}
}
