package vekta.overlay.singleplayer;

import processing.core.PVector;
import vekta.mission.Mission;
import vekta.mission.objective.Objective;
import vekta.module.ModuleType;
import vekta.object.SpaceObject;
import vekta.object.Targeter;
import vekta.object.planet.TerrestrialPlanet;
import vekta.object.ship.ModularShip;
import vekta.overlay.Overlay;
import vekta.player.Player;

import static processing.core.PApplet.*;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static vekta.Vekta.*;

public class NavigationOverlay implements Overlay {
	private final Player player;

	private Targeter targeter;
	private String distString;

	public NavigationOverlay(Player player) {
		this.player = player;

		updateUIInformation();
	}

	private void updateUIInformation() {
		targeter = ((Targeter)player.getShip().getModule(ModuleType.NAVIGATION));
		if(targeter != null && targeter.getTarget() != null) {
			SpaceObject target = targeter.getTarget();
			distString = distanceString(player.getShip().relativePosition(target).mag());
		}
		else {
			distString = "--";
		}
	}

	@Override
	public void render() {
		if(v.frameCount % 10 == 0) {
			updateUIInformation();
		}

		ModularShip ship = player.getShip();

		float dialHeight = v.height - 64;

		// Draw telemetry bar
		v.fill(0);
		v.stroke(UI_COLOR);
		v.strokeWeight(1);
		v.rectMode(CORNERS);
		v.rect(-1, v.height - 130, v.width + 1, v.height + 1);
		v.rectMode(CENTER);

		// Ship heading/velocity indicators
		v.fill(UI_COLOR);
		PVector heading = ship.getHeading();
		PVector velocity = ship.getVelocity();
		drawDial("Heading", ship.getHeading(), v.width - 370, dialHeight, UI_COLOR);
		if(velocity.magSq() >= 1) {
			drawDial("Velocity", velocity, v.width - 500, dialHeight, UI_COLOR);
		}

		// New heading dial
		drawHeadingIndicator(ship.getHeading(), targeter.getTarget());

		// Targeter information
		if(targeter != null) {
			SpaceObject target = targeter.getTarget();
			String targetString;
			if(target == null) {
				v.fill(100, 100, 100);
				v.stroke(UI_COLOR);
				// TODO: change depending on key binding
				targetString = "PLANET [1], ASTEROID [2], SHIP [3]" + (player.getCurrentMission() != null ? ", OBJECTIVE [4]" : "");
			}
			else {
				String massString = massString(target.getMass());
				if(target instanceof TerrestrialPlanet) {
					TerrestrialPlanet closestPlanet = (TerrestrialPlanet)target;
					targetString = target.getName() + " - " + distString + " \nHabitable: " + (closestPlanet.isHabitable() ? "YES" : "NO") + "\nMass: " + massString;
					SpaceObject orbit = closestPlanet.getOrbitObject();
					if(orbit != null) {
						targetString += "\nOrbiting: " + orbit.getName();
					}
				}
				else {
					targetString = target.getName() + " - " + distString + " \n\nMass: " + massString;
				}
				// Closest object arrow
				drawDial("Direction", target.getPosition().sub(ship.getPosition()), 450, dialHeight, target.getColor());
				v.fill(target.getColor());
				v.stroke(target.getColor());
			}
			v.text("Target: " + targetString, 50, v.height - 100);

			// Draw mission objective
			Mission mission = player.getCurrentMission();
			if(mission != null) {
				Objective current = mission.getCurrentObjective();
				SpaceObject objTarget = current.getSpaceObject();
				// Draw objective direction dial
				if(objTarget != null && objTarget != targeter.getTarget()) {
					drawDial("Objective", objTarget.getPosition().sub(ship.getPosition()), v.width / 2F, dialHeight, objTarget.getColor());
				}
			}
		}
	}

	private void drawDial(String name, PVector info, float locX, float locY, int c) {
		v.strokeWeight(2);
		float radius = 50;
		v.noFill();
		v.stroke(c);
		v.ellipse(locX, locY, radius, radius);
		v.fill(100, 100, 100);
		v.textAlign(CENTER);
		v.textSize(14);
		v.text(name, locX, locY + radius / 2);
		v.textAlign(LEFT);
		v.textSize(16);
		v.stroke(c);
		drawArrow(info, (int)(radius * .8), locX, locY);
		v.strokeWeight(1);
	}

	private void drawArrow(PVector heading, float length, float locX, float locY) {
		heading.setMag(length);
		v.line(locX, locY, locX + heading.x, locY + heading.y);
		float angle = heading.heading();
		float x = cos(angle);
		float y = sin(angle);
		PVector endpoint = new PVector(x, y);
		PVector arms = endpoint.copy();
		endpoint.mult(length);
		arms.mult(length * .9F); // scale the arms to a certain length
		// render the arms
		v.line(locX + endpoint.x, locY + endpoint.y, locX + cos(angle - .3F) * (length * .8F), locY + sin(angle - .3F) * (length * .8F));
		v.line(locX + endpoint.x, locY + endpoint.y, locX + cos(angle + .3F) * (length * .8F), locY + sin(angle + .3F) * (length * .8F));
	}

	private void drawHeadingIndicator(PVector heading, SpaceObject target) {
		int resolution = 36;		// Number of bars to draw
		int top = 40;				// Distance (px) from top of screen
		int height = 20;			// Height of the largest indicator bars
		int offset = 10;			// Offset in height of the smaller bars
		float width = v.width / 2;	// Width of the indicator as a whole

		int left = (int) ((v.width - width) / 2);

		// Draw arrow
		v.stroke(0, 255, 0);
		v.line(v.width / 2 + 10, 75, v.width / 2, 65);
		v.line(v.width / 2 - 10, 75, v.width / 2, 65);

		// Write current heading
		int headingNumber = (int)Math.toDegrees(heading.heading());
		if(Math.signum(headingNumber) == -1) headingNumber += 360;
		v.color(0, 255, 0);
		v.textAlign(CENTER);
		v.text(headingNumber, v.width / 2, 90);

		// Draw lines
		for(int i = 0; i < resolution * 3; i++) {
			int horizontalLocation = (int)(left + ((i - resolution) * (width / resolution)) - ((float)((headingNumber - 180.0) / 360.0) * (width)));
			float sinModifier = sin((float) (Math.PI * ((horizontalLocation - left - (headingNumber - 180.0) / 360.0) / width)));

			v.stroke(0, 255, 0, 255 * sinModifier);
			v.line(horizontalLocation, top, horizontalLocation, top + height - (i % 4 == 0 ? 0 : offset));

			if((i % 4) == 0) {
				v.fill(0, 255, 0, 255 * sinModifier);
				v.textSize(14);
				v.text((i % resolution) * 360 / resolution, horizontalLocation, top + height + 16);
			}
		}
		v.textAlign(LEFT);
	}
}
