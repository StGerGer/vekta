package vekta.module;

import processing.core.PVector;
import vekta.object.HomingProjectile;
import vekta.object.SpaceObject;
import vekta.object.Targeter;
import vekta.object.ship.ModularShip;

import static vekta.Vekta.*;

public class TorpedoModule extends WeaponModule {
	private final float speed;

	public TorpedoModule() {
		this(1);
	}

	public TorpedoModule(float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public String getName() {
		return "Torpedo Launcher v" + getSpeed();
	}

	@Override
	public Module getVariant() {
		return new TorpedoModule(chooseInclusive(1, 2));
	}

	@Override
	public boolean isBetter(Module other) {
		return other instanceof TorpedoModule && getSpeed() > ((TorpedoModule)other).getSpeed();
	}

	@Override
	public void fireWeapon() {
		ModularShip ship = getShip();
		Module m = ship.getModule(ModuleType.TARGET_COMPUTER);
		if(m instanceof Targeter) {
			SpaceObject target = ((Targeter)m).getTarget();
			if(target != null && ship.consumeEnergyImmediate(1)) {
				getWorld().playSound("laser", ship.getPosition());
				PVector velocity = ship.getHeading().add(PVector.random2D()).add(ship.getVelocity());
				register(new HomingProjectile(ship, target, getSpeed(), ship.getPosition(), velocity, ship.getColor()));
			}
		}
	}
}
