package vekta.module;

import processing.core.PVector;
import vekta.object.ship.ModularShip;
import vekta.object.SpaceObject;

import static vekta.Vekta.*;

public class AutopilotModule extends TargetingModule {
	private static final float APPROACH_SCALE = .02F;
	private static final float SLOWDOWN_FACTOR = .8F;
	private static final float ROTATE_SMOOTH = .1F;

	public AutopilotModule() {
		super();
	}

	public boolean isActive() {
		return getShip().isLanding();
	}

	public void setActive(boolean active) {
		getShip().setLanding(active);
		getWorld().updateTargeters(getShip());
	}

	@Override
	public String getName() {
		return "Autopilot Computer";
	}

	@Override
	public boolean isBetter(Module other) {
		return other instanceof TargetingModule && !(other instanceof AutopilotModule);
	}

	@Override
	public Module getVariant() {
		return new AutopilotModule();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		ModularShip ship = getShip();
		SpaceObject target = getTarget();
		if(isActive() && target != null && ship.consumeEnergy(5 * PER_MINUTE)) {
			// Grab telemetry vectors
			PVector position = ship.getPosition();
			PVector velocity = ship.getVelocity();
			PVector heading = ship.getHeading();
			PVector offset = target.getPosition().sub(position);
			PVector relative = target.getVelocity().sub(velocity);

			// Compute desired velocity
			float dist = offset.mag();
			float dot = offset.copy().normalize().dot(relative);

			float stoppingSpeed = sqrt(2 * ship.getSpeed() * dist);
			float approachSpeed = min(
					stoppingSpeed * SLOWDOWN_FACTOR + dot,
					APPROACH_SCALE * (1 + target.getRadius() / dist));

			PVector desiredVelocity = target.getVelocity()
					.add(relative.sub(offset.copy().setMag(dot)).mult(5)) // Cancel tangential velocity
					.add(offset.copy().mult(approachSpeed));

			// Choose direction to fire engines
			float dir = heading.dot(velocity.copy().sub(desiredVelocity)) >= 0 ? 1 : -1;
			float deltaV = velocity.dist(desiredVelocity);

			// Set heading and engine power
			PVector newHeading = velocity.sub(desiredVelocity).setMag(dir).add(ship.getHeading());
			ship.setHeading(PVector.lerp(heading, newHeading, ROTATE_SMOOTH));
			ship.setThrustControl(Math.max(-1, Math.min(1, -dir * deltaV / ship.getSpeed())));
		}
	}

	@Override
	public void onKeyPress(char key) {
		super.onKeyPress(key);

		if(key == '\t') {
			setActive(true);
		}
		else if(key == 'w' || key == 'a' || key == 's' || key == 'd')/* TODO: reference external key mapping */ {
			setActive(false);
		}
	}
}