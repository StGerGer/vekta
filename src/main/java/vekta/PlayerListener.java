package vekta;

import vekta.menu.Menu;
import vekta.mission.Mission;
import vekta.module.Module;
import vekta.object.ship.ModularShip;
import vekta.object.ship.Ship;
import vekta.overlay.singleplayer.Notification;
import vekta.terrain.LandingSite;

/**
 * Player-related event listener
 */
public interface PlayerListener {
	default void onKeyPress(char key) {
	}

	default void onKeyRelease(char key) {
	}

	default void onMenu(Menu menu) {
	}

	default void onEvent(PlayerEvent event) {
	}

	default void onChangeShip(ModularShip ship) {
	}

	default void onNotification(Notification notification) {
	}

	default void onLand(LandingSite site) {
	}

	default void onDock(Ship ship) {
	}

	default void onMissionStatus(Mission mission) {
	}

	default void onInstallModule(Module module) {
	}

	default void onUninstallModule(Module module) {
	}
}
