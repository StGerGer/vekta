package vekta.module;

import vekta.knowledge.OceanKnowledge;
import vekta.menu.Menu;
import vekta.menu.handle.LocationMenuHandle;
import vekta.menu.handle.OceanMenuHandle;
import vekta.menu.option.LootMenuButton;
import vekta.menu.option.ScanOceanButton;
import vekta.terrain.location.Location;
import vekta.terrain.location.OceanLocation;
import vekta.util.InfoGroup;

import java.util.List;

public class OceanScannerModule extends ShipModule {
	private float strength;

	public OceanScannerModule() {
		this(1);
	}

	public OceanScannerModule(float strength) {
		this.strength = strength;
	}

	public float getStrength() {
		return strength;
	}

	@Override
	public String getName() {
		return "Oceanic Scanner v" + getStrength();
	}

	@Override
	public ModuleType getType() {
		return ModuleType.OCEAN;
	}

	@Override
	public int getMass() {
		return 1000;
	}

	@Override
	public float getValueScale() {
		return 1.5F * getStrength();
	}

	@Override
	public boolean isBetter(Module other) {
		return other instanceof OceanScannerModule && getStrength() > ((OceanScannerModule)other).getStrength();
	}

	@Override
	public Module createVariant() {
		return new OceanScannerModule(chooseInclusive(.5F, 3, .5F));
	}

	@Override
	public void onMenu(Menu menu) {
		if(menu.getHandle() instanceof OceanMenuHandle) {
			Location location = ((LocationMenuHandle)menu.getHandle()).getLocation();

			if(location instanceof OceanLocation) {
				OceanLocation ocean = (OceanLocation)location;

				menu.add(new ScanOceanButton(ocean, getStrength()));
			}
		}
	}

	@Override
	public void onInfo(InfoGroup info) {
		info.addDescription("Detect underwater features on oceanic planets.");
	}
}
