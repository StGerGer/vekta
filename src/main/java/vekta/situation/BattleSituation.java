package vekta.situation;

import processing.sound.SoundFile;
import vekta.Player;
import vekta.RenderLevel;
import vekta.Resources;
import vekta.object.ship.FighterShip;
import vekta.sound.SoundGroup;
import vekta.spawner.WorldGenerator;

import static vekta.Vekta.getWorld;
import static vekta.Vekta.v;

public class BattleSituation implements Situation {
	private static final SoundGroup BATTLE_MUSIC = new SoundGroup("battle");
	private static final float BATTLE_RADIUS = WorldGenerator.getRadius(RenderLevel.SHIP);

	@Override
	public boolean isHappening(Player player) {
		for(FighterShip ship : getWorld().findObjects(FighterShip.class)) {
			if(ship.getTarget() != null && player.getShip().relativePosition(ship).magSq() <= BATTLE_RADIUS * BATTLE_RADIUS) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void start(Player player) {
		SoundFile sound = BATTLE_MUSIC.random();
		Resources.setMusic(sound, true);

		sound.jump(v.random(sound.duration())); // Start at random point in music
	}

	@Override
	public void during(Player player) {
	}

	@Override
	public void end(Player player) {
		Resources.stopMusic();
	}
}