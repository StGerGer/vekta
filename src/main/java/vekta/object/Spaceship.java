package vekta.object;

import processing.core.*;
import vekta.*;
import vekta.item.Inventory;

import java.util.List;

import static processing.core.PConstants.TRIANGLES;
import static vekta.Vekta.*;

public class Spaceship extends SpaceObject {
	// Default Spaceship stuff
	private static final int HANDLING_SCALE = 1000;
	private static final float LANDING_SPEED = 1F;

	// Exclusive Spaceship things
	private int controlScheme; // Defined by CONTROL_DEF: 0 = WASD, 1 = IJKL
	private float speed;  // Force of the vector added when engine is on
	private int handling; // Speed of turning
	private int ammo;
	private float thrust;
	private float turn;

	// Normal SpaceObject stuff
	private final String name;
	private final float mass;
	private final float radius;
	private final PVector heading;

	// Landing doodads
	private boolean landing;
	private final PVector influence = new PVector();

	private final Inventory inventory = new Inventory();

	public Spaceship(String name, float mass, float radius, PVector heading, PVector position, PVector velocity, int color, int ctrl, float speed, int handling, int ammo, int money) {
		super(position, velocity, color);
		this.name = name;
		this.mass = mass;
		this.radius = radius;
		this.heading = heading;
		this.controlScheme = ctrl;
		this.speed = speed;
		this.handling = handling;
		this.inventory.add(money);
		this.ammo = ammo;
	}

	// Draws a nice triangle
	// Shamelessly stolen from https://processing.org/examples/flocking.html
	@Override
	public void draw() {
		// Draw a triangle rotated in the direction of ship
		float theta = heading.heading() + processing.core.PApplet.radians(90);
		Vekta v = getInstance();
		v.fill(1);
		v.stroke(getColor());
		v.pushMatrix();
		v.translate(position.x, position.y);
		v.rotate(theta);
		v.beginShape(TRIANGLES);
		v.vertex(0, -radius * 2);
		v.vertex(-radius, radius * 2);
		v.vertex(radius, radius * 2);
		v.endShape();
		v.popMatrix();

		// Draw influence vector
		v.stroke(255, 0, 0);
		v.line(position.x, position.y, position.x + (influence.x * 100), position.y + (influence.y * 100));
	}

	@Override
	public void onUpdate() {
		if(thrust != 0) {
			addVelocity(heading.copy().setMag(thrust * speed));
		}
		turnBy(turn);

		SpaceObject target = ((Singleplayer)getWorld()).closestObject;
		if(landing && target != null) {
			PVector relative = velocity.copy().sub(target.getVelocity());
			float mag = relative.mag();
			if(mag > 0) {
				heading.set(relative).normalize();
				float approachFactor = Math.min(1, 5 * target.getRadius() / target.getPosition().sub(position).mag());
				thrust = Math.max(-1, Math.min(1, (LANDING_SPEED * (1 - approachFactor / 2) - mag) * approachFactor / speed));
			}
		}
	}

	public void keyPress(char key) {
		landing = false;
		if(controlScheme == 0) {   // WASD
			switch(key) {
			case 'w':
				Resources.loopSound("engine");
				thrust = 1;
				break;
			case 'a':
				turn = -((float)handling / HANDLING_SCALE);
				break;
			case 's':
				Resources.loopSound("engine");
				thrust = -1;
				break;
			case 'd':
				turn = ((float)handling / HANDLING_SCALE);
				break;
			case 'x':
				fireProjectile();
				break;
			case 'z':
				fireProjectile();
				break;
			case '\t':
				landing = true;
				break;
			}
		}
		// TODO: map these keys using a config object rather than as switch statements
		if(controlScheme == 1) {   // IJKL
			switch(key) {
			case 'i':
				Resources.stopSound("engine");
				thrust = 1;
				break;
			case 'j':
				turn = -((float)handling / HANDLING_SCALE);
				break;
			case 'k':
				Resources.stopSound("engine");
				thrust = -1;
				break;
			case 'l':
				turn = ((float)handling / HANDLING_SCALE);
				break;
			case 'm':
				fireProjectile();
				break;
			case ',':
				fireProjectile();
				break;
			case '\\':
				landing = true;
				break;
			}
		}
	}

	public void keyReleased(char key) {
		if((key == 'w' || key == 's') && controlScheme == 0) {
			Resources.stopSound("engine");
			thrust = 0;
		}
		if((key == 'a' || key == 'd') && controlScheme == 0) {
			turn = 0;
		}

		if((key == 'i' || key == 'k') && controlScheme == 1) {
			Resources.stopSound("engine");
			thrust = 0;
		}
		if((key == 'j' || key == 'l') && controlScheme == 1) {
			turn = 0;
		}
	}

	private void turnBy(float turnBy) {
		heading.rotate(turnBy);
	}

	private void fireProjectile() {
		if(ammo > 0) {
			Resources.playSound("laser");
			addObject(new Projectile(this, position.copy(), velocity.copy(), heading.copy(), getColor()));
			ammo--;
		}
	}

	@Override
	public void onDestroy(SpaceObject s) {
		getWorld().setDead();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean isLanding() {
		return landing;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getAmmo() {
		return ammo;
	}

	@Override
	public float getMass() {
		return mass;
	}

	@Override
	public float getRadius() {
		return radius;
	}

	public PVector getHeading() {
		return heading;
	}

	@Override
	public PVector applyInfluenceVector(List<SpaceObject> objects) {
		this.influence.set(super.applyInfluenceVector(objects));
		return this.influence;
	}

	@Override
	public boolean collidesWith(SpaceObject s) {
		// TODO: generalize, perhaps by adding SpaceObject::getParent(..) and handling this case in SpaceObject
		return !(s instanceof Projectile && ((Projectile)s).getParent() == this) && super.collidesWith(s);
	}
}  