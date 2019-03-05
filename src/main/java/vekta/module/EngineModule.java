package vekta.module;

import vekta.object.ship.ModularShip;

import static com.jogamp.opengl.math.FloatUtil.abs;

public class EngineModule extends ShipModule {
	private final float speed;

	public EngineModule(float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public String getName() {
		return "Engine v" + getSpeed();
	}

	@Override
	public ModuleType getType() {
		return ModuleType.ENGINE;
	}

	@Override
	public boolean isBetter(Module other) {
		return other instanceof EngineModule && getSpeed() > ((EngineModule)other).getSpeed();
	}

	@Override
	public Module getVariant() {
		return new EngineModule(chooseInclusive(.5F, 4, .1F));
	}

	@Override
	public void onUpdate() {
		ModularShip ship = getShip();
		float thrust = ship.getThrustControl();
		if(ship.consumeEnergy(20 * getSpeed() * abs(thrust) * PER_MINUTE)) {
			ship.accelerate(thrust * getSpeed());
		}
	}

	@Override
	public void onKeyPress(char key) {
		if(key == 'w') {
			getShip().setThrustControl(1);
		}
		if(key == 's') {
			getShip().setThrustControl(-1);
		}
	}

	@Override
	public void onKeyRelease(char key) {
		if(key == 'w' || key == 's') {
			getShip().setThrustControl(0);
		}
	}
}